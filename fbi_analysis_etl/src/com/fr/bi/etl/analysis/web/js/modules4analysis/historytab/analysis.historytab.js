/**
 * Created by Young's on 2017/4/12.
 */
BI.AnalysisHistoryTab = BI.inherit(BI.Widget, {

    _constant: {
        width: 190
    },

    props: {
        extraCls: "bi-history-tab",
        allHistory: false,
        table: {}
    },

    render: function () {
        var o = this.options;
        this.model = new BI.AnalysisHistoryTabModel();
        this.historyTabs = [];

        this.tabButton = BI.createWidget({
            type: "bi.analysis_history_button_group",
            items: o.items,
            allHistory: o.allHistory,
            layouts: [{
                type: "bi.vertical"
            }]
        });

        this.tab = BI.createWidget({
            direction: "custom",
            type: "bi.tab",
            tab: this.tabButton,
            defaultShowIndex: false,
            cardCreator: BI.bind(this._createTabs, this)
        });

        BI.createWidget({
            type: "bi.htape",
            element: this,
            bgap: 10,
            items: [{
                el: this.tab
            }, {
                el: {
                    type: "bi.center",
                    cls: "histroy-tab-east",
                    rgap: 10,
                    vgap: 10,
                    items: [{
                        type: "bi.vtape",
                        cls: "bi-history-tab-button-group",
                        items: [{
                            el: {
                                type: "bi.center_adapt",
                                cls: "bi-history-tab-title",
                                items: [{
                                    type: "bi.label",
                                    text: BI.i18nText("BI-History_Record")
                                }]
                            },
                            height: 40
                        }, {
                            el: {
                                type: "bi.layout"
                            },
                            height: 10
                        }, {
                            el: {
                                type: "bi.center",
                                lgap: -1,
                                scrolly: null,
                                items: [this.tabButton]
                            }
                        }]
                    }]
                },
                width: this._constant.width
            }]
        });
    },

    isCurrentTheLastOperator: function () {
        var v = this.tabButton.getValue()[0];
        var position = this.getIndexByValue(v);
        return position === this.getSavedItems().length - 1;
    },

    _createTabs: function (v) {
        var self = this;
        BI.nextTick(function () {
            self.historyTabs[v].populate(self.model.getOperatorByValue(v));
        });
        return {
            type: this.getOperatorTypeByValue(v),
            isCurrentTheLastOperator: BI.bind(this.isCurrentTheLastOperator, this),
            ref: function (ref) {
                self.historyTabs[v] = ref;
            },
            listeners: [{
                eventName: BI.Controller.EVENT_CHANGE,
                action: function (v, table) {
                    if (v === false) {
                        self.addNewHistory(table)
                    }
                }
            }, {
                eventName: BI.AnalysisTopPointerSavePane.EVENT_SAVE,
                action: function (table) {
                    self.saveOneSheet(table);
                    self.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_SAVE, table);
                }
            }, {
                eventName: BI.AnalysisTopPointerSavePane.EVENT_FIELD_VALID,
                action: function (fields) {
                    self.refreshValidFields(v, fields);
                    self.fireEvent(BI.AnalysisHistoryTab.VALID_CHANGE, self.model.isModelValid());
                }
            }, {
                eventName: BI.AnalysisTopPointerSavePane.EVENT_INVALID,
                action: function (title) {
                    self.setInvalid(v, title);
                    self.fireEvent(BI.AnalysisHistoryTab.VALID_CHANGE, self.model.isModelValid());
                }
            }, {
                eventName: BI.AnalysisOperatorTitle.EVENT_SAVE,
                action: function (value, desc) {
                    self.clickTitleSave(v, value, desc);
                }
            }, {
                eventName: BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE,
                action: function () {
                    self.fireEvent(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, arguments)
                }
            }, {
                eventName: BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE,
                action: function () {
                    self.tempAddButton(v.getValue())
                }
            }, {
                eventName: BI.AnalysisTopPointerSavePane.EVENT_CANCEL,
                action: function () {
                    self.cancelTempAddButton()
                }
            }, {
                eventName: BI.MergeHistory.CANCEL,
                action: function () {
                    self.selectLastTab()
                }
            }]
        };
    },

    _selectLastTab: function () {
        var items = this.model.getOperators();
        var validIndex = this.model.getInvalidIndex();
        this._selectTabByIndex(Math.min(validIndex, items.length - 1));
    },

    _selectTabByIndex: function (index) {
        var items = this.model.getOperators();
        if (items.length === 0) {
            return;
        }
        var len = Math.max(0, Math.min(items.length - 1, index));
        this.tab.setSelect(items[len]["value"]);
    },

    findItem: function (v) {
        var allHistoryId = this.tabButton.allHistoryId;
        if (v === allHistoryId) {
            return this.model.createHistoryModel()
        }
        return this.model.findItem(v);
    },

    getIndexByValue: function (v) {
        return this.model.getIndexByValue(v);
    },

    /**
     * historyTab中实际保存的items(不包括新增的正在编辑的那个)
     */
    getSavedItems: function () {
        return this.model.getOperators();
    },

    checkBeforeSave: function (table) {
        var v = this.tabButton.getValue()[0];
        var position = this.model.getIndexByValue(v);
        return this.model.checkBeforeSave(table, position);
    },

    addNewHistory: function (table) {
        this.cancelTempAddButton();
        var operator = ETLCst.ANALYSIS_TABLE_OPERATOR_KEY[table.etlType];
        var v = this.tabButton.getValue()[0];
        var position = this.model.getIndexByValue(v);
        var item = this.model.addNewOperator(operator, table);
        this._addNewButtonAfterPos(item, position);
        this.tab.setSelect(item.value);
        this._refreshAfterSheets(item);
    },

    populateOneTab: function (v) {
        var self = this;
        var tab = this.tab.getTab(v);
        tab.populate(BI.extend({tableName: this.model.getValue().tableName}, this.findItem(v)), BI.extend(this.options, {
            checkBeforeSave: function (table) {
                return self.checkBeforeSave(table);
            }
        }))
    },

    _refreshAfterSheets: function (table) {
        var self = this;
        var index = this.model.getIndexByValue(table.value);
        //从index开始更新index后面的面板信息
        var items = this.model.getOperators();
        for (var i = index + 1; i < items.length; i++) {
            setTimeout(function (item) {
                return function () {
                    self.populateOneTab(item.value)
                }
            }(items[i]), 0);
        }
        if (this.options.allHistory === true) {
            var allHistoryId = this.tabButton.allHistoryId;
            BI.nextTick(function () {
                self.populateOneTab(allHistoryId)
            })

        }
    },

    saveOneSheet: function (table) {
        this.model.saveItem(table);
        this.populateOneTab(table.value);
        this._refreshAfterSheets(table);
    },

    clickTitleSave: function (id, value, desc) {
        var item = this.model.findItem(id);
        var sheets = [BI.extend(BI.deepClone(item), {
            value: this.model.getValue("value"),
            tableName: value
        })];
        var table = {};
        table[ETLCst.ITEMS] = sheets;
        var analysis = {};
        analysis.id = BI.UUID();
        analysis.name = value;
        analysis.describe = desc;
        analysis.table = table;
        BI.ETLReq.reqSaveTable(analysis, BI.emptyFn);
    },

    getOperatorTypeByValue: function (v) {
        var allHistoryId = this.tabButton.allHistoryId;
        if (v === allHistoryId) {
            return "bi.analysis_etl_merge_history";
        }
        return this.model.getOperatorTypeByValue(v);
    },

    setInvalid: function (v, title) {
        var index = this.model.getIndexByValue(v);
        var invalidIndex = this.model.getInvalidIndex();
        if (invalidIndex === -1 || index <= invalidIndex) {
            this.model.setInvalidIndex(index);
            this.model.setInvalidTitle(title);
            var items = this.model.getOperators();
            for (var i = index; i < items.length; i++) {
                var btn = this.tabButton.getButton(items[i].value);
                this.model.setFields(items[i].value, []);
                btn.setValid(false);
                btn.setTitle(title);
                btn.setWarningTitle(title);
                if (i > index) {
                    btn.setEnable(false);
                }
            }
        }
    },

    refreshValidFields: function (v, fields) {
        this.model.setFields(v, fields);
        var index = this.model.getIndexByValue(v);
        if (index === this.model.getInvalidIndex()) {
            var items = this.model.getOperators();
            for (var i = index; i < items.length; i++) {
                var btn = this.tabButton.getButton(items[i].value);
                btn.setValid(true);
                btn.setEnable(true);
                btn.setTitle(btn.getText());
            }
            this.model.setInvalidIndex(-1);
        }
    },

    cancelTempAddButton: function () {
        this._deleteTempAdd();
        this.addTempModel = false;
        this._clearHistoryMask();
    },

    _showHistoryMask: function () {
        BI.Layers.make(this.getName(), this.tabButton);
        BI.Layers.show(this.getName());
    },

    _clearHistoryMask: function () {
        BI.Layers.hide(this.getName());
    },

    tempAddButton: function (operatorValue) {
        this._deleteTempAdd();
        this.tabButton.element.addClass("bi-history-tab-fake-model");
        this.addTempModel = true;
        this._showHistoryMask();
        var operator = ETLCst.ANALYSIS_TABLE_OPERATOR_KEY[operatorValue];
        var v = this.tabButton.getValue()[0];
        var position = this.model.getIndexByValue(v);
        var item = BI.extend(this.model.createItem(operator), {
            cls: "bi-history-tab-button-temp"
        });
        this._addNewButtonAfterPos(item, position);
    },

    _deleteTempAdd: function () {
        this.tabButton.element.removeClass("bi-history-tab-fake-model");
        if (this.addTempModel === true) {
            var v = this.tabButton.getValue()[0];
            var position = this.model.getIndexByValue(v);
            this.tabButton.deletePosition(position + 1);
        }
    },

    _removeSheet: function (id) {
        var deletePos = this.model.removeItemFromValue(id);
        this.tabButton.deleteFromPosition(deletePos);
        this._selectLastTab();
        this.fireEvent(BI.AnalysisHistoryTab.VALID_CHANGE, id, this.model.isModelValid());
        var tab = this.tab.getSelectedTab();
        if (BI.isNotNull(tab) && BI.isNotNull(tab.resetPointerPosition)) {
            tab.resetPointerPosition();
        }
    },

    _addNewButtonAfterPos: function (item, index) {
        var self = this;
        var deleteButton = BI.createWidget({
            type: "bi.icon_button",
            title: BI.i18nText("Delete"),
            cls: "delete-field-font delete"
        });
        var button = BI.createWidget(BI.extend({}, item, {
            type: "bi.analysis_history_button",
            deleteButton: deleteButton
        }));
        var confirmCombo = BI.createWidget({
            type: "bi.bubble_combo",
            el: {},
            element: deleteButton,
            popup: {
                type: "bi.bubble_bar_popup_view",
                buttons: [{
                    value: BI.i18nText(BI.i18nText("BI-Basic_Sure")),
                    handler: function () {
                        confirmCombo.hideView();
                        var v = button.getValue();
                        var pos = self.model.getIndexByValue(v);
                        if (pos === 0) {
                            if (self.options.allHistory === true) {
                                self.fireEvent(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_DELETE);
                            }
                            return;
                        }
                        self._removeSheet(v);
                    }
                }, {
                    value: BI.i18nText("BI-Basic_Cancel"),
                    level: "ignore",
                    handler: function () {
                        confirmCombo.hideView();
                    }
                }],
                el: {
                    type: "bi.vertical_adapt",
                    items: [{
                        type: "bi.label",
                        whiteSpace: "normal",
                        text: BI.i18nText("BI-Confirm_Delete_Etl_History"),
                        cls: "delete-label",
                        textAlign: "left",
                        width: 300
                    }],
                    width: 300,
                    height: 100,
                    hgap: 20
                },
                maxHeight: 140,
                minWidth: 340
            }
        });
        deleteButton.on(BI.IconButton.EVENT_CHANGE, function () {
            var v = button.getValue();
            var pos = self.model.getIndexByValue(v);
            if (pos === 0 && self.options.allHistory === false) {
                BI.Msg.alert(BI.i18nText('BI-Cannot-Delete'), BI.i18nText('BI-Cannot-Delete-Last'))
            } else if (self.model.get(ETLCst.ITEMS).length - 1 !== pos) {
                confirmCombo.showView();
            } else {
                self._removeSheet(v)
            }
        });
        var invalidIndex = this.model.getInvalidIndex();
        if (invalidIndex <= index && invalidIndex !== -1) {
            button.setValid(false);
            button.setTitle(this.model.getInvalidTitle())
        }
        this.tabButton.addItemFromIndex(button, index);
        button.on(BI.Controller.EVENT_CHANGE, function () {
            BI.nextTick(function () {
                var v = button.getValue();
                var tab = self.tab.getTab(v);
                if (BI.isNotNull(tab) && BI.isNotNull(tab.resetPointerPosition)) {
                    tab.resetPointerPosition()
                }
            })
        })
    },

    selectLastTab: function () {
        this.tabButton.selectLastValue();
        this.tab.setSelect(this.tabButton.getValue([0]));
    },

    getValue: function () {
        var size = this.historyTabs.length;
        return this.historyTabs[size - 1].model.update();
    },

    populate: function (table) {
        this.model.populate(table);
        var items = this.model.getOperators();
        var self = this;
        BI.each(items, function (idx, item) {
            self._addNewButtonAfterPos(item, idx);
        });
        self._selectLastTab();
        var invalidIndex = this.model.getInvalidIndex();
        for (var i = invalidIndex; i < items.length && invalidIndex !== -1; i++) {
            var btn = this.tabButton.getButton(items[i].value);
            btn.setValid(false);
            btn.setTitle(this.model.getInvalidTitle());
        }
    }

});
BI.AnalysisHistoryTab.VALID_CHANGE = "VALID_CHANGE";
BI.shortcut("bi.analysis_history_tab", BI.AnalysisHistoryTab);