/**
 * @class BI.DimensionDateComboShow
 * @extend BI.AbstractDimensionCombo
 *
 */
BI.DimensionDateComboShow = BI.inherit(BI.AbstractDimensionCombo, {

    config: {
        ASCEND: BICst.DIMENSION_DATE_COMBO.ASCEND,
        DESCEND: BICst.DIMENSION_DATE_COMBO.DESCEND,
        DATE: BICst.DIMENSION_DATE_COMBO.DATE,
        YEAR: BICst.DIMENSION_DATE_COMBO.YEAR,
        QUARTER: BICst.DIMENSION_DATE_COMBO.QUARTER,
        MONTH: BICst.DIMENSION_DATE_COMBO.MONTH,
        WEEK: BICst.DIMENSION_DATE_COMBO.WEEK
    },

    defaultItems: function () {
        return [
            [{
                text: BI.i18nText("BI-Date"),
                value: BICst.DIMENSION_DATE_COMBO.DATE,
                iconCls1: ""
            }, {
                text: BI.i18nText("BI-Year_Fen"),
                value: BICst.DIMENSION_DATE_COMBO.YEAR,
                iconCls1: ""
            }, {
                text: BI.i18nText("BI-Quarter"),
                value: BICst.DIMENSION_DATE_COMBO.QUARTER,
                cls: ""
            }, {
                text: BI.i18nText("BI-Month_Fen"),
                value: BICst.DIMENSION_DATE_COMBO.MONTH,
                cls: ""
            }, {
                text: BI.i18nText("BI-Week_XingQi"),
                value: BICst.DIMENSION_DATE_COMBO.WEEK,
                cls: ""
            }],
            [{
                el: {
                    text: BI.i18nText("BI-Ascend"),
                    value: BICst.DIMENSION_DATE_COMBO.ASCEND,
                    cls: ""
                },
                children: []
            }, {
                el: {
                    text: BI.i18nText("BI-Descend"),
                    value: BICst.DIMENSION_DATE_COMBO.DESCEND,
                    cls: ""
                },
                children: []
            }],
            [{
                text: BI.i18nText("BI-Show_Qualified_Result"),
                value: BICst.DIMENSION_DATE_COMBO.FILTER,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Dimension_From"),
                value: BICst.DIMENSION_DATE_COMBO.INFO,
                cls: "",
                disabled: true
            }]
        ]
    },

    typeConfig: function () {
        return this.config;
    },

    _defaultConfig: function () {
        return BI.extend(BI.DimensionDateComboShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-date-combo"
        })
    },

    _init: function () {
        BI.DimensionDateComboShow.superclass._init.apply(this, arguments);
    }
});
BI.DimensionDateComboShow.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.dimension_date_combo_show", BI.DimensionDateComboShow);
