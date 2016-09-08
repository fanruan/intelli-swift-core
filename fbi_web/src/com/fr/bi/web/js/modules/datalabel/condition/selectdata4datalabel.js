/**
 * Created by fay on 2016/9/8.
 */
BI.SelectDataLabelDataCombo = BI.inherit(BI.SelectDimensionDataCombo, {

    _init: function () {
        BI.SelectDimensionDataCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.combo = BI.createWidget({
            type: 'bi.multi_select_combo',
            element: this.element,
            itemsCreator: BI.bind(this._itemsCreator, this),
            valueFormatter: function (v) {
                var items = v.split("-");
                BI.each(items, function (idx, item) {
                    items[idx] = BI.parseInt(item)
                });
                return items.join("/");
            },
            width: o.width,
            height: o.height
        });

        this.dimension = {
            name: BI.Utils.getDimensionNameByID(o.dId),
            _src: BI.Utils.getDimensionSrcByID(o.dId),
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

    _itemsCreator: function (options, callback) {
        var o = this.options, self = this;
        var optionsResult = BI.deepClone(options);
        BI.each(optionsResult.selected_values, function (idx, value) {
            optionsResult.selected_values[idx] = new Date(value.split("-").join("/")).getTime();
        });
        BI.Utils.getWidgetDataByWidgetInfo({
            "1234567": self.dimension
        }, {
            "10000": ["1234567"]
        }, function (data) {
            if (optionsResult.type == BI.MultiSelectCombo.REQ_GET_ALL_DATA) {
                callback({
                    items: self._createItemsByData(data.value)
                });
                return;
            }
            if (optionsResult.type == BI.MultiSelectCombo.REQ_GET_DATA_LENGTH) {
                callback({count: data.value});
                return;
            }
            callback({
                items: self._createItemsByData(data.value),
                hasNext: data.hasNext
            });
        }, {
            type: BICst.WIDGET.STRING,
            page: -1,
            text_options: optionsResult
        });
    },

    _createItemsByData: function (values) {
        var self = this, result = [];
        BI.each(values, function (idx, value) {
            var group = BI.Utils.getDimensionGroupByID(self.options.dId);
            var date = new Date(BI.parseInt(value));
            var year = date.getFullYear(),
                month = '' + (date.getMonth() + 1),
                day = '' + date.getDate();
            if (BI.isNotNull(group) && group.type === BICst.GROUP.YMD) {
                var text = [year, month, day].join('/');
                if (month.length < 2) {
                    month = '0' + month;
                }
                if (day.length < 2) {
                    day = '0' + day;
                }
                var dateValue =  [year, month, day].join('-');
                result.push({
                    text: text,
                    value: dateValue,
                    title: text
                })
            } else {
                if (month.length < 2) {
                    month = '0' + month;
                }
                if (day.length < 2) {
                    day = '0' + day;
                }
                var dateValue =  [year, month, day].join('-');
                result.push({
                    text: value,
                    value: dateValue,
                    title: value
                })
            }
        });
        return result;
    }
});
$.shortcut('bi.select_data_label_data_combo', BI.SelectDataLabelDataCombo);