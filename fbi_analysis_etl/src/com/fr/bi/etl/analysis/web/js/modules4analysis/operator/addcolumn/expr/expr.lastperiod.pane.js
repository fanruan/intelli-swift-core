/**
 * Created by 小灰灰 on 2016/4/5.
 */
BI.AnalysisETLOperatorAddColumnExprLastPeriodPane  = BI.inherit(BI.AnalysisETLOperatorAddColumnPeriodPane, {

    //这边之前调用的layout的addItems方法添加元素，没必要
    //提供统一接口，父类创建时调用接口就好
    createUniqItems: function(){
        var self = this;
        return [{el : {
            type : 'bi.label',
            cls : 'label-name',
            width : self._constants.LABEL_WIDTH,
            text : BI.i18nText('BI-Year_Fen') + '/'+ BI.i18nText('BI-Month_Fen') + '/'+ BI.i18nText('BI-Basic_Quarter'),
            textAlign : 'right'
        },
            left : 500,
            top : 50
        },{
            el : {
                type: "bi.text_value_combo",
                height : self._constants.HEIGHT,
                width : self._constants.WIDTH,
                items : [],
                ref: function(_ref){
                    self.yearMonthSeason = _ref;
                },
                listeners: [{
                    eventName: BI.TextValueCombo.EVENT_CHANGE,
                    action: function(v){
                        self.setDateField(v);
                    }
                }]
            },
            left : 570,
            top : 45
        }]
    },

    //原controller
    _checkCanSave : function () {
        if (BI.isNull(this.model.get('period'))){
            this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted',  BI.i18nText('BI-Year_Fen') + '/'+ BI.i18nText('BI-Month_Fen') + '/'+ BI.i18nText('BI-Basic_Quarter')));
        } else {
            BI.AnalysisETLOperatorAddColumnExprLastPeriodController.superclass._checkCanSave.apply(this, arguments);
        }
    },

    _getSelectedFields : function () {
        var fields = [];
        if (BI.isNotNull(this.model.get('period'))){
            fields.push(this.model.get('period'));
        }
        return fields.concat(BI.AnalysisETLOperatorAddColumnExprLastPeriodController.superclass._getSelectedFields.apply(this, arguments));
    },

    _refreshCombo : function () {
        BI.AnalysisETLOperatorAddColumnExprLastPeriodController.superclass._refreshCombo.apply(this, arguments);
        this.yearMonthSeason.populate(this._createLeftItems());
        this.yearMonthSeason.setValue(this.model.get('period'));
    },

    _refreshComboValue : function (combo, oldValue) {
        BI.AnalysisETLOperatorAddColumnExprLastPeriodController.superclass._refreshComboValue.apply(this, arguments);
        if (combo !== this.yearMonthSeason && combo.getValue()[0] === this.model.get('period')){
            this.yearMonthSeason.setValue(oldValue);
            this.model.set('period', oldValue);
        }
    },

    setDateField : function (field) {
        var oldValue = this.model.get('period');
        this.model.set('period', field);
        this._refreshComboValue(this.yearMonthSeason, oldValue)
        this._afterValueSetted();
    },

    populate : function (m, options) {
        BI.AnalysisETLOperatorAddColumnPeriodPane.superclass.populate.apply(this, arguments);
        this.yearMonthSeason.populate(this._createLeftItems());
        this.yearMonthSeason.setValue(this.model.get('period'));
        this._afterValueSetted();
    },

    _populateLabel : function () {
        var text = BI.i18nText('BI-Calculate_Target_Each_Value_Get', this.model.get('period') || '', this.model.get('field')||'');
        this.labels.populate([{
            type : 'bi.label',
            cls : 'detail-label',
            textAlign : 'left',
            text : text,
            title : text
        }]);
        this._populateDownLabel();
    },

    _populateDownLabel : function () {
        var self = this;
        var group = this.model.get('group') || [];
        var labels = []
        BI.each(group, function (i, item) {
            var text = i === group.length -1 ? BI.i18nText('BI-Calculate_Target_Include_In_Same_Last', item, self.model.get('period') || ''): BI.i18nText('BI-Calculate_Target_Include_In_Same', item);
            labels.push({
                type : 'bi.label',
                cls : 'detail-label',
                textAlign : 'left',
                text : text,
                title : text
            })
        });
        if (BI.isEmptyArray(group)){
            var text = BI.i18nText('BI-Calculate_Target_Last', this.model.get('period') || '')
            labels.push({
                type : 'bi.label',
                cls : 'detail-label',
                textAlign : 'left',
                text : text,
                title : text
            })
        }
        var text = BI.i18nText('BI-Brackets_Value', this.model.get('field')||'');
        labels.push({
            type : 'bi.label',
            cls : 'detail-label',
            textAlign : 'left',
            text : text,
            title : text
        });
        this.labels.addItems(labels);
    }
});
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.EXPR_LP, BI.AnalysisETLOperatorAddColumnExprLastPeriodPane);