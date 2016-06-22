/**
 * Created by roy on 16/3/30.
 */
BI.CalculateTargetPopupSummaryModel = BI.inherit(FR.OB, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetPopupSummaryModel.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BI.CalculateTargetPopupSummaryModel.superclass._init.apply(this, arguments);
        this._initData();
    },

    _initData: function () {
        var o = this.options;
        this.wId = o.wId;
        this.dimensions = BI.Utils.getAllTargetDimensionIDs(this.wId);
        this.targetId = o.targetId;
        this.targetData = this._isNotNewTarget() ? this._createTargetData(this.targetId) : this._createNewTargetData();
    },

    _isNotNewTarget: function () {
        return BI.contains(this.dimensions, this.targetId);
    },

    _createTargetData: function (dId) {
        return {
            _src: {
                expression: BI.Utils.getExpressionByDimensionID(dId)
            },
            name: BI.Utils.getDimensionNameByID(dId),
            type: BI.Utils.getDimensionTypeByID(dId)
        }
    },

    _createNewTargetData: function () {
        return {
            _src: {
                expression: {
                    formula_value: "",
                    ids: []
                }
            },
            name: BI.Func.createDistinctName(BI.Utils.getWidgetDimensionsByID(this.wId), BI.i18nText("BI-Calculation_Index")),
            dimension_map: {},
            type: BICst.TARGET_TYPE.FORMULA,
            used: true
        };
    },

    isDuplicated: function (v) {
        var self = this;
        var dimensions = this.getDimensions();
        return BI.some(dimensions, function (i, dId) {
            if (BI.Utils.getDimensionNameByID(dId) === v && dId != self.targetId) {
                return true
            }
        });
    },

    setTargetExpression: function (expression) {
        this.targetData._src.expression = expression;
    },

    setTargetType: function (type) {
        this.targetData.type = type;
    },

    setTargetName: function (name) {
        this.targetData.name = name;
    },

    getTargetName: function () {
        return BI.deepClone(this.targetData.name);
    },

    getTargetType: function () {
        return BI.deepClone(this.targetData.type);
    },

    getFormulaFields: function () {
        var self = this;
        var dimensions = this.getDimensions();
        var fields = [];
        BI.each(dimensions, function (i, dId) {
            if (dId != self.targetId) {
                var fieldItem = {};
                var fieldName = BI.Utils.getDimensionNameByID(dId);
                fieldItem.text = fieldName;
                fieldItem.value = dId;
                fieldItem.title = fieldName;
                fieldItem.fieldType = BICst.COLUMN.NUMBER;
                fields.push(fieldItem);
            }
        });
        return fields;
    },


    getTargetExpression: function () {
        var expression = "";
        if (this._isNotNewTarget()) {
            expression = BI.Utils.getExpressionByDimensionID(this.targetId);
        }
        return expression;
    },

    getFieldComboItems: function () {
        var self = this;
        var dimensions = this.getDimensions();
        var fields = [];
        BI.each(dimensions, function (i, dId) {
            if (dId != self.targetId) {
                var fieldItem = {};
                fieldItem.text = BI.Utils.getDimensionNameByID(dId);
                fieldItem.value = dId;
                fields.push(fieldItem);
            }

        });
        return fields;
    },

    getDimDimensionIDs: function () {
        return BI.Utils.getAllDimDimensionIDs(this.wId);
    },


    getDimensions: function () {
        return BI.deepClone(this.dimensions);
    },

    getValue: function () {
        return {
            dId: this.targetId,
            data: this.targetData
        }
    }
});