BI.AnalysisETLOperatorAddColumnPane = BI.inherit(BI.MVCWidget, {

    _constant : {
        SINGLE_COLUMN_CARD:"single_column",
        ALL_COLUMNS_CARD :"all_columns"
    },

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorAddColumnPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls:"bi-analysis-etl-operator-add-column-pane",
            value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.ADD_COLUMN,
            model :{
                columns:[]
            },
            controller : {
                columns:[]
            }
        })
    },

    _initController : function () {
        return BI.AnalysisETLOperatorAddColumnPaneController;
    },

    _initModel : function () {
       return BI.AnalysisETLOperatorAddColumnPaneModel;
    },

    cancelHandler : function () {
        this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true)
        return this.controller.cancelColumn();
    },

    saveHandler : function (isEditing) {
        return this.controller.saveColumn(isEditing);
    },

    _initView: function () {
        var self = this;
        this.title = BI.createWidget({
            type:ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + ETLCst.ANALYSIS_TABLE_PANE + ETLCst.ANALYSIS_TABLE_TITLE
        })
        this.title.on(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, function () {
            self.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, arguments)
        });
        this.title.on(BI.AnalysisETLOperatorAddColumnPaneTitle.EVENT_ADD_COLUMN_TYPE_CHANGE, function (v) {
            self.controller.refreshOneConditionPane(v);
        });
        this.title.on(BI.AnalysisETLOperatorAddColumnPaneTitle.EVENT_COLUMN_TYPE_CHANGE, function () {
            self.controller.refreshOneConditionPaneViewIfNeeded();
        })
        this.oneConditionPane = BI.createWidget({
            type : 'bi.vtape',
            scrolly : false
        })
        this.allColumnsPane = BI.createWidget({
            type:ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + ETLCst.ANALYSIS_TABLE_PANE + "_all",
            items: []
        });
        this.allColumnsPane.on(BI.AnalysisETLOperatorAllColumnsPane.EVENT_NEW, function () {
            self.controller.createNewAddColumn()
        });
        this.allColumnsPane.on(BI.AnalysisETLOperatorAllColumnsPane.EVENT_DELETE, function (v) {
            self.controller.deleteColumnByName(v);
        })
        this.allColumnsPane.on(BI.AnalysisETLOperatorAllColumnsPane.EVENT_EDIT, function (v) {
            self.controller.editColumnByName(v);
        })
        this.card = BI.createWidget({
            type:'bi.card',
            element:this.element,
            items:[{
                cardName:this._constant.SINGLE_COLUMN_CARD,
                el: {
                    type:"bi.border",

                    items: {
                        north:{
                            type:"bi.layout",
                            height:10
                        },
                        west:{
                            type:"bi.layout",
                            width:10
                        },
                        east:{
                            type:"bi.layout",
                            width:10
                        },
                        center : {
                            el : {
                                type:"bi.vtape",
                                scrollx:true,
                                cls : "bi-analysis-etl-operator-add-column-single-pane",
                                items:[{
                                    type:"bi.vtape",
                                    cls:"add-column-min-width",
                                    items: [{
                                        el: this.title,
                                        height:45
                                    },{
                                        el : self.oneConditionPane
                                    }]
                                }]
                            }
                        }
                    }
                }
            }, {
                cardName:this._constant.ALL_COLUMNS_CARD,
                el : this.allColumnsPane
            }]
        })
    }

})
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + ETLCst.ANALYSIS_TABLE_PANE, BI.AnalysisETLOperatorAddColumnPane);