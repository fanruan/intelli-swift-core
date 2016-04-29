/**
 * Created by 小灰灰 on 2016/4/5.
 */
BI.AnalysisETLOperatorAddColumnExprSamePeriodPane  = BI.inherit(BI.AnalysisETLOperatorAddColumnPeriodPane, {
    _initController : function() {
        return BI.AnalysisETLOperatorAddColumnExprSamePeriodController;
    },
    
    _initView : function () {
        BI.AnalysisETLOperatorAddColumnExprSamePeriodPane.superclass._initView.apply(this, arguments);
        var self = this;
        self.year = BI.createWidget({
            type: "bi.text_icon_combo",
            height : self._constants.HEIGHT,
            width : self._constants.WIDTH,
            items : []
        });
        self.year.on(BI.TextIconCombo.EVENT_CHANGE, function(v){
            self.controller.setValueField(v);
        })
        self.monthSeason = BI.createWidget({
            type: "bi.text_icon_combo",
            height : self._constants.HEIGHT,
            width : self._constants.WIDTH,
            items : []
        });
        self.monthSeason.on(BI.TextIconCombo.EVENT_CHANGE, function(v){
            self.controller.setValueField(v);
        })
        self.layout.addItems([{el : {
            type : 'bi.label',
            cls : 'label-name',
            width : self._constants.LABEL_WIDTH,
            text : BI.i18nText('BI-Year_Fen'),
            textAlign : 'right'
        },
            left : 500,
            top : 50
        },{el : {
            type : 'bi.label',
            cls : 'label-name',
            width : self._constants.LABEL_WIDTH,
            text : BI.i18nText('BI-Value_From'),
            textAlign : 'right'
        },
            left : 500,
            top : 90
        },{
            el : self.year,
            left : 570,
            top : 45
        },{
            el : self.monthSeason,
            left : 570,
            top : 85
        }])
    }
});
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BI.ANALYSIS_ETL_ADD_COLUMN_TYPE.EXPR_CPP, BI.AnalysisETLOperatorAddColumnExprSamePeriodPane);