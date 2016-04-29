/**
 * Created by Young's on 2016/3/23.
 */
BI.IconMarkCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.IconMarkCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-icon-mark-combo"
        })
    },

    _init: function(){
        BI.IconMarkCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var trigger = BI.createWidget({
            type: "bi.icon_mark_trigger"
        });
        var popup = BI.createWidget({
            type: "bi.icon_mark_popup",
            items: [{
                value: BICst.TARGET_STYLE.ICON_STYLE.NONE
            }, {
                value: BICst.TARGET_STYLE.ICON_STYLE.POINT
            }, {
                value: BICst.TARGET_STYLE.ICON_STYLE.ARROW
            }]
        });
        this.combo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            el: trigger,
            popup: {
                el: popup,
                maxWidth: 300
            }
        });
        popup.on(BI.IconMarkPopup.EVENT_CHANGE, function(){
            self.combo.hideView();
        });
        this.combo.setValue([o.icon_style]);
        this.combo.on(BI.Combo.EVENT_CHANGE, function(){
            self.fireEvent(BI.IconMarkCombo.EVENT_CHANGE);
        });
    },

    getValue: function(){
        return this.combo.getValue();
    },

    setValue: function(v){
        this.combo.setValue(v);
    }
});
BI.IconMarkCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.icon_mark_combo", BI.IconMarkCombo);