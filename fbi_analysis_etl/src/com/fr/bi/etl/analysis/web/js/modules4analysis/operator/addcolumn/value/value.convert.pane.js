/**
 * Created by 小灰灰 on 2016/3/31.
 */
BI.AnalysisETLOperatorAddColumnValueConvertPane = BI.inherit(BI.Widget, {
    _constants: {
        HEIGHT: 30,
        LABEL_WIDTH: 70,
        WIDTH : 300,
        GAP : 10
    },

    render: function(){
        var self = this;
        this.model = new BI.AnalysisETLOperatorAddColumnValueConvertModel({});
        this.combo = this.segment = null;
        return {
            type : 'bi.vertical',
            tgap : self._constants.GAP,
            lgap : self._constants.GAP,
            width : self._constants.WIDTH,
            items :[
                {
                    type : 'bi.htape',
                    items : [
                        {
                            type : 'bi.label',
                            cls : 'label-name',
                            text : BI.i18nText('BI-Basic_Field') + BI.i18nText('BI-Basic_Choose'),
                            width : self._constants.LABEL_WIDTH,
                            textAlign : 'left'
                        },
                        {
                            el : {
                                type: "bi.text_value_combo",
                                height : self._constants.HEIGHT,
                                items : [],
                                listeners: [{
                                    eventName: BI.TextValueCombo.EVENT_CHANGE,
                                    action: function(v){
                                        self.setField(v);
                                    }
                                }],
                                ref: function(_ref){
                                    self.combo = _ref;
                                }
                            }
                        }
                    ],
                    height : self._constants.HEIGHT
                },
                {
                    type : 'bi.htape',
                    items : [
                        {
                            type : 'bi.label',
                            cls : 'label-name',
                            width : self._constants.LABEL_WIDTH,
                            text : BI.i18nText('BI-Basic_Type') + BI.i18nText('BI-Basic_Choose'),
                            textAlign : 'left'
                        },
                        {
                            el : {
                                type: "bi.segment",
                                items: [
                                    {
                                        type: "bi.text_button",
                                        text: BI.i18nText("BI-Numeric_Type"),
                                        cls: "bi-segment-button",
                                        value: BICst.COLUMN.NUMBER
                                    }, {
                                        type: "bi.text_button",
                                        text: BI.i18nText("BI-Text_Type"),
                                        cls: "bi-segment-button",
                                        value: BICst.COLUMN.STRING
                                    }, {
                                        type: "bi.text_button",
                                        text: BI.i18nText("BI-Time_Type"),
                                        cls: "bi-segment-button",
                                        value: BICst.COLUMN.DATE
                                    }
                                ],
                                ref: function(_ref){
                                    self.segment = _ref;
                                },
                                listeners: [{
                                    eventName: BI.Segment.EVENT_CHANGE,
                                    action: function(v){
                                        self.setType(v);
                                    }
                                }]
                            }
                        }
                    ],
                    height : self._constants.HEIGHT
                }
            ]
        }
    },

    _checkCanSave : function () {
        if (BI.isNull(this.model.get('field'))){
            this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Basic_Field')));
        } else {
            this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true);
        }
    },

    setField : function (field) {
        this.model.set('field', field);
        var f = this.model.getFieldByValue(field)
        this.segment.setEnabledValue(ETLCst.ANALYSIS_ADD_COLUMN_CONVERT_ACCEPT_FIELDS[f.fieldType])
        var type = ETLCst.ANALYSIS_ADD_COLUMN_CONVERT_ACCEPT_FIELDS[f.fieldType][0];
        this.segment.setValue(type)
        this.setType(type)
    },

    setType : function (type) {
        this.model.set('fieldType', type);
        this._checkCanSave();
    },

    _populate: function(){
        var fields = this.model.createFieldsItems();
        this.combo.populate(fields);
        var field = this.model.get('field') || (fields.length > 0 ? fields[0].value : null);
        this.combo.setValue(field);
        if(BI.isNotNull(field)) {
            var f = this.model.getFieldByValue(field);
            this.model.set('field', field);
            this.segment.setEnabledValue(ETLCst.ANALYSIS_ADD_COLUMN_CONVERT_ACCEPT_FIELDS[f.fieldType]);
            var type = this.model.get('fieldType') || ETLCst.ANALYSIS_ADD_COLUMN_CONVERT_ACCEPT_FIELDS[f.fieldType][0];
            this.segment.setValue(type);
            this.model.set('fieldType', type);
        }
        this._checkCanSave();
    },

    populate : function (m, options) {
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    },

    update: function(){
        return this.model.update()
    }

});
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.VALUE_CONVERT, BI.AnalysisETLOperatorAddColumnValueConvertPane);