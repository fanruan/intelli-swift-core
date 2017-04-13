/**
 * Created by Young's on 2017/4/5.
 */
BI.AnalysisDynamicTab = BI.inherit(BI.Widget, {
    props: {
        extraCls: "bi-sheet-tab-dynamic",
        model: {
            items: []
        },
        height: 30
    },

    render: function () {
        var self = this;
        var o = this.options;
        this.model = new BI.AnalysisDynamicTabModel(o);
        this.tabButton = BI.createWidget({
            type: "bi.analysis_dynamic_tab_button",
            items: []
        });
        this.tabButton.on(BI.AnalysisDynamicTabButton.ADD_SHEET, function (v) {
            self.addNewSheet({});
        });
        this.tabButton.on(BI.AnalysisDynamicTabButton.MERGE_SHEET, function (v) {
            self.chooseSheetForMerge();
        });

        this.tab = BI.createWidget({
            direction: "custom",
            type: "bi.tab",
            cls: "tab-dynamic-center",
            tab: this.tabButton.tab,
            defaultShowIndex: false,
            cardCreator: BI.bind(this._createTabs, this)
        });
        BI.createWidget({
            type: "bi.vtape",
            element: this,
            items: [{
                el: {
                    type: "bi.center",
                    items: [this.tab],
                    hgap: 20
                }
            }, {
                el: this.tabButton,
                height: o.height
            }]
        });
        this.populate();
    },

    _createTabs: function (v) {
        var self = this;
        this.historyTab = BI.createWidget({
            type: "bi.analysis_history_tab",
            cls: "bi-animate-right-in",
            allHistory: this.model.hasMergeHistory(v)
        });
        this.historyTab.on(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, function (data, oldSheets) {
            self.changeMergeSheet(data, oldSheets, v)
        });
        this.historyTab.on(BI.AnalysisHistoryTab.VALID_CHANGE, function () {
            self.setTabValid(v)
        });
        this.historyTab.on(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_DELETE, function () {
            self.deleteMergeSheet(v)
        });
        return this.historyTab;
    },

    renameSheet: function (newName, id) {
        var button = this.tabButton.getButton(id);
        button.setText(newName);
        this.model.reName(id, newName);
        this._resize();
    },

    setTabValid: function (v) {
        var isValid = this.model.get(v).isModelValid();
        var button = this.tabButton.getButton(v);
        button.setValid(isValid)
    },

    deleteMergeSheet: function (v) {
        var self = this;
        var oldMergeTable = this.model.getSheetData(v);
        var parents = oldMergeTable.parents;
        while (parents.length === 1) {
            parents = parents[0].parents;
        }
        self._removeSheet(v);
        BI.each(parents, function (idx, item) {
            self.addNewSheet(item);
        });
        this.selectFirstTab()
    },

    selectFirstTab: function () {
        this.selectTabByIndex(0);
    },

    changeMergeSheet: function (data, oldMergeTable, v) {
        var sheets = data.sheets;
        var self = this;
        BI.each(sheets, function (idx, item) {
            self._removeSheet(item);
        });
        BI.each(oldMergeTable, function (idx, item) {
            if (BI.indexOf(sheets, item.value) < 0) {
                self.addNewSheet(item);
            }
        });
        this.tab.setSelect(v);
    },

    _removeSheet: function (id) {
        var deletePos = this.model.removeItem(id);
        if (deletePos < 0) {
            return;
        }
        var buttonGroup = this.tabButton.tab;
        var currentTabValue = this.buttonGroup.getValue()[0];
        buttonGroup.removeItems(id);
        if (currentTabValue === id) {
            this.selectTabByIndex(deletePos);
        }
        this._resize();
        this._refreshButtonStatus()
    },

    selectTabByIndex: function (index) {
        var items = this.model.get(ETLCst.ITEMS);
        if (items.length === 0) {
            return;
        }
        var len = Math.max(0, Math.min(items.length - 1, index));
        this.tab.setSelect(items[len]);
    },

    addNewSheet: function (v) {
        var value = this.model.addItem(v);
        this._addNewButton(value);
        this.tabButton.resize();
        this.tabButton.scrollToEnd();
        this.tab.setSelect(value);
        this.tabButton.setMergeEnable(this.model.getValue(ETLCst.ITEMS).length > 1);
    },

    _addNewButton: function (value) {
        var o = this.options;
        var item = {
            type: "bi.sheet_button",
            height: o.height - 1,
            value: value,
            text: this.model.getName(value)
        };
        var button = BI.createWidget(item);
        this.registerComboEvent(button);
        this.tabButton.tab.addItems([button]);
    },

    registerComboEvent: function (button) {
        var self = this;
        button.on(BI.WidgetCombo.EVENT_CHANGE, function (v) {
            self.dealWithCombo(v, button);
        });
    },

    dealWithCombo: function (v, button) {
        var self = this;
        switch (v) {
            case ETLCst.ANALYSIS_TABLE_SET.DELETE:
                var items = this.model.getItems();
                if (items.length === 1) {
                    BI.Msg.alert(BI.i18nText('BI-Cannot-Delete'), BI.i18nText('BI-Cannot-Delete-Last'))
                    break;
                    var id = button.getValue();
                    BI.Msg.confirm(BI.i18nText("BI-Confirm_Delete"), BI.i18nText("BI-Confirm_Delete") + model.getName(id) + "?", function (v) {
                        if (v === true) {
                            self._removeSheet(id);
                        }
                    });
                    break;
                }
            case ETLCst.ANALYSIS_TABLE_SET.COPY:
                this._copySheet(button.getValue());
                break;
            case ETLCst.ANALYSIS_TABLE_SET.RENAME:
                var id = button.getValue();
                this.renameController.showPopover(model.getName(id), function (v) {
                    return !model.isNameExists(v, id);
                }, function (v) {
                    self.renameSheet(v, id)
                });
                break;
        }
    },

    chooseSheetForMerge: function () {
        var self = this;
        var tabs = this.model.get(ETLCst.ITEMS);
        var items = BI.map(tabs, function (idx, item) {
            return {
                text: self.model.getName(item),
                value: item
            }
        });
        var tables = this._createTables();
        var getTablesBySheetId = function (sheets) {
            var tables = [];
            var self = this;
            BI.each(sheets, function (idx, item) {
                tables.push(getTableById(item))
            });
            return tables;
        };
        var getTableById = function (id) {
            return BI.find(tables, function (idx, item) {
                return item.value === id;
            })
        };
        var func = function (v) {
            var m = {
                name: self.model.createNewName(BI.i18nText("BI-Merge_Table")),
                tables: tables
            };
            m[ETLCst.PARENTS] = getTablesBySheetId(v);
            BI.createWidget({
                type: "bi.analysis_etl_merge_sheet",
                element: BI.Layers.create(BICst.ANALYSIS_MERGE_LAYER, "body"),
                model: m,
                controller: {
                    saveHandler: function (v) {
                        self.saveMergeSheet(v)
                    }
                }
            });
            BI.Layers.show(BICst.ANALYSIS_MERGE_LAYER)
        };
        if (items.length === 2) {
            var value = [];
            BI.each(items, function (idx, item) {
                value.push(item.value)
            });
            func(value)
        } else {
            var popover = BI.createWidget({
                type: "bi.analysis_etl_choose_sheet_popover",
                items: items
            });
            popover.on(BI.PopoverSection.EVENT_CLOSE, function () {
                BI.Layers.hide(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
            });
            popover.on(BI.AnalysisETLMergeSheetPopover.EVENT_CHANGE, func);
            BI.Popovers.remove("etlChooseSheetForMerge");
            BI.Popovers.create("etlChooseSheetForMerge", popover, {
                width: 500,
                height: 400,
                container: BI.Layers.create(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER)
            }).open("etlChooseSheetForMerge");
            BI.Layers.show(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER)
        }
    },

    getCurrentTables: function () {
        var tables = this._createTables();
        var id = this.tabButton.getValue()[0];
        var others = [];
        var currentTable = null;
        BI.each(tables, function (idx, item) {
            if (item.value === id) {
                currentTable = item;
            } else {
                others.push(item)
            }
        });
        return {
            currentTable: currentTable,
            others: others
        }
    },

    _createTables: function () {
        return this.model.getValue().items;
    },

    _refreshButtonStatus: function () {
        this.tabButton.setMergeEnable(this.options.items.length > 1);
    },

    populate: function () {
        var self = this, o = this.options;
        var items = [];
        BI.each(this.model.get(ETLCst.ITEMS), function (idx, value) {
            var button = BI.createWidget({
                type: "bi.sheet_button",
                height: o.height - 1,
                value: value,
                text: self.model.getName(value)
            });
            self.registerComboEvent(button);
            items.push(button);
        });
        this.historyTab && this.historyTab.populate();
        this.tabButton.tab.populate(items);
        BI.nextTick(function () {
            self.deferChange();
        });
    },

    deferChange: function () {
        this.tabButton.resize();
        this.tabButton.scrollToEnd();
        this.selectFirstTab();
        this._refreshButtonStatus();
    }
});
BI.shortcut("bi.analysis_dynamic_tab", BI.AnalysisDynamicTab);