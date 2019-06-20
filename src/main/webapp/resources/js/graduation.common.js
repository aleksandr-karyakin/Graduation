let context, form;

const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function (e, xhr, options) {
    xhr.setRequestHeader(header, token);
});

function makeEditable(ctx) {
    context = ctx;

    context.datatableApi = $("#datatable").DataTable(
        $.extend(true, ctx.datatableOpts,
            {
                "ajax": {
                    "url": context.ajaxUrl,
                    "dataSrc": ""
                },
                "language": {
                    "search": i18n["common.search"]
                }
            }
        ));

    form = $('#detailsForm');

    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failNoty(jqXHR);
    });

    $.ajaxSetup({cache: false});
}


function add() {
    $("#modalTitle").html(i18n["addTitle"]);
    form.find(":input").val("");
    $("#editRow").modal();
}

function updateRow(id) {
    $("#modalTitle").html(i18n["editTitle"]);
    $.get(context.ajaxUrl + id, function (data) {
        $.each(data, function (key, value) {
            form.find("input[name='" + key + "']").val(value);
        });
        $('#editRow').modal();
    });
}

function save() {
    $.ajax({
        type: "POST",
        url: context.ajaxUrl,
        data: form.serialize()
    }).done(function () {
        $("#editRow").modal("hide");
        context.updateTable();
        successNoty("common.saved", "");
    });
}

function deleteRow(id) {
    if (confirm(i18n['common.confirm'])) {
        $.ajax({
            url: context.ajaxUrl + id,
            type: "DELETE"
        }).done(function () {
            context.updateTable();
            successNoty("common.deleted", "");
        });
    }
}

function updateTableByData(data) {
    context.datatableApi.clear().rows.add(data).draw();
}


let failedNote;

function closeNoty() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
}

function successNoty(key, msg) {
    closeNoty();
    new Noty({
        text: "<span class='fa fa-lg fa-check'></span> &nbsp;" + i18n[key] + " " + msg,
        type: 'success',
        layout: "bottomRight",
        timeout: 1000
    }).show();
}

function failNoty(jqXHR) {
    closeNoty();
    const errorInfo = JSON.parse(jqXHR.responseText);
    failedNote = new Noty({
        text: "<span class='fa fa-lg fa-exclamation-circle'></span> &nbsp;" + errorInfo.typeMessage + "<br>" + errorInfo.details.join("<br>"),
        type: "error",
        layout: "bottomRight"
    }).show();
}


function renderEditBtn(data, type, row) {
    if (type === "display") {
        return "<a onclick='updateRow(" + row.id + ");'><span class='fa fa-pencil'></span></a>";
    }
}

function renderDeleteBtn(data, type, row) {
    if (type === "display") {
        return "<a onclick='deleteRow(" + row.id + ");'><span class='fa fa-remove'></span></a>";
    }
}

function renderChangeRolesBtn(data, type, row) {
    if (type === "display") {
        return "<button class='btn-lg btn-primary' onclick='changeRoles(" + row.id + ")'/>";
    }
}

function vote(chkbox, id, url) {
    const enabled = chkbox.is(":checked");
    $.ajax({
        url: url + id + "/votes",
        type: "POST"
    }).done(function (data) {
        context.updateTable();
        successNoty("common.voting", data);
    }).fail(function () {
        $(chkbox).prop("checked", !enabled);
    });
}

function changeRoles(id) {
    $.ajax({
        url: context.ajaxUrl + id + "/changeRoles",
        type: "POST",
    }).done(function () {
        context.updateTable();
        successNoty("common.saved", "");
    });
}

function enable(chkbox, id, url) {
    const enabled = chkbox.is(":checked");
    $.ajax({
        url: url + id,
        type: "POST",
        data: "enabled=" + enabled
    }).done(function () {
        chkbox.closest("tr").attr("data-Enabled", enabled);
        successNoty(enabled ? "common.enabled" : "common.disabled", "");
    }).fail(function () {
        $(chkbox).prop("checked", !enabled);
    });
}

function getSelectedRestaurant() {
    $.ajax({
        url: restaurantsAjaxUrl + "selected",
        type: "POST"
    }).done(function (data) {
        selectedRestaurantId = data;
        $.get(restaurantsAjaxUrl, updateTableByData);
    });
}