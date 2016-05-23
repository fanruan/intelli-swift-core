/**
 * @class BI.DimensionStringCombo
 * @extend BI.AbstractDimensionCombo
 * 字段类型string
 */
BI.DimensionStringCombo = BI.inherit(BI.AbstractDimensionCombo, {

    config : {
        ASCEND : BICst.DIMENSION_STRING_COMBO.ASCEND,
        DESCEND: BICst.DIMENSION_STRING_COMBO.DESCEND,
        NOT_SORT : BICst.DIMENSION_STRING_COMBO.NOT_SORT,
        SORT_BY_CUSTOM : BICst.DIMENSION_STRING_COMBO.SORT_BY_CUSTOM,
        GROUP_BY_VALUE : BICst.DIMENSION_STRING_COMBO.GROUP_BY_VALUE,
        GROUP_BY_CUSTOM : BICst.DIMENSION_STRING_COMBO.GROUP_BY_CUSTOM
    },

    defaultItems: function () {
        return [
            [{
                el: {
                    text: BI.i18nText("BI-Ascend"),
                    value: BICst.DIMENSION_STRING_COMBO.ASCEND,
                    iconCls1: ""
                },
                children: []
            }, {
                el: {
                    text: BI.i18nText("BI-Descend"),
                    value: BICst.DIMENSION_STRING_COMBO.DESCEND,
                    iconCls1: ""
                },
                children: []
            }, {
                text: BI.i18nText("BI-Custom_Sort_Dot"),
                value: BICst.DIMENSION_STRING_COMBO.SORT_BY_CUSTOM,
                cls: "dot-e-font"
            }],
            [{
                text: BI.i18nText("BI-Same_Value_A_Group"),
                value: BICst.DIMENSION_STRING_COMBO.GROUP_BY_VALUE,
                cls: "dot-e-font"
            }, {
                text: BI.i18nText("BI-Custom_Grouping_Dot"),
                value: BICst.DIMENSION_STRING_COMBO.GROUP_BY_CUSTOM,
                cls: "dot-e-font"
            }],
            [{
                text: BI.i18nText("BI-Show_Qualified_Result"),
                value: BICst.DIMENSION_STRING_COMBO.FILTER,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Math_Relationships"),
                value: BICst.DIMENSION_STRING_COMBO.DT_RELATION,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Copy"),
                value: BICst.DIMENSION_STRING_COMBO.COPY,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Remove"),
                value: BICst.DIMENSION_STRING_COMBO.DELETE,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Dimension_From"),
                tipType: "warning",
                value: BICst.DIMENSION_STRING_COMBO.INFO,
                cls: "",
                disabled: true
            }]
        ]
    },

    typeConfig: function(){
        return this.config;
    },

    _defaultConfig: function () {
        return BI.extend(BI.DimensionStringCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-string-combo"
        })
    },

    _init: function () {
        BI.DimensionStringCombo.superclass._init.apply(this, arguments);
    },

    _assertGroup: function(val){
        val || (val = {});
        if(BI.isNull(val.type)){
            val.type = BICst.GROUP.ID_GROUP;
        }
        return val;
    },

    _assertSort: function(val){
        val || (val = {});
        if(BI.isNull(val.type)){
            val.type = BICst.SORT.ASC;
        }
        val.sort_target || (val.sort_target = this.options.dId);
        return val;
    }
});
$.shortcut("bi.dimension_string_combo", BI.DimensionStringCombo);