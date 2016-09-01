/**
 * Created by zcf on 2016/8/29.
 */
BI.GlobalStyleIndexTitleToolBar=BI.inherit(BI.Widget,{
    _defaultConfig:function () {
        return BI.extend(BI.GlobalStyleIndexTitleToolBar.superclass._defaultConfig.apply(this,arguments),{
            baseCls:"bi-global-style-title-toolbar"
        })
    },
    _init:function () {
        BI.GlobalStyleIndexTitleToolBar.superclass._init.apply(this,arguments);

        var self=this,o=this.options;

        this.bold = BI.createWidget({
            type: "bi.icon_button",
            height: 20,
            width: 20,
            cls: "text-toolbar-button bi-list-item-active text-bold-font"
        });
        this.bold.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleIndexTitleToolBar.EVENT_CHANGE, arguments);
        });

        this.italic = BI.createWidget({
            type: "bi.icon_button",
            height: 20,
            width: 20,
            cls: "text-toolbar-button bi-list-item-active text-italic-font"
        });
        this.italic.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleIndexTitleToolBar.EVENT_CHANGE, arguments);
        });

        this.alignChooser=BI.createWidget({
            type:"bi.global_style_index_align_chooser",
            cls: "text-toolbar-button"
        });
        this.alignChooser.on(BI.GlobalStyleIndexAlignChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleIndexTitleToolBar.EVENT_CHANGE, arguments);
        });

        this.colorchooser = BI.createWidget({
            type: "bi.color_chooser",
            el: {
                type: "bi.text_toolbar_color_chooser_trigger",
                cls: "text-toolbar-button"
            }
        });
        this.colorchooser.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleIndexTitleToolBar.EVENT_CHANGE, arguments);
        });

        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [this.bold, this.italic, this.alignChooser, this.colorchooser],
            hgap: 3,
            vgap: 3
        })
    },
    getValue:function () {
        return{
            "font-weight": this.bold.isSelected() ? "bold" : "normal",
            "font-style": this.italic.isSelected() ? "italic" : "normal",
            "text-align": this.alignChooser.getValue(),
            "color": this.colorchooser.getValue()
        }
    },
    setValue:function (v) {
        v || (v = {});
        this.bold.setSelected(v["font-weight"] === "bold");
        this.italic.setSelected(v["font-style"] === "italic");
        this.alignChooser.setValue(v["text-align"] || "left");
        this.colorchooser.setValue(v["color"] || "#000000");
    }
});
BI.GlobalStyleIndexTitleToolBar.EVENT_CHANGE="BI.GlobalStyleIndexTitleToolBar.EVENT_CHANGE";
$.shortcut("bi.global_style_index_title_tool_bar",BI.GlobalStyleIndexTitleToolBar);