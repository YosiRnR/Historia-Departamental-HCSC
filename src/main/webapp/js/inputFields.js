/**
 * 
 */
const NUMBER = 'NUMBER';
const SEARCH = 'SEARCH';
const SELECT = 'SELECT';
const CALENDAR = 'CALENDAR';


//< Cargar dinamicamente los inputs de la vista de actuaciones >
function loadInputFields() {
    $.ajax({
        data: {},
        type: "get",
        url: urlInputs,
        cache: false,
        success: function(response) {
            buildHtml(response.Inputs);
            continueLoad();
            loadCalendar();
        },
        error: function() {
            continueLoad();
            loadCalendar();
        }
    });
}


function buildHtml(inputs) {
    
    //Diagnosticos principales
    var diagp = inputs.filter(function(e) {
        return e.Seccion === 'DIAGP';
    });
    
    let cont = 1;
    diagp.forEach(function(e) {
        let fieldset = $("#diag-psi-prin fieldset");
        if (e.CheckField || e.Manual || e.TipoCampo === NUMBER) {
            if (!fieldset.hasClass("tri-col")) {
                fieldset.addClass("tri-col");
            }
        }
        for (let i = 1; i <= e.Totales; i++) {
            let id = "diag-p-" + cont++;
            switch (e.TipoCampo) {
                case SEARCH: 
                    fieldset.append(addSearch(e, i, id, PSIQ));
                    break;
                case NUMBER: 
                    fieldset.append(addNumber(e, i, id));
                    break;
                case SELECT: 
                    fieldset.append(addSelect(e, i, id));
                    break;
                case CALENDAR: 
                    fieldset.append(addCalendar(e, i, id));
                    break;
            }
        }
    });
    
    //Diagnosticos secundarios
    var diags = inputs.filter(function(e) {
        return e.Seccion === 'DIAGS';
    });
    
    cont = 1;
    diags.forEach(function(e) {
        let fieldset = $("#diag-psi-sec fieldset");
        if (e.CheckField || e.Manual || e.TipoCampo === NUMBER) {
            if (!fieldset.hasClass("tri-col")) {
                fieldset.addClass("tri-col");
            }
        }
        for (let i = 1; i <= e.Totales; i++) {
            let id = "diag-s-" + cont++;
            switch (e.TipoCampo) {
                case SEARCH: 
                    fieldset.append(addSearch(e, i, id, PSIQSEC));
                    break;
                case NUMBER: 
                    fieldset.append(addNumber(e, i, id));
                    break;
                case SELECT: 
                    fieldset.append(addSelect(e, i, id));
                    break;
                case CALENDAR: 
                    fieldset.append(addCalendar(e, i, id));
                    break;
            }
        }
    });
    
    //Diagnosticos no psiquiatricos
    var diagn = inputs.filter(function(e) {
        return e.Seccion === 'DIAGN';
    });
    
    cont = 1;
    diagn.forEach(function(e) {
        let fieldset = $("#diag-no-psi fieldset");
        if (e.CheckField || e.Manual || e.TipoCampo === NUMBER) {
            if (!fieldset.hasClass("tri-col")) {
                fieldset.addClass("tri-col");
            }
        }
        for (let i = 1; i <= e.Totales; i++) {
            let id = "diag-n-" + cont++;
            switch (e.TipoCampo) {
                case SEARCH: 
                    fieldset.append(addSearch(e, i, id, NPSIQ));
                    break;
                case NUMBER: 
                    fieldset.append(addNumber(e, i, id));
                    break;
                case SELECT: 
                    fieldset.append(addSelect(e, i, id));
                    break;
                case CALENDAR: 
                    fieldset.append(addCalendar(e, i, id));
                    break;
            }
        }
    });
    
    //Estadiaje
    var esta = inputs.filter(function(e) {
        return e.Seccion === 'ESTA';
    });
    
    cont = 1;
    esta.forEach(function(e) {
        let fieldset = $("#form-estadiaje fieldset");
        if (e.CheckField || e.Manual || e.TipoCampo === NUMBER) {
            if (!fieldset.hasClass("tri-col")) {
                fieldset.addClass("tri-col");
            }
        }
        for (let i = 1; i <= e.Totales; i++) {
            let id = "estadiaje-" + cont++;
            switch (e.TipoCampo) {
                case SEARCH: 
                    fieldset.append(addSearch(e, i, id, NPSIQ));
                    break;
                case NUMBER: 
                    fieldset.append(addNumber(e, i, id));
                    break;
                case SELECT: 
                    fieldset.append(addSelect(e, i, id));
                    break;
                case CALENDAR: 
                    fieldset.append(addCalendar(e, i, id));
                    break;
            }
        }
    });
    
    //Situación clínica
    var sitclin = inputs.filter(function(e) {
        return e.Seccion === 'SCF';
    });
    
    cont = 1;
    sitclin.forEach(function(e) {
        let fieldset = $("#sit-clin fieldset");
        if (e.CheckField || e.Manual || e.TipoCampo === NUMBER) {
            if (!fieldset.hasClass("tri-col")) {
                fieldset.addClass("tri-col");
            }
        }
        for (let i = 1; i <= e.Totales; i++) {
            let id = "sit-clin-" + cont++;
            switch (e.TipoCampo) {
                case SEARCH: 
                    fieldset.append(addSearch(e, i, id, NPSIQ));
                    break;
                case NUMBER: 
                    fieldset.append(addNumber(e, i, id));
                    break;
                case SELECT: 
                    fieldset.append(addSelect(e, i, id));
                    break;
                case CALENDAR: 
                    fieldset.append(addCalendar(e, i, id));
                    break;
            }
        }
    });
    
    //Discapacidad das
    var discdas = inputs.filter(function(e) {
        return e.Seccion === 'DAS';
    });
    
    cont = 1;
    discdas.forEach(function(e) {
        let fieldset = $("#discapacidad-das fieldset");
        if (e.CheckField || e.Manual || e.TipoCampo === NUMBER) {
            if (!fieldset.hasClass("tri-col")) {
                fieldset.addClass("tri-col");
            }
        }
        for (let i = 1; i <= e.Totales; i++) {
            let id = "discapacidad-das-" + cont++;
            switch (e.TipoCampo) {
                case SEARCH: 
                    fieldset.append(addSearch(e, i, id, NPSIQ));
                    break;
                case NUMBER: 
                    fieldset.append(addNumber(e, i, id));
                    break;
                case SELECT: 
                    fieldset.append(addSelect(e, i, id));
                    break;
                case CALENDAR: 
                    fieldset.append(addCalendar(e, i, id));
                    break;
            }
        }
    });
    
    //Tratamientos psicologicos
    var tratpsi = inputs.filter(function(e) {
        return e.Seccion === 'TRAT';
    });
    
    cont = 1;
    tratpsi.forEach(function(e) {
        let fieldset = $("#tratamientos-psi");
        if (e.CheckField || e.Manual || e.TipoCampo === NUMBER) {
            if (!fieldset.hasClass("tri-col")) {
                fieldset.addClass("tri-col");
            }
        }
        for (let i = 1; i <= e.Totales; i++) {
            let id = "tratamientos-psi-" + cont++;
            switch (e.TipoCampo) {
                case SEARCH: 
                    fieldset.append(addSearch(e, i, id, NPSIQ));
                    break;
                case NUMBER: 
                    fieldset.append(addNumber(e, i, id));
                    break;
                case SELECT: 
                    fieldset.append(addSelect(e, i, id));
                    fieldset.append(addCalendar({ Label: "Fecha Inicio", CheckField: false, Manual: false }, i, "fec-inicio-" + id));
                    $("#fec-inicio-" + id + " > input").prop("disabled", "true");
                    fieldset.append(addCalendar({ Label: "Fecha Finalización", CheckField: false, Manual: false }, i, "fec-fin-" + id));
                    $("#fec-fin-" + id + " > input").prop("disabled", "true");
                    
                    if (cont <= tratpsi.length) {
                        fieldset.append($("<hr>"));
                    }
                    
                    break;
                case CALENDAR: 
                    fieldset.append(addCalendar(e, i, id));
                    break;
            }
        }
    });
    
    //Programas Unidades Proceso
    var progunid = inputs.filter(function(e) {
        return e.Seccion === 'PUEP';
    });
    
    cont = 1;
    progunid.forEach(function(e) {
        let fieldset = $("#prog-unid fieldset:nth-child(1)");
        if (e.CheckField || e.Manual || e.TipoCampo === NUMBER) {
            if (!fieldset.hasClass("tri-col")) {
                fieldset.addClass("tri-col");
            }
        }
        for (let i = 1; i <= e.Totales; i++) {
            let id = "prog-unid-" + cont++;
            switch (e.TipoCampo) {
                case SEARCH: 
                    fieldset.append(addSearch(e, i, id, NPSIQ));
                    break;
                case NUMBER: 
                    fieldset.append(addNumber(e, i, id));
                    break;
                case SELECT: 
                    fieldset.append(addSelect(e, i, id));
                    break;
                case CALENDAR: 
                    fieldset.append(addCalendar(e, i, id));
                    break;
            }
        }
    });
}


function addSearch(e, cont, id, tipoBusqueda) {
    let label = $("<label></label>");
    let labelText = e.Label;
    if (e.Totales > 1) {
        labelText += " " + cont;
    }
    labelText += ":"
    label.text(labelText);

    if (e.CheckField) {
        label.attr("for", "l-" + id);
    } else {
        label.attr("for", "b-" + id);
    }

    let divGlobal = $("<div></div>");
    let divParent = $("<div></div>");

    let div = $("<div></div>");
    div.addClass("desplegable");
    div.attr("id", id);

    let lupa = $("<label></label>");
    lupa.text("🔍");
    lupa.addClass("lupa");
    lupa.attr("for", "b-" + id);

    let clean = $("<label></label>");
    clean.text("❌");
    clean.addClass("clean");

    let input = $("<input></input>");
    input.attr("type", "text");
    input.attr("name", id);
    input.on("click", function(event) {
        buscarDiagnosticoPorPatron(event, tipoBusqueda);
    });
    input.on("keyup", function(event) {
        buscarDiagnosticoPorPatron(event, tipoBusqueda);
    });
    input.attr("id", "b-" + id);

    let ul = $("<ul></ul>");
    ul.attr("id", "ul-" + id);

    div.append(lupa);
    div.append(input);
    div.append(clean);
    div.append(ul);
    
    if (e.CheckField) {
        let check = $("<input></input>");
        check.attr("type", "checkbox");
        check.attr("id", "l-" + id);
        divParent.append(check);
    }
    divParent.append(label);
    divParent.append(div);
    divParent.attr("id", id);
    
    if(e.Manual) {
        let span = $("<span></span>");
        span.addClass("enlace");
        
        let link = $("<a></a>");
        link.attr("href", e.UrlManual);
        link.attr("target", "_blank");
        link.text("Descargar manual de uso");
        
        span.append(link);
        divParent.append(span);
        
    }

    divGlobal.append(divParent);
    
    return divGlobal;
}


function addNumber(e, cont, id) {
    let label = $("<label></label>");
    let labelText = e.Label;
    if (e.Totales > 1) {
        labelText += " " + cont;
    }
    labelText += ":"
    label.text(labelText);
    if (e.CheckField) {
        label.attr("for", "l-" + id);
    } else {
        label.attr("for", "n-" + id);
    }

    let divGlobal = $("<div></div>");
    let divParent = $("<div></div>");

    let input = $("<input></input>");
    input.attr("type", "number");
    input.attr("min", e.Min);
    input.attr("max", e.Max);
    input.attr("id", "n-" + id);
    input.attr("name", "n-" + id);
    
    let span = $("<span></span>");
    span.text("Numérica del " + e.Min + " al " + e.Max);
    
    if (e.CheckField) {
        let check = $("<input></input>");
        check.attr("type", "checkbox");
        check.attr("id", "l-" + id);
        divParent.append(check);
    }
    divParent.append(label);
    divParent.append(input);
    divParent.append(span);
    divParent.attr("id", id);
    
    if(e.Manual) {
        let span = $("<span></span>");
        span.addClass("enlace");
        
        let link = $("<a></a>");
        link.attr("href", e.UrlManual);
        link.attr("target", "_blank");
        link.text("Descargar manual de uso");
        
        span.append(link);
        divParent.append(span);
        
    } else {
        divParent.append($("<span></span>"));
    }

    divGlobal.append(divParent);
    
    return divGlobal;
}


function addSelect(e, cont, id) {
    let label = $("<label></label>");
    let labelText = e.Label;
    if (e.Totales > 1) {
        labelText += " " + cont;
    }
    labelText += ":"
    label.text(labelText);
    if (e.CheckField) {
        label.attr("for", "l-" + id);
    } else {
        label.attr("for", "s-" + id);
    }

    let divGlobal = $("<div></div>");
    let divParent = $("<div></div>");

    let select = $("<select></select>");
    select.attr("id", "s-" + id);
    select.attr("name", "s-" + id);
    
    let opciones = e.Opciones.split(";");
    let opcion = $("<option></option>");
    opcion.val("0");
    opcion.text("-- " + e.Label);
    opcion.attr("selected", "true");
    
    if (e.CheckField) {
        opcion.attr("disabled", "true");
    }
    
    select.append(opcion);
    for (let i = 1; i <= opciones.length; i++) {
        opcion = $("<option></option>");
        opcion.val(i);
        opcion.text(opciones[i-1]);
        select.append(opcion);
    }
    
    if (e.CheckField) {
        let check = $("<input></input>");
        check.attr("type", "checkbox");
        check.attr("id", "l-" + id);
        divParent.append(check);
    }
    divParent.append(label);
    divParent.append(select);
    divParent.attr("id", id);
    
    if(e.Manual) {
        let span = $("<span></span>");
        span.addClass("enlace");
        
        let link = $("<a></a>");
        link.attr("href", e.UrlManual);
        link.attr("target", "_blank");
        link.text("Descargar manual de uso");
        
        span.append(link);
        divParent.append(span);
        
    }

    divGlobal.append(divParent);
    
    return divGlobal;
}


function addCalendar(e, cont, id) {
    
    let label = $("<label></label>");
    let labelText = e.Label;
    if (e.Totales > 1) {
        labelText += " " + cont;
    }
    labelText += ":"
    label.text(labelText);
    if (e.CheckField) {
        label.attr("for", "l-" + id);
    } else {
        label.attr("for", "c-" + id);
    }

    let divGlobal = $("<div></div>");
    let divParent = $("<div></div>");

    let divCalendar = $("<div></div>");
    divCalendar.addClass("calDiv");
    
    let iconCal = $("<label></label>");
    iconCal.text("📅");
    iconCal.addClass("calIcon");
    iconCal.attr("for", "c-" + id);
    
    let iconClean = $("<label></label>");
    iconClean.text("❌");
    iconClean.addClass("clean");
    
    let inputCalendar = $("<input></input>");
    inputCalendar.attr("type", "text");
    inputCalendar.addClass("calendar");
    inputCalendar.attr("id", "c-" + id);
    inputCalendar.attr("name", "c-" + id);
    inputCalendar.attr("readonly", true);
    
    divCalendar.append(iconCal);
    divCalendar.append(inputCalendar);
    divCalendar.append(iconClean);
    divCalendar.attr("id", id);
    
    
    if (e.CheckField) {
        let check = $("<input></input>");
        check.attr("type", "checkbox");
        check.attr("id", "l-" + id);
        divParent.append(check);
    }
    
    divParent.append(label);
    divParent.append(divCalendar);
    divParent.attr("id", id);
    
    if(e.Manual) {
        let span = $("<span></span>");
        span.addClass("enlace");
        
        let link = $("<a></a>");
        link.attr("href", e.UrlManual);
        link.attr("target", "_blank");
        link.text("Descargar manual de uso");
        
        span.append(link);
        divParent.append(span);
        
    }

    divGlobal.append(divParent);
    
    return divGlobal;
}
