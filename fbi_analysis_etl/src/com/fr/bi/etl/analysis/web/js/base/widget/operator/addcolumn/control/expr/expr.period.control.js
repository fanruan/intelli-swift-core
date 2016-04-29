/**
 * Created by 小灰灰 on 2016/4/5.
 */
BI.AnalysisETLOperatorAddColumnExprPeriodController = BI.inherit(BI.MVCController, {
    _checkCanSave : function (widget, model) {

    },

    setValueField : function (value, widget, model) {
        model.set('field', value);
    },

    populate : function (widget, model) {
        widget.combo.populate(model.getNumberFields());
        widget.combo.setValue(model.get('field'));
        widget.list.populate([{text : 'aa'},{text : 'bb'},{text : 'acccafdasa'}])
    }
})