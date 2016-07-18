/**
 * Created by Young's on 2016/5/23.
 */
BI.LoginInfoSelectSingleField = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.LoginInfoSelectSingleField.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-login-info-select-single-field",
            height: 30,
            dId: ""
        });
    },

    _init: function () {
        BI.LoginInfoSelectSingleField.superclass._init.apply(this, arguments);
        var self = this;

        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: this.element,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.getAllPackages(function(packs){
            self.packs = packs;
            var ids = BI.Utils.getAllPackageIDs4Conf();
            if(BI.isEmptyArray(ids)) {
                ids = [BI.Utils.getCurrentPackageId4Conf()]
            }
            self.selectDataPane.setPackage(ids[0]);
            mask.destroy();
        });

        this.selectDataPane = BI.createWidget({
            type: "bi.package_select_data_service",
            element: this.element,
            showRelativeTables: false,
            showExcelView: false,
            showDateGroup: false,
            isDefaultInit: true,
            packageCreator: function() {
                return BI.Utils.getAllGroupedPackagesTree();
            },
            tablesCreator: function (packageId) {
                return self._getTablesStructureByPackId(packageId);
            },
            fieldsCreator: function (tableId, isRelation) {
                return self._getFieldsStructureByTableId(tableId);
            }
        });
        this.selectDataPane.on(BI.PackageSelectDataService.EVENT_CLICK_ITEM, function(){
            self.fireEvent(BI.LoginInfoSelectSingleField.EVENT_CLICK_ITEM, arguments);
        });
    },

    _getTablesStructureByPackId: function (pId) {
        var self = this;
        var tablesStructure = [];
        var translations = Data.SharingPool.get("translations");
        var tables = self.packs[pId];
        BI.each(tables, function(id, table){
            tablesStructure.push({
                id: id,
                type: "bi.select_data_level0_node",
                text: translations[id],
                value: id,
                isParent: true,
                open: false,
                title: translations[id]
            });
        });
        return tablesStructure;
    },

    _getFieldsStructureByTableId: function (tableId) {
        var fieldStructure = [];
        BI.some(this.packs, function(pId, pack) {
            return BI.some(pack, function (tId, fields) {
                if (tableId === tId) {
                    BI.each(fields, function (i, field) {
                        fieldStructure.push({
                            id: field.id,
                            pId: tableId,
                            type: "bi.select_data_level0_item",
                            fieldType: field.field_type,
                            text: field.field_name,
                            value: field
                        })
                    });
                }
            });
        });
        return fieldStructure;
    }
});
BI.LoginInfoSelectSingleField.EVENT_CLICK_ITEM = "LoginInfoSelectSingleField.EVENT_CONFIRM";
$.shortcut('bi.login_info_select_single_field', BI.LoginInfoSelectSingleField);