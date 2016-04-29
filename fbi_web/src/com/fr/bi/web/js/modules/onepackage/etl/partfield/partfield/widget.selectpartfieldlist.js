/**
 * @class BI.SelectPartFieldList
 * @extends BI.Widget
 * 选择部分字段所用列表
 */

BI.SelectPartFieldList = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectPartFieldList.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-part-field-list"
        })
    },

    _init: function () {
        BI.SelectPartFieldList.superclass._init.apply(this, arguments);
        var self = this;
        this.list = BI.createWidget({
            type: "bi.select_list",
            element: this.element,
            el: {
                type: "bi.list_pane",
                el: {
                    chooseType: BI.ButtonGroup.CHOOSE_TYPE_MULTI,
                    layouts: [{
                        type: "bi.vertical"
                    }]
                }
            }
        });

        this.list.on(BI.SelectList.EVENT_CHANGE, function () {
            self.fireEvent(BI.SelectPartFieldList.EVENT_CHANGE);
        });
    },

    _getDataByFields: function (fields) {
        var items = [];
        BI.each(fields, function (i, field) {
            items.push({
                type: "bi.multi_select_item",
                selected: true,
                height: 25,
                text: field["field_name"],
                value: field["field_name"]
            })
        });
        return items;
    },

    populate: function (items) {
        this.list.populate(this._getDataByFields(items));
    },

    getValue: function () {
        return this.list.getValue();
    },

    setValue: function (v) {
        this.list.setValue(v);
    },

    setNotSelectedValue: function (v) {
        this.list.setNotSelectedValue(v);
    },

    getNotSelectedValue: function () {
        return this.list.getNotSelectedValue();
    }
});
BI.SelectPartFieldList.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.select_part_field_list", BI.SelectPartFieldList);