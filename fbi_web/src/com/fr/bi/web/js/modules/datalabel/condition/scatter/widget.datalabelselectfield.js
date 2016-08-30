/**
 * Created by fay on 2016/8/1.
 */
BI.ScatterFilterSelectField = BI.inherit(BI.Widget, {
    _constant: {
        DIMENSION_FIELD: 1,
        X_Y_FIELD: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.ScatterFilterSelectField.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-data-label-filter-select-field"
        });
    },

    _init: function () {
        BI.ScatterFilterSelectField.superclass._init.apply(this, arguments);
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
                    fontType: BI.DimensionSelectDataLevel0Node.SERIES,
                    open: true
                }, {
                    id: BICst.DATACOLUMN.XANDY,
                    type: "bi.select_data_level0_item",
                    text: BI.i18nText("BI-Uppercase_X_Axis") + BI.i18nText("BI-And") + BI.i18nText("BI-Uppercase_Y_Axis"),
                    title: BI.i18nText("BI-Uppercase_X_Axis") + BI.i18nText("BI-And") + BI.i18nText("BI-Uppercase_Y_Axis"),
                    fieldType: BICst.DATACOLUMN.XANDY,
                    value: BICst.DATACOLUMN.XANDY,
                    isParent: false
                }, {
                    id: BICst.DATACOLUMN.X,
                    type: "bi.select_data_level0_item",
                    text: BI.i18nText("BI-Uppercase_X_Axis"),
                    title: BI.i18nText("BI-Uppercase_X_Axis"),
                    fieldType: BICst.DATACOLUMN.X,
                    value: BICst.DATACOLUMN.X,
                    isParent: false
                }, {
                    id: BICst.DATACOLUMN.Y,
                    type: "bi.select_data_level0_item",
                    text: BI.i18nText("BI-Uppercase_Y_Axis"),
                    title: BI.i18nText("BI-Uppercase_Y_Axis"),
                    fieldType: BICst.DATACOLUMN.Y,
                    value: BICst.DATACOLUMN.Y,
                    isParent: false
                }]
            },
            fieldsCreator: function () {
                var categories = BI.Utils.getAllUsableDimDimensionIDs(BI.Utils.getWidgetIDByDimensionID(o.dId));
                var result = [];
                BI.each(categories, function (i, dId) {
                    result.push({
                        id: dId,
                        pId: self._constant.DIMENSION_FIELD,
                        type: "bi.select_data_level0_item",
                        fieldType: BI.Utils.getFieldTypeByDimensionID(dId),
                        text: BI.Utils.getDimensionNameByID(dId),
                        title: BI.Utils.getDimensionNameByID(dId),
                        value: dId
                    });
                });
                return result;
            }
        });
        this.service.on(BI.SimpleSelectDataService.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.ScatterFilterSelectField.EVENT_CLICK_ITEM, arguments);
        });
    }
});
BI.ScatterFilterSelectField.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.scatter_filter_select_field", BI.ScatterFilterSelectField);