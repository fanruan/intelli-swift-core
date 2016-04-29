/**
 * @class BI.ConfFilterSelectSingleField
 * @extend BI.Widget
 * 选择表中单个字段(提供搜索)
 */
BI.ConfFilterSelectSingleField = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ConfFilterSelectSingleField.superclass._defaultConfig.apply(this, arguments), {
            table: {}
        })
    },

    _init: function () {
        BI.ConfFilterSelectSingleField.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        o.fields = o.table.fields[0];
        this.fieldArray = o.fields;
        this.searcher = BI.createWidget({
            type: "bi.simple_select_data_searcher",
            element: this.element,
            itemsCreator: function (op, populate) {
                if (BI.isNotNull(op.keyword) && BI.isNotNull(op.searchType)) {
                    var type = op.searchType, keyword = op.keyword;
                    var searchResult = [], matchResult = [];
                    //选择了表
                    if (type & BI.SelectDataSearchSegment.SECTION_TABLE) {
                        var result = [];
                        BI.each(self.fieldArray, function (i, field) {
                            if (!field.pId) {
                                result.push(field);
                            }
                        });
                        result = BI.Func.getSearchResult(result, keyword);
                        searchResult = result.finded;
                        matchResult = result.matched;
                        //把表字段收起
                        BI.each(searchResult, function (i, field) {
                            if (BI.isNotNull(field.open)) {
                                field.open = false;
                            }
                        });
                    } else {
                        var result = [], map = [];
                        BI.each(self.fieldArray, function (i, field) {
                            if (BI.isNotNull(field.pId)) {
                                result.push(field);
                            }
                        });
                        result = BI.Func.getSearchResult(result, keyword);
                        BI.each(result.finded, function (j, finded) {
                            if (!map[finded.pId]) {
                                searchResult.push({
                                    id: finded.pId,
                                    type: "bi.select_data_level"+ BICst.BUSINESS_TABLE_TYPE.NORMAL +"_node",
                                    text: o.table.table_name,
                                    value: finded.pId,
                                    isParent: true,
                                    open: true
                                });
                                map[finded.pId] = true;
                            }
                        });
                        searchResult = searchResult.concat(result.finded);
                        matchResult = result.matched;
                    }

                    populate(searchResult, matchResult);
                }
            }
        });
        this.searcher.on(BI.SimpleSelectDataSearcher.EVENT_CHANGE, function () {
            self.fireEvent(BI.ConfFilterSelectSingleField.EVENT_CHANGE, self.searcher.getValue());
        });
        this.searcher.on(BI.SimpleSelectDataSearcher.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.ConfFilterSelectSingleField.EVENT_CLICK_ITEM, arguments);
        });
        this.populate(this.fieldArray);
    },

    _getFieldsStructureByFields: function (fields) {
        var fieldStructure = [], o = this.options;
        var tableName = o.table.table_name, tId = o.table.id;
        fieldStructure.push({
            id: tId,
            type: "bi.select_data_level"+ BICst.BUSINESS_TABLE_TYPE.NORMAL +"_node",
            text: tableName,
            value: tId,
            open: true
        });
        BI.each(fields, function (i, field) {
            var fId = BI.UUID();
            fieldStructure.push({
                id: fId,
                pId: tId,
                type: "bi.select_data_level0_item",
                fieldType: field["field_type"],
                text: field["field_name"],
                value: {
                    field: field,
                    table: o.table
                }
            })
        });
        return fieldStructure;
    },

    stopSearch: function(){
        this.searcher.stopSearch();
    },

    populate: function (fields) {
        this.fieldArray = this._getFieldsStructureByFields(fields);
        this.searcher.populate(this.fieldArray);
    }
});
BI.ConfFilterSelectSingleField.EVENT_CHANGE = "EVENT_CHANGE";
BI.ConfFilterSelectSingleField.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.conf_filter_select_single_field", BI.ConfFilterSelectSingleField);