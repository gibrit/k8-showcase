// jSaltuk Live Api Plugin

; (function ($) {
    $.jSaltuk = function (el, options) {

        var defaults = {
            url: 'http://localhost/api/users',
            filters: {},
            live: null,
            onError: function (e) { }
        }

        var plugin = this;

        plugin.settings = {}

        var init = function () {

            plugin.settings = $.extend({}, defaults, options);
            plugin.table = $(el).find('tbody');
            $(el).find('#run-filter').click(e => {
                let offset = parseInt($('[name="pg_offset"]').val());
                let limit = parseInt($('[name="pg_limit"]').val());
                offset = offset < 0 ? 0 : offset;
                limit = limit <= 0 ? 20 : limit;
                plugin.settings.filters["pg_offset"] = offset;
                plugin.settings.filters["pg_limit"] = limit;
                list();

            });
            const createBtn = $(el).find('#create-user-modal .btn-save')
            createBtn.click((e) => {
                const name = $('#create-user-modal [name="name"]').val();
                const password = $('#create-user-modal [name="password"]').val();
                const email = $('#create-user-modal [name="email"]').val();
                const username = $('#create-user-modal [name="username"]').val();
                const age = parseInt($('#create-user-modal [name="age"]').val());
                create({ "name": name, "password": password, "username": username, "email": email, "age": age });
            });

            const updateBtn = $(el).find('#update-user-modal .btn-save');
            updateBtn.click((e) => {
                const id = $(e.target).data('id');
                const name = $('#update-user-modal [name="name"]').val();
                const password = $('#update-user-modal [name="password"]').val();
                const email = $('#update-user-modal [name="email"]').val();
                const username = $('#update-user-modal [name="username"]').val();
                const age = parseInt($('#update-user-modal [name="age"]').val());
                update(id, { "name": name, "password": password, "username": username, "email": email, "age": age });
            });
            $(el).find('#reset-filter').click(e => {
                plugin.settings.filters = {};
                $('[name="pg_offset"]').val('0');
                $('[name="pg_limit"]').val('20');
                plugin.settings.filters["pg_offset"] = 0;
                plugin.settings.filters["pg_limit"] = 20;
                updateFilterList();
                list();
            });
            plugin.filterList = $(el).find("#filter-list");
            $(el).find('.add-filter').click(e => {
                const filterName = $(e.target).attr('data-filter-type');
                var val = $('#' + filterName + '-filter [name="value"]').val();
                if (val) {
                    var filter = $('#' + filterName + '-filter [name="filter"]').val();
                    plugin.settings.filters[filterName + filter] = val;
                    updateFilterList();

                }
            });
            plugin.el = el;
            // code goes here
        }
        plugin.run = () => {
            list();
        }
        plugin.connect = (callback) => {
            var options = {
                vertxbus_reconnect_attempts_max: Infinity, // Max reconnect attempts
                vertxbus_reconnect_delay_min: 1000, // Initial delay (in ms) before first reconnect attempt
                vertxbus_reconnect_delay_max: 5000, // Max delay (in ms) between reconnect attempts
                vertxbus_reconnect_exponent: 2, // Exponential backoff factor
                vertxbus_randomization_factor: 0.5 // Randomization factor between 0 and 1
            };

            plugin.eb = new EventBus(plugin.settings.url + '/eventbus');
            plugin.eb.enableReconnect(true);
            plugin.eb.onopen = function () {
                callback();
            };
        }
        plugin.runLive = () => {
            $(plugin.el).find('#run-live-filter').click(e => {
                let offset = parseInt($('[name="pg_offset"]').val());
                let limit = parseInt($('[name="pg_limit"]').val());
                offset = offset < 0 ? 0 : offset;
                limit = limit <= 0 ? 20 : limit;
                plugin.settings.filters["pg_offset"] = offset;
                plugin.settings.filters["pg_limit"] = limit;
                listLive();
            });
            $(plugin.el).find('#reset-live-filter').click(e => {
                plugin.settings.filters = {};
                $('[name="pg_offset"]').val('0');
                $('[name="pg_limit"]').val('20');
                plugin.settings.filters["pg_offset"] = 0;
                plugin.settings.filters["pg_limit"] = 20;
                updateFilterList();
                unregisterLive();
            });
        }
        list = function () {
            plugin.table.html("");
            filterApi()
                .then(data => {
                    if (data.success) {
                        plugin.table.html("");
                        listTable(data);
                    } else {
                        alert(data.error);
                    }
                })
                .catch(e => {
                    alert('Api Fect Error', e);
                });
        }
        listLive = function () {
            plugin.table.html("");
            filterLiveApi()
                .then(data => {
                    if (data.success) {
                        listTable(data);
                        registerLive(data);
                    } else {
                        alert(data.error);
                    }
                })
                .catch(e => {
                    alert('Api Fect Error', e);
                });
        }
        var registerLive = (data) => {
            if (data.meta.token_live) {
                unregisterLive();
                const live = {
                    "address": data.meta.token_live.address,
                    "headers": {
                        "token": data.meta.token_live.token
                    }
                }
                plugin.eb.registerHandler(live.address, live.headers, listLiveCallback);
                plugin.settings.live = live;
                console.log(plugin.settings.live);
            }
        }
        var unregisterLive = () => {
            if (plugin.settings.live) {
                const live = plugin.settings.live;
                plugin.eb.unregisterHandler(live.address, live.headers, listLiveCallback);
            }
        }
        var listLiveCallback = (error, message) => {
            if (message) {
                const data = message.body;
                if(data.success){
                    listTable(data);
                }else{
                    alert("Live Query Error, please check console"); 
                    console.log(data);
                }

            }
        }
        var listTable = (data) => {
            plugin.table.html("");
            if (data.data.length > 0) {
                data.data.forEach((v, index) => {
                    const row = $('<tr id="item-' + v.id + '">');
                    row.append($('<th scope="row">' + v.id + '</th>'));
                    row.append($('<td>' + v.name + '</td>'));
                    row.append($('<td>' + v.username + '</td>'));
                    row.append($('<td>' + v.email + '</td>'));
                    row.append($('<td>' + v.age + '</td>'));

                    const actionCol = $('<td/>');
                    const btnUpdate = $('<button type="button" class="btn btn-sm btn-success" label="Update">Update</button>');
                    const btnDelete = $('<button type="button" class="btn btn-sm  btn-danger" label="Delete">Delete</button>');
                    actionCol.append(btnUpdate);
                    actionCol.append(btnDelete);
                    row.append(actionCol);
                    plugin.table.append(row);
                    btnUpdate.click(e => {
                        updateModal(v);
                    });
                    btnDelete.click(e => {
                        deleteRow(v);
                    });
                });
            } else {
                const row = $('<tr>');
                row.append('<td colspan="6">No records</td>');
                plugin.table.append(row);

            }
        }
        var updateFilterList = () => {
            plugin.filterList.html("");
            Object.keys(plugin.settings.filters).forEach((v, index) => {
                const listItem = $('<li id="filter-id' + index + '">');
                const label = $('<span class="m3">' + v + '-' + plugin.settings.filters[v] + '</span>');
                const btn = $('<button type="button"  class="btn btn-sm  btn-danger" label="Delete">Delete</button>');
                btn.click((e) => {
                    $('#filter-id' + index).remove();
                    delete plugin.settings.filters[v];
                });
                listItem.append(label);
                listItem.append(btn);
                plugin.filterList.append(listItem);
            });
        }
        var deleteRow = (item) => {
            callApi(plugin.settings.url + "/" + item.id, "DELETE", null)
                .then(data => {
                    if (data.success) {
                        const v = data.data[0];
                        plugin.table.find("#item-" + v.id).remove();

                    } else {
                        alert("Error While deleting Check Console.");
                        console.log(data);
                    }
                });
        }
        var updateModal = (data) => {
            $('#update-user-modal .btn-save').data('id', data.id);
            $('#update-user-modal [name="name"]').val(data.name);
            $('#update-user-modal [name="password"]').val(data.password);
            $('#update-user-modal [name="email"]').val(data.email);
            $('#update-user-modal [name="username"]').val(data.username);
            $('#update-user-modal [name="age"]').val(data.age);
            $('#update-user-modal').modal('show');
        }
        var update = (id, item) => {
            callApi(plugin.settings.url + "/" + id, "PUT", { "multiple": false, "payload": item }).then(data => {
                if (data.success) {
                    const v = data.data[0];
                    const curRow = plugin.table.find("#item-" + v.id);
                    const row = $('<tr id="item-' + v.id + '">');
                    row.append($('<th scope="row">' + v.id + '</th>'));
                    row.append($('<td>' + v.name + '</td>'));
                    row.append($('<td>' + v.username + '</td>'));
                    row.append($('<td>' + v.email + '</td>'));
                    row.append($('<td>' + v.age + '</td>'));

                    const actionCol = $('<td/>');
                    const btnUpdate = $('<button type="button" class="btn btn-sm btn-success" label="Update">Update</button>');
                    const btnDelete = $('<button type="button" class="btn btn-sm  btn-danger" label="Delete">Delete</button>');
                    btnUpdate.click(e => {
                        updateModal(v);
                    });
                    btnDelete.click(e => {
                        deleteRow(v);
                    });
                    actionCol.append(btnUpdate);
                    actionCol.append(btnDelete);
                    row.append(actionCol);
                    curRow.replaceWith(row);
                    $('#update-user-modal').modal('hide');
                } else {
                    alert('Error Create Check Logs');
                    console.log(data);
                }
            });
        }
        var create = (item) => {
            callApi(plugin.settings.url, "post", { "multiple": false, "payload": item }).then(data => {
                if (data.success) {
                    list();
                } else {
                    alert('Error  Check Logs');
                    console.log(data);
                }
            });
        }
        var callApi = async function (url, method, data) {
            var options = {

            };
            options['method'] = method;
            options['headers'] = {
                'Content-Type': 'application/json'
            };
            if (data) {
                options["body"] = JSON.stringify(data);

            }
            const response = await fetch(url, options);
            return response.json();
        }

        var filterApi = async () => {


            var url = new URL(plugin.settings.url);
            Object.keys(plugin.settings.filters).forEach(v => {
                url.searchParams.append(v, plugin.settings.filters[v]);
            });
            const response = await fetch(url);
            return response.json();
        }
        var filterLiveApi = async () => {


            var url = new URL(plugin.settings.url + "/live");
            Object.keys(plugin.settings.filters).forEach(v => {
                url.searchParams.append(v, plugin.settings.filters[v]);
            });
            const response = await fetch(url);
            return response.json();
        }

        init();

    }

})(jQuery);