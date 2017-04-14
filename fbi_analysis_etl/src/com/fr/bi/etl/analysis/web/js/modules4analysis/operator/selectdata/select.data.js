/**
 * Created by windy on 2017/4/6.
 */
BI.AnalysisETLOperatorSelectData = BI.inherit(BI.Widget, {

    props: {
        extraCls: "bi-analysis-etl-operator-select-data"
    },

    render: function(){
        var self = this, o = this.options;
        this.center = this.selectPane = this.cancelButton = this.saveButton = null;
        this.model = new BI.AnalysisETLOperatorSelectDataModel(o.items);
        this.trigger = BI.Utils.triggerPreview()
        return {
            type: "bi.htape",
            items: [{
                el: {
                    type: "bi.vtape",
                    cls: o.extraCls + "-west",
                    items: [{
                        el: {
                            type: "bi.center_adapt",
                            items: [{
                                type: "bi.layout",
                                width: 10
                            }, {
                                type: "bi.button",
                                ref: function(_ref){
                                    self.cancelButton = _ref;
                                },
                                text: BI.i18nText("BI-Basic_Cancel"),
                                height: 30,
                                level: 'ignore',
                                handler: function () {
                                    self.cancelAddField();
                                }
                            }, {
                                type: "bi.layout",
                                width: 10
                            }, {
                                type: "bi.button",
                                text: BI.i18nText("BI-Finish_add"),
                                height: 30,
                                ref: function(_ref){
                                    self.saveButton = _ref;
                                },
                                handler: function () {
                                    self.changeEditState()
                                }
                            }, {
                                type: "bi.layout",
                                width: 10
                            }],

                        },
                        height: 40
                    }, {
                        el: {
                            type: "bi.analysis_etl_select_data_pane",
                            ref: function(_ref){
                                self.selectPane = _ref;
                            },
                            listeners: [{
                                eventName: BI.AnalysisETLSelectDataPane.EVENT_CLICK_ITEM,
                                action: function(v){
                                    self.addField.apply(self, [v])
                                }
                            }]
                        }
                    }]
                },
                width: 240
            }, {
                el: {
                    type: "bi.analysis_etl_operator_center",
                    nameValidationController: function () {
                        return self.checkNameValid.apply(self, arguments)
                    },
                    ref: function(_ref){
                        self.center = _ref;
                    },
                    listeners: [{
                        eventName: BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE,
                        action: function(){
                            self.saveButton.setEnable(false);
                            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, arguments)
                        }
                    }, {
                        eventName: BI.TopPointerSavePane.EVENT_SAVE,
                        action: function(v){
                            self.saveButton.setEnable(true);
                        }
                    }, {
                        eventName: BI.TopPointerSavePane.EVENT_CANCEL,
                        action: function(){
                            self.saveButton.setEnable(true);
                            self._populate();
                            self.fireEvent(BI.TopPointerSavePane.EVENT_CANCEL, arguments)
                        }
                    }, {
                        eventName: BI.AnalysisETLPreviewTable.DELETE_EVENT,
                        action: function(v){
                            self.deleteFieldByIndex(v);
                        }
                    }, {
                        eventName: BI.AnalysisETLOperatorCenter.EVENT_RENAME,
                        action: function(index, name){
                            self.renameField(index, name);
                        }
                    },{
                        eventName: BI.AnalysisETLPreviewTable.EVENT_SORT_COLUMN,
                        action: function(oldIndex, newIndex){
                            self.sortColumn(oldIndex, newIndex);
                        }
                    }, {
                        eventName: BI.TopPointerSavePane.EVENT_INVALID,
                        action: function(){
                            self.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, arguments)
                        }
                    }, {
                        eventName: BI.TopPointerSavePane.EVENT_FIELD_VALID,
                        action: function(){
                            self.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, arguments)
                        }
                    }, {
                        eventName: BI.Controller.EVENT_CHANGE,
                        action: function(v){
                            self.saveButton.setEnable(!v);
                            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments)
                        }
                    }, {
                        eventName: BI.AnalysisOperatorTitle.EVENT_SAVE,
                        action: function(){
                            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_SAVE, arguments)
                        }
                    }, {
                        eventName: BI.AnalysisOperatorTitle.EVENT_SAVE,
                        action: function(){
                            self.fireEvent(BI.AnalysisOperatorTitle.EVENT_SAVE, arguments)
                        }
                    }, {
                        eventName: BI.AnalysisETLOperatorCenter.DATA_CHANGE,
                        action: function(model){
                            self.populate(model);
                        }
                    }, {
                        eventName: BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE,
                        action: function(previewModel, operatorType){
                            self.refreshPreviewData(previewModel, operatorType)
                        }
                    }]
                }
            }]
        };
    },

    addField : function(fieldId){
        if(this._editing !== true){
            return;
        }
        this.model.addField(fieldId)
        this._refreshState();
    },

    _refreshSelectDataPane : function () {

        var tables = this.model.getTempFieldsTables();
        tables = BI.Utils.getProbablySinglePathTables(tables);

        this.selectPane.setEnableTables(tables);
    },

    refreshPopData : function (operatorType){
        this.trigger(this.center, this.model, operatorType, ETLCst.PREVIEW.SELECT)
    },

    refreshPreviewData:function (tempModel, operatorType) {
        this.trigger(this.center, tempModel, operatorType, ETLCst.PREVIEW.NORMAL);
    },

    deleteFieldByIndex : function (v) {
        this.model.removeAt(v);
        this._refreshState();
    },

    renameField : function (index, name) {
        this.model.rename(index, name)
    },

    checkNameValid : function (index, name) {
        return this.model.checkNameValid(index, name);
    },

    sortColumn : function (oldIndex, newIndex) {
        this.model.sort(oldIndex, newIndex)
        this._refreshState();
    },

    changeEditState : function() {
        var self = this;
        var change = function () {
            self._editing = !self._editing;

            if(self._editing === false){
                self.model.save();
                var v = self.model.update();
                self.fireEvent(BI.TopPointerSavePane.EVENT_SAVE, v);
            } else {
                self.model.cancel();
                self._refreshState();
            }
        }
        if(this._editing === true){
            var res = this.options.checkBeforeSave(this.model.update4Preview())
            if(res[0] === false) {
                BI.Msg.confirm(BI.i18nText("BI-Modify_Step"), res[1], function (v) {
                    if(v === true) {
                        change();
                    }
                })
            } else {
                change();
            }
        } else {
            change()
        }





    },


    cancelAddField : function () {
        this.model.cancel();
        this.changeEditState();
    },


    _refreshButtonState : function () {
        this.cancelButton.setEnable(this.model.needCancel());
        this.saveButton.setText( this._editing === true ? BI.i18nText("BI-Finish_add") : BI.i18nText("BI-continue_add"));
        if(this._editing === true) {
            this.saveButton.setEnable(this.model.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY).length > 0)
        }
    },


    _refreshCenterState : function() {
        var enable = !this._editing;
        //如果没有添加字段是不可以下一步的
        if(this.model.get(BI.AnalysisETLOperatorSelectDataModel.KEY) === 0){
            enable = false;
        }
        this.center.setEnable(enable)
    },

    _showMask:function () {
        var masker = BI.Layers.make(this.getName(),  this.selectPane,  {
            render : {
                cls:"disable"
            }
        });
        BI.Layers.show(this.getName());
    },

    _clearMask : function () {
        BI.Layers.hide(this.getName());
    },

    _refreshState : function () {
        this._refreshSelectDataPane();
        this._refreshButtonState();
        this._refreshCenterState();
        this.refreshPopData(this._editing ? ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA : ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL);
        this._editing === true ? this._clearMask() : this._showMask();
    },

    _populate: function(){
        this.center.populate(this.model.update(), BI.extend(this.options, {
            showContent: false
        }));
        this._refreshState();
        this.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, this.model.getCopyValue(ETLCst.FIELDS))
    },

    //todo 外界调用populate居然还会传options.func进来以拓展自身的controller，现在放widget里，之后删掉
    populate : function (m, options) {
        this.model.populate(m);
        this._editing = this.model.get(ETLCst.FIELDS).length === 0;
        this.selectPane.populate(m);
        BI.extend(this.options, options);
        this._populate();
    },

    getValue: function(){
        return this.model.update();
    }

})

BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA, BI.AnalysisETLOperatorSelectData);