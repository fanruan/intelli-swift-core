/**
 * Created by Young's on 2016/5/9.
 */
BIShow.GeneralQueryModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.NumberWidgetModel.superclass._defaultConfig.apply(this), {
            name: "",
            bounds: {},
            type: BICst.WIDGET.GENERAL_QUERY,
            value: []
        })
    },

    _init: function(){
        BIShow.GeneralQueryModel.superclass._init.apply(this, arguments);
    }
});