/**
 * @class BI.DetailStringDimensionCombo
 * @extend BI.Widget
 * 明细表文本维度的combo
 */
BI.DetailStringDimensionCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailStringDimensionCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-string-dimension-combo"
        })
    },

    _init: function () {
        BI.DetailStringDimensionCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var fieldId = BI.Utils.getFieldIDByDimensionID(o.dId);
        var fieldName = BI.Utils.getFieldNameByID(fieldId);
        var tableName = BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(fieldId));
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            height: 25,
            iconCls: "detail-dimension-set-font",
            items: [
                [{
                    text: BI.i18nText("BI-Filter_Setting"),
                    value: BICst.DETAIL_STRING_COMBO.FILTER
                }],
                [{
                    text: BI.i18nText("BI-Hyperlink"),
                    value: BICst.DETAIL_STRING_COMBO.HYPERLINK
                }],
                [{
                    text: BI.i18nText("BI-Remove"),
                    value: BICst.DETAIL_STRING_COMBO.DELETE
                }],
                [{
                    text: BI.i18nText("BI-This_Target_From") + ":" + tableName + "."  + fieldName,
                    title: BI.i18nText("BI-This_Target_From") + ":" + tableName + "."  + fieldName,
                    tipType: "warning",
                    value: BICst.DETAIL_STRING_COMBO.INFO,
                    disabled: true
                }]
            ]
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.DetailStringDimensionCombo.EVENT_CHANGE, v);
        })
    },

    getValue: function () {
        return this.combo.getValue();
    }

});
BI.DetailStringDimensionCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_string_dimension_combo", BI.DetailStringDimensionCombo);