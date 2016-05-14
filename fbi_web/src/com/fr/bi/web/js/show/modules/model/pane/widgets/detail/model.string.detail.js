//控件详细设置界面选择图表类型
BIShow.StringDetailModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.StringDetailModel.superclass._defaultConfig.apply(this, arguments), {
            dimensions: {},
            view: {},
            name: "",
            type: BICst.WIDGET.STRING,
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
        BIShow.StringDetailModel.superclass._init.apply(this, arguments);
    }
});
