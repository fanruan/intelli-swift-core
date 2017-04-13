/**
 * Created by windy on 2017/4/10.
 */
BI.AnalysisETLOperatorAddColumnPane = BI.inherit(BI.Widget, {

    _constant : {
        SINGLE_COLUMN_CARD:"single_column",
        ALL_COLUMNS_CARD :"all_columns"
    },

    props: {
        baseCls:"bi-analysis-etl-operator-add-column-pane",
        value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.ADD_COLUMN,
    },

    render: function(){
        var self = this, o = this.options;
        this._editing = false;
        this.model = new BI.AnalysisETLOperatorAddColumnPaneModel(o.items);
        this.card = null;
        return {
            type:'bi.tab',
            ref: function(_ref){
                self.card = _ref;
            },
            cardCreator: BI.bind(this._createTabs, this)
        }
    },

    _createTabs: function (v) {
        var self = this;
        switch (v) {
            case this._constant.SINGLE_COLUMN_CARD:
                return BI.createWidget({
                        type: "bi.border",

                        items: {
                            north: {
                                type: "bi.layout",
                                height: 10
                            },
                            west: {
                                type: "bi.layout",
                                width: 10
                            },
                            east: {
                                type: "bi.layout",
                                width: 10
                            },
                            center: {
                                el: {
                                    type: "bi.vtape",
                                    scrollx: true,
                                    cls: "bi-analysis-etl-operator-add-column-single-pane",
                                    items: [{
                                        type: "bi.vtape",
                                        cls: "add-column-min-width",
                                        items: [{
                                            el: {
                                                type: ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + ETLCst.ANALYSIS_TABLE_PANE + ETLCst.ANALYSIS_TABLE_TITLE,
                                                ref: function (_ref) {
                                                    self.title = _ref;
                                                },
                                                listeners: [{
                                                    eventName: BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS,
                                                    action: function () {
                                                        self.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, arguments)
                                                    }
                                                }, {
                                                    eventName: BI.AnalysisETLOperatorAddColumnPaneTitle.EVENT_ADD_COLUMN_TYPE_CHANGE,
                                                    action: function (v) {
                                                        self.refreshOneConditionPane(v);
                                                    }
                                                }, {
                                                    eventName: BI.AnalysisETLOperatorAddColumnPaneTitle.EVENT_COLUMN_TYPE_CHANGE,
                                                    action: function () {
                                                        self.refreshOneConditionPaneViewIfNeeded();
                                                    }
                                                }]
                                            },
                                            height: 45
                                        }, {
                                            el: {
                                                type: "bi.button_group",
                                                items: [],
                                                layouts: [{
                                                    type: 'bi.vtape',
                                                    scrolly: false
                                                }],
                                                ref: function (_ref) {
                                                    self.oneConditionPane = _ref;
                                                }
                                            }
                                        }]
                                    }]
                                }
                            }
                        }
                    }
                );
            case this._constant.ALL_COLUMNS_CARD:
                return BI.createWidget({
                    type:ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + ETLCst.ANALYSIS_TABLE_PANE + "_all",
                    items: [],
                    ref: function(_ref){
                        self.allColumnsPane = _ref;
                    },
                    listeners: [{
                        eventName: BI.AnalysisETLOperatorAllColumnsPane.EVENT_NEW,
                        action: function(){
                            self.createNewAddColumn()
                        }
                    }, {
                        eventName: BI.AnalysisETLOperatorAllColumnsPane.EVENT_DELETE,
                        action: function(v){
                            self.deleteColumnByName(v);
                        }
                    }, {
                        eventName: BI.AnalysisETLOperatorAllColumnsPane.EVENT_EDIT,
                        action: function(v){
                            self.editColumnByName(v);
                        }
                    }]
                })
        }
    },

    cancelHandler : function () {
        this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true)
        return this.cancelColumn();
    },

    saveHandler : function (isEditing) {
        return this.saveColumn(isEditing);
    },

    _populate: function(){
        this._check();
        var cardName = this.getDefaultCardName();
        this.doCheck();
        this.card.setSelect(cardName);
        switch (cardName) {
            case this._constant.SINGLE_COLUMN_CARD:
                this.title.populate({}, {
                    columnNames: this._getAllColumnNames()
                })
                break;
            case this._constant.ALL_COLUMNS_CARD:
                this.allColumnsPane.populate(this.model.getAddColumns());
                break;
        }
        this.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, this.model, this.model.isValid() ? this.options.value.operatorType : ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR)
    },

    populate : function (m, options) {
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    },

    doCheck : function () {
        this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true)
    },

    _doModelCheck : function () {
        var found = this.model.check();
        if(found[0] === true) {
            this.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, found[1])
        }
        return found[0];
    },

    _check : function () {
        var found = this._doModelCheck()
        if (!found){
            this.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, this.model.createFields())
        } else {
            this.model.set(ETLCst.FIELDS, [])
        }
        this.fireEvent(BI.AnalysisETLOperatorAbstractController.VALID_CHANGE, !found);
    },

    getDefaultCardName : function () {
        this._editing = this.model.getAddColumns().length === 0;
        return this._editing ? this._constant.SINGLE_COLUMN_CARD : this._constant.ALL_COLUMNS_CARD;
    },

    createNewAddColumn : function () {
        this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true);
        this._editing = true;
        this.card.setSelect(this._constant.SINGLE_COLUMN_CARD);
        this.title.populate({},{
            columnNames: this._getAllColumnNames()
        });
    },

    deleteColumnByName : function (name) {
        this.model.deleteColumnByName(name);
        this._cancelEditColumn();
        this._doModelCheck();
        this.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, this.model, this.model.isValid() ? this.options.value.operatorType :  ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR)
        this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, this.model.getAddColumns().length !== 0, BI.i18nText('BI-Basic_Please') + BI.i18nText('BI-Add_Column'))
    },

    editColumnByName : function (name) {
        var column = this.model.getColumnByName(name);
        if(BI.isNull(column)){
            console.log("error can't find column : " + name)
            return;
        }
        this._editing = true;
        this._editColumnName = name;
        this.card.setSelect(this._constant.SINGLE_COLUMN_CARD);
        this.title.populate(column, {
            columnNames: this._getAllColumnNames(name)
        });
    },

    _isEditing : function () {
        return this._editing
    },

    cancelColumn : function () {
        if(this._isEditing()){
            if(this.model.getAddColumns().length === 0) {
                return false;
            }
            this._cancelEditColumn();
            return true;
        } else {
            return false;
        }
    },

    _cancelEditColumn : function () {
        this._editing = false;
        this.card.setSelect(this._constant.ALL_COLUMNS_CARD);
        this.allColumnsPane.populate(this.model.getAddColumns());
        this._editColumnName = null;
    },

    _saveColumn : function () {

        var column = this.title.update();
        var value = this.currentEditPane.update();
        column["item"] = value;
        if(BI.isNotNull(value.fieldType)) {
            column.fieldType = value.fieldType;
        }
        var isEdit = BI.isNotNull(this._editColumnName)
        if(isEdit){
            this.model.editColumn(column, this._editColumnName);
        } else {
            this.model.addColumn(column);
        }
        this._doModelCheck();
        this.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, this.model, this.model.isValid() ? this.options.value.operatorType :  ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR)
        this._cancelEditColumn();
        if(!isEdit) {
            this.card.getSelectedTab().scrollToEnd();
        }
    },

    saveColumn : function (editing) {
        if(editing !== true || !this._isEditing()) {
            return false;
        }
        this._saveColumn();
        this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, this.model.getAddColumns().length !== 0);
        return true;
    },

    _getAllColumnNames : function (name) {
        var columnNames = [];
        var parent = this.model.get(ETLCst.PARENTS)[0];
        BI.each(BI.concat(parent[ETLCst.FIELDS], this.model.getAddColumns()), function (idx, item) {
            if(item.fieldName !== name) {
                columnNames.push(item.fieldName)
            }
        })
        return columnNames;
    },

    refreshOneConditionPane : function (type) {
        var self = this;
        var parent = this.model.get(ETLCst.PARENTS)[0];
        var c = this.model.getColumnByName(this._editColumnName);
        var value = BI.isNotNull(c) ? c["item"] : null
        this.currentEditPane = BI.createWidget({
            type : ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + type
        })
        this.currentEditPane.on(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, function () {
            self.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, arguments)
        });
        this.oneConditionPane.populate([this.currentEditPane]);
        var column = this.title.update();
        var fields = {}
        fields[ETLCst.FIELDS] = parent[ETLCst.FIELDS];
        fields[ETLCst.PARENTS] = this.model.getCopyValue(ETLCst.PARENTS);
        this.currentEditPane.populate(BI.extend(fields, value), {
            fieldType : column.fieldType
        })
    },


    refreshOneConditionPaneViewIfNeeded : function () {
        if(BI.isNotNull(this.currentEditPane) && BI.isFunction(this.currentEditPane.changeFieldType)){
            var column = this.title.update();
            this.currentEditPane.changeFieldType(column.fieldType);
        }
    },

    getValue: function(){
        return this.model.update();
    }

})
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + ETLCst.ANALYSIS_TABLE_PANE, BI.AnalysisETLOperatorAddColumnPane);