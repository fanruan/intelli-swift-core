/**
 * @class BI.ConfTargetStringFieldFilterItem
 * @extend BI.AbstractFilterItem
 * 指标过滤——文本字段类型过滤条件
 */
BI.ConfTargetStringFieldFilterItem = BI.inherit(BI.AbstractFilterItem, {

    _constant: {
        LEFT_ITEMS_H_GAP: 5,
        CONTAINER_HEIGHT: 40,
        BUTTON_HEIGHT: 30,
        COMBO_WIDTH: 80,
        FIELD_NAME_BUTTON_WIDTH: 90,
        TEXT_BUTTON_H_GAP: 10,
        INPUT_WIDTH: 200
    },


    _defaultConfig: function () {
        return BI.extend(BI.ConfTargetStringFieldFilterItem.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-target-string-field-item",
            afterValueChange: BI.emptyFn
        })
    },

    _init: function () {
        BI.ConfTargetStringFieldFilterItem.superclass._init.apply(this, arguments);
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
            return "";
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
            items: BICst.TARGET_FILTER_STRING_COMBO
        });
        this.filterType.setValue(o.filter_type);
        this.filterType.on(BI.TextValueDownListCombo.EVENT_CHANGE, function () {
            self._refreshFilterWidget(self.filterType.getValue()[0]);
            self._setNodeData({
                filter_type : this.getValue()[0]
            });
            o.afterValueChange.apply(self, arguments);
        });
        this._refreshFilterWidget(o.filter_type, this.options.filter_value);
        return [this.fieldButton, this.filterType, this.filterWidgetContainer];
    },

    _refreshFilterWidget: function (filterType, initData) {
        switch (filterType) {
            case BICst.TARGET_FILTER_STRING.BELONG_VALUE:
            case BICst.TARGET_FILTER_STRING.NOT_BELONG_VALUE:
                this._createTargetStringBelongCombo(initData);
                break;
            case BICst.TARGET_FILTER_STRING.CONTAIN:
            case BICst.TARGET_FILTER_STRING.NOT_CONTAIN:
                this._createStringInput(initData);
                break;
            case BICst.TARGET_FILTER_STRING.IS_NULL:
            case BICst.TARGET_FILTER_STRING.NOT_NULL:
                this.filterWidget = BI.createWidget();
                break;
            case BICst.TARGET_FILTER_STRING.BEGIN_WITH:
            case BICst.TARGET_FILTER_STRING.END_WITH:
                this._createStringInput(initData);
                break;
        }
        this.filterWidgetContainer.empty();
        this.filterWidgetContainer.addItem(this.filterWidget);
    },

    _createTargetStringBelongCombo: function (initData) {
        var o = this.options, self = this;
        this.filterWidget = BI.createWidget({
            type: "bi.conf_filter_value_chooser_combo",
            width: 200,
            table: o.table,
            fieldName: o.field_name,
            height: this._constant.BUTTON_HEIGHT
        });
        this.filterWidget.on(BI.ConfFilterValueChooserCombo.EVENT_CONFIRM, function () {
            self._setNodeData({
                filter_value : this.getValue()
            });
            o.afterValueChange.apply(self, arguments);
        });
        BI.isNotNull(initData) && this.filterWidget.setValue(initData);
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
            self._setNodeData({
                filter_value : this.getValue()
            });
            o.afterValueChange.apply(self, arguments);
        });
        BI.isNotNull(initData) && this.filterWidget.setValue(initData);
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

BI.ConfTargetStringFieldFilterItem.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.conf_target_string_field_filter_item", BI.ConfTargetStringFieldFilterItem);