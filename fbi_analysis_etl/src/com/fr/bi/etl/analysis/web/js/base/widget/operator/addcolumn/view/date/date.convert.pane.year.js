/**
 * Created by 小灰灰 on 2016/3/30.
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
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BI.ANALYSIS_ETL_ADD_COLUMN_TYPE.DATE_YEAR, BI.AnalysisETLOperatorAddColumnAbstractDateConvertYearPane);