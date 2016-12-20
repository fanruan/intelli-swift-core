/**
 * @class BI.DimensionDateCombo
 * @extend BI.AbstractDimensionCombo
 *
 */
BI.DimensionDateCombo = BI.inherit(BI.AbstractDimensionCombo, {

    config: {
        ASCEND: BICst.DIMENSION_DATE_COMBO.ASCEND,
        DESCEND: BICst.DIMENSION_DATE_COMBO.DESCEND,
        DATE: BICst.DIMENSION_DATE_COMBO.DATE,
        YEAR: BICst.DIMENSION_DATE_COMBO.YEAR,
        QUARTER: BICst.DIMENSION_DATE_COMBO.QUARTER,
        MONTH: BICst.DIMENSION_DATE_COMBO.MONTH,
        WEEK: BICst.DIMENSION_DATE_COMBO.WEEK,
        POSITION_BY_ADDRESS: BICst.DIMENSION_DATE_COMBO.ADDRESS,
        POSITION_BY_LNG_LAT: BICst.DIMENSION_DATE_COMBO.LNG_LAT,
        POSITION_BY_LNG: BICst.DIMENSION_DATE_COMBO.LNG,
        POSITION_BY_LAT: BICst.DIMENSION_DATE_COMBO.LAT
    },

    defaultItems: function () {
        return [
            [{
                el: {
                    text: BI.i18nText("BI-Ascend"),
                    value: BICst.DIMENSION_DATE_COMBO.ASCEND,
                    iconCls1: "dot-e-font"
                },
                children: []
            }, {
                el: {
                    text: BI.i18nText("BI-Descend"),
                    value: BICst.DIMENSION_DATE_COMBO.DESCEND,
                    iconCls1: "dot-e-font"
                },
                children: []
            }],
            [{
                text: BI.i18nText("BI-Date"),
                value: BICst.DIMENSION_DATE_COMBO.DATE,
                cls: "dot-e-font"
            }, {
                text: BI.i18nText("BI-Year_Fen"),
                value: BICst.DIMENSION_DATE_COMBO.YEAR,
                cls: "dot-e-font"
            }, {
                text: BI.i18nText("BI-Quarter"),
                value: BICst.DIMENSION_DATE_COMBO.QUARTER,
                cls: "dot-e-font"
            }, {
                text: BI.i18nText("BI-Month_Fen"),
                value: BICst.DIMENSION_DATE_COMBO.MONTH,
                cls: "dot-e-font"
            }, {
                text: BI.i18nText("BI-Week_XingQi"),
                value: BICst.DIMENSION_DATE_COMBO.WEEK,
                cls: "dot-e-font"
            }],
            [{
                text: BI.i18nText("BI-Show_Qualified_Result"),
                title: BI.i18nText("BI-Dimension_Filter_Tip"),
                value: BICst.DIMENSION_DATE_COMBO.FILTER,
                cls: "filter-h-font"
            }],
            [{
                text: BI.i18nText("BI-Math_Relationships"),
                value: BICst.DIMENSION_DATE_COMBO.DT_RELATION,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Show_Field"),
                value: BICst.DIMENSION_DATE_COMBO.SHOW_FIELD,
                cls: BI.Utils.isDimensionUsable(this.options.dId) ? "widget-combo-show-title-font" : ""
            }],
            [{
                text: BI.i18nText("BI-Rename"),
                value: BICst.DIMENSION_DATE_COMBO.RENAME
            }],
            [{
                text: BI.i18nText("BI-Copy"),
                value: BICst.DIMENSION_DATE_COMBO.COPY,
                cls: "copy-h-font"
            }],
            [{
                text: BI.i18nText("BI-Remove"),
                value: BICst.DIMENSION_DATE_COMBO.DELETE,
                cls: "delete-h-font"
            }],
            [{
                text: BI.i18nText("BI-Dimension_From"),
                value: BICst.DIMENSION_DATE_COMBO.INFO,
                tipType: "success",
                cls: "dimension-from-font",
                disabled: true
            }]
        ]
    },

    _rebuildItems: function () {
        var chartTypes = [
            BICst.WIDGET.ACCUMULATE_AREA,
            BICst.WIDGET.ACCUMULATE_AXIS,
            BICst.WIDGET.ACCUMULATE_BAR,
            BICst.WIDGET.ACCUMULATE_RADAR,
            BICst.WIDGET.PERCENT_ACCUMULATE_AREA,
            BICst.WIDGET.PERCENT_ACCUMULATE_AXIS,
            BICst.WIDGET.COMBINE_CHART,
            // BICst.WIDGET.MULTI_AXIS_COMBINE_CHART
        ];
        var items = BI.DimensionDateCombo.superclass._rebuildItems.apply(this, arguments), o = this.options;
        var rType = BI.Utils.getRegionTypeByDimensionID(o.dId);
        var wType = BI.Utils.getWidgetTypeByID(BI.Utils.getWidgetIDByDimensionID(o.dId));
        if(BI.Utils.isDimensionRegion2ByRegionType(rType) && BI.contains(chartTypes, wType)) {
            items.splice(2, 0, [{
                el: {
                    text: BI.i18nText("BI-Series_Accumulation_Attribute"),
                    cls: "",
                    value: BICst.DIMENSION_DATE_COMBO.SERIES_ACCUMULATION_ATTRIBUTE
                },
                children:[{
                    text: BI.i18nText("BI-No_Accumulation"),
                    value: BICst.DIMENSION_DATE_COMBO.NO_SERIES,
                    cls: "dot-e-font"
                },{
                    text: BI.i18nText("BI-Series_Accumulation"),
                    value: BICst.DIMENSION_DATE_COMBO.SERIES_ACCUMULATION,
                    cls: "dot-e-font"
                }]
            }]);
        }
        return items
    },

    typeConfig: function () {
        return this.config;
    },

    _assertSort: function (val) {
        val || (val = {});
        val.type || (val.type = BICst.SORT.ASC);
        val.sort_target || (val.sort_target = this.options.dId);
        return val;
    },

    _assertGroup: function (val) {
        val || (val = {});
        val.type || (val.type = BICst.GROUP.NO_GROUP);
        return val;
    },

    _assertAddress: function (val) {
        val || (val = {});
        if (BI.isNull(val.type)) {
            val.type = BICst.GIS_POSITION_TYPE.LNG_FIRST
        }
        return val;
    },

    _defaultConfig: function () {
        return BI.extend(BI.DimensionDateCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-date-combo"
        })
    },

    _init: function () {
        BI.DimensionDateCombo.superclass._init.apply(this, arguments);
    }
});
$.shortcut("bi.dimension_date_combo", BI.DimensionDateCombo);