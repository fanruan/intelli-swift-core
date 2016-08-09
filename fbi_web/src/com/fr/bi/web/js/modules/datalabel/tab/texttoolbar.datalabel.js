/**
 * Created by Fay on 2016/7/14.
 */
BI.DataLabelTextToolBar = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.DataLabelTextToolBar.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-data-tab-text-toolbar",
            height: 28
        });
    },

    _init: function () {
        var self = this;
        BI.DataLabelTextToolBar.superclass._init.apply(this, arguments);
        this.family = BI.createWidget({
            type: "bi.data_label_text_toolbar_font_chooser",
            height: 24,
            width: 80,
            cls: "text-toolbar-size-chooser-trigger"
        });
        this.size = BI.createWidget({
            type: "bi.text_toolbar_size_chooser",
            height: 24,
            width: 40,
            cls: "text-toolbar-size-chooser-trigger"
        });
        this.size.on(BI.TextToolbarSizeChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.DataLabelTextToolBar.EVENT_CHANGE, arguments);
        });
        this.bold = BI.createWidget({
            type: "bi.icon_button",
            height: 20,
            width: 20,
            cls: "text-toolbar-button bi-list-item-active text-bold-font"
        });
        this.bold.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.DataLabelTextToolBar.EVENT_CHANGE, arguments);
        });
        this.italic = BI.createWidget({
            type: "bi.icon_button",
            height: 20,
            width: 20,
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
            },
            height: 20,
            width: 20
        });
        this.colorchooser.on(BI.ColorChooser.EVENT_CHANGE, function () {

            self.fireEvent(BI.DataLabelTextToolBar.EVENT_CHANGE, arguments);
        });
        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [this.family, this.size, this.bold, this.italic, this.colorchooser],
            hgap: 3,
            vgap: 3
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