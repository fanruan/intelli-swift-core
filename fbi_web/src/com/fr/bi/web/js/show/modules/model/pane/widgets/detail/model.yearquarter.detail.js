//控件详细设置界面选择图表类型
BIShow.DateQuarterDetailModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.DateQuarterDetailModel.superclass._defaultConfig.apply(this, arguments), {
            dimensions: {},
            view: {},
            name: "",
            type: BICst.Widget.QUARTER,
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
        BIShow.DateQuarterDetailModel.superclass._init.apply(this, arguments);
    }
});
