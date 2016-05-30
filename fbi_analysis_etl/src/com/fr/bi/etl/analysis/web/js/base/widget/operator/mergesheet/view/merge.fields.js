BI.AnalysisETLMergeSheetFields = BI.inherit(BI.MVCWidget, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLMergeSheetFields.superclass._defaultConfig.apply(this, arguments), {
            extraCls:"bi-analysis-etl-merge-fields",
            model : {
            },
            controller: {
            }
        })
    },

    _initModel : function () {
        return BI.AnalysisETLMergeSheetFieldsModel;
    },


    _initController :function () {
        return BI.AnalysisETLMergeSheetFieldsController;
    },


    _initView : function () {
        var self = this;
        var addMergeButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Add_The_Merged_Basis"),
            height: 30
        });
        addMergeButton.on(BI.Button.EVENT_CHANGE, function(){
            self.controller.addEmptyMerge();
            self.fireEvent(BI.AnalysisETLMergeSheetFields.MERGE_CHANGE, false)
        });
        this.joinTableFields = BI.createWidget({
            type: "bi.table_add_union",
            tables: []
        });
        this.joinTableFields.on(BI.TableAddUnion.EVENT_REMOVE_UNION, function(index){
            self.controller.removeMergeField(index);
            self.fireEvent(BI.AnalysisETLMergeSheetFields.MERGE_CHANGE, self.controller.isValid())
        });
        this.joinTableFields.on(BI.TableAddUnion.EVENT_CHANGE, function(row, col, nValue, oValue){
            self.controller.changeMergeField(row, col, nValue);
            self.fireEvent(BI.AnalysisETLMergeSheetFields.MERGE_CHANGE, self.controller.isValid())
        });
        BI.createWidget({
            type:"bi.vtape",
            cls: "join-merge-field",
            element:this.element,
            items:[{
                el: {
                    type: "bi.left_right_vertical_adapt",
                    cls: "add-merge-field",
                    height: 40,
                    llgap:10,
                    rrgap:10,
                    items: {
                        left: [{
                            type: "bi.label",
                            cls: "title",
                            text: BI.i18nText("BI-Merge_By_Following_Fields")
                        }],
                        right: [addMergeButton]
                    }
                },
                height: 40
            }, {
                el: {
                    type:"bi.vertical",
                    items:[this.joinTableFields],
                    rgap:10,
                    lgap:10,
                    tgap:20
                }
            }]
        })
    }

})

BI.AnalysisETLMergeSheetFields.MERGE_CHANGE = "MERGE_CHANGE";
$.shortcut("bi.analysis_etl_merge_fields",BI.AnalysisETLMergeSheetFields)
