/**
 * Created by windy on 2017/4/5.
 */
BI.AnalysisETLOperatorFilterPane = BI.inherit(BI.Widget, {

    _constant : {
        nullCard : "null_card",
        filterCard : "filter_card"
    },

    props: {
        extraCls:"bi-analysis-etl-operator-filter-pane",
        value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.FILTER
    },

    _createTabs: function(v){
        var self = this;
        switch (v) {
            case this._constant.nullCard:
                return BI.createWidget({
                    type:"bi.center_adapt",
                    items:[{
                        type:"bi.label",
                        cls:"null-label",
                        text: BI.i18nText('BI-Add_Filter_ON_Table_Title')
                    }]
                });
            case this._constant.filterCard:
                return BI.createWidget({
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
                                            }, {
                                                type:'bi.text_value_combo',
                                                width: 250,
                                                height: 30,
                                                ref: function(_ref){
                                                    self.operatorCombo = _ref;
                                                },
                                                items: [{
                                                    text: BI.i18nText('BI-Show_Operator_Together_AND'),
                                                    value: BICst.FILTER_TYPE.AND
                                                }, {
                                                    text: BI.i18nText('BI-Show_Operator_Together_OR'),
                                                    value: BICst.FILTER_TYPE.OR
                                                }],
                                                listeners: [{
                                                    eventName: BI.TextValueCombo.EVENT_CHANGE,
                                                    action: function(){
                                                        self.controller.operatorChange(self.operatorCombo.getValue()[0]);
                                                    }
                                                }]
                                            }]
                                        }]
                                    },
                                    height:30
                                },  {
                                    type:"bi.layout",
                                    height:10
                                }, {
                                    type:ETLCst.ANALYSIS_ETL_PAGES.FILTER + "_table",
                                    ref: function(_ref){
                                        self.content = _ref;
                                    }
                                }]
                            }
                        }
                    }
                })
        }
    },

    render: function(){
        var self = this;
        this.operatorCombo = this.content = this.card = null;
        return {
            type:"bi.tab",
            ref: function(_ref){
                self.card = _ref;
            },
            cardCreator: BI.bind(this._createTabs, this)
        };
    }
})

BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.FILTER + ETLCst.ANALYSIS_TABLE_PANE, BI.AnalysisETLOperatorFilterPane);