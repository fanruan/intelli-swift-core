/**
 * Created by 小灰灰 on 2016/4/6.
 */
BI.AnalysisETLOperatorAddColumnExprLastPeriodPercentController = BI.inherit(BI.AnalysisETLOperatorAddColumnExprLastPeriodController, {
    _populateLabel : function (widget, model) {
        widget.firstDetail.empty();
        widget.firstDetail.addItem(
            BI.createWidget({
                type : 'bi.label',
                cls : 'detail-label',
                textAlign : 'left',
                text : 'aaaaaa'
            })
        )
        widget.secondDetail.empty();
        BI.each(['a', 'b', 'c'], function (i, item) {
            widget.secondDetail.addItem(
                BI.createWidget({
                    type : 'bi.label',
                    cls : 'detail-label',
                    textAlign : 'left',
                    text : item
                })
            )
        })
    }
})