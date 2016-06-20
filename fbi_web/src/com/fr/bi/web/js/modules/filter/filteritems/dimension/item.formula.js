/**
 * @class BI.DimensionFormulaFilterItem
 * @extend BI.AbstractFilterItem
 * 过滤条件——公式
 */
BI.DimensionFormulaFilterItem = BI.inherit(BI.AbstractFilterItem, {

    _constant: {
        LEFT_ITEMS_H_GAP: 5,
        CONTAINER_HEIGHT: 40,
        BUTTON_HEIGHT: 30,
        FIELD_NAME_BUTTON_WIDTH: 120,
        ICON_BUTTON_WIDTH: 22,
        TEXT_BUTTON_H_GAP: 15
    },

    _defaultConfig: function(){
        return BI.extend(BI.DimensionFormulaFilterItem.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-formula-filter-item"
        })
    },

    _init: function(){
        BI.DimensionFormulaFilterItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.id = o.id;
        var left = this._buildFormula(o.filter_value);

        this.deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-h-font",
            width: this._constant.ICON_BUTTON_WIDTH
        });
        this.deleteButton.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, BI.Events.DESTROY, o.id, self);
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                type: "bi.td",
                columnSize: [this._constant.FIELD_NAME_BUTTON_WIDTH, "", this._constant.ICON_BUTTON_WIDTH],
                height: this._constant.CONTAINER_HEIGHT,
                items: [[left[0], left[1], this.deleteButton]]
            }]
        });
    },

    populate: function(item){
        this.formula.setValue(item.filter_value);
    },

    _buildFormula: function(value){
        var self = this, o = this.options;
        this.fulfilLabel = BI.createWidget({
            type: "bi.text_button",
            text: BI.i18nText("BI-Fulfil"),
            width: this._constant.FIELD_NAME_BUTTON_WIDTH,
            height: this._constant.BUTTON_HEIGHT,
            textAlign: "left",
            hgap: this._constant.TEXT_BUTTON_H_GAP
        });
        this.fulfilLabel.on(BI.Controller.EVENT_CHANGE, function(){
            arguments[1] = BI.FilterPane.FILTER_PANE_CLICK_ITEM;
            arguments[2] = self;
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        this.formula = BI.createWidget({
            type: "bi.formula_combo",
            items: this._getTargetItems()
        });

        this.formula.setValue(self.formula.getValue() || value);

        this.formula.on(BI.FormulaCombo.EVENT_CHANGE, function(){
            self._setNodeData({
                filter_value : this.getValue()
            });
            o.afterValueChange.apply(self, [self.getValue()]);
        });
        return [this.fulfilLabel, this.formula];
    },

    _getTargetItems: function () {
        var dId = this.options.dId, fieldItems = [];
        var wId = BI.Utils.getWidgetIDByDimensionID(dId);
        var tIds = BI.Utils.getAllTargetDimensionIDs(wId);
        BI.each(tIds, function (i, tId) {
            var fieldType = BICst.COLUMN.STRING;
            switch (BI.Utils.getDimensionTypeByID(tId)){
                case BICst.TARGET_TYPE.STRING:
                    fieldType = BICst.COLUMN.STRING;
                    break;
                case BICst.TARGET_TYPE.DATE:
                    fieldType = BICst.COLUMN.DATE;
                    break;
                case BICst.TARGET_TYPE.NUMBER:
                case BICst.TARGET_TYPE.FORMULA:
                case BICst.TARGET_TYPE.YEAR_ON_YEAR_RATE:
                case BICst.TARGET_TYPE.MONTH_ON_MONTH_RATE:
                case BICst.TARGET_TYPE.YEAR_ON_YEAR_VALUE:
                case BICst.TARGET_TYPE.MONTH_ON_MONTH_VALUE:
                case BICst.TARGET_TYPE.SUM_OF_ABOVE:
                case BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP:
                case BICst.TARGET_TYPE.SUM_OF_ALL:
                case BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP:
                case BICst.TARGET_TYPE.RANK:
                case BICst.TARGET_TYPE.RANK_IN_GROUP:
                    fieldType = BICst.COLUMN.NUMBER;
                    break;
            }
            fieldItems.push({
                text: BI.Utils.getDimensionNameByID(tId),
                value: tId,
                fieldType: fieldType
            });
        });
        return fieldItems;
    },

    _setNodeData: function (v) {
        var o = this.options;
        o.node.set("data", BI.extend(o.node.get("data"), v));
    },

    getFilterId: function () {
        return this.id;
    },

    getValue: function () {
        return {
            id: this.id,
            filter_type: BICst.FILTER_TYPE.FORMULA,
            filter_value: this.formula.getValue()
        };
    }

});
$.shortcut("bi.dimension_formula_filter_item", BI.DimensionFormulaFilterItem);