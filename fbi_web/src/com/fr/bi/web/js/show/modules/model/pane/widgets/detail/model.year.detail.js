//控件详细设置界面选择图表类型
BIShow.YearDetailModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.YearDetailModel.superclass._defaultConfig.apply(this, arguments), {
            dimensions: {},
            view: {},
            name: "",
            type: BICst.WIDGET.YEAR,
            filter_value: {}
        });
    },

    _static: function () {

    },

    change: function (changed) {

    },

    splice: function (old, key1, key2) {
    },

    local: function () {
        return false;
    },

    _init: function () {
        BIShow.YearDetailModel.superclass._init.apply(this, arguments);
    }
});
