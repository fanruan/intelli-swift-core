/**
 * @class BI.TableAddUnion
 * @extend BI.Widget
 * 添加union依据的表格
 */
BI.TableAddUnion = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.TableAddUnion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-table-add-union"
        })
    },

    _init: function(){
        BI.TableAddUnion.superclass._init.apply(this, arguments);
        this.table = BI.createWidget({
            type: "bi.table_view",
            element: this.element,
            cls: "union-table-fields",
            isNeedMerge: false
        });
    },

    _getETLTableName: function(table){
        var tables = table.tables;
        var tableName = [];
        function getDefaultName(tables){
            //只取tables[0]
            if(BI.isNotNull(tables[0].etl_type)){
                tableName.push("_" + tables[0].etl_type);
                getDefaultName(tables[0].tables);
            } else {
                tableName.push(tables[0].table_name);
            }
        }
        BI.isNotNull(tables) && getDefaultName(tables);
        //反向遍历
        tableName.reverse();
        var tableNameString = "";
        BI.each(tableName, function(i, name){
            tableNameString += name;
        });
        return tableNameString + "_" + table.etl_type;
    },

    _createTableItems: function(mergeFields){
        var self = this;
        var tables = this.options.tables, items = [];
        var validArray = this._checkUnion(mergeFields);
        BI.each(mergeFields, function(i, fArray){
            var item = [];
            var isValid = validArray[i].valid;
            BI.each(tables, function(j, table){
                item.push({
                    type: "bi.center_adapt",
                    items: [self._createFieldCombo(table, fArray, i, j, validArray[i])],
                    height: "100%",
                    cls: isValid === false ? "merge-field-warning" : ("table-color" + j%5)
                });
            });
            item.push({
                type: "bi.center_adapt",
                items: [self._createRemoveButton(i)],
                height: "100%"
            });
            items.push(item);
        });
        return items;
    },

    _createFieldCombo: function(table, fArray, indexOfMerge, index, isValidOb){
        var self = this;
        var tFields = [{
            text: BI.i18nText("BI-Null"),
            value: BI.TableAddUnion.UNION_FIELD_NULL
        }];
        var allFields = this._getAllFieldsOfOneTable(table);
        BI.each(allFields, function(i, field){
            tFields.push({
                text: field["field_name"],
                value: i
            })
        });
        var combo = BI.createWidget({
            type: "bi.text_value_combo",
            height: 30,
            cls: "table-field-combo",
            items: tFields,
            tipType: isValidOb.valid === true ? "success" : "warning",
            title: isValidOb.valid === true ? allFields[fArray[index]].field_name : isValidOb.comment
        });
        combo.setValue(fArray[index]);
        combo.on(BI.TextValueCombo.EVENT_CHANGE, function(){
            self.fireEvent(BI.TableAddUnion.EVENT_CHANGE, indexOfMerge, index, combo.getValue()[0]);
        });
        return combo;
    },

    _createRemoveButton: function(index){
        var self = this;
        var removeButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-h-font",
            width: 20,
            height: 20
        });
        removeButton.on(BI.IconButton.EVENT_CHANGE, function(){
            self.fireEvent(BI.TableAddUnion.EVENT_REMOVE_UNION, index);
        });
        return removeButton;
    },

    _getAllFieldsOfOneTable: function(table){
        var allFields = table["fields"] ;
        var s = allFields[0], n = allFields[1], d = allFields[2];
        allFields = s.concat(n);
        return allFields.concat(d);
    },

    /**
     * @param mergeFields
     * 判断逻辑：1、当前表中的字段只可被使用一次（如果重复添加之前的会被置空，所以这个应该不是在这个地方判断）；
     * 2、不为空的>1;
     * 3、字段类型相同；
     */
    _checkUnion: function(mergeFields){
        var self = this;
        var validArray = [], tables = self.options.tables;
        BI.each(mergeFields, function(i, mf){
            var notNullCount = 0, fieldTypes = [], fieldTypeValid = true;
            BI.each(mf, function(j, fieldIndex){
                if(fieldIndex > -1){
                    notNullCount++;
                    var allFields = self._getAllFieldsOfOneTable(tables[j]);
                    var fieldType = allFields[fieldIndex]["field_type"];
                    if(fieldTypes.length > 0 && !fieldTypes.contains(fieldType)){
                        fieldTypeValid = false;
                    }
                    fieldTypes.push(allFields[fieldIndex]["field_type"]);
                }
            });
            (notNullCount > 1 && fieldTypeValid === true) ?
                validArray.push({valid: true}) :
                validArray.push({
                    valid: false,
                    comment: fieldTypeValid === false ? BI.i18nText("BI-Field_Type_InValid") : BI.i18nText("BI-Two_More_Fields_Can_Merge")
                });
        });
        return validArray;
    },

    populate: function(tables, mergeFields){
        var self = this;
        this.options.tables = tables;
        var header = [], columnSize = [];
        BI.each(tables, function(i, table){
            var tableName = table.table_name;
            if(BI.isNull(tableName)){
                tableName = self._getETLTableName(table);
            }
            header.push({
                type: "bi.center_adapt",
                height: "100%",
                items: [{
                    type: "bi.label",
                    text: tableName
                }],
                cls: "table-color" + i%5
            });
            columnSize.push("");
        });
        header.push({
            type: "bi.label",
            width: 40
        });
        columnSize.push("");
        this.table.attr({columnSize: columnSize});
        this.table.populate(this._createTableItems(mergeFields), [header]);
    }
});
BI.extend(BI.TableAddUnion, {
    UNION_FIELD_NULL: -1
});
BI.TableAddUnion.EVENT_REMOVE_UNION = "EVENT_REMOVE_UNION";
BI.TableAddUnion.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.table_add_union", BI.TableAddUnion);