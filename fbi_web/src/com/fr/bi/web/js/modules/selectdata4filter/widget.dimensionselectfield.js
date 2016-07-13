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

    _defaultConfig: function () {
        return BI.extend(BI.DimensionFilterSelectField.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-filter-select-field"
        })
    },

    _init: function () {
        BI.DimensionFilterSelectField.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.service = BI.createWidget({
            type: "bi.simple_select_data_service",
            element: this.element,
            isDefaultInit: true,
            tablesCreator: function () {
                return [{
                    id: self._constant.DIMENSION_FIELD,
                    type: "bi.dimension_select_data_level0_node",
                    text: BI.i18nText("BI-Dimension"),
                    value: BI.i18nText("BI-Dimension"),
                    isParent: true,
                    fontType: BI.DimensionSelectDataLevel0Node.CLASSIFY,
                    open: true
                }, {
                    id: self._constant.TARGET_FIELD,
                    type: "bi.dimension_select_data_level0_node",
                    text: BI.i18nText("BI-Target"),
                    value: BI.i18nText("BI-Target"),
                    isParent: true,
                    fontType: BI.DimensionSelectDataLevel0Node.SERIES,
                    open: true
                }]
            },
            fieldsCreator: function (tableId) {
                if (self._constant.DIMENSION_FIELD === tableId) {
                    var fieldId = BI.Utils.getFieldIDByDimensionID(o.dId);
                    return [{
                        id: fieldId,
                        pId: self._constant.DIMENSION_FIELD,
                        type: "bi.select_data_level0_item",
                        fieldType: BI.Utils.getFieldTypeByID(fieldId),
                        text: BI.Utils.getFieldNameByID(fieldId),
                        title: BI.Utils.getFieldNameByID(fieldId),
                        value: o.dId
                    }]
                } else {
                    var widgetId = BI.Utils.getWidgetIDByDimensionID(o.dId);
                    var allTargets = BI.Utils.getAllTargetDimensionIDs(widgetId);
                    var result = [];
                    BI.each(allTargets, function (i, tId) {
                        result.push({
                            id: BI.Utils.getFieldIDByDimensionID(tId),
                            pId: self._constant.TARGET_FIELD,
                            type: "bi.select_data_level0_item",
                            fieldType: BI.Utils.getFieldTypeByDimensionID(tId),
                            text: BI.Utils.getDimensionNameByID(tId),
                            title: BI.Utils.getDimensionNameByID(tId),
                            value: tId
                        });
                    });
                    return result;
                }
            }
        });
        this.service.on(BI.SimpleSelectDataService.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.DimensionFilterSelectField.EVENT_CLICK_ITEM, arguments);
        });
    }
});
BI.DimensionFilterSelectField.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.dimension_filter_select_field", BI.DimensionFilterSelectField);