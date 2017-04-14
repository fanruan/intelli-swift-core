/**
 * Created by 小灰灰 on 2016/5/4.
 */
BI.AnalysisETLOperatorAddColumnRankPane  = BI.inherit(BI.AnalysisETLOperatorAddColumnAccPane, {

    _isGroupWillShow: function () {
        return this.model.get('rule') ===  BICst.TARGET_TYPE.RANK_IN_GROUP;
    },

    _getLabelLastText : function () {
        var self = this;
        var rule = BI.find(ETLCst.ANALYSIS_ADD_COLUMN_EXPR_RANK_TYPE_ITEMS, function (i, item) {
            return item.sortType === self.model.get('sortType') && item.groupType === self.model.get('rule');
        });
        return BI.isNull(rule) ? '' : BI.i18nText(BI.isNotEmptyArray(this.model.get('group')) ? rule.value : rule.noneGroupText, this.model.get('field'))
    },

    setRule : function (value) {
        var rule = BI.find(ETLCst.ANALYSIS_ADD_COLUMN_EXPR_RANK_TYPE_ITEMS, function (i, item) {
            return item.value === value;
        });
        this.model.set('rule', rule.groupType);
        this.model.set('sortType', rule.sortType);
        this._afterValueSetted();
    },

    _populate: function(){
        var self = this;
        var fields = this.model.getNumberFields();
        this.fieldCombo.populate(fields);
        if (BI.isNull(this.model.get('field')) && BI.isNotEmptyArray(fields)){
            this.model.set('field', fields[0].value)
        }
        this.fieldCombo.setValue(this.model.get('field'));
        this.rule.populate(ETLCst.ANALYSIS_ADD_COLUMN_EXPR_RANK_TYPE_ITEMS);
        var rule = BI.find(ETLCst.ANALYSIS_ADD_COLUMN_EXPR_RANK_TYPE_ITEMS, function (i, item) {
                return item.sortType === self.model.get('sortType') && item.groupType === self.model.get('rule');
            }) || ETLCst.ANALYSIS_ADD_COLUMN_EXPR_RANK_TYPE_ITEMS[0];
        this.model.set('rule', this.model.get('rule') || rule.groupType);
        this.model.set('sortType', this.model.get('sortType') || rule.sortType);
        if (BI.isNotNull(rule)){
            this.rule.setValue(rule.value);
        }
        this._afterValueSetted();
    },

    populate : function (m, options) {
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    }
});
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.EXPR_RANK, BI.AnalysisETLOperatorAddColumnRankPane);