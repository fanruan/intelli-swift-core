/**
 * @class BI.DimensionNumberCombo
 * @extend BI.AbstractDimensionCombo
 * @abstract
 */
BI.DimensionNumberCombo = BI.inherit(BI.AbstractDimensionCombo, {

    constants: {
        customSortPos : 2,
        CordonPos: 2
    },

    config:{
        ASCEND : BICst.DIMENSION_NUMBER_COMBO.ASCEND,
        DESCEND: BICst.DIMENSION_NUMBER_COMBO.DESCEND,
        SORT_BY_CUSTOM : BICst.DIMENSION_NUMBER_COMBO.SORT_BY_CUSTOM,
        GROUP_BY_VALUE : BICst.DIMENSION_NUMBER_COMBO.GROUP_BY_VALUE,
        GROUP_SETTING : BICst.DIMENSION_NUMBER_COMBO.GROUP_SETTING,
        POSITION_BY_ADDRESS: BICst.DIMENSION_NUMBER_COMBO.ADDRESS,
        POSITION_BY_LNG_LAT: BICst.DIMENSION_NUMBER_COMBO.LNG_LAT,
        POSITION_BY_LNG: BICst.DIMENSION_NUMBER_COMBO.LNG,
        POSITION_BY_LAT: BICst.DIMENSION_NUMBER_COMBO.LAT
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

    _assertAddress: function(val){
        val || (val = {});
        if(BI.isNull(val.type)){
            val.type = BICst.GIS_POSITION_TYPE.LNG_FIRST
        }
        return val;
    },

    _rebuildItems :function(){
        var items = BI.DimensionStringCombo.superclass._rebuildItems.apply(this, arguments), o = this.options;
        if(BI.Utils.getWidgetTypeByID(BI.Utils.getWidgetIDByDimensionID(o.dId)) === BICst.WIDGET.GIS_MAP){
        }else{
            var group = this._assertGroup(BI.Utils.getDimensionGroupByID(o.dId));
            var customSort = items[0][this.constants.customSortPos];
            group.type === BICst.GROUP.ID_GROUP ? customSort.disabled = true : customSort.disabled = false;
        }
        switch (BI.Utils.getWidgetTypeByID(BI.Utils.getWidgetIDByDimensionID(o.dId))) {
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                if(BI.Utils.getRegionTypeByDimensionID(o.dId) === BICst.REGION.DIMENSION2){
                    BI.removeAt(items, this.constants.CordonPos);
                }
                break;
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
                items[this.constants.CordonPos][0].text = BI.i18nText("BI-Cordon") + "(" + BI.i18nText("BI-Horizontal") +")";
                if(BI.Utils.getRegionTypeByDimensionID(o.dId) === BICst.REGION.DIMENSION2){
                    BI.removeAt(items, this.constants.CordonPos);
                }
                break;
            default:
                BI.removeAt(items, this.constants.CordonPos);
                break;

        }
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
                cls: "dot-e-font"
            }],
            [{
                text: BI.i18nText("BI-Same_Value_A_Group"),
                value: BICst.DIMENSION_NUMBER_COMBO.GROUP_BY_VALUE,
                cls: "dot-e-font"
            },{
                text: BI.i18nText("BI-Grouping_Setting"),
                value: BICst.DIMENSION_NUMBER_COMBO.GROUP_SETTING,
                cls: "dot-e-font"
            }],
            [{
                text: BI.i18nText("BI-Cordon") + "(" + BI.i18nText("BI-Vertical") +")",
                value: BICst.DIMENSION_NUMBER_COMBO.CORDON,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Show_Qualified_Result"),
                value: BICst.DIMENSION_NUMBER_COMBO.FILTER,
                cls: "filter-h-font"
            }],
            [{
                text: BI.i18nText("BI-Math_Relationships"),
                value: BICst.DIMENSION_NUMBER_COMBO.DT_RELATION,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Copy"),
                value: BICst.DIMENSION_NUMBER_COMBO.COPY,
                cls: "copy-h-font"
            }],
            [{
                text: BI.i18nText("BI-Remove"),
                value: BICst.DIMENSION_NUMBER_COMBO.DELETE,
                cls: "delete-h-font"
            }],
            [{
                text: BI.i18nText("BI-Dimension_From"),
                value: BICst.DIMENSION_NUMBER_COMBO.INFO,
                tipType: "success",
                cls: "dimension-from-font",
                disabled: true
            }]
        ]
    },

    typeConfig: function(){
        return this.config;
    }
});
$.shortcut("bi.dimension_number_combo", BI.DimensionNumberCombo);