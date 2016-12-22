/**
 * Created by fay on 2016/9/1.
 */
BI.DataImageStyleSet = BI.inherit(BI.Widget, {
    _constant: {
        ICON_WIDTH: 20,
        ICON_HEIGHT: 20,
        BUTTON_HEIGHT: 40,
        TRIGGER_BUTTON_WIDTH: 50,
        TRIGGER_ICON_WIDTH: 12,
        TRIGGER_WIDTH: 62
    },

    _defaultConfig: function () {
        return BI.extend(BI.DataImageStyleSet.superclass._defaultConfig.apply(this, arguments), {});
    },

    _init: function () {
        BI.DataImageStyleSet.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.triggerButton = BI.createWidget({
            type: "bi.text_button",
            text: BI.i18nText("BI-Pictures"),
            height: this._constant.BUTTON_HEIGHT,
            width: this._constant.TRIGGER_BUTTON_WIDTH
        });
        this.triggerIcon = BI.createWidget({
            type: "bi.trigger_icon_button",
            cls: "trigger-icon",
            width: this._constant.TRIGGER_ICON_WIDTH
        });
        this.styleTrigger = BI.createWidget({
            type: "bi.horizontal",
            cls: "condition-trigger",
            items: [this.triggerButton, this.triggerIcon],
            height: "100%"
        });
        this.stylePane = BI.createWidget({
            type: "bi.data_image_pane",
            chartType: o.chartType,
            dId: o.dId,
            height: 146
        });
        this.stylePane.on(BI.DataImagePane.IMG_CHANGE, function () {
            self.stylePane.populate(self.stylePane.getValue().src);
            self.style.hideView();
        });
        this.style = BI.createWidget({
            type: "bi.combo",
            isNeedAdjustWidth: false,
            isNeedAdjustHeight: false,
            element: this.element,
            el: this.styleTrigger,
            popup: {
                el: this.stylePane
            },
            direction: "bottom,left",
            offsetStyle: "right",
            height: "100%"
        });
        this.style.on(BI.Combo.EVENT_AFTER_POPUPVIEW, function () {
            self.stylePane.populate(self.stylePane.getValue().src);
        });
    },

    setValue: function (v) {
        this.stylePane.setValue(v);
    },

    getValue: function () {
        return this.stylePane.getValue();
    }
});

$.shortcut("bi.data_image_style_set", BI.DataImageStyleSet);