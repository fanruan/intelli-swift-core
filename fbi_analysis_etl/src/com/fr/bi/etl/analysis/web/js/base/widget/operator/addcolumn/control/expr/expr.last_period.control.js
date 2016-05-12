/**
 * Created by 小灰灰 on 2016/4/5.
 */
BI.AnalysisETLOperatorAddColumnExprLastPeriodController = BI.inherit(BI.AnalysisETLOperatorAddColumnExprPeriodController, {
    _checkCanSave : function (widget, model) {
        if (BI.isNull(model.get('period'))){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted',  BI.i18nText('BI-Year_Fen') + '/'+ BI.i18nText('BI-Month_Fen') + '/'+ BI.i18nText('BI-Quarter')));
        } else {
            BI.AnalysisETLOperatorAddColumnExprLastPeriodController.superclass._checkCanSave.apply(this, arguments);
        }
    },

    _getSelectedFields : function (model) {
        var fields = [];
        if (BI.isNotNull(model.get('period'))){
            fields.push(model.get('period'));
        }
        return fields.concat(BI.AnalysisETLOperatorAddColumnExprLastPeriodController.superclass._getSelectedFields.apply(this, arguments));
    },

    _refreshCombo : function (widget, model) {
        BI.AnalysisETLOperatorAddColumnExprLastPeriodController.superclass._refreshCombo.apply(this, arguments);
        widget.yearMonthSeason.populate(this._createLeftItems(model));
        widget.yearMonthSeason.setValue(model.get('period'));
    },

    _refreshComboValue : function (combo, oldValue, widget, model) {
        BI.AnalysisETLOperatorAddColumnExprLastPeriodController.superclass._refreshComboValue.apply(this, arguments);
        if (combo !== widget.yearMonthSeason && combo.getValue()[0] === model.get('period')){
            widget.yearMonthSeason.setValue(oldValue);
            model.set('period', oldValue);
        }
    },

    setDateField : function (field, widget, model) {
        var oldValue = model.get('period');
        model.set('period', field);
        this._refreshComboValue(widget.yearMonthSeason, oldValue, widget, model)
        this._afterValueSetted(widget, model);
    },

    populate : function (widget, model) {
        BI.AnalysisETLOperatorAddColumnExprLastPeriodController.superclass.populate.apply(this, arguments);
        widget.yearMonthSeason.populate(this._createLeftItems(model));
        widget.yearMonthSeason.setValue(model.get('period'));
        this._afterValueSetted(widget, model);
    },

    _populateLabel : function (widget, model) {
        widget.labels.empty();
        var text = BI.i18nText('BI-Calculate_Target_Each_Value_Get', model.get('period') || '', model.get('field')||'');
        widget.labels.addItem(
            BI.createWidget({
                type : 'bi.label',
                cls : 'detail-label',
                textAlign : 'left',
                text : text,
                title : text
            })
        )
        this._populateDownLabel(widget.labels, model);
    },

    _populateDownLabel : function (label, model) {
        var group = model.get('group') || [];
        BI.each(group, function (i, item) {
            var text = i === group.length -1 ? BI.i18nText('BI-Calculate_Target_Include_In_Same_Last', item, model.get('period') || ''): BI.i18nText('BI-Calculate_Target_Include_In_Same', item);
            label.addItem(
                BI.createWidget({
                    type : 'bi.label',
                    cls : 'detail-label',
                    textAlign : 'left',
                    text : text,
                    title : text
                })
            )
        })
        var text = BI.i18nText('BI-Brackets_Value', model.get('field')||'');
        label.addItem(
            BI.createWidget({
                type : 'bi.label',
                cls : 'detail-label',
                textAlign : 'left',
                text : text,
                title : text
            })
        )  
    }
})