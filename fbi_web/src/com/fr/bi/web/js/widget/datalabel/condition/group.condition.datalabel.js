/**
 * Created by lfhli on 2016/7/15.
 */
BI.DataLabelConditionGroup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.DataLabelConditionGroup.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {

        });
    },

    _init: function () {
        BI.DataLabelConditionGroup.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.buttonGroup = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            items: o.items,
            layouts: [{
                type: "bi.vertical",
                hgap: 10,
                vgap: 2
            }]
        });

        this.buttons = this.buttonGroup.getAllButtons();
    },

    addItem: function () {
        var o = this.options;
        var item = {
            type: "bi.data_label_condition_item",
            field_id: BI.Utils.getFieldIDByDimensionID(o.dId),
            id: BI.UUID()
        };
        this.buttonGroup.addItems([item]);
        this.buttons = this.buttonGroup.getAllButtons();
    },

    populate: function () {
        var o = this.options;
        var conditions = BI.Utils.getDimensionDatalabelByID(o.dId);
        var items = [];
        var item = {};
        BI.each(conditions, function (idx, cdt) {
            var type = BI.DataLabelFilterItemFactory.createFilterItemByFilterType(cdt.filter_type);
            items.push({
                type: type.type,
                id: cdt.id,
                _src: cdt._src,
                filter_type: cdt.filter_type,
                filter_value: cdt.filter_value,
                style_setting: cdt.style_setting,
                field_id: BI.Utils.getFieldIDByDimensionID(o.dId)
            });
            item = {
                filter_type: cdt.filter_type,
                filter_value: cdt.filter_value,
                style_setting: cdt.style_setting,
            };
        });
        this.buttonGroup.addItems(items);
        this.buttons = this.buttonGroup.getAllButtons();
    },

    getValue: function () {
        var result = [];
        BI.each(this.buttons, function (i, el) {
            if (el.getValue() !== ""){
                result.push(el.getValue());
            }
        });
        return result;
    }
});
BI.DataLabelConditionGroup.EVENT_CHANGE = "BI.DataLabelConditionGroup.EVENT_CHANGE";
$.shortcut("bi.data_label_condition_group", BI.DataLabelConditionGroup);