/**
 * Created by Young's on 2016/5/9.
 */
BI.GeneralQueryFilter = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.GeneralQueryFilter.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-general-query-filter",
            dId: ""
        })
    },

    _init: function () {
        BI.GeneralQueryFilter.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.filter = BI.createWidget({
            type: "bi.filter",
            el: {
                type: "bi.filter_operation",
                selections: [BICst.FILTER_OPERATION_CONDITION]
            },
            itemCreator: function(item){
                var t = BI.TargetFilterItemFactory.createFilterItemByFilterType(item.value);
                if(item.value === BICst.FILTER_TYPE.EMPTY_CONDITION) {
                    t.type = "bi.general_query_no_type_filter_item";
                }
                item.type = t.type;
                item.field_id = o.field_id;
                item.afterValueChange = function(){
                    self.fireEvent(BI.GeneralQueryFilter.EVENT_CHANGE);
                };
            },
            expander: {
                type: "bi.andor_filter_expander"
            },
            element: this.element
        });
        this.filter.on(BI.Filter.EVENT_CHANGE, function(){
            self.fireEvent(BI.GeneralQueryFilter.EVENT_CHANGE);
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

    populate: function (conditions) {
        conditions = BI.isArray(conditions) ? conditions : [conditions];
        this._transformConditions2Tree(conditions);
        this.filter.populate(conditions);
    },

    getValue: function () {
        return this.filter.getValue();
    }
});
BI.GeneralQueryFilter.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.general_query_filter", BI.GeneralQueryFilter);