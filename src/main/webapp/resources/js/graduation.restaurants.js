const restaurantsAjaxUrl = "ajax/restaurants/";

let selectedRestaurantId = 0;

$(function () {
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
                        "render": function (data, type, row) {
                            if (type === "display") {
                                return "<input type='checkbox' " + (row.id === selectedRestaurantId ? "checked" : "") + " onclick='vote($(this)," + row.id + ", restaurantsAjaxUrl);'/>";
                            }
                            return data;
                        }
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
                getSelectedRestaurant();
            }
        }
    );
    getSelectedRestaurant();
});
