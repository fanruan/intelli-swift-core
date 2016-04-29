/**
 * @class BIShow.DetailDimensionView
 * @extend BI.View
 * 明细表的单个维度
 */
BIShow.DetailDimensionView = BI.inherit(BI.View, {

    constants: {
        DIMENSION_BUTTON_HEIGHT: 25,
        COMBO_WIDTH: 25,
        CONTAINER_HEIGHT: 25
    },

    _defaultConfig: function () {
        return BI.extend(BIShow.DetailDimensionView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-dimension-view"
        })
    },

    _init: function () {
        alert('show.detail.dimension.js!:render');
        BIShow.DetailDimensionView.superclass._init.apply(this, arguments);
    },

    change: function (changed) {

    },

    _render: function (vessel) {

        var self = this;
        this.usedCheck = BI.createWidget({
            type: "bi.checkbox"
        });
        this.usedCheck.setSelected(this.model.get("used"));
        this.usedCheck.on(BI.Checkbox.EVENT_CHANGE, function () {
            self.model.set("used", self.usedCheck.isSelected());
        });
        this.editor = BI.createWidget({
            type: "bi.sign_editor",
            height: this.constants.DIMENSION_BUTTON_HEIGHT,
            cls: "bi-dimension-name",
            validationChecker: function () {
                return self._checkDimensionName(self.editor.getValue());
            }
        });
        this.editor.setValue(this.model.get("name"));
        this.editor.setState(this.model.get("name"));
        this.editor.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.model.set("name", self.editor.getValue());
        });


        switch (this.model.get("type")) {
            case BICst.COLUMN.STRING:
                this._createStringCombo();
                break;
            case BICst.COLUMN.NUMBER:
                this._createNumberCombo();
                break;
            case BICst.COLUMN.DATE:
                this._createDateCombo();
                break;
            default :
                this._createStringCombo();
        }


        BI.createWidget({
            type: "bi.default",
            element: vessel,
            tagName: "li",
            height: this.constants.CONTAINER_HEIGHT,
            data: {id: this.model.get("id")},
            items: [{
                type: "bi.border",
                height: this.constants.CONTAINER_HEIGHT,
                items: {
                    west: {
                        el: {
                            type: "bi.center_adapt",
                            items: [this.usedCheck]
                        },
                        width: this.constants.COMBO_WIDTH
                    },
                    center: {
                        el: this.editor
                    },
                    east: {el: this.combo, width: this.constants.COMBO_WIDTH}
                }
            }]
        });
    },

    _checkDimensionName: function (name) {
        var currId = this.model.get("id");
        var widgetId = BI.Utils.getWidgetIDByDimensionID(currId);
        var dimsId = BI.Utils.getAllDimensionIDs(widgetId);
        var valid = true;
        BI.some(dimsId, function (i, id) {
            if (currId !== id && BI.Utils.getDimensionNameByID(id) === name) {
                valid = false;
                return true;
            }
        });
        return valid;
    },

    _createStringCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.detail_string_dimension_combo",
            dId: self.model.get("id")
        });
        this.combo.on(BI.DetailStringDimensionCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.DETAIL_STRING_COMBO.FILTER:
                    self._buildFilterPane();
                    break;
                case BICst.DETAIL_STRING_COMBO.HYPERLINK:
                    break;
                case BICst.DETAIL_STRING_COMBO.DELETE:
                    self._deleteDimension();
                    break;
                case BICst.DETAIL_STRING_COMBO.INFO:
                    break;
            }
        })
    },

    _createNumberCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.detail_number_dimension_combo",
            dId: self.model.get("id")
        });
        this.combo.on(BI.DetailNumberDimensionCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.DETAIL_NUMBER_COMBO.FORM_SETTING:
                    break;
                case BICst.DETAIL_NUMBER_COMBO.FILTER:
                    self._buildFilterPane();
                    break;
                case BICst.DETAIL_NUMBER_COMBO.HYPERLINK:
                    break;
                case BICst.DETAIL_NUMBER_COMBO.DELETE:
                    self._deleteDimension();
                    break;
                case BICst.DETAIL_NUMBER_COMBO.INFO:
                    break;
            }
        });
    },

    _createDateCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.detail_date_dimension_combo",
            dId: self.model.get("id")
        });
        this.combo.on(BI.DetailDateDimensionCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.DETAIL_DATE_COMBO.YMD:
                    break;
                case BICst.DETAIL_DATE_COMBO.YMD_HMS:
                    break;
                case BICst.DETAIL_DATE_COMBO.YEAR:
                    break;
                case BICst.DETAIL_DATE_COMBO.SEASON:
                    break;
                case BICst.DETAIL_DATE_COMBO.MONTH:
                    break;
                case BICst.DETAIL_DATE_COMBO.WEEK:
                    break;
                case BICst.DETAIL_DATE_COMBO.FILTER:
                    self._buildFilterPane();
                    break;
                case BICst.DETAIL_DATE_COMBO.HYPERLINK:
                    break;
                case BICst.DETAIL_DATE_COMBO.DELETE:
                    self._deleteDimension();
                    break;
                case BICst.DETAIL_DATE_COMBO.INFO:
                    break;
            }
        })
    },

    _createFormulaCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.detail_formula_dimension_combo",
            dId: this.model.get("id")
        });
        this.combo.on(BI.DetailFormulaDimensionCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.DETAIL_FORMULA_COMBO.FORM_SETTING:
                    break;
                case BICst.DETAIL_FORMULA_COMBO.UPDATE_FORMULA:
                    self._updateFormula();
                    break;
                case BICst.DETAIL_FORMULA_COMBO.HYPERLINK:
                    break;
                case BICst.DETAIL_FORMULA_COMBO.DELETE:
                    self._deleteDimension();
                    break;
            }
        })
    },

    _buildFilterPane: function () {
        BIShow.FloatBoxes.open("detailTargetFilter", "conditions", {}, this, {id: this.model.get("id")});
    },

    _deleteDimension: function () {
        this.model.destroy();
    },

    _updateFormula: function () {
        var self = this;
        var id = this.model.get("id");
        var wId = BI.Utils.getWidgetIDByDimensionID(id);
        var dimensions = BI.Utils.getAllDimensionIDs(wId);
        var value = this.model.get("value");
        var fieldItems = [];
        BI.each(dimensions, function (i, id) {
            var type = BI.Utils.getDimensionTypeByID(id);
            if (type === BICst.COLUMN.NUMBER) {
                fieldItems.push({
                    text: BI.Utils.getDimensionNameByID(id),
                    value: BI.Utils.getDimensionNameByID(id),
                    fieldType: type
                })
            }
        });
        var detailCalculate = BI.createWidget({
            type: "bi.detail_table_calculate",
            fieldItems: fieldItems
        });
        BI.Popovers.create("addCalculate", detailCalculate).open("addCalculate");
        detailCalculate.on(BI.DetailTableCalculate.EVENT_SAVE, function () {
            self.model.set("valueChange", detailCalculate.getValue());
        });
        detailCalculate.setValue(value);
        detailCalculate.on(BI.DetailTableCalculate.EVENT_CLOSE, function () {
            BI.Popovers.remove("addCalculate");
        });
    },

    local: function () {

    },

    refresh: function () {

    }
});
