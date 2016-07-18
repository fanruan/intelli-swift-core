/**
 * @class BI.TargetFormulaEmptyFilterItem
 * @extend BI.Widget
 * 过滤条件——公式
 */
BI.TargetFormulaEmptyFilterItem = BI.inherit(BI.AbstractFilterItem, {

    _constant: {
        LEFT_ITEMS_H_GAP: 5,
        CONTAINER_HEIGHT: 40,
        CONDITION_TYPE_COMBO_ADJUST: 2,
        BUTTON_HEIGHT: 28,
        FORMULA_V_GAP: 10,
        ADD_FORMULA_POPUP_WIDTH: 600,
        FORMULA_H_GAP: 20,
        HEIGHT_MAX: 10000,
        MAX_HEIGHT: 500,
        MAX_WIDTH: 600
    },

    _defaultConfig: function(){
        return BI.extend(BI.TargetFormulaEmptyFilterItem.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-formula-empty-filter-item"
        })
    },

    _init: function(){
        BI.TargetFormulaEmptyFilterItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.id = o.id;
        var left = this._buildFormulaEmpty();
        this.container = BI.createWidget({
            type: "bi.left",
            items: [left],
            hgap: this._constant.LEFT_ITEMS_H_GAP
        });

        this.deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-h-font"
        });
        this.deleteButton.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, BI.Events.DESTROY, o.id, self);
        });

        this.itemContainer = BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "item-no-type",
            height: this._constant.CONTAINER_HEIGHT,
            items: {
                left: [this.container],
                right: [this.deleteButton]
            },
            rhgap: this._constant.LEFT_ITEMS_H_GAP
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [this.itemContainer]
        });
    },

    populate: function(){

    },

    _buildFormulaEmpty: function(){
        var self = this, o = this.options;
        var formulaPopup = BI.createWidget({
            type: "bi.formula_combo_popup",
            fieldItems: this._getFieldItems()
        });

        var editFormula = BI.createWidget({
            type: "bi.combo",
            isNeedAdjustHeight: true,
            isNeedAdjustWidth: false,
            adjustLength: this._constant.CONDITION_TYPE_COMBO_ADJUST,
            el: {
                type: "bi.button",
                level: "common",
                height: this._constant.BUTTON_HEIGHT,
                text: BI.i18nText("BI-Edit_Formula")
            },
            popup: {
                el: {
                    type: "bi.absolute",
                    height: this._constant.HEIGHT_MAX,
                    width: this._constant.ADD_FORMULA_POPUP_WIDTH,
                    items: [{
                        el: formulaPopup,
                        top: this._constant.FORMULA_V_GAP,
                        left: this._constant.FORMULA_H_GAP,
                        right: this._constant.FORMULA_H_GAP,
                        bottom: this._constant.FORMULA_V_GAP
                    }]
                },
                stopPropagation: false,
                maxHeight: this._constant.MAX_HEIGHT,
                width: this._constant.MAX_WIDTH
            }
        });

        formulaPopup.on(BI.FormulaComboPopup.EVENT_CHANGE, function(){
            self._onEditFormula(editFormula.getValue()[0]);
            self._setNodeData({
                value: BICst.FILTER_TYPE.FORMULA,
                id: self.id,
                filter_type: BICst.FILTER_TYPE.FORMULA,
                filter_value: self.getValue().filter_value
            });
            o.afterValueChange.apply(self, [self.getValue()]);
        });

        formulaPopup.on(BI.FormulaComboPopup.EVENT_VALUE_CANCEL, function(){
            editFormula.hideView();
        });
        return editFormula;
    },

    _getFieldItems: function(){
        var field_id = this.options.field_id, fieldItems = [];
        var tId = BI.Utils.getTableIdByFieldID(field_id);
        var fIds = BI.Utils.getFieldIDsOfTableID(tId);
        BI.each(fIds, function (i, fId) {
            fieldItems.push({
                text: BI.Utils.getFieldNameByID(fId),
                value: BICst.FIELD_ID.HEAD + fId,
                fieldType: BI.Utils.getFieldTypeByID(fId)
            });
        });
        return fieldItems;
    },

    _onEditFormula: function(value){
        this.itemContainer.destroy();
        this.itemContainer = null;
        var o = this.options, self = this;
        this.formulaItem = BI.createWidget({
            type: "bi.target_formula_filter_item",
            element: this.element,
            id: this.options.id,
            dId: this.options.dId,
            field_id: this.options.field_id,
            filter_type: BICst.FILTER_TYPE.FORMULA,
            filter_value: value,
            afterValueChange: o.afterValueChange
        });
        this.formulaItem.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        })
    },

    _setNodeData: function(v){
        var o = this.options;
        o.node.set("data", BI.extend(o.node.get("data"), v));
    },

    getFilterId: function(){
        return this.id;
    },

    getValue: function(){
        if(BI.isNotNull(this.formulaItem)){
            return this.formulaItem.getValue();
        }
        return {
            id: this.id,
            filter_type: BICst.FILTER_TYPE.EMPTY_FORMULA
        };
    }
});
$.shortcut("bi.target_formula_empty_filter_item", BI.TargetFormulaEmptyFilterItem);