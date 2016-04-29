/**
 * Created by roy on 16/2/2.
 */
BI.LabelCoveredEditor = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.LabelCoveredEditor.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " label-covered-editor",
            validationChecker: BI.emptyFn()
        })
    },

    _init: function () {
        var self = this, o = this.options;
        BI.LabelCoveredEditor.superclass._init.apply(this, arguments);
        this.text = BI.createWidget({
            type: "bi.label",
            height: 30,
            value: o.value
        });

        this.editor = BI.createWidget({
            type: "bi.editor",
            height: 30,
            value: o.value,
            validationChecker: o.validationChecker,
            errorText: o.errorText,
            allowBlank: true
        });

        this.editor.on(BI.Editor.EVENT_CONFIRM, function () {
            self._showHint();
            self._checkText();
            self.fireEvent(BI.LabelCoveredEditor.EVENT_CONFIRM, arguments);
        });

        this.editor.on(BI.Editor.EVENT_CHANGE, function () {
            self.fireEvent(BI.LabelCoveredEditor.EVENT_CHANGE, arguments);
        });

        this.editor.on(BI.Editor.EVENT_ERROR, function () {
            self._checkText();
            self.fireEvent(BI.LabelCoveredEditor.EVENT_ERROR, arguments);
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.text,
                top: 0,
                bottom: 0,
                left: 0,
                right: 0
            }]
        });

        BI.createWidget({
            type: "bi.vertical",
            scrolly: false,
            element: this.element,
            items: [this.editor]
        });
        this._showHint();
        this._checkText();
    },

    _showHint: function () {
        this.editor.invisible();
        this.text.visible();
    },

    _showInput: function () {
        this.editor.visible();
        this.text.invisible();
    },

    _checkText: function () {
        var o = this.options;
        if (this.editor.getValue() === "") {
            this.text.setValue(o.watermark || "");
            this.text.element.addClass("bi-water-mark");
        } else {
            this.text.setValue(this.editor.getValue());
            this.text.element.removeClass("bi-water-mark");
        }
    },

    focus: function () {
        this._showInput();
        this.editor.focus();
    },

    selectAll: function () {
        this.editor.selectAll();
    },


    doHighLight: function () {
        if (this.editor.getValue() === "" && BI.isKey(this.options.watermark)) {
            return;
        }
        this.text.doHighLight.apply(this.text, arguments);
    },

    unHighLight: function () {
        this.text.unHighLight.apply(this.text, arguments);
    },

    getValue: function () {
        return this.editor.getValue();
    },

});
BI.LabelCoveredEditor.EVENT_CONFIRM = "EVENT_CONFIRM";
BI.LabelCoveredEditor.EVENT_CHANGE = "EVENT_CHANGE";
BI.LabelCoveredEditor.EVENT_ERROR = "EVENT_ERROR";
$.shortcut("bi.label_covered_editor", BI.LabelCoveredEditor);