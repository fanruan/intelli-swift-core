/**
 * Created by Young's on 2016/3/22.
 * 汇总表表头上的过滤主面板
 */
BI.SummaryTargetFilter = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SummaryTargetFilter.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-filter",
            dId: ""
        })
    },

    _init: function () {
        BI.SummaryTargetFilter.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.filter = BI.createWidget({
            type: "bi.filter",
            el: {
                type: "bi.filter_operation",
                selections: [BICst.FILTER_OPERATION_CONDITION]
            },
            itemCreator: function(item){
                item.type = "bi.target_number_field_filter_item";
                item.field_id = BI.Utils.getFieldIDByDimensionID(o.dId);
                item._src = {
                    field_id: BI.Utils.getFieldIDByDimensionID(o.dId)
                };
                if(item.value === BICst.FILTER_TYPE.EMPTY_CONDITION){
                    item.value = BICst.TARGET_FILTER_NUMBER.BELONG_VALUE;
                    item.filter_type = BICst.TARGET_FILTER_NUMBER.BELONG_VALUE;
                    item.filter_value = {value: []};
                    item.node.set("data", item);
                }
            },
            expander: {
                type: "bi.andor_filter_expander"
            },
            element: this.element,
            dId: o.dId
        });
    },

    _transformConditions2Tree: function (conditions) {
        var self = this;
        BI.each(conditions, function (i, condition) {
            condition.id || (condition.id = BI.UUID());
            condition.value = condition.filter_type;
            if (condition.filter_type === BICst.FILTER_TYPE.AND || condition.filter_type === BICst.FILTER_TYPE.OR) {
                condition.children = condition.filter_value;
                self._transformConditions2Tree(condition.children);
            }
        })
    },

    populate: function () {
        var o = this.options;
        var targetFilters = BI.Utils.getWidgetFilterValueByID(BI.Utils.getWidgetIDByDimensionID(o.dId));
        var conditions = BI.isNotNull(targetFilters) ? targetFilters[o.dId] || [] : [];
        conditions = BI.isArray(conditions) ? conditions : [conditions];
        this._transformConditions2Tree(conditions);
        this.filter.populate(conditions);
    },

    getValue: function () {
        return this.filter.getValue();
    }
});
$.shortcut("bi.target_summary_filter", BI.SummaryTargetFilter);