/**
 * @class BI.DetailFormulaDimensionCombo
 * @extend BI.Widget
 * 明细表计算指标的combo
 */
BI.DetailFormulaDimensionCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailFormulaDimensionCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-formula-dimension-combo"
        })
    },

    _rebuildItems: function(){
        return [
            [{
                text: BI.i18nText("BI-Style_Setting"),
                value: BICst.DETAIL_FORMULA_COMBO.FORM_SETTING
            }],
            [{
                text: BI.i18nText("BI-Modify_Cal_Formula"),
                value: BICst.DETAIL_FORMULA_COMBO.UPDATE_FORMULA
            }],
            [{
                text: BI.i18nText("BI-Hyperlink"),
                value: BICst.DETAIL_FORMULA_COMBO.HYPERLINK,
                cls: "hyper-link-font"
            }],
            [{
                text: BI.i18nText("BI-Show_Field"),
                value: BICst.DETAIL_FORMULA_COMBO.SHOW_FIELD,
                cls: BI.Utils.isDimensionUsable(this.options.dId) ? "widget-combo-show-title-font" : ""
            }],
            [{
                text: BI.i18nText("BI-Rename"),
                value: BICst.DETAIL_FORMULA_COMBO.RENAME
            }],
            [{
                text: BI.i18nText("BI-Remove"),
                value: BICst.DETAIL_FORMULA_COMBO.DELETE,
                cls: "delete-h-font"
            }]
        ];
    },

    _init: function () {
        BI.DetailFormulaDimensionCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            height: 25,
<<<<<<< HEAD
            iconCls: "detail-dimension-set-font"
=======
            iconCls: "detail-dimension-set-font",
            items: [
                [{
                    text: BI.i18nText("BI-Style_Setting"),
                    value: BICst.DETAIL_FORMULA_COMBO.FORM_SETTING
                }],
                [{
                    text: BI.i18nText("BI-Modify_Cal_Formula"),
                    value: BICst.DETAIL_FORMULA_COMBO.UPDATE_FORMULA
                }],
                [{
                    text: BI.i18nText("BI-Hyperlink"),
                    value: BICst.DETAIL_FORMULA_COMBO.HYPERLINK,
                    cls: "hyper-link-font"
                }],
                [{
                    text: BI.i18nText("BI-Remove"),
                    value: BICst.DETAIL_FORMULA_COMBO.DELETE,
                    cls: "delete-h-font"
                }]
            ]
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.DetailFormulaDimensionCombo.EVENT_CHANGE, v);
        });

        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW,function(){
            this.populate(self._rebuildItems());
        });
    },


    setValue: function (v) {
        this.combo.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue();
    }

})
;
BI.DetailFormulaDimensionCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_formula_dimension_combo", BI.DetailFormulaDimensionCombo);