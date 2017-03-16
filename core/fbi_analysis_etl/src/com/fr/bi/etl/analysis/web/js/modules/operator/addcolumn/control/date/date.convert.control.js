/**
 * Created by 小灰灰 on 2016/3/30.
 */
BI.AnalysisETLOperatorAddColumnDateConvertController = BI.inherit(BI.MVCController, {
    _checkCanSave : function (widget, model) {
        if (BI.isNull(model.get('field'))){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Field')));
        } else {
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true);
        }
    },

    setField : function (field, widget, model) {
        model.set('field', field);
        this._checkCanSave(widget, model);
    },


    populate : function (widget, model) {
        var fields = [];
        BI.each(model.get(ETLCst.FIELDS) || [], function (idx, item) {
            if(item.field_type === BICst.COLUMN.DATE) {
                fields.push({
                    text:item.field_name,
                    value:item.field_name
                })
            }
        })
        model.set(ETLCst.FIELDS, fields)
        widget.combo.populate(model.get(ETLCst.FIELDS));
        widget.combo.setValue(model.get('field') || (fields.length > 0 ? fields[0].value : null));
        this.setField(widget.combo.getValue()[0], widget, model)
    },
    
    update : function (widget, model) {
        var v = model.update();
        delete v[ETLCst.FIELDS];
        return v;
    }
})