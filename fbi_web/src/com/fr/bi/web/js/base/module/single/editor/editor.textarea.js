/**
 *
 * Created by GUY on 2016/1/18.
 * @class BI.TextAreaEditor
 * @extends BI.Single
 */
BI.TextAreaEditor = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        return $.extend(BI.TextAreaEditor.superclass._defaultConfig.apply(), {
            baseCls: 'bi-textarea-editor',
            value: ''
        });
    },
    _init: function () {
        BI.TextAreaEditor.superclass._init.apply(this, arguments);
        var o = this.options, self = this;
        this.content = BI.createWidget({
            type: "bi.layout",
            tagName: "textarea",
            width: "99%",
            height: "99%",
            cls: "textarea-editor-content display-block"
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.adaptive",
                    items: [this.content]
                },
                left: 10,
                right: 10,
                top: 10,
                bottom: 10
            }]
        });

        this.content.element.focus(function () {
            self.content.element.addClass("textarea-editor-focus");
            self.fireEvent(BI.TextAreaEditor.EVENT_FOCUS);
        });
        this.content.element.blur(function () {
            self.content.element.removeClass("textarea-editor-focus");
            self.fireEvent(BI.TextAreaEditor.EVENT_BLUR);
        });
        if (BI.isKey(o.value)) {
            self.setValue(o.value);
        }
    },

    focus: function () {
        this.content.element.addClass("textarea-editor-focus");
        this.content.element.focus();
    },

    blur: function () {
        this.content.element.removeClass("textarea-editor-focus");
        this.content.element.blur();
    },

    getValue: function () {
        return this.content.element.val();
    },

    setValue: function (value) {
        this.content.element.val(value);
    },

    setStyle: function (style) {
        this.style = style;
        this.element.css(style);
        this.content.element.css(style)
    },

    getStyle: function () {
        return this.style;
    }
});
BI.TextAreaEditor.EVENT_BLUR = "EVENT_BLUR";
BI.TextAreaEditor.EVENT_FOCUS = "EVENT_FOCUS";
$.shortcut("bi.textarea_editor", BI.TextAreaEditor);