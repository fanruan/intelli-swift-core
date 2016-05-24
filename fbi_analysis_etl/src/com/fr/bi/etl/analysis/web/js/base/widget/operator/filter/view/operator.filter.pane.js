BI.AnalysisETLOperatorFilterPane = FR.extend(BI.MVCWidget, {

    _constant : {
        nullCard : "null_card",
        filterCard : "filter_card"
    },

    _initController : function () {
        return BI.AnalysisETLOperatorFilterPaneController;
    },

    _initModel : function () {
        return BI.AnalysisETLOperatorFilterPaneModel;
    },

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorFilterPane.superclass._defaultConfig.apply(this, arguments), {
            extraCls:"bi-analysis-etl-operator-filter-pane",
            value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.FILTER,
            model:{
                operator:BICst.FILTER_TYPE.AND,
                items:[1]
            }
        })
    },

    _initView: function () {
        var self = this;
        this.operatorCombo = BI.createWidget({
            type:'bi.text_value_combo',
            width: 250,
            height: 30,
            items: [{
                text: BI.i18nText('BI-Show_Operator_Together_AND'),
                value: BICst.FILTER_TYPE.AND
            }, {
                text: BI.i18nText('BI-Show_Operator_Together_OR'),
                value: BICst.FILTER_TYPE.OR
            }]
        });
        this.operatorCombo.on(BI.TextValueCombo.EVENT_CHANGE, function () {
            self.controller.operatorChange(self.operatorCombo.getValue()[0]);
        })
        this.content = BI.createWidget({
            type:ETLCst.ANALYSIS_ETL_PAGES.FILTER + "_table"
        })
        this.card = BI.createWidget({
            element:this.element,
            type:"bi.card",
            items:[{
                cardName: this._constant.nullCard,
                el :{
                    type:"bi.center_adapt",
                    items:[{
                        type:"bi.label",
                        cls:"null-label",
                        text: BI.i18nText('BI-Add_Filter_ON_Table_Title')
                    }]
                }
            }, {
                cardName: this._constant.filterCard,
                el :{
                    type:"bi.border",
                    items :{
                        west :{
                            el : {
                                type:"bi.layout"
                            },
                            width:10
                        },
                        east : {
                            el : {
                                type:"bi.layout"
                            },
                            width:10
                        },
                        center: {
                            el:{
                                type:"bi.vtape",
                                items:[{
                                        type:"bi.layout",
                                        height:10
                                    },{
                                    el:{
                                        type:"bi.center_adapt",
                                        cls:"operator-choose",
                                        items:[{
                                            type:"bi.vertical_adapt",
                                            items:[{
                                                type:"bi.label",
                                                cls:'bi-label-msg',
                                                text: BI.i18nText('BI-Show_Filter')
                                            }, {
                                                type:"bi.layout",
                                                width:10
                                            },this.operatorCombo]
                                        }]
                                    },
                                    height:30
                                },  {
                                    type:"bi.layout",
                                    height:10
                                }, this.content]
                            }
                        }
                    }
                }
            }]
        })
    }
})

$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.FILTER + ETLCst.ANALYSIS_TABLE_PANE, BI.AnalysisETLOperatorFilterPane);