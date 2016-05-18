BI.HistoryTabColltroller = BI.inherit(BI.MVCController, {

    _defaultConfig: function () {
        return BI.extend(BI.HistoryTabColltroller.superclass._defaultConfig.apply(this, arguments), {


        });
    },

    _init: function () {
        BI.HistoryTabColltroller.superclass._init.apply(this, arguments);
        this.addTempModel = false;
    },

    _selectLastTab : function(widget, model) {
        var items = model.getValue(ETLCst.ITEMS)
        var validIndex = model.get('invalidIndex');
        this._selectTabByIndex(Math.min(validIndex, items.length - 1), widget, model)
    },

    _selectTabByIndex : function (index, widget, model) {
        var items = model.get(ETLCst.ITEMS)
        if(items.length === 0) {
            return;
        }
        var len = Math.max(0, Math.min(items.length - 1, index))
        widget.tab.setSelect(items[len]["value"]);
    },

    findItem : function (v, widget, model) {
        var allHistoryId = this._getTabButtonGroup(widget).allHistoryId;
        if(v === allHistoryId) {
            return model.createHistoryModel()
        }
        return model.findItem(v);
    },

    addNewSheet : function (table, widget, model) {
        this.cancelTempAddButton(widget, model);
        var operator = ETLCst.ANALYSIS_TABLE_OPERATOR_KEY[table.etlType];
        var v = this._getTabButtonGroup(widget).getValue()[0];
        var position = model.getIndexByValue(v);
        var item = model.addItemAfter(operator, position, table);
        this._addNewButtonAfterPos(item, position, widget, model);
        widget.tab.setSelect(item["value"]);
        this._refreshAfterSheets(item, widget, model)
        this.deferChange(widget, model);
    },

    populateOneTab : function (v, widget, model) {
        var tab = widget.tab.getTab(v)
        tab.populate(this.findItem(v, widget, model), this.options)
    },

    _refreshAfterSheets : function (table, widget, model) {
        var self = this;
        var index = model.getIndexByValue(table.value);
        //从index开始更新index后面的面板信息
        var items = model.get(ETLCst.ITEMS);
        for(var i = index + 1; i < items.length; i++) {
            setTimeout(function (item) {
                return function() {
                    self.populateOneTab(item.value, widget, model)
                }
            }(items[i]),0);
        }
        if(widget.options.allHistory === true){
            var allHistoryId = this._getTabButtonGroup(widget).allHistoryId;
            setTimeout(function(){
                self.populateOneTab(allHistoryId, widget, model)
            }, 0)

        }
    },


    saveOneSheet : function (table, widget, model) {
        model.saveItem(table);
        this.populateOneTab(table.value, widget, model)
        this._refreshAfterSheets(table, widget, model)
        this.deferChange(widget, model);
    },

    clickTitleSave : function (id, widget, model) {
        var self = this;
        var namePopover = BI.createWidget({
            type: "bi.etl_table_name_popover",
        });
        namePopover.on(BI.PopoverSection.EVENT_CLOSE, function () {
            BI.Layers.hide(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
        })
        var item = model.findItem(id)
        namePopover.on(BI.ETLTableNamePopover.EVENT_CHANGE, function (v) {
            var sheets = [BI.extend(BI.deepClone(item), {
                value:model.getValue("value"),
                table_name:v
            })]
            var res = {};
            var table = {};
            table[ETLCst.ITEMS] = sheets;
            res["table"] = table;
            res["id"] = BI.UUID();
            res["name"] = v;
            BI.ETLReq.reqSaveTable(res, BI.emptyFn);
        });
        BI.Popovers.remove("etlTableName");
        BI.Popovers.create("etlTableName", namePopover, {width : 400, height : 320, container: BI.Layers.create(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER)}).open("etlTableName");
        BI.Layers.show(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
        namePopover.populate(model.getValue("table_name") + "-" + ETLCst.ANALYSIS_TABLE_OPERATOR_KEY[item.operatorValue].text);
        namePopover.setTemplateNameFocus();
    },

    getOperatorTypeByValue : function (v, widget, model) {
        var allHistoryId = this._getTabButtonGroup(widget).allHistoryId;
        if(v === allHistoryId) {
            return "bi.analysis_etl_merge_history";
        }
        return model.getOperatorType(v);
    },

    setInvalid : function(v, title, widget, model){
        var index = model.getIndexByValue(v);
        if (index <= model.get('invalidIndex')){
            model.set('invalidIndex', index);
            model.set('invalidTitle', title)
            var items = model.get(ETLCst.ITEMS);
            for(var i = index; i < items.length; i++) {
                var btn = widget.tabButton.getButton(items[i].value);
                model.setFields(items[i].value, []);
                btn.setValid(false);
                btn.setTitle(title);
                btn.setWarningTitle(title);
                if(i > index) {
                    btn.setEnable(false);
                }
            }
        }
    },

    refreshValidFields : function(v, fields, widget, model){
        model.setFields(v, fields);
        var index = model.getIndexByValue(v);
        if (index === model.get('invalidIndex')){
            var items = model.get(ETLCst.ITEMS);
            for(var i = index; i < items.length; i++) {
                var btn = widget.tabButton.getButton(items[i].value);
                btn.setValid(true);
                btn.setEnable(true);
                btn.setTitle(btn.getText());
            }
            model.set('invalidIndex', Number.MAX_VALUE);
        }
    },


    cancelTempAddButton : function (widget, model) {
        this._deleteTempAdd(widget, model);
        this.addTempModel = false;
        this._clearHistoryMask(widget);
    },

    _showHistoryMask:function (widget) {
        var masker = BI.Layers.make(widget.getName(),  this._getTabButtonGroup(widget));
        BI.Layers.show(widget.getName());
    },

    _clearHistoryMask : function (widget) {
        BI.Layers.hide(widget.getName());
    },

    tempAddButton : function (operatorValue, widget, model) {
        this._deleteTempAdd(widget, model);
        this._getTabButtonGroup(widget).element.addClass("bi-history-tab-fake-model")
        this.addTempModel = true;
        this._showHistoryMask(widget);
        var operator = ETLCst.ANALYSIS_TABLE_OPERATOR_KEY[operatorValue];
        var v = this._getTabButtonGroup(widget).getValue()[0]
        var position = model.getIndexByValue(v);
        var item = BI.extend(model.createItem(operator), {
            cls:"bi-history-tab-button-temp"
        });
        this._addNewButtonAfterPos(item, position, widget, model);
    },

    _deleteTempAdd : function (widget, model) {
        this._getTabButtonGroup(widget).element.removeClass("bi-history-tab-fake-model")
        if(this.addTempModel === true) {
            var v = this._getTabButtonGroup(widget).getValue()[0]
            var position = model.getIndexByValue(v);
            this._getTabButtonGroup(widget).deletePosition(position + 1);
        }
    },

    _removeSheet : function (id, widget, model){
        var deletePos = model.removeItemFromValue(id);
        this._getTabButtonGroup(widget).deleteFromPosition(deletePos)
        this._selectLastTab(widget, model);
        widget.fireEvent(BI.HistoryTab.VALID_CHANGE)
        var tab = widget.tab.getSelectedTab();
        if (BI.isNotNull(tab) && BI.isNotNull(tab.controller.resetPointerPosition)) {
            tab.controller.resetPointerPosition()
        }
    },

    _addNewButtonAfterPos : function(item, index, widget, model) {
        var button = BI.createWidget(BI.extend(item, {
            type:"bi.history_button"
        }))
        var self = this;
        button.on(BI.HistoryButton.EVENT_DELETE, function(v){
            var pos = model.getIndexByValue(v);
            if(pos === 0) {
                if(widget.options.allHistory === true) {
                    BI.Msg.confirm(BI.i18nText("BI-Confirm_Delete"),BI.i18nText("BI-Confirm_Delete_Etl_History"), function (v) {
                        if(v === true) {
                            widget.fireEvent(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_DELETE);
                        }
                    })
                } else {
                    BI.Msg.alert(BI.i18nText('BI-Cannot-Delete'),  BI.i18nText('BI-Cannot-Delete-Last'))
                }
                return;
            }
            BI.Msg.confirm(BI.i18nText("BI-Confirm_Delete"), BI.i18nText("BI-Confirm_Delete_Etl_History"), function (res) {
                if(res === true) {
                    self._removeSheet(v, widget, model)
                }
            })

        })
        var invalidIndex = model.get('invalidIndex');
        if(invalidIndex <= index) {
            button.setValid(false);
            button.setTitle(model.get("invalidTitle"))
        }
        this._getTabButtonGroup(widget).addItemFromIndex(button, index);
        button.on(BI.Controller.EVENT_CHANGE, function () {
            BI.defer(function () {
                var v = button.getValue();
                var tab = widget.tab.getTab(v)
                if (BI.isNotNull(tab) && BI.isNotNull(tab.controller.resetPointerPosition)) {
                    tab.controller.resetPointerPosition()
                }
            })
        })
    },

    _getTabButtonGroup : function (widget) {
        return widget.tabButton;
    },

    populate : function (widget, model) {
        var items = model.get(ETLCst.ITEMS);
        var self = this;
        BI.each(items, function (idx, item) {
            self._addNewButtonAfterPos(item, idx, widget, model)
        });
        self._selectLastTab(widget, model);
        for(var i = model.get('invalidIndex'); i < items.length; i++) {
            var btn = widget.tabButton.getButton(items[i].value);
            btn.setValid(false);
            btn.setTitle(model.get('invalidTitle'));
        }
    },

    deferChange : function (widget, model) {
        // BI.HistoryTabColltroller.superclass.deferChange.apply(this, arguments)
        // var tab = widget.tab.getSelectedTab()
        // if (BI.isNotNull(tab) && BI.isNotNull(tab.controller.hideOperatorPane)) {
        //     tab.controller.hideOperatorPane();
        // }
    },

    selectLastTab : function (widget, model) {
        this._getTabButtonGroup(widget).selectLastValue()
        widget.tab.setSelect(this._getTabButtonGroup(widget).getValue([0]))
    }

})