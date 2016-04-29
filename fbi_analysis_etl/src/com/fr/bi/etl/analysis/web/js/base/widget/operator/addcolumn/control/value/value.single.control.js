/**
 * Created by 小灰灰 on 2016/3/31.
 */
BI.AnalysisETLOperatorAddColumnValueSingleController = BI.inherit(BI.MVCController, {
    _checkCanSave : function (widget, model) {
        if (BI.isNull(model.get('v'))){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Value_Cannot_Be_Null'));
        } else {
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS,  this.valid);
        }
    },

    setEditorValid : function (v) {
        this.valid =  v;
    },

    checkValid : function (widget, model) {
        this._checkCanSave(widget, model)
    },

    setValue : function (value, widget, model) {
        model.set('v', value);
        this._checkCanSave(widget, model);
    },

    populate : function (widget, model) {
        widget.createEditor(this.options.field_type, model.get('v'))
    },

    changeFieldType : function (field_type, widget, model) {
        this.options.field_type = field_type;
        this.populate(widget, model)
    }
})