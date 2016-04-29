/**
 * Created by 小灰灰 on 2016/4/5.
 */
BI.AnalysisETLOperatorAddColumnExprPeriodModel = BI.inherit(BI.AnalysisETLOperatorAddColumnExprNumberFieldsModel, {
    getDateNumberFields : function () {
        var fields = [];
        fields.push({value : BI.AnalysisETLOperatorAddColumnExprPeriodModel.None, text : BI.i18nText('BI-None')})
        BI.each(this.get(ETLCst.FIELDS), function (i, field) {
            if (field.fieldType === BICst.COLUMN.DATE){
                fields.push(field);
            }
        })
        return fields;
    }
});
BI.AnalysisETLOperatorAddColumnExprPeriodModel.None = 1;