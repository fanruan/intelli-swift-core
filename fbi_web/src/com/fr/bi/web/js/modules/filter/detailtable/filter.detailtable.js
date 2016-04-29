/**
 * Created by Young's on 2016/4/15.
 */
BI.DetailTableFilter = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.DetailTableFilter.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-table-filter"
        })
    },

    _init: function(){
        BI.DetailTableFilter.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var dId = o.dId;
        this.filter = BI.createWidget({
            type: "bi.filter",
            el: {
                type: "bi.filter_operation",
                selections: [BICst.FILTER_OPERATION_CONDITION]
            },
            itemCreator: function(item){
                var fieldType = BI.Utils.getFieldTypeByDimensionID(dId);
                switch (fieldType) {
                    case BICst.COLUMN.STRING:
                        item.type = "bi.target_string_field_filter_item";
                        break;
                    case BICst.COLUMN.NUMBER:
                        item.type = "bi.target_number_field_filter_item";
                        break;
                    case BICst.COLUMN.DATE:
                        item.type = "bi.date_field_filter_item";
                        break;
                }
                item.field_id = BI.Utils.getFieldIDByDimensionID(dId);
                item._src = {
                    field_id: BI.Utils.getFieldIDByDimensionID(dId)
                };
                if(item.value === BICst.FILTER_TYPE.EMPTY_CONDITION){
                    switch (fieldType) {
                        case BICst.COLUMN.STRING:
                            item.value = BICst.TARGET_FILTER_STRING.BELONG_VALUE;
                            item.filter_type = BICst.TARGET_FILTER_STRING.BELONG_VALUE;
                            break;
                        case BICst.COLUMN.NUMBER:
                            item.value = BICst.TARGET_FILTER_NUMBER.BELONG_VALUE;
                            item.filter_type = BICst.TARGET_FILTER_NUMBER.BELONG_VALUE;
                            break;
                        case BICst.COLUMN.DATE:
                            item.value = BICst.FILTER_DATE.BELONG_DATE_RANGE;
                            item.filter_type = BICst.FILTER_DATE.BELONG_DATE_RANGE;
                            break;
                    }
                    item.filter_value = {value: []};
                    item.node.set("data", item);
                }
            },
            expander: {
                type: "bi.andor_filter_expander"
            },
            element: this.element,
            dId: dId
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
$.shortcut("bi.detail_table_filter", BI.DetailTableFilter);