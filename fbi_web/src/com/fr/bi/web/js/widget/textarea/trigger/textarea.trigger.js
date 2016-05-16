/**
 * 文本控件点击编辑文字
 *
 * Created by GameJian on 2016/1/18.
 */
BI.TextAreaTrigger = BI.inherit(BI.Trigger, {
    _defaultConfig: function () {
        var conf = BI.TextAreaTrigger.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-text-area-trigger",
            height: 50,
            value: ""
        })
    },

    _init: function () {
        BI.TextAreaTrigger.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.editor = BI.createWidget({
            type: "bi.textarea_editor"
        });

        this.editor.on(BI.TextAreaEditor.EVENT_FOCUS, function () {
            self.fireEvent(BI.TextAreaTrigger.EVENT_FOCUS);
        });

        this.editor.on(BI.TextAreaEditor.EVENT_BLUR, function(){
            self.fireEvent(BI.TextAreaTrigger.EVENT_BLUR);
        });

        this.del = BI.createWidget({
            type: "bi.icon_button",
            width: 32,
            height: 32,
            cls: "text-area-trigger-button bi-list-item-hover img-shutdown-font",
            title: BI.i18nText("BI-Delete")
        });

        this.del.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.TextAreaTrigger.EVENT_CHANGE)
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el:this.editor,
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            },{
                el: this.del,
                right: 4,
                top: 4
            }]
        });
    },

    focus: function () {
      this.editor.focus();
    },

    blur: function () {
      this.editor.blur();
    },

    setStyle: function (style) {
        this.editor.setStyle(style);
    },

    getStyle: function () {
        return this.editor.getStyle();
    },

    setValue: function (k) {
        this.editor.setValue(k);
    },

    getValue: function () {
        return this.editor.getValue();
    }
});

BI.TextAreaTrigger.EVENT_CHANGE = "EVENT_CHANGE";
BI.TextAreaTrigger.EVENT_FOCUS = "EVENT_FOCUS";
BI.TextAreaTrigger.EVENT_BLUR = "EVENT_BLUR";

$.shortcut("bi.text_area_trigger", BI.TextAreaTrigger);