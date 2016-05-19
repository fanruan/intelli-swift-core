BI.AnalysisETLOperatorSelectData = BI.inherit(BI.MVCWidget, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorSelectData.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-operator-select-data"

        })
    },


    _initController: function () {
        return BI.AnalysisETLOperatorSelectDataController;
    },

    _initModel : function () {
        return BI.AnalysisETLOperatorSelectDataModel;
    },

    _initView: function () {
        var self = this;
        var o = this.options;
        this.center = BI.createWidget({
            type:"bi.analysis_etl_operator_center",
            nameValidationController : function () {
                return self.controller.checkNameValid.apply(self.controller, arguments)
            }
        })

        this.center.on(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, function(v){
            self.saveButton.setEnable(false)
            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, arguments)
        })

        this.center.on(BI.TopPointerSavePane.EVENT_SAVE, function(v){
            self.saveButton.setEnable(true);
        })

        this.center.on(BI.TopPointerSavePane.EVENT_CANCEL, function(){
            self.saveButton.setEnable(true)
            self.controller.populate();
            self.fireEvent(BI.TopPointerSavePane.EVENT_CANCEL, arguments)
        })

        this.center.on(BI.AnalysisETLPreviewTable.DELETE_EVENT, function(v){
            self.controller.deleteFieldByIndex(v);
        })
        this.center.on(BI.AnalysisETLOperatorCenter.EVENT_RENAME, function(index, name){
            self.controller.renameField(index, name);
        })

        this.center.on(BI.AnalysisETLPreviewTable.EVENT_SORT_COLUMN, function(oldIndex, newIndex){
            self.controller.sortColumn(oldIndex, newIndex);
        })

        this.center.on(BI.TopPointerSavePane.EVENT_INVALID, function(){
            self.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, arguments)
        })

        this.center.on(BI.TopPointerSavePane.EVENT_FIELD_VALID, function(){
            self.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, arguments)
        })
        this.center.on(BI.Controller.EVENT_CHANGE, function(v){
            self.saveButton.setEnable(!v);
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments)
        });

        this.center.on(BI.AnalysisOperatorTitle.EVENT_SAVE, function(v){
            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_SAVE, arguments)
        })

        this.center.on(BI.AnalysisETLOperatorCenter.DATA_CHANGE, function (model) {
            self.controller.refreshModel(model);
        })

        this.center.on(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, function (previewModel, operatorType) {
            self.controller.refreshPreviewData(previewModel, operatorType)
        })

        this.selectPane = BI.createWidget({
            type: "bi.analysis_etl_select_data_pane",
        })
        this.selectPane.on(BI.SelectDataSearcher.EVENT_CLICK_ITEM, function(v){
            self.controller.addField.apply(self.controller, [v])
        })
        this.registerSimpleWidget(this.selectPane)
        this.saveButton = BI.createWidget({
            type:"bi.button",
            text:BI.i18nText("BI-finish_add"),
            height:30,
            handler : function () {
                self.controller.changeEditState()
            }
        })
        this.cancelButton = BI.createWidget({
            type:"bi.button",
            text:BI.i18nText("BI-Cancel"),
            height:30,
            level:'ignore',
            handler : function () {
                self.controller.cancelAddField();
            }
        })

        BI.createWidget({
            type : "bi.border",
            element : this.element,
            items :{
                west :{
                    el : {

                        type:"bi.vtape",
                        cls : o.extraCls + "-west",
                        items : [{
                            el: {
                                type  : "bi.center_adapt",
                                items:[{
                                    type:"bi.layout",
                                    width:10
                                },this.cancelButton,{
                                    type:"bi.layout",
                                    width:10
                                }, this.saveButton,{
                                    type:"bi.layout",
                                    width:10
                                }],

                            },
                            height:40
                        }, {
                             el: this.selectPane
                        }]
                    },
                    left: 20,
                    right:20,
                    width:240
                },

                south : {
                    el : {
                        type:"bi.layout"
                    },
                    height:10
                },
                east : {
                    el : {
                        type:"bi.layout"
                    },
                    width:20
                },
                center: {
                    el:this.center
                }
            }
        });
    }

})

$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA, BI.AnalysisETLOperatorSelectData);