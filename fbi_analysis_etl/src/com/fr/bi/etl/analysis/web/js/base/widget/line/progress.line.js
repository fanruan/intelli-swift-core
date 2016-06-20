BI.AnalysisProgressBar = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisProgressBar.superclass._defaultConfig.apply(this, arguments), {
            tagName: "div",
            extraCls: "bi-analysis-progress"
        })
    },

    _init : function () {
        BI.AnalysisProgressBar.superclass._init.apply(this, arguments);
        this.per = BI.createWidget({
            type:"bi.layout",
            cls:"bar",
            width:this.options.width,
            height:this.options.height
        })

        BI.createWidget({
            type:"bi.absolute",
            element:this.per,
            items:[{
                el:{
                    type:"bi.layout",
                    cls:"bi-analysis-progress-point",
                    width:5,
                    height:this.options.height
                }
            }]
        })

        BI.createWidget({
            type:"bi.absolute",
            element:this.element,
            items:[this.per]
        })

    },


    setPercent : function (percent) {
        this.per.setWidth(this.options.width * percent)
    }

})
$.shortcut("bi.analysis_progress", BI.AnalysisProgressBar);