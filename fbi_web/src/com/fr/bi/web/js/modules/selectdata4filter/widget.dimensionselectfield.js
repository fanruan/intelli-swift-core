/**
 * @class BI.DimensionFilterSelectField
 * @extend BI.Widget
 * 维度过滤选择字段——当前维度字段和所有指标除记录数字段
 */
BI.DimensionFilterSelectField = BI.inherit(BI.Widget, {

    _constant: {
        DIMENSION_FIELD: 1,
        TARGET_FIELD: 2
    },

    _defaultConfig: function(){
        return BI.extend(BI.DimensionFilterSelectField.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-filter-select-field"
        })
    },

    _init: function(){
        BI.DimensionFilterSelectField.superclass._init.apply(this, arguments);
        var self = this;
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
                        BI.each(result.matched.concat(result.finded), function (j, finded) {
                            if (!map[finded.pId]) {
                                var name = (finded.pId === self._constant.DIMENSION_FIELD ? BI.i18nText("BI-Dimension") : BI.i18nText("BI-Target"));
                                searchResult.push({
                                    id: finded.pId,
                                    type: "bi.dimension_select_data_level0_node",
                                    text: name,
                                    value: name,
                                    fontType: finded.pId === self._constant.DIMENSION_FIELD ? BI.DimensionSelectDataLevel0Node.CLASSIFY : BI.DimensionSelectDataLevel0Node.SERIES,
                                    isParent: true,
                                    open: true
                                });
                                map[finded.pId] = true;
                            }
                        });
                        searchResult = searchResult.concat(result.matched.concat(result.finded));
                        matchResult = result.matched;
                    }
                    populate(searchResult, matchResult);
                }else{

                }
            }
        });
        this.searcher.on(BI.SimpleSelectDataSearcher.EVENT_CHANGE, function () {
            self.fireEvent(BI.DimensionFilterSelectField.EVENT_CHANGE, self.searcher.getValue());
        });
        this.searcher.on(BI.SimpleSelectDataSearcher.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.DimensionFilterSelectField.EVENT_CLICK_ITEM, arguments);
        });
        this.populate();
    },

    _getFieldsStructureByTargetId: function (dId) {
        var self = this, fieldStructure = [];
        var fieldId = BI.Utils.getFieldIDByDimensionID(dId);
        fieldStructure.push({
            id: this._constant.DIMENSION_FIELD,
            type: "bi.dimension_select_data_level0_node",
            text: BI.i18nText("BI-Dimension"),
            value: BI.i18nText("BI-Dimension"),
            isParent: true,
            fontType: BI.DimensionSelectDataLevel0Node.CLASSIFY,
            open: true
        });
        fieldStructure.push({
            id: fieldId,
            pId: this._constant.DIMENSION_FIELD,
            type: "bi.select_data_level0_item",
            fieldType: BI.Utils.getFieldTypeByID(fieldId),
            text: BI.Utils.getFieldNameByID(fieldId),
            value: dId
        });
        fieldStructure.push({
            id: this._constant.TARGET_FIELD,
            type: "bi.dimension_select_data_level0_node",
            text: BI.i18nText("BI-Target"),
            value: BI.i18nText("BI-Target"),
            isParent: true,
            fontType: BI.DimensionSelectDataLevel0Node.SERIES,
            open: true
        });
        var widgetId = BI.Utils.getWidgetIDByDimensionID(dId);
        var allTargets = BI.Utils.getAllTargetDimensionIDs(widgetId);
        BI.each(allTargets, function(i, tId){
            var field_id = BI.Utils.getFieldIDByDimensionID(tId);
            fieldStructure.push({
                id: tId,
                pId: self._constant.TARGET_FIELD,
                type: "bi.select_data_level0_item",
                fieldType: BI.Utils.getFieldTypeByID(field_id),
                text: BI.Utils.getDimensionNameByID(tId),
                value: tId
            });
        });
        return fieldStructure;
    },

    populate: function () {
        this.fieldArray = this._getFieldsStructureByTargetId(this.options.dId);
        this.searcher.populate(this.fieldArray);
    }
});
BI.DimensionFilterSelectField.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
BI.DimensionFilterSelectField.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.dimension_filter_select_field", BI.DimensionFilterSelectField);