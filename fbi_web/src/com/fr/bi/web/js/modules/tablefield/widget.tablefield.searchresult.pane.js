/**
 * @class BI.TableFieldInfoSearchResultPane
 * @extend BI.Widget
 * etl界面表数据设置表格的搜索结果面板
 */
BI.TableFieldInfoSearchResultPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TableFieldInfoSearchResultPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-table-field-info-search-result-pane"
        })
    },

    _init: function () {
        BI.TableFieldInfoSearchResultPane.superclass._init.apply(this, arguments);
        this.resultPane = BI.createWidget({
            type: "bi.absolute",
            element: this.element
        });
    },

    populate: function (tableInfo, matchResult, keyword) {
        var self = this;
        if (BI.isEmptyArray(tableInfo.fields) && BI.isEmptyArray(matchResult)) {
            this.resultPane.empty();
            this.resultPane.addItem({
                el: {
                    type: "bi.label",
                    cls: "no-result-comment",
                    textAlign: "center",
                    text: BI.i18nText("BI-No_Select")
                },
                top: 10,
                left: 0,
                right: 0
            });
            return;
        }

        this.tableInfo = tableInfo;
        this.matchResult = matchResult;

        this.keyword = keyword;
        this.changeLocked = false;
        var onUsedFieldsChange = this.options.onUsedFieldsChange;
        this.headerCheckbox = BI.createWidget({
            type: "bi.multi_select_bar",
            width: 20,
            height: 20
        });
        this.headerCheckbox.on(BI.MultiSelectBar.EVENT_CHANGE, function () {
            self._headerCheckChange();
            onUsedFieldsChange(self.usedFields);
        });
        var header = [[
            {
                type: "bi.label",
                text: BI.i18nText("BI-Original_Field_Name")
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Basic_Type")
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
            type: "bi.preview_table",
            element: this.element,
            isNeedMerge: false,
            columnSize: [125, 65, 90, 120, ""],
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
    _sortFields: function (fields) {
        var self = this;
        var sortedFields = [], usedFields = [], noUsedFields = [];
        BI.each(fields, function (i, field) {
            if (self.usedFields.contains(field.id)) {
                usedFields.push(field);
            } else {
                noUsedFields.push(field);
            }
        });
        var usedNoTrans = [];
        BI.each(usedFields, function (i, field) {
            var tranName = BI.Utils.getTransNameById4Conf(field.id);
            if (BI.isNotNull(tranName) && tranName !== "") {
                sortedFields.push(field);
            } else {
                usedNoTrans.push(field);
            }
        });
        sortedFields = sortedFields.concat(usedNoTrans);
        var noUsedNoTrans = [];
        BI.each(noUsedFields, function (i, field) {
            var tranName = BI.Utils.getTransNameById4Conf(field.id);
            if (BI.isNotNull(tranName) && tranName !== "") {
                sortedFields.push(field);
            } else {
                noUsedNoTrans.push(field);
            }
        });
        sortedFields = sortedFields.concat(noUsedNoTrans);
        return sortedFields;
    },

    _createTableItems: function () {
        var self = this, items = [];
        this.usedFields = this.tableInfo.usedFields || [];
        this.translations = this.tableInfo.translations;
        this.isUsableArray = [];
        var sortedFields = this._sortFields(this.matchResult).concat(this.tableInfo.fields);
        BI.each(sortedFields, function (i, field) {
            var fieldType = field.field_type, typeCls = "chart-string-font";
            switch (fieldType) {
                case BICst.COLUMN.NUMBER:
                    typeCls = "chart-number-font";
                    break;
                case BICst.COLUMN.DATE:
                    typeCls = "chart-date-font";
                    break;
            }
            var item = [];
            if (BI.Utils.isPrimaryKeyByFieldId4Conf(field.id)) {
                var fieldNameLabel = BI.createWidget({
                    type: "bi.label",
                    text: field["field_name"],
                    title: field["field_name"],
                    width: 90,
                    whiteSpace: "nowrap",
                    textAlign: "left"
                });
                fieldNameLabel.doRedMark(self.keyword);
                item.push({
                    type: "bi.left",
                    cls: "primary-key-font",
                    items: [{
                        type: "bi.icon",
                        width: 20,
                        title: BI.i18nText("BI-Primary_Key")
                    }, fieldNameLabel],
                    lgap: 5
                });
            } else {
                var fieldNameLabel = BI.createWidget({
                    type: "bi.label",
                    text: field["field_name"],
                    title: field["field_name"],
                    width: 125,
                    whiteSpace: "nowrap",
                    textAlign: "left",
                    lgap: 5
                });
                fieldNameLabel.doRedMark(self.keyword);
                item.push(fieldNameLabel);
            }
            item.push({
                type: "bi.icon_button",
                cls: "field-type " + typeCls
            });
            item.push(self._createTranName(field.id, field.field_name));
            item.push(self._createRelationButton(field.id));

            var isUsable = self._createIsUsable(field);
            field.is_enable === false && self.usedFields.remove(field.id);
            isUsable.attr("disabled") === false && self.isUsableArray.push(isUsable);
            item.push({
                type: "bi.center_adapt",
                items: [isUsable],
                height: 25
            });
            items.push(item);
        });
        this._changeMultiCheck();
        return items;
    },

    _createRelationButton: function (fieldId) {
        var self = this;
        var relationButton = BI.createWidget({
            type: "bi.relation_tables_button",
            fieldId: fieldId
        });
        relationButton.on(BI.RelationTablesButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.TableFieldInfo.EVENT_RELATION_CHANGE, fieldId);
        });
        return relationButton;
    },

    _createTranName: function (fieldId) {
        var self = this;
        var tranName = BI.Utils.getTransNameById4Conf(fieldId);
        var onTranslationsChange = this.options.onTranslationsChange;
        var transName = BI.createWidget({
            type: "bi.sign_editor",
            value: tranName,
            title: tranName,
            allowBlank: true,
            errorText: BI.i18nText("BI-Trans_Name_Exist"),
            height: 25,
            validationChecker: function (v) {
                return BI.Utils.checkTranNameById4Conf(fieldId, v);
            }
        });
        transName.on(BI.SignEditor.EVENT_CHANGE, function () {
            transName.setTitle(transName.getValue());
            self.translations[fieldId] = transName.getValue();
            onTranslationsChange(self.translations);
        });
        return transName;
    },

    _createIsUsable: function (field) {
        var self = this;
        var onUsedFieldsChange = this.options.onUsedFieldsChange;
        var isUsable = BI.createWidget({
            type: "bi.checkbox",
            selected: this.usedFields.contains(field.id) && (field.is_enable === true),
            disabled: !field.is_enable
        });
        isUsable.on(BI.Checkbox.EVENT_CHANGE, function () {
            self._halfCheckChange(field.id, isUsable);
            onUsedFieldsChange(self.usedFields, field.id);
        });
        return isUsable;
    },

    _halfCheckChange: function (fieldId, isUsable) {
        var self = this;
        if (this.changeLocked === true) {
            return;
        }
        this.changeLocked = true;
        if (isUsable.isSelected() === true) {
            this.usedFields.push(fieldId);
        } else {
            BI.some(this.usedFields, function (i, fId) {
                if (fId === fieldId) {
                    self.usedFields.splice(i, 1);
                    return true;
                }
            })
        }
        this._changeMultiCheck();
        this.changeLocked = false;
    },

    _changeMultiCheck: function () {
        //在搜索结果面板与原始的面板不同
        var checkedFields = 0;
        BI.each(this.isUsableArray, function (i, isUsable) {
            if (isUsable.isSelected() === true) {
                checkedFields++;
            }
        });
        if (checkedFields === 0) {
            this.headerCheckbox.setSelected(false);
        } else if (checkedFields === this.isUsableArray.length) {
            this.headerCheckbox.setSelected(true);
        } else {
            this.headerCheckbox.setHalfSelected(true);
        }
    },

    _headerCheckChange: function () {
        var self = this;
        if (this.changeLocked === true) {
            return;
        }
        this.changeLocked = true;
        var selected = this.headerCheckbox.isSelected();
        BI.each(this.isUsableArray, function (i, isUsable) {
            self.isUsableArray[i].setSelected(selected);
        });
        var fields = this.tableInfo.fields;
        if (selected === true) {
            BI.each(fields, function (i, field) {
                field.is_enable === true && !self.usedFields.contains(field.id) && self.usedFields.push(field.id);
            })
        } else {
            BI.each(fields, function (i, field) {
                if (self.usedFields.contains(field.id)) {
                    BI.remove(self.usedFields, field.id);
                }
            });
        }
        this.changeLocked = false;
    },


    getUsedFields: function () {
        return this.usedFields;
    },

    empty: function () {
        this.resultPane.empty();
    }
});
$.shortcut("bi.table_field_info_search_result_pane", BI.TableFieldInfoSearchResultPane);