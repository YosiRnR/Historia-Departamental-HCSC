/**
 ** 
 **/
const RQ_BUSCAR_PACIENTE_POR_PATRON = 12;
const RQ_CERRAR_SESION              = 27;

var idFacultativo = "";


$(document).ready(function () {
	
	/** Comprobar si existe el parametro idFacultativo para solo permitir acceder a usuariso que hayan pasado el login **/
	{
		let params = new Params([ "idFacultativo" ]);
		if (params.results[0] === "" || params.results[0] === undefined) {
			location.href = "login.html";
		}
		idFacultativo = params.results[0];
	}
	/** Fin de la comprobacion para permitir usuarios que pasen por el login **/

	$("#cb-modo-consulta").click(function () {
		if ($(this).is(":checked")) {
			$("#btn-nuevo").attr("disabled", true);
		}
		else {
			$("#btn-nuevo").attr("disabled", false);
		}
	});
	
	$("#table-pacientes").dataTable({
		"language": { "url": "js/spanish.lang.json" },
		"aoColumnDefs": [
			{ "bVisible": false, "aTargets": [5] },
			{ "bVisible": false, "aTargets": [0] },
			{ "bVisible": true, "aTargets": [1] },
			{ "bVisible": true, "targets": [2], "className": "table-text-align-right" },
			{ "bVisible": true, "targets": [3], "className": "table-text-align-right" },
			{
				"bVisible": true, "aTargets": [4],
//				"defaultContent": "<div class='btn'><button>Seleccionar</button></div>",
				"targets": -1,
				"data": null
			},
			{ "bDeferRender": true }
		]
	});
	
	let params = new Params([ "idFacultativo", "password", "modoConsulta" ]);
	
	if (params.results[2] === "true") {
		$("#cb-modo-consulta").prop("checked", true);
		$("#cb-modo-consulta").prop("disabled", true);
		$("#btn-nuevo").attr("disabled", true);		
	}
	
	/** Clear form inputs **/
    $("#p-ape").val("");
    $("#s-ape").val("");
    $("#nombre").val("");
    $("#num-doc").val("");
    $("#fec-nac").val("");        
    
    /** Wait for events **/
    loadCalendar();    
});


function nuevo() {
	
}


function buscar() {
	let tipoDoc = "";
	let dni = "";
	let pasaporte = "";
	let nie = "";
	
	switch($("#sel-doc").children("option:selected").val()) {
	case "1":
		tipoDoc = "1";
		dni = $("#num-doc").val();
		break;
	case "2":
		tipoDoc = "2";
		pasaporte = $("#num-doc").val();
		break;
	case "3":
		tipoDoc = "3";
		nie = $("#num-doc").val();
		break;
	}
	
	$.ajaxSetup({ "cache": false });
	let buscarPacientes = $.get(url, {
		"peticion": RQ_BUSCAR_PACIENTE_POR_PATRON,
		"codUser" : idFacultativo,
		"modoConsulta": $("#cb-modo-consulta").is(":checked"),
		"apellido1": $("#p-ape").val(),
		"apellido2": $("#s-ape").val(),
		"nombre": $("#nombre").val(),
		"fechaNac": $("#fec-nac").val(),
		"tipoDoc": tipoDoc,
		"dni": dni,
		"pasaporte": pasaporte,
		"nie": nie,
		"numeroHC": $("#num-hc").val(),
		"numeroCIPA": $("#num-cipa").val(),
		"numeroTS": $("#num-ts").val()
	});
	
	setLoader();
	$.when(buscarPacientes).done(function (response) {
		unsetLoader();
		crearTablaResultados(response.Pacientes);
	}).fail(function (response) {
		console.log(response);
		unsetLoader();
		if (response.state === "SESSION CLOSED") {
			location.href = "login.html";
		}
	});
}


function crearTablaResultados(data) {
	let searchTable = $("#table-pacientes").DataTable();
	
	searchTable.clear().draw();
	
	let resultsLength = data.length;
	if (resultsLength > 200) resultsLength = 200;
	
	for (let cont = 0; cont < resultsLength; cont++) {
		let item = data[cont];

		let doc = "";
		if (item.dni != null && item.dni !== "") {
			doc = item.dni;
		}
		else if (item.pasaporte != null && item.pasaporte !== "") {
			doc = item.pasaporte;
		}
		else if (item.nie != null && item.nie !== "") {
			doc = item.nie;
		}
		
		searchTable.row.add([
			item.idPaciente,
			(item.nombre + " " + item.apellido1 + " " + item.apellido2).toUpperCase(),
			item.fechaNac,
			doc,
			"<div class='btn' id='btn-" + cont + "'><button type='button'>Seleccionar</button></div>",
			item.numeroHistoriaClinica
		]).draw(true);
		
		$('#table-pacientes tbody').off().on('click', 'button', function (evt) {
			var current_row = $(this).parents('tr');
			if (current_row.hasClass('child')) {
				current_row = current_row.prev();
			}
			var data = searchTable.row(current_row).data();
			if (data[0] !== 0 && data[0] !== -1) {
				seleccionarPorIdPaciente(data[0]);
			}
			else {
				seleccionarPorNumeroHC(data[5]);
			}
			//let data = searchTable.row( "row-" + $(this).attr('id') ).data();
			/*let data = searchTable.row( $(this).parents('tr') ).data();
			if (data[0] !== 0) {
				seleccionarPorIdPaciente(data[0]);
			}
			else {
				seleccionarPorNumeroHC(data[5]);
			}*/
	    });
	}
	
    $(window).trigger('resize');
}


function clickSeleccionar(obj, event) {
	console.log("PACIENTE SELECCIONADO");
	
	let searchTable = $("#table-pacientes").DataTable();
	
	let data = searchTable.row( $(this).parents('tr') ).data();
	if (data[0] !== 0) {
		seleccionarPorIdPaciente(data[0]);
	}
	else {
		seleccionarPorNumeroHC(data[5]);
	}	
}


function seleccionarPorIdPaciente(idPaciente) {
	var params = new Params([ "idFacultativo", "equipoCalle", "internoSoloConsulta", "cias" ]);
	
    let query = CryptoJS.AES.encrypt("idPaciente=" + idPaciente + "&idFacultativo=" + params.results[0] +
    			"&modoConsulta=" + $("#cb-modo-consulta").is(":checked") +
    			"&equipoCalle=" + params.results[1] + "&internoSoloConsulta=" + params.results[2] + "&cias=" + params.results[3], "12349876aeiou");
    
	location.href = "registro.html?" + query;
}


function seleccionarPorNumeroHC(numHC) {
	let params = new Params([ "idFacultativo", "equipoCalle", "internoSoloConsulta", "cias" ]);
	
    let query = CryptoJS.AES.encrypt("idPaciente=0&idFacultativo=" + params.results[0] + "&numeroHC=" + numHC +
    			"&modoConsulta=" + $("#cb-modo-consulta").is(":checked") + "&equipoCalle=" + params.results[1] +
    			"&internoSoloConsulta=" + params.results[2] + "&cias=" + params.results[3], "12349876aeiou");
    
	location.href = "registro.html?" + query;
}


function nuevo() {
	var params = new Params([ "idFacultativo", "equipoCalle", "cias" ]);

    let query = CryptoJS.AES.encrypt("idFacultativo=" + params.results[0] + "&equipoCalle=" + params.results[1] + "&cias=" + params.results[2], "12349876aeiou");
	location.href = "registro.html?" + query; 
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
