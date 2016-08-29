/**
 * Created by fay on 2016/8/1.
 */
BI.BubbleFilterSelectField = BI.inherit(BI.Widget, {
    _constant: {
        DIMENSION_FIELD: 1,
        X_Y_FIELD: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.BubbleFilterSelectField.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-data-label-filter-select-field"
        });
    },

    _init: function () {
        BI.BubbleFilterSelectField.superclass._init.apply(this, arguments);
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
                    id: self._constant.X_Y_FIELD,
                    type: "bi.dimension_select_data_level0_node",
                    text: BI.i18nText("BI-Uppercase_X_Axis") + "/" + BI.i18nText("BI-Uppercase_Y_Axis"),
                    value: BI.i18nText("BI-Uppercase_X_Axis") + "/" + BI.i18nText("BI-Uppercase_Y_Axis"),
                    isParent: true,
                    fontType: BI.DimensionSelectDataLevel0Node.CLASSIFY,
                    open: true
                }]
            },
            fieldsCreator: function (tableId) {
                var categories = BI.Utils.getAllUsableDimDimensionIDs(BI.Utils.getWidgetIDByDimensionID(o.dId));
                var result = [];
                if (tableId === self._constant.DIMENSION_FIELD) {
                    BI.each(categories, function (i, dId) {
                        if (!BI.Utils.isDimensionUsable(dId)) {
                            return;
                        }
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
                } else {
                    result.push({
                        id: BICst.DATACOLUMN.XANDYANDSIZE,
                        pId: self._constant.X_Y_FIELD,
                        type: "bi.select_data_level0_item",
                        text: BI.i18nText("BI-Uppercase_X_Axis") + BI.i18nText("BI-And") + BI.i18nText("BI-Uppercase_Y_Axis") + BI.i18nText("BI-And") + BI.i18nText("BI-Bubble_Size"),
                        title: BI.i18nText("BI-Uppercase_X_Axis") + BI.i18nText("BI-And") + BI.i18nText("BI-Uppercase_Y_Axis") + BI.i18nText("BI-And") + BI.i18nText("BI-Bubble_Size"),
                        fieldType: BICst.DATACOLUMN.XANDYANDSIZE,
                        value: BICst.DATACOLUMN.XANDYANDSIZE
                    },{
                        id: BICst.DATACOLUMN.XANDY,
                        pId: self._constant.X_Y_FIELD,
                        type: "bi.select_data_level0_item",
                        text: BI.i18nText("BI-Uppercase_X_Axis") + BI.i18nText("BI-And") + BI.i18nText("BI-Uppercase_Y_Axis"),
                        title: BI.i18nText("BI-Uppercase_X_Axis") + BI.i18nText("BI-And") + BI.i18nText("BI-Uppercase_Y_Axis"),
                        fieldType: BICst.DATACOLUMN.XANDY,
                        value: BICst.DATACOLUMN.XANDY
                    },{
                        id: BICst.DATACOLUMN.X,
                        pId: self._constant.X_Y_FIELD,
                        type: "bi.select_data_level0_item",
                        fieldType: BICst.DATACOLUMN.X,
                        text: BI.i18nText("BI-Uppercase_X_Axis"),
                        title: BI.i18nText("BI-Uppercase_X_Axis"),
                        value: BICst.DATACOLUMN.X
                    },{
                        id: BICst.DATACOLUMN.Y,
                        pId: self._constant.X_Y_FIELD,
                        type: "bi.select_data_level0_item",
                        fieldType: BICst.DATACOLUMN.Y,
                        text: BI.i18nText("BI-Uppercase_Y_Axis"),
                        title: BI.i18nText("BI-Uppercase_Y_Axis"),
                        value: BICst.DATACOLUMN.Y
                    },{
                        id: BICst.DATACOLUMN.Z,
                        pId: self._constant.X_Y_FIELD,
                        type: "bi.select_data_level0_item",
                        fieldType: BICst.DATACOLUMN.Z,
                        text: BI.i18nText("BI-Bubble_Size"),
                        title: BI.i18nText("BI-Bubble_Size"),
                        value: BICst.DATACOLUMN.Z
                    });
                }
                return result;
            }
        });
        this.service.on(BI.SimpleSelectDataService.EVENT_CLICK_ITEM, function () {
            self.fireEvent(BI.BubbleFilterSelectField.EVENT_CLICK_ITEM, arguments);
        });
    }
});
BI.BubbleFilterSelectField.EVENT_CLICK_ITEM = "EVENT_CLICK_ITEM";
$.shortcut("bi.bubble_filter_select_field", BI.BubbleFilterSelectField);