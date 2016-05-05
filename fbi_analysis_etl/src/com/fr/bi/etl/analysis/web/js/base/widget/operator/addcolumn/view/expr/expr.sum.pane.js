/**
 * Created by 小灰灰 on 2016/5/4.
 */
BI.AnalysisETLOperatorAddColumnSumPane  = BI.inherit(BI.AnalysisETLOperatorAddColumnAccPane, {

    _createComboPane : function () {
        var self = this;
        var comboPane = BI.AnalysisETLOperatorAddColumnSumPane.superclass._createComboPane.apply(this, arguments);
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
        comboPane.addItems([
            {el : self.sumLabel,
            left : 0,
            top : 85
        },{el : self.sumType,
            left : 70,
            top : 80
        }])
        return comboPane;
    },

    _initController : function() {
        return BI.AnalysisETLOperatorAddColumnExprSumController;
    }

});
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BI.ANALYSIS_ETL_ADD_COLUMN_TYPE.EXPR_SUM, BI.AnalysisETLOperatorAddColumnSumPane);