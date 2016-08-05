/**
 * @class BI.SelectDataWithMask
 * @extend BI.Widget
 * 带有蒙版的选择字段（设置表关联）
 */
BI.SelectDataWithMask = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.SelectDataWithMask.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-data-with-mask"
        })
    },

    _init: function(){
        BI.SelectDataWithMask.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = o.model;
        this.fieldId = o.field_id;
        this._initAllRelationTables();

        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: this.element,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.getAllPackages(function(packs){
            self.packs = packs;
            //选中当前业务包
            self.selectDataPane.setPackage(BI.Utils.getCurrentPackageId4Conf());
            mask.destroy();
        });

        this.selectDataPane = BI.createWidget({
            type: "bi.package_select_data_service",
            showRelativeTables: false,
            showExcelView: false,
            showDateGroup: false,
            packageCreator: function() {
                return BI.Utils.getAllGroupedPackagesTreeJSON4Conf();
            },
            tablesCreator: function (packageId) {
                return self._getTablesStructureByPackId(packageId);
            },
            fieldsCreator: function (tableId, isRelation) {
                return self._getFieldsStructureByTableId(tableId);
            }
        });
        this.selectDataPane.on(BI.PackageSelectDataService.EVENT_CLICK_ITEM, function(){
            self.fireEvent(BI.SelectDataWithMask.EVENT_CHANGE, arguments);
        });

        var wrapper = BI.createWidget({
            type: "bi.vtape",
            cls: "select-data-wrapper",
            items: [{
                el: this.selectDataPane,
                height: "fill"
            }, {
                el: this._createSelectDataBottom(),
                height: 50
            }],
            width: 240
        });
        this.mask = BI.createWidget({
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
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.mask,
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }, {
                el: wrapper,
                top: 10,
                bottom: 10,
                left: 10
            }]
        })
    },

    _createSelectDataBottom: function(){
        var self = this;
        var cancelButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            width: 150,
            height: 30,
            text: BI.i18nText("BI-Cancel")
        });
        cancelButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.SelectDataWithMask.EVENT_VALUE_CANCEL);
        });
        return BI.createWidget({
            type: "bi.horizontal_auto",
            cls: "select-data-button-group",
            items: [cancelButton],
            vgap: 10
        })
    },

    _initAllRelationTables: function(){
        var self = this;
        var relations = this.model.getRelations(), fieldId = this.model.getFieldId();
        //灰化：所有已与当前表建立过关联关系的表灰化
        //只需要遍历connectionSet
        var connectionSet = relations.connectionSet;
        this.allRelationTables = [];
        var tableId = this.model.getTableIdByFieldId(fieldId);
        BI.each(connectionSet, function (i, pf) {
            var primaryKey = pf.primaryKey, foreignKey = pf.foreignKey;
            //修改的就不用灰化了
            if(self.fieldId === primaryKey.field_id || self.fieldId === foreignKey.field_id) {
                return;
            }
            if (tableId === self.model.getTableIdByFieldId(primaryKey.field_id)) {
                self.allRelationTables.push(self.model.getTableIdByFieldId(foreignKey.field_id));
            } else if (tableId === self.model.getTableIdByFieldId(foreignKey.field_id)) {
                self.allRelationTables.push(self.model.getTableIdByFieldId(primaryKey.field_id));
            }
        });
    },

    _getTablesStructureByPackId: function (pId) {
        var self = this;
        var translations = this.model.getTranslations();
        var tableId = this.model.getTableIdByFieldId(this.model.getFieldId());
        var tablesStructure = [];
        //当前编辑业务包从Sharing Pool取
        if (pId === BI.Utils.getCurrentPackageId4Conf()) {
            var tables = BI.Utils.getCurrentPackageTables4Conf();
            BI.each(tables, function (id, table) {
                tablesStructure.push({
                    id: id,
                    type: "bi.select_data_level0_node",
                    text: translations[id],
                    value: id,
                    isParent: true,
                    open: false,
                    disabled:self.allRelationTables.contains(id) || id === tableId,
                    title: translations[id],
                    warningTitle:  BI.i18nText("BI-Already_Relation_With_Current_Table")
                });
            });
        } else {
            var tables = self.packs[pId];
            BI.each(tables, function(id, table){
                tablesStructure.push({
                    id: id,
                    type: "bi.select_data_level0_node",
                    text: translations[id],
                    value: id,
                    isParent: true,
                    open: false,
                    disabled: self.allRelationTables.contains(id) || id === tableId,
                    title: translations[id],
                    warningTitle:  BI.i18nText("BI-Already_Relation_With_Current_Table")
                });
            });
        }
        return tablesStructure;
    },

    _getFieldsStructureByTableId: function (tableId) {
        var translations = this.model.getTranslations();
        var fieldStructure = [];
        var tables = BI.Utils.getCurrentPackageTables4Conf();
        var fieldType = this.model.getFieldTypeByFieldId(this.model.getFieldId());
        if (BI.isNotNull(tables[tableId])) {
            var fields = [];
            BI.each(tables[tableId].fields[0], function(i, field){
                fieldType === field.field_type && fields.push(field);
            });
            BI.each(fields, function (i, field) {
                fieldStructure.push({
                    id: field.id,
                    pId: tableId,
                    type: "bi.select_data_level0_item",
                    fieldType: fieldType,
                    text: translations[field.id] || field.field_name,
                    title: translations[field.id] || field.field_name,
                    value: {
                        field_id: field.id
                    }
                })
            });
        } else {
            BI.some(this.packs, function(pId, pack){
                return BI.some(pack, function(tId, fields){
                    if(tableId === tId) {
                        BI.each(fields, function(i, field){
                            if(field.field_type === fieldType) {
                                fieldStructure.push({
                                    id: field.id,
                                    pId: tableId,
                                    type: "bi.select_data_level0_item",
                                    fieldType: fieldType,
                                    text: translations[field.id] || field.field_name,
                                    title: translations[field.id] || field.field_name,
                                    value: {
                                        field_id: field.id
                                    }
                                })
                            }
                        });
                    }
                });
            });
        }
        return fieldStructure;
    },

    destroy: function(){
        this.selectDataPane.destroy();
    }
});
BI.SelectDataWithMask.EVENT_CHANGE = "EVENT_CHANGE";
BI.SelectDataWithMask.EVENT_VALUE_CANCEL = "EVENT_VALUE_CANCEL";
$.shortcut("bi.select_data_with_mask", BI.SelectDataWithMask);