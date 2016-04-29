BI.AnalysisETLOperatorUsePartPane = FR.extend(BI.MVCWidget, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorUsePartPane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-operator-use-part-pane",
            value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.USE_PART_FIELDS
        })
    },

    _initController : function () {
        return BI.AnalysisETLOperatorUsePartPaneController;
    },

    _initView: function () {
        this.fieldList = BI.createWidget({
            type: "bi.select_part_field_list",
        });
        var self = this;
        this.fieldList.on(BI.SelectPartFieldList.EVENT_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, self.controller, self.options.value.operatorType)
            self.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, self.controller.isValid())
        })
        BI.createWidget({
            type:"bi.vtape",
            element:this.element,
            items:[{
                height:40,
                el : {
                    type:"bi.htape",
                    cls:"title",
                    height:40,
                    items:[{
                        type:"bi.layout",
                        width:10
                    },{
                        el : {
                            type:"bi.center_adapt",
                            items :[{
                                type:"bi.label",
                                textAlign :"left",
                                text:BI.i18nText("BI-Choose_Use_Fields")
                            }]
                        }
                    }]
                }
            }, {
                el : this.fieldList
            }]
        })
    }

})

$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.USE_PART_FIELDS  + ETLCst.ANALYSIS_TABLE_PANE, BI.AnalysisETLOperatorUsePartPane);