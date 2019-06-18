const managerRestaurantsAjaxUrl = "ajax/manager/restaurants/";

$(function () {
    makeEditable({
            ajaxUrl: managerRestaurantsAjaxUrl,
            datatableOpts: {
                "columns": [
                    {
                        "data": "name",
                        "render": function (data, type, row) {
                            if (type === "display") {
                                return "<a href='manager/restaurants/" + row.id + "/dishes'>" + data + "</a>";
                            }
                            return data;
                        }
                    },
                    {
                        "orderable": false,
                        "defaultContent": "",
                        "render": renderEditBtn
                    },
                    {
                        "orderable": false,
                        "defaultContent": "",
                        "render": renderDeleteBtn
                    }
                ],
                "order": [
                    [
                        0,
                        "asc"
                    ]
                ]
            },
            updateTable: function () {
                $.get(managerRestaurantsAjaxUrl, updateTableByData);
            }
        }
    );
});