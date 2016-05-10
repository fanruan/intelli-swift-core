/**
 * Created by GameJian on 2016/3/14.
 */
BIShow.WebWidgetModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.WebWidgetModel.superclass._defaultConfig.apply(this, arguments), {});
    },

    _init: function () {
        BIShow.WebWidgetModel.superclass._init.apply(this, arguments);
    },

    refresh: function () {

    },

    local: function () {
        return false;
    },

    updateURL: function () {
        return this.cmd("widget_setting");
    }
});
