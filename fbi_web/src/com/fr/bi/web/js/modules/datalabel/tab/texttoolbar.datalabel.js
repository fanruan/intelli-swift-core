/**
 * Created by Fay on 2016/7/14.
 */
BI.DataLabelTextToolBar = BI.inherit(BI.Widget, {
    _constant: {
        TRIGGER_HEIGHT: 24,
        TRIGGER_WIDTH: 80,
        CHOOSER_WIDTH: 40,
        BUTTON_HEIGHT: 20,
        BUTTON_WIDTH: 20
    },

    _defaultConfig: function () {
        var conf = BI.DataLabelTextToolBar.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-data-tab-text-toolbar"
        });
    },

    _init: function () {
        var self = this, o = this.options;
        BI.DataLabelTextToolBar.superclass._init.apply(this, arguments);
        this.family = BI.createWidget({
            type: "bi.text_toolbar_font_chooser",
            height: this._constant.TRIGGER_HEIGHT,
            width: this._constant.TRIGGER_WIDTH,
            cls: "text-toolbar-size-chooser-trigger"
        });
        this.family.on(BI.TextToolbarFontChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.DataLabelTextToolBar.EVENT_CHANGE, arguments);
        });
        this.size = BI.createWidget({
            type: "bi.text_toolbar_size_chooser",
            height: this._constant.TRIGGER_HEIGHT,
            width: this._constant.CHOOSER_WIDTH,
            cls: "text-toolbar-size-chooser-trigger"
        });
        this.size.on(BI.TextToolbarSizeChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.DataLabelTextToolBar.EVENT_CHANGE, arguments);
        });
        this.bold = BI.createWidget({
            type: "bi.icon_button",
            height: this._constant.BUTTON_HEIGHT,
            width: this._constant.BUTTON_WIDTH,
            cls: "text-toolbar-button bi-list-item-active text-bold-font"
        });
        this.bold.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.DataLabelTextToolBar.EVENT_CHANGE, arguments);
        });
        this.italic = BI.createWidget({
            type: "bi.icon_button",
            height: this._constant.BUTTON_HEIGHT,
            width: this._constant.BUTTON_WIDTH,
            cls: "text-toolbar-button bi-list-item-active text-italic-font"
        });
        this.italic.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.DataLabelTextToolBar.EVENT_CHANGE, arguments);
        });
        this.colorchooser = BI.createWidget({
            type: "bi.color_chooser",
            el: {
                type: "bi.text_toolbar_color_chooser_trigger",
                cls: "text-toolbar-button"
            }
        });
        this.colorchooser.on(BI.ColorChooser.EVENT_CHANGE, function () {

            self.fireEvent(BI.DataLabelTextToolBar.EVENT_CHANGE, arguments);
        });
        var top = BI.createWidget({
            type: "bi.left",
            items: [this.family, this.size, this.bold, this.italic, this.colorchooser],
            hgap: 3,
            vgap: 3
        });
        if (o.chartType === BICst.WIDGET.BUBBLE) {
            this.showItems = BI.createWidget({
                type: "bi.text_tool_bar_content_select",
                items: [{
                    value: BI.i18nText("BI-X_Value")
                }, {
                    value: BI.i18nText("BI-Y_Value")
                }, {
                    value: BI.i18nText("BI-Bubble_Size_Value")
                }]
            });
        } else if(o.chartType === BICst.WIDGET.SCATTER) {
            this.showItems = BI.createWidget({
                type: "bi.text_tool_bar_content_select",
                items: [{
                    value: BI.i18nText("BI-X_Value")
                }, {
                    value: BI.i18nText("BI-Y_Value")
                }]
            });
        } else {
            this.showItems = BI.createWidget();
        }

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [top, this.showItems],
            lgap: 8
        })
    },

    setValue: function (v) {
        v || (v = {});
        this.family.setValue(v["font-family"] || "");
        this.size.setValue(v["font-size"] || 14);
        this.bold.setSelected(v["font-weight"] === "bold");
        this.italic.setSelected(v["font-style"] === "italic");
        this.colorchooser.setValue(v["color"] || "#000000");
    },

    getValue: function () {
        return {
            "font-family": this.family.getValue(),
            "font-size": this.size.getValue(),
            "font-weight": this.bold.isSelected() ? "bold" : "normal",
            "font-style": this.italic.isSelected() ? "italic" : "normal",
            "color": this.colorchooser.getValue()
        }
    }
});
BI.DataLabelTextToolBar.EVENT_CHANGE = "BI.DataLabelTextToolBar.EVENT_CHANGE";
$.shortcut("bi.data_label_text_toolbar", BI.DataLabelTextToolBar);