/**
 * Created by roy on 16/3/17.
 */
BI.AddFormulaFieldPopoverModel = BI.inherit(FR.OB, {
    _init: function () {
        BI.AddFormulaFieldPopoverModel.superclass._init.apply(this, arguments);
        var o = this.options;
        var info = o.info;
        this.id = info.id;
        this.formulaData = info.formulaData;
        this.fields = info.fields;
        this.formula = BI.isNotNull(this.formulaData[this.id]) ? this.formulaData[this.id].formula : {};
    },

    initData: function (callback) {
        callback()
    },


    isDuplicate: function (fieldName) {
        var isDuplicated = false;
        var id = this.getID();
        var formulaData = this.getFormulaData();
        var fields = this.getFields();
        delete formulaData[id];
        var findDuplicate = BI.find(formulaData, function (id, formulaObj) {
            if (fieldName === formulaObj.formula.field_name) {
                return true;
            }
        });

        BI.each(fields, function (i, fieldArray) {
            findDuplicate = findDuplicate || BI.find(fieldArray, function (id, field) {
                    if (field.field_name === fieldName) {
                        return true
                    }
                })
        });

        if (BI.isNotNull(findDuplicate)) {
            isDuplicated = true;
        }
        return isDuplicated
    },


    getFields: function () {
        return BI.deepClone(this.fields);
    },

    getFormulaData: function () {
        return BI.deepClone(this.formulaData);
    },

    getFormula: function () {
        return BI.deepClone(this.formula);
    },

    getID: function () {
        return BI.deepClone(this.id);
    }


});
