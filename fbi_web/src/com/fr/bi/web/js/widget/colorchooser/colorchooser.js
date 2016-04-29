/**
 * 选色控件
 *
 * Created by GUY on 2015/11/17.
 * @class BI.ColorChooser
 * @extends BI.Widget
 */
BI.ColorChooser = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ColorChooser.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-color-chooser",
            el: {}
        })
    },

    _init: function () {
        BI.ColorChooser.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.trigger = BI.createWidget(BI.extend({
            type: "bi.color_chooser_trigger"
        }, o.el));
        this.colorPicker = BI.createWidget({
            type: "bi.color_chooser_popup"
        });

        this.combo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            adjustLength: 1,
            el: this.trigger,
            popup: {
                el: this.colorPicker,
                stopPropagation: false,
                minWidth: 202
            }
        });

        this.colorPicker.on(BI.ColorChooserPopup.EVENT_CHANGE, function () {
            var color = this.getValue();
            self.trigger.setValue(color);
            var colors = BI.string2Array(BI.Cache.getItem("colors") || "");
            colors.remove(color);
            colors.unshift(color);
            colors = colors.slice(0, 8);
            BI.Cache.setItem("colors", BI.array2String(colors));
            self.combo.hideView();
            self.fireEvent(BI.ColorChooser.EVENT_CHANGE, arguments);
        });
        this.combo.on(BI.Combo.EVENT_BEFORE_POPUPVIEW, function () {
            self.colorPicker.setStoreColors(BI.string2Array(BI.Cache.getItem("colors") || ""));
        });
    },

    isViewVisible: function(){
        return this.combo.isViewVisible();
    },

    setValue: function (color) {
        this.combo.setValue(color);
    },

    getValue: function () {
        return this.colorPicker.getValue();
    }
});
BI.ColorChooser.EVENT_CHANGE = "ColorChooser.EVENT_CHANGE";
$.shortcut("bi.color_chooser", BI.ColorChooser);