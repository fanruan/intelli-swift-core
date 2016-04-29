/**
 * 凭借tId获取所有过滤所需信息的过滤器
 * @class BI.CommonFilter
 * @extend BI.Widget
 */
BI.CommonFilter = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CommonFilter.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-common-filter",
            field_id: ""
        })
    },

    _init: function () {
        BI.CommonFilter.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.filter = BI.createWidget({
            type: "bi.filter",
            itemCreator: function(item){
                var t = BI.TargetFilterItemFactory.createFilterItemByFilterType(item.value);
                item.type = t.type;
                item.field_id = o.field_id;
                item.afterValueChange = function(){
                    self.fireEvent(BI.CommonFilter.EVENT_CHANGE);
                };
            },
            expander: {
                type: "bi.andor_filter_expander"
            },
            element: this.element
        });

        this.filter.on(BI.Filter.EVENT_CHANGE, function(){
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

    populate: function (conditions) {
        conditions = BI.isArray(conditions) ? conditions : [conditions];
        this.transformConditions2Tree(conditions);
        this.filter.populate(conditions);
    },

    getValue: function () {
        return this.filter.getValue();
    }
});
BI.CommonFilter.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.common_filter", BI.CommonFilter);