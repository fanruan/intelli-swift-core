/**
 * Created by 小灰灰 on 2016/5/4.
 */
BI.AnalysisETLOperatorAddColumnExprRankController = BI.inherit(BI.AnalysisETLOperatorAddColumnExprAccController, {

    _isGroupWillShow: function (model) {
        return model.get('rule') ===  BICst.TARGET_TYPE.RANK_IN_GROUP;
    },

    _getLabelLastText : function (model) {
        var rule = BI.find(ETLCst.ANALYSIS_ADD_COLUMN_EXPR_RANK_TYPE_ITEMS, function (i, item) {
            return item.sortType === model.get('sortType') && item.groupType === model.get('rule');
        })
        return BI.isNull(rule) ? '' : BI.i18nText(rule.value, model.get('field'))
    },
    
    setRule : function (value, widget, model) {
        var rule = BI.find(ETLCst.ANALYSIS_ADD_COLUMN_EXPR_RANK_TYPE_ITEMS, function (i, item) {
            return item.value === value;
        })
        model.set('rule', rule.groupType);
        model.set('sortType', rule.sortType);
        this._afterValueSetted(widget, model);
    },
    
    
    populate : function (widget, model) {
        var fields = model.getNumberFields();
        widget.fieldCombo.populate(fields);
        if (BI.isNull(model.get('field')) && BI.isNotEmptyArray(fields)){
            model.set('field', fields[0].value)
        }
        widget.fieldCombo.setValue(model.get('field'));
        widget.rule.populate(ETLCst.ANALYSIS_ADD_COLUMN_EXPR_RANK_TYPE_ITEMS);
        var rule = BI.find(ETLCst.ANALYSIS_ADD_COLUMN_EXPR_RANK_TYPE_ITEMS, function (i, item) {
            return item.sortType === model.get('sortType') && item.groupType === model.get('rule');
        }) || ETLCst.ANALYSIS_ADD_COLUMN_EXPR_RANK_TYPE_ITEMS[0];
        model.set('rule', model.get('rule') || rule.groupType);
        model.set('sortType', model.get('sortType') || rule.sortType);
        if (BI.isNotNull(rule)){
            widget.rule.setValue(rule.value);
        }
        this._afterValueSetted(widget, model);
    }
})