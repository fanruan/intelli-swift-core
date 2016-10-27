/**
 * Created by fay on 2016/7/27.
 */

BI.DataLabelStringFieldFilterItem = BI.inherit(BI.AbstractDataLabelFilterItem, {

    _constant: {
        LEFT_ITEMS_H_GAP: 5,
        CONTAINER_HEIGHT: 40,
        BUTTON_HEIGHT: 30,
        COMBO_WIDTH: 120,
        FIELD_NAME_BUTTON_WIDTH: 85,
        TEXT_BUTTON_H_GAP: 10,
        INPUT_WIDTH: 240
    },

    _defaultConfig: function () {
        return BI.extend(BI.DataLabelStringFieldFilterItem.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "condition-item"
        })
    },

    _init: function () {
        BI.DataLabelStringFieldFilterItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var left = this._buildConditions();
        this.styleSetting = this._createStyle(o.style_setting);
        this.deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-h-font delete-icon"
        });
        this.deleteButton.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AbstractDataLabelFilterItem.DELETE, self);
            self.destroy();
            BI.DataLabelStringFieldFilterItem.superclass.destroy.apply(this, arguments);
        });
        BI.createWidget({
            type: "bi.vertical",
            cls: "item-content",
            element: this.element,
            items: [{
                type: "bi.left_right_vertical_adapt",
                height: this._constant.CONTAINER_HEIGHT,
                items: {
                    left: [left[0], left[1], left[2]],
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
        var fieldName = BI.Utils.getDimensionNameByID(o.dId);
        this.fieldButton = BI.createWidget({
            type: "bi.text_button",
            text: fieldName,
            title: fieldName,
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
        this.filterType = BI.createWidget({
            type: "bi.text_value_down_list_combo",
            width: this._constant.COMBO_WIDTH,
            height: this._constant.BUTTON_HEIGHT,
            items: BICst.DATA_LABEL_FILTER_STRING_COMBO
        });
        this.filterType.setValue(o.filter_type);
        this.filterType.on(BI.TextValueDownListCombo.EVENT_CHANGE, function () {
            self._refreshFilterWidget(self.filterType.getValue()[0]);
        });
        this._refreshFilterWidget(o.filter_type, o.filter_value);
        return [this.fieldButton, this.filterType, this.filterWidgetContainer];
    },

    _refreshFilterWidget: function (filterType, initData) {
        switch (filterType) {
            case BICst.DIMENSION_FILTER_STRING.BELONG_VALUE:
            case BICst.DIMENSION_FILTER_STRING.NOT_BELONG_VALUE:
                this._createStringBelongCombo(initData);

                break;
            case BICst.DIMENSION_FILTER_STRING.CONTAIN:
            case BICst.DIMENSION_FILTER_STRING.NOT_CONTAIN:
                this._createStringInput(initData);
                break;
            case BICst.DIMENSION_FILTER_STRING.IS_NULL:
            case BICst.DIMENSION_FILTER_STRING.NOT_NULL:
                this.filterWidget = BI.createWidget();
                break;
            case BICst.DIMENSION_FILTER_STRING.BEGIN_WITH:
            case BICst.DIMENSION_FILTER_STRING.END_WITH:
            case BICst.DIMENSION_FILTER_STRING.NOT_BEGIN_WITH:
            case BICst.DIMENSION_FILTER_STRING.NOT_END_WITH:
                this._createStringInput(initData);
                break;
        }
        this.filterWidgetContainer.empty();
        this.filterWidgetContainer.addItem(this.filterWidget);
    },

    _createStringBelongCombo: function (initData) {
        var o = this.options;
        this.filterWidget = BI.createWidget({
            type: "bi.select_data_label_data_combo",
            dId: o.dId,
            width: this._constant.INPUT_WIDTH,
            height: this._constant.BUTTON_HEIGHT
        });

        BI.isNotNull(initData) && this.filterWidget.setValue(initData);
        return this.filterWidget;
    },

    _createStringInput: function (initData) {
        this.filterWidget = BI.createWidget({
            type: "bi.sign_editor",
            cls: "condition-operator-input",
            allowBlank: true,
            height: this._constant.BUTTON_HEIGHT,
            width: this._constant.INPUT_WIDTH
        });
        BI.isNotNull(initData) && this.filterWidget.setValue(initData);
        return this.filterWidget;
    },

    _createStyle: function (initData) {
        var o = this.options;
        this.style = BI.createWidget({
            type: "bi.data_label_style_set",
            chartType: o.chartType,
            sdId: o.sdId
        });
        BI.isNotNull(initData) && this.style.setValue(initData);
        return this.style;
    },

    getValue: function () {
        return {
            target_id: this.options.dId,
            filter_type: this.filterType.getValue()[0],
            filter_value: this.filterWidget.getValue(),
            style_setting: this.style.getValue()
        }
    }
});
$.shortcut("bi.data_label_string_field_filter_item", BI.DataLabelStringFieldFilterItem);