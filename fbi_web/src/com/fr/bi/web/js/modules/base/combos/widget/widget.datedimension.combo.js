/**
 * @class BI.DimensionDateCombo
 * @extend BI.AbstractDimensionCombo
 *
 */
BI.DimensionDateCombo = BI.inherit(BI.AbstractDimensionCombo, {

    config : {
        ASCEND : BICst.DIMENSION_DATE_COMBO.ASCEND,
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
                el:{
                    text: BI.i18nText("BI-Ascend"),
                    value: BICst.DIMENSION_DATE_COMBO.ASCEND,
                    cls: ""
                },
                children:[]
            }, {
                el:{
                    text: BI.i18nText("BI-Descend"),
                    value: BICst.DIMENSION_DATE_COMBO.DESCEND,
                    cls: ""
                },
                children:[]
            }],
            [{
                text: BI.i18nText("BI-Show_Qualified_Result"),
                value: BICst.DIMENSION_DATE_COMBO.FILTER,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Math_Relationships"),
                value: BICst.DIMENSION_DATE_COMBO.DT_RELATION,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Copy"),
                value: BICst.DIMENSION_DATE_COMBO.COPY,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Remove"),
                value: BICst.DIMENSION_DATE_COMBO.DELETE,
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

    typeConfig: function(){
        return this.config;
    },

    _assertSort: function (val) {
        val || (val = {});
        val.type || (val.type = BICst.SORT.ASC);
        val.sort_target || (val.sort_target = this.options.dId);
        return val;
    },

    _assertGroup:function(val){
        val || (val = {});
        val.type || (val.type = BICst.GROUP.NO_GROUP);
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
BI.DimensionDateCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.dimension_date_combo", BI.DimensionDateCombo);