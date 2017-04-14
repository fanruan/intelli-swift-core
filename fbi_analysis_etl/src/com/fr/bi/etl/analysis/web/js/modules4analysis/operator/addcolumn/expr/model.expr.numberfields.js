/**
 * Created by windy on 2017/4/10.
 */
BI.AnalysisETLOperatorAddColumnExprNumberFieldsModel = BI.inherit(BI.AnalysisETLOperatorAddColumnAllFieldsModel, {
    getNumberFields : function () {
        var fields = [];
        BI.each(this.get(ETLCst.FIELDS), function (i, field) {
            if (field.fieldType === BICst.COLUMN.NUMBER){
                fields.push(field);
            }
        })
        return fields;
    }
});