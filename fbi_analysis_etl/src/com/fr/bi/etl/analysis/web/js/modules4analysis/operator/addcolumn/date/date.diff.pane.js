/**
 * Created by windy on 2017/4/10.
 */
BI.AnalysisETLOperatorAddColumnDateDiffPane = BI.inherit(BI.Widget, {
    _constants : {
        TGAP : 10,
        LGAP : 10,
        RGAP : 5,
        HEIGHT : 30,
        LABEL_WIDTH : 45,
        SMALL_LABEL_WIDTH : 30,
        SEGMENT_WIDTH : 480,
        COMBO_WIDTH : 225
    },

    render: function(){
        var self = this;
        this.segment = this.lcombo = this.rcombo = null;
        this.model = new BI.AnalysisETLOperatorDatePaneModel({});
        return {
            type : 'bi.vertical',
            tgap : self._constants.TGAP,
            lgap : self._constants.LGAP,
            items : [{
                type : 'bi.htape',
                rgap : self._constants.RGAP,
                height : self._constants.HEIGHT,
                items :[{el : {
                    type: 'bi.label',
                    cls : 'label-name',
                    text: BI.i18nText('BI-Date_Diff'),
                    height : self._constants.HEIGHT,
                    textAlign: 'left'
                },
                    width : self._constants.LABEL_WIDTH
                },{
                    el : {
                        type: "bi.text_value_combo",
                        height : self._constants.HEIGHT,
                        items : [],
                        ref: function(_ref){
                            self.lcombo = _ref;
                        },
                        listeners: [{
                            eventName: BI.TextValueCombo.EVENT_CHANGE,
                            action: function(v){
                                self.setFirstField(v);
                            }
                        }]
                    },
                    width : self._constants.COMBO_WIDTH
                },{el : {
                    type : 'bi.label',
                    cls : 'label-name',
                    text : BI.i18nText("BI-Basic_Minus"),
                    height : self._constants.HEIGHT,
                    textAlign : 'center'
                },
                    width : self._constants.SMALL_LABEL_WIDTH
                },{
                    el : {
                        type: "bi.text_value_combo",
                        height:self._constants.HEIGHT,
                        items :[],
                        ref: function(_ref){
                            self.rcombo = _ref;
                        },
                        listeners: [{
                            eventName: BI.TextValueCombo.EVENT_CHANGE,
                            action: function(v){
                                self.setSecondField(v);
                            }
                        }]
                    },
                    width : self._constants.COMBO_WIDTH
                }]
            },{
                type : 'bi.htape',
                rgap : self._constants.RGAP,
                height : self._constants.HEIGHT,
                items : [{el : {
                    type : 'bi.label',
                    cls : 'label-name',
                    text : BI.i18nText('BI-Unit_Normal'),
                    height : self._constants.HEIGHT,
                    textAlign : 'left'
                },
                    width : self._constants.LABEL_WIDTH
                },{
                    el : {
                        type : 'bi.segment',
                        items : ETLCst.ANALYSIS_ADD_COLUMN_DATE_DIFF_TYPE_ITEMS,
                        listeners: [{
                            eventName: BI.Segment.EVENT_CHANGE,
                            action: function(){
                                self.setType(self.segment.getValue()[0]);
                            }
                        }],
                        ref: function(_ref){
                            self.segment = _ref;
                        }
                    },
                    width : self._constants.SEGMENT_WIDTH
                }]
            }]
        }
    },

    //原controller
    _checkCanSave : function () {
        if (BI.isNull(this.model.get('type'))){
            this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Unit')));
        } else if (BI.isNull(this.model.get('firstField'))){
            this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Date_Diff')));
        } else if (BI.isNull(this.model.get('secondField'))){
            this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Date_Diff')));
        } else if (BI.isEqual(this.model.get('secondField'), this.model.get('firstField'))) {
            this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Date_Diff')));
        }else {
            this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true);
        }
    },

    setFirstField : function (field) {
        this.model.set('firstField', field);
        this._checkCanSave();
    },

    setSecondField : function (field) {
        this.model.set('secondField', field);
        this._checkCanSave();
    },

    setType : function (type) {
        this.model.set('type', type);
        this._checkCanSave();
    },

    _populate: function(){
        var fields = [];
        fields.push({
            text: BI.i18nText("BI-System_Time"),
            value:ETLCst.SYSTEM_TIME
        })
        BI.each(this.model.get(ETLCst.FIELDS) || [], function (idx, item) {
            if(item.fieldType === BICst.COLUMN.DATE) {
                fields.push({
                    text:item.fieldName,
                    value:item.fieldName
                })
            }
        })
        this.model.set(ETLCst.FIELDS, fields)
        this.segment.setValue(this.model.get('type') || ETLCst.ANALYSIS_ADD_COLUMN_DATE_DIFF_TYPE.YEAR);
        this.model.set("type", this.segment.getValue()[0])
        this.lcombo.populate(this.model.get(ETLCst.FIELDS));
        this.rcombo.populate(this.model.get(ETLCst.FIELDS));
        this.lcombo.setValue(this.model.get('firstField') || (fields.length > 0 ? fields[0].value : null));
        this.rcombo.setValue(this.model.get('secondField') || (fields.length > 1 ? fields[1].value : null));
        this.model.set("firstField", this.lcombo.getValue()[0]);
        this.model.set("secondField", this.rcombo.getValue()[0])
        this._checkCanSave();
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
})
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.DATE_DIFF, BI.AnalysisETLOperatorAddColumnDateDiffPane);