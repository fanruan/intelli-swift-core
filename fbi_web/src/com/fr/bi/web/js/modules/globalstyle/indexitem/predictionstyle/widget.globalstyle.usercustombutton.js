/**
 * Created by zcf on 2016/9/5.
 */
BI.GlobalStyleUserCustomButton = BI.inherit(BI.BasicButton, {
    _defaultConfig: function () {
        var conf = BI.GlobalStyleUserCustomButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: conf.baseCls + " bi-global-style-user-custom-button",
            text: "",
            selected: false,
            value: null,
            cannotDelete: false
        })
    },

    _init: function () {
        BI.GlobalStyleUserCustomButton.superclass._init.apply(this, arguments);
        var o = this.options;
        var self = this;
        this.button = BI.createWidget({
            type: "bi.global_style_style_button",
            title: o.text,
            value: o.value
        });
        this.deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-red-font"
        });
        this.deleteButton.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.GlobalStyleUserCustomButton.EVENT_DELETE);
        });
        this.widget = BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.button
            }, {
                el: this.deleteButton
            }],
            height: 70,
            width: 110,
            rgap: 0,
            tgap: 0
        });
        this.deleteButton.setVisible(false);
        if (!this.options.cannotDelete) {
            this.widget.element.hover(function () {
                self.deleteButton.setVisible(true);
            }, function () {
                self.deleteButton.setVisible(false);
            })
        }
    },

    getValue: function () {
        return this.button.getValue()
    },

    setValue: function (v) {
        this.button.setValue(v);
    },

    doClick: function () {
        BI.GlobalStyleUserCustomButton.superclass.doClick.apply(this, arguments);
        if (this.isValid()) {
            this.fireEvent(BI.GlobalStyleUserCustomButton.EVENT_CHANGE);
        }
    }
});
BI.GlobalStyleUserCustomButton.EVENT_CHANGE = "BI.GlobalStyleUserCustomButton.EVENT_CHANGE";
BI.GlobalStyleUserCustomButton.EVENT_DELETE = "BI.GlobalStyleUserCustomButton.EVENT_DELETE";
$.shortcut("bi.global_style_user_custom_button", BI.GlobalStyleUserCustomButton);