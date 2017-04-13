/**
 * Created by windy on 2017/4/10.
 */
BI.AnalysisETLOperatorAllColumnsPane = BI.inherit(BI.Widget, {

    props: {
        baseCls:"bi-analysis-etl-operator-add-column-all-pane",
        items:[]
    },

    render: function(){
        var self = this;
        this.columnGroups = null;
        return {
            type:"bi.border",
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
                        el: {
                            type:"bi.button_group",
                            chooseType:BI.Selection.None,
                            cls:"columns",
                            items:[],
                            layouts:[{
                                type: "bi.vertical",
                                hgap: 5,
                                vgap: 5
                            }],
                            ref: function(_ref){
                                self.columnGroups = _ref;
                            }
                        },
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
        }
    },

    mounted: function(){
        this.populate(this.options.items);
    },

    populate : function (items) {
        var self = this;
        var buttons = BI.map(items, function (idx, item) {
            var button  = BI.createWidget(BI.extend({
                type:"bi.etl_button_column"
            } ,item));
            button.on(BI.ColumnButton.EVENT_EDIT, function () {
                self.fireEvent(BI.AnalysisETLOperatorAllColumnsPane.EVENT_EDIT, arguments)
            })
            button.on(BI.ColumnButton.EVENT_DELETE, function () {
                self.fireEvent(BI.AnalysisETLOperatorAllColumnsPane.EVENT_DELETE, arguments)
            })
            return button;
        });
        self.columnGroups.populate(buttons);
    },

    scrollToEnd : function () {
        this.columnGroups.element.scrollTop(BI.MAX)
    }
})
BI.AnalysisETLOperatorAllColumnsPane.EVENT_DELETE="event_delete";
BI.AnalysisETLOperatorAllColumnsPane.EVENT_EDIT="event_edit";
BI.AnalysisETLOperatorAllColumnsPane.EVENT_NEW="event_new";
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + ETLCst.ANALYSIS_TABLE_PANE + "_all", BI.AnalysisETLOperatorAllColumnsPane);