/**
 * Created by 小灰灰 on 2016/4/6.
 */
BI.AnalysisETLOperatorAddColumnAccPane  = BI.inherit(BI.MVCWidget, {
    _constants: {
        TGAP : 20,
        LGAP : 10,
        HEIGHT: 30,
        WIDTH : 200,
        COMBO_PANE_WIDTH : 270,
        PANE_HEIGHT : 200,
        GROUP_HEIGHT : 178,
        DETAIL_WIDTH : 230,
        LIST_HEIGHT : 164,
        LIST_DOWN_HEIGHT : 148
    },

    _initView: function () {
        var self = this, o = this.options;
        BI.createWidget({
            type : 'bi.horizontal',
            tgap : self._constants.TGAP,
            lgap : self._constants.LGAP,
            element : self.element,
            items : [self._createComboPane(), self._createGroupPane(), self._createDetailPane()]
        })
    },

    _createComboPane : function () {
        var self = this;
        self.fieldCombo = BI.createWidget({
            type: "bi.text_icon_combo",
            height : self._constants.HEIGHT,
            width : self._constants.WIDTH,
            items : []
        });
        self.fieldCombo.on(BI.TextIconCombo.EVENT_CHANGE, function(v){
            self.controller.setValueField(v);
        });
        self.rule = BI.createWidget({
            type: "bi.text_icon_combo",
            height : self._constants.HEIGHT,
            width : self._constants.WIDTH,
            items : []
        });
        self.rule.on(BI.TextIconCombo.EVENT_CHANGE, function(v){
            self.controller.setRule(v);
        });
        self.sumType = BI.createWidget({
            type: "bi.text_icon_combo",
            height : self._constants.HEIGHT,
            width : self._constants.WIDTH,
            items : BICst.SUMMARY_TYPE_ITEMS
        });
        self.sumType.on(BI.TextIconCombo.EVENT_CHANGE, function(v){
            self.controller.setSumType(v);
        });
        self.sumLabel = BI.createWidget({
            type : 'bi.label',
            cls : 'label-name',
            text : BI.i18nText('BI-Statistic_Type'),
            textAlign : 'left'
        })
        return BI.createWidget({
            type : 'bi.absolute',
            width : self._constants.COMBO_PANE_WIDTH,
            height : self._constants.PANE_HEIGHT,
            items : [{el : {
                type : 'bi.label',
                cls : 'label-name',
                text : BI.i18nText('BI-Value_From'),
                textAlign : 'left'
                },
                left : 0,
                top : 5
            },{el : {
                type : 'bi.label',
                cls : 'label-name',
                text : BI.i18nText('BI-Value_Principle'),
                textAlign : 'left'
                },
                left : 0,
                top : 45
            },{el : self.sumLabel,
                left : 0,
                top : 85
            },{el : self.fieldCombo,
                left : 70,
                top : 0
            },{el : self.rule,
                left : 70,
                top : 40
            },{el : self.sumType,
                left : 70,
                top : 80
            }
            ]
        })
    },

    _createGroupPane : function () {
        var self = this;
        self.list = BI.createWidget({
            type : 'bi.etl_group_sortable_list'
        });
        self.groupPane = BI.createWidget({
            type : 'bi.absolute',
            width : self._constants.WIDTH,
            height : self._constants.PANE_HEIGHT,
            items : [{el : BI.createWidget({
                    type : 'bi.vertical',
                    cls : 'group-list',
                    width : self._constants.WIDTH,
                    height : self._constants.GROUP_HEIGHT,
                    scrolly : false,
                    items : [
                        {
                            el : BI.createWidget({
                                type : 'bi.label',
                                text : BI.i18nText('BI-Group_Detail_Setting'),
                                cls : 'first-label label-name',
                                textAlign : 'center',
                                height : self._constants.HEIGHT
                            })
                        },
                        {
                            el : self.list
                        }
                    ]
            }

            )}]
        });

        return self.groupPane;
    },

    _createDetailPane : function () {
        var self = this;
        self.labels = BI.createWidget({
            type : 'bi.vertical',
            cls : 'detail-view',
            height : self._constants.LIST_DOWN_HEIGHT
        });
        return BI.createWidget({
            type : 'bi.vertical',
            width : self._constants.DETAIL_WIDTH,
            height : self._constants.PANE_HEIGHT,
            cls :'group-detail',
            scrolly : false,
            items : [
                {
                    el : BI.createWidget({
                        type : 'bi.label',
                        cls : 'label-name',
                        text : BI.i18nText('BI-Group_Detail_Short'),
                        textAlign : 'left',
                        height : self._constants.HEIGHT
                    })
                },
                {
                    el : self.labels
                }
            ]
        });
    },

    _initController : function() {
        return BI.AnalysisETLOperatorAddColumnExprAccController;
    },

    _initModel : function () {
        return BI.AnalysisETLOperatorAddColumnExprNumberFieldsModel;
    }
});
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BI.ANALYSIS_ETL_ADD_COLUMN_TYPE.EXPR_ACC, BI.AnalysisETLOperatorAddColumnAccPane);
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BI.ANALYSIS_ETL_ADD_COLUMN_TYPE.EXPR_RANK, BI.AnalysisETLOperatorAddColumnAccPane);
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BI.ANALYSIS_ETL_ADD_COLUMN_TYPE.EXPR_SUM, BI.AnalysisETLOperatorAddColumnAccPane);