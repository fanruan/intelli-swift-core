/**
 * Created by fay on 2016/7/22.
 */
BI.DataLabelNumberFieldFilterItem = BI.inherit(BI.AbstractFilterItem, {
    _comboItems: [[{
        text: BI.i18nText("BI-In"),
        value: BICst.TARGET_FILTER_NUMBER.BELONG_VALUE,
        cls: "dot-e-font"
    }, {
        text: BI.i18nText("BI-Not_In"),
        value: BICst.TARGET_FILTER_NUMBER.NOT_BELONG_VALUE,
        cls: "dot-e-font"
    }], [{
        text: BI.i18nText("BI-Equal"),
        value: BICst.TARGET_FILTER_NUMBER.EQUAL_TO,
        cls: "dot-e-font"
    }, {
        text: BI.i18nText("BI-Not_Equal_To"),
        value: BICst.TARGET_FILTER_NUMBER.NOT_EQUAL_TO,
        cls: "dot-e-font"
    }], [{
        text: BI.i18nText("BI-Is_Null"),
        value: BICst.TARGET_FILTER_NUMBER.IS_NULL,
        cls: "dot-e-font"
    }, {
        text: BI.i18nText("BI-Not_Null"),
        value: BICst.TARGET_FILTER_NUMBER.NOT_NULL,
        cls: "dot-e-font"
    }], [
        {
            text: "第N名",
            value: 201,
            cls: "dot-e-font"
        }
    ], [
        {
            text: "大于等于平均值",
            value: 202,
            cls: "dot-e-font"
        }, {
            text: "小于平均值",
            value: 203,
            cls: "dot-e-font"
        }
    ]],
    _constant: {
        LEFT_ITEMS_H_GAP: 5,
        CONTAINER_HEIGHT: 40,
        BUTTON_HEIGHT: 30,
        COMBO_WIDTH: 120,
        FIELD_NAME_BUTTON_WIDTH: 90,
        TEXT_BUTTON_H_GAP: 10,
        INPUT_WIDTH: 230,
        INPUT_WIDTH_CHANGE: 180,
        COMBO_WIDTH_CHANGE: 80,
        LABEL_WIDTH: 30
    },

    _defaultConfig: function () {
        return BI.extend(BI.DataLabelNumberFieldFilterItem.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "data-label-condition-item",
            afterValueChange: BI.emptyFn
        })
    },

    _init: function () {
        BI.DataLabelNumberFieldFilterItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.size = {};
        var left = this._buildConditions();
        this.styleSetting = this._createStyle(o.style_setting);
        this.deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-h-font"
        });
        this.deleteButton.on(BI.Controller.EVENT_CHANGE, function () {
            self.destroy();
            BI.DataLabelNumberFieldFilterItem.superclass.destroy.apply(this, arguments);
        });

        var leftContainer = BI.createWidget({
            type: "bi.vertical",
            cls: "item-content",
            items: [{
                type: "bi.left_right_vertical_adapt",
                height: this._constant.CONTAINER_HEIGHT,
                items: {
                    left: [left[0], left[1], left[2], left[3]],
                    right: [this.styleSetting]
                },
                lhgap: this._constant.LEFT_ITEMS_H_GAP,
                width: 540
            }]
        });
        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                el: {
                    type: "bi.left_right_vertical_adapt",
                    height: 42,
                    items: {
                        left: [leftContainer],
                        right: [this.deleteButton]
                    }
                }
            }]
        })
    },

    populate: function (item) {
        this.filterType.setValue(item.filter_type);
        this._refreshFilterWidget(item.filter_type, item.filter_value);
    },

    _buildConditions: function () {
        var self = this, o = this.options;
        if (BI.isNull(o.field_id)) {
            return [];
        }

        this.fieldId = o.field_id;
        var fieldName = BI.Utils.getFieldNameByID(this.fieldId);
        var selfName = BI.Utils.getFieldNameByID(BI.Utils.getFieldIDByDimensionID(o.dId));
        this._isSelf = fieldName === selfName;
        this.size.INPUT_WIDTH = this._isSelf ? this._constant.INPUT_WIDTH_CHANGE : this._constant.INPUT_WIDTH;
        this.size.COMBO_WIDTH = this._isSelf ? this._constant.COMBO_WIDTH_CHANGE : this._constant.COMBO_WIDTH;
        this.fieldButton = BI.createWidget({
            type: "bi.text_button",
            text: fieldName === selfName ? "自身" : fieldName,
            title: fieldName === selfName ? "自身" : fieldName,
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
            width: this.size.COMBO_WIDTH,
            height: this._constant.BUTTON_HEIGHT,
            items: this._comboItems
        });
        this.filterType.setValue(o.filter_type);
        this.filterType.on(BI.TextValueDownListCombo.EVENT_CHANGE, function () {
            self._refreshFilterWidget(self.filterType.getValue()[0]);
            o.afterValueChange.apply(self, arguments);
        });
        this._refreshFilterWidget(o.filter_type, this.options.filter_value);

        if (this._isSelf) {
            this.filterRange = this._createRange();
            this.filterRange.setValue(100001);
            return [this.fieldButton, this.filterType, this.filterWidgetContainer, this.filterRange];
        }
        return [this.fieldButton, this.filterType, this.filterWidgetContainer];
    },

    _refreshFilterWidget: function (filterType, initData) {
        if (this._isSelf) {
            this.filterType.setWidth(this.size.COMBO_WIDTH);
            this.filterRange && this.filterRange.setWidth(this.size.COMBO_WIDTH);
        }
        switch (filterType) {
            case BICst.TARGET_FILTER_NUMBER.EQUAL_TO:
            case BICst.TARGET_FILTER_NUMBER.NOT_EQUAL_TO:
                var addItem = this._createNumberInput(initData);
                break;
            case BICst.TARGET_FILTER_NUMBER.BELONG_VALUE:
            case BICst.TARGET_FILTER_NUMBER.NOT_BELONG_VALUE:
                var addItem = this._createNumberIntervalFilter(initData);
                break;
            case BICst.TARGET_FILTER_NUMBER.BELONG_USER:
            case BICst.TARGET_FILTER_NUMBER.NOT_BELONG_USER:
                var addItem = this._createNumberIntervalFilter(initData);
                break;
            case 201:
                var addItem = this._createRank(initData);
                break;
            case 202:
            case 203:
            case BICst.TARGET_FILTER_NUMBER.IS_NULL:
            case BICst.TARGET_FILTER_NUMBER.NOT_NULL:
                this.filterType.setWidth(this._constant.COMBO_WIDTH);
                this.filterRange && this.filterRange.setWidth(this._constant.COMBO_WIDTH);
                this.filterWidget = BI.createWidget();
                var addItem = this.filterWidget;
                break;
        }
        this.filterWidgetContainer.empty();
        this.filterWidgetContainer.addItem(addItem);
    },

    _createNumberIntervalFilter: function (initData) {
        var self = this, o = this.options;
        this.filterWidget = BI.createWidget({
            type: "bi.numerical_interval",
            width: this.size.INPUT_WIDTH,
            height: this._constant.BUTTON_HEIGHT
        });
        BI.isNotNull(initData) && this.filterWidget.setValue(initData);
        this.filterWidget.on(BI.NumericalInterval.EVENT_CHANGE, function () {
            // self._setNodeData({
            //     filter_value : this.getValue()
            // });
            o.afterValueChange.apply(self, arguments);
        });
        return this.filterWidget;
    },

    _createNumberInput: function (initData) {
        var self = this, o = this.options;
        this.filterWidget = BI.createWidget({
            type: "bi.text_editor",
            validationChecker: function () {
                if (!BI.isNumeric(self.filterWidget.getValue())) {
                    return false;
                }
            },
            errorText: BI.i18nText("BI-Numerical_Interval_Input_Data"),
            allowBlank: true,
            height: this._constant.BUTTON_HEIGHT,
            width: this.size.INPUT_WIDTH - this._constant.LABEL_WIDTH
        });
        this.filterWidget.on(BI.TextEditor.EVENT_CONFIRM, function () {
            // self._setNodeData({
            //     filter_value : this.getValue()
            // });
            o.afterValueChange.apply(self, arguments);
        });
        BI.isNotNull(initData) && this.filterWidget.setValue(initData);
        return BI.createWidget({
            type: "bi.inline",
            items: [{
                type: "bi.label",
                height: this._constant.BUTTON_HEIGHT,
                text: "N = ",
                width: this._constant.LABEL_WIDTH
            }, this.filterWidget]
        });
    },

    _createRank: function (initData) {
        var self = this, o = this.options;
        this.filterWidget = BI.createWidget({
            type: "bi.text_editor",
            validationChecker: function () {
                if (!BI.isNumeric(self.filterWidget.getValue())) {
                    return false;
                }
            },
            errorText: BI.i18nText("BI-Numerical_Interval_Input_Data"),
            allowBlank: true,
            height: this._constant.BUTTON_HEIGHT,
            width: this.size.INPUT_WIDTH - this._constant.LABEL_WIDTH
        });
        this.filterWidget.on(BI.TextEditor.EVENT_CONFIRM, function () {
            // self._setNodeData({
            //     filter_value : this.getValue()
            // });
            o.afterValueChange.apply(self, arguments);
        });
        BI.isNotNull(initData) && this.filterWidget.setValue(initData);
        return BI.createWidget({
            type: "bi.inline",
            items: [{
                type: "bi.label",
                height: this._constant.BUTTON_HEIGHT,
                text: "N = ",
                width: this._constant.LABEL_WIDTH
            }, this.filterWidget]
        });
    },

    _createRange: function () {
        return BI.createWidget({
            type: "bi.text_value_down_list_combo",
            width: this.size.COMBO_WIDTH,
            height: this._constant.BUTTON_HEIGHT,
            items: [[{
                text: "所有(无范围)",
                value: 100001,
                cls: "dot-e-font"
            }, {
                text: "分类内",
                value: 100002,
                cls: "dot-e-font"
            }, {
                text: "系列内",
                value: 100003,
                cls: "dot-e-font"
            }]]
        });
    },

    _createStyle: function (initData) {
        var self = this, o = this.options;
        this.style = BI.createWidget({
            type: "bi.data_label_style_set"
        });
        BI.isNotNull(initData) && this.style.setValue(initData);
        return this.style;
    },

    // _setNodeData: function(v){
    //     var o = this.options;
    //     o.node.set("data", BI.extend(o.node.get("data"), v));
    // },

    getValue: function () {
        return {
            field_id: this.options.field_id,
            filter_type: this.filterType.getValue()[0],
            filter_value: this.filterWidget.getValue(),
            style_setting: this.style.getValue()
        }
    }
});
$.shortcut("bi.data_label_number_field_filter_item", BI.DataLabelNumberFieldFilterItem);