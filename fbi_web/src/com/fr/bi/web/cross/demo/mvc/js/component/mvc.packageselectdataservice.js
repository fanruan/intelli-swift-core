PackageSelectDataServiceView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(PackageSelectDataServiceView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-package-select-data-service bi-mvc-layout"
        })
    },

    _init: function () {
        PackageSelectDataServiceView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var service = BI.createWidget({
            type: "bi.package_select_data_service",
            cls: "service",
            width: 220,
            showRelativeTables: true,
            showExcelView: true,
            showDateGroup: true,
            tablesCreator: function (packageId, isRelation) {
                if (isRelation === true) {
                    var tIds = BI.Utils.getPrimaryRelationTablesByTableID(packageId);
                    return BI.map(tIds, function (i, id) {
                        return {
                            id: id
                        }
                    })
                }
                var ids = BI.Utils.getTableIDsOfPackageID(packageId);
                return BI.map(ids, function (i, id) {
                    return {
                        id: id
                    }
                })
            },
            fieldsCreator: function (tableId, isRelation) {
                var ids = BI.Utils.getSortedFieldIdsOfOneTableByTableId(tableId)
                return BI.map(ids, function (i, id) {
                    return {
                        id: id
                    }
                })
            }
        });
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

PackageSelectDataServiceModel = BI.inherit(BI.Model, {});