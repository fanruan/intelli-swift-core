/**
 * Created by 小灰灰 on 2016/4/5.
 */
BI.AnalysisETLOperatorAddColumnPeriodPane  = BI.inherit(BI.MVCWidget, {
    _constants: {
        GAP : 10,
        HEIGHT: 30,
        WIDTH : 200,
        DETAIL_WIDTH : 230,
        LIST_HEIGHT : 164,
        FIRST_DETAIL_HEIGHT : 25,
        SECOND_DETAIL_HEIGHT : 60,
        LIST_DOWN_HEIGHT : 138,
        LABEL_WIDTH : 60
    },

    _initView: function () {
        var self = this, o = this.options;
        self.combo = BI.createWidget({
            type: "bi.text_value_combo",
            height : self._constants.HEIGHT,
            width : self._constants.WIDTH,
            items : []
        });
        self.combo.on(BI.TextValueCombo.EVENT_CHANGE, function(v){
            self.controller.setValueField(v);
        })
        self.layout = BI.createWidget(    {
            type : 'bi.absolute',
            element : self.element,
            items : [
                {el : {
                    type : 'bi.label',
                    cls : 'label-name',
                    text : BI.i18nText('BI-ETL_Add_Column_EXPR_Warning'),
                    textAlign : 'left'
                },
                    left : 10,
                    top : 15
                }, {el : {
                    type : 'bi.label',
                    cls : 'label-name',
                    text : BI.i18nText('BI-Value_From'),
                    textAlign : 'left'
                },
                    left : 10,
                    top : 45
                },{
                    el : self.combo,
                    left : 75,
                    top : 40
                },{
                    el : self._createGroup(),
                    left : 285,
                    top : 10
                },{
                    el : {
                        type : 'bi.label',
                        cls : 'label-name',
                        text : BI.i18nText('BI-Formula_Time_Field') + BI.i18nText('BI-Setting'),
                        textAlign : 'left'
                    },
                    left : 585,
                    top : 15
                },{
                    el : self._createDetail(),
                    left : 785,
                    top : 10
                }

            ],
            height : self._constants.HEIGHT
        });
    },

    _initController : function() {
        return BI.AnalysisETLOperatorAddColumnExprSamePeriodController;
    },
    
    _initModel : function () {
        return BI.AnalysisETLOperatorAddColumnExprNumberFieldsModel;
    },

    _createDetail: function () {
        var self = this;
        self.labels = BI.createWidget({
            type : 'bi.vertical',
            cls : 'detail-view',
            lgap : self._constants.GAP,
            tgap : self._constants.GAP,
            height : self._constants.LIST_DOWN_HEIGHT
        });
        return BI.createWidget({
            type : 'bi.vertical',
            width : self._constants.DETAIL_WIDTH,
            height : self._constants.LIST_HEIGHT,
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
    
    _createGroup: function () {
        var self = this;
        self.listContainer = BI.createWidget({
            type : 'bi.vertical',
            height : 133
        })
        return BI.createWidget({
            type : 'bi.vertical',
            cls : 'group-list',
            width : self._constants.WIDTH,
            height : self._constants.LIST_HEIGHT,
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
        });
    },
});