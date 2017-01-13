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
        this.popup.on(BI.UpdateExcelPopup.EVENT_CONFIRM, function () {
            if (this.getIsAllowUpdate()) {
                self._updateExcelTableDate();
            }
            self.combo.hideView();
        });
        this.popup.on(BI.UpdateExcelPopup.EVENT_CANCEL, function () {
            self.combo.hideView();
        });

        this.combo = BI.createWidget({
            type: "bi.combo",
            toggle: true,
            element: this.element,
            el: textButton,
            popup: {
                el: this.popup,
                minWidth: 550,
                minHeight: 350
            }
        });
        this.combo.on(BI.Combo.EVENT_BEFORE_POPUPVIEW, function () {
            self.popup.populate();
        })
    },

    _updateExcelTableDate: function () {
        var oldTableId = this.options.tableId;
        var newExcelFullName = this.popup.getExcelFullName();
        var data = {
            oldTableId: oldTableId,
            newExcelFullName: newExcelFullName
        };
        Data.Req.reqUpdateExcelTableCube(data, function (date) {
            console.log(date);
        }, function () {
            console.log("complete");
        })
    }
});
$.shortcut("bi.update_excel_combo", BI.UpdateExcelCombo);