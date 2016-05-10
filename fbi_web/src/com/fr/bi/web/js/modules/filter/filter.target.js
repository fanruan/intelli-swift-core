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
            type: "bi.filter",
            itemCreator: function (item) {
                var t = BI.TargetFilterItemFactory.createFilterItemByFilterType(item.value);
                item.type = t.type;
                item.field_id = BI.Utils.getFieldIDByDimensionID(o.dId);
                item.afterValueChange = function () {
                    self.fireEvent(BI.CommonFilter.EVENT_CHANGE);
                };
            },
            expander: {
                type: "bi.andor_filter_expander"
            },
            element: this.element
        });

        this.filter.on(BI.Filter.EVENT_CHANGE, function () {
            self.fireEvent(BI.CommonFilter.EVENT_CHANGE);
        });
    },

    transformConditions2Tree: function (conditions) {
        var self = this;
        BI.each(conditions, function (i, condition) {
            condition.id || (condition.id = BI.UUID());
            condition.value = condition.filter_type;
            if (condition.filter_type === BICst.FILTER_TYPE.AND || condition.filter_type === BICst.FILTER_TYPE.OR) {
                condition.children = condition.filter_value;
                self.transformConditions2Tree(condition.children);
            }
        })
    },

    populate: function () {
        var o = this.options;
        var conditions = BI.Utils.getDimensionFilterValueByID(o.dId) || [];
        conditions = BI.isArray(conditions) ? conditions : [conditions];
        this.transformConditions2Tree(conditions);
        this.filter.populate(conditions);
    },

    getValue: function () {
        return this.filter.getValue();
    }
});
BI.TargetFilter.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.target_filter", BI.TargetFilter);