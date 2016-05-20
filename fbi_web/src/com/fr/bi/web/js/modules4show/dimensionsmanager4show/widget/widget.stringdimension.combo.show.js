/**
 * 报表预览时会调用
 * @class BI.DimensionStringComboShow
 * @extend BI.AbstractDimensionCombo
 * 字段类型string
 */
BI.DimensionStringComboShow = BI.inherit(BI.AbstractDimensionCombo, {

    config: {
        ASCEND: BICst.DIMENSION_STRING_COMBO.ASCEND,
        DESCEND: BICst.DIMENSION_STRING_COMBO.DESCEND,
        NOT_SORT: BICst.DIMENSION_STRING_COMBO.NOT_SORT,
        SORT_BY_CUSTOM: BICst.DIMENSION_STRING_COMBO.SORT_BY_CUSTOM,
        GROUP_BY_VALUE: BICst.DIMENSION_STRING_COMBO.GROUP_BY_VALUE,
        GROUP_BY_CUSTOM: BICst.DIMENSION_STRING_COMBO.GROUP_BY_CUSTOM
    },

    _assertGroup: function (val) {
        val || (val = {});
        if (BI.isNull(val.type)) {
            val.type = BICst.GROUP.ID_GROUP;
        }
        return val;
    },

    _assertSort: function (val) {
        val || (val = {});
        if (BI.isNull(val.type)) {
            val.type = BICst.SORT.ASC;
        }
        val.sort_target || (val.sort_target = this.options.dId);
        return val;
    },

    typeConfig: function () {
        return this.config;
    },

    _defaultConfig: function () {
        return BI.extend(BI.DimensionStringComboShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-string-combo"
        })
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
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Same_Value_A_Group"),
                value: BICst.DIMENSION_STRING_COMBO.GROUP_BY_VALUE,
                cls: ""
            }, {
                text: BI.i18nText("BI-Custom_Grouping_Dot"),
                value: BICst.DIMENSION_STRING_COMBO.GROUP_BY_CUSTOM,
                cls: ""
            }],
            [{
                text: BI.i18nText("BI-Dimension_From"),
                tipType: "warning",
                value: BICst.DIMENSION_STRING_COMBO.INFO,
                cls: "",
                disabled: true
            }]
        ];
    },

    _init: function () {
        BI.DimensionStringComboShow.superclass._init.apply(this, arguments);
    }
});
BI.DimensionStringComboShow.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.dimension_string_combo_show", BI.DimensionStringComboShow);
