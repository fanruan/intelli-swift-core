/**
 * Created by Young's on 2016/5/19.
 */
BI.AuthorityFilter = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.AuthorityFilter.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-authority-filter"
        })
    },

    _init: function () {
        BI.AuthorityFilter.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.filter = BI.createWidget({
            type: "bi.filter",
            element: this.element,
            el: {
                type: "bi.filter_operation",
                selections: [BICst.FILTER_OPERATION_CONDITION]
            },
            itemCreator: function (item) {
                var t = BI.AuthorityFilterItemFactory.createFilterItemByFilterType(item.value);
                if (item.value === BICst.FILTER_TYPE.EMPTY_CONDITION) {
                    t.type = "bi.authority_no_type_field_filter_item";
                }
                item.type = t.type;
            },
            expander: {
                type: "bi.andor_filter_expander"
            }
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

    populate: function (filter) {
        filter = filter || [];
        this._transformConditions2Tree(filter);
        this.filter.populate(filter);
    },

    getValue: function () {
        return this.filter.getValue();
    }
});

$.shortcut("bi.authority_filter", BI.AuthorityFilter);