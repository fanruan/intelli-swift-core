BI.DynamictabController = BI.inherit(BI.MVCController, {

   
    _defaultConfig: function () {
        return BI.extend(BI.DynamictabController.superclass._defaultConfig.apply(this, arguments), {
        });
    },

    _init: function () {
        BI.DynamictabController.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.renameController = new BI.ETLRenamePopoverController();
    },

    selectFirstTab : function (widget, model) {
        this.selectTabByIndex(0, widget, model);
    },

    dealWithCombo : function (v, button, widget, model) {
        var self = this;
        switch (v) {
            case ETLCst.ANALYSIS_TABLE_SET.DELETE:{
                var items = model.get(ETLCst.ITEMS);
                if(items.length === 1) {
                    BI.Msg.alert(BI.i18nText('BI-Cannot-Delete'),  BI.i18nText('BI-Cannot-Delete-Last'))
                    break;
                }
                var id = button.getValue();
                BI.Msg.confirm(BI.i18nText("BI-Confirm_Delete"),  BI.i18nText("BI-Confirm_Delete") +  model.getName(id) + "?", function (v) {
                    if(v === true) {
                        self._removeSheet(id, widget, model);
                    }
                })
                break;
            }
            case ETLCst.ANALYSIS_TABLE_SET.COPY:{
                this._copySheet(button.getValue(), widget, model);
                break;
            }
            case ETLCst.ANALYSIS_TABLE_SET.RENAME:{
                var id = button.getValue();
                this.renameController.showPopover(model.getName(id), function (v) {
                    return !model.isNameExists(v, id);
                }, function(v) {
                    self.renameSheet(v, id, widget, model)
                });
                break;
            }
        }
    },

    renameSheet : function (newName, id, widget, model) {
        var button = widget.tabButton.getButton(id);
        button.setText(newName);
        model.reName(id, newName)
        this._resize(widget);
    },

    getSheetName : function (id,  widget, model ){
        return model.getName(id)
    },

    _copySheet : function(id, widget, model) {
        var item = model.copyItem(id)
        this._addNewButton(item, widget, model);
        this._resize(widget)
        this._getButtonController(widget).scrollToEnd();
        widget.tab.setSelect(item);
        this._refreshButtonStatus(widget, model)
    },

    _refreshButtonStatus : function (widget, model) {
        this._getButtonController(widget).setMergeEnable(model.getValue(ETLCst.ITEMS).length > 1);
    },

    _removeSheet : function (id, widget, model){
        var deletePos = model.removeItem(id)
        if(deletePos < 0) {
            return;
        }
        var buttonGroup = this._getTabButtonGroup(widget);
        var currentTabValue = buttonGroup.getValue()[0];
        buttonGroup.removeItems(id)
        if(currentTabValue === id) {
            this.selectTabByIndex(deletePos, widget, model);
        }
        this._resize(widget);
        this._refreshButtonStatus(widget, model)
    },

    selectTabByIndex : function (index, widget, model) {
        var items = model.get(ETLCst.ITEMS);
        if(items.length === 0) {
            return;
        }
        var len = Math.max(0, Math.min(items.length - 1, index))
        widget.tab.setSelect(items[len]);
    },


    addNewSheet : function (v, widget, model) {
        var value = model.addItem(v);
        this._addNewButton(value, widget, model);
        this._resize(widget)
        this._getButtonController(widget).scrollToEnd();
        widget.tab.setSelect(value);
        this._refreshButtonStatus(widget, model)
    },

    _addNewButton : function(value, widget, model) {
        var o = widget.options;
        var item = {
            type:"bi.sheet_button",
            height: o.height - 1,
            value : value,
            text : model.getName(value)
        }
        var button = BI.createWidget(item);
        this.registerComboEvent(button, widget, model);
        this._getTabButtonGroup(widget).addItems([button]);
    },

    _getTabButtonGroup : function (widget) {
        return widget.tabButton.tab;
    },

    _getButtonController : function (widget) {
        return widget.tabButton.controller;
    },

    _resize : function(widget) {
        this._getButtonController(widget).resize()
    },


    getTabItem : function (v, widget, model) {
        return model.getSheetData(v);
    },

    resize : function (widget) {
      this._resize(widget);
    },

    registerComboEvent : function (button, widget, model) {
        var self = this;
        button.on(BI.WidgetCombo.EVENT_CHANGE, function(v){;
            self.dealWithCombo(v, button, widget, model);
        });
    },

    //FIXME 表格的数据结构现在取第一个选的字段处理,这里只是处理下demo
    _createTables : function (widget, model) {
        return model.update()[ETLCst.ITEMS];
    },

    deleteMergeSheet : function (v, widget, model) {
        var oldMergeTable = model.getSheetData(v);
        var parents = oldMergeTable[ETLCst.PARENTS];
        while(parents.length === 1) {
            parents = parents[0][ETLCst.PARENTS];
        }
        var self = this;
        self._removeSheet(v, widget, model);
        BI.each(parents, function (idx, item) {
            self.addNewSheet(item, widget, model)
        });
        this.selectFirstTab(widget, model)
    },

    changeMergeSheet : function (data, oldMergeTable, v, widget, model) {
        var sheets = data["sheets"];
        var self = this;
        BI.each(sheets, function (idx, item) {
            self._removeSheet(item, widget, model);
        });
        BI.each(oldMergeTable, function (idx, item) {
            if(BI.indexOf(sheets, item.value) < 0) {
                self.addNewSheet(item, widget, model);
            }
        });
        widget.tab.setSelect(v);

    },

    saveMergeSheet : function (v, widget, model) {
        var sheets = v["sheets"];
        var self = this;
        BI.each(sheets, function (idx, item) {
            self._removeSheet(item, widget, model);
        })
        self.addNewSheet(BI.extend(v, {
            table_name:v["name"],
            allHistory:true,
            etlType: ETLCst.ETL_TYPE.MERGE_SHEET,
            operator: BI.deepClone(v)
        }),  widget, model)
    },

    chooseSheetForMerge : function (widget, model) {
        var tabs = model.get(ETLCst.ITEMS)
        var items = BI.map(tabs, function (idx, item) {
            return {
                text: model.getName(item),
                value:item
            }
        });

        var tables = this._createTables(widget, model);

        var self = this;

        var getTablesBySheetId = function (sheets) {
            var tables = [];
            var self = this;
            BI.each(sheets, function (idx, item) {
                tables.push(getTableById(item))
            })
            return tables;
        }

        var getTableById = function (id) {
            return BI.find(tables, function (idx, item) {
                return item.value === id;
            })
        }

        var func = function (v) {
            var m = {
                name: model.createNewName(BI.i18nText("BI-Merge_Table")),
                tables: tables
            }
            m[ETLCst.PARENTS] = getTablesBySheetId(v);
            BI.createWidget({
                type : "bi.analysis_etl_merge_sheet",
                element:BI.Layers.create(BICst.ANALYSIS_MERGE_LAYER, "body"),
                model :m,
                controller : {
                    saveHandler : function(v) {
                        self.saveMergeSheet(v, widget, model)
                    }
                }
            })
            BI.Layers.show(BICst.ANALYSIS_MERGE_LAYER)
        }
        if(items.length === 2) {
            var value = [];
            BI.each(items, function (idx, item) {
                value.push(item.value)
            })
            func(value)
        } else {
            var popover = BI.createWidget({
                type: "bi.analysis_etl_choose_sheet_popover",
                items: items
            });
            popover.on(BI.PopoverSection.EVENT_CLOSE, function () {
                BI.Layers.hide(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
            })
            popover.on(BI.AnalysisETLMergeSheetPopover.EVENT_CHANGE, func);
            BI.Popovers.remove("etlChooseSheetForMerge");
            BI.Popovers.create("etlChooseSheetForMerge", popover, {width : 500, height : 400, container: BI.Layers.create(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER)}).open("etlChooseSheetForMerge");
            BI.Layers.show(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER)
        }
    },

    getCurrentTables : function (widget, model) {
        var tables = this._createTables(widget, model);
        var id =   this._getTabButtonGroup(widget).getValue()[0];
        var others = [];
        var currentTable = null;
        BI.each(tables, function (idx, item) {
            if(item.value === id) {
                currentTable = item;
            } else {
                others.push(item)
            }
        })
        return {
            currentTable :currentTable,
            others : others
        }
    },

    populate : function (widget, model) {
        var items = model.getValue(ETLCst.ITEMS);
        this._getTabButtonGroup(widget).empty();
        var self = this;
        BI.each(items, function(idx, item){
            self._addNewButton(item, widget, model)
        });
    },

    deferChange : function (widget, model) {
        this._resize(widget)
        this._getButtonController(widget).scrollToEnd();
        this.selectFirstTab(widget, model);
        this._refreshButtonStatus(widget, model)
    }
})