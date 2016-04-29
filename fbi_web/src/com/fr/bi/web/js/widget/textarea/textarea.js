/**
 * 文本组件中 编辑栏作为trigger
 *
 * Created by GameJian on 2016/1/24.
 */
BI.TextAreaEditor = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TextAreaEditor.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-text-area-editor",
            height: 20
        });
    },

    _init: function () {
        BI.TextAreaEditor.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.textarea = BI.createWidget({
            type: "bi.text_area_trigger",
            height: o.height
        });

        this.textarea.on(BI.TextAreaTrigger.EVENT_CHANGE, function () {
            self.fireEvent(BI.TextAreaEditor.EVENT_DESTROY)
        });

        this.toolbar = BI.createWidget({
            type: "bi.text_toolbar"
        });

        this.label = BI.createWidget({
            type: "bi.text_button",
            cls: "text-area-editor-text-button-label",
            whiteSpace: "normal",
            text: BI.i18nText("BI-Click_To_Input_Text")
        });

        this.label.on(BI.TextButton.EVENT_CHANGE, function () {
            self._showInput();
            self.textarea.focus();
        });

        this.combo = BI.createWidget({
            type: "bi.combo",
            toggle: false,
            direction: "top",
            adjustLength: 1,
            element: this.element,
            el: this.textarea,
            hideChecker: function (e) {
                if (self.toolbar.isColorChooserVisible() || self.toolbar.isBackgroundChooserVisible()) {
                    return false;
                }
            },
            popup: {
                el: this.toolbar,
                minWidth: 253,
                height: 30,
                stopPropagation: false
            }
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.label,
                left: 10,
                right: 10,
                top: 10,
                bottom: 10
            }]
        });

        this.toolbar.on(BI.TextToolbar.EVENT_CHANGE, function () {
            self.setValue(self.getValue());
            self.fireEvent(BI.TextAreaEditor.EVENT_VALUE_CHANGE, arguments);
        });

        this.textarea.on(BI.TextAreaTrigger.EVENT_BLUR, function () {
            self._showLabel();
            if (BI.isNotEmptyString(this.getValue())) {
                self._showInput()
            }
            self.fireEvent(BI.TextAreaEditor.EVENT_VALUE_CHANGE, arguments)
        });
    },

    _showInput: function () {
        this.label.invisible();
    },

    _showLabel: function () {
        this.label.visible();
    },

    setValue: function (v) {
        v || (v = {});
        if (BI.isNotEmptyString(v.content)) {
            this._showInput();
        }
        this.textarea.setValue(v.content);
        this.toolbar.setValue(v.style);
        this.textarea.setStyle(v.style);
    },

    getValue: function () {
        return {style: this.toolbar.getValue(), content: this.textarea.getValue()};
    }
});
BI.TextAreaEditor.EVENT_VALUE_CHANGE = "EVENT_VALUE_CHANGE";
BI.TextAreaEditor.EVENT_DESTROY = "EVENT_DESTROY";
$.shortcut("bi.text_area_editor", BI.TextAreaEditor);