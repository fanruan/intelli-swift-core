/**
 * Created by GameJian on 2016/3/14.
 */
BIDezi.WebWidgetModel = BI.inherit(BI.Model, {
    _defaultConfig: function() {
        return BI.extend(BIDezi.WebWidgetModel.superclass._defaultConfig.apply(this,arguments), {
            url: ""
        });
    },

    _init: function(){
        BIDezi.WebWidgetModel.superclass._init.apply(this, arguments);
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