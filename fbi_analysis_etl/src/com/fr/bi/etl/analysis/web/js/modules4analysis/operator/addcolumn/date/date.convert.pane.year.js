/**
 * Created by windy on 2017/4/10.
 */
BI.AnalysisETLOperatorAddColumnAbstractDateConvertYearPane = BI.inherit(BI.AnalysisETLOperatorAddColumnAbstractDateConvertPane, {
    _createLabel : function () {
        return BI.createWidget({
            type : 'bi.label',
            cls : 'label-name',
            text : BI.i18nText('BI-Date_Year'),
            textAlign : 'left'
        })
    }
})
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.DATE_YEAR, BI.AnalysisETLOperatorAddColumnAbstractDateConvertYearPane);