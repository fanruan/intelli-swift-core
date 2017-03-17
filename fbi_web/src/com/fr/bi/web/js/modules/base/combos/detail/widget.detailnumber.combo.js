/**
 * @class BI.DetailNumberDimensionCombo
 * @extend BI.Widget
 * 明细表数值维度的combo
 */
BI.DetailNumberDimensionCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailNumberDimensionCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-number-dimension-combo"
        })
    },

    _rebuildItems: function(){
        var o = this.options;
        var fieldId = BI.Utils.getFieldIDByDimensionID(o.dId);
        var fieldName = BI.Utils.getFieldNameByID(fieldId);
        var tableName = BI.Utils.getTableNameByID(BI.Utils.getTableIdByFieldID(fieldId));
        return [
            [{
                text: BI.i18nText("BI-Style_Setting"),
                value: BICst.DETAIL_NUMBER_COMBO.FORM_SETTING
            }],
            [{
                text: BI.i18nText("BI-Filter_Setting"),
                value: BICst.DETAIL_NUMBER_COMBO.FILTER,
                cls: "filter-h-font"
            }],
            [{
                text: BI.i18nText("BI-Basic_Hyperlink"),
                value: BICst.DETAIL_NUMBER_COMBO.HYPERLINK,
                cls: "hyper-link-font"
            }],
            [{
                text: BI.i18nText("BI-Show_Field"),
                value: BICst.DETAIL_NUMBER_COMBO.SHOW_FIELD,
                cls: BI.Utils.isDimensionUsable(this.options.dId) ? "widget-combo-show-title-font" : ""
            }],
            [{
                text: BI.i18nText("BI-Base_Rename"),
                value: BICst.DETAIL_NUMBER_COMBO.RENAME
            }],
            [{
                text: BI.i18nText("BI-Base_Remove"),
                cls: "delete-h-font",
                value: BICst.DETAIL_NUMBER_COMBO.DELETE
            }],
            [{
                text: BI.i18nText("BI-This_Target_From") + ":" + tableName + "."  + fieldName,
                title: BI.i18nText("BI-This_Target_From") + ":" + tableName + "."  + fieldName,
                tipType: "success",
                cls: "dimension-from-font",
                value: BICst.DETAIL_NUMBER_COMBO.INFO,
                disabled: true
            }]
        ];
    },

    _init: function () {
        BI.DetailNumberDimensionCombo.superclass._init.apply(this, arguments);
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            height: 25,
            iconCls: "detail-dimension-set-font"

        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.DetailNumberDimensionCombo.EVENT_CHANGE, v);
        });
        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW,function(){
            this.populate(self._rebuildItems());
        });
    },

    getValue: function () {
        return this.combo.getValue();
    }

});
BI.DetailNumberDimensionCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_number_dimension_combo", BI.DetailNumberDimensionCombo);