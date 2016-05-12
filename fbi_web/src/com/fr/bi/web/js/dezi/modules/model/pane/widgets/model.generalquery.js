/**
 * Created by Young's on 2016/5/9.
 */
BIDezi.GeneralQueryModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.NumberWidgetModel.superclass._defaultConfig.apply(this), {
            name: "",
            bounds: {},
            type: BICst.Widget.GENERAL_QUERY,
            value: [],
            settings: BICst.DEFAULT_CONTROL_SETTING
        })
    },

    _init: function(){
        BIDezi.GeneralQueryModel.superclass._init.apply(this, arguments);
    }
});