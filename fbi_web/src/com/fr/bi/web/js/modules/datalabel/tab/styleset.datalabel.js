/**
 * Created by fay on 2016/7/25.
 */
BI.DataLabelStyleSet = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DataLabelStyleSet.superclass._defaultConfig.apply(this, arguments), {});
    },

    _init: function () {
        BI.DataLabelStyleSet.superclass._init.apply(this, arguments);
        var self = this;
        this.textTrigger = BI.createWidget({
            type: "bi.text_button",
            text: "设置样式",
            height: 40,
            cls: "condition-trigger"
        });
        this.imgTrigger = BI.createWidget({
            type: "bi.image_button",
            height: 40,
            iconWidth: 20,
            iconHeight: 20,
            cls: "condition-trigger"
        });
        this.styleTab = BI.createWidget({
            type: "bi.data_label_tab"
        });
        this.styleTab.on(BI.DataLabelTab.IMG_CHANGE, function () {
            self.style.hideView();
        });
        this.imgTrigger.setVisible(false);
        this.triggerButton = BI.createWidget({
            type: "bi.vertical",
            items: [this.textTrigger, this.imgTrigger]
        });
        this.triggerIcon = BI.createWidget({
            type: "bi.trigger_icon_button",
            cls: "trigger-icon",
            width: 20
        });
        this.styleTrigger = BI.createWidget({
            type: "bi.htape",
            items: [{
                el: this.triggerButton,
                width: 50
            }, {
                el: this.triggerIcon,
                width: 12
            }],
            width: 62,
            height: 40
        });
        this.style = BI.createWidget({
            type: "bi.combo",
            isNeedAdjustWidth: false,
            element: this.element,
            el: this.styleTrigger,
            popup: {
                el: this.styleTab
            },
            offsetStyle: "right"
        });
        this.style.on(BI.Combo.EVENT_AFTER_HIDEVIEW, function () {
            if (self.styleTab.getValue().type === "img") {
                self.imgTrigger.setSrc(self.styleTab.getValue().src);
                self.imgTrigger.setVisible(true);
                self.textTrigger.setVisible(false);
            } else {
                self.textTrigger.setValue("text");
                $(self.textTrigger.element).css(self.styleTab.getValue());
                self.imgTrigger.setVisible(false);
                self.textTrigger.setVisible(true);
            }
        });
    },

    _checkVisible: function (v) {
        if (v.type === "img") {
            this.imgTrigger.setSrc(v.src);
            this.imgTrigger.element.css({"display": "block"});
            this.imgTrigger.setVisible(true);
            this.textTrigger.setVisible(false);
        } else {
            this.textTrigger.setValue("text");
            //todo
            $(this.textTrigger.element).css(v);
        }
    },

    setValue: function (v) {
        this._checkVisible(v);
        this.styleTab.setValue(v);
    },

    getValue: function () {
        return this.styleTab.getValue();
    }
});

$.shortcut("bi.data_label_style_set", BI.DataLabelStyleSet);