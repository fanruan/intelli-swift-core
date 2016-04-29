/**
 * Created by 小灰灰 on 2016/4/5.
 */
BI.AnalysisETLOperatorAddColumnExprLastPeriodPane  = BI.inherit(BI.AnalysisETLOperatorAddColumnPeriodPane, {
    _initController : function() {
        return BI.AnalysisETLOperatorAddColumnExprLastPeriodController;
    },
    
    _initView : function () {
        BI.AnalysisETLOperatorAddColumnExprLastPeriodPane.superclass._initView.apply(this, arguments);
        var self = this;
        self.yearMonthSeason = BI.createWidget({
            type: "bi.text_icon_combo",
            height : self._constants.HEIGHT,
            width : self._constants.WIDTH,
            items : []
        });
        self.yearMonthSeason.on(BI.TextIconCombo.EVENT_CHANGE, function(v){
            self.controller.setValueField(v);
        })
        self.layout.addItems([{el : {
            type : 'bi.label',
            cls : 'label-name',
            width : self._constants.LABEL_WIDTH,
            text : BI.i18nText('BI-Year_Fen') + '/'+ BI.i18nText('BI-Month_Fen') + '/'+ BI.i18nText('BI-Quarter'),
            textAlign : 'right'
        },
            left : 500,
            top : 50
        },{
            el : self.yearMonthSeason,
            left : 570,
            top : 45
        }])
    }
});
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BI.ANALYSIS_ETL_ADD_COLUMN_TYPE.EXPR_LP, BI.AnalysisETLOperatorAddColumnExprLastPeriodPane);