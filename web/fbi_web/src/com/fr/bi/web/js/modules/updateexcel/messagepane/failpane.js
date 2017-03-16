/**
 * Created by zcf on 2017/1/16.
 */
BI.UpdateExcelFailPane = BI.inherit(BI.Widget, {
    _constant: {
        WIDTH: 460,
        HEIGHT: 140,
        TITLE_HEIGHT: 35,
        LABEL_HEIGHT: 20,
        BUTTON_HEIGHT: 14,
        BUTTON_WIDTH: 14
    },

    _defaultConfig: function () {
        return BI.extend(BI.UpdateExcelFailPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: 'bi-update-excel-fail-pane'
        })
    },

    _init: function () {
        BI.UpdateExcelFailPane.superclass._init.apply(this, arguments);

        var self = this, c = this._constant;

        var title = this._createTitle();
        var labelFirstLine = this._createLabel(BI.i18nText("BI-Upload_Excel_Version"));
        var labelSecondLine = this._createLabel(BI.i18nText("BI-Upload_Excel_Format"));
        var labelThirdLine = this._createLabel(BI.i18nText("BI-Update_Excel_First_Sheet"));
        var labelFourthLine = this._createLabel(BI.i18nText("BI-Upload_Excel_Name_Value"));

        var text = BI.createWidget({
            type: "bi.vertical",
            items: [title, labelFirstLine, labelSecondLine, labelThirdLine, labelFourthLine]
        });

        var button = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-red-font",
            height: c.BUTTON_HEIGHT,
            width: c.BUTTON_WIDTH,
            iconWidth: c.BUTTON_WIDTH,
            iconHeight: c.BUTTON_HEIGHT
        });
        button.on(BI.IconButton.EVENT_CHANGE, function () {
            self.setVisible(false);
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            width: c.WIDTH,
            height: c.HEIGHT,
            items: [{
                el: text,
                top: 0,
                bottom: 5,
                left: 20,
                right: 20
            }, {
                el: button,
                right: 10,
                top: 10
            }]
        })
    },

    _createTitle: function () {
        var c = this._constant;
        return BI.createWidget({
            type: "bi.label",
            cls: "fail-title",
            text: BI.i18nText("BI-Upload_Failed"),
            textAlign: "left",
            height: c.TITLE_HEIGHT
        })
    },

    _createLabel: function (text) {
        var c = this._constant;
        return BI.createWidget({
            type: "bi.label",
            cls: "fail-text",
            text: text,
            textAlign: "left",
            height: c.LABEL_HEIGHT
        })
    }
});
$.shortcut("bi.update_excel_fail_pane", BI.UpdateExcelFailPane);