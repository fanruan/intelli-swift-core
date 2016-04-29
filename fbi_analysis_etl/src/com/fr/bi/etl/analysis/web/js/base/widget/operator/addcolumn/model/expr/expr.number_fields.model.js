/**
 * Created by 小灰灰 on 2016/4/6.
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