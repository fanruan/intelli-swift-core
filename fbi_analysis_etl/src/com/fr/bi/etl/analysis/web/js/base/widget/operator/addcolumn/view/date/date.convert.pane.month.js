/**
 * Created by 小灰灰 on 2016/3/30.
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
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BI.ANALYSIS_ETL_ADD_COLUMN_TYPE.DATE_MONTH, BI.AnalysisETLOperatorAddColumnAbstractDateConvertMonthPane);