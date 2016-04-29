/**
 * @class BI.TableFieldInfo
 * @extend BI.Widget
 * 表字段设置
 */
BI.TableFieldInfo = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.TableFieldInfo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-table-field-info"
        })
    },

    _init: function(){
        BI.TableFieldInfo.superclass._init.apply(this, arguments);
    },

    populate: function(tableInfo){
        var self = this;
        this.tableInfo = tableInfo;
        this.changeLocked = false;
        this.headerCheckbox = BI.createWidget({
            type: "bi.multi_select_bar",
            width: 20,
            height: 20
        });
        this.headerCheckbox.on(BI.MultiSelectBar.EVENT_CHANGE, function(){
            self._headerCheckChange();
            self.fireEvent(BI.TableFieldInfo.EVENT_USABLE_CHANGE, self.usedFields);
        });
        var header = [[
                {
                    type: "bi.label",
                    text: BI.i18nText("BI-Original_Field_Name")
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Type")
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Escape_Name")
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Associate_Table")
                }, {
                    type: "bi.center_adapt",
                    items: [this.headerCheckbox, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Involved_Analysis")
                    }]
                }]];

        this.table = BI.createWidget({
            type: "bi.table_view",
            element: this.element,
            isNeedMerge: false,
            columnSize: [125, 65, 90, 120, 100],
            header: header
        });

        this.table.populate(this._createTableItems());
        return this.table;
    },

    /**
     *  fields排序
     *  参与分析的放在前面：其中转义过的放在前面，没有转义过的放在后面
     ?  不参与分析的放在后面：其中转义过的放在前面，没有转义过的放在后面
     ?  转义过/没转义过的各自之间的字段按照数据库里面的顺序排列
     * @private
     */
    _sortFields: function(){
        var self = this;
        var fields = this.tableInfo.fields;
        var sortedFields = [], usedFields = [], noUsedFields = [];
        var allTransKeys = BI.keys(this.translations);
        BI.each(fields, function(i, fieldArray){
            BI.each(fieldArray, function(j, field){
                if(self.usedFields.contains(field.field_name)){
                    usedFields.push(field);
                } else {
                    noUsedFields.push(field);
                }
            });
        });
        var usedNoTrans = [];
        BI.each(usedFields, function(i, field){
            if(allTransKeys.contains(field.id)){
                sortedFields.push(field);
            } else {
                usedNoTrans.push(field);
            }
        });
        sortedFields = sortedFields.concat(usedNoTrans);
        var noUsedNoTrans = [];
        BI.each(noUsedFields, function(i, field){
            if(allTransKeys.contains(field.id)){
                sortedFields.push(field);
            } else {
                noUsedNoTrans.push(field);
            }
        });
        sortedFields = sortedFields.concat(noUsedNoTrans);
        return sortedFields;
    },

    /**
     * 创建items
     * @returns {Array}
     * @private
     */
    _createTableItems: function(){
        var self = this, items = [];
        this.usedFields = this.tableInfo.usedFields || [];
        this.translations = this.tableInfo.translations;
        this.isUsableArray = [];
        var sortedFields = this._sortFields();
        BI.each(sortedFields, function(i, field){
            var fieldType = field.field_type, typeCls = "chart-string-font";
            switch (fieldType){
                case BICst.COLUMN.NUMBER:
                    typeCls = "chart-number-font";
                    break;
                case BICst.COLUMN.DATE:
                    typeCls = "chart-date-font";
                    break;
            }
            var item = [];
            item.push({
                type: "bi.label",
                text: field["field_name"],
                title: field["field_name"],
                width: 125,
                whiteSpace: "nowrap",
                textAlign: "left",
                lgap: 5
            });
            item.push({
                type: "bi.icon_button",
                cls: "field-type " + typeCls
            });
            item.push(self._createTranName(self.tableInfo.id, field.field_name));
            item.push(self._createRelationButton(field.id));

            var isUsable = self._createIsUsable(field);
            field.is_enable === false && self.usedFields.remove(field.field_name);
            isUsable.attr("disabled") === false && self.isUsableArray.push(isUsable);
            item.push({
                type: "bi.center_adapt",
                items: [isUsable]
            });
            items.push(item);
        });
        this._changeMultiCheck();
        return items;
    },

    _createRelationButton : function(fieldId){
        var self = this;
        var relationButton = BI.createWidget({
            type: "bi.relation_tables_button",
            relation_tables: this._getRelationTables(fieldId)
        });
        relationButton.on(BI.RelationTablesButton.EVENT_CHANGE, function(){
            self.fireEvent(BI.TableFieldInfo.EVENT_RELATION_CHANGE, fieldId);
        });
        return relationButton;
    },

    _checkTransName: function(id, tId, fieldName){
        if(id.indexOf(tId) !== -1 && id.indexOf(fieldName) !== -1){
            var tmp = id.substring(BI.size(tId));
            if(BI.isEqual(tmp, fieldName)){
                return true;
            }
        }
        return false;
    },

    _createTranName: function(tId, fieldName){
        var self = this;
        var tranName = "";
        BI.some(this.translations, function(id, name){
            if(self._checkTransName(id, tId, fieldName) && !BI.isEqual(name, fieldName)){
                tranName = name;
                return true;
            }
        });
        var transName = BI.createWidget({
            type: "bi.sign_editor",
            value: tranName,
            title: tranName,
            allowBlank: true,
            errorText: BI.i18nText("BI-Trans_Name_Exist"),
            validationChecker: function(v){
                var isValid = true;
                BI.each(self.translations, function(id, name){
                    if(id.indexOf(tId) !== -1 && id.indexOf(fieldName) === -1 && isValid === true){
                        v === name && (isValid = false);
                    }
                });
                return isValid;
            }
        });
        transName.on(BI.SignEditor.EVENT_CHANGE, function(){
            transName.setTitle(transName.getValue());
            self.translations[tId + fieldName] = transName.getValue();
            self.fireEvent(BI.TableFieldInfo.EVENT_TRANSLATION_CHANGE, self.translations);
        });
        transName.on(BI.SignEditor.EVENT_EMPTY, function(){
            delete self.translations[tId + fieldName];
            self.fireEvent(BI.TableFieldInfo.EVENT_TRANSLATION_CHANGE, self.translations);
        });
        return BI.createWidget({
            type: "bi.center_adapt",
            items: [transName]
        });
    },

    _getRelationTables: function(fieldId){
        var relations = this.tableInfo.relations;
        var translations = this.tableInfo.translations;
        var allFields = this.tableInfo.all_fields;
        var primKeyMap = relations.primKeyMap, foreignKeyMap = relations.foreignKeyMap;
        var currentPrimKey = primKeyMap[fieldId] || [], currentForKey = foreignKeyMap[fieldId];
        var relationTables = [];
        BI.each(currentPrimKey, function(i, maps){
            var pk = maps.primaryKey, fk = maps.foreignKey;
            if(pk.field_id === fieldId && fk.field_id !== fieldId){
                relationTables.push(translations[allFields[fk.field_id].table_id]);
            }
        });
        BI.each(currentForKey, function(i, maps){
            var pk = maps.primaryKey, fk = maps.foreignKey;
            if(fk.field_id === fieldId && pk.field_id !== fieldId){
                relationTables.push(translations[allFields[pk.field_id].table_id]);
            }
        });
        return relationTables;
    },

    _createIsUsable: function(field){
        var self = this;
        var isUsable = BI.createWidget({
            type: "bi.checkbox",
            selected: this.usedFields.contains(field.field_name) && (field.is_enable === true),
            disabled: !field.is_enable
        });
        isUsable.on(BI.Checkbox.EVENT_CHANGE, function(){
            self._halfCheckChange(field.field_name, isUsable);
            self.fireEvent(BI.TableFieldInfo.EVENT_USABLE_CHANGE, self.usedFields);
        });
        return isUsable;
    },

    _halfCheckChange: function(fieldName, isUsable){
        var self = this;
        if(this.changeLocked === true){
            return;
        }
        this.changeLocked = true;
        if(isUsable.isSelected() === true){
            this.usedFields.push(fieldName);
        } else {
            BI.some(this.usedFields, function(i, fName){
                if(fName === fieldName){
                    self.usedFields.splice(i, 1);
                    return true;
                }
            })
        }
        this._changeMultiCheck();
        this.changeLocked = false;
    },

    _changeMultiCheck: function(){
        if(this.usedFields.length === 0){
            this.headerCheckbox.setSelected(false);
        } else if(this.usedFields.length === this.isUsableArray.length){
            this.headerCheckbox.setSelected(true);
        } else {
            this.headerCheckbox.setHalfSelected(true);
        }
    },

    _headerCheckChange: function(){
        var self = this;
        if(this.changeLocked === true){
            return;
        }
        this.changeLocked = true;
        var selected = this.headerCheckbox.isSelected();
        BI.each(this.isUsableArray, function(i, isUsable){
            self.isUsableArray[i].setSelected(selected);
        });
        if(selected === true){
            var fields = this.tableInfo.fields;
            this.usedFields = [];
            BI.each(fields, function(i, fs){
                BI.each(fs, function(j, field){
                    field.is_enable === true && self.usedFields.push(field.field_name);
                })
            })
        } else {
            this.usedFields = [];
        }
        this.changeLocked = false;
    },


    getUsedFields: function(){
        return this.usedFields;
    }
});
BI.TableFieldInfo.EVENT_USABLE_CHANGE = "EVENT_USABLE_CHANGE";
BI.TableFieldInfo.EVENT_RELATION_CHANGE = "EVENT_RELATION_CHANGE";
BI.TableFieldInfo.EVENT_TRANSLATION_CHANGE = "EVENT_TRANSLATION_CHANGE";
$.shortcut("bi.table_field_info", BI.TableFieldInfo);