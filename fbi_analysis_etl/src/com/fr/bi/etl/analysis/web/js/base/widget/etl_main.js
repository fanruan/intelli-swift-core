BI.AnalysisETLMain = FR.extend(BI.MVCWidget, {

    _constant : {
        buttonHeight:30,
        buttonWidth:90,
        titleHeight:40
    },

    _defaultConfig: function() {
        var conf = BI.AnalysisETLMain.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls : "bi-analysis-etl-main bi-analysis-etl-main-animate",
            data : null
        })
    },

    _initView : function() {
        var self = this;
        this.saveButton = BI.createWidget({
            type:"bi.button",
            height: this._constant.buttonHeight,
            width: this._constant.buttonWidth,
            text:BI.i18nText("BI-Save"),
            handler : function(e){
               self.controller.save();
            }
        })

        var cancelButton = BI.createWidget({
            type:"bi.button",
            height: this._constant.buttonHeight,
            width: this._constant.buttonWidth,
            level:"ignore",
            text:BI.i18nText("BI-Cancel"),
            handler : function(e){
                self.controller.doCancel()
            }
        })

        var title = BI.createWidget({
            type:"bi.right",
            height:this._constant.titleHeight,
            items : [{
                type:"bi.center_adapt",
                cls:"bi-analysis-etl-main-save-button",
                items:[this.saveButton],
                height:this._constant.titleHeight
            }, {
                type:"bi.center_adapt",
                cls:"bi-analysis-etl-main-save-button",
                items:[cancelButton],
                height:this._constant.titleHeight
            }]
        })
        self.tab = BI.createWidget({
            type: "bi.dynamic_tab",
            items: []
        })
        this.registerChildWidget(BI.AnalysisETLMainModel.TAB, self.tab)
        BI.createWidget({
            type:"bi.vtape",
            element: this.element,
            items: [{
                el: title,
                height:this._constant.titleHeight
            }, {
                el:self.tab,
            }]
        })
    },

    _initController : function () {
        return BI.AnalysisETLMainController;
    },

    _initModel : function () {
        return BI.AnalysisETLMainModel;
    },

    setVisible : function(v){
        BI.AnalysisETLMain.superclass.setVisible.apply(this, arguments);
        if(v === true){
            BI.Layers.show(ETLCst.ANALYSIS_LAYER);
        } else {
            BI.Layers.remove(ETLCst.ANALYSIS_LAYER);
        }
    }

})
$.shortcut("bi.analysis_etl_main", BI.AnalysisETLMain);