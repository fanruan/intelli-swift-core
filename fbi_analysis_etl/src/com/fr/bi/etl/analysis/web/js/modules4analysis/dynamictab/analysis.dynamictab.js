/**
 * Created by Young's on 2017/4/5.
 */
BI.AnalysisDynamicTab = BI.inherit(BI.Widget, {
    constants: {
        RENAME_LAYER: "__rename_layer__"
    },

    props: {
        extraCls: "bi-sheet-tab-dynamic",
        items: [],
        height: 30
    },

    render: function () {
        var self = this;
        var o = this.options;
        this.model = new BI.AnalysisDynamicTabModel();
        this.sheets = {};

        this.tabButton = BI.createWidget({
            type: "bi.analysis_dynamic_tab_button",
            items: o.items,
            listeners: [{
                eventName: BI.AnalysisDynamicTabButton.ADD_SHEET,
                action: function () {
                    self.addNewSheet({});
                }
            }, {
                eventName: BI.AnalysisDynamicTabButton.MERGE_SHEET,
                action: function () {
                    self.chooseSheetForMerge();
                }
            }]
        });

        this.tab = BI.createWidget({
            direction: "custom",
            type: "bi.tab",
            cls: "tab-dynamic-center",
            tab: this.tabButton.getSheetGroup(),
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
    },

    _createTabs: function (v) {
        var self = this;
        BI.nextTick(function () {
            self.sheets[v].populate(self.model.getSheetByValue(v));
        });
        return {
            type: "bi.analysis_history_tab",
            cls: "bi-animate-right-in",
            ref: function (ref) {
                self.sheets[v] = ref;
            },
            listeners: [{
                eventName: BI.AnalysisTopPointerSavePane.EVENT_SAVE,
                action: function () {

                }
            }, {
                eventName: BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE,
                action: function (data, oldSheets) {
                    self.changeMergeSheet(data, oldSheets, v)
                }
            }, {
                eventName: BI.AnalysisHistoryTab.VALID_CHANGE,
                action: function (isValid) {
                    self.setTabValid(v, isValid);
                }
            }, {
                eventName: BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_DELETE,
                action: function () {
                    self.deleteMergeSheet(v)
                }
            }]
        };
    },

    renameSheet: function (newName, id) {
        var button = this.tabButton.getButton(id);
        button.setText(newName);
        this.model.reName(id, newName);
        this._resize();
    },

    setTabValid: function (v, isValid) {
        var button = this.tabButton.getButton(v);
        button.setValid(isValid)
    },

    deleteMergeSheet: function (v) {
        var self = this;
        var oldMergeTable = this.model.getSheetByValue(v);
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
        var deletePos = this.model.removeSheet(id);
        if (deletePos < 0) {
            return;
        }
        var buttonGroup = this.tabButton.getSheetGroup();
        var currentTabValue = this.tabButton.getSheetGroup().getValue()[0];
        buttonGroup.removeItems(id);
        if (currentTabValue === id) {
            this.selectTabByIndex(deletePos);
        }
        this.tabButton.resize();
        this._refreshButtonStatus()
    },

    selectTabByIndex: function (index) {
        var sheet = this.model.getSheetByIndex(index);
        this.tab.setSelect(sheet.value);
    },

    addNewSheet: function (v) {
        var value = this.model.addSheet(v);
        this._addNewButton(value);
        this.tabButton.resize();
        this.tabButton.scrollToEnd();
        this.tab.setSelect(value);
        this.tabButton.setMergeEnable(this.model.getSheets().length > 1);
    },

    _addNewButton: function (value) {
        var o = this.options;
        var item = {
            type: "bi.analysis_tab_sheet_button",
            height: o.height - 1,
            value: value,
            text: this.model.getSheetNameByValue(value)
        };
        var button = BI.createWidget(item);
        this.registerComboEvent(button);
        this.tabButton.getSheetGroup().addItems([button]);
    },

    registerComboEvent: function (button) {
        var self = this;
        button.on(BI.WidgetCombo.EVENT_CHANGE, function (v) {
            self.dealWithCombo(v, button);
        });
    },

    dealWithCombo: function (v, button) {
        var self = this;
        var id = button.getValue();
        switch (v) {
            case ETLCst.ANALYSIS_TABLE_SET.DELETE:
                var sheets = this.model.getSheets();
                if (sheets.length === 1) {
                    BI.Msg.alert(BI.i18nText('BI-Cannot-Delete'), BI.i18nText('BI-Cannot-Delete-Last'));
                    break;
                }
                BI.Msg.confirm(BI.i18nText("BI-Confirm_Delete"), BI.i18nText("BI-Confirm_Delete") + this.model.getSheetNameByValue(id) + "?", function (v) {
                    if (v === true) {
                        self._removeSheet(id);
                    }
                });
                break;
            case ETLCst.ANALYSIS_TABLE_SET.COPY:
                this._copySheet(id);
                break;
            case ETLCst.ANALYSIS_TABLE_SET.RENAME:
                this._buildRenamePopover(id, self.model.getSheetNameByValue(id));
                break;
        }
    },

    _buildRenamePopover: function (value, name) {
        var self = this;
        var renamePopover = BI.createWidget({
            type: "bi.etl_table_rename_popover",
            renameChecker: function () {
                return !self.model.isNameExists(name, value);
            }
        });
        renamePopover.on(BI.AnalysisTableRename.EVENT_CHANGE, function (newName) {
            self.renameSheet(newName, value);
        });
        renamePopover.on(BI.PopoverSection.EVENT_CLOSE, function () {
            BI.Layers.hide(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
        });
        BI.Popovers.remove(this.constants.RENAME_LAYER);
        BI.Popovers.create(this.constants.RENAME_LAYER, renamePopover, {
            width: 400,
            height: 320,
            container: BI.Layers.create(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER)
        }).open(this.constants.RENAME_LAYER);
        BI.Layers.show(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
        renamePopover.populate(name);
        renamePopover.setTemplateNameFocus();
    },

    _copySheet : function(id) {
        var value = this.model.copyItem(id);
        this._addNewButton(value);
        this.tabButton.resize();
        this.tabButton.scrollToEnd();
        this.tab.setSelect(value);
        this._refreshButtonStatus();
    },


    chooseSheetForMerge: function () {
        var self = this;
        var sheets = this.model.getSheets();
        var items = BI.map(sheets, function (idx, sheet) {
            return {
                text: self.model.getSheetNameByValue(sheet.value),
                value: sheet.value
            }
        });
        var getTablesBySheetId = function (sheets) {
            var tables = [];
            BI.each(sheets, function (idx, item) {
                tables.push(getTableById(item))
            });
            return tables;
        };
        var getTableById = function (id) {
            return BI.find(self.sheets, function (idx, item) {
                return item.value === id;
            })
        };
        var func = function (v) {
            var m = {
                name: self.model.createNewName(BI.i18nText("BI-Merge_Table")),
                tables: self.sheets
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
        var id = this.tabButton.getValue()[0];
        var others = [];
        var currentTable = null;
        BI.each(this.sheets, function (idx, item) {
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

    _refreshButtonStatus: function () {
        this.tabButton.setMergeEnable(this.getSheetLength() > 1);
    },

    getSheetLength: function () {
        return BI.size(this.sheets);
    },

    getValue: function () {
        var items = [];
        BI.each(this.sheets, function (id, sheet) {
            items.push(sheet.getValue());
        });
        return {
            items: items
        };
    },

    populate: function (sheets) {
        var self = this, o = this.options;
        var items = [];
        this.model.populate(sheets);
        BI.each(this.model.getSheets(), function (idx, sheet) {
            var value = sheet.value;
            var button = BI.createWidget({
                type: "bi.analysis_tab_sheet_button",
                height: o.height - 1,
                value: value,
                text: self.model.getSheetNameByValue(value)
            });
            self.registerComboEvent(button);
            items.push(button);
        });
        this.tabButton.populateSheetGroup(items);
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