var inputSelect = null;

function loadCalendar() {
    $(".calendar").on("click", function(e) {
        input = e.target;
        showCalendar(input);
    });
}

var meses = ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"];

var dias = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"];

var fecha = new Date();
var diaClick;
var mes = fecha.getMonth();
var mesClick;
var anio = fecha.getFullYear();
var anioClick;
var numDiaSem = fecha.getDay();

function previousYear() {
    $("#tablaMes").css("display", "none");
    anio--;
    showCalendar();
}

function nextYear() {
    $("#tablaMes").css("display", "none");
    anio++;
    showCalendar();
}

function previousMonth() {
    $("#tablaMes").css("display", "none");
    mes--;
    if (mes < 0) {
        mes = 11;
        anio--;
    }
    showCalendar();
}

function nextMonth() {
    $("#tablaMes").css("display", "none");
    mes++;
    if (mes > 11) {
        mes = 0;
        anio++;
    }
    showCalendar();
}

function selectMY() {
    $("#tablaMes").remove();
    var tablaMes = $("<table></table>");
    tablaMes.attr("id", "tablaMes");
    var headerMes = $("<thead></thead>");
    var filaHeaderMes = $("<tr></tr>");
    var tituloHeaderMes = $("<th></th>");
    tituloHeaderMes.attr("colspan", 4);
    
    var inputAnio = $("<input></input>");
    inputAnio.attr("id", "inputAnio");
    inputAnio.attr("type", "number");
    inputAnio.attr("max", 2200);
    inputAnio.attr("min", 1900);
    inputAnio.val(anio);
    
    var btnAnio = $("<button></button>");
    btnAnio.attr("id", "btnAnio");
    btnAnio.text("OK");
    btnAnio.on("click", function(e) {
        e.preventDefault();
        anio = inputAnio.val();
        showCalendar();
    });
    btnAnio.addClass("selectDia");
    
    tituloHeaderMes.append(inputAnio);
    tituloHeaderMes.append(btnAnio);
    
    filaHeaderMes.append(tituloHeaderMes);
    headerMes.append(filaHeaderMes);
    
    var mesesCuerpo = $("<tbody></tbody>");
    
    var fila;
    for (let i = 0; i < 12; i++) {
        if (i%4 === 0) {
            fila = $("<tr></tr>");
        }
        
        var celda = $("<td></td>");
        celda.text(meses[i]);
        celda.addClass("noClose");
        if (i === mes) {
            celda.addClass("selectDia");
        }
        let numMes = i;
        celda.on("click", function() {
            $("#tablaMes").css("display", "none");
            mes = numMes;
            anio = inputAnio.val();
            showCalendar();
        });
        
        fila.append(celda);
        
        if (i%4 === 0) {
            mesesCuerpo.append(fila);
        }
        
    }
    
    tablaMes.append(headerMes);
    tablaMes.append(mesesCuerpo);
    
    $("#cal").append(tablaMes);
    $("#tablaMes").css("display", "block");
}

function showCalendar(input) {
    $("#tablaMes").css("display", "none");

    //Fecha que se setea cuando el campo de texto está vacío
    var fecha = new Date().getDate() + "/" + new Date().getMonth() + "/" + new Date().getFullYear();

    //Mostramos el calendario
    if (input !== undefined) {
        input.parentNode.appendChild($("#cal")[0]);
        $("#cal").css("display", "block");
        $("#calendario").css("min-width", input.clientWidth < 275 ? 275 : input.clientWidth + "px");

        inputSelect = input;

        var camposFecha = input.value.split("/");

        if (camposFecha.length === 3) {
            fecha = camposFecha[0] + "/" + camposFecha[1] + "/" + camposFecha[2];

            //Extraemos la fecha del input
            camposFecha = fecha.split("/");
            mes = parseInt(camposFecha[1], 10)-1;
        } else {
            camposFecha = fecha.split("/");
            mes = parseInt(camposFecha[1], 10);
        }
    
        diaClick = parseInt(camposFecha[0], 10);
        anio = parseInt(camposFecha[2], 10);
        mesClick = mes;
        anioClick = anio;
    }

    $("#cal tbody").empty();

    $("#calMes").text(meses[mes]);
    $("#calAnio").text(anio);



    //PINTAMOS CALENDARIO
    var diasDelMes = new Date(anio, mes + 1, 0).getDate();
    var primerDiaMes = new Date(anio, mes, 1).getDay() - 1;


    var ultimoDiaMesAnterior = new Date(anio, mes, 0).getDate();

    if (primerDiaMes === -1) {
        primerDiaMes = 6;
    }

    var primerDiaSemana = ultimoDiaMesAnterior - primerDiaMes + 1;

    var contDia = 1;
    let i;

    //Primera semana
    var fila = $("<tr></tr>");
    for (i = 0; i < 7; i++) {
        var celdaDia = $("<td></td>");
        if (i >= primerDiaMes) {
            if(contDia === diaClick && mes == mesClick && anio == anioClick) {
                celdaDia.addClass("selectDia");
            }
            celdaDia.text(contDia);
            celdaDia.on("click", function(e) {
                selectDia(e.target.innerHTML, mes, anio);
            });
            contDia++;
        } else {
            celdaDia.text(primerDiaSemana);
            celdaDia.on("click", function(e) {
                selectDia(e.target.innerHTML, mes-1, anio);
            });
            celdaDia.addClass("diaGris");
            primerDiaSemana++;
        }
        fila.append(celdaDia);
    }
    $("#calendario tbody").append(fila);

    //Semanas intermedias
    var fila = null;
    for (contDia; contDia <= diasDelMes; contDia++) {
        if (i > 6) {
            if (fila !== null) {
               $("#calendario tbody").append(fila);
            }

            fila = $("<tr></tr>");
            i = 0;
        }
        var celdaDia = $("<td></td>");

        if(contDia === diaClick && mes == mesClick && anio == anioClick) {
            celdaDia.addClass("selectDia");
        }
        celdaDia.text(contDia);
        celdaDia.on("click", function(e) {
            selectDia(e.target.innerHTML, mes, anio);
        });
        fila.append(celdaDia);
        i++;
    }

    //Última semana 
    contDia = 1;
    for (i; i < 7; i++) {
        var celdaDia = $("<td></td>");
        celdaDia.text(contDia++);
        celdaDia.on("click", function(e) {
            selectDia(e.target.innerHTML, mes+1, anio);
        });
        celdaDia.addClass("diaGris");
        fila.append(celdaDia);
    }
    $("#calendario tbody").append(fila);
}

function selectDia(dia, mes, anio) {
    $("#tablaMes").css("display", "none");
    mes++;
    
    diaClick = dia;
    mesClick = mes;
    anioClick = anio;
    
    if (dia < 10) {
        dia = "0" + dia;
    }

    if (mes === 13) {
        mes = 1;
        anio++;
    } else if (mes === 0) {
        mes = 12;
        anio--;
    }

    if (mes < 10) {
        mes = "0" + mes;
    }

    inputSelect.value = dia + "/" + mes + "/" + anio;
    var input = jQuery(inputSelect)[0];
    $(input).change();
    $(input).siblings(".clean").css("display", "block");
    $(input).siblings(".clean").on("click", function () {
        inputSelect.value = "";
        $(input).siblings(".clean").css("display", "none");
    });
}

$(document).on("click", function(ev) {
    if(ev.target !== inputSelect) {
        
        if(ev.target.classList[0] !== "noClose" && ev.target.classList[0] !== "paginate_button") {
            if (ev.target.tagName.toLowerCase() !== "th") {

                var parent = ev.target.parentNode.tagName;
                if (parent === undefined || parent.toLowerCase() !== "th") {
                    $("#tablaMes").css("display", "none");
                    $("#cal").css("display", "none");
                }
            }
        }
    }
});