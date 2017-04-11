/**
 * Created by windy on 2017/4/10.
 */
BI.AnalysisETLOperatorAddColumnAbstractDateConvertMonthPane = BI.inherit(BI.AnalysisETLOperatorAddColumnAbstractDateConvertPane, {
    _createLabel : function () {
        return BI.createWidget({
            type : 'bi.label',
            cls : 'label-name',
            text : BI.i18nText('BI-Date_Month'),
            textAlign : 'left'
        })
    }
})
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.DATE_MONTH, BI.AnalysisETLOperatorAddColumnAbstractDateConvertMonthPane);