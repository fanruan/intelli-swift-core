/**
 * Created by lfhli on 2016/7/15.
 */
BI.DataLabelConditionGroup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.DataLabelConditionGroup.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "data-label-group"
        });
    },

    _init: function () {
        BI.DataLabelConditionGroup.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.chartType = BI.Utils.getWidgetTypeByID(BI.Utils.getWidgetIDByDimensionID(o.dId));
        this.buttonGroup = BI.createWidget({
            type: "bi.button_group",
            cls: "",
            element: this.element,
            items: o.items,
            layouts: [{
                type: "bi.vertical",
                hgap: 10,
                tgap: 4
            }]
        });

        this.buttons = this.buttonGroup.getAllButtons();
    },

    addItem: function () {
        var o = this.options;
        var type;
        if (this.chartType === BICst.WIDGET.SCATTER) {
            type = "bi.scatter_no_type_field_filter_item";
        } else if (this.chartType === BICst.WIDGET.BUBBLE) {
            type = "bi.bubble_no_type_field_filter_item";
        } else {
            type = "bi.data_label_no_type_field_filter_item";
        }
        var item = {
            type: type,
            sdId: o.dId
        };
        this.buttonGroup.addItems([item]);
        this.buttons = this.buttonGroup.getAllButtons();
    },

    populate: function () {
        var self = this, o = this.options;
        var conditions = BI.Utils.getDatalabelByID(o.dId);
        var items = [];
        BI.each(conditions, function (idx, cdt) {
            var t = {};
            if (self.chartType === BICst.WIDGET.SCATTER) {
                t = BI.ScatterFilterItemFactory.createFilterItemByFilterType(cdt.filter_type);
            } else if (self.chartType === BICst.WIDGET.BUBBLE) {
                // t = BI.BubbleFilterItemFactory.createFilterItemByFilterType(cdt.filter_type);
            } else {
                if(cdt.target_id != o.dId && !BI.Utils.isDimensionUsable(cdt.target_id)) {
                    cdt = {};
                    cdt.filter_type = BICst.FILTER_TYPE.EMPTY_CONDITION;
                    cdt.sdId = o.dId;
                }
                t = BI.DataLabelFilterItemFactory.createFilterItemByFilterType(cdt.filter_type);
            }
            items.push({
                type: t.type,
                sdId: o.dId,
                dId: cdt.target_id,
                filter_type: cdt.filter_type,
                filter_value: cdt.filter_value,
                filter_range: cdt.filter_range,
                style_setting: cdt.style_setting
            });
        });
        this.buttonGroup.addItems(items);
        this.buttons = this.buttonGroup.getAllButtons();
    },

    getValue: function () {
        var result = [];
        BI.each(this.buttons, function (i, el) {
            if (el.getValue() !== "") {
                result.push(el.getValue());
            }
        });
        return result;
    }
});
BI.DataLabelConditionGroup.EVENT_CHANGE = "BI.DataLabelConditionGroup.EVENT_CHANGE";
$.shortcut("bi.data_label_condition_group", BI.DataLabelConditionGroup);