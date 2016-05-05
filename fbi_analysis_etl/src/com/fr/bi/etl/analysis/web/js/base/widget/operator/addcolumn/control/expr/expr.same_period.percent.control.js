/**
 * Created by 小灰灰 on 2016/4/6.
 */
BI.AnalysisETLOperatorAddColumnExprSamePeriodPercentController = BI.inherit(BI.AnalysisETLOperatorAddColumnExprSamePeriodController, {
    _populateLabel : function (widget, model) {
        widget.firstDetail.empty();
        widget.firstDetail.addItem(
            BI.createWidget({
                type : 'bi.label',
                cls : 'detail-label',
                textAlign : 'left',
                text : BI.i18nText('BI-Calculate_Target_Last_Include_In_Same', model.get('monthSeason') || '', model.get('field')||'')
            })
        )
        widget.secondDetail.empty();
        this._populateDownLabel(widget.secondDetail, model);
    }
})