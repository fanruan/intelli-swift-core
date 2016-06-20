/**
 * Created by Young's on 2016/5/9.
 */
BIDezi.GeneralQueryModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.NumberWidgetModel.superclass._defaultConfig.apply(this), {
            name: "",
            bounds: {},
            type: BICst.WIDGET.GENERAL_QUERY,
            value: []
        })
    },

    _init: function () {
        BIDezi.GeneralQueryModel.superclass._init.apply(this, arguments);
    },

    local: function () {
        if (this.has("expand")) {
            this.get("expand");
            return true;
        }
        return false;
    }
});