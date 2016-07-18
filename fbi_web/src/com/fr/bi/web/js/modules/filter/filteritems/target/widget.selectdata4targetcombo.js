/**
 *
 * @class BI.SelectFieldDataCombo
 * @extends BI.Widget
 */
BI.SelectFieldDataCombo = BI.inherit(BI.Widget, {

    _const: {
        perPage: 10
    },

    _defaultConfig: function () {
        return BI.extend(BI.SelectFieldDataCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-field-data-combo",
            height: 28,
            field_id: ""
        });
    },

    _init: function () {
        BI.SelectFieldDataCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.combo = BI.createWidget({
            type: 'bi.multi_select_combo',
            element: this.element,
            itemsCreator: BI.bind(this._itemsCreator, this),
            width: o.width,
            height: o.height
        });

        this.func = BI.Utils.getWidgetDataByDimensionInfo({field_id: o.field_id});

        switch (BI.Utils.getFieldTypeByID(o.field_id)) {
            case BICst.COLUMN.DATE:
                this.func.setDimensionType(BICst.TARGET_TYPE.DATE);
                break;
            case BICst.COLUMN.NUMBER:
                this.func.setDimensionType(BICst.TARGET_TYPE.NUMBER);
                break;
            case BICst.COLUMN.STRING:
                this.func.setDimensionType(BICst.TARGET_TYPE.STRING);
                break;
        }

        this.combo.on(BI.MultiSelectCombo.EVENT_CONFIRM, function () {
            self.fireEvent(BI.SelectFieldDataCombo.EVENT_CONFIRM);
        });
    },

    _itemsCreator: function(options, callback){
        var o = this.options, self = this;
        self.func.setOptions(options);
        if(options.times === 1){
            self.func.first(function (res) {
                callback({
                    items: self._createItemsByData(res.value),
                    hasNext: res.hasNext
                });
            });
            return;
        }
        if(options.type === BI.MultiSelectCombo.REQ_GET_ALL_DATA){
            self.func.all(function(res){
                callback({
                    items: self._createItemsByData(res.value),
                    hasNext: res.hasNext
                });
            });
            return;
        }
        if(options.type === BI.MultiSelectCombo.REQ_GET_DATA_LENGTH){
            self.func.all(function(res){
                callback({count: res.value});
            });
            return;
        }
        self.func.next(function(res){
            callback({
                items: self._createItemsByData(res.value),
                hasNext: res.hasNext
            });
        });
    },

    _createItemsByData: function (values) {
        var result = [];
        BI.each(values, function(idx, value){
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
BI.SelectFieldDataCombo.EVENT_CONFIRM = "SelectFieldDataCombo.EVENT_CONFIRM";
$.shortcut('bi.select_field_data_combo', BI.SelectFieldDataCombo);