/**
 * Created by windy on 2017/4/7.
 */
BI.AnalysisETLOperatorUsePart = BI.inherit(BI.AnalysisOperatorAbstractPane, {

    props: {
        extraCls: "bi-analysis-etl-operator-select-data",
        value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.USE_PART_FIELDS
    },

    _init : function() {
        BI.AnalysisETLOperatorUsePart.superclass._init.apply(this, arguments);
        this._editing = false;
    }
})

BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.USE_PART_FIELDS, BI.AnalysisETLOperatorUsePart);