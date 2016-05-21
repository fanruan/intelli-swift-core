/**
 * @class BI.DimensionNumberComboShow
 * @extend BI.AbstractDimensionCombo
 * @abstract
 */
BI.DimensionNumberComboShow = BI.inherit(BI.AbstractDimensionCombo, {

    constants: {
        customSortPos: 3
    },

    config: {
        ASCEND: BICst.DIMENSION_NUMBER_COMBO.ASCEND,
        DESCEND: BICst.DIMENSION_NUMBER_COMBO.DESCEND,
        NOT_SORT: BICst.DIMENSION_NUMBER_COMBO.NOT_SORT,
        SORT_BY_CUSTOM: BICst.DIMENSION_NUMBER_COMBO.SORT_BY_CUSTOM,
        GROUP_BY_VALUE: BICst.DIMENSION_NUMBER_COMBO.GROUP_BY_VALUE,
        GROUP_SETTING: BICst.DIMENSION_NUMBER_COMBO.GROUP_SETTING
    },

    _defaultConfig: function () {
        return BI.extend(BI.DimensionNumberComboShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-number-combo"
        })
    },

    _init: function () {
        BI.DimensionNumberComboShow.superclass._init.apply(this, arguments);
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

    _rebuildItems: function () {
        var items = BI.DimensionStringCombo.superclass._rebuildItems.apply(this, arguments), o = this.options;
        var group = this._assertGroup(BI.Utils.getDimensionGroupByID(o.dId));
        var customSort = items[0][this.constants.customSortPos];
        group.type === BICst.GROUP.ID_GROUP ? customSort.disabled = false : customSort.disabled = true;
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
                text: BI.i18nText("BI-Unsorted"),
                value: BICst.DIMENSION_NUMBER_COMBO.NOT_SORT,
                cls: ""
            }, {
                text: BI.i18nText("BI-Custom_Sort_Dot"),
                value: BICst.DIMENSION_NUMBER_COMBO.SORT_BY_CUSTOM,
                cls: "",
                disabled: true
            }],
            [{
                text: BI.i18nText("BI-Same_Value_A_Group"),
                value: BICst.DIMENSION_NUMBER_COMBO.GROUP_BY_VALUE,
                cls: ""
            }, {
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
                text: BI.i18nText("BI-Dimension_From"),
                value: BICst.DIMENSION_NUMBER_COMBO.INFO,
                cls: "",
                disabled: true
            }]
        ]
    },

    typeConfig: function () {
        return this.config;
    }
});
BI.DimensionNumberComboShow.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.dimension_number_combo_show", BI.DimensionNumberComboShow);
