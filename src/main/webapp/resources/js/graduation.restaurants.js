const restaurantsAjaxUrl = "ajax/restaurants/";

let selectedRestaurantId = 0;

$(function () {
    getSelectedRestaurant();
    makeEditable({
            ajaxUrl: restaurantsAjaxUrl,
            datatableOpts: {
                "columns": [
                    {
                        "data": "name"
                    },
                    {
                        "data": "dishes",
                        "orderable": false,
                        "render": function (data, type, row) {
                            let result = '';
                            $.each(row.dishes, function (index, value) {
                                if (value.enabled === true) {
                                    result = result + value.name + '<br/>';
                                }
                            });
                            return result;
                        }
                    },
                    {
                        "data": "dishes",
                        "orderable": false,
                        "render": function (data, type, row) {
                            let result = '';
                            $.each(row.dishes, function (index, value) {
                                if (value.enabled === true) {
                                    result = result + value.price + '<br/>';
                                }
                            });
                            return result;
                        }
                    },
                    {
                        "orderable": false,
                        "defaultContent": "",
                        "render": renderVoteBtn
                    }
                ],
                "order": [
                    [
                        0,
                        "asc"
                    ]
                ],
                "rowCallback": function (row, data, displayNum, displayIndex, dataIndex) {
                    if (data.id === selectedRestaurantId) {
                        $(row).css("background-color", "lightgreen");
                    }
                },
                "createdRow": function (row, data, dataIndex) {
                    if (data.id === selectedRestaurantId) {
                        $(row).css("background-color", "lightgreen");
                    }
                }
            },
            updateTable: function () {
                getSelectedRestaurant();
                $.get(restaurantsAjaxUrl, updateTableByData);
            }
        }
    );
});
