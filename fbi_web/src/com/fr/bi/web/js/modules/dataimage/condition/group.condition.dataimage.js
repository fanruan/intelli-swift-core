/**
 * Created by fay on 2016/9/1.
 */
BI.DataImageConditionGroup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.DataImageConditionGroup.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "data-image-group data-label-group"
        });
    },

    _init: function () {
        BI.DataImageConditionGroup.superclass._init.apply(this, arguments);
        var o = this.options;
        this.wId = BI.Utils.getWidgetIDByDimensionID(o.dId);
        this.chartType = BI.Utils.getWidgetTypeByID(this.wId);
        this.buttonGroup = BI.createWidget({
            type: "bi.button_group",
            cls: "",
            element: this.element,
            items: o.items,
            layouts: [{
                type: "bi.vertical"
            }]
        });

        this.buttons = this.buttonGroup.getAllButtons();
    },

    addItem: function () {
        var o = this.options;
        var item = {
            type: "bi.data_image_no_type_field_filter_item",
            sdId: o.dId,
            chartType: this.chartType
        };
        this.buttonGroup.addItems([item]);
        this.buttons = this.buttonGroup.getAllButtons();
    },

    populate: function () {
        var self = this, o = this.options, conditions;
        conditions = BI.Utils.getDataimageByID(o.dId);
        var items = [];
        BI.each(conditions, function (idx, cdt) {
            var t = BI.DataImageFilterItemFactory.createFilterItemByFilterType(cdt.filter_type);
            items.push({
                type: t.type,
                sdId: o.dId,
                chartType: self.chartType,
                dId: cdt.target_id,
                filter_type: cdt.filter_type,
                filter_value: cdt.filter_value,
                style_setting: cdt.style_setting
            });
        });
        this.buttonGroup.addItems(items);
        this.buttons = this.buttonGroup.getAllButtons();
    },

    getValue: function () {
        var result = [];
        BI.each(this.buttons, function (i, el) {
            var value = el.getValue();
            if (!BI.isEmptyString(value)) {
                result.push(value);
            }
        });
        return {
            filterValues: result,
            images: this.images
        };
    }
});
BI.DataImageConditionGroup.EVENT_CHANGE = "BI.DataImageConditionGroup.EVENT_CHANGE";
$.shortcut("bi.data_image_condition_group", BI.DataImageConditionGroup);