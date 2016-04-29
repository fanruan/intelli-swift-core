/**
 * Created by roy on 15/11/17.
 */
BIShow.NumberDimensionModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.NumberDimensionModel.superclass._defaultConfig.apply(this, arguments), {});
    },

    _init: function () {
        BIShow.NumberDimensionModel.superclass._init.apply(this, arguments);
    },

    refresh: function () {

    },

    local: function () {

    }
});
