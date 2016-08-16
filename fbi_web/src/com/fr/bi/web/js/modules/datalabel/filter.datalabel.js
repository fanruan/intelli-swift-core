/**
 * Created by fay on 2016/8/10.
 */
BI.DataLabelFilter = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DataLabelFilter.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-filter",
            dId: ""
        })
    },

    _init: function () {
        BI.DataLabelFilter.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.filter = BI.createWidget({
            type: "bi.data_label_filter",
            itemCreator: function (item) {
                var t = BI.DataLabelFilterItemFactory.createFilterItemByFilterType(item.value);
                item.type = t.type;
                item.field_id = BI.Utils.getFieldIDByDimensionID(o.dId);
                item.dId = o.dId;
                item.afterValueChange = function () {
                    self.fireEvent(BI.DataLabelFilter.EVENT_CHANGE);
                };
            },
            expander: {
                type: "bi.data_label_andor_filter_expander"
            },
            element: this.element
        });

        this.filter.on(BI.Filter.EVENT_CHANGE, function () {
            self.fireEvent(BI.DataLabelFilter.EVENT_CHANGE);
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
        var conditions = BI.Utils.getDatalabelByID(o.dId);
        if (BI.isNotEmptyObject(conditions[0])) {
            conditions = [conditions[0]];
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
BI.DataLabelFilter.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.data_label2", BI.DataLabelFilter);