/**
 * @class BI.TableFieldWithSearchPane
 * @extend BI.Widget
 * 带有搜索的表格，用于elt界面左侧
 */
BI.TableFieldWithSearchPane = FR.extend(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TableFieldWithSearchPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-table-field-with-search-pane"
        })
    },

    _init: function () {
        BI.TableFieldWithSearchPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var tableInfo = o.tableInfo;
        var fields = tableInfo.fields;
        var searcher = BI.createWidget({
            type: "bi.searcher",
            el: {
                type: "bi.search_editor",
                width: 500
            },
            onSearch: function (op, callback) {
                var res;
                //搜索所有的字段信息匹配
                var newFields = [], newTableInfo = BI.deepClone(tableInfo);
                var matched = [], finded = [];
                BI.each(fields, function (i, fs) {
                    res = BI.Func.getSearchResult(fs, op.keyword, "field_name");
                    matched = matched.concat(res.matched);
                    finded = finded.concat(res.finded);
                });
                newTableInfo.fields = finded;
                newTableInfo.usedFields = table.getUsedFields();
                callback(newTableInfo, matched, op.keyword);
            },
            isAutoSearch: false,
            isAutoSync: false,
            popup: {
                type: "bi.table_field_info_search_result_pane",
                onUsedFieldsChange: function (usedFields) {
                    tableInfo.usedFields = usedFields;
                    table.populate(tableInfo);
                    self.fireEvent(BI.TableFieldWithSearchPane.EVENT_USABLE_CHANGE, usedFields);
                },
                onRelationsChange: function (fieldId) {
                    self.fireEvent(BI.TableFieldWithSearchPane.EVENT_RELATION_CHANGE, fieldId);
                },
                onTranslationsChange: function (translations) {
                    tableInfo.translations = translations;
                    table.populate(tableInfo);
                    self.fireEvent(BI.TableFieldWithSearchPane.EVENT_TRANSLATION_CHANGE, translations);
                }
            }
        });

        var table = BI.createWidget({
            type: "bi.table_field_info"
        });
        table.on(BI.TableFieldInfo.EVENT_USABLE_CHANGE, function (usedFields) {
            self.fireEvent(BI.TableFieldWithSearchPane.EVENT_USABLE_CHANGE, usedFields);
        });
        table.on(BI.TableFieldInfo.EVENT_TRANSLATION_CHANGE, function (translations) {
            self.fireEvent(BI.TableFieldWithSearchPane.EVENT_TRANSLATION_CHANGE, translations);
        });
        table.on(BI.TableFieldInfo.EVENT_RELATION_CHANGE, function (fieldId) {
            self.fireEvent(BI.TableFieldWithSearchPane.EVENT_RELATION_CHANGE, fieldId);
        });
        table.populate(tableInfo);
        searcher.setAdapter(table);
        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: searcher,
                height: 40
            }, {
                el: table,
                height: "fill"
            }]
        })
    }
});
BI.TableFieldWithSearchPane.EVENT_USABLE_CHANGE = "EVENT_USABLE_CHANGE";
BI.TableFieldWithSearchPane.EVENT_RELATION_CHANGE = "EVENT_RELATION_CHANGE";
BI.TableFieldWithSearchPane.EVENT_TRANSLATION_CHANGE = "EVENT_TRANSLATION_CHANGE";
$.shortcut("bi.table_field_info_with_search_pane", BI.TableFieldWithSearchPane);