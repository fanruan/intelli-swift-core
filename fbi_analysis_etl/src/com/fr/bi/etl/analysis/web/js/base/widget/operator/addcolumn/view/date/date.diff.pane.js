/**
 * Created by 小灰灰 on 2016/3/24.
 */
BI.AnalysisETLOperatorAddColumnDateDiffPane = BI.inherit(BI.MVCWidget, {
    _constants : {
        TGAP : 10,
        LGAP : 10,
        RGAP : 5,
        HEIGHT : 30,
        LABEL_WIDTH : 45,
        SMALL_LABEL_WIDTH : 20,
        SEGMENT_WIDTH : 480,
        COMBO_WIDTH : 225
    },

    _initView : function () {
        var self = this, o = this.options;
        self.segment = BI.createWidget({
            type : 'bi.segment',
            items : ETLCst.ANALYSIS_ADD_COLUMN_DATE_DIFF_TYPE_ITEMS
        });
        self.segment.on(BI.Segment.EVENT_CHANGE, function (type) {
            self.controller.setType(type);
        });
        self.lcombo = BI.createWidget({
            type: "bi.text_value_combo",
            height : self._constants.HEIGHT,
            items : []
        });
        self.lcombo.on(BI.TextValueCombo.EVENT_CHANGE, function(v){
            self.controller.setFirstField(v);
        })
        self.rcombo = BI.createWidget({
            type: "bi.text_value_combo",
            height:self._constants.HEIGHT,
            items :[]
        });
        self.rcombo.on(BI.TextValueCombo.EVENT_CHANGE, function(v){
            self.controller.setSecondField(v);
        })
        BI.createWidget({
            type : 'bi.vertical',
            tgap : self._constants.TGAP,
            lgap : self._constants.LGAP,
            element : self.element,
            items : [{
                type : 'bi.htape',
                rgap : self._constants.RGAP,
                height : self._constants.HEIGHT,
                items :[{el : {
                    type: 'bi.label',
                    cls : 'label-name',
                    text: BI.i18nText('BI-Date_Diff'),
                    height : self._constants.HEIGHT,
                    textAlign: 'left'
                    },
                    width : self._constants.LABEL_WIDTH
                },{
                    el : self.lcombo,
                    width : self._constants.COMBO_WIDTH
                },{el : {
                    type : 'bi.label',
                    cls : 'label-name',
                    text : "-",
                    height : self._constants.HEIGHT,
                    textAlign : 'center'
                    },
                    width : self._constants.SMALL_LABEL_WIDTH
                },{
                    el : self.rcombo,
                    width : self._constants.COMBO_WIDTH
                }]
            },{
                type : 'bi.htape',
                rgap : self._constants.RGAP,
                height : self._constants.HEIGHT,
                items : [{el : {
                    type : 'bi.label',
                    cls : 'label-name',
                    text : BI.i18nText('BI-Unit_Normal'),
                    height : self._constants.HEIGHT,
                    textAlign : 'left'
                    },
                    width : self._constants.LABEL_WIDTH
                },{
                    el : self.segment,
                    width : self._constants.SEGMENT_WIDTH
                }]
            }]
        })
    },

    _initController : function() {
        return BI.AnalysisETLOperatorAddColumnDateDiffController;
    }
})
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BI.ANALYSIS_ETL_ADD_COLUMN_TYPE.DATE_DIFF, BI.AnalysisETLOperatorAddColumnDateDiffPane);