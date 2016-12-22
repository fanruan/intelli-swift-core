/**
 * Created by zcf on 2016/12/16.
 */
BI.SelectDataStringList = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.SelectDataStringList.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-data-string-list",
            wId: ""
        });
    },

    _init: function () {
        BI.SelectDataStringList.superclass._init.apply(this, arguments);

        var self = this, o = this.options;

        this.list = BI.createWidget({
            type: 'bi.multi_string_list',
            itemsCreator: BI.bind(this._itemsCreator, this)
        });

        this.list.on(BI.MultiStringList.EVENT_CHANGE, function () {
            self.fireEvent(BI.SelectDataStringList.EVENT_CHANGE);
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.list
            }]
        })
    },

    _itemsCreator: function (options, callback) {
        var o = this.options, self = this;
        var dimensions = BI.Utils.getAllDimDimensionIDs(o.wId);
        if (dimensions.length === 0) {
            callback([]);
        } else {
            BI.Utils.getWidgetDataByID(o.wId, {
                success: function (data) {
                    if (options.type == BI.MultiStringList.REQ_GET_ALL_DATA) {
                        callback({
                            items: self._createItemsByData(data)
                        });
                        return;
                    }
                    if (options.type == BI.MultiStringList.REQ_GET_DATA_LENGTH) {
                        callback({count: data.value});
                        return;
                    }
                    callback({
                        items: self._createItemsByData(data),
                        hasNext: data.hasNext
                    });
                }
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
        this.list.setValue(value);
    },

    getValue: function () {
        var val = this.list.getValue();
        return {
            type: val.type,
            value: val.value
        };
    },

    populate: function () {
        var o = this.options;
        this.list.setStoreValue(BI.Utils.getWidgetValueByID(o.wId));
        this.list.populate();
    }
});

BI.SelectDataStringList.EVENT_CHANGE = "SelectDataStringList.EVENT_CHANGE";
$.shortcut('bi.select_data_string_list', BI.SelectDataStringList);