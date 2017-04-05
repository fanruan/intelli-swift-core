/**
 * @class BI.SelectDataLevel8Node
 * @extends BI.NodeButton
 */
BI.SelectDataLevel8Node = FR.extend(BI.NodeButton, {

    _constant: {
        DELETE_WARNING: 1,
        USED_BY_TEMPLATE: 2,
        USED_BY_TABLE: 3,
        EDIT_WARNING: 4
    },

    _defaultConfig: function () {
        return BI.extend(BI.SelectDataLevel8Node.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-select-data-level8-node bi-list-item",
            id: "",
            pId: "",
            open: false,
            height: 25,
            iconWidth:13,
            iconHeight:13
        })
    },

    _initControl : function () {
        this.controller = new BI.SelectDataLevel8NodeController();
        this.controller.setWidget(this);
    },

    _init: function () {
        this.options.title = BI.Utils.getDescribe(this.options.id) || this.options.title;
        BI.SelectDataLevel8Node.superclass._init.apply(this, arguments);
        var self = this, o = this.options, c = this._constant;
        self._initControl();
        this.checkbox = BI.createWidget({
            type: "bi.tree_group_node_checkbox",
            iconWidth: o.iconWidth,
            iconHeight: o.iconHeight
        })
        this.icon = BI.createWidget({
            type:"bi.center_adapt",
            cls : "icon-analysis-table",
            height: o.height,
            items : [BI.createWidget ({
                type : "bi.icon",
                width: o.iconWidth,
                height: 14
            })]
        })
        this.text = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            whiteSpace: "nowrap",
            textHeight: o.height,
            height: o.height,
            hgap: o.hgap,
            text: o.text,
            value: o.value,
            py: o.py
        });
        this.settingIcon = BI.createWidget({
            type: "bi.down_list_combo",
            height: o.height,
            iconCls: "icon-analysis-table-set",
            items: this._createItemList()
        })

        this.settingIcon.on(BI.DownListCombo.EVENT_CHANGE, function(v){
            self.afterClickList(v, o);
        });

        this.settingIcon.element.click(function(e){
            e.stopPropagation();
        })

        this.confirmCombo = BI.createWidget({
            type: "bi.bubble_combo",
            el: {},
            element: this.settingIcon,
            popup: {
                type: "bi.bubble_bar_popup_view",
                buttons: [{
                    value: BI.i18nText(BI.i18nText("BI-Basic_Sure")),
                    handler: function () {
                        self.confirmCombo.hideView();
                        if(self.popupTab.getSelect() === c.EDIT_WARNING){
                            BI.createWidget({
                                type: "bi.analysis_etl_main",
                                element: BI.Layers.create(ETLCst.ANALYSIS_LAYER, "body"),
                                model: self.res
                            })
                        }else{
                            BI.ETLReq.reqDeleteTable({id: o.id}, BI.emptyFn)
                        }
                    }
                }, {
                    value: BI.i18nText("BI-Basic_Cancel"),
                    level: "ignore",
                    handler: function () {
                        self.confirmCombo.hideView();
                    }
                }],
                el: {
                    direction: "custom",
                    type: "bi.tab",
                    ref: function(_ref){
                        self.popupTab = _ref;
                    },
                    cardCreator: BI.bind(this._createTabs, this)
                },
                minHeight: 140,
                maxHeight: 340,
                minWidth: 340
            }
        });

        this.tip = BI.createWidget({
            type: "bi.label",
            cls: "select-data-selected-count-label",
            whiteSpace: "nowrap",
            width: 25,
            height: o.height
        });
        this.checkbox.on(BI.Controller.EVENT_CHANGE, function (type) {
            if(type ===  BI.Events.CLICK) {
                self.setSelected(self.isSelected());
            }
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        })
        this.loadingBar = BI.createWidget({
            type: "bi.analysis_progress",
            width:220,
            height:2
        })
        this.loadingBar.setPercent(0);
        BI.createWidget({
            type: "bi.htape",
            element: this,
            items: [{
                width: 23,
                el: this.checkbox
            }, {
                width : 23,
                el: this.icon
            },{
                el: this.text
            }, {
                el : this.settingIcon,
                width : 23
            },{
                width: 25,
                el: this.tip
            }]
        })
        BI.createWidget({
            type: "bi.absolute",
            element: this,
            items: [{
                el : self.loadingBar,
                left : 10,
                right : 10,
                top :0,
                bottom : 15
            }]
        })
        self.settingIcon.setVisible(BI.Utils.isTableEditable(this.options.id));
        self.loadingBar.setVisible(false);
        self.controller.startChecker(this.options.id);
    },

    _createItemList : function (){
        return [[{
            text: BI.i18nText("BI-Basic_Edit"),
            value:ETLCst.ANALYSIS_TABLE_SET.EDIT
        }], [{
            text: BI.i18nText("BI-Basic_Rename"),
            value:ETLCst.ANALYSIS_TABLE_SET.RENAME
        }], [{
            text: BI.i18nText("BI-Basic_Copy"),
            value:ETLCst.ANALYSIS_TABLE_SET.COPY
        }], [{
            text: BI.i18nText("BI-Basic_Remove"),
            value:ETLCst.ANALYSIS_TABLE_SET.DELETE
        }]];
    },

    _createTabs: function(v){
        var self = this, o = this.options;
        var c = this._constant;
        switch (v) {
            case c.EDIT_WARNING:
                return BI.createWidget({
                    type: "bi.vertical_adapt",
                    items:[{
                        type: "bi.label",
                        whiteSpace: "normal",
                        text: BI.i18nText("BI-Current_Table_Is_Used_By_Other_Confirm_To_Continue"),
                        cls: "delete-label",
                        textAlign: "left",
                        width: 300,
                        height: 100
                    }],
                    width: 300,
                    height: 100,
                    hgap: 20
                });
            case c.DELETE_WARNING:
                return BI.createWidget({
                    type: "bi.vertical_adapt",
                    items:[{
                        type: "bi.label",
                        whiteSpace: "normal",
                        text: BI.i18nText("BI-Is_Sure_Delete_ETL_Table", o.text),
                        cls: "delete-label",
                        textAlign: "left",
                        width: 300,
                        height: 100
                    }],
                    width: 300,
                    height: 100,
                    hgap: 20
                });
            case c.USED_BY_TABLE:
                return BI.createWidget({
                    type: "bi.button_group",
                    items: [],
                    layouts: [{
                        type: "bi.vertical"
                    }]
                });
            case c.USED_BY_TEMPLATE:
                return BI.createWidget({
                    type: "bi.vtape",
                    items: [{
                        el: {
                            type: "bi.label",
                            whiteSpace: "normal",
                            text: BI.i18nText("BI-ETL_Analysis_Is_Used"),
                            cls: "delete-label",
                            textAlign: "left",
                            width: 300
                        },
                        height: 50
                    }, {
                        type: "bi.preview_table",
                        width: 300,
                        ref: function(_ref){
                            self.preViewTable = _ref;
                        }
                    }],
                    hgap: 20,
                    height: 100
                })
        }
    },

    afterClickList: function (v, option) {
        var self = this, c = this._constant;
        this.res = null;
        switch (v) {
            case ETLCst.ANALYSIS_TABLE_SET.EDIT :
                BI.ETLReq.reqEditTable({id: option.id}, function (res) {
                    if (res['used'] || BI.keys(res["usedTemplate"]).length > 0) {
                        self.res = res;
                        self.confirmCombo.showView();
                        self.popupTab.setSelect(c.EDIT_WARNING);
                    } else {
                        BI.createWidget({
                            type: "bi.analysis_etl_main",
                            element: BI.Layers.create(ETLCst.ANALYSIS_LAYER, "body"),
                            model: res
                        })
                    }
                });
                return;
            case ETLCst.ANALYSIS_TABLE_SET.RENAME :
                self.controller._showRenamePop(option.id, option.text);
                return;
            case ETLCst.ANALYSIS_TABLE_SET.DELETE :
                BI.ETLReq.reqEditTable({id: option.id}, function (res) {
                    if (BI.keys(res["usedTemplate"]).length > 0) {
                        self.res = res;
                        self.confirmCombo.showView();
                        self.popupTab.setSelect(c.USED_BY_TEMPLATE);
                        var tableItems = [];
                        BI.each(res["usedTemplate"], function(tName, widgets){
                            BI.each(widgets, function(idx, name){
                                tableItems.push([{text: tName, textAlign: "center"}, {text: name, textAlign: "center"}]);
                            });
                        });
                        var tableHeader = [[{text: BI.i18nText("BI-ETL_Template_Name")}, {text: BI.i18nText("BI-ETL_Widget_Name")}]]
                        self.preViewTable.populate(tableItems, tableHeader);
                        self.popupTab.getSelectedTab().resize();
                    } else if(res['used']){
                        self.popupTab.setSelect(c.USED_BY_TABLE);
                        var items = res["usedTables"] || [];
                        self.res = res;
                        if(BI.isEmptyArray(items) || items.length === 1){
                        }else{
                            self.confirmCombo.showView();
                            var showItems = [];
                            BI.each(items, function(idx, tId){
                                if(tId !== option.id){
                                    var tableName = BI.Utils.getTableNameByID(tId);
                                    showItems.push({
                                        type: "bi.label",
                                        text: tableName,
                                        title: tableName,
                                        cls: "delete-label",
                                        textAlign: "center",
                                        width: 360
                                    });
                                }
                            });
                        }
                        self.popupTab.getSelectedTab().populate(BI.concat([{
                            type: "bi.label",
                            whiteSpace: "normal",
                            text: BI.i18nText("BI-ETL_Sure_Delete_Used_Table"),
                            cls: "delete-label",
                            textAlign: "center",
                            width: 360
                        }], items));
                    } else {
                        self.confirmCombo.showView();
                        self.popupTab.setSelect(c.DELETE_WARNING);
                    }
                });
                return;
            case ETLCst.ANALYSIS_TABLE_SET.COPY :
                BI.ETLReq.reqSaveTable({
                    id: option.id,
                    new_id: BI.UUID(),
                    name: BI.Utils.createDistinctName(BI.Utils.getAllETLTableNames(), option.text)
                }, BI.emptyFn);
                return;
        }

    },

    showLoading : function (percent) {
        var self = this;
        if (percent !== 1){
            this.loadingBar.setVisible(true);
        } else {
            BI.delay(function () {
                self.loadingBar.setVisible(false)
            }, 600)
        }
        this.loadingBar.setPercent(percent);
    },

    setEnable: function (b) {
        BI.assert(b, [true, false]);
        if (b === true) {
            this.options.disabled = false;
        } else if (b === false) {
            this.options.disabled = true;
        }
        this.checkbox.setEnable(b);
        this.text.setEnable(b);
        this.icon.setEnable(b);
        this.tip.setEnable(b);
    },

    doRedMark: function () {
        this.text.doRedMark.apply(this.text,  this.controller.getMarkArguments(this.options.id, this.options.text));
    },

    unRedMark: function () {
        this.text.unRedMark.apply(this.text, arguments);
    },

    doClick: function () {
        BI.SelectDataLevel8Node.superclass.doClick.apply(this, arguments);
        this.checkbox.setSelected(this.isOpened());
    },

    setOpened: function (v) {
        BI.SelectDataLevel8Node.superclass.setOpened.apply(this, arguments);
        this.checkbox.setSelected(v);
    },

    setValue: function (items) {
        BI.SelectDataLevel8Node.superclass.setValue.apply(this, arguments);
        if(BI.isEmpty(items)){
            this.tip.setText("");
        } else {
            this.tip.setText("(" + items.length + ")");
        }
        this.tip.setTitle(items.toString());
    }
});
BI.shortcut("bi.select_data_level" + ETLCst.BUSINESS_TABLE_TYPE.ANALYSIS_TYPE + "_node", BI.SelectDataLevel8Node);