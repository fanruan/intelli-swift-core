BI.AnalysisETLOperatorAddColumnPaneTitle = FR.extend(BI.MVCWidget, {

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorAddColumnPaneTitle.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-operator-add-column-title",
            height:45,
            model :{

            },
            controller : {
               
            }
        })
    },

    _initModel : function () {
        return BI.AnalysisETLOperatorAddColumnPaneTitleModel;
    },

    _initController : function () {
        return BI.AnalysisETLOperatorAddColumnPaneTitleController;
    },

    _initView: function () {
        var self = this;
        this.nameEditor = BI.createWidget({
            type: "bi.text_editor",
            cls:"add-column-name",
            watermark: BI.i18nText("BI-Input_Column_Name"),
            errorText: BI.i18nText("BI-Cannot_Have_Repeated_Field_Name"),
            validationChecker: function(v){
                return self.controller.checkName(v);
            },
            allowBlank: false,
            width: 200,
            height: 30
        })

        this.nameEditor.on(BI.TextEditor.EVENT_CHANGE, function () {
            self.controller.changeColumnName();
        })
        this.typeCombo = BI.createWidget({
            type: "bi.text_value_down_list_combo",
            cls:"add-column-type",
            items:ETLCst.ANALYSIS_ADD_COLUMN_TYPE,
            width: 200,
            height: 30
        })
        this.typeCombo.on(BI.TextValueDownListCombo.EVENT_CHANGE, function () {
            self.controller.changeColumnType();
            self.fireEvent(BI.AnalysisETLOperatorAddColumnPaneTitle.EVENT_ADD_COLUMN_TYPE_CHANGE, self.typeCombo.getValue())
        })
        this.fieldTypeSegment = BI.createWidget({
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
            width: 240
        })
        this.fieldTypeLabel = BI.createWidget({
            type:"bi.label",
            cls:"title-name",
            text: BI.i18nText("BI-Field_Type")
        })
        this.fieldTypeSegment.on(BI.Segment.EVENT_CHANGE, function () {
            self.controller.changeFieldType()
            self.fireEvent(BI.AnalysisETLOperatorAddColumnPaneTitle.EVENT_COLUMN_TYPE_CHANGE, self.fieldTypeSegment.getValue())
        })
        BI.createWidget({
            type:"bi.center_adapt",
            element:this.element,
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
                    el : this.nameEditor,
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
                    el : this.typeCombo,
                    width:200
                }, {
                    type:"bi.layout",
                    width:30
                },  {
                    el:this.fieldTypeLabel
                }, {
                    type:"bi.layout",
                    width:5
                }, {
                    el : this.fieldTypeSegment,
                    width:240
                }]
            }]
        })
    }

})

BI.AnalysisETLOperatorAddColumnPaneTitle.EVENT_COLUMN_TYPE_CHANGE = "column_type_change";
BI.AnalysisETLOperatorAddColumnPaneTitle.EVENT_ADD_COLUMN_TYPE_CHANGE = "add_column_type_change";
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + ETLCst.ANALYSIS_TABLE_PANE + ETLCst.ANALYSIS_TABLE_TITLE, BI.AnalysisETLOperatorAddColumnPaneTitle);