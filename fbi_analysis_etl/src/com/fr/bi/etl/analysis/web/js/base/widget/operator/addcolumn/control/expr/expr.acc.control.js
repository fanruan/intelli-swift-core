/**
 * Created by 小灰灰 on 2016/4/6.
 */
BI.AnalysisETLOperatorAddColumnExprAccController = BI.inherit(BI.MVCController, {
    _checkCanSave : function (widget, model) {

    },

    setValueField : function (value, widget, model) {
        model.set('field', value)
    },
    
    setSumType : function (value, widget, model) {
        model.set('sum_type', value);
    },

    setRule : function (value, widget, model) {
        model.set('rule', value);
    },
    
    populate : function (widget, model) {
        widget.fieldCombo.populate(model.getNumberFields());
        widget.fieldCombo.setValue(model.get('field'));
        if (model.get('add_column_type') == BI.ANALYSIS_ETL_ADD_COLUMN_TYPE.EXPR_ACC){
            widget.sumLabel.setVisible(true);
            widget.sumType.setVisible(true);
            widget.sumType.setValue(model.get('sum_type'));
        } else {
            widget.sumLabel.setVisible(false);
            widget.sumType.setVisible(false);
        }
        
    }
})