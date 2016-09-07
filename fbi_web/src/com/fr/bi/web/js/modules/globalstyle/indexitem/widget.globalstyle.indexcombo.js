/**
 * Created by zcf on 2016/8/25.
 */
BI.GlobalStyleIndexBackground = BI.inherit(BI.Widget, {
    _constant: {},

    _defaultConfig: function () {
        return BI.extend(BI.GlobalStyleIndexBackground.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-global-style-indexcombo"
        })
    },

    _init: function () {
        BI.GlobalStyleIndexBackground.superclass._init.apply(this, arguments);
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.text_value_combo",
            width: 120,
            height: 30,
            text: BI.i18nText("BI-Colors"),
            items: [{
                text: BI.i18nText("BI-Colors"),
                value: BICst.BACKGROUND_TYPE.COLOR
            }, {
                text: BI.i18nText("BI-Pictures"),
                value: BICst.BACKGROUND_TYPE.IMAGE
            }]
        });
        this.combo.on(BI.TextValueCombo.EVENT_CHANGE, function () {
            self.tab.setSelect(this.getValue()[0]);
            self.fireEvent(BI.GlobalStyleIndexBackground.EVENT_CHANGE);
        });
        this.tab = BI.createWidget({
            type: "bi.tab",
            cardCreator: function (v) {
                switch (v) {
                    case BICst.BACKGROUND_TYPE.COLOR:
                        var colorChooser = BI.createWidget({
                            type: "bi.color_chooser",
                            height: 30,
                            width: 30
                        });
                        colorChooser.on(BI.ColorChooser.EVENT_CHANGE, function () {
                            self.fireEvent(BI.GlobalStyleIndexBackground.EVENT_CHANGE);
                        });
                        return colorChooser;
                    case BICst.BACKGROUND_TYPE.IMAGE:
                        var uploadImage = BI.createWidget({
                            type: "bi.multifile_editor",
                            accept: "*.jpg;*.png;*.gif;*.bmp;*.jpeg;",
                            maxSize: 1024 * 1024 * 100
                        });
                        uploadImage.on(BI.MultifileEditor.EVENT_CHANGE, function () {

                        });
                        return uploadImage;
                }
            },
            width: 30,
            height: 30
        });
        BI.createWidget({
            type: "bi.left",
            cls: "bi-global-style-indexcombo",
            element: this.element,
            items: [this.combo, this.tab],
            hgap: 5
        })
    },

    getValue: function () {
        return {
            type: this.combo.getValue()[0],
            value: this.tab.getValue()
        }
    },

    setValue: function (v) {
        if (BI.isNotNull(v) && BI.isNotNull(v.type)) {
            this.combo.setValue(v.type);
            this.tab.setSelect(v.type);
            this.tab.setValue(v.value);
        }
    }
});
BI.GlobalStyleIndexBackground.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.global_style_index_background", BI.GlobalStyleIndexBackground);