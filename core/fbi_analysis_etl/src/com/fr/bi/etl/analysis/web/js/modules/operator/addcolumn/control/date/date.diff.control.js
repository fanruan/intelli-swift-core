/**
 * Created by 小灰灰 on 2016/3/24.
 */

BI.AnalysisETLOperatorAddColumnDateDiffController = BI.inherit(BI.MVCController, {


    _checkCanSave : function (widget, model) {
        if (BI.isNull(model.get('type'))){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Unit')));
        } else if (BI.isNull(model.get('firstField'))){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Date_Diff')));
        } else if (BI.isNull(model.get('secondField'))){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Date_Diff')));
        } else if (BI.isEqual(model.get('secondField'), model.get('firstField'))) {
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Date_Diff')));
        }else {
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true);
        }
    },

    setFirstField : function (field, widget, model) {
        model.set('firstField', field);
        this._checkCanSave(widget, model);
    },

    setSecondField : function (field, widget, model) {
        model.set('secondField', field);
        this._checkCanSave(widget, model);
    },

    setType : function (type, widget, model) {
        model.set('type', type);
        this._checkCanSave(widget, model);
    },

    populate : function (widget, model) {
        var fields = [];
        fields.push({
            text: BI.i18nText("BI-System_Time"),
            value:ETLCst.SYSTEM_TIME
        })
        BI.each(model.get(ETLCst.FIELDS) || [], function (idx, item) {
            if(item.field_type === BICst.COLUMN.DATE) {
                fields.push({
                    text:item.field_name,
                    value:item.field_name
                })
            }
        })
        model.set(ETLCst.FIELDS, fields)
        widget.segment.setValue(model.get('type') || ETLCst.ANALYSIS_ADD_COLUMN_DATE_DIFF_TYPE.YEAR);
        model.set("type", widget.segment.getValue()[0])
        widget.lcombo.populate(model.get(ETLCst.FIELDS));
        widget.rcombo.populate(model.get(ETLCst.FIELDS));
        widget.lcombo.setValue(model.get('firstField') || (fields.length > 0 ? fields[0].value : null));
        widget.rcombo.setValue(model.get('secondField') || (fields.length > 1 ? fields[1].value : null));
        model.set("firstField", widget.lcombo.getValue()[0]);
        model.set("secondField", widget.rcombo.getValue()[0])
        this._checkCanSave(widget, model);
    },

    update : function (widget, model) {
        var v = model.update();
        delete v[ETLCst.FIELDS];
        return v;
    }
})