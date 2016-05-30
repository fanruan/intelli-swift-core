/**
 * @class BI.TargetNoTypeFieldFilterItem
 * @extend BI.AbstractFilterItem
 * 指标过滤——待确定字段类型过滤条件
 */

BI.TargetNoTypeFieldFilterItem = BI.inherit(BI.AbstractFilterItem, {

    _constant: {
        LEFT_ITEMS_H_GAP: 5,
        CONTAINER_HEIGHT: 40,
        CONDITION_TYPE_COMBO_ADJUST: 2,
        BUTTON_HEIGHT: 30,
        TEXT_BUTTON_H_GAP: 10,
        ADD_FIELD_POPUP_WIDTH: 230,
        HEIGHT_MAX: 10000,
        MAX_HEIGHT: 500
    },

    _defaultConfig: function () {
        return BI.extend(BI.TargetNoTypeFieldFilterItem.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-target-no-type-field-item",
            afterValueChange: BI.emptyFn
        })
    },

    _init: function () {
        BI.TargetNoTypeFieldFilterItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        var left = this._buildConditionsNoType();
        this.deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-h-font"
        });
        this.deleteButton.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, BI.Events.DESTROY, o.id, self);
        });

        this.itemContainer = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "item-no-type",
            height: this._constant.CONTAINER_HEIGHT,
            items: {
                left: [left],

                right: [this.deleteButton]
            },
            lhgap: this._constant.LEFT_ITEMS_H_GAP,
            rhgap: this._constant.LEFT_ITEMS_H_GAP
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [this.itemContainer]

        });
    },

    populate: function (items) {
        if (BI.isNotNull(this.typeSelectedItem)) {
            this.typeSelectedItem.populate(items);
        }
    },

    _buildConditionsNoType: function () {
        var self = this, o = this.options;
        var selectFieldPane = BI.createWidget({
            type: "bi.target_filter_select_field",
            height: this._constant.MAX_HEIGHT,
            field_id: o.field_id
        });

        this.addCondition = BI.createWidget({
            type: "bi.combo",
            isNeedAdjustHeight: true,
            adjustLength: this._constant.CONDITION_TYPE_COMBO_ADJUST,
            el: {
                type: "bi.button",
                level: "common",
                height: this._constant.BUTTON_HEIGHT,
                text: BI.i18nText("BI-Please_Select_Field")
            },
            popup: {
                el: selectFieldPane,
                minWidth: 228,
                maxHeight: this._constant.MAX_HEIGHT
            }
        });

        selectFieldPane.on(BI.TargetFilterSelectField.EVENT_CLICK_ITEM, function (v) {
            self._onTypeSelected(v);

        });
        return this.addCondition;
    },

    _onTypeSelected: function (v) {
        var fieldType = BI.Utils.getFieldTypeByID(v);
        var self = this, o = this.options;
        var filterItem = BI.TargetFilterItemFactory.createFilterItemByFieldType(fieldType);
        this.itemContainer.destroy();
        this.itemContainer = null;
        this.typeSelectedItem = BI.createWidget(filterItem, {
            element: this.element,
            _src: {
                field_id: v
            },
            id: this.options.id,
            node: o.node,
            afterValueChange: o.afterValueChange
        });
        this.typeSelectedItem.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        o.node.set("data", BI.extend(o.node.get("data"), {
            value: filterItem.filter_type,
            filter_type: filterItem.filter_type,
            filter_value: {value: []},
            _src: {
                field_id: v
            }

        }));
    },

    getValue: function () {
        if (BI.isNotNull(this.typeSelectedItem)) {

            return this.typeSelectedItem.getValue();
        }
        return {
            filter_type: BICst.FILTER_TYPE.EMPTY_CONDITION
        }
    }
});
$.shortcut("bi.target_no_type_field_filter_item", BI.TargetNoTypeFieldFilterItem);