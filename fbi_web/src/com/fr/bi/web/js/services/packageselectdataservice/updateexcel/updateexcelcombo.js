/**
 * Created by zcf on 2017/1/6.
 */
BI.UpdateExcelCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.UpdateExcelCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-update-excel-combo",
            tableId: "",
            height: 25,
            width: 70
        })
    },
    _init: function () {
        BI.UpdateExcelCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var textButton = BI.createWidget({
            type: "bi.text_button",
            text: BI.i18nText("BI-Update_Excel"),
            stopPropagation: true,
            height: o.height,
            width: o.width
        });
        textButton.on(BI.TextButton.EVENT_CHANGE, function () {
            self.combo.showView();
        });
        this.popup = BI.createWidget({
            type: "bi.update_excel_popup",
            tableId: o.tableId
        });
        this.combo = BI.createWidget({
            type: "bi.combo",
            toggle: true,
            element: this.element,
            el: textButton,
            popup: {
                el: this.popup,
                minWidth: 600,
                minHeight: 400
            }
        });
        this.combo.on(BI.Combo.EVENT_BEFORE_POPUPVIEW, function () {
            self.popup.populate();
        })
    }
});
$.shortcut("bi.update_excel_combo", BI.UpdateExcelCombo);