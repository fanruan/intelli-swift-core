BIShow.NumberWidgetModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.NumberWidgetModel.superclass._defaultConfig.apply(this, arguments), {});
    },

    _init: function () {
        BIShow.NumberWidgetModel.superclass._init.apply(this, arguments);
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
