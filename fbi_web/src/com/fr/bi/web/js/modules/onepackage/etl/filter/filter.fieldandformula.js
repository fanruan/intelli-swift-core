/**
 * 指标过滤
 *
 * Created by GUY on 2015/11/20.
 * @class BI.FieldFilter
 * @extend BI.Widget
 */
BI.FieldFilter = BI.inherit(BI.Widget, {

    constants: {
        FIELD_TYPE_NUMBER: 1,
        FIELD_TYPE_STRING: 0,
        FIELD_TYPE_DATE: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.FieldFilter.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-filter",
            table: {}
        })
    },

    _init: function () {
        BI.FieldFilter.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.filter = BI.createWidget({
            type: "bi.filter",
            itemCreator: function (item) {
                var t = BI.ConfFilterItemFactory.createFilterItemByFilterType(item.value);
                item.type = t.type;
                item.table = o.table;
                item.afterValueChange = function(){
                    self.fireEvent(BI.FieldFilter.EVENT_CHANGE);
                }
            },
            expander: {
                type: "bi.conf_andor_filter_expander"
            },
            element: this.element
        });

        this.filter.on(BI.Filter.EVENT_CHANGE, function(){
            self.fireEvent(BI.FieldFilter.EVENT_CHANGE);
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

    populate: function (obj) {
        var o = this.options, conditions = obj.conditions;
        o.table = obj.table;
        conditions = BI.isArray(conditions) ? conditions : [conditions];
        this.transformConditions2Tree(conditions);
        this.filter.populate(conditions);
    },

    getValue: function () {
        return this.filter.getValue();
    },

    isEmpty: function(){
        return BI.isEmpty(this.filter.getValue());
    }
});
BI.FieldFilter.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.field_filter", BI.FieldFilter);