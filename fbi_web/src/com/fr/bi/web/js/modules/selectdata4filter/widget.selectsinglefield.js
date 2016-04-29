/**
 * @class BI.SelectSingleField
 * @extend BI.Widget
 * 选择表中单个字段(提供搜索)
 */
BI.SelectSingleField = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectSingleField.superclass._defaultConfig.apply(this, arguments), {
            dId: ""
        })
    },

    _init: function () {
        BI.SelectSingleField.superclass._init.apply(this, arguments);
        var self = this;
        var fieldArray = this.fieldArray = this._getFieldsStructureByTargetId(this.options.dId);
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
                        BI.each(fieldArray, function (i, field) {
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
                        BI.each(fieldArray, function (i, field) {
                            if (BI.isNotNull(field.pId)) {
                                result.push(field);
                            }
                        });
                        result = BI.Func.getSearchResult(result, keyword);
                        BI.each(result.matched.concat(result.finded), function (j, finded) {
                            if (!map[finded.pId]) {
                                searchResult.push({
                                    id: finded.pId,
                                    type: "bi.select_data_level0_node",
                                    text: BI.Utils.getTableNameByID(finded.pId),
                                    value: finded.pId,
                                    isParent: true,
                                    open: true
                                })
                                map[finded.pId] = true;
                            }
                        });
                        searchResult = searchResult.concat(result.matched.concat(result.finded));
                        matchResult = result.matched;
                    }

                    populate(searchResult, matchResult);
                }
            }
        });
        this.searcher.on(BI.SimpleSelectDataSearcher.EVENT_CHANGE, function () {
            self.fireEvent(BI.SelectSingleField.EVENT_CHANGE, self.searcher.getValue());
        });
        this.searcher.on(BI.SimpleSelectDataSearcher.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.SelectSingleField.EVENT_CLICK_ITEM, arguments);
        });
        this.populate();
    },

    _getFieldsStructureByTargetId: function (dId) {
        var self = this, field = BI.Utils.getFieldNameByID(BI.Utils.getFieldIDByDimensionID(dId));
        var tableId = field.id;
        var fieldStructure = [];
        //string, number
        BI.each(BI.Utils.getFieldIDsOfTableID(tableId), function (i, fid) {
            var fname = BI.Utils.getFieldNameByID(fid);
            fieldStructure.push({
                id: fid,
                pId: tableId,
                type: "bi.select_data_level0_item",
                fieldType: BI.Utils.getFieldTypeByID(fid),
                text: fname,
                value: fid
            })
        });
        var tableName = BI.Utils.getTableNameByID(tableId);
        fieldStructure.push({
            id: tableId,
            type: "bi.select_data_level0_node",
            text: tableName,
            value: tableId,
            open: true
        });
        return fieldStructure;
    },

    populate: function () {
        this.searcher.populate(this.fieldArray);
    }
});
BI.SelectSingleField.EVENT_CHANGE = "EVENT_CHANGE";
BI.SelectSingleField.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.select_single_field", BI.SelectSingleField);