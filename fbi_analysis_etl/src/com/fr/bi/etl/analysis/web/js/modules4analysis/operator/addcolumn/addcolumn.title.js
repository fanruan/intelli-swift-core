/**
 * Created by windy on 2017/4/10.
 */
BI.AnalysisETLOperatorAddColumnPaneTitle = BI.inherit(BI.Widget, {

    props: {
        extraCls: "bi-analysis-etl-operator-add-column-title",
        height:45
    },

    render: function(){
        var self = this;
        this.columnNames = this.options.columnNames;
        this.model = new BI.AnalysisETLOperatorAddColumnPaneTitleModel({});
        this.nameEditor = null;
        return {
            type:"bi.center_adapt",
            height:45,
            items:[{
                type:"bi.vertical_adapt",
                height:45,
                items:[{
                    type:"bi.layout",
                    width:10
                }, {
                    type:"bi.center_adapt",
                    items:[{
                        type:"bi.label",
                        cls:"title-name",
                        text:BI.i18nText("BI-Column_Name")
                    }]
                },{
                    type:"bi.layout",
                    width:5
                }, {
                    el : {
                        type: "bi.text_editor",
                        cls:"add-column-name",
                        watermark: BI.i18nText("BI-Input_Column_Name"),
                        errorText: BI.i18nText("BI-Cannot_Have_Repeated_Field_Name"),
                        validationChecker: function(v){
                            return self.checkName(v);
                        },
                        allowBlank: false,
                        width: 200,
                        height: 30,
                        ref: function(_ref){
                            self.nameEditor = _ref;
                        },
                        listeners: [{
                            eventName: BI.TextEditor.EVENT_CHANGE,
                            action: function(){
                                self.changeColumnName();
                            }
                        }]
                    },
                    width:200
                }, {
                    type:"bi.layout",
                    width:10
                }, {
                    el:{
                        type:"bi.label",
                        cls:"title-name",
                        text:BI.i18nText("BI-New_Column_Type")
                    }
                },{
                    el : {
                        type: "bi.text_value_down_list_combo",
                        cls:"add-column-type",
                        items:ETLCst.ANALYSIS_ADD_COLUMN_TYPE,
                        width: 200,
                        height: 30,
                        ref: function(_ref){
                            self.typeCombo = _ref;
                        },
                        listeners: [{
                            eventName: BI.TextValueDownListCombo.EVENT_CHANGE,
                            action: function(){
                                self.changeColumnType();
                                self.fireEvent(BI.AnalysisETLOperatorAddColumnPaneTitle.EVENT_ADD_COLUMN_TYPE_CHANGE, self.typeCombo.getValue())
                            }
                        }]
                    },
                    width:200
                }, {
                    type:"bi.layout",
                    width:30
                },  {
                    el: {
                        type:"bi.label",
                        cls:"title-name",
                        text: BI.i18nText("BI-Field_Type"),
                        ref: function(_ref){
                            self.fieldTypeLabel = _ref;
                        }
                    }
                }, {
                    type:"bi.layout",
                    width:5
                }, {
                    el : {
                        type: "bi.segment",
                        items: [
                            {
                                type: "bi.icon_button",
                                cls: "select-data-field-number-font bi-segment-button",
                                value: BICst.COLUMN.NUMBER
                            }, {
                                type: "bi.icon_button",
                                cls: "select-data-field-string-font bi-segment-button",
                                value: BICst.COLUMN.STRING,
                                selected: true
                            }, {
                                type: "bi.icon_button",
                                cls: "select-data-field-date-font bi-segment-button",
                                value:  BICst.COLUMN.DATE
                            }
                        ],
                        width: 240,
                        ref: function(_ref){
                            self.fieldTypeSegment = _ref;
                        },
                        listeners: [{
                            eventName: BI.Segment.EVENT_CHANGE,
                            action: function(){
                                self.changeFieldType()
                                self.fireEvent(BI.AnalysisETLOperatorAddColumnPaneTitle.EVENT_COLUMN_TYPE_CHANGE, self.fieldTypeSegment.getValue())
                            }
                        }]
                    },
                    width:240
                }]
            }]
        }
    },

    //原controller
    _getDefaultColumnName : function () {
        return BI.Utils.createDistinctName(this.columnNames, BI.i18nText("BI-New_Column_Name"))
    },

    checkName : function (name) {
        return BI.indexOf(this.columnNames, name) < 0
    },


    changeColumnType : function () {
        var type = this.typeCombo.getValue()[0];
        var acceptTypes = ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[type];
        this.fieldTypeLabel.setVisible(type !== BICst.ETL_ADD_COLUMN_TYPE.VALUE_CONVERT)
        this.fieldTypeSegment.setVisible(type !== BICst.ETL_ADD_COLUMN_TYPE.VALUE_CONVERT)
        this.fieldTypeSegment.setEnabledValue(acceptTypes);
        this.fieldTypeSegment.setValue(acceptTypes[0]);
        this.model.setAddColumnType(type);
        this.model.setFieldType(this.fieldTypeSegment.getValue()[0])
    },

    changeFieldType:function () {
        this.model.setFieldType(this.fieldTypeSegment.getValue()[0])
    },

    changeColumnName : function () {
        this.model.setName(this.nameEditor.getValue())
    },

    _populate: function(){
        var name = this.model.getName();
        if(BI.isNull(name)) {
            name = this._getDefaultColumnName();
            this.model.setName(name);
        }
        this.nameEditor.setValue(name);
        this.typeCombo.setValue(this.model.getAddColumnType());
        this.fireEvent(BI.AnalysisETLOperatorAddColumnPaneTitle.EVENT_ADD_COLUMN_TYPE_CHANGE, this.typeCombo.getValue())
        var acceptTypes = ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[this.model.getAddColumnType()];
        this.fieldTypeSegment.setEnabledValue(acceptTypes);
        this.fieldTypeSegment.setValue(this.model.getFieldType());
        var type = this.model.getAddColumnType();
        this.fieldTypeLabel.setVisible(type !== BICst.ETL_ADD_COLUMN_TYPE.VALUE_CONVERT);
        this.fieldTypeSegment.setVisible(type !== BICst.ETL_ADD_COLUMN_TYPE.VALUE_CONVERT)
    },

    populate : function (m, options) {
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    },

    update: function(){
        return this.model.update();
    }

})

BI.AnalysisETLOperatorAddColumnPaneTitle.EVENT_COLUMN_TYPE_CHANGE = "column_type_change";
BI.AnalysisETLOperatorAddColumnPaneTitle.EVENT_ADD_COLUMN_TYPE_CHANGE = "add_column_type_change";
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + ETLCst.ANALYSIS_TABLE_PANE + ETLCst.ANALYSIS_TABLE_TITLE, BI.AnalysisETLOperatorAddColumnPaneTitle);