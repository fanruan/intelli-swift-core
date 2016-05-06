/**
 * Created by 小灰灰 on 2016/4/6.
 */
BI.AnalysisETLOperatorAddColumnExprAccController = BI.inherit(BI.MVCController, {
    _checkCanSave : function (widget, model) {
        if (BI.isNull(model.get('field'))){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Value_From')));
        } else  if (BI.isNull(model.get('rule'))){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Value_Principle')));
        } else {
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true);
        }
    },

    _afterValueSetted : function (widget, model) {
        this._checkRule(widget, model);
        this._checkGroup(widget, model);
        this._refreshLabel(widget, model);
        this._checkCanSave(widget, model);
    },

    _checkRule : function (widget, model) {
        widget.rule.setEnable(BI.isNotNull(model.get('field'))) ;
    },

    _isGroupWillShow: function (model) {
        return model.get('rule') === BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP;
    },

    _refreshLabel : function (widget, model) {
        widget.labels.empty();
        var group = model.get('group') || [];
        BI.each(group, function(i, item){
            var text = BI.i18nText('BI-Same') + BI.i18nText('BI-Brackets_Value', item) + (i === group.length - 1 ? BI.i18nText('BI-Relation_In') : '');
            widget.labels.addItem(BI.createWidget({
                type : 'bi.label',
                textAlign : 'left',
                height : 25,
                text : text,
                title : text
            }))
        })
        var text = this._getLabelLastText(model);
        widget.labels.addItem(BI.createWidget({
            type : 'bi.label',
            textAlign : 'left',
            whiteSpace : 'normal',
            text : text,
            title : text
        }))
    },

    _getLabelLastText : function (model) {
        return BI.i18nText(this._isGroupWillShow(model) ? 'BI-Calculate_Target_Sum_Above_Group_Get_Sum' : 'BI-Calculate_Target_Sum_Above_Logic', model.get('field'))
    },

    _checkGroup : function (widget, model) {
        if (this._isGroupWillShow(model)){
            var items = [];
            var group = model.getValue('group')||[];
            var field = model.get('field');
            group.remove(field);
            BI.each(group, function (i, item) {
                items.push({
                    text : item,
                    value : item,
                    selected : true
                });
            })
            BI.each(model.get(ETLCst.FIELDS), function (i, item) {
                if (BI.indexOf(group, item.value) === -1 && item.value !== field){
                    items.push({text : item.text, value : item.value});
                }
            })
            widget.refreshGroup(items);
            widget.groupPane.setVisible(true);
        } else {
            widget.groupPane.setVisible(false)
            model.unset('group');
        }
    },

    setGroup : function (group, widget, model) {
        model.set('group' , group);
        this._refreshLabel(widget, model);
        this._checkCanSave(widget, model);
    },

    setValueField : function (value, widget, model) {
        model.set('field', value);
        this._afterValueSetted(widget, model);
    },
    
    setRule : function (value, widget, model) {
        model.set('rule', value);
        this._afterValueSetted(widget, model);
    },
    populate : function (widget, model) {
        var fields = model.getNumberFields();
        widget.fieldCombo.populate(fields);
        if (BI.isNull(model.get('field')) && BI.isNotEmptyArray(fields)){
            model.set('field', fields[0].value)
        }
        widget.fieldCombo.setValue(model.get('field'));
        widget.rule.populate([{
            text: BI.i18nText("BI-Cumulative_Value"),
            value: BICst.TARGET_TYPE.SUM_OF_ABOVE
        }, {
            text: BI.i18nText("BI-Cumulative_Value_In_Group"),
            value: BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP
        }]);
        model.set('rule', model.get('rule') ||BICst.TARGET_TYPE.SUM_OF_ALL);
        widget.rule.setValue(model.get('rule'));
        this._afterValueSetted(widget, model);
    }
})