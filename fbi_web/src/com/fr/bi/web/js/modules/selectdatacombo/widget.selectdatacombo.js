/**
 *
 * @class BI.SelectDataCombo
 * @extends BI.Widget
 */
BI.SelectDataCombo = BI.inherit(BI.Widget, {

    _const: {
        perPage: 10
    },

    _defaultConfig: function () {
        return BI.extend(BI.SelectDataCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-data-combo",
            height: 24,
            wId: ""
        });
    },

    _init: function () {
        BI.SelectDataCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.combo = BI.createWidget({
            type: 'bi.multi_select_combo',
            element: this.element,
            itemsCreator: BI.bind(this._itemsCreator, this),
            width: o.width,
            height: o.height
        });

        this.combo.on(BI.MultiSelectCombo.EVENT_CONFIRM, function () {
            self.fireEvent(BI.SelectDataCombo.EVENT_CONFIRM);
        });
    },

    _itemsCreator: function (options, callback) {
        var o = this.options, self = this;
        var dimensions = BI.Utils.getAllDimDimensionIDs(o.wId);
        if (dimensions.length === 0) {
            callback([]);
        } else {
            BI.Utils.getWidgetDataByID(o.wId, function (data) {
                if (options.type == BI.MultiSelectCombo.REQ_GET_ALL_DATA) {
                    callback({
                        items: self._createItemsByData(data)
                    });
                    return;
                }
                if (options.type == BI.MultiSelectCombo.REQ_GET_DATA_LENGTH) {
                    callback({count: data.value});
                    return;
                }
                callback({
                    items: self._createItemsByData(data),
                    hasNext: data.hasNext
                });
            }, {text_options: options});
        }
    },

    _createItemsByData: function (data) {
        var result = [];
        BI.each(data.value, function (idx, value) {
            result.push({
                text: value,
                value: value,
                title: value
            })
        });
        return result;
    },

    _assertValue: function (v) {
        if (BI.isEmpty(v)) {
            return {
                type: BI.Selection.Multi,
                value: []
            }
        }
        return v;
    },

    setValue: function (v) {
        var value = this._assertValue(v);
        this.combo.setValue(value);
    },

    getValue: function () {
        return this.combo.getValue();
    },

    populate: function () {
        var o = this.options;
        this.combo.populate();
        this.setValue(BI.Utils.getWidgetValueByID(o.wId));
    }
});
BI.SelectDataCombo.EVENT_CONFIRM = "SelectDataCombo.EVENT_CONFIRM";
$.shortcut('bi.select_data_combo', BI.SelectDataCombo);