/**
 * Created by 小灰灰 on 2016/4/6.
 */
BI.AnalysisETLOperatorAddColumnExprSamePeriodPercentController = BI.inherit(BI.AnalysisETLOperatorAddColumnExprSamePeriodController, {
    _populateLabel : function (widget, model) {
        widget.firstDetail.empty();
        var text = BI.i18nText('BI-Calculate_Target_Last_Include_In_Same', model.get('monthSeason') || '', model.get('field')||'');
        widget.firstDetail.addItem(
            BI.createWidget({
                type : 'bi.label',
                cls : 'detail-label',
                textAlign : 'left',
                text : text,
                title : text
            })
        )
        widget.secondDetail.empty();
        this._populateDownLabel(widget.secondDetail, model);
    }
})