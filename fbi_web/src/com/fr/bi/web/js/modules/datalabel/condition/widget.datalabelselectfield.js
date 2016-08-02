/**
 * Created by fay on 2016/8/1.
 */
BI.DataLabelFilterSelectField = BI.inherit(BI.Widget, {
    _constant: {
        SELF_FIELD: 1,
        DIMENSION_FIELD: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.DataLabelFilterSelectField.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-data-label-filter-select-field"
        });
    },

    _init: function () {
        BI.DataLabelFilterSelectField.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.service = BI.createWidget({
            type: "bi.simple_select_data_service",
            element: this.element,
            isDefaultInit: true,
            tablesCreator: function () {
                return [{
                    id: self._constant.SELF_FIELD,
                    type: "bi.dimension_select_data_level0_node",
                    text: "自身",
                    value: "自身",
                    isParent: true,
                    fontType: BI.DimensionSelectDataLevel0Node.CLASSIFY,
                    open: true
                }, {
                    id: self._constant.DIMENSION_FIELD,
                    type: "bi.dimension_select_data_level0_node",
                    text: BI.i18nText("BI-Dimension"),
                    value: BI.i18nText("BI-Dimension"),
                    isParent: true,
                    fontType: BI.DimensionSelectDataLevel0Node.SERIES,
                    open: true
                }]
            },
            fieldsCreator: function (tableId) {
                if (self._constant.SELF_FIELD === tableId) {
                    var fieldId = BI.Utils.getFieldIDByDimensionID(o.dId);
                    return [{
                        id: fieldId,
                        pId: self._constant.SELF_FIELD,
                        type: "bi.select_data_level0_item",
                        fieldType: BI.Utils.getFieldTypeByID(fieldId),
                        text: "自身",
                        title: "自身",
                        value: fieldId
                    }]
                } else {
                    var widgetId = BI.Utils.getWidgetIDByDimensionID(o.dId);
                    var allDimensions = BI.Utils.getAllDimDimensionIDs(widgetId);
                    var result = [];
                    BI.each(allDimensions, function (i, dId) {
                        result.push({
                            id: BI.Utils.getFieldIDByDimensionID(dId),
                            pId: self._constant.DIMENSION_FIELD,
                            type: "bi.select_data_level0_item",
                            fieldType: BI.Utils.getFieldTypeByDimensionID(dId),
                            text: BI.Utils.getDimensionNameByID(dId),
                            title: BI.Utils.getDimensionNameByID(dId),
                            value: BI.Utils.getFieldIDByDimensionID(dId)
                        });
                    });
                    return result;
                }
            }
        });
        this.service.on(BI.SimpleSelectDataService.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.DataLabelFilterSelectField.EVENT_CLICK_ITEM, arguments);
        });
    }
});
BI.DataLabelFilterSelectField.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.data_label_filter_select_field", BI.DataLabelFilterSelectField);