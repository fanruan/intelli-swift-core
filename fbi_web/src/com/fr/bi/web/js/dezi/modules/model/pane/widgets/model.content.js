/**
 * Created by GameJian on 2016/3/4.
 */
BIDezi.ContentWidgetModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.ContentWidgetModel.superclass._defaultConfig.apply(this, arguments), {
            style: {},
            content: ""
        });
    },

    _init: function () {
        BIDezi.ContentWidgetModel.superclass._init.apply(this, arguments);
    },

    change: function (changed) {

    },

    refresh: function () {

    },

    local: function () {
        if (this.has("expand")) {
            this.get("expand");
            return true;
        }
        return false;
    }
});