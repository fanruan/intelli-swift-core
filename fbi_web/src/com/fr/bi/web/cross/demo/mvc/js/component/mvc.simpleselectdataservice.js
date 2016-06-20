SimpleSelectDataServiceView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SimpleSelectDataServiceView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-package-select-data-service bi-mvc-layout"
        })
    },

    _init: function () {
        SimpleSelectDataServiceView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var service = BI.createWidget({
            type: "bi.simple_select_data_service",
            cls: "service",
            width: 220,
            tablesCreator: function () {
                var pIds = BI.Utils.getAllPackageIDs();
                var ids = BI.Utils.getTableIDsOfPackageID(pIds[0]);
                return BI.map(ids, function (i, id) {
                    return {
                        id: id
                    }
                })
            },
            fieldsCreator: function (tableId) {
                var ids = BI.Utils.getSortedFieldIdsOfOneTableByTableId(tableId)
                return BI.map(ids, function (i, id) {
                    return {
                        id: id
                    }
                })
            }
        });
        service.populate();
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: service,
                left: 20,
                top: 20,
                bottom: 20
            }]
        })
    }
});

SimpleSelectDataServiceModel = BI.inherit(BI.Model, {});