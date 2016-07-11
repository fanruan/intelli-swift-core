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
        var filter = this.filter.getValue()[0];
        if(BI.isNotNull(filter)) {
            return this.parseFilter(filter);
        }
    },

    parseFilter: function (filter) {
        var self = this;
        var filterType = filter.filter_type, filterValue = filter.filter_value;
        if (BI.isEmptyObject(filterValue)) {
            return filter;
        }
        if (filterType === BICst.FILTER_TYPE.AND || filterType === BICst.FILTER_TYPE.OR) {
            BI.each(filterValue, function (i, value) {
                self.parseFilter(value);
            });
        }
        if (filterType === BICst.FILTER_DATE.BELONG_DATE_RANGE || filterType === BICst.FILTER_DATE.NOT_BELONG_DATE_RANGE) {
            var start = filterValue.start, end = filterValue.end;
            if (BI.isNull(start)) {
                delete filterValue.start;
            }
            if (BI.isNull(end)) {
                delete filterValue.end;
            }
        }

        if (filterType === BICst.FILTER_DATE.EQUAL_TO || filterType === BICst.FILTER_DATE.NOT_EQUAL_TO) {
            filterValue.values = parseComplexDate(filterValue);
            filterValue.type = filterValue.type || BICst.MULTI_DATE_CALENDAR
        }
        return filter;

        function parseComplexDate(v) {
            var type = v.type, value = v.value;
            var date = new Date();
            var currY = date.getFullYear(), currM = date.getMonth(), currD = date.getDate();
            var tool = new BI.MultiDateParamTrigger();
            if (BI.isNull(type) && BI.isNotNull(v.year)) {
                return new Date(v.year, v.month, v.day).getTime();
            }
            switch (type) {
                case BICst.MULTI_DATE_YEAR_PREV:
                    return new Date(currY - 1 * value, currM, currD).getTime();
                case BICst.MULTI_DATE_YEAR_AFTER:
                    return new Date(currY + 1 * value, currM, currD).getTime();
                case BICst.MULTI_DATE_YEAR_BEGIN:
                    return new Date(currY, 1, 1).getTime();
                case BICst.MULTI_DATE_YEAR_END:
                    return new Date(currY, 11, 31).getTime();
                case BICst.MULTI_DATE_MONTH_PREV:
                    return tool._getBeforeMultiMonth(value).getTime();
                case BICst.MULTI_DATE_MONTH_AFTER:
                    return tool._getAfterMultiMonth(value).getTime();
                case BICst.MULTI_DATE_MONTH_BEGIN:
                    return new Date(currY, currM, 1).getTime();
                case BICst.MULTI_DATE_MONTH_END:
                    return new Date(currY, currM, (date.getLastDateOfMonth()).getDate()).getTime();
                case BICst.MULTI_DATE_QUARTER_PREV:
                    return tool._getBeforeMulQuarter(value).getTime();
                case BICst.MULTI_DATE_QUARTER_AFTER:
                    return tool._getAfterMulQuarter(value).getTime();
                case BICst.MULTI_DATE_QUARTER_BEGIN:
                    return tool._getQuarterStartDate().getTime();
                case BICst.MULTI_DATE_QUARTER_END:
                    return tool._getQuarterEndDate().getTime();
                case BICst.MULTI_DATE_WEEK_PREV:
                    return date.getOffsetDate(-7 * value).getTime();
                case BICst.MULTI_DATE_WEEK_AFTER:
                    return date.getOffsetDate(7 * value).getTime();
                case BICst.MULTI_DATE_DAY_PREV:
                    return date.getOffsetDate(-1 * value).getTime();
                case BICst.MULTI_DATE_DAY_AFTER:
                    return date.getOffsetDate(1 * value).getTime();
                case BICst.MULTI_DATE_DAY_TODAY:
                    return date.getTime();
                case BICst.MULTI_DATE_CALENDAR:
                    return new Date(value.year, value.month, value.day).getTime();
            }
        }
    }
});

$.shortcut("bi.authority_filter", BI.AuthorityFilter);