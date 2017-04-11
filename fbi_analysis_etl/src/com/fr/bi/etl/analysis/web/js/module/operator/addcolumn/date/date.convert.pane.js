/**
 * Created by windy on 2017/4/10.
 */
BI.AnalysisETLOperatorAddColumnAbstractDateConvertPane = BI.inherit(BI.Widget, {
    _constants : {
        GAP : 10,
        HEIGHT : 30,
        LABEL_WIDTH : 36,
        COMBO_WIDTH : 200
    },

    render: function(){
        var self = this;
        this.model =
        this.combo = null;
        this.model = new BI.AnalysisETLOperatorDatePaneModel({});
        return {
            type : 'bi.horizontal',
            tgap : self._constants.GAP,
            lgap : self._constants.GAP,
            items :[{
                type: 'bi.label',
                cls : 'label-name',
                text: BI.i18nText('BI-Select_Field'),
                textAlign: 'left'
            }, {
                el : {
                    type: "bi.text_value_combo",
                    height : self._constants.HEIGHT,
                    width : self._constants.COMBO_WIDTH,
                    items : [],
                    ref: function(_ref){
                        self.combo = _ref;
                    },
                    listeners: [{
                        eventName: BI.TextValueCombo.EVENT_CHANGE,
                        action: function(v){
                            self.setField(v);
                        }
                    }]
                }
            },{
                el : self._createLabel()
            }]
        }
    },

    _checkCanSave : function () {
        if (BI.isNull(this.model.get('field'))){
            this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Basic_Field')));
        } else {
            this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true);
        }
    },

    setField : function (field) {
        this.model.set('field', field);
        this._checkCanSave();
    },

    _populate: function(){
        var fields = [];
        BI.each(this.model.get(ETLCst.FIELDS) || [], function (idx, item) {
            if(item.fieldType === BICst.COLUMN.DATE) {
                fields.push({
                    text:item.fieldName,
                    value:item.fieldName
                })
            }
        })
        this.model.set(ETLCst.FIELDS, fields)
        this.combo.populate(this.model.get(ETLCst.FIELDS));
        this.combo.setValue(this.model.get('field') || (fields.length > 0 ? fields[0].value : null));
        this.setField(this.combo.getValue()[0])
    },

    populate : function (m, options) {
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    },

    update : function () {
        var v = this.model.update();
        delete v[ETLCst.FIELDS];
        return v;
    }
});