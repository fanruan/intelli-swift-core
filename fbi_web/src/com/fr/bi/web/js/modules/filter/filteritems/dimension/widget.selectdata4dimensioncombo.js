/**
 *
 * @class BI.SelectDimensionDataCombo
 * @extends BI.Widget
 */
BI.SelectDimensionDataCombo = BI.inherit(BI.Widget, {

    _const: {
        perPage: 10
    },

    _defaultConfig: function () {
        return BI.extend(BI.SelectDimensionDataCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-dimension-data-combo",
            height: 30,
            dId: ""
        });
    },

    _init: function () {
        BI.SelectDimensionDataCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.combo = BI.createWidget({
            type: 'bi.multi_select_combo',
            element: this.element,
            itemsCreator: BI.bind(this._itemsCreator, this),
            width: o.width,
            height: o.height
        });

        this.dimension = {
            name: BI.Utils.getDimensionNameByID(o.dId),
            _src: {
                field_id: BI.Utils.getFieldIDByDimensionID(o.dId)
            },
            group: BI.Utils.getDimensionGroupByID(o.dId),
            sort: BI.Utils.getDimensionSortByID(o.dId)
        };

        switch (BI.Utils.getFieldTypeByDimensionID(o.dId)) {
            case BICst.COLUMN.DATE:
                this.dimension.type = BICst.TARGET_TYPE.DATE;
                break;
            case BICst.COLUMN.NUMBER:
                this.dimension.type = BICst.TARGET_TYPE.NUMBER;
                break;
            case BICst.COLUMN.STRING:
                this.dimension.type = BICst.TARGET_TYPE.STRING;
                break;
        }

        this.combo.on(BI.MultiSelectCombo.EVENT_CONFIRM, function () {
            self.fireEvent(BI.SelectDimensionDataCombo.EVENT_CONFIRM);
        });
    },

    _itemsCreator: function(options, callback){
        var o = this.options, self = this;

        BI.Utils.getWidgetDataByWidgetInfo({
            height: 0,
            width: 0,
            left: 0,
            top: 0
        }, {
            "1234567": self.dimension
        }, {}, BICst.Widget.STRING, {
            "10000": ["1234567"]
        }, -1, function (data) {
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
    },

    _createItemsByData: function (data) {
        var result = [];
        BI.each(data.value, function(idx, value){
            result.push({
                text: value,
                value: value,
                title: value
            })
        });
        return result;
    },

    _assertValue: function(v){
        v = v || {};
        v.type = v.type || BI.Selection.Multi;
        v.value = v.value || [];
        return v;
    },

    setValue: function (v) {
        v = this._assertValue(v);
        this.combo.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue();
    },

    populate: function () {
        this.combo.populate();
    }
});
BI.SelectDimensionDataCombo.EVENT_CONFIRM = "SelectDimensionDataCombo.EVENT_CONFIRM";
$.shortcut('bi.select_dimension_data_combo', BI.SelectDimensionDataCombo);