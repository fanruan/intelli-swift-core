/**
 * Created by roy on 16/3/30.
 */
BI.CalculateTargetPopupDetailModel = BI.inherit(FR.OB, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetPopupDetailModel.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BI.CalculateTargetPopupDetailModel.superclass._init.apply(this, arguments);
        this._initData();
    },

    _initData: function () {
        var o = this.options;
        this.wId = o.wId;
        this.dimensions = BI.firstObject(BI.Utils.getWidgetViewByID(this.wId));
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
            name: BI.Utils.getDimensionNameByID(dId)
        }
    },

    _createNewTargetData: function () {
        return {
            _src: {
                expression: ""
            },
            name: "",
            dimension_map: {},
            type: BICst.TARGET_TYPE.FORMULA,
            used: true
        };

    },

    _isNumberOrCalTarget: function (dId) {
        var targetType = BI.Utils.getDimensionTypeByID(dId);
        return targetType === BICst.TARGET_TYPE.NUMBER || targetType === BICst.TARGET_TYPE.FORMULA
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


    setFormulaExpression: function (expression) {
        this.targetData._src.expression = expression;
    },

    setTargetName: function (name) {
        this.targetData.name = name;
    },

    getFormulaFields: function () {
        var self = this;
        var dimensions = BI.Utils.getAllDimensionIDs(this.wId);
        var fields = [];
        BI.each(dimensions, function (i, dId) {
            if (dId != self.targetId && self._isNumberOrCalTarget(dId)) {
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

    getFormulaExpression: function () {
        var expression = "";
        if (this._isNotNewTarget()) {
            expression = BI.Utils.getExpressionByDimensionID(this.targetId).formula_value;
        }
        return expression;
    },


    getDimensions: function () {
        return BI.deepClone(this.dimensions);
    },

    getTargetName: function () {
        return BI.deepClone(this.targetData.name)
    },

    getValue: function () {
        return {
            dId: this.targetId,
            data: this.targetData
        }
    }


});