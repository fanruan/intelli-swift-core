/**
 * Created by fay on 2016/8/23.
 */
BI.ScatterMultiFieldFilterItem = BI.inherit(BI.AbstractDataLabelFilterItem, {
    _constant: {
        LEFT_ITEMS_H_GAP: 5,
        CONTAINER_HEIGHT: 80,
        BUTTON_HEIGHT: 30,
        COMBO_WIDTH: 120,
        FIELD_NAME_BUTTON_WIDTH: 60,
        TEXT_BUTTON_H_GAP: 10,
        INPUT_WIDTH: 230,
        LABEL_WIDTH: 30
    },

    _defaultConfig: function () {
        return BI.extend(BI.ScatterMultiFieldFilterItem.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "data-label-condition-item",
            afterValueChange: BI.emptyFn
        })
    },

    _init: function () {
        BI.ScatterMultiFieldFilterItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.filterType = [];
        this.filterWidget = [];
        this.filterField = [{
            name: BI.i18nText("BI-Uppercase_X_Axis"),
            key: "x"
        }, {
            name: BI.i18nText("BI-Uppercase_Y_Axis"),
            key: "y"
        }];
        var and = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-And")
        });
        var left = this._buildConditions();
        this.styleSetting = this._createStyle(o.style_setting);
        this.deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-h-font"
        });
        this.deleteButton.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AbstractDataLabelFilterItem.DELETE, self);
            self.destroy();
            BI.ScatterMultiFieldFilterItem.superclass.destroy.apply(this, arguments);
        });

        BI.createWidget({
            type: "bi.vertical",
            cls: "item-content",
            element: this.element,
            items: [{
                type: "bi.left_right_vertical_adapt",
                height: this._constant.CONTAINER_HEIGHT,
                items: {
                    left: [and, left],
                    right: [this.styleSetting, this.deleteButton]
                },
                lhgap: this._constant.LEFT_ITEMS_H_GAP,
                rhgap: this._constant.LEFT_ITEMS_H_GAP
            }]
        });
    },

    populate: function (item) {
        this.filterType.setValue(item.filter_type);
        this._refreshFilterWidget(item.filter_type, item.filter_value);
    },

    _buildConditions: function () {
        var self = this, o = this.options;
        this.filterItems = [];
        this.filterValues = o.filter_value || this.filterField;
        BI.each(this.filterValues, function (i, filter) {
            if(BI.isNull(filter.filter_type)) {
                filter.filter_type = o.filter_type
            }
            var fieldButton = BI.createWidget({
                type: "bi.text_button",
                text: self.filterField[i].name,
                title: self.filterField[i].name,
                width: self._constant.FIELD_NAME_BUTTON_WIDTH,
                height: self._constant.BUTTON_HEIGHT,
                textAlign: "left",
                hgap: self._constant.TEXT_BUTTON_H_GAP
            });

            fieldButton.on(BI.Controller.EVENT_CHANGE, function () {
                arguments[2] = self;
                self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
            });
            var filterWidgetContainer = BI.createWidget({
                type: "bi.left"
            });
            self.filterType[i] = BI.createWidget({
                type: "bi.text_value_down_list_combo",
                width: self._constant.COMBO_WIDTH,
                height: self._constant.BUTTON_HEIGHT,
                items: BICst.DATA_LABEL_FILTER_NUMBER_COMBO
            });
            self.filterType[i].setValue(filter.filter_type);
            self.filterType[i].on(BI.TextValueDownListCombo.EVENT_CHANGE, function () {
                self._refreshFilterWidget(i, filterWidgetContainer, self.filterType[i].getValue()[0]);
                o.afterValueChange.apply(self, arguments);
            });
            self._refreshFilterWidget(i, filterWidgetContainer, filter.filter_type, filter.filter_value);
            self.filterItems.push([fieldButton, self.filterType[i], filterWidgetContainer]);
        });
        return BI.createWidget({
            type: "bi.vertical",
            cls: "condition-items",
            items: [BI.createWidget({
                type: "bi.horizontal",
                items: this.filterItems[0]
            }), BI.createWidget({
                type: "bi.horizontal",
                items: this.filterItems[1]
            })],
            vgap: 5
        });
    },

    _refreshFilterWidget: function (id, filterWidgetContainer, filterType, initData) {
        var filterItem;
        switch (filterType) {
            case BICst.TARGET_FILTER_NUMBER.EQUAL_TO:
            case BICst.TARGET_FILTER_NUMBER.NOT_EQUAL_TO:
            case BICst.DIMENSION_FILTER_NUMBER.TOP_N:
                filterItem = this._createNumberInput(initData, id);
                break;
            case BICst.DIMENSION_FILTER_NUMBER.BELONG_VALUE:
            case BICst.DIMENSION_FILTER_NUMBER.NOT_BELONG_VALUE:
                this.filterWidget[id] = this._createNumberIntervalFilter(initData);
                break;
            case BICst.DIMENSION_FILTER_NUMBER.BELONG_USER:
            case BICst.DIMENSION_FILTER_NUMBER.NOT_BELONG_USER:
                this.filterWidget[id] = this._createNumberIntervalFilter(initData);
                break;
            case BICst.TARGET_FILTER_NUMBER.LARGE_OR_EQUAL_CAL_LINE:
            case BICst.TARGET_FILTER_NUMBER.SMALL_THAN_CAL_LINE:
            case BICst.DIMENSION_FILTER_NUMBER.IS_NULL:
            case BICst.DIMENSION_FILTER_NUMBER.NOT_NULL:
                this.filterWidget[id] = BI.createWidget();
                break;
            default:
                this.filterWidget[id] = BI.createWidget();
        }
        filterWidgetContainer.empty();
        filterWidgetContainer.addItem(filterItem || this.filterWidget[id]);
    },

    _createNumberIntervalFilter: function (initData) {
        var self = this, o = this.options;
        var filterWidget = BI.createWidget({
            type: "bi.numerical_interval",
            width: this._constant.INPUT_WIDTH,
            height: this._constant.BUTTON_HEIGHT
        });
        BI.isNotNull(initData) && filterWidget.setValue(initData);
        filterWidget.on(BI.NumericalInterval.EVENT_CHANGE, function () {
            o.afterValueChange.apply(self, arguments);
        });
        return filterWidget;
    },

    _createNumberInput: function (initData, id) {
        var self = this, o = this.options;
        this.filterWidget[id] = BI.createWidget({
            type: "bi.text_editor",
            validationChecker: function () {
                if (!BI.isNumeric(self.filterWidget[id].getValue())) {
                    return false;
                }
            },
            errorText: BI.i18nText("BI-Numerical_Interval_Input_Data"),
            allowBlank: true,
            height: this._constant.BUTTON_HEIGHT,
            width: this._constant.INPUT_WIDTH - this._constant.LABEL_WIDTH
        });
        this.filterWidget[id].on(BI.TextEditor.EVENT_CONFIRM, function () {
            o.afterValueChange.apply(self, arguments);
        });
        BI.isNotNull(initData) && this.filterWidget[id].setValue(initData);
        return BI.createWidget({
            type: "bi.inline",
            items: [{
                type: "bi.label",
                height: this._constant.BUTTON_HEIGHT,
                text: "N = ",
                width: this._constant.LABEL_WIDTH
            }, this.filterWidget[id]]
        });
    },

    _createStyle: function (initData) {
        var self = this, o = this.options;
        // var chartType = BI.Utils.getWidgetTypeByID(BI.Utils.getWidgetIDByDimensionID(this.filterValues[0].target_id));
        this.style = BI.createWidget({
            type: "bi.data_label_style_set",
            // chartType: chartType
        });
        BI.isNotNull(initData) && this.style.setValue(initData);
        return this.style;
    },

    getValue: function () {
        var self = this;
        var value = [];
        BI.each(this.filterValues, function (i) {
            value.push({
                key: self.filterField[i].key,
                filter_type: self.filterType[i].getValue()[0],
                filter_value: self.filterWidget[i].getValue()
            });
        });
        return {
            filter_type: BICst.FILTER_TYPE.AND,
            filter_value: value,
            style_setting: this.style.getValue()
        }
    }
});
$.shortcut("bi.scatter_multi_field_filter_item", BI.ScatterMultiFieldFilterItem);