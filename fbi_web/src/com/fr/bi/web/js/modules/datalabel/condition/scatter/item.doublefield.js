/**
 * Created by fay on 2016/8/23.
 */
BI.ScatterDoubleFieldFilterItem = BI.inherit(BI.AbstractFilterItem, {
    _constant: {
        LEFT_ITEMS_H_GAP: 5,
        CONTAINER_HEIGHT: 75,
        BUTTON_HEIGHT: 30,
        COMBO_WIDTH: 120,
        FIELD_NAME_BUTTON_WIDTH: 60,
        TEXT_BUTTON_H_GAP: 10,
        INPUT_WIDTH: 230,
        LABEL_WIDTH: 30
    },

    _defaultConfig: function () {
        return BI.extend(BI.ScatterDoubleFieldFilterItem.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "data-label-condition-item",
            afterValueChange: BI.emptyFn
        })
    },

    _init: function () {
        BI.ScatterDoubleFieldFilterItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

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
            self.destroy();
            BI.ScatterDoubleFieldFilterItem.superclass.destroy.apply(this, arguments);
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
        if (BI.isNull(o.dId)) {
            return [];
        }
        this.filterItems = [];
        var targets = BI.Utils.getWidgetViewByID(BI.Utils.getWidgetIDByDimensionID(o.dId[0]));
        BI.each(o.dId, function (i, dId) {
            var fieldName = BI.Utils.getDimensionNameByID(dId);
            if (BI.contains(targets[30000], dId)) {
                fieldName = BI.i18nText("BI-Uppercase_Y_Axis");
            }
            if (BI.contains(targets[40000], dId)) {
                fieldName = BI.i18nText("BI-Uppercase_X_Axis");
            }
            var fieldButton = BI.createWidget({
                type: "bi.text_button",
                text: fieldName,
                title: fieldName,
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
            var filterType = BI.createWidget({
                type: "bi.text_value_down_list_combo",
                width: self._constant.COMBO_WIDTH,
                height: self._constant.BUTTON_HEIGHT,
                items: BICst.DATA_LABEL_FILTER_NUMBER_COMBO
            });
            filterType.setValue(o.filter_type);
            filterType.on(BI.TextValueDownListCombo.EVENT_CHANGE, function () {
                self._refreshFilterWidget(filterWidgetContainer, self.filterType.getValue()[0]);
                o.afterValueChange.apply(self, arguments);
            });
            self._refreshFilterWidget(filterWidgetContainer, o.filter_type, o.filter_value);
            self.filterItems.push([fieldButton, filterType, filterWidgetContainer]);
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

    _refreshFilterWidget: function (filterWidgetContainer, filterType, initData) {
        switch (filterType) {
            case BICst.DATA_LABEL_FILTER_STRING.BELONG_VALUE:
            case BICst.DATA_LABEL_FILTER_STRING.NOT_BELONG_VALUE:
                var addItem = this._createStringBelongCombo(initData);

                break;
            case BICst.DATA_LABEL_FILTER_STRING.CONTAIN:
            case BICst.DATA_LABEL_FILTER_STRING.NOT_CONTAIN:
                var addItem = this._createStringInput(initData);
                break;
            case BICst.DATA_LABEL_FILTER_STRING.IS_NULL:
            case BICst.DATA_LABEL_FILTER_STRING.NOT_NULL:
                this.filterWidget = BI.createWidget();
                var addItem = this.filterWidget;
                break;
            case BICst.DATA_LABEL_FILTER_STRING.BEGIN_WITH:
            case BICst.DATA_LABEL_FILTER_STRING.END_WITH:
                var addItem = this._createStringInput(initData);
                break;
            case BICst.DATA_LABEL_FILTER_NUMBER.EQUAL_TO:
            case BICst.DATA_LABEL_FILTER_NUMBER.NOT_EQUAL_TO:
                var addItem = this._createNumberInput(initData);
                break;
            case BICst.DATA_LABEL_FILTER_NUMBER.BELONG_VALUE:
            case BICst.DATA_LABEL_FILTER_NUMBER.NOT_BELONG_VALUE:
                var addItem = this._createNumberIntervalFilter(initData);
                break;
            case BICst.DATA_LABEL_FILTER_NUMBER.BELONG_USER:
            case BICst.DATA_LABEL_FILTER_NUMBER.NOT_BELONG_USER:
                var addItem = this._createNumberIntervalFilter(initData);
                break;
            case BICst.DATA_LABEL_FILTER_NUMBER.TOP_N:
                var addItem = this._createRank(initData);
                break;
            case BICst.DATA_LABEL_FILTER_NUMBER.LARGE_OR_EQUAL_CAL_LINE:
            case BICst.DATA_LABEL_FILTER_NUMBER.SMALL_THAN_CAL_LINE:
            case BICst.DATA_LABEL_FILTER_NUMBER.IS_NULL:
            case BICst.DATA_LABEL_FILTER_NUMBER.NOT_NULL:
                var filterWidget = BI.createWidget();
                var addItem = filterWidget;
                break;
        }
        filterWidgetContainer.empty();
        filterWidgetContainer.addItem(addItem);
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

    _createNumberInput: function (initData) {
        var self = this, o = this.options;
        var filterWidget = BI.createWidget({
            type: "bi.text_editor",
            validationChecker: function () {
                if (!BI.isNumeric(self.filterWidget.getValue())) {
                    return false;
                }
            },
            errorText: BI.i18nText("BI-Numerical_Interval_Input_Data"),
            allowBlank: true,
            height: this._constant.BUTTON_HEIGHT,
            width: this._constant.INPUT_WIDTH
        });
        filterWidget.on(BI.TextEditor.EVENT_CONFIRM, function () {
            o.afterValueChange.apply(self, arguments);
        });
        BI.isNotNull(initData) && filterWidget.setValue(initData);
        return BI.createWidget({
            type: "bi.inline",
            items: [{
                type: "bi.label",
                height: this._constant.BUTTON_HEIGHT,
                text: "N = ",
                width: this._constant.INPUT_WIDTH - this._constant.LABEL_WIDTH
            }, filterWidget]
        });
    },

    _createRank: function (initData) {
        var self = this, o = this.options;
        var filterWidget = BI.createWidget({
            type: "bi.text_editor",
            validationChecker: function () {
                if (!BI.isNumeric(self.filterWidget.getValue())) {
                    return false;
                }
            },
            errorText: BI.i18nText("BI-Numerical_Interval_Input_Data"),
            allowBlank: true,
            height: this._constant.BUTTON_HEIGHT,
            width: this._constant.INPUT_WIDTH - this._constant.LABEL_WIDTH
        });
        filterWidget.on(BI.TextEditor.EVENT_CONFIRM, function () {
            o.afterValueChange.apply(self, arguments);
        });
        BI.isNotNull(initData) && filterWidget.setValue(initData);
        return BI.createWidget({
            type: "bi.inline",
            items: [{
                type: "bi.label",
                height: this._constant.BUTTON_HEIGHT,
                text: "N = ",
                width: this._constant.LABEL_WIDTH
            }, filterWidget]
        });
    },

    _createStyle: function (initData) {
        var self = this, o = this.options;
        var chartType = BI.Utils.getWidgetTypeByID(BI.Utils.getWidgetIDByDimensionID(o.dId[0]));
        var style = BI.createWidget({
            type: "bi.data_label_style_set",
            chartType: chartType
        });
        BI.isNotNull(initData) && style.setValue(initData);
        return style;
    },

    getValue: function () {
        return {
            // target_id: this.options.dId,
            // filter_type: this.filterType.getValue()[0],
            // filter_value: this.filterWidget.getValue(),
            // filter_range: this.filterRange ? this.filterRange.getValue() : "",
            // style_setting: this.style.getValue()
        }
    }
});
$.shortcut("bi.scatter_double_field_filter_item", BI.ScatterDoubleFieldFilterItem);