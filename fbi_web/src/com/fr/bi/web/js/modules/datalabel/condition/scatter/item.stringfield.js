/**
 * Created by fay on 2016/7/27.
 */

BI.ScatterLabelStringFieldFilterItem = BI.inherit(BI.AbstractFilterItem, {

    _constant: {
        LEFT_ITEMS_H_GAP: 5,
        CONTAINER_HEIGHT: 40,
        BUTTON_HEIGHT: 30,
        COMBO_WIDTH: 120,
        FIELD_NAME_BUTTON_WIDTH: 80,
        TEXT_BUTTON_H_GAP: 10,
        INPUT_WIDTH: 230
    },

    _defaultConfig: function () {
        return BI.extend(BI.ScatterLabelStringFieldFilterItem.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "data-label-condition-item",
            afterValueChange: BI.emptyFn
        })
    },

    _init: function () {
        BI.ScatterLabelStringFieldFilterItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var left = this._buildConditions();
        this.styleSetting = this._createStyle(o.style_setting);
        this.deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-h-font"
        });
        this.deleteButton.on(BI.Controller.EVENT_CHANGE, function () {
            self.destroy();
            BI.ScatterLabelStringFieldFilterItem.superclass.destroy.apply(this, arguments);
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
            o.afterValueChange.apply(self, arguments);
        });
        this._refreshFilterWidget(o.filter_type, o.filter_value);
        return [this.fieldButton, this.filterType, this.filterWidgetContainer];
    },

    _refreshFilterWidget: function (filterType, initData) {
        switch (filterType) {
            case BICst.DATA_LABEL_FILTER_STRING.BELONG_VALUE:
            case BICst.DATA_LABEL_FILTER_STRING.NOT_BELONG_VALUE:
                this._createStringBelongCombo(initData);

                break;
            case BICst.DATA_LABEL_FILTER_STRING.CONTAIN:
            case BICst.DATA_LABEL_FILTER_STRING.NOT_CONTAIN:
                this._createStringInput(initData);
                break;
            case BICst.DATA_LABEL_FILTER_STRING.IS_NULL:
            case BICst.DATA_LABEL_FILTER_STRING.NOT_NULL:
                this.filterWidget = BI.createWidget();
                break;
            case BICst.DATA_LABEL_FILTER_STRING.BEGIN_WITH:
            case BICst.DATA_LABEL_FILTER_STRING.END_WITH:
                this._createStringInput(initData);
                break;
        }
        this.filterWidgetContainer.empty();
        this.filterWidgetContainer.addItem(this.filterWidget);
    },

    _createStringBelongCombo: function (initData) {
        var o = this.options, self = this;
        this.filterWidget = BI.createWidget({
            type: "bi.select_dimension_data_combo",
            dId: o.dId,
            width: 230,
            height: this._constant.BUTTON_HEIGHT
        });

        this.filterWidget.on(BI.SelectDimensionDataCombo.EVENT_CONFIRM, function () {
            o.afterValueChange.apply(self, arguments);
        });
        BI.isNotNull(initData) && this.filterWidget.setValue(initData);
        return this.filterWidget;
    },

    _createStringInput: function (initData) {
        var self = this, o = this.options;
        this.filterWidget = BI.createWidget({
            type: "bi.sign_editor",
            cls: "condition-operator-input",
            allowBlank: true,
            height: this._constant.BUTTON_HEIGHT,
            width: this._constant.INPUT_WIDTH
        });
        this.filterWidget.on(BI.SignEditor.EVENT_CONFIRM, function () {
            o.afterValueChange.apply(self, arguments);
        });
        BI.isNotNull(initData) && this.filterWidget.setValue(initData);
        return this.filterWidget;
    },

    _createStyle: function (initData) {
        var self = this, o = this.options;
        var chartType = BI.Utils.getWidgetTypeByID(BI.Utils.getWidgetIDByDimensionID(o.dId));
        this.style = BI.createWidget({
            type: "bi.data_label_style_set",
            chartType: chartType
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
$.shortcut("bi.scatter_string_field_filter_item", BI.ScatterLabelStringFieldFilterItem);