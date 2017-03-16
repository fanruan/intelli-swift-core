/**
 * Created by zcf on 2017/1/16.
 */
BI.UpdateExcelSuccessPane = BI.inherit(BI.Widget, {

    _constant: {
        WIDTH: 460,
        HEIGHT: 35
    },

    _defaultConfig: function () {
        return BI.extend(BI.UpdateExcelSuccessPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-update-excel-fail-pane"
        })
    },

    _init: function () {
        BI.UpdateExcelSuccessPane.superclass._init.apply(this, arguments);

        var c = this._constant;

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            height: c.HEIGHT,
            width: c.WIDTH,
            items: [{
                el: this._createLabel(),
                top: 0,
                bottom: 0,
                left: 20,
                right: 20
            }]
        })

    },

    _createLabel: function () {
        var c = this._constant;
        return BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Excel_Pass_Verification"),
            textAlign: "left",
            height: c.HEIGHT
        })
    }
});
$.shortcut("bi.update_excel_success_pane", BI.UpdateExcelSuccessPane);