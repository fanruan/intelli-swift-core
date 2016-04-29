/**
 *
 * Created by GUY on 2016/1/18.
 * @class BI.ContentEditor
 * @extends BI.Single
 */
BI.ContentEditor = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        return $.extend(BI.ContentEditor.superclass._defaultConfig.apply(), {
            baseCls: 'bi-content-editor',
            value: ''
        });
    },
    _init: function () {
        BI.ContentEditor.superclass._init.apply(this, arguments);
        var o = this.options, self = this;
        this.content = BI.createWidget({
            type: "bi.layout",
            cls: "content-editor-content",
            attributes: {
                contenteditable: true
            }
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.content,
                left: 10,
                right: 10,
                top: 10,
                bottom: 10
            }]
        });

        this.content.element.focus(function () {
            self.fireEvent(BI.ContentEditor.EVENT_FOCUS);
        });
        this.content.element.blur(function () {
            self.fireEvent(BI.ContentEditor.EVENT_BLUR);
        });
        if (BI.isKey(o.value)) {
            self.setValue(o.value);
        }
    },

    focus: function () {
        this.content.element.focus();
    },

    blur: function () {
        this.content.element.blur();
    },

    getValue: function () {
        return this.content.element.text();
    },

    setValue: function (value) {
        this.content.element.text(value);
    },

    getHtml: function () {
        return this.content.element.html();
    },

    setHtml: function (html) {
        this.content.element.html(html);
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
BI.ContentEditor.EVENT_BLUR = "EVENT_BLUR";
BI.ContentEditor.EVENT_FOCUS = "EVENT_FOCUS";
$.shortcut("bi.content_editor", BI.ContentEditor);