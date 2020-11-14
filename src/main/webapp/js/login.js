/**
 * 
 */
const RQ_LOGIN_USUARIO              = 10;
const RQ_OBTENER_CODIGO_FACULTATIVO = 11;

//const url = "actuaciones";

function login() {
	let username = $("#username").val();
	let password = $("#password").val();
	
	let equipoCalle         = $("#equipo-calle").prop("checked");
	let internoSoloConsulta = false;
	
	setLoader();
	let ajaxLogin = $.post(url, {
		"peticion": RQ_LOGIN_USUARIO,
		"username": username,
		"password": password
	});
	
	$.when(ajaxLogin).done(function (response) {
		if (response.error == 0) {
			unsetLoader();
			showMessageBox(response.message);
		}
		else {
			$.ajaxSetup({ "cache": false });
			
			let ajaxCodigoFacultativo = $.get(url, {
				"peticion": RQ_OBTENER_CODIGO_FACULTATIVO,
				"userDni" : username
			});
			
			$.when(ajaxCodigoFacultativo).done(function (response) {
				unsetLoader();
				if (response.codigoFacultativo === "") {
					showMessageBox("<span style='font-weight:bold;'>ERROR:</span> USUARIO '" + username + "' NO PERMITIDO");
					return;
				}
			
				let modoConsulta = false;
				if (response.categoria.toUpperCase() === "ENFERMERO/A" ||
						response.categoria.toUpperCase() === "TRABAJADOR/A SOCIAL") {
					equipoCalle  = false;
					modoConsulta = true;
					internoSoloConsulta = true;
				}
				
				if (equipoCalle) {
					let query = CryptoJS.AES.encrypt("idFacultativo=" + response.codigoFacultativo +
							"&cias=" + response.cias +
							"&dni=" + username +
							"&modoConsulta=" + modoConsulta +
							"&equipoCalle=true", "12349876aeiou");
					
					location.href = "search.html?" + query;
				}
				else if (internoSoloConsulta === false) {
					let query = CryptoJS.AES.encrypt("idFacultativo=" + response.codigoFacultativo +
							"&idFacultaAlt=" + response.codigoFacultaAlt + "&cias=" + response.cias +
							"&dni=" + username +
							"&equipoCalle=false&internoSoloConsulta=false","12349876aeiou");
					
					location.href = "citas.html?" + query;
				}
				else {
					let query = CryptoJS.AES.encrypt("idFacultativo=" + response.codigoFacultativo +
							"&cias=" + response.cias +
							"&dni=" + username +
							"&modoConsulta=true&equipoCalle=false", "12349876aeiou");
					
					location.href = "search.html?" + query;
				}
			}).fail(function (response) {
				unsetLoader();
				console.log(response);
			});
		}
	}).fail(function (response) {
		unsetLoader();
		console.log(response);
		showMessageBox("Error de conexión al servicio de validación de usuarios\n" +
				response.responseText);
	});
}


function showMessageBox(message) {
	$("#msg-box-content").children("p").html(message);
	$("#msg-box-background").css("display",	"block");
}
function closeMessageBox() {
	$("#msg-box-background").css("display",	"none");
}
