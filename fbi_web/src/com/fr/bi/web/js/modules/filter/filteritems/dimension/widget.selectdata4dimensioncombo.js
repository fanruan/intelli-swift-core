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
            height: 28,
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
            valueFormatter: function (v) {
                var text = v;
                if (BI.Utils.getDimensionTypeByID(o.dId) === BICst.TARGET_TYPE.DATE && (v + "").length > 4) {
                    var date = new Date(BI.parseInt(v));
                    text = date.getFullYear() + "/" + (date.getMonth() + 1) + "/" + date.getDate();
                }
                if(BI.Utils.getDimensionGroupByID(o.dId) === BICst.GROUP.M){
                    text = BI.parseInt(v) + 1;
                }
                return text;
            },
            width: o.width,
            height: o.height
        });

        this.dimension = {
            name: BI.Utils.getDimensionNameByID(o.dId),
            _src: BI.Utils.getDimensionSrcByID(o.dId),
            group: BI.Utils.getDimensionGroupByID(o.dId),
            sort: BI.Utils.getDimensionSortByID(o.dId),
            dimension_map: BI.Utils.getDimensionMapByDimensionID(o.dId)
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

        var dimensions = {};
        var view = {};
        dimensions[o.dId] = this.dimension;
        view[BICst.REGION.DIMENSION1] = [o.dId];
        var targetIds = BI.Utils.getAllTargetDimensionIDs(BI.Utils.getWidgetIDByDimensionID(o.dId));
        BI.each(targetIds, function(idx, targetId){
            dimensions[targetId] = Data.SharingPool.get("dimensions", targetId);
            //去掉指标的过滤条件，因为指标对日期做过滤的话是需要做计算转化的，干脆不要了
            delete dimensions[targetId].filter_value;
            if(!BI.has(view, BICst.REGION.TARGET1)){
                view[BICst.REGION.TARGET1] = [];
            }
            view[BICst.REGION.TARGET1].push(targetId);
        });

        BI.Utils.getWidgetDataByWidgetInfo(dimensions, view, function (data) {
            if (options.type == BI.MultiSelectCombo.REQ_GET_ALL_DATA) {
                callback({
                    items: self._createItemsByData(data.value)
                });
                return;
            }
            if (options.type == BI.MultiSelectCombo.REQ_GET_DATA_LENGTH) {
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
            text_options: options
        });
    },

    _createItemsByData: function (values) {
        var self = this, result = [];
        BI.each(values, function (idx, value) {
            var group = BI.Utils.getDimensionGroupByID(self.options.dId);
            if (BI.isNotNull(group) && group.type === BICst.GROUP.YMD) {
                var date = new Date(BI.parseInt(value));
                var text = date.getFullYear() + "/" + (date.getMonth() + 1) + "/" + date.getDate();
                result.push({
                    text: text,
                    value: value,
                    title: text
                })
            } else {
                var text = (BI.isNotNull(group) && group.type === BICst.GROUP.M) ? BI.parseInt(value) + 1 : value
                result.push({
                    text:  text,
                    value: value,
                    title: text
                })
            }
        });
        return result;
    },

    _assertValue: function (v) {
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
        var val = this.combo.getValue() || {};
        return {
            type: val.type,
            value: val.value
        }
    },

    populate: function () {
        this.combo.populate();
    }
});
BI.SelectDimensionDataCombo.EVENT_CONFIRM = "SelectDimensionDataCombo.EVENT_CONFIRM";
$.shortcut('bi.select_dimension_data_combo', BI.SelectDimensionDataCombo);