/**
 * 
 **/
const RQ_CITAS_POR_FACULTATIVO     = 20;
const RQ_OBTENER_PACIENTE_POR_CIPA = 25;
const RQ_CERRAR_SESION             = 3500;


$(document).ready(function () {
	
	let todayDate = new Date();
	let day = (todayDate.getDate().length < 2) ? '0' + todayDate.getDate() : todayDate.getDate();
	let month = ((todayDate.getMonth() + 1 + "").length < 2) ? '0' + (todayDate.getMonth() + 1) : todayDate.getMonth() + 1;
	let todayString = day + "/" + month	+ "/" + todayDate.getFullYear();
	
	loadCalendar();
	
	$("#table-citas").dataTable({
		"language": {
			"url": "js/spanish.lang.json"
		},
		"createdRow": function (row, data, dataIndex) {
			if (data[9] === true) {
				$(row).addClass('atendida');
			}
			if (data[4] === "PACIENTE" && data[5] === "NO ENCONTRADO" && data[6] === "ERROR DE CONEXIÓN AL HPHIS") {
				$(row).addClass('paciente-no-encontrado');
			}
		},
		"aoColumnDefs": [
			{ "bVisible": true, "aTargets": 0 },
			{ "bVisible": true, "aTargets": 1 },
			{ "bVisible": true, "aTargets": 2 },
			{ "bVisible": true, "aTargets": 3 },
			{ "bVisible": true, "aTargets": 4 },
			{ "bVisible": true, "aTargets": 5 },
			{ "bVisible": true, "aTargets": 6 },
			{ "bVisible": true, "aTargets": 7 },
			{ "bVisible": true, "aTargets": 8 },
			{ "bVisible": false, "aTargets": 9 },
			{ "bVisible": false, "aTargets": 10 },
			{ "bVisible": false, "aTargets": 11 },
			{ "bVisible": false, "aTargets": 12 }
		],
		"order": [[ "2", "asc" ]]
	});
	
	$("#fec-citas").on("change paste keyup", function() {
		cargarCitas();
	});
	
	$("#fec-citas").val(todayString).change();
});	
	

function cargarCitas() {
	/** Comprobar si existe el parametro idFacultativo para solo permitir acceder a usuariso que hayan pasado el login **/
	{
		let params = new Params([ "idFacultativo" ]);
		if (params.results[0] === "" || params.results[0] === undefined) {
			location.href = "login.html";
		}
	}
	/** Fin de la comprobacion para permitir usuarios que pasen por el login **/
	
	$("#table-citas").DataTable().clear().draw();
	
	/*$("#table-citas").dataTable({
		"language": {
			"url": "js/spanish.lang.json"
		},
		"createdRow": function (row, data, dataIndex) {
			if (data[9] === true) {
				$(row).addClass('atendida');
			}
		},
		"aoColumnDefs": [
			{ "bVisible": true, "aTargets": 0 },
			{ "bVisible": true, "aTargets": 1 },
			{ "bVisible": true, "aTargets": 2 },
			{ "bVisible": true, "aTargets": 3 },
			{ "bVisible": true, "aTargets": 4 },
			{ "bVisible": true, "aTargets": 5 },
			{ "bVisible": true, "aTargets": 6 },
			{ "bVisible": true, "aTargets": 7 },
			{ "bVisible": true, "aTargets": 8 },
			{ "bVisible": false, "aTargets": 9 },
			{ "bVisible": false, "aTargets": 10 },
			{ "bVisible": false, "aTargets": 11 },
			{ "bVisible": false, "aTargets": 12 }
		]
	});*/
	
	let params = new Params(["idFacultativo", "idFacultaAlt"]);
	
	$.ajaxSetup({ "cache": false });
	let citas = $.get(url, {
		"peticion": RQ_CITAS_POR_FACULTATIVO,
		"id-facultativo": params.results[0],
		"id-faculta-alt": params.results[1],
		"fecha": $("#fec-citas").val()
	});
	
	setLoader();
	$.when(citas).done(function (response) {
		if (response.citas.length === 0) {			
			$("#empty-list").css({"display":"block"});	            		
			$("#facultativo-nombre").text(
					"Dr/Dra: " + response.facultativo.nombre + " "
								+ response.facultativo.apellido1 + " "
								+ response.facultativo.apellido2
				);
		}
		else {
			listarCitas(response);
		}
		unsetLoader();
	}).fail(function (response) {
		console.log(response);
		unsetLoader();
	})
}


function listarCitas(response) {
	let citasTable = $("#table-citas").DataTable();
	
	$("#empty-list").css({"display":"none"});
	
	$("#facultativo-nombre").text(
		"Dr/Dra: " + response.citas[0].nombreFacultativo + " " + response.citas[0].apellidosFacultativo
	);
	//console.log(response.citas[0].nombreFacultativo);
	
	for (let i = 0; i < response.citas.length; i++) {
		let item = response.citas[i];
		
		if (item.nombrePaciente === "" && item.apellido1Paciente === "" && item.apellido2Paciente === "") {
			item.apellido1Paciente = "PACIENTE";
			item.apellido2Paciente = "NO ENCONTRADO";
			item.nombrePaciente = "ERROR DE CONEXIÓN AL HPHIS";
			item.idAgenda = "¡ERROR!"
			item.fechaInicioCita = "Num. Cita: " + item.numeroCita;
			item.horaCita = "Num. HC: " + item.numeroHC;
			item.duracionCita = "";
			item.descripcionPrestacion = "INTENTE RECARGAR";
		}
		item.duracionCita = item.duracionCita == "" ? "" : item.duracionCita / 60000 + " minutos";
		if (item.atendida) {
			citasTable.row.add([
				item.idAgenda,
				item.fechaInicioCita,
				item.horaCita,
				item.duracionCita,
				item.apellido1Paciente,
				item.apellido2Paciente,
				item.nombrePaciente,
				item.descripcionPrestacion,
				"<div class='btn'><button type='button' class='ver'>Ver</button><button type='button' class='edit'>Editar</button></div>",
				item.atendida,
				item.numeroICU,
				item.numeroCita,
				item.numeroCIPA
			]).draw(false);
		}
		else {
			let disabled = "";
			/*let fecCitas = Date.parse($("#fec-citas").val());
			let today = new Date();
			if (fecCitas > today) {
				disabled = "disabled";
			}*/
			citasTable.row.add([
				item.idAgenda,
				item.fechaInicioCita,
				item.horaCita,
				item.duracionCita,
				item.apellido1Paciente,
				item.apellido2Paciente,
				item.nombrePaciente,
				item.descripcionPrestacion,
				"<div class='btn'><button type='button' class='nueva' " + disabled + ">Nueva</button></div>",
				item.atendida,
				item.numeroICU,
				item.numeroCita,
				item.numeroHC
			]).draw(false);			
		}
	}
	
	/** Delegated event click **/
	$('#table-citas tbody').off().on('click', 'button', function(e) {
		//let data = citasTable.row( $(this).parents('tr') ).data();
		var current_row = $(this).parents('tr');
		if (current_row.hasClass('child')) {
			current_row = current_row.prev();
		}
		var data = citasTable.row(current_row).data();

		
		if ($(this).attr("class") === "ver") {
			seleccionar({ atendida: data[9], numeroICU: data[10], numeroCita: data[11], numeroCIPA: data[12] });				
		}
		else if ($(this).attr("class") === "edit") {
			editar({ atendida: data[9], numeroICU: data[10], numeroCita: data[11], numeroCIPA: data[12] });				
		}
		else if ($(this).attr("class") === "nueva") {
			seleccionar({ atendida: data[9], numeroICU: data[10], numeroCita: data[11], numeroHC: data[12] });				
		}
	});		
}


function seleccionar(item) {
	let params = new Params(["idFacultativo", "cias"]);

	/** Si la cita esta atendida debe estar en la BD, buscar el idPaciente para mostrar sus datos. **/
	if (item.atendida) {
		let seleccion = $.get(url, {
			"peticion": RQ_OBTENER_PACIENTE_POR_CIPA,
			"numeroCIPA": item.numeroCIPA
		});
		
		$.when(seleccion).done(function (response) {			
			let idPaciente = response.Paciente.idPaciente;
			
        	let query = CryptoJS.AES.encrypt("idFacultativo=" + params.results[0] + "&idPaciente=" + idPaciente +
					"&numeroCita=" + item.numeroCita + "&modoConsulta=true" +
					"&cias=" + params.results[1] + 
					"&equipoCalle=false&numICU=" + item.numeroICU + "&internoSoloConsulta=false", "12349876aeiou");
        	
        	location.href = "registro.html?" + query;
        	
		}).fail(function (response) {
			console.log(response);
		});
	}
	/** Si no esta atendida, ir a la pagina de registro como nuevo paciente cargando sus datos del HPHIS **/
	else {
    	let query = CryptoJS.AES.encrypt("idFacultativo=" + params.results[0] + "&numeroHC=" + item.numeroHC +
				"&numeroCita=" + item.numeroCita + "&equipoCalle=false&numeroICU=" + item.numeroICU +
				"&cias=" + params.results[1] + "&from=citas" +
				"&internoSoloConsulta=false", "12349876aeiou");
    	
    	location.href = "registro.html?" + query;
	}
}


/** Buscar el idPaciente en la BD para poder editar sus actuaciones psiquiatricas **/
function editar(item) {
	let params = new Params(["idFacultativo", "cias"]);
	
	let edit = $.get(url, {
		"peticion": RQ_OBTENER_PACIENTE_POR_CIPA,
		"numeroCIPA": item.numeroCIPA
	});
	
	$.when(edit).done(function (response) {
		let idPaciente = response.Paciente.idPaciente;
    	
		let query = CryptoJS.AES.encrypt("idFacultativo=" + params.results[0] + "&idPaciente=" + idPaciente +
								"&numeroCita=" + item.numeroCita + "&modoConsulta=false" +
								"&cias=" + params.results[1] + 
								"&equipoCalle=false&numeroICU=" + item.numeroICU + "&internoSoloConsulta=false", "12349876aeiou");
		
    	location.href = "registro.html?" + query;
    	
		
	}).fail(function (response) {
		console.log(response);
	});
}


function irBusquedaPacientes() {
	let params = new Params([ "idFacultativo", "cias" ]);
	
	let query = CryptoJS.AES.encrypt("idFacultativo=" + params.results[0] +
								"&cias=" + params.results[1] + 
								"&modoConsulta=false&equipoCalle=false&internoSoloConsulta=false", "12349876aeiou");
	
	location.href = "search.html?" + query;
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
