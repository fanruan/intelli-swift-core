/**
 * Created by zcf on 2017/1/16.
 */
BI.UpdateExcelTrigger = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.UpdateExcelTrigger.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-update-excel-trigger"
        })
    },

    _init: function () {
        BI.UpdateExcelTrigger.superclass._init.apply(this, arguments);

        var self = this, o = this.options;

        this.textButton = BI.createWidget({
            type: "bi.text_button",
            text: BI.i18nText("BI-Update_Excel"),
            stopPropagation: true,
            height: o.height,
            width: 70
        });
        this.textButton.on(BI.TextButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.UpdateExcelTrigger.EVENT_CHANGE);
        });

        this.stateIcon = BI.createWidget({
            type: "bi.state_icon_button",
            height: o.height - 2,
            width: 20,
            iconWidth: 20,
            iconHeight: 20
        });
        this.stateIcon.on(BI.StateIconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.UpdateExcelTrigger.EVENT_CHANGE);
        });

        BI.createWidget({
            type: "bi.right",
            element: this.element,
            height: o.height,
            width: o.width,
            items: [this.stateIcon, this.textButton]
        })
    },

    setText: function (text) {
        this.textButton.setText(text);
    },

    setState: function (state) {
        this.stateIcon.setState(state);
    }
});
BI.UpdateExcelTrigger.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.update_excel_trigger", BI.UpdateExcelTrigger);