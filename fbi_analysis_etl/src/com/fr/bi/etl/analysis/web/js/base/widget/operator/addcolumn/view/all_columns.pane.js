BI.AnalysisETLOperatorAllColumnsPane = FR.extend(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorAllColumnsPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls:"bi-analysis-etl-operator-add-column-all-pane",
            items:[]
        })
    },

    _init: function () {
        BI.AnalysisETLOperatorAllColumnsPane.superclass._init.apply(this, arguments);
        var self = this;
        this.columnGroups = BI.createWidget({
            type:"bi.button_group",
            chooseType:BI.Selection.None,
            cls:"columns",
            items:[],
            layouts:[{
                type: "bi.vertical",
                hgap: 5,
                vgap: 5
            }]
        })
        BI.createWidget({
            type:"bi.border",
            element:this.element,
            items:{
                north: {
                    type:"bi.layout",
                    height:10
                },
                east: {
                    type:"bi.layout",
                    width:10
                },
                west: {
                    type:"bi.layout",
                    width:10
                },
                center : {
                    type:"bi.htape",
                    items:[{
                        el:this.columnGroups,
                        width:230
                    }, {
                        type:"bi.layout",
                        width:10
                    }, {
                        el :{
                            type:"bi.vtape",
                            items:[{
                                el:{
                                    type:"bi.center_adapt",
                                    items:[{
                                        type:"bi.label",
                                        cls:"title",
                                        textAlign:"left",
                                        text: BI.i18nText("BI-ETL_Create_More_Columns")
                                    }]
                                },
                                height:30
                            }, {
                                type:"bi.button",
                                text:BI.i18nText("BI-ETL_Continue_Add"),
                                cls:"bi-shake2",
                                width:230,
                                height:30,
                                handler : function () {
                                    self.fireEvent(BI.AnalysisETLOperatorAllColumnsPane.EVENT_NEW)
                                }
                            }]
                        }
                    }]
                }
            }
        })
        this.populate(this.options.items)
    },
    
    
    populate : function (items) {
        this.columnGroups.empty()
        var self = this;
        BI.each(items, function (idx, item) {
            var button  = BI.createWidget(BI.extend({
                type:"bi.etl_button_column"
            } ,item));
            button.on(BI.ColumnButton.EVENT_EDIT, function () {
                self.fireEvent(BI.AnalysisETLOperatorAllColumnsPane.EVENT_EDIT, arguments)
            })
            button.on(BI.ColumnButton.EVENT_DELETE, function () {
                self.fireEvent(BI.AnalysisETLOperatorAllColumnsPane.EVENT_DELETE, arguments)
            })
            self.columnGroups.addItems([button]);
        })
    }

})
BI.AnalysisETLOperatorAllColumnsPane.EVENT_DELETE="event_delete";
BI.AnalysisETLOperatorAllColumnsPane.EVENT_EDIT="event_edit";
BI.AnalysisETLOperatorAllColumnsPane.EVENT_NEW="event_new";
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + ETLCst.ANALYSIS_TABLE_PANE + "_all", BI.AnalysisETLOperatorAllColumnsPane);