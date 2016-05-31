/**
 * 维度过滤
 * @class BI.DimensionFilter
 * @extend BI.Widget
 */
BI.DimensionFilter = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DimensionFilter.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-filter",
            dId: ""
        })
    },

    _init: function () {
        BI.DimensionFilter.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.filter = BI.createWidget({
            type: "bi.filter",
            itemCreator: function(item){
                var t = BI.DimensionFilterItemFactory.createFilterItemByFilterType(item.value);
                item.type = t.type;
                item.dId = item.target_id || o.dId;
                item.afterValueChange = function(){
                    self.fireEvent(BI.DimensionFilter.EVENT_CHANGE);
                };
            },
            expander: {
                type: "bi.andor_filter_expander"
            },
            element: this.element
        });

        this.filter.on(BI.Filter.EVENT_CHANGE, function(){
            self.fireEvent(BI.DimensionFilter.EVENT_CHANGE);
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
        var conditions = BI.Utils.getDimensionFilterValueByID(o.dId);
        if (BI.isNotEmptyObject(conditions)) {
            conditions = [conditions];
        } else {
            conditions = [];
        }
        this.transformConditions2Tree(conditions);
        this.filter.populate(conditions);
    },

    getValue: function () {
        return this.filter.getValue();
    }
});
BI.DimensionFilter.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.dimension_filter", BI.DimensionFilter);