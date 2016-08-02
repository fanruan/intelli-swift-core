/**
 * @class BI.ConfTargetDateFieldFilterItem
 * @extend BI.AbstractFilterItem
 * 指标过滤——日期字段类型过滤条件
 */
BI.ConfTargetDateFieldFilterItem = BI.inherit(BI.AbstractFilterItem, {

    _constant: {
        LEFT_ITEMS_H_GAP: 5,
        CONTAINER_HEIGHT: 40,
        BUTTON_HEIGHT: 30,
        COMBO_WIDTH: 80,
        FIELD_NAME_BUTTON_WIDTH: 90,
        TEXT_BUTTON_H_GAP: 10,
        INPUT_WIDTH: 230,
        TIME_INTERVAL_HEIGHT: 32
    },

    _defaultConfig: function () {
        return BI.extend(BI.ConfTargetDateFieldFilterItem.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-date-field-item",
            afterValueChange: BI.emptyFn
        })
    },

    _init: function () {
        BI.ConfTargetDateFieldFilterItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var left = this._buildConditions();

        this.deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-h-font"
        });
        this.deleteButton.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, BI.Events.DESTROY, o.id, self);
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                el: {
                    type: "bi.left_right_vertical_adapt",
                    height: this._constant.CONTAINER_HEIGHT,
                    items: {
                        left: [left[0], left[1], left[2]],
                        right: [this.deleteButton]
                    },
                    lhgap: this._constant.LEFT_ITEMS_H_GAP,
                    rhgap: this._constant.LEFT_ITEMS_H_GAP
                }
            }]
        });

    },

    populate: function (items, keyword, context) {
        this.filterType.setValue(context.filter_type);
        this._refreshFilterWidget(context.filter_type, context.filter_value);
    },

    _buildConditions: function () {
        var self = this, o = this.options;
        if (BI.isNull(o.field_name)) {
            return [];
        }
        var fieldName = o.field_name;
        this.fieldButton = BI.createWidget({
            type: "bi.text_button",
            text: fieldName,
            width: this._constant.FIELD_NAME_BUTTON_WIDTH,
            height: this._constant.BUTTON_HEIGHT,
            textAlign: "left",
            hgap: this._constant.TEXT_BUTTON_H_GAP
        });
        this.fieldButton.on(BI.Controller.EVENT_CHANGE, function () {
            arguments[2] = self;
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        this.filterWidgetContainer = BI.createWidget({
            type: "bi.left"
        });
        this.id = o.id;
        this.filterType = BI.createWidget({
            type: "bi.text_value_down_list_combo",
            width: this._constant.COMBO_WIDTH,
            height: this._constant.BUTTON_HEIGHT,
            items: BICst.DATA_SETTING_FILTER_DATE_COMBO
        });
        this.filterType.setValue(o.filter_type);
        this.filterType.on(BI.TextValueDownListCombo.EVENT_CHANGE, function () {
            self._refreshFilterWidget(self.filterType.getValue()[0]);
            self._setNodeData({
                filter_type : this.getValue()[0]
            });
            o.afterValueChange.apply(self, arguments);
        });
        this._refreshFilterWidget(o.filter_type, o.filter_value);

        return [this.fieldButton, this.filterType, this.filterWidgetContainer];
    },

    _refreshFilterWidget: function (filterType, filtetValue) {
        switch (filterType) {
            case BICst.FILTER_DATE.BELONG_DATE_RANGE:
            case BICst.FILTER_DATE.NOT_BELONG_DATE_RANGE:
                this._createTimeRange(filtetValue);
                break;
            case BICst.FILTER_DATE.LATER_THAN:
            case BICst.FILTER_DATE.EARLY_THAN:
                this.filterWidget = BI.createWidget();
                break;
            case BICst.FILTER_DATE.EQUAL_TO:
            case BICst.FILTER_DATE.NOT_EQUAL_TO:
                this._createDate(filtetValue);
                break;
            case BICst.FILTER_DATE.IS_NULL:
            case BICst.FILTER_DATE.NOT_NULL:
                this.filterWidget = BI.createWidget();
                break;
        }
        this.filterWidgetContainer.empty();
        this.filterWidgetContainer.addItem(this.filterWidget);
    },

    _createTimeRange: function (value) {
        var self = this, o = this.options;
        this.filterWidget = BI.createWidget({
            type: "bi.custom_time_interval",
            width: this._constant.INPUT_WIDTH,
            height: this._constant.TIME_INTERVAL_HEIGHT
        });
        this.filterWidget.on(BI.CustomTimeInterval.EVENT_CHANGE, function(){
            self._setNodeData({
                filter_value : this.getValue()
            });
            o.afterValueChange.apply(self, arguments);
        });
        if (BI.isNotNull(value)) {
            this.filterWidget.setValue(value);
        }
    },

    _createDate: function (value) {
        var self = this, o = this.options;
        this.filterWidget = BI.createWidget({
            type: "bi.custom_multi_date_combo",
            width: this._constant.INPUT_WIDTH,
            height: this._constant.TIME_INTERVAL_HEIGHT
        });
        this.filterWidget.on(BI.CustomMultiDateCombo.EVENT_CHANGE, function(){
            self._setNodeData({
                filter_value : this.getValue()
            });
            o.afterValueChange.apply(self, arguments);
        });
        if (BI.isNotNull(value)) {
            this.filterWidget.setValue(value);
        }
    },

    _setNodeData: function(v){
        var o = this.options;
        o.node.set("data", BI.extend(o.node.get("data"), v));
    },

    getValue: function () {
        return {
            field_name: this.options.field_name,
            filter_type: this.filterType.getValue()[0],
            filter_value: this.filterWidget.getValue()
        }
    }
});

BI.ConfTargetDateFieldFilterItem.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.conf_date_field_filter_item", BI.ConfTargetDateFieldFilterItem);