/**
 * Created by Young's on 2016/3/16.
 */
BI.ExcelTipCombo = BI.inherit(BI.Widget, {

    constants: {
        TIP_WIDTH: 24,
        TIP_HEIGHT: 24,
        TIP_POPUP_WIDTH: 520,
        TIP_POPUP_HEIGHT: 420,
        TIP_POPUP_EXAMPLE_WIDTH: 480,
        TIP_POPUP_EXAMPLE_HEIGHT: 240,
        TIP_POPUP_COMMENT_HEIGHT: 20,
        TIP_POPUP_GAP: 15
    },

    _defaultConfig: function(){
        return BI.extend(BI.ExcelTipCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-excel-tip-combo"
        })
    },

    _init: function(){
        BI.ExcelTipCombo.superclass._init.apply(this, arguments);
        var trigger = BI.createWidget({
            type: "bi.icon_button",
            cls: "excel-upload-tip-font tip-combo-trigger",
            width: this.constants.TIP_WIDTH,
            height: this.constants.TIP_HEIGHT
        });
        var popup = BI.createWidget({
            type: "bi.vertical",
            cls: "tip-combo-popup",
            items: [{
                type: "bi.label",
                height: this.constants.TIP_POPUP_GAP
            },{
                type: "bi.label",
                text: BI.i18nText("BI-Attention"),
                textAlign: "left",
                cls: "popup-tip-label",
                height: this.constants.TIP_POPUP_COMMENT_HEIGHT
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Upload_Excel_Version"),
                textAlign: "left",
                height: this.constants.TIP_POPUP_COMMENT_HEIGHT
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Upload_Excel_Format"),
                textAlign: "left",
                height: this.constants.TIP_POPUP_COMMENT_HEIGHT
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Upload_Excel_First_Sheet"),
                textAlign: "left",
                height: this.constants.TIP_POPUP_COMMENT_HEIGHT
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Upload_Excel_Name_Value"),
                textAlign: "left",
                height: this.constants.TIP_POPUP_COMMENT_HEIGHT
            }, {
                type: "bi.label",
                text: BI.i18nText("BI-Example"),
                textAlign: "left",
                cls: "popup-tip-label",
                height: 25
            }, {
                type: "bi.center_adapt",
                cls: "example-excel-icon",
                items: [{
                    type: "bi.icon",
                    width: this.constants.TIP_POPUP_EXAMPLE_WIDTH,
                    height: this.constants.TIP_POPUP_EXAMPLE_HEIGHT
                }]
            }, {
                type: "bi.label",
                height: this.constants.TIP_POPUP_GAP
            }],
            hgap: this.constants.TIP_POPUP_GAP
        });
        BI.createWidget({
            type: "bi.combo",
            element: this.element,
            trigger: "hover",
            isNeedAdjustWidth: false,
            isNeedAdjustHeight: false,
            offsetStyle: "center",
            el: trigger,
            popup: {
                el: popup,
                maxHeight: this.constants.TIP_POPUP_HEIGHT,
                width: this.constants.TIP_POPUP_WIDTH
            }
        })
    }
});
$.shortcut("bi.excel_tip_combo", BI.ExcelTipCombo);