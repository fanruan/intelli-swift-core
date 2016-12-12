/**
 * Created by fay on 2016/12/6.
 */
BI.AccumulationGroup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.AccumulationGroup.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function () {
        BI.AccumulationGroup.superclass._init.apply(this, arguments);

    },
});

$.shortcut("bi.accumulation_group", BI.AccumulationGroup);