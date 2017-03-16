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
        GROUP_HEIGHT : 158,
        DETAIL_WIDTH : 230,
        LIST_HEIGHT : 144,
        LIST_DOWN_HEIGHT : 128
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
            type: "bi.text_value_combo",
            height : self._constants.HEIGHT,
            width : self._constants.WIDTH,
            items : []
        });
        self.fieldCombo.on(BI.TextValueCombo.EVENT_CHANGE, function(v){
            self.controller.setValueField(v);
        });
        self.rule = BI.createWidget({
            type: "bi.text_value_combo",
            height : self._constants.HEIGHT,
            width : self._constants.WIDTH,
            items : []
        });
        self.rule.on(BI.TextValueCombo.EVENT_CHANGE, function(v){
            self.controller.setRule(v);
        });
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
            },{el : self.fieldCombo,
                left : 70,
                top : 0
            },{el : self.rule,
                left : 70,
                top : 40
            }
            ]
        })
    },

    _createGroupPane : function () {
        var self = this;
        self.listContainer = BI.createWidget({
            type : 'bi.vertical',
            height : 127
        })
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
                            el : self.listContainer
                        }
                    ]
            }

            )}]
        });

        return self.groupPane;
    },

    refreshGroup : function(items){
        var self = this;
        var list = BI.createWidget({
            type : 'bi.etl_group_sortable_list',
            items : items
        });
        list.on(BI.ETLGroupSortableList.EVENT_CHANGE, function(){
            self.controller.setGroup(list.getValue())
        })  
        self.listContainer.empty();
        self.listContainer.addItem(list);
    },
    
    _createDetailPane : function () {
        var self = this;
        self.labels = BI.createWidget({
            type : 'bi.vertical',
            cls : 'detail-view',
            lgap : self._constants.LGAP,
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
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.EXPR_ACC, BI.AnalysisETLOperatorAddColumnAccPane);