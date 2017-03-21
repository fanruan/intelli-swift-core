/**
 * @class BI.SelectDataWithMask
 * @extend BI.Widget
 * 带有蒙版的选择字段（设置表关联）
 */
BI.SelectDataWithMask = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectDataWithMask.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-data-with-mask"
        })
    },

    _init: function () {
        BI.SelectDataWithMask.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.field = o.field;
        var data = o.data;
        this.selectDataPane = BI.createWidget({
            type: "bi.package_select_data_service",
            showRelativeTables: false,
            showExcelView: false,
            showDateGroup: false,
            packageCreator: function () {
                return BI.Utils.getAllGroupedPackagesTreeJSON4Conf(data);
            },
            tablesCreator: function (packageId) {
                return self._getTablesStructureByPackId(packageId);
            },
            fieldsCreator: function (tableId, opt) {
                return self._getFieldsStructureByTableId(tableId, opt);
            }
        });
        this.selectDataPane.on(BI.PackageSelectDataService.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.SelectDataWithMask.EVENT_CHANGE, arguments);
        });

        var selectDataWrapper = BI.createWidget({
            type: "bi.vtape",
            cls: "select-data-wrapper",
            items: [{
                el: this.selectDataPane,
                height: "fill"
            }, {
                el: this._createSelectDataBottom(),
                height: 50
            }]
        })

        selectDataWrapper.element.resizable({
            handles: "e",
            minWidth: 200,
            maxWidth: 400,
            autoHide: true,
            helper: "bi-resizer",
            start: function () {
            },
            resize: function (e, ui) {
            },
            stop: function (e, ui) {
                items[1].width = ui.size.width;
                selectDataWrapper.resize();
            }
        });

        var items = [{
            el: {
                type: "bi.absolute",
                items: [{
                    el: {
                        type: "bi.default",
                        cls: "select-data-mask"
                    },
                    top: 0,
                    left: 0,
                    bottom: 0,
                    right: 0
                }]
            },
            top: 0,
            right: 0,
            bottom: 0,
            left: 0
        }, {
            el: selectDataWrapper,
            top: 10,
            bottom: 10,
            width: 240,
            left: 10
        }];

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: items
        });

        this.selectDataPane.setPackage(this._getPackageIdByTableId(this.field.table_id));
    },

    _getPackageIdByTableId: function (tableId) {
        var packages = this.options.data.packages;
        var packId;
        BI.some(packages, function (pId, pack) {
            var tables = pack.tables;
            return BI.some(tables, function (i, table) {
                if (table.id === tableId) {
                    packId = pId;
                    return true;
                }
            })
        });
        return packId;
    },

    _createSelectDataBottom: function () {
        var self = this;
        var cancelButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            width: 150,
            height: 30,
            text: BI.i18nText("BI-Basic_Cancel")
        });
        cancelButton.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.SelectDataWithMask.EVENT_VALUE_CANCEL);
        });
        return BI.createWidget({
            type: "bi.horizontal_auto",
            cls: "select-data-button-group",
            items: [cancelButton],
            vgap: 10
        })
    },

    _isTableOpen: function (table) {
        var self = this;
        return BI.some(table.fields, function (i, fs) {
            return BI.some(fs, function (j, field) {
                return field.id === self.field.id;
            });
        });
    },

    _getTableIdByPackageId: function(packId) {
        var packages = this.options.data.packages;
        var tableIds = [];
        BI.each(packages[packId].tables, function (i, table) {
            tableIds.push(table.id);
        });
        return tableIds;
    },

    _getTablesStructureByPackId: function (pId) {
        var self = this, o = this.options;
        var data = o.data;
        var translations = data.translations;
        var tablesStructure = [];
        var tableIds = this._getTableIdByPackageId(pId);
        BI.each(tableIds, function (i, id) {
            tablesStructure.push({
                id: id,
                type: "bi.select_data_level_node",
                text: translations[id],
                title: translations[id],
                value: id,
                isParent: true,
                open: self._isTableOpen(id),
                disabled: id === self.field.table_id,
                warningTitle: BI.i18nText("BI-Can_Not_Relation_Self")
            });
        });
        return tablesStructure;
    },

    _getFieldsByTableId: function (tableId) {
        var allFields = this.options.data.fields;
        var fields = [];
        BI.each(allFields, function (id, field) {
            if (field.table_id === tableId) {
                fields.push(BI.deepClone(field));
            }
        });
        return fields;
    },

    _getFieldsStructureByTableId: function (tableId) {
        var fieldStructure = [];
        var fieldType = this.field.field_type;
        var translations = this.options.data.translations;
        var relationFields = BI.Utils.getRelationFieldsByFieldId4Conf(this.options.relations, this.field.id);
        var fields =this._getFieldsByTableId(tableId);
        BI.each(fields, function (i, field) {
            if (field.field_type === fieldType) {
                fieldStructure.push({
                    id: field.id,
                    pId: tableId,
                    type: "bi.select_data_level_item",
                    fieldType: fieldType,
                    text: translations[field.id] || field.field_name,
                    title: translations[field.id] || field.field_name,
                    value: {
                        field_id: field.id
                    },
                    disabled: relationFields.contains(field.id),
                    warningTitle: BI.i18nText("BI-Already_Relation_With_Current_Field")
                })
            }
        });
        return fieldStructure;
    },

    destroy: function () {
        this.selectDataPane.destroy();
    }
});
BI.SelectDataWithMask.EVENT_CHANGE = "EVENT_CHANGE";
BI.SelectDataWithMask.EVENT_VALUE_CANCEL = "EVENT_VALUE_CANCEL";
$.shortcut("bi.select_data_with_mask", BI.SelectDataWithMask);
