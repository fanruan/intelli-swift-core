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
        model: {
            items: []
        }
    },

    render: function () {
        var self = this, o = this.options;
        this.model = new BI.AnalysisHistoryTabModel(o.model);
        this.tabButton = BI.createWidget({
            type: "bi.analysis_history_button_group",
            items: [],
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
        this.populate();
    },

    isCurrentTheLastOperator: function () {
        var v = this.tabButton.getValue()[0];
        var position = this.getIndexByValue(v);
        return position === this.getSavedItems().length - 1;
    },

    _createTabs: function (v) {
        var self = this;
        var tab = BI.createWidget({
            type: this.getOperatorTypeByValue(v),
            isCurrentTheLastOperator: BI.bind(this.isCurrentTheLastOperator, this)
        });

        BI.nextTick(function () {
            self.populateOneTab(v);
        });
        tab.on(BI.Controller.EVENT_CHANGE, function () {
            if (arguments[0] === false) {
                self.addNewSheet(arguments[1])
            }
        });

        tab.on(BI.TopPointerSavePane.EVENT_SAVE, function (table) {
            self.saveOneSheet(table);
        });

        tab.on(BI.TopPointerSavePane.EVENT_FIELD_VALID, function (fields) {
            self.refreshValidFields(v, fields);
            self.fireEvent(BI.AnalysisHistoryTab.VALID_CHANGE)
        });

        tab.on(BI.TopPointerSavePane.EVENT_INVALID, function (title) {
            self.setInvalid(v, title);
            self.fireEvent(BI.AnalysisHistoryTab.VALID_CHANGE)
        });

        tab.on(BI.AnalysisOperatorTitle.EVENT_SAVE, function (value, desc) {
            self.clickTitleSave(v, value, desc);
        });

        tab.on(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, function () {
            self.fireEvent(BI.AnalysisETLOperatorMergeSheetPane.MERGE_SHEET_CHANGE, arguments)
        });
        tab.on(BI.AnalysisOperatorTitle.EVENT_OPERATOR_CHANGE, function (v) {
            self.tempAddButton(v.getValue())
        });
        tab.on(BI.TopPointerSavePane.EVENT_CANCEL, function (v) {
            self.cancelTempAddButton()
        });
        tab.on(BI.MergeHistory.CANCEL, function () {
            self.selectLastTab()
        });
        return tab;
    },

    _selectLastTab: function () {
        var items = this.model.get(ETLCst.ITEMS);
        var validIndex = this.model.get('invalidIndex');
        this._selectTabByIndex(Math.min(validIndex, items.length - 1));
    },

    _selectTabByIndex: function (index) {
        var items = this.model.get(ETLCst.ITEMS);
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
        return this.model.get(ETLCst.ITEMS);
    },

    checkBeforeSave: function (table) {
        var v = this.tabButton.getValue()[0];
        var position = this.model.getIndexByValue(v);
        return this.model.checkBeforeSave(table, position);
    },

    addNewSheet: function (table) {
        this.cancelTempAddButton();
        var operator = ETLCst.ANALYSIS_TABLE_OPERATOR_KEY[table.etlType];
        var v = this.tabButton.getValue()[0];
        var position = this.model.getIndexByValue(v);
        var item = this.model.addItemAfter(operator, position, table);
        this._addNewButtonAfterPos(item, position);
        this.tab.setSelect(item["value"]);
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
        var items = this.model.get(ETLCst.ITEMS);
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
        var res = {};
        var table = {};
        table[ETLCst.ITEMS] = sheets;
        res["table"] = table;
        res["id"] = BI.UUID();
        res["name"] = value;
        res['describe'] = desc;
        BI.ETLReq.reqSaveTable(res, BI.emptyFn);
    },

    getOperatorTypeByValue: function (v) {
        var allHistoryId = this.tabButton.allHistoryId;
        if (v === allHistoryId) {
            return "bi.analysis_etl_merge_history";
        }
        return this.model.getOperatorType(v);
    },

    setInvalid: function (v, title) {
        var index = this.model.getIndexByValue(v);
        if (index <= this.model.get('invalidIndex')) {
            this.model.set('invalidIndex', index);
            this.model.set('invalidTitle', title);
            var items = this.model.get(ETLCst.ITEMS);
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
        if (index === this.model.get('invalidIndex')) {
            var items = this.model.get(ETLCst.ITEMS);
            for (var i = index; i < items.length; i++) {
                var btn = this.tabButton.getButton(items[i].value);
                btn.setValid(true);
                btn.setEnable(true);
                btn.setTitle(btn.getText());
            }
            this.model.set('invalidIndex', Number.MAX_VALUE);
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
        this.fireEvent(BI.AnalysisHistoryTab.VALID_CHANGE);
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
        var invalidIndex = this.model.get('invalidIndex');
        if (invalidIndex <= index) {
            button.setValid(false);
            button.setTitle(this.model.get("invalidTitle"))
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

    populate: function () {
        var items = this.model.get(ETLCst.ITEMS);
        var self = this;
        BI.each(items, function (idx, item) {
            self._addNewButtonAfterPos(item, idx);
        });
        self._selectLastTab();
        for (var i = this.model.get('invalidIndex'); i < items.length; i++) {
            var btn = this.tabButton.getButton(items[i].value);
            btn.setValid(false);
            btn.setTitle(this.model.get('invalidTitle'));
        }
    },

    selectLastTab: function () {
        this.tabButton.selectLastValue();
        this.tab.setSelect(this.tabButton.getValue([0]));
    }
});
BI.AnalysisHistoryTab.VALID_CHANGE = "VALID_CHANGE";
BI.shortcut("bi.analysis_history_tab", BI.AnalysisHistoryTab);