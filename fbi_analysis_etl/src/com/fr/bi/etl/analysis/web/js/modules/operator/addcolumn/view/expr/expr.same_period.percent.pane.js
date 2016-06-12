/**
 * Created by 小灰灰 on 2016/4/6.
 */
BI.AnalysisETLOperatorAddColumnExprSamePeriodPercentPane  = BI.inherit(BI.AnalysisETLOperatorAddColumnExprSamePeriodPane, {
    _initController : function() {
        return BI.AnalysisETLOperatorAddColumnExprSamePeriodPercentController;
    },
    _createDetail: function () {
        var self = this;
        self.firstDetail = BI.createWidget({
            type : 'bi.vertical',
            cls : 'detail-view',
            lgap : self._constants.GAP,
            height : self._constants.FIRST_DETAIL_HEIGHT
        });
        self.secondDetail = BI.createWidget({
            type : 'bi.vertical',
            cls : 'detail-view',
            lgap : self._constants.GAP,
            tgap : self._constants.GAP,
            height : self._constants.SECOND_DETAIL_HEIGHT
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
                        height : self._constants.LABEL_HEIGHT
                    })
                },
                {
                    el : self.firstDetail
                },
                {
                    el : BI.createWidget({
                        type : 'bi.label',
                        cls : 'label-name',
                        text : BI.i18nText('BI-Divide_By'),
                        textAlign : 'center',
                        height : self._constants.LABEL_HEIGHT
                    })
                },
                {
                    el : self.secondDetail
                }
            ]
        });
    }

});
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.EXPR_CPP_PERCENT, BI.AnalysisETLOperatorAddColumnExprSamePeriodPercentPane);