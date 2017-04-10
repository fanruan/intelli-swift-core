/**
 * Created by windy on 2017/4/6.
 */
BI.AnalysisETLOperatorFilterSingleContent = BI.inherit(BI.Widget, {

    props: {
        extraCls:"bi-analysis-etl-operator-filter-single-content",
        width:200,
        height:160
    },

    render: function(){
        return {
            type:"bi.vertical",
            tgap : 10,
            lgap : 10,
            rgap : 10,
            items : this.options.data
        }
    }

})


BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.FILTER + "_single_content", BI.AnalysisETLOperatorFilterSingleContent)