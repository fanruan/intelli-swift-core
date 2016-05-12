/**
 * Created by GameJian on 2016/3/14.
 */
BIDezi.ImageWidgetModel = BI.inherit(BI.Model, {
    _defaultConfig: function() {
        return BI.extend(BIDezi.ImageWidgetModel.superclass._defaultConfig.apply(this,arguments), {
            href: "",
            size: "original",
            src: "",
            settings: BICst.DEFAULT_CONTROL_SETTING
        });
    },

    _init: function(){
        BIDezi.ImageWidgetModel.superclass._init.apply(this, arguments);
    },

    refresh: function(){

    },

    local: function(){
        return false;
    },

    updateURL: function(){
        return this.cmd("widget_setting");
    }
});