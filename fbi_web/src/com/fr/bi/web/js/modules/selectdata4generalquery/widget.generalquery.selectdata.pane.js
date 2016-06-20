/**
 * Created by Young's on 2016/5/9.
 */
BI.GeneralQuerySelectDataPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.GeneralQuerySelectDataPane.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BI.GeneralQuerySelectDataPane.superclass._init.apply(this, arguments);
        var self = this;
        this.service = BI.createWidget({
            type: "bi.package_select_data_service",
            element: this.element,
            showRelativeTables: false,
            showExcelView: false,
            showDateGroup: false,
            tablesCreator: function (packageId) {
                var ids = BI.Utils.getTableIDsOfPackageID(packageId);
                return BI.map(ids, function (i, id) {
                    return {
                        id: id
                    }
                })
            },
            fieldsCreator: function (tableId) {
                var ids = BI.Utils.getSortedFieldIdsOfOneTableByTableId(tableId);
                var result = [];
                BI.each(ids, function (i, fid) {
                    if (BI.Utils.getFieldIsUsableByID(fid) === true) {
                        result.push({
                            id: fid,
                            type: "bi.general_query_select_data_item",
                            fieldType: BI.Utils.getFieldTypeByID(fid)
                        })
                    }
                });
                return result;
            }
        });

        this.service.on(BI.PackageSelectDataService.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.GeneralQuerySelectDataPane.EVENT_CLICK_ITEM, arguments);
        });
    }
});
BI.GeneralQuerySelectDataPane.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.general_query_select_data_pane", BI.GeneralQuerySelectDataPane);
