/**
 * @class BI.DimensionNumberCombo
 * @extend BI.AbstractDimensionCombo
 * @abstract
 */
BI.DimensionNumberCombo = BI.inherit(BI.AbstractDimensionCombo, {

    constants: {
        customSortPos : 2
    },

    config:{
        ASCEND : BICst.DIMENSION_NUMBER_COMBO.ASCEND,
        DESCEND: BICst.DIMENSION_NUMBER_COMBO.DESCEND,
        SORT_BY_CUSTOM : BICst.DIMENSION_NUMBER_COMBO.SORT_BY_CUSTOM,
        GROUP_BY_VALUE : BICst.DIMENSION_NUMBER_COMBO.GROUP_BY_VALUE,
        GROUP_SETTING : BICst.DIMENSION_NUMBER_COMBO.GROUP_SETTING
    },

    _defaultConfig: function(){
        return BI.extend(BI.DimensionNumberCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-number-combo"
        })
    },

    _init: function(){
        BI.DimensionNumberCombo.superclass._init.apply(this, arguments);
    },

    _assertGroup: function(val){
        val || (val = {});
        if(BI.isNull(val.type)){
            val.type = BICst.GROUP.CUSTOM_NUMBER_GROUP;
        }
        return val;
    },

    _assertSort: function(val){
        val || (val = {});
        if(BI.isNull(val.type)){
            val.type = BICst.SORT.CUSTOM;
        }
        return val;
    },

    _rebuildItems :function(){
        var items = BI.DimensionStringCombo.superclass._rebuildItems.apply(this, arguments), o = this.options;
        var group = this._assertGroup(BI.Utils.getDimensionGroupByID(o.dId));
        var customSort = items[0][this.constants.customSortPos];
        group.type === BICst.GROUP.ID_GROUP ? customSort.disabled = true : customSort.disabled = false;
        return items;
    },

    defaultItems: function () {
        return [
            [{
                el: {
                    text: BI.i18nText("BI-Ascend"),
                    value: BICst.DIMENSION_NUMBER_COMBO.ASCEND,
                    iconCls1: ""
                },
                children: []
            }, {
                el: {
                    text: BI.i18nText("BI-Descend"),
                    value: BICst.DIMENSION_NUMBER_COMBO.DESCEND,
                    iconCls1: ""
                },
                children: []
            }, {
                text: BI.i18nText("BI-Custom_Sort_Dot"),
                value: BICst.DIMENSION_NUMBER_COMBO.SORT_BY_CUSTOM,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Same_Value_A_Group"),
                value: BICst.DIMENSION_NUMBER_COMBO.GROUP_BY_VALUE,
                cls: ""
            },{
                text: BI.i18nText("BI-Grouping_Setting"),
                value: BICst.DIMENSION_NUMBER_COMBO.GROUP_SETTING,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Show_Qualified_Result"),
                value: BICst.DIMENSION_NUMBER_COMBO.FILTER,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Math_Relationships"),
                value: BICst.DIMENSION_NUMBER_COMBO.DT_RELATION,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Copy"),
                value: BICst.DIMENSION_NUMBER_COMBO.COPY,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Remove"),
                value: BICst.DIMENSION_NUMBER_COMBO.DELETE,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Dimension_From"),
                value: BICst.DIMENSION_NUMBER_COMBO.INFO,
                cls: "",
                disabled: true
            }]
        ]
    },

    typeConfig: function(){
        return this.config;
    }
});
$.shortcut("bi.dimension_number_combo", BI.DimensionNumberCombo);