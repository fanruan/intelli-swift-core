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
            text: BI.i18nText("BI-Basic_Loading")
        });
        BI.Utils.getData4SelectField4Conf(function (data) {
            self.cacheData = data;
            var packages = data.packages || [];
            self.selectDataPane.setPackage(BI.keys(packages)[0]);
        }, function () {
            mask.destroy();
        });

        this.selectDataPane = BI.createWidget({
            type: "bi.package_select_data_service",
            element: this.element,
            showRelativeTables: false,
            showExcelView: false,
            showDateGroup: false,
            isDefaultInit: true,
            packageCreator: function () {
                return BI.Utils.getAllGroupedPackagesTree();
            },
            tablesCreator: function (packageId) {
                return self._getTablesStructureByPackId(packageId);
            },
            fieldsCreator: function (tableId, opt) {
                return self._getFieldsStructureByTableId(tableId, opt);
            }
        });
        this.selectDataPane.on(BI.PackageSelectDataService.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.LoginInfoSelectSingleField.EVENT_CLICK_ITEM, arguments);
        });
    },

    _getTableIdByPackageId: function (packId) {
        var packages = this.cacheData.packages;
        var tableIds = [];
        BI.each(packages[packId].tables, function (i, table) {
            tableIds.push(table.id);
        });
        return tableIds;
    },

    _getTablesStructureByPackId: function (pId) {
        var self = this;
        var tablesStructure = [];
        var translations = this.cacheData.translations;
        var tables = self._getTableIdByPackageId(pId);
        BI.each(tables, function (i, id) {
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
        var fields = this.cacheData.fields;
        var translations = this.cacheData.translations;
        BI.each(fields, function (i, field) {
            if (tableId === field.table_id &&
                field.field_type === BICst.COLUMN.STRING) {
                var fieldName = translations[field.id] || field.field_name;
                field[BICst.JSON_KEYS.TABLE_TRAN_NAME] = translations[field.table_id];
                field[BICst.JSON_KEYS.FIELD_TRAN_NAME] = fieldName;
                fieldStructure.push({
                    id: field.id,
                    pId: tableId,
                    type: "bi.select_data_level0_item",
                    fieldType: field.field_type,
                    text: fieldName,
                    title: fieldName,
                    value: field
                })
            }
        });
        return fieldStructure;
    }
});
BI.LoginInfoSelectSingleField.EVENT_CLICK_ITEM = "LoginInfoSelectSingleField.EVENT_CONFIRM";
$.shortcut('bi.login_info_select_single_field', BI.LoginInfoSelectSingleField);
