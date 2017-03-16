/**
 * @class BI.DetailStringDimensionCombo
 * @extend BI.Widget
 * 明细表文本维度的combo
 */
BI.DetailStringDimensionComboShow = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailStringDimensionComboShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-string-dimension-combo"
        })
    },

    _init: function () {
        BI.DetailStringDimensionComboShow.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            height: 25,
            iconCls: "detail-dimension-set-font"
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.DetailStringDimensionComboShow.EVENT_CHANGE, v);
        });
        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW, function () {
            this.populate(self._rebuildItems());
        })
    },

    _rebuildItems: function () {
        var self = this, o = this.options;
        var fieldId = BI.Utils.getFieldIDByDimensionID(o.dId);
        var fieldName = BI.Utils.getFieldNameByID(fieldId);
        var tableName = BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(fieldId));
        return [
            [{
                text: BI.i18nText("BI-Show_Field"),
                value: BICst.DETAIL_STRING_COMBO.SHOW_FIELD,
                cls: BI.Utils.isDimensionUsable(this.options.dId) ? "widget-combo-show-title-font" : ""
            }],
            [{
                text: BI.i18nText("BI-This_Target_From") + ":" + tableName + "." + fieldName,
                title: BI.i18nText("BI-This_Target_From") + ":" + tableName + "." + fieldName,
                tipType: "warning",
                value: BICst.DETAIL_STRING_COMBO.INFO,
                disabled: true
            }]
        ]
    },

    getValue: function () {
        return this.combo.getValue();
    }

});
BI.DetailStringDimensionComboShow.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_string_dimension_combo_show", BI.DetailStringDimensionComboShow);