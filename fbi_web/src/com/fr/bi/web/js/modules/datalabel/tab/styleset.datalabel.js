/**
 * Created by fay on 2016/7/25.
 */
BI.DataLabelStyleSet = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DataLabelStyleSet.superclass._defaultConfig.apply(this, arguments), {});
    },

    _init: function () {
        BI.DataLabelStyleSet.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
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
            type: "bi.data_label_tab",
            chartType: o.chartType
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
            var style = self.styleTab.getValue();
            switch (style.type) {
                case 1:
                    self.textTrigger.setValue("text");
                    $(self.textTrigger.element).css(style.textStyle);
                    self.imgTrigger.setVisible(false);
                    self.textTrigger.setVisible(true);
                    break;
                case 2:
                    self.imgTrigger.setSrc(style.imgStyle.src);
                    self.imgTrigger.setVisible(true);
                    self.textTrigger.setVisible(false);
            }
        });
    },

    _checkStyle: function (v) {
        switch (v.type) {
            case 1:
                this.textTrigger.setValue("text");
                //todo
                $(this.textTrigger.element).css(v.textStyle);
                break;
            case 2:
                this.imgTrigger.setSrc(v.imgStyle.src);
                this.imgTrigger.element.css({"display": "block"});
                this.imgTrigger.setVisible(true);
                this.textTrigger.setVisible(false);
        }
    },

    setValue: function (v) {
        this._checkStyle(v);
        this.styleTab.setValue(v);
    },

    getValue: function () {
        return this.styleTab.getValue();
    }
});

$.shortcut("bi.data_label_style_set", BI.DataLabelStyleSet);