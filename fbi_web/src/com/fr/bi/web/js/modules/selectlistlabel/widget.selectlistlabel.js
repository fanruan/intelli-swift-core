/**
 * Created by fay on 2016/10/10.
 */
BI.SelectListLabel = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectListLabel.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-list-label",
            wId: ""
        });
    },

    _init: function () {
        BI.SelectListLabel.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.listLabel = BI.createWidget({
            type: "bi.list_label",
            element: this.element
        });
        this.listLabel.on(BI.ListLabel.EVENT_CHANGE, function () {
            self.fireEvent(BI.SelectListLabel.EVENT_CONFIRM, this);
        })
    },

    populate: function () {
        var o = this.options, self = this;
        var dimensions = BI.Utils.getAllDimDimensionIDs(o.wId);
        if (dimensions.length === 0) {
            self.listLabel.populate([]);
        } else {
            var labels = BI.Utils.getDimensionNameByID(dimensions[0]);
            BI.Utils.getWidgetDataByID(o.wId, function (data) {
                var result = [];
                BI.each(data.value, function (idx, dt) {
                    result.push({
                        value: dt,
                        text: dt,
                        title: dt
                    })
                });
                self.listLabel.populate({
                    title: labels,
                    items: result
                });
                self.setValue(BI.Utils.getWidgetValueByID(o.wId));
            }, {text_options: {times: 1}});
        }
    },

    setValue: function (v) {
        v = v || {};
        if (v.type === 1) {
            this.listLabel.setValue(v.value);
        } else {
            this.listLabel.setValue([BICst.LIST_LABEL_TYPE.ALL]);
        }
    },

    getValue: function () {
        var value = this.listLabel.getValue();
        if (BI.contains(value, BICst.LIST_LABEL_TYPE.ALL)) {
            return {
                type: 2,
                value: []
            }
        } else {
            return {
                type: 1,
                value: value
            }
        }
    }
});

BI.SelectListLabel.EVENT_CONFIRM = "SelectListLabel.EVENT_CONFIRM";
$.shortcut('bi.select_list_label', BI.SelectListLabel);