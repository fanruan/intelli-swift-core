BI.AnalysisETLOperatorFilterSingleContent = FR.extend(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorFilterSingleContent.superclass._defaultConfig.apply(this, arguments), {
            extraCls:"bi-analysis-etl-operator-filter-single-content",
            width:200,
            height:160
        })
    },

    _init: function () {
        BI.AnalysisETLOperatorFilterSingleContent.superclass._init.apply(this, arguments)
        BI.createWidget({
            type:"bi.vertical",
            element:this.element,
            tgap : 10,
            lgap : 10,
            rgap : 10,
            items : this.options.data
        })

    }

})


$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.FILTER + "_single_content", BI.AnalysisETLOperatorFilterSingleContent)