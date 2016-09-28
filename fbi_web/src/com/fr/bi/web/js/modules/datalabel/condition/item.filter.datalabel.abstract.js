/**
 * Created by fay on 2016/8/25.
 */
BI.AbstractDataLabelFilterItem = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AbstractFilterItem.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BI.AbstractFilterItem.superclass._init.apply(this, arguments);
    }

});

BI.AbstractDataLabelFilterItem.DELETE = "BI.AbstractDataLabelFilterItem.DELETE";