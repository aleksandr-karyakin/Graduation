let path = window.location.toString();
let restaurantIndex = path
    .slice(0, path.indexOf("/dishes"))
    .slice(path.lastIndexOf("s/") + 2);

const managerDishesAjaxUrl = "ajax/manager/restaurants/" + restaurantIndex + "/dishes/";

$(function () {
    makeEditable({
            ajaxUrl: managerDishesAjaxUrl,
            datatableOpts: {
                "columns": [
                    {
                        "data": "name"
                    },
                    {
                        "data": "price"
                    },
                    {
                        "data": "enabled",
                        "render": function (data, type, row) {
                            if (type === "display") {
                                return "<input type='checkbox' " + (data ? "checked" : "") + " onclick='enable($(this)," + row.id + ", managerDishesAjaxUrl);'/>";
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
                ],
                "createdRow": function (row, data, dataIndex) {
                    if (!data.enabled) {
                        $(row).attr("data-Enabled", false);
                    }
                }
            },
            updateTable: function () {
                $.get(managerDishesAjaxUrl, updateTableByData);
            }
        }
    );
});