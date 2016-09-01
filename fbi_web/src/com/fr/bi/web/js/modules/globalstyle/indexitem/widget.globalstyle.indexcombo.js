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
                value: "colour"
            }, {
                text: BI.i18nText("BI-Pictures"),
                value: "picture"
            }]
        });
        this.combo.on(BI.TextValueCombo.EVENT_CHANGE, function () {
            self._createComboItems(this.getValue());
            self.fireEvent(BI.GlobalStyleIndexBackground.EVENT_CHANGE)
        });
        this.comboItem = BI.createWidget({
            type: "bi.button_group",
            items: [],
            height: 30,
            width: 30
        });
        this.comboItem.populate([self._selectColour()]);
        BI.createWidget({
            type: "bi.left",
            cls:"bi-global-style-indexcombo",
            element: this.element,
            items: [self.combo, self.comboItem
            ],
            hgap: 5
        })
    },

    _selectColour: function () {
        var self = this;
        this.selectColour = BI.createWidget({
            type: "bi.color_chooser",
            height: 30,
            width: 30
        });
        this.selectColour.on(BI.ColorChooser.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleIndexBackground.EVENT_CHANGE)
        });
        return this.selectColour;
    },

    _selectPicture: function () {
        var self = this;
        this.selectPicture = BI.createWidget({
            type: "bi.upload_image",
            invisible: false,
            height: 30,
            width: 30
        });
        this.selectPicture.on(BI.UploadImage.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleIndexBackground.EVENT_CHANGE)
        });
        return this.selectPicture;
    },

    _createComboItems: function (value) {
        var self = this;
        if (value[0] == "colour") {
            self.comboItem.populate([self._selectColour()]);
        } else if (value[0] == "picture") {
            self.comboItem.populate([self._selectPicture()]);
        } else {
            BI.Msg.toast("widget type error")
        }
    },

    getValue: function () {
        var self = this;
        var value = {};
        var comboType = self.combo.getValue();
        if (comboType[0] == "colour") {
            value = {
                "selectType": "colour",
                "Value": self.selectColour.getValue()
            }
        } else if (comboType[0] == "picture") {
            value = {
                "selectType": "picture",
                "Value": self.selectPicture.getValue()
            }
        } else {
            BI.Msg.toast("widget type getValue error");
        }
        return value;
    },

    setValue: function (values) {
        if(values["selectType"]=="colour"){
            this.combo.setValue(["colour"]);
            this._createComboItems(["colour"]);
            this.selectColour.setValue(values["Value"])
        }else if(values["selectType"]=="picture"){
            this.combo.setValue(["picture"]);
            this._createComboItems(["picture"]);
            this.selectPicture.setValue(values["Value"])
        }
    },

    populate: function (values) {

    }
});
BI.GlobalStyleIndexBackground.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.global_style_index_background", BI.GlobalStyleIndexBackground);