/**
 * 指标过滤
 * Created by GUY on 2015/11/20.
 * @class BI.TargetFilter
 * @extend BI.Widget
 */
BI.TargetFilter = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TargetFilter.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-filter",
            dId: ""
        })
    },

    _init: function () {
        BI.TargetFilter.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.filter = BI.createWidget({
            type: "bi.common_filter",
            element: this.element,
            field_id: BI.Utils.getFieldIDByDimensionID(o.dId)
        });

        this.filter.on(BI.CommonFilter.EVENT_CHANGE, function(){
            self.fireEvent(BI.TargetFilter.EVENT_CHANGE);
        });
    },

    populate: function () {
        var o = this.options;
        var conditions = BI.Utils.getDimensionFilterValueByID(o.dId) || [];
        this.filter.populate(conditions);
    },

    getValue: function () {
        return this.filter.getValue();
    }
});
BI.TargetFilter.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.target_filter", BI.TargetFilter);