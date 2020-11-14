/**
 * 
 */
const PSIQ    = "psiq";
const PSIQSEC = "psiqsec";
const NPSIQ   = "npsiq";
const ESTADIAJE = "estadiaje";

const RQ_OBTENER_ACTUACION_POR_PACIENTE = 13;
const RQ_OBTENER_ACTUACION_POR_REGISTRO = 14;
const RQ_GUARDAR_REGISTRO               = 15;
const RQ_ACTUALIZAR_REGISTRO            = 16;
const RQ_METABUSCADOR_DIAGNOSTICOS      = 17;
const RQ_GUARDAR_DATOS_CLINICOS         = 18;
const RQ_OBTENER_NOMBRE_FACULTATIVO     = 19;
const RQ_OBTENER_PACIENTE_DEL_HOSPITAL  = 21;
const RQ_OBTENER_DATOS_AGENDA           = 22;
const RQ_MARCAR_CITA_ATENDIDA           = 23;
const RQ_ENVIAR_INFORME_HL7             = 24;
//const RQ_EXPORTAR_A_CSV                 = 26;

const RQ_CERRAR_SESION      = 27;
const RQ_DEBUG_MUP_LOCATION = 50;

var diagPrim   = [];
var diagSec    = [];
var diagNoPsiq = [];
var estad      = [];

var actuacionAbierta = {};
var actuacionEnVista = {};
var ElementosActuacionAbierta = [];

var totalRegistros = 0;
var registroActual = 0;
var pacienteActual = 0;
var actuacionActual = 0;
var registroExistente = false;
var editandoRegistro = false;

var modoConsulta = false;
var numeroCita = "";
var numeroICU = "";
var numeroCitaNuevoRegistro = "";
var numeroICUNuevoRegistro = "";

var gUserDni = "";
var gUserCias = "";
var gUserId = "";

var caracteres = "0123456789ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz,;.:-_çÇ!¡?¿\/$%&=|€<>*+ ";

var ignoreBeforeUnload = false;



$(document).ready(function () {
	
	/** Comprobar si existe el parametro idFacultativo para solo permitir acceder a usuariso que hayan pasado el login **/
	{
		let params = new Params([ "idFacultativo", "cias", "dni" ]);
		if (params.results[0] === "" || params.results[0] === undefined) {
			location.href = "login.html";
		}
		
		gUserId   = params.results[0];
		gUserCias = params.results[1];
		gUserDni  = params.results[2];
	}
	/** Fin de la comprobacion para permitir usuarios que pasen por el login **/

	setLoader();
	loadInputFields();
	// Al terminar de construir el HTML, continua llamando a htmlLoaded() desde continueLoad()

	$(window).bind("beforeunload", function() {
		if ( !ignoreBeforeUnload ) {
			$(":focus").blur();
	
			if (!modoConsulta) {
				console.log("GUARDANDO CHANGINGACTUACIONENVISTA (BeforeUnload)...");
				let changingActuacionEnVista = new StoredActuacion();
				
				if (!jQuery.isEmptyObject(actuacionEnVista)) {
					if (!changingActuacionEnVista.isEqual(actuacionEnVista)) {
						return "HAY CAMBIOS SIN GUARDAR, ¿DESEA SALIR?";
					}
				}
				else if (changingActuacionEnVista.containsSomething()){
					return "HAY CAMBIOS SIN GUARDAR, ¿DESEA SALIR?";				
				}
			}
		}
		else {
			ignoreBeforeUnload = false;
		}
	});
		
});


function htmlLoaded() {
	let params = new Params([ "idPaciente", "modoConsulta", "numeroCita", "numeroICU", "numeroHC" ]);
	
	pacienteActual = params.results[0];
	modoConsulta = params.results[1] === "true" ? true : false;
	numeroCitaNuevoRegistro = parseInt(params.results[2]);
	numeroICUNuevoRegistro = parseInt(params.results[3]);
	numeroCita = parseInt(params.results[2]);
	numeroICU = parseInt(params.results[3]);
	numeroHC = parseInt(params.results[4]);
	
//	setMotivoAltaOptions(numeroCita == 0);
	
	if (pacienteActual !== "" && pacienteActual !== "0" && pacienteActual !== "-1") {
		obtenerActuacionPorIdPaciente(pacienteActual);
	}
	else if (!modoConsulta) {
		if (!isNaN(numeroCita) || !isNaN(numeroHC)) {
			obtenerPacienteDelHospital(numeroHC, numeroCita);
		}
		else {
			nuevaActuacion();
		}
		
	}
}


function obtenerPacienteDelHospital(numHC, numCita) {
	$.ajax({
		data: {
			"peticion": RQ_OBTENER_PACIENTE_DEL_HOSPITAL,
			"numeroHC": numHC,
			"userId"  : gUserId
		},
		type: "get",
		url: url,
		cache: false,
		success: function (response) {
			if (response.idPaciente != -1) {
				pacienteActual = response.idPaciente;
				if ( !isNaN(numCita) && numCita !== undefined ) {
					obtenerActuacionPorIdPacienteNuevoRegistro(response.idPaciente, numCita);
				}
				else {
					obtenerActuacionPorIdPaciente(response.idPaciente);
				}
				return;
			}
			else {
				nuevoRegistro();
				displayPacienteEnVista(response);
			}

			if (!isNaN(numCita)) {
				let numeroCIPA = response.numeroCIPA;
				$.ajax({
					data: {
						"peticion": RQ_OBTENER_DATOS_AGENDA,
						"numeroCita": numCita,
						"userId" : gUserId
					},
					url: url,
					cache: false,
					type : "get",
					success: function (response) {
						console.log(response);

						// Mostrar los datos de la agenda recibidos en los selectores de la vista correspondientes //
						$("#id-agenda").val(response.codigoAgenda);
						$("#lugar-ate").val(response.descripcionCentro);
						$("#tipo-prest").val(response.descripcionPrestacion);
						
						unsetLoader();

						// Intentar encontrar el paciente en la BD local por CIPA para mostrar si tiene DATOS CLINICOS //
//						$.ajax({
//							data: {
//								"peticion": OBTENER_PACIENTE_POR_CIPA,
//								"numeroCIPA": numeroCIPA
//							},
//							url: url,
//							cache: false,
//							type: "get",
//							success: function (response) {
//								if (response.IdPaciente) {
//									$.ajax({
//										data: {
//											"peticion": EXTRAER_DATOS_CLINICOS,
//											"idPaciente": response.IdPaciente
//										},
//										url: url,
//										cache: false,
//										type: "get",
//										success: function (response) {
//											displayDatosClinicosEnVista(response);
//										}
//									});
//								}
//							}
//						});
					},
					error: function (response) {
						unsetLoader();
						console.log(response);
					}
				});
			}
			else {
				unsetLoader();
			}
		},
		error: function (response) {
			unsetLoader();
			console.log(response);
		}
	});	
}


function clickNuevo() {
	var params = new Params([ "idFacultativo", "equipoCalle", "modoConsulta",
								"internoSoloConsulta", "cias",  "dni" ]);

	if (params.results[1] === "true") {
		let query = CryptoJS.AES.encrypt("idFacultativo=" + params.results[0] + "&equipoCalle=true" +
											"&modoConsulta=false&cias=" + params.results[4] +
											"&dni=" + params.results[5],
											"12349876aeiou");
		location.href = "search.html?" + query;
	}
	else if (params.results[1] === "false" && params.results[3] === "false") {
		let query = CryptoJS.AES.encrypt("idFacultativo=" + params.results[0] +
											"&equipoCalle=false&cias=" + params.results[4] +
											"&dni=" + params.results[5],
											"12349876aeiou");
		location.href = "citas.html?" + query;
	}
	else {
		let query = CryptoJS.AES.encrypt("idFacultativo=" + params.results[0] + "&equipoCalle=false" +
				"&modoConsulta=true&cias=" + params.results[4] + "&dni=" + params.results[5], "12349876aeiou");
		location.href = "search.html?" + query;
	}
}


function nuevoRegistro() {
	// Protege de la creacion de nuevos registros si ya existe uno abierto //
	// No se puede usar foreach porque 'return' no rompe el bucle foreach //
	for (let i = 0, len = $('#fec-mod').children('option').length; i < len; i++) {
		if ($('#fec-mod option').eq(i).text().indexOf("Nuevo") !== -1) {
			mostrarActuacionAlmacenada(actuacionAbierta);
			registroActual = totalRegistros;
			$("#num-reg").text(registroActual + " de " + totalRegistros + " registros");
			$('fec-mod:selected').val(totalRegistros);
			updateNavigationButtons();
			return;
		}
	}
//	$("#fec-mod option").each(function() {
//		if ($(this).text().indexOf("Nuevo") !== -1) {
//			return;
//		}
//	});
	
	if (registroActual < totalRegistros) {
		mostrarActuacionAlmacenada(actuacionAbierta);
	}
	
	$("#evolucion-comentarios").val("");
	$("#id-agenda").val("");
	$("#lugar-ate").val("");
	$("#tipo-prest").val("");
	if ($("#cod-empleado").val() != gUserId) {
		$.ajaxSetup({ "cache": false });
		let obtenerNombreProfesional = $.get(url, {
			"peticion": RQ_OBTENER_NOMBRE_FACULTATIVO,
			"codFacultativo": gUserId
		});
		
		$("#cod-empleado").val(gUserId);
		
		$.when(obtenerNombreProfesional).done(function(response) {
			$("#profesional").val(response.nombre + " " + response.apellido1 + " " + response.apellido2);
			gUserDni = response.dni;
		});
	}
	let params = new Params(["equipoCalle"]);
	if (params.results[0] === "true") {
		$("#equipo-calle").prop('checked', true);
	}
	
	numeroCita = numeroCitaNuevoRegistro;
	numeroICU = numeroICUNuevoRegistro;
	
	totalRegistros++;
	registroActual = totalRegistros;
	
	$("#num-reg").text(registroActual + " de " + totalRegistros + " registros");
	$("#fec-mod").append("<option value='" + totalRegistros + "' selected>Nuevo: " +
			new DateFormatter(new Date()).toString() + "</option>");
	
	/** Vacía el contenido de los elementos de fecha de fin si estan marcados solos **/
	$("#tratamientos select").each(function () {
		let parentId = $(this).parent()[0].id;
		if ($("#c-fec-fin-" + parentId + ".calendar").val() !== ""
				&& $(this).siblings("input:checkbox")[0].checked === false) {
			$("#c-fec-fin-" + parentId + ".calendar").val("");
		}
	});

	
	editandoRegistro = true;
	registroExistente = false;

	updateNavigationButtons();
	
	$("#informe").attr("disabled", true);
}


function nuevaActuacion() {
	unsetLoader();
	registroExistente = false;
	limpiarVista();
	vaciarArrays();
	
	editandoRegistro = true;
	
	$("main .clean").css({ "display" : "none" });
	$("main :checkbox").prop("checked", false);
	$("main form:not(#paciente) select").val(0);
	var currentDate = new Date();
	var dia = currentDate.getDate();
	if (dia < 10) {
		dia = "0" + dia;
	}

	var mes = currentDate.getMonth() + 1;
	if (mes < 10) {
		mes = "0" + mes;
	}

	$("#fec-mod").append("<option value='" + totalRegistros + "' selected>Nuevo: " + dia + "/"
							+ mes + "/" + currentDate.getFullYear()	+ "</option>");
	$("#num-reg").text("");
	totalRegistros++;
	registroActual = totalRegistros;
	$("#btn-nuevo").attr("disabled", false);

	let params = new Params([ "idFacultativo", "equipoCalle" ]);

	$.ajaxSetup({ "cache": false });
	let obtenerNombreProfesional = $.get(url, {
		"peticion": RQ_OBTENER_NOMBRE_FACULTATIVO,
		"codFacultativo": params.results[0]
	});
	
	$("#cod-empleado").val(params.results[0]);
	
	$.when(obtenerNombreProfesional).done(function(response) {
		$("#profesional").val(response.nombre + " " + response.apellido1 + " " + response.apellido2);
		gUserDni = response.dni;
	});
	
	//$("#profesional").val(params.results[0]);
	if (params.results[1] === "true") {
		$("#equipo-calle").prop('checked', true);
		$("#id-agenda").val("");
		$("#lugar-ate").val("");
		$("#tipo-prest").val("");
	}
	 
	updateNavigationButtons();
	
	editandoRegistro = true;
}


function obtenerActuacionPorIdPaciente(idPaciente) {
	$.ajaxSetup({ "cache": false });
	let obtenerActuacion = $.get(url, {
		"peticion": RQ_OBTENER_ACTUACION_POR_PACIENTE,
		"idPaciente": idPaciente,
		"idProfesional": gUserId
	});
	
	setLoader();
	$.when(obtenerActuacion).done(function (response) {
		if (response.totalRegistros > 0) {
			totalRegistros = response.totalRegistros;
			registroActual = totalRegistros;
			/* COMENTADO para la funcionalidad de que no se autocree un nuevo registro
			if (!modoConsulta) {
				totalRegistros++;
				registroActual = totalRegistros;
			}*/
			
			$("#fec-mod").empty();

			let fechasArray = response.fechasActuaciones;
			fechasArray.reverse();
			let select = $("#fec-mod");
			for (let i = 0; i < fechasArray.length; i++) {
				let option = new Option(fechasArray[i], i + 1, false, i === fechasArray.length - 1);
				select.append(option);
			}
		}
		actuacionActual = response.Actuacion.idActuacion;
		pacienteActual = response.Paciente.idPaciente;
		displayActuacion(response);

		/* COMENTADO para la funcionalidad de que no se autocree un nuevo registro
		if (!modoConsulta) {
			$("#evolucion-comentarios").val("");
		}*/
		
//** COMENTARIO: 29-10-2020: creo que no hace falta al no tener que crearse
//** un nuevo registro de forma automatica		
//		let params = new Params([ "equipoCalle", "modoConsulta", "idFacultativo" ]);
//		if (params.results[0] === "true" && params.results[1] === "false") {
//			$("#equipo-calle").prop('checked', true);
//			$("#id-agenda").val("");
//			$("#lugar-ate").val("");
//			$("#tipo-prest").val("");
//			
//			// NUEVO
//			$.ajaxSetup({ "cache": false });
//			let obtenerNombreProfesional = $.get(url, {
//				"peticion": RQ_OBTENER_NOMBRE_FACULTATIVO,
//				"codFacultativo": params.results[2]
//			});
//			
//			$("#cod-empleado").val(params.results[2]);
//					
//			$.when(obtenerNombreProfesional).done(function(response) {
//				$("#profesional").val(response.nombre + " " + response.apellido1 + " " + response.apellido2);
//			});
//			//
//		}
		
		unsetLoader();
		updateNavigationButtons();
		
	}).fail(function (response) {
		unsetLoader();
		console.log("ERROR: " + response);
		if (response.state === "SESSION CLOSED") {
			location.href = "login.html";
		}
	});	
}

function obtenerActuacionPorIdPacienteNuevoRegistro(idPaciente, numCita) {
	$.ajaxSetup({ "cache": false });
	let obtenerActuacion = $.get(url, {
		"peticion": RQ_OBTENER_ACTUACION_POR_PACIENTE,
		"idPaciente": idPaciente,
		"idProfesional": gUserId
	});
	
	setLoader();
	$.when(obtenerActuacion).done(function (response) {
		if (response.totalRegistros > 0) {
			totalRegistros = response.totalRegistros;
			registroActual = totalRegistros;
			/* COMENTADO para la funcionalidad de que no se autocree un nuevo registro
			if (!modoConsulta) {
				totalRegistros++;
				registroActual = totalRegistros;
			}*/
			
			$("#fec-mod").empty();

			let fechasArray = response.fechasActuaciones;
			fechasArray.reverse();
			let select = $("#fec-mod");
			for (let i = 0; i < fechasArray.length; i++) {
				let option = new Option(fechasArray[i], i + 1, false, i === fechasArray.length - 1);
				select.append(option);
			}
		}
		actuacionActual = response.Actuacion.idActuacion;
		pacienteActual = response.Paciente.idPaciente;
		displayActuacion(response);

		/* COMENTADO para la funcionalidad de que no se autocree un nuevo registro
		if (!modoConsulta) {
			$("#evolucion-comentarios").val("");
		}*/
		
		let params = new Params([ "idFacultativo" ]);
		// NUEVO
		$.ajaxSetup({ "cache": false });
		let obtenerNombreProfesional = $.get(url, {
			"peticion": RQ_OBTENER_NOMBRE_FACULTATIVO,
			"codFacultativo": params.results[0]
		});
		
		$("#cod-empleado").val(params.results[0]);
				
		$.when(obtenerNombreProfesional).done(function(response) {
			$("#profesional").val(response.nombre + " " + response.apellido1 + " " + response.apellido2);
		}).fail(function(response) {
			console.log(response);
		});
		//
		
		nuevoRegistro();
		
		if (!isNaN(numCita)) {
			let numeroCIPA = response.NumCIPA;
			$.ajax({
				data: {
					"peticion": RQ_OBTENER_DATOS_AGENDA,
					"numeroCita": numCita
				},
				url: url,
				cache: false,
				type : "get",
				success: function (response) {
					// Mostrar los datos de la agenda recibidos en los selectores de la vista correspondientes //
					$("#id-agenda").val(response.codigoAgenda);
					$("#lugar-ate").val(response.descripcionCentro);
					$("#tipo-prest").val(response.descripcionPrestacion);
				},
				error: function (response) {
					unsetLoader();
					console.log(response);
				}
			});
		}
		
		unsetLoader();
		updateNavigationButtons();
		
	}).fail(function (response) {
		unsetLoader();
		console.log("ERROR: " + response);
		if (response.state === "SESSION CLOSED") {
			location.href = "login.html";
		}
	});	
}


function obtenerActuacionPorNumRegistro(idPaciente) {
	$("input[type=checkbox] ~ select").attr("disabled", true);
	$("input[type=checkbox] ~ div .calendar").attr("disabled", true);

	if (registroActual == totalRegistros && !modoConsulta) {
		$("#num-reg").text(registroActual + " de " + totalRegistros + " registros");
		mostrarActuacionAlmacenada(actuacionAbierta);
	}
	else {
		$.ajaxSetup({ "cache": false });
		let obtenerActuacion = $.get(url, {
			"peticion": RQ_OBTENER_ACTUACION_POR_REGISTRO,
			"idPaciente": pacienteActual,
			"registro": registroActual,
			"idProfesional": gUserId
		});
		
		setLoader();
		$.when(obtenerActuacion).done(function (response) {
			unsetLoader();		
			displayActuacion(response);
		}).fail(function (response) {
			unsetLoader();		
			console.log("ERROR: " + response);
			if (response.state === "SESSION CLOSED") {
				location.href = "login.html";
			}
		});
	}
}


function registroAnterior() {
	if (!modoConsulta) {
		var confirm = true;

		if (registroActual == totalRegistros) {
			actuacionAbierta = new StoredActuacion();
		} else {
			var changingActuacionEnVista = new StoredActuacion();
			if (!changingActuacionEnVista.isEqual(actuacionEnVista)) {
				confirm = window.confirm("Hay cambios no guardados. ¿Quieres salir sin guardar los cambios?");
			}
			if (!confirm) {
				return;
			}
			$("#informe").attr("disabled", false);
		}
	}

	if (registroActual > 1) {
		--registroActual;
		$("#fec-mod").val(registroActual);
		obtenerActuacionPorNumRegistro(pacienteActual);
		actuacionEnVista = new StoredActuacion();
	}

	updateNavigationButtons();
}


function registroSiguiente() {
	if (!modoConsulta) {
		var confirm = true;

		var changingActuacionEnVista = new StoredActuacion();
		if (!changingActuacionEnVista.isEqual(actuacionEnVista)) {
			confirm = window.confirm("Hay cambios no guardados. ¿Quieres salir sin guardar los cambios?");
		}
		if (!confirm) {
			return;
		}
	}

	if (registroActual < totalRegistros) {
		++registroActual;
		$("#fec-mod").val(registroActual);
		$("#informe").attr("disabled", false);
		obtenerActuacionPorNumRegistro(pacienteActual);
	}

	updateNavigationButtons();
}


function cambioFechaActuacion() {
	var selectedIndex = $("#fec-mod").val();

	var confirm = true;

	if (registroActual != totalRegistros) {
		var changingActuacionEnVista = new StoredActuacion();
		if (!changingActuacionEnVista.isEqual(actuacionEnVista)) {
			confirm = window.confirm("Hay cambios no guardados. ¿Quieres salir sin guardar los cambios?");
		}

		if (!confirm) {
			$("#fec-mod").val(registroActual);
			return;
		} else {
			registroActual = selectedIndex;
			obtenerActuacionPorNumRegistro(pacienteActual);
			$("#fec-mod").val(selectedIndex);
		}
	} else {
		$("#fec-mod").val(registroActual);
		actuacionAbierta = new StoredActuacion();
		registroActual = selectedIndex;
		obtenerActuacionPorNumRegistro(pacienteActual);
		$("#fec-mod").val(selectedIndex);
	}

	updateNavigationButtons();
}


function updateNavigationButtons() {
	let fecha = $("#fec-mod option:selected").text(); 
	if (fecha.indexOf("Nuevo") !== -1) {
		$("#nuevo-registro").attr("disabled", true);
	}
	else {
		$("#nuevo-registro").attr("disabled", false);		
	}
	
	// Si el registro aún no se ha cerrado, el botón de Generar Informe está desactivado //
	if (editandoRegistro === true && registroActual == totalRegistros) {
		$("#informe").attr("disabled", true);
	}
	else {
		$("#informe").attr("disabled", false);		
	}

	if (registroActual == 1) {
		$("#btn-reg-ant").attr("disabled", true);
		/* COMENTADO para la funcionalidad de que no se autocree un nuevo registro
		$("#informe").attr("disabled", true);*/
	} else {
		$("#btn-reg-ant").attr("disabled", false);
	}
	
	if (registroActual == totalRegistros) {
		$("#btn-reg-post").attr("disabled", true);
		/* COMENTADO para la funcionalidad de que no se autocree un nuevo registro
		if (modoConsulta === false) {
			$("#informe").attr("disabled", true);
		}
		else {
			$("#informe").attr("disabled", false);			
		}*/
	} else {
		$("#btn-reg-post").attr("disabled", false);
		/* COMENTADO para la funcionalidad de que no se autocree un nuevo registro
		if (registroActual == totalRegistros - 1) {
			$("#informe").attr("disabled", false);
		} else {
			$("#informe").attr("disabled", true);
		}*/
	}
}


function guardarRegistro() {
	editandoRegistro = false;
	$("#informe").attr("disabled", false);

	
	let fecha = $("#fec-mod option:selected").text();
	
	let peticion = RQ_ACTUALIZAR_REGISTRO;
	
	if (fecha.indexOf("Nuevo") > -1) {
		fecha = fecha.substr(7);
		peticion = RQ_GUARDAR_REGISTRO;
	}
	
	let data = vistaToJson(fecha);
	if (data === undefined) {
		return;
	}
	
	let guardarActuacion = $.ajax({
		url: url,
		cache: false,
		type: "post",
		data: {
			"peticion": peticion,
			"actuacion": JSON.stringify(data)
		}
	});
	
	setLoader();
	$.when(guardarActuacion).done(function (responseActuacion) {
		console.log(responseActuacion);
		var actualizarDatosClinicos = false;
		if (actuacionActual == responseActuacion.idActuacion) {
			actualizarDatosClinicos = true;
		}
		actuacionActual = responseActuacion.idActuacion;
		pacienteActual = responseActuacion.idPaciente;
		
		let datosClinicos = {
				antecedentes: $("#antecedentes").val(),
				enfermedadActual: $("#hist-actual").val(),
				evolucionComentarios: $("#evolucion-comentarios").val(),
				idPaciente: pacienteActual,
				idActuacion: actuacionActual
		};
		let guardarDatosClinicos = $.ajax({
			url: url,
			cache: false,
			type: "post",
			data: {
				"peticion": RQ_GUARDAR_DATOS_CLINICOS,
				"datos-clinicos": JSON.stringify(datosClinicos),
				"actualizar": actualizarDatosClinicos
			}
		});
		
		$.when(guardarDatosClinicos).done(function (responseDatosClinicos) {
			console.log(responseDatosClinicos);
			
			let params = new Params([ "numeroCita" ]);
			
			if (params.results[0] !== "" && !isNaN(params.results[0])) {
				console.log("TRATANDO DE MARCAR " + params.results[0] + " LA CITA COMO ATENDIDA");
				let marcarCita = $.ajax({
					data: {
						"peticion" : RQ_MARCAR_CITA_ATENDIDA,
						"numeroCita" : params.results[0]
					},
					url: url,
					cache: false,
					type: "get"
				});
				
				$.when(marcarCita).done(function (responseCita) {
					console.log("MARCAR CITA ATENDIDA success response:");
					console.log(responseCita);
					
					/** ENVIAR INFORME HL7 AL HIS SI EXISTE NUMERO DE CITA PARA LA ACTUACION
					/** -- imprimirInforme(sendHL7=boolean, downloadPDF=boolean)
					 **/
					imprimirInforme(true, false);
					
					obtenerActuacionPorIdPaciente(pacienteActual);
					
					showMessageBox(responseActuacion.message + " <br> " + responseDatosClinicos.message);

					unsetLoader();
				}).fail(function (response) {
					unsetLoader();		
					console.log(response);
				});
			}
			else {
				imprimirInforme(true, false);
				obtenerActuacionPorIdPaciente(pacienteActual);
				
				showMessageBox(responseActuacion.message + " <br> " + responseDatosClinicos.message);
				
				unsetLoader();
			}
			
		}).fail(function (responseDatosClinicos) {
			unsetLoader();
			console.log(responseDatosClinicos);
		});
		
	}).fail(function (responseActuacion) {
		unsetLoader();
		console.log(responseActuacion.responseJSON.exception);
		if (responseActuacion.responseJSON.show) {
			showMessageBox(responseActuacion.responseJSON.message);
		}
	});
}


/**
 ** Funcion MetaBuscadora de codigos CIE10 de Diagnosticos
 **/
function buscarDiagnosticoPorPatron(event, tipoDiag) {
	$(".desplegable input ~ ul").css({ "display" : "none" });

	var combo = event.target;
	var text = combo.value.trim();
	var pos = combo.id.split("-")[2] - 1;

	text = text.replace("†", " ");

	if (event.type === "keyup" && (caracteres.indexOf(event.key) !== -1 || event.key === "Backspace")) {
		switch (tipoDiag) {
		case PSIQ:
			diagPrim[pos] = "";
			break;
		case PSIQSEC:
			diagSec[pos] = "";
			break;
		case NPSIQ:
			diagNoPsiq[pos] = "";
			break;
		default:
			break
		}
	}

	if (text !== "") {
		$("#" + combo.id + " ~ ul").css({ "display" : "block" });
		$("#" + combo.id + " + .clean").css({ "display" : "block" });
		$("#" + combo.id + " + .clean").on("click", function () {
			combo.value = "";
			$("#" + combo.id + " ~ ul").empty();
			$("#" + combo.id + " + .clean").css({ "display" : "none" });
		});

		if (text.length >= 3) {
			$.ajax({
				data: {
					"peticion": RQ_METABUSCADOR_DIAGNOSTICOS,
					"patron": text,
					"psiquiatricos": tipoDiag
				},
				type: "post",
				url: url,
				cache: false,
				success: function (response) {
					$("#" + combo.id + " ~ ul").empty();

					for (let cont = 0; cont < response.Diagnosticos.length; cont++) {
						let i = response.Diagnosticos[cont];
						var opt = $("<li></li>");
						opt.on("click", function () {
							combo.value = i.descripcion;

							var pos = combo.id.substr(combo.id.length - 1);

							switch (tipoDiag) {
							case PSIQ:
								diagPrim[pos - 1] = i.cie;
								break;
							case PSIQSEC:
								diagSec[pos - 1] = i.cie;
								break;
							case NPSIQ:
								diagNoPsiq[pos - 1] = i.cie;
								break;
							default:
								break;
							}

							$("#" + combo.id + " ~ ul").css({ "display" : "none" });
						});
						
						opt.text(i.descripcion);
						$("#" + combo.id + " ~ ul").append(opt);
					}
				}
			});
		}
	} else {
		$("#" + combo.id + " ~ ul").empty();
		$("#" + combo.id + " + .clean").css({ "display" : "none" });
	}
}


function displayActuacion(data) {
	$("main form:not(#paciente) input").val("");
	vaciarArrays();
	$("main .clean").css({ "display" : "none" });
	$("main :checkbox").prop("checked", false);
	$("main form:not(#paciente) select").val(0);
	
	displayPacienteEnVista(data.Paciente);
	displayActuacionEnVista(data.Actuacion);
	displayDatosClinicosEnVista(data.DatosClinicos);
	displayDiagnosticosEnVista(data.Diagnosticos);
	displayTratamientosEnVista(data.Tratamientos, data.TratamientosMUP);
	displaySituacionClinicaFuncionalEnVista(data.SituacionClinicaFuncional);
	displayProgramasUnidadesEspecialesProcesosEnVista(data.ProgramasUnidadesEspecialesProcesos);
	
//	let params = new Params([ "equipoCalle" ]);
//	if (params.results[0] === "true") {
//		$("#equipo-calle").prop("checked", true);
//	}
	
	actuacionEnVista = new StoredActuacion();
	
	/* COMENTADO para la funcionalidad de que no se autocree un nuevo registro.
	if (registroActual == totalRegistros && modoConsulta === false) {
		$("#num-reg").text(registroActual + " de " + totalRegistros + " registros");
		$("#fec-mod").append("<option value='" + totalRegistros + "' selected>Nuevo: " +
				new DateFormatter(new Date()).toString() + "</option>");
		
		actuacionAbierta = new StoredActuacion();
		
		mostrarActuacionAlmacenada(actuacionAbierta);
	}*/
	
	if (modoConsulta) {
		desactivarCampos();
	}
}


//< --------------------------------------------------------------- >
//< --------------------------------------------------------------- >
//< METODOS PARA MOSTRAR EN LA VISTA LOS DATOS RECIBIDOS >
//< --------------------------------------------------------------- >
function displayActuacionEnVista(data) {
	let fecha = $("#fec-mod option:selected").text(); 
	if (fecha.indexOf("Nuevo") !== -1) {
		numeroCita = numeroCitaNuevoRegistro;
		numeroICU = numeroICUNuevoRegistro;		
	}
	else {
		numeroCita = data.numeroCita;
		numeroICU = data.numeroICU;
	}
	actuacionActual = data.idActuacion;

	$("#num-reg").text(registroActual + " de " + totalRegistros + " registros");

	$("#id-agenda").val(data.codigoAgenda != null ? data.codigoAgenda.toUpperCase() : "");
	$("#profesional").val(data.idProfesional);
	$("#cod-empleado").val(data.codEmpleado);
	$("#lugar-ate").val(data.lugarAtencion != null ? data.lugarAtencion.toUpperCase() : "");
	$("#tipo-prest").val(data.tipoPrestacion != null ? data.tipoPrestacion.toUpperCase() : "");
	if (data.equipoDeCalle && data.fechaAltaEquipoCalle == "")
		$("#equipo-calle").prop("checked", true);
	else
		$("#equipo-calle").prop("checked", false);

	$("#alta #motivo").empty();
	/**
	 ** Utilizamos el numeroCita almacenado para saber si el registro lo guardó un Facultativo del Hospital (lo hace con citas)
	 ** o el Equipo de Calle (que no tiene citas). Usado para cargar las opciones en el selector de 'Motivo de Alta' correspondientes para cada uno.
	 **/
	setMotivoAltaOptions(numeroCita == 0);
	
	var altaData = data.Alta;
	if (altaData.check) {
		$("#alta .calendar").attr("disabled", false);
		$("#alta #motivo").attr("disabled", false);
		$("#alta .calendar").val(altaData.fechaAlta);

		$("#alta #motivo").val(altaData.motivoAlta);
		$("#alta input[type=checkbox]").prop("checked", true);
	} else {
		$("#alta .calendar").attr("disabled", true);
		$("#alta #motivo").attr("disabled", true);
		$("#alta .calendar").val("");

		$("#alta #motivo").val(0);
		$("#alta input[type=checkbox]").prop("checked", false);
	}

	$("#total").prop("checked", data.incapacidadTotal);
	$("#cur-salud").prop("checked", data.curatelaSalud);
	$("#cur-eco").prop("checked", data.curatelaEconomica);
	$("#cb-cont-cuidados").prop("checked", data.programaContinuidadCuidados);
	$("#cb-prog-joven").prop("checked", data.programaJoven);
	$("#fec-pj-inicio").val(data.fechaInicioProgramaJoven);
	$("#fec-pj-fin").val(data.fechaFinProgramaJoven);
	$("#med-proteccion").prop("checked", data.medidaProteccion);
	$("#residencia").prop("checked", data.residencia);
}


function displayPacienteEnVista(data) {
	$("#nombre").val((data.nombre).toUpperCase());
	$("#apellido1").val((data.apellido1).toUpperCase());
	$("#apellido2").val((data.apellido2).toUpperCase());
	/**
	 ** Carga el listbox con el sexo (1=MASCULINO), (2=FEMENINO), (3=INDETERMINADO), (4=DESCONOCIDO)
	 **/
	$("#sexo").val(data.sexo);
	/** Muestra la fecha de nacimiento
	 **/
	if (data.fechaNac !== "") {
		$("#fec-nac").siblings(".clean").css({ "display" : "block" });
		$("#fec-nac").siblings(".clean").on("click", function () {
			$("#fec-nac").val("");
			$("#fec-nac").siblings(".clean").css("display", "none");
		});
		$("#fec-nac").val(data.fechaNac);
	}
	$("#direccion").val((data.direccion === null) ? "" : data.direccion.toUpperCase());
	$("#poblacion").val((data.poblacion === null) ? "" : data.poblacion.toUpperCase());
	$("#cp").val(data.codigoPostal <= 0 ? "" : data.codigoPostal);
	/**
	 ** Muestra el documento de identidad segun el tipo recibido (DNI=1, PASAPORTE=2, NIE=3)
	 **/
	if (data.dni !== "" && data.dni !== null) {
		$("#sel-dni").val(1);
		$("#num-doc").val(data.dni);
	} else if (data.pasaporte !== "" && data.pasaporte !== null) {
		$("#sel-dni").val(2);
		$("#num-doc").val(data.pasaporte);
	} else if (data.nie !== "" && data.nie !== null) {
		$("#sel-dni").val(3);
		$("#num-doc").val(data.nie);
	}
	if (data.numeroHistoriaClinica > -1)
		$("#n-hist-cli").val(data.numeroHistoriaClinica == 0 ? "" : data.numeroHistoriaClinica);
	if (data.numeroSeguridadSocial > -1)
		$("#n-ss").val(data.numeroSeguridadSocial);
	if (data.numeroTarjetaSanitaria !== "")
		$("#n-tar-san").val(data.numeroTarjetaSanitaria);
	if (data.numeroCIPA > 0)
		$("#n-cipa").val(data.numeroCIPA);
	$("#tel-1").val(data.telefono1);
	$("#tel-2").val(data.telefono2);
	$("#familiar").val((data.familiar).toUpperCase());
	$("#tel-fam").val(data.telefonoFamiliar);
}



function displayDatosClinicosEnVista(data) {
	$("#antecedentes").val(data.antecedentes);
	$("#hist-actual").val(data.enfermedadActual);
	$("#evolucion-comentarios").val(data.evolucionComentarios);
}


function displayDiagnosticosEnVista(data) {
	let diagsPrim = data.filter(function (e) {
		return e.tipoDiagnostico === 1;
	});
	let diagsSec = data.filter(function (e) {
		return e.tipoDiagnostico === 2;
	});
	let diagsNoPsiq = data.filter(function (e) {
		return e.tipoDiagnostico === 3;
	});
	let estadiajes = data.filter(function (e) {
		return e.tipoDiagnostico === 4;
	});

	for (let i = 0; i < diagsPrim.length; i++) {
		let cont = diagsPrim[i].posCombo;
		diagPrim[i] = diagsPrim[i].cieDiagnostico;
		$("#diag-p-" + (cont) + " input").val(diagPrim[i] + " " + diagsPrim[i].descripcion);
		$("#diag-p-" + (cont) + " input").on("change", function () {
			diagPrim[cont - 1] = "";
		});
	
		$("#diag-p-" + (cont) + " .clean").css({ "display" : "block" });
		$("#diag-p-" + (cont) + " .clean").on("click", function () {
			diagPrim[cont - 1] = "";
			$("#diag-p-" + (cont) + " input").val("");
			$("#diag-p-" + (cont) + " input").focus();
			$("#diag-p-" + (cont) + " ul").empty();
			$("#diag-p-" + (cont) + " .clean").css({ "display" : "none" });
		});
	}

	for (let i = 0; i < diagsSec.length; i++) {
		let cont = diagsSec[i].posCombo;
		diagSec[i] = diagsSec[i].cieDiagnostico;
		$("#diag-s-" + (cont) + " input").val(diagSec[i] + " " + diagsSec[i].descripcion);
		$("#diag-s-" + (cont) + " input").on("change", function () {
			diagSec[cont - 1] = "";
		});

		$("#diag-s-" + (cont) + " .clean").css({ "display" : "block" });
		$("#diag-s-" + (cont) + " .clean").on("click", function () {
			diagSec[cont - 1] = "";
			$("#diag-s-" + (cont) + " input").val("");
			$("#diag-s-" + (cont) + " input").focus();
			$("#diag-s-" + (cont) + " ul").empty();
			$("#diag-s-" + (cont) + " .clean").css({ "display" : "none" });
		});
	}

	for (let i = 0; i < diagsNoPsiq.length; i++) {
		let cont = diagsNoPsiq[i].posCombo;
		diagNoPsiq[i] = diagsNoPsiq[i].cieDiagnostico;
		$("#diag-n-" + (cont) + " input").val(diagNoPsiq[i] + " " + diagsNoPsiq[i].descripcion);
		$("#diag-n-" + (cont) + " input").on("change", function () {
			diagNoPsiq[cont - 1] = "";
		});

		$("#diag-n-" + (cont) + " .clean").css({ "display" : "block" });
		$("#diag-n-" + (cont) + " .clean").on("click", function () {
			diagNoPsiq[cont - 1] = "";
			$("#diag-n-" + (cont) + " input").val("");
			$("#diag-n-" + (cont) + " input").focus();
			$("#diag-n-" + (cont) + " ul").empty();
			$("#diag-n-" + (cont) + " .clean").css({ "display" : "none" });
		});
	}

	for (let i = 0; i < estadiajes.length; i++) {
		let cont = estadiajes[i].posCombo;
		estad[i] = estadiajes[i].cieDiagnostico;
		$("#estadiaje-" + (cont) + " select").val(estadiajes[i].cieDiagnostico);

		$("#estadiaje-" + (cont) + " .clean").css({ "display" : "block"	});
		$("#estadiaje-" + (cont) + " .clean").on("click", function () {
			estad[i] = $("#estadiaje-" + (cont) + " select").val("");
		});
	}
}


function displayTratamientosEnVista(data, dataMup) {
	for (let i = 0; i < data.length; i++) {
		$("#s-tratamientos-psi-" + data[i].posicion).val(data[i].valor === "" ? 0 : data[i].valor);
		$("#s-tratamientos-psi-" + data[i].posicion).attr("disabled", data[i].valor === "" ? true : false);
		$("#l-tratamientos-psi-" + data[i].posicion).prop("checked", data[i].valor !== "" ? true : false);
		$("#c-fec-inicio-tratamientos-psi-" + data[i].posicion).val(data[i].fechaInicio);
		$("#c-fec-fin-tratamientos-psi-" + data[i].posicion).val(data[i].fechaFin);
	}

	$("#txt-trat-reco").val(dataMup.tratamientoRecomendacion);
	$("#txt-mup").val(dataMup.descripcion);
}


function displaySituacionClinicaFuncionalEnVista(data) {
	for (let i = 0; i < data.length; i++) {
		if (data[i].tipoSCFDAS === 0) {
			$("#n-sit-clin-" + data[i].posicion).val(data[i].valor);
			
		} else if (data[i].tipoSCFDAS === 1) {
			$("#n-discapacidad-das-" + data[i].posicion).val(data[i].valor);
		}
	}
}


function displayProgramasUnidadesEspecialesProcesosEnVista(data) {
	for (let i = 0; i < data.length; i++) {
		$("#c-prog-unid-" + data[i].posicion).val(data[i].valor);
		$("#c-prog-unid-" + data[i].posicion).attr("disabled", false);
		$("#l-prog-unid-" + data[i].posicion).prop("checked", true);
	}
}


function continueLoad() {
	$(document).on("click", function(event) {
		if (event.target.parentElement.classList[0] !== "desplegable") {
			$(".desplegable ul").css({ "display" : "none" });
		}
	});

	$("#filtro button").on("click", function(e) {
		e.preventDefault();
	});

	$(".clean").parent().css("display", "flex");
	$(".clean").parent().css("justify-content", "center");
	$(".clean").parent().css("align-items", "center");

	//vaciarArrays();

	$("input, :input").attr("autocomplete", "off");
	$("input, :input").val("");
	$("select").val(0);

	// EXTRAER ID PACIENTE DE LA URL
	{
	let params = new Params([ "idPaciente", "equipoCalle" ]);
	setMotivoAltaOptions(params.results[1] === "true");
	}
/*
	var idPac = params.results[0];
	// Comprobar que se encontró un valor válido, de lo contrario, mostrar la actuación con la última fecha
	// console.log("ID PACIENTE: " + idPac);
	if (idPac !== null && idPac !== "" && idPac !== "-1" && idPac !== "0") {
		actualPaciente = idPac;
		console.log("CALLING GETPACIENTE()");
		getPaciente(idPac);
		existingRegister = true;
	} else if (idPac === "-1") {
		nuevoPaciente = true;
	} else {
		$("#btn-reg-ant").attr("disabled", true);
		$("#btn-reg-post").attr("disabled", true);		
		nuevaActuacion();
		if (numeroHC != -1 && numeroHC != 0) {
			var params = new Params([ "numeroCita" ]);
			getPacienteFromHospital(numeroHC, params.results[0]);
		}
	}
*/
	$("#tratamientos-psi select, #prog-unid .calendar").attr("disabled", "true");

	$("#tratamientos-psi input[type=checkbox]").on("change", function (e) {
		if (e.target.checked) {
			$(e.target).siblings("select").attr("disabled", false);
			let fecIniName = "c-fec-inicio" + e.target.id.slice(1);
			let fecFinName = "c-fec-fin" + e.target.id.slice(1);
			// $("#" + fecIniName).attr("disabled", false);
			$("#" + fecFinName).attr("disabled", false);
			$("#" + fecIniName).val(new DateFormatter(new Date()).toString());
			$("#" + fecFinName).val("");
			$("#" + fecFinName + " ~ .clean").css("display", "none");
		} else {
			$(e.target).siblings("select").attr("disabled", true);
			$(e.target).siblings("select").val(0);
			let fecIniName = "c-fec-inicio" + e.target.id.slice(1);
			let fecFinName = "c-fec-fin" + e.target.id.slice(1);
			$("#" + fecIniName).val("");
			$("#" + fecFinName).val("");
			$("#" + fecIniName).attr("disabled", true);
			$("#" + fecFinName).attr("disabled", false);
			$("#" + fecFinName).val(new DateFormatter(new Date()).toString());;
			$("#" + fecFinName + " ~ .clean").css("display", "block");
		    $("#" + fecFinName + " ~ .clean").on("click", function () {
				$("#" + fecFinName).val("");
		        $(this).css("display", "none");
		    });
		}

	});

	$("#prog-unid input[type=checkbox]").on("change", function (e) {
		if (e.target.checked) {
			$(e.target).siblings().children(".calendar").attr("disabled", false);
		} else {
			$(e.target).siblings().children(".calendar").attr("disabled", true);
			$(e.target).siblings().children(".calendar").val("");
			$(e.target).siblings().children(".clean").css("display", "none");
		}
	});

	$("#alta input[type=checkbox]").on("change", function (e) {
		if (e.target.checked) {
			$(e.target).siblings().children(".calendar").attr("disabled", false);
			$("#motivo").attr("disabled", false);
		} else {
			$(e.target).siblings().children(".calendar").attr("disabled", true);
			$(e.target).siblings().children(".calendar").val("");
			$("#motivo").attr("disabled", true);
			$("#motivo").val(0);
			$(e.target).siblings().children(".clean").css("display", "none");
		}
	});

	$("input[type=number]").on("keypress", function(e) {
		if (isNaN(e.key)) {
			e.preventDefault();
		} else {
			$("#" + e.target.id).on("keyup", function(e) {
				var num = parseInt($("#" + e.target.id).val());
				if (num > parseInt($("#" + e.target.id).attr("max"))) {
					$("#" + e.target.id).val($("#" + e.target.id).attr("max"));
				}
				if (num < parseInt($("#" + e.target.id).attr("min"))) {
					$("#" + e.target.id).val($("#" + e.target.id).attr("min"));
				}
			});
		}
	});

	$("input[type=number]").on("keydown", function(e) {
		if (typeof this.alreadyChanged == "undefined") {
			this.alreadyChanged = false;
		}

		if (e.keyCode === 38) { // UP
			e.preventDefault();
			var val = parseInt($("#" + e.target.id).val());
			$("#" + e.target.id).val((val === "" || isNaN(val)) ? $("#" + e.target.id).attr("min") : val + 1);
		} else if (e.keyCode === 40) { // DOWN
			e.preventDefault();
			var val = parseInt($("#" + e.target.id).val());
			$("#" + e.target.id).val((val === "" || isNaN(val)) ? $("#" + e.target.id).attr("min") : val - 1);
		}

		var num = parseInt($("#" + e.target.id).val());
		if (num > parseInt($("#" + e.target.id).attr("max"))) {
			$("#" + e.target.id).val($("#" + e.target.id).attr("max"));
		}
		if (num < parseInt($("#" + e.target.id).attr("min"))) {
			$("#" + e.target.id).val($("#" + e.target.id).attr("min"));
		}
	});

	$("div:not(.checks) > div > input[type=checkbox]").bind("change", function (ev) {
		var input;
		if ($(ev.target).siblings()[1].tagName.toUpperCase() !== "SELECT"
			&& $(ev.target).siblings()[1].hasChildNodes()) {
			input = $(ev.target).siblings().children("input");
		} else {
			input = $(ev.target).siblings()[1];
		}
	});

	$(".checks input[type=checkbox]").bind("change", function (ev) {
		if (ev.target.checked) {
			if (ev.target.id === "cb-prog-joven") {
				$("#fec-pj-inicio").attr("disabled", false);
				$("#fec-pj-fin").attr("disabled", false);
				$("#fec-pj-fin").val("");
				$("#fec-pj-inicio").val("");
				$("#fec-pj-inicio").val(new DateFormatter(new Date()).toString());;
				$("#fec-pj-inicio ~ .clean").css("display", "block");
				$("#fec-pj-fin ~ .clean").css("display", "none");
			    $("#fec-pj-inicio ~ .clean").on("click", function () {
					$("#fec-pj-inicio").val("");
			        $(this).css("display", "none");
			    });
			}
		} else {
			if (ev.target.id === "cb-prog-joven") {
//				$("#fec-pj-inicio").attr("disabled", true);
//				$("#fec-pj-fin").attr("disabled", true);
				$("#fec-pj-fin").val("");
				$("#fec-pj-inicio").val("");
				$("#fec-pj-fin").val(new DateFormatter(new Date()).toString());;
				$("#fec-pj-fin ~ .clean").css("display", "block");
				$("#fec-pj-inicio ~ .clean").css("display", "none");
			    $("#fec-pj-fin ~ .clean").on("click", function () {
					$("#fec-pj-fin").val("");
			        $(this).css("display", "none");
			    });
			}
		}
	});

	{
	let params = new Params([ "idFacultativo" ]);
	$("#profesional").val(params.results[0]);
	}

	$(".desplegable").hover(function () {
		if ($(this).children("input").val() != "") {
			$("#tooltip-text").text($(this).children("input").val());
			var offsX = ($(this).width() - $("#tooltip-container").width()) / 2;
			$("#tooltip-container").css("left",	$(this).offset().left + offsX);
			$("#tooltip-container").css("top", $(this).offset().top	- $("#tooltip-container").height() - 10);

			$("#tooltip-container").show();
		}
	}, function () {
		$("#tooltip-container").hide();
	});

	$("#l-ate-wrap").hover(function () {
		$("#tooltip-text").text($("#lugar-ate").val());
		var offsX = ($("#lugar-ate").width() - $("#tooltip-container").width()) / 2;
		$("#tooltip-container").css("left", $("#lugar-ate").offset().left + offsX);
		$("#tooltip-container").css("top", $("#lugar-ate").offset().top - $("#tooltip-container").height() - 10);

		$("#tooltip-container").show();
	}, function () {
		$("#tooltip-container").hide();
	});
	
	htmlLoaded();
}


function setMotivoAltaOptions(equipoCalle) {
	$("#motivo").append($('<option>').val(0).text("-- Seleccione motivo"));
	if (equipoCalle) {
		$("#motivo").append($('<option>').val(1).text("Retoma seguimiento previo en Salud Mental"));
		$("#motivo").append($('<option>').val(2).text("Inicia seguimiento en otro dispositivo"));
		$("#motivo").append($('<option>').val(3).text("Redefinición diagnóstica"));
		$("#motivo").append($('<option>').val(4).text("Integración en la Red Normalizada de Salud Mental"));
		$("#motivo").append($('<option>').val(5).text("Programa de Retorno Supervisado a su país de origen"));
		$("#motivo").append($('<option>').val(6).text("Derivación a Dispositivo de Larga Estancia"));
		$("#motivo").append($('<option>').val(7).text("Derivación a Dispositivo de Psicogeriatría"));
		$("#motivo").append($('<option>').val(8).text("Internamiento en Centro Penintenciario"));
		$("#motivo").append($('<option>').val(9).text("Exitus"));
		$("#motivo").append($('<option>').val(10).text("Desaparición"));
	} else {
		$("#motivo").append($('<option>').val(1).text("Derivación AP"));
		$("#motivo").append($('<option>').val(2).text("Derivación otro especialista o servicio"));
		$("#motivo").append($('<option>').val(3).text("Derivación a CSM de HCSC"));
		$("#motivo").append($('<option>').val(4).text("Traslado área"));
		$("#motivo").append($('<option>').val(5).text("Abandono"));
		$("#motivo").append($('<option>').val(6).text("Defunción"));
	}
}


function vaciarArrays() {
	diagPrim = [];
	diagSec = [];
	diagNoPsiq = [];
	estad = [];

	scf = [];
	das = [];

	trat = [];
	puep = [];
}


function limpiarVista() {
	let inputs = $("input");
	for (let i = 0; i < inputs.length; i++) {
		$("#" + inputs[i].id).val("");
		$("#" + inputs[i].id + " ~ .clean").css("display", "none");
	}
	
	let selects = $("select");
	for (let i = 0; i < selects.length; i++) {
		$("#" + selects[i].id).val(0);
	}
	
	let checks = $("[type=checkbox]");
	for (let i = 0; i < checks.length; i++) {
		$("#" + checks[i].id).prop("checked", false);
	}
	
	let textArea = $("textarea");
	for (let i = 0; i < textArea.length; i++) {
		$("#" + textArea[i].id).val("");
	}
}


function desactivarCampos() {
	let inputsList = $("select, input").not("#fec-mod");
	for (let i = 0; i < inputsList.length; i++) {
		$("#" + inputsList[i].id).prop("disabled", true);
	}

	$(".clean").prop("disabled", true);

	/** En modo consulta no se permite guardar registros **/
	$("#btn-guardar").prop("disabled", true);
	$("#nuevo-registro").prop("disabled", true);

	/** Desactiva los textareas pero permite que se pueda hacer scroll en su contenido **/
	$("#txt-mup").attr("readonly", "true");
	$("#txt-mup").css("background-color", "#f0f0f0");//"#ebebe4");

	$("#txt-trat-reco").attr("readonly", "true");
	$("#txt-trat-reco").css("background-color", "#f0f0f0");

	$("#antecedentes").attr("readonly", "true");
	$("#antecedentes").css("background-color", "#f0f0f0");

	$("#hist-actual").attr("readonly", "true");
	$("#hist-actual").css("background-color", "#f0f0f0");

	$("#evolucion-comentarios").attr("readonly", "true");
	$("#evolucion-comentarios").css("background-color", "#f0f0f0");
}


function validarElementoActuacion(elemento) {
	if (elemento.tagName === "SELECT") {
		return (elemento.value !== undefined && elemento.value !== null	&& elemento.value !== ''
					&& elemento.value !== "0") ? elemento.value : "";
		
	} else if (elemento.tagName === "INPUT") {
		return (elemento.value !== undefined && elemento.value !== null
					&& elemento.value !== '') ? elemento.value : elemento.checked;
	}

	return "";
}


function mostrarActuacionAlmacenada(actuacionAlmacenada) {
	limpiarVista();
	
	numeroCita = numeroCitaNuevoRegistro;
	numeroICU = numeroICUNuevoRegistro;
	
	// < Rellenar los campos con los datos almacenados en el objeto > //
	for ( let propId in actuacionAlmacenada) {
		if (actuacionAlmacenada[propId].valor !== undefined	&& actuacionAlmacenada[propId].valor !== null
				&& actuacionAlmacenada[propId].valor !== ""	&& actuacionAlmacenada[propId].valor !== false) {
			
			if (actuacionAlmacenada[propId].tipo === "INPUT" && actuacionAlmacenada[propId].valor === true) {
				$("#" + propId).prop("checked", true);
			} else {
				$("#" + propId).val(actuacionAlmacenada[propId].valor);
				
				if (~propId.indexOf("diag")) {
					$("#" + propId.slice(2) + " .clean").css({ "display" : "block" });
					
				} else if (~propId.indexOf("prog-unid")) {
					$("#" + propId.slice(2) + " .clean").css({ "display" : "block" });
					
				} else if (~propId.indexOf("fec-pj")) {
					$("#" + propId + " .clean").css({ "display" : "block" });
				}
			}
		}
	}
	diagPrim   = actuacionAlmacenada.diagPrimSt;
	diagSec    = actuacionAlmacenada.diagSecSt;
	diagNoPsiq = actuacionAlmacenada.diagNoPsiqSt;
	actuacionActual = actuacionAlmacenada.idActuacion;
}


function vistaToJson(fecha) {
	let json = {};
	
	/** PACIENTE **/
	let jsonPaciente = {};
	let paciente = $("#paciente").serializeArray();
	$(paciente).each(function (index, obj) {
		jsonPaciente[obj.name] = obj.value;
	});
	jsonPaciente["tipo-doc"] = $("#sel-dni option:selected").val();
	jsonPaciente["id-paciente"] = Number(pacienteActual);
	
	json["paciente"] = jsonPaciente;
	
	/** AGENDA **/
	let agenda = $("#agenda").serializeArray();
	$(agenda).each(function (index, obj) {
		json[obj.name] = obj.value;
	});
	if (json["id-agenda"].length > 6) {
		showMessageBox("EL CAMPO 'CÓDIGO AGENDA' NO PUEDE TENER MÁS DE 6 CARÁCTERES (" + json["id-agenda"] + ")");
		return;
	}
	json["id-profesional"] = $("#cod-empleado").val();
	
	json["equipo-calle"] = $("#equipo-calle").is(":checked");
	
	/** ALTA **/
	if ($("#alta-check").prop("checked") && ($("#cal-alta").val() === "" || $("#motivo").val() === "0")) {
		showMessageBox("HA SELECCIONADO 'ALTA MÉDICA' PERO NO HA INCLUIDO UNA FECHA O UN MOTIVO");
		return;				
	}
	json["fecha-alta"] = $("#cal-alta").val();
	json["motivo-alta"] = $("#motivo").val();
	
	/** ACTUACION **/
	json["id-actuacion"] = actuacionActual;
	json["fecha-actuacion"] = fecha;
	json["incapacidad-total"] = $("#total").prop("checked");
	json["curatela-salud"] = $("#cur-salud").prop("checked");
	json["curatela-economica"] = $("#cur-eco").prop("checked");
	json["prog-cont-cuidados"] = $("#cb-cont-cuidados").prop("checked");
//	if ($("#cb-prog-joven").prop("checked") && $("#fec-pj-fin").val() === "") {
//	showMessageBox("HA SELECCIONADO 'PROGRAMA JOVEN' PERO NO HA INCLUIDO UNA FECHA DE FINALIZACIÓN");
//	return;		
//}
	if ($("#cb-prog-joven").prop("checked") && $("#fec-pj-inicio").val() === "") {
		showMessageBox("HA SELECCIONADO 'PROGRAMA JOVEN' PERO NO HA INCLUIDO UNA FECHA DE INICIO");
		return;		
	}
	json["prog-joven"] = $("#cb-prog-joven").prop("checked");
	json["fec-ini-pj"] = $("#fec-pj-inicio").val();
//	json["fec-fin-pj"] = $("#fec-pj-fin").val();
	if ($("#fec-pj-fin").val() === "")
		json["fec-fin-pj"] = "";
	else
		json["fec-fin-pj"] = $("#fec-pj-fin").val();		
	json["numeroCita"] = numeroCita;
	json["numeroICU"] = numeroICU;
	json["med-proteccion"] = $("#med-proteccion").prop("checked");
	json["residencia"] = $("#residencia").prop("checked");
//	console.log("vistaToJson -> numeroICU: " + numeroICU);
//	console.log("vistaToJson -> numeroCita: " + numeroCita);
	
	/** DIAGNOSTICOS y ESTADIAJES **/
	let jsonDiag = [];
	let diagPrimarios = $("#diag-psi-prin").serializeArray();
	let diagValidInput = true;
	let blankFounded = false;
	for (let i = 0; i < diagPrimarios.length; i++) {
		if (diagPrimarios[i].value === "" && i < diagPrimarios.length) {
			blankFounded = true;
		}
		else if (blankFounded) {
			diagValidInput = false;
			break;
		}
		if (diagPrimarios[i].value !== "" && (diagPrim[i] === "" || diagPrim[i] === undefined)) {
			diagValidInput = false;
		}
	}
	if (!diagValidInput) {
		showMessageBox("LOS DIAGNOSTICOS PRIMARIOS DEBEN INTRODUCIRSE SECUENCIALMENTE Y SELECCIONANDOLOS DEL DESPLEGABLE DE LA BÚSQUEDA");
		return;
	}
	$(diagPrimarios).each(function (index, obj) {
		if (obj.value !== "") {
			jsonDiag.push({
				tipoDiagnostico: 1,
				posCombo: index + 1,
				codigo: diagPrim[index]
			});
		}
	});
	let diagSecundarios = $("#diag-psi-sec").serializeArray();
	blankFounded = false;
	for (let i = 0; i < diagSecundarios.length; i++) {
		if (diagSecundarios[i].value === "" && i < diagSecundarios.length) {
			blankFounded = true;
		}
		else if (blankFounded) {
			diagValidInput = false;
			break;
		}
		if (diagSecundarios[i].value !== "" && (diagSec[i] === "" || diagSec[i] === undefined)) {
			diagValidInput = false;
		}
	}
	if (!diagValidInput) {
		showMessageBox("LOS DIAGNOSTICOS SECUNDARIOS DEBEN INTRODUCIRSE SECUENCIALMENTE Y SELECCIONANDOLOS DEL DESPLEGABLE DE LA BÚSQUEDA");
		return;
	}
	$(diagSecundarios).each(function (index, obj) {
		if (obj.value !== "") {
			jsonDiag.push({
				tipoDiagnostico: 2,
				posCombo: index + 1,
				codigo: diagSec[index]
			});
		}
	});
	let diagNoPsiquiatria = $("#diag-no-psi").serializeArray();
	blankFounded = false;
	for (let i = 0; i < diagNoPsiquiatria.length; i++) {
		if (diagNoPsiquiatria[i].value === "" && i < diagNoPsiquiatria.length) {
			blankFounded = true;
		}
		else if (blankFounded) {
			diagValidInput = false;
			break;
		}
		if (diagNoPsiquiatria[i].value !== "" && (diagNoPsiq[i] === "" || diagNoPsiq[i] === undefined)) {
			diagValidInput = false;
		}
	}
	if (!diagValidInput) {
		showMessageBox("LOS DIAGNOSTICOS NO PSIQUIATRICOS DEBEN INTRODUCIRSE SECUENCIALMENTE Y SELECCIONANDOLOS DEL DESPLEGABLE DE LA BÚSQUEDA");
		return;
	}
	$(diagNoPsiquiatria).each(function (index, obj) {
		if (obj.value !== "") {
			jsonDiag.push({
				tipoDiagnostico: 3,
				posCombo: index + 1,
				codigo: diagNoPsiq[index]
			});
		}
	});
	let estadiajes = $("#form-estadiaje").serializeArray();
	$(estadiajes).each(function (index, obj) {
		if (obj.value > 0) {
			jsonDiag.push({
				tipoDiagnostico: 4,
				posCombo: index + 1,
				codigo: obj.value
			})
		}
	});
	
	json["diagnosticos"] = jsonDiag;
	
	/** SITUACION CLINICA y FUNCIONAL / DISCAPACIDAD DAS **/
	let jsonSitClinDAS = [];
	let sitClin = $("#sit-clin").serializeArray();
	$(sitClin).each(function (index, obj) {
		if (obj.value !== "") {
			jsonSitClinDAS.push({
				tipo: 0,
				valor: obj.value,
				posicion: index + 1
			});
		}
	});
	
	let DAS = $("#discapacidad-das").serializeArray();
	$(DAS).each(function (index, obj) {
		if (obj.value !== "") {
			jsonSitClinDAS.push({
				tipo: 1,
				valor: obj.value,
				posicion: index + 1
			});
		}
	});
	
	json["scf"] = jsonSitClinDAS;
	
	/** TRATAMIENTOS (serializeArray() no lee los campos que esten desactivados) **/
	let validInput = true;
	$("#tratamientos select").each(function () {
		if ($(this).val() === null && $(this).siblings("input:checkbox")[0].checked) {
			let label = $(this).siblings("label")[0];
			showMessageBox("<span style='font-weight:bold;'>AVISO:</span> NO SE SELECCIONARON SESIONES " +
						"PARA EL TRATAMIENTO '" + label.textContent.replace(':', '') + "'");
			validInput = false;
		}
//		else if ($(this).siblings("input:checkbox")[0].checked && $("#fec-fin-" + ($(this).parent()[0]).id + " .calDiv input").val() === "") {
//			let label = $(this).siblings("label")[0];
//			showMessageBox("NO SE SELECCIONÓ FECHA DE FINALIZACIÓN PARA EL TRATAMIENTO '" +
//							label.textContent.replace(':', '') + "'");
//			validInput = false;
//		}
	});
	if (!validInput) {
		return;
	}
	
	let jsonTrat = [];
	let trats = $("#tratamientos select").serializeArray();
	trats = trats.concat($("#tratamientos .calendar").serializeArray().filter(function (item) {
		let partName = item.name.substr(item.name.length - 5, item.name.length);
		if ($('#l-tratamientos-' + partName).is(':checked') === false) {
			return item.value !== "";
		}
		else return false;
	}));
//	trats = trats.concat(function () {
	let data = [];
	let tmp = $('#tratamientos .calendar:disabled').each(function (item) {
		if (this.value !== "") {
			let partName = this.name.substr(this.name.length - 5, this.name.length);
			if ($('#l-tratamientos-' + partName).is(':checked') === false) {
				data.push({name: this.name, value: this.value});
			}
		}
	});
	trats = trats.concat(data);
//	});
	$(trats).each(function (index, obj) {
		if (obj.value !== "" && obj.name.indexOf("c-fec") === -1) {
			jsonTrat.push({
				tipo: 1,
				valor: obj.value,
				posicion: parseInt(obj.name[obj.name.length - 1]),
				fechaInicio: $("#c-fec-inicio" + obj.name.substring(1)).val(),
				fechaFin: $("#c-fec-fin" + obj.name.substring(1)).val()
			});
		}
		else {
			jsonTrat.push({
				tipo: 1,
				valor: "",
				posicion: parseInt(obj.name[obj.name.length - 1]),
				fechaInicio: "",
				fechaFin: obj.value
			});			
		}
	});
	
	json["tratamientos"] = jsonTrat;
	
	/** TRATAMIENTOS MUP y TRATAMIENTOS/RECOMENDACIONES **/
	let jsonMup = {
		"trat-recom": $("#txt-trat-reco").val(),
		"mup": $("#txt-mup").val()
	};
	
	json["tratamientos-mup"] = jsonMup;
	
	/** PROGRAMAS, UNIDADES ESPECIALES, PROCESOS **/
	validInput = true;
	$("#prog-unid input:checkbox").each(function () {
		if ($(this)[0].checked && $(this).siblings("div").children("input").val() === "") {
			let label = $(this).siblings("label")[0];
			showMessageBox("<span style='font-weight:bold;'>AVISO:</span> NO SE SELECCIONÓ FECHA DE INICIO PARA EL PROGRAMA '" +
							label.textContent.replace(':', '') + "'");
			validInput = false;
		}
	});
	if (!validInput) {
		return;
	}
	
	let jsonProgUnids = [];
	let progsUnids = $("#prog-unid").serializeArray();
	$(progsUnids).each(function (index, obj) {
		if (obj.value !== "") {
			jsonProgUnids.push({
				tipo: 1,
				valor: obj.value,
				posicion: parseInt(obj.name[obj.name.length - 1])
			});
		}
	});
	
	json["puep"] = jsonProgUnids;
	
	//console.log(JSON.stringify(json));
	return json;
}


function imprimirInforme(send, download) {
	var confirmImprimir = true;
	if (editandoRegistro === true) {
		confirmImprimir = confirm("El registro aún no esta cerrado. ¿Desea imprimir el informe en el estado actual?");
	}
	if (confirmImprimir === false) {
		return;
	}
	/** Calcular la edad del paciente mediante la fecha de nacimiento **/
	
	var parts = $("#fec-nac").val().split('/');
	var fecnacDate = new Date(parts[2], parts[1] - 1, parts[0]);
	var birthyear = fecnacDate.getFullYear();
	var birthmonth = fecnacDate.getMonth();
	var birthday = fecnacDate.getDate();
	var today = new Date();
	var nowyear = today.getFullYear();
	var nowmonth = today.getMonth();
	var nowday = today.getDate();

	var age = nowyear - birthyear;
	var agemonth = nowmonth - birthmonth;
	var ageday = nowday - birthday;

	if (agemonth < 0 || (agemonth == 0 && ageday < 0)) {
		age = parseInt(age) - 1;
	}

	nowmonth++;
	var nowmonthStr = "" + nowmonth;
	var nowdayStr = "" + nowday
	if (nowmonth < 10)
		nowmonthStr = "0" + nowmonthStr;
	if (nowday < 10)
		nowdayStr = "0" + nowdayStr;

	/***************************************************************************
	 * Extrae la fecha del informe de la fecha del registro seleccionado (solo
	 * se debe poder generar informe del ultimo registro del paciente) *
	 **************************************************************************/
	var fechaInforme = $("#fec-mod option:selected").text().replace("Nuevo: ", "");

	/** TRATAMIENTOS PARA INFORME * */
	var tratamientos = [];
	let inputs = $("#tratamientos-psi select");
	let checks = $("#tratamientos-psi input[type=checkbox]");
	for (let i = 0; i < inputs.length; i++) {
		if ($("#" + checks[i].id).prop("checked")
				|| $("#c-fec-fin-" + inputs[i].id.slice(2)).val() !== "") {
			let valorText = $("#" + inputs[i].id + " option:selected").text() + " sesiones";
			if ($("#" + checks[i].id).prop("checked") === false)  {
				valorText = "Finalizado";
			}
			tratamientos.push({
				Tipo: 1,
				TratName: $("#" + inputs[i].id).siblings("label").html(),
				Valor: $("#" + inputs[i].id).val(),
				ValorText: valorText,//$("#" + inputs[i].id + " option:selected").text(),
				Posicion: i + 1,
				FecIni: $("#c-fec-inicio-" + inputs[i].id.slice(2)).val(),
				FecFin: $("#c-fec-fin-" + inputs[i].id.slice(2)).val()
			});
		}
	}
	
	/** DIAGNOSTICOS PARA INFORME **/
	var diagnosticos = [];
	let diagsPsiPri = $("#diag-psi-prin input");
	let diagsPsiSec = $("#diag-psi-sec input");
	let diagsNoPsiq = $("#diag-no-psi input");
	for (let i = 0; i < diagsPsiPri.length; i++) {
		if ($("#" + diagsPsiPri[i].id).val() !== "")
			diagnosticos.push({
				DiagName: $("#" + diagsPsiPri[i].id).val()
			});
	}
	for (let i = 0; i < diagsPsiSec.length; i++) {
		if ($("#" + diagsPsiSec[i].id).val() !== "")
			diagnosticos.push({
				DiagName: $("#" + diagsPsiSec[i].id).val()
			});
	}
	for (let i = 0; i < diagsNoPsiq.length; i++) {
		if ($("#" + diagsNoPsiq[i].id).val() !== "")
			diagnosticos.push({
				DiagName: $("#" + diagsNoPsiq[i].id).val()
			});
	}
	
	/** Crear un JSON con los datos que necesita el informe * */
	var jsonData = {
			enviarHIS: send,
			download: download,
			nombre: $("#nombre").val(),
			apellido1: $("#apellido1").val(),
			apellido2: $("#apellido2").val(),
			sexo: $("#sexo option:selected").text(),
			fecnac: $("#fec-nac").val(),
			edad: age,
			domicilio: $("#direccion").val(),
			poblacion: $("#poblacion").val(),
			codpostal: $("#cp").val(),
			dni: $("#num-doc").val(),
			numerohc: $("#n-hist-cli").val(),
			numeross: $("#n-ss").val(),
			numerots: $("#n-tar-san").val(),
			numerocipa: $("#n-cipa").val(),
			numtlf1: $("#tel-1").val(),
			numtlf2: $("#tel-2").val(),
			familiar: $("#familiar").val(),
			tlffam: $("#tel-fam").val(),
			//codfacul: $("#profesional").val(),
			codfacul: $("#cod-empleado").val(),
			centro: $("#lugar-ate").val(),
			fecha: fechaInforme,
			antecedentes: $("#antecedentes").val(),
			historiaactual: $("#hist-actual").val(),
			evolucioncomentarios: $("#evolucion-comentarios").val(),
			tratamientosrecomendaciones: $("#txt-trat-reco").val(),
			tratamientos: tratamientos,
			diagnosticos: diagnosticos,
			fechahoy: nowdayStr + "/" + nowmonthStr + "/" + nowyear,
			
			numICU: numeroICU,
			numeroCita: numeroCita,
			prestacion: $("#tipo-prest").val()
	};
	//console.log(numeroICU);
	//console.log(numeroCita);

	/**
	 * RECIBE EL PDF DEL INFORME DEL PACIENTE - ASI SI FUNCIONA
	 */
	var fData = new FormData();
	fData.append('data', JSON.stringify(jsonData));

	var xhr = new XMLHttpRequest();
	xhr.open('POST', url + "?peticion=" + RQ_ENVIAR_INFORME_HL7, true);
	xhr.setRequestHeader("charset", "utf-8");
//	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded;  charset=utf-8");
	xhr.send(fData);
//	xhr.send("peticion=" + RQ_ENVIAR_INFORME_HL7 + "&data=" + JSON.stringify(jsonData));
	if (download) {
		xhr.responseType = 'arraybuffer';
		xhr.onload = function(e) {
			if (this.status == 200) {
				let blob = new Blob([ this.response ], { type: "application/pdf" });
				let filename = "Report_" + jsonData.nombre + "_" + jsonData.apellido1 + "_" + new Date() + ".pdf";
//				
//				if(window.navigator.msSaveOrOpenBlob) {
//					window.navigator.msSaveOrOpenBlob(blob, filename);
//				}
//				else {
//					var link = document.createElement('a');
//					link.href = window.URL.createObjectURL(blob);
//					link.download = filename;
//					link.click();
//				}
				saveAs(blob, "Report_" + jsonData.nombre + "_" + jsonData.apellido1 + "_" + new Date() + ".pdf");
				/** Estas lineas sirven para navegadores modernos, se pueden utilizar en pruebas locales **/
//				var link = document.createElement('a');
//				link.href = window.URL.createObjectURL(blob);
//				link.download = filename;
//				link.click();
			}
		};
	}
	else if (send) {
		xhr.responseType = 'application/json';
		xhr.onload = function(e) {
			if (this.status == 200) {
				console.log(this.response);
			}
		};
	}
}


//function exportToCSV() {
//	var xhr = new XMLHttpRequest();
//	xhr.open('POST', url, true);
//	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded;  charset=utf-8");
//	xhr.send("peticion=" + RQ_EXPORTAR_A_CSV);// + "&idPaciente=" + pacienteActual);
//	xhr.responseType = 'arraybuffer';
//	
//	xhr.onload = function(e) {
//		if (this.status == 200) {
//			let blob = new Blob([ this.response ], { type: "application/zip" });
//			let filename = "HCSCPSIQ_" + new Date() + ".zip";
//			
//			if(window.navigator.msSaveOrOpenBlob) {
//				window.navigator.msSaveOrOpenBlob(blob, filename);
//			}
//			else {
//				var link = document.createElement('a');
//				link.href = window.URL.createObjectURL(blob);
//				link.download = filename;
//				link.click();
//			}
////			saveAs(blob, filename);
//		}
//	};
//}


function goMUP(e) {
	e.preventDefault();
	/** FORMATO DE ACCESO AL MUP
	'https://mup.salud.madrid.org/csm-mup-webapp/prescripcionesIndirecto?cipa=' + cipa + '&dni=' + dniMedico
	+ '&cias=' + ciasMedico + '&idcentro=' + centro + '&codambitogeneral=' + codAmbitoGeneral + '&codambito='
	+ codAmbito + '&numicu=' + numicu + '&tipoapertura=' + tipoApertura;
	
	Siendo:
	Cipa, CIPA del paciente.
	Dni, DNI del médico que realiza la acción
	Cias, CIAS del médico
	Numicu, número de episodio

	Y el resto sería valores fijos:
	centro = '2546';
	codAmbitoGeneral = 'AH';
	codAmbito = 'CEX';
	tipoApertura = 'NUEVAVENTANA';

	Los datos del paciente, médico y episodio son opcionales. A tener en cuenta:
	Si no le pasas los datos del paciente te abrirá el MUP pero sin un paciente cargado.
	Si no le pasas los datos del médico te abrirá el MUP pero en la pantalla para que se identifique.
	El episodio es necesario pasarlo para que la prescripción quede bien asociada al mismo. No lo tenemos así es que no lo podemos pasar.
	**/
	let MUPLocation = "https://mup.salud.madrid.org/csm-mup-webapp/prescripcionesIndirecto";
	MUPLocation += "?idcentro=2546";
	MUPLocation += "&codambitogeneral=AH";
	MUPLocation += "&codambito=CEX";
	MUPLocation += "&tipoapertura=NUEVAVENTANA";
	MUPLocation += "&dni=" + gUserDni;
	if (gUserCias !== 'NULL') MUPLocation += "&cias=" + gUserCias;
	if (numeroICU !== "" && !isNaN(numeroICU) && numeroICU !== "-1" && numeroICU !== "0") {
		MUPLocation += "&numIcu=" + numeroICU;
	}
	if ($("#n-cipa").val() !== "") {
		MUPLocation += "&cipa=" + $("#n-cipa").val();
	}
	
	$.ajax({
		data: {
			"peticion": RQ_DEBUG_MUP_LOCATION,
			"mupString": MUPLocation
		},
		type: "get",
		url: url,
		cache: false,
		success: function (response) {
			console.log(response);
		}
	});
	
	ignoreBeforeUnload = true;
	
	//location.href = MUPLocation;
	window.open(MUPLocation, '_blank');
}


function cerrarSesion() {
	let params = new Params(["idFacultativo"]);
	
	$.ajaxSetup({ "cache": false });
	let closeSession = $.get(url, {
		"peticion": RQ_CERRAR_SESION,
		"id-facultativo": params.results[0]
	});
	
	$.when(closeSession).done(function (response) {
		console.log(response);
	}).fail(function (response) {
		console.log(response);
	})
	
	location.href = "login.html";
}


function showMessageBox(message) {
	$("#msg-box-content").children("p").html(message);
	$("#msg-box-background").css("display",	"block");
}
function closeMessageBox() {
	$("#msg-box-background").css("display",	"none");
}


/** #######################################################################################
 **
 ** OBJECTOS JAVASCRIPT
 **/
var DateFormatter = function DateToShortFormat(date) {
	this.fecha = date;
};
DateFormatter.prototype.toString = function() {
	let fecha = new Date();
	let dia = fecha.getDate() >= 10 ? fecha.getDate() : "0" + fecha.getDate().toString();
	let mes = fecha.getMonth() + 1 >= 10 ? (fecha.getMonth() + 1).toString() : "0" +
			(fecha.getMonth() + 1).toString();

	return dia + "/" + mes + "/" + fecha.getFullYear().toString();
}


function StoredActuacion() {
	this.diagPrimSt   = diagPrim;
	this.diagSecSt    = diagSec;
	this.diagNoPsiqSt = diagNoPsiq;
	this.idActuacion  = actuacionActual;
	
	let inputsList = $("select, input");
	
	for (let i = 0; i < inputsList.length; i++) {
		this[inputsList[i].id] = {
				tipo: inputsList[i].tagName,
				valor: validarElementoActuacion(inputsList[i])
		};
		if (inputsList[i].id == "alta-check") {
			console.log("Guardando alta-check (" + this.name + "): " + this[inputsList[i].id].valor);
		}
	}
	this["txt-mup"] = {
			tipo: "INPUT",
			valor: $("#txt-mup").val()
	};
	this["txt-trat-reco"] = {
			tipo: "INPUT",
			valor: $("#txt-trat-reco").val()
	};
	this["antecedentes"] = {
			tipo: "INPUT",
			valor: $("#antecedentes").val()
	};
	this["hist-actual"] = {
			tipo: "INPUT",
			valor: $("#hist-actual").val()
	};
	this["evolucion-comentarios"] = {
			tipo: "INPUT",
			valor: $("#evolucion-comentarios").val()
	};
}
StoredActuacion.prototype.isEqual = function(other) {
	for ( let prop in this) {
		if (prop !== "fec-mod") {
			if (other[prop].valor !== this[prop].valor) {
				console.log(prop + ": " + other[prop].valor);
				console.log(prop + ": " + this[prop].valor);
				return false;
			}
		}
	}
	return true;
}

StoredActuacion.prototype.containsSomething = function() {
	for ( let prop in this) {
		if (prop !== "fec-mod" && prop !== "equipo-calle" && prop !== "profesional" && prop !== "isEqual" && prop !== "containsSomething") {
			if (this[prop].valor !== false && this[prop].valor !== "") {
				return true;
			}
		}
	}
	return false;
}
