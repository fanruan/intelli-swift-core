/**
 * Created by 小灰灰 on 2016/5/4.
 */
BI.AnalysisETLOperatorAddColumnRankPane  = BI.inherit(BI.AnalysisETLOperatorAddColumnAccPane, {

    _initController : function() {
        return BI.AnalysisETLOperatorAddColumnExprRankController;
    }
});
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.EXPR_RANK, BI.AnalysisETLOperatorAddColumnRankPane);