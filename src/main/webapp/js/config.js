/**
 * 
 */
const url = "actuaciones";
var urlInputs = "inputFields";


var Params = (function (names) {
	var results = new Array();
	
	for (let i = 0; i < names.length; i++) {
		
		let name = names[i].replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
		
		var res;
	    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)");
	    let code = 
	    res = regex.exec("?" + CryptoJS.AES.decrypt(location.search.slice(1), "12349876aeiou").toString(CryptoJS.enc.Utf8));
	    results.push(res === null ? "" : decodeURIComponent(res[1].replace(/\+/g, " ")));
	}
    
	
	Object.defineProperty(this, "results", {
		get: function() {
			return results;
		}
	});	
});

Params.prototype = {};
Params.prototype.constructor = Params;


function setLoader() {
	$("#fondo-loader").css({"display":"block"});
	$("body").css({"overflow":"hidden"});				
}

function unsetLoader() {
	$("#fondo-loader").css({"display":"none"});
	$("body").css({"overflow":"scroll"});	
}
