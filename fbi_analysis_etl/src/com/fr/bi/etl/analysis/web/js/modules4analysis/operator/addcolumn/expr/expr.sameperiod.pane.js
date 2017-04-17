/**
 * Created by 小灰灰 on 2016/4/5.
 */
BI.AnalysisETLOperatorAddColumnExprSamePeriodPane  = BI.inherit(BI.AnalysisETLOperatorAddColumnPeriodPane, {

    createUniqItems: function(){
        var self = this;
        return [{el : {
            type : 'bi.label',
            cls : 'label-name',
            width : self._constants.LABEL_WIDTH,
            text : BI.i18nText('BI-Year_Fen'),
            textAlign : 'right'
        },
            left : 500,
            top : 50
        },{el : {
            type : 'bi.label',
            cls : 'label-name',
            width : self._constants.LABEL_WIDTH,
            text : BI.i18nText('BI-Month_Fen') + '/'+ BI.i18nText('BI-Basic_Quarter'),
            textAlign : 'right'
        },
            left : 500,
            top : 90
        },{
            el : {
                type: "bi.text_value_combo",
                height : self._constants.HEIGHT,
                width : self._constants.WIDTH,
                items : [],
                ref: function(_ref){
                    self.year = _ref;
                },
                listeners: [{
                    eventName: BI.TextValueCombo.EVENT_CHANGE,
                    action: function(v){
                        self.setYearField(v);
                    }
                }]
            },
            left : 570,
            top : 45
        },{
            el : {
                type: "bi.text_value_combo",
                height : self._constants.HEIGHT,
                width : self._constants.WIDTH,
                items : [],
                ref: function(_ref){
                    self.monthSeason = _ref;
                },
                listeners: [{
                    eventName: BI.TextValueCombo.EVENT_CHANGE,
                    action: function(v){
                        self.setMonthSeasonField(v);
                    }
                }]
            },
            left : 570,
            top : 85
        }]
    },

    //原controller
    _checkCanSave : function () {
        if (BI.isNull(this.model.get('period'))){
            this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted',  BI.i18nText('BI-Year_Fen')));
        } else if (BI.isNull(this.model.get('monthSeason'))){
            this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted',  BI.i18nText('BI-Month_Fen') + '/'+ BI.i18nText('BI-Basic_Quarter')));
        } else {
            BI.AnalysisETLOperatorAddColumnExprSamePeriodPane.superclass._checkCanSave.apply(this, arguments);
        }
    },

    _getSelectedFields : function () {
        var fields = [];
        if (BI.isNotNull(this.model.get('period'))){
            fields.push(this.model.get('period'));
        }
        if (BI.isNotNull(this.model.get('monthSeason'))){
            fields.push(this.model.get('monthSeason'));
        }
        return fields.concat(BI.AnalysisETLOperatorAddColumnExprSamePeriodPane.superclass._getSelectedFields.apply(this, arguments));
    },

    _refreshCombo : function () {
        BI.AnalysisETLOperatorAddColumnExprSamePeriodPane.superclass._refreshCombo.apply(this, arguments);
        this.year.populate(this._createLeftItems());
        this.year.setValue(this.model.get('period'));
        this.monthSeason.populate(this._createLeftItems());
        this.monthSeason.setValue(this.model.get('monthSeason'));
    },

    _refreshComboValue : function (combo, oldValue) {
        BI.AnalysisETLOperatorAddColumnExprSamePeriodPane.superclass._refreshComboValue.apply(this, arguments);
        if (combo !== this.year && combo.getValue()[0] === this.model.get('period')){
            this.year.setValue(oldValue);
            this.model.set('period', oldValue);
        }
        if (combo !== this.monthSeason && (combo.getValue()[0] === this.model.get('monthSeason'))){
            this.monthSeason.setValue(oldValue);
            this.model.set('monthSeason', oldValue);
        }
    },

    setYearField : function (value) {
        var oldValue = this.model.get('period');
        this.model.set('period', value);
        this._refreshComboValue(this.year, oldValue);
        this._afterValueSetted();
    },

    setMonthSeasonField :function (value) {
        var oldValue = this.model.get('monthSeason');
        this.model.set('monthSeason', value);
        this._refreshComboValue(this.monthSeason, oldValue);
        this._afterValueSetted();
    },

    populate : function () {
        BI.AnalysisETLOperatorAddColumnExprSamePeriodPane.superclass.populate.apply(this, arguments);
        this.year.populate(this._createLeftItems());
        this.year.setValue(this.model.get('period'));
        this.monthSeason.populate(this._createLeftItems());
        this.monthSeason.setValue(this.model.get('monthSeason'));
        this._afterValueSetted();
    },

    _populateLabel : function () {
        var text =  BI.i18nText('BI-Calculate_Target_Each_Value_Get', this.model.get('monthSeason') || '', this.model.get('field')||'');
        this.labels.populate([{
            type : 'bi.label',
            cls : 'detail-label',
            textAlign : 'left',
            height : 25,
            text : text,
            title : text
        }]);
        this._populateDownLabel();
    },

    _populateDownLabel : function () {
        var self = this;
        var group = this.model.get('group') || [];
        var items = [];
        BI.each(group, function (i, item) {
            var text = BI.i18nText('BI-Calculate_Target_Include_In_Same', item);
            items.push({
                type : 'bi.label',
                cls : 'detail-label',
                textAlign : 'left',
                text : text,
                title : text
            });
        })
        var text = BI.i18nText('BI-Calculate_Target_Last_Include_In_Same', this.model.get('period') || '', this.model.get('monthSeason') || '');
        items.push({
            type : 'bi.label',
            cls : 'detail-label',
            textAlign : 'left',
            text : text,
            title : text
        })
        items.push({
            type : 'bi.label',
            cls : 'detail-label',
            textAlign : 'left',
            text : BI.i18nText('BI-Brackets_Value', this.model.get('field')||'')
        });
        this.labels.addItems(items);
    }
});
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.EXPR_CPP, BI.AnalysisETLOperatorAddColumnExprSamePeriodPane);