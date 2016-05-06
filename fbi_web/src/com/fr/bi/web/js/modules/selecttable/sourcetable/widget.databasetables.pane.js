/**
 * @class BI.DatabaseTablesPane
 * @extend BI.LoadingPane
 * 单个数据连接中所有表 tab
 */
BI.DatabaseTablesPane = BI.inherit(BI.LoadingPane, {

    _constant: {
        NORTH_HEIGHT: 60,
        PANE_GAP: 20
    },

    _defaultConfig: function(){
        return BI.extend(BI.DatabaseTablesPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-data-link-tables-pane"
        })
    },

    _init: function(){
        BI.DatabaseTablesPane.superclass._init.apply(this, arguments);
        this.allPageTables = [];
        //用于保存所有已选择的表，为了搜索
        this.selectedTables = [];
        this.wrapper = BI.createWidget({
            type: "bi.default",
            element: this.element
        })
    },

    populateTab: function(connName){
        var self = this;
        this.loading();
        Data.BufferPool.getTablesByConnectionName(connName, function(res){
            self.populate(connName, res);
            console.log(res);
            self.loaded();
        });
    },

    populate: function(connName, items){
        this.connectionName = connName;
        this.dataLinkTables = items;
        this.wrapper.empty();
        //TODO 服务器数据集
        if(connName === BI.i18nText("BI-Server_Data_Set")){
            this.wrapper.addItem({
                el: {
                    type: "bi.label",
                    text: BI.i18nText("BI-Server_Data_Set_Null"),
                    height: 30,
                    cls: "server-data-comment"
                },
                top: 100,
                left: 0,
                right: 0
            });
            return;
        }

        //对于连接失败的
        if(BI.isNotNull(items["__ERROR_MESSAGE__"])){
            this._createLinkFailPane(items["__ERROR_MESSAGE__"]);
            return;
        }

        //没有表的
        if(BI.isEmptyArray(items)) {
            return;
        }

        //两种 是否含有schema
        if(BI.isNotNull(items[0].schema)){
            //两个tab，一个使用combo控制，一个使用pager控制
            var comboItems = [];
            BI.each(items, function(i, item){
                comboItems.push({
                    text: item.schema,
                    value: item.schema
                })
            });
            this.schemaCombo = BI.createWidget({
                type: "bi.text_value_check_combo",
                items: comboItems,
                height: 28,
                width: 230
            });
            BI.createWidget({
                type: "bi.absolute",
                element: this.element,
                items: [{
                    el: {
                        type: "bi.left",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Mode"),
                            cls: "schema-mode",
                            height: 30
                        }, this.schemaCombo],
                        hgap: 10
                    },
                    top: -45,
                    left: 10
                }]
            });
            this.schemaCombo.setValue(this.dataLinkTables[0].schema);
            this.tab = BI.createWidget({
                type: "bi.tab",
                direction: "custom",
                tab: this.schemaCombo,
                defaultShowIndex: this.dataLinkTables[0].schema,
                cardCreator: BI.bind(this._createSchemaCard, this)
            });
        } else {
            this.allTables = items[0].tables;
            var pager = BI.createWidget({
                type: "bi.database_tables_pager",
                pages: this.allTables.length,
                height: 50
            });
            this.tab = BI.createWidget({
                type: "bi.tab",
                direction: "custom",
                tab: pager,
                defaultShowIndex: 1,
                cardCreator: BI.bind(this._createPageCard, this)
            });
            BI.createWidget({
                type: "bi.absolute",
                element: this.tab,
                items: [{
                    el: pager,
                    right: 30,
                    bottom: 0
                }]
            });
        }
        this.wrapper.addItem({
            el: this.tab,
            top: 0,
            left: 0,
            right: 0,
            bottom: 0
        });
    },

    _createSchemaCard: function(v){
        var self = this;
        BI.some(this.dataLinkTables, function(i, item){
            if(item.schema === v){
                self.allTables = item.tables;
                return true;
            }
        });
        var pager = BI.createWidget({
            type: "bi.database_tables_pager",
            pages: this.allTables.length,
            height: 50
        });

        var tab = BI.createWidget({
            type: "bi.tab",
            direction: "custom",
            tab: pager,
            defaultShowIndex: 1,
            cardCreator: BI.bind(this._createPageCard, this)
        });
        BI.createWidget({
            type: "bi.absolute",
            element: tab,
            items: [{
                el: pager,
                right: 10,
                bottom: 0
            }]
        });
        return tab;
    },

    _formatGroup: function(groups){
        var self = this;
        var formatGroup= [];
        var tables = this.options.tables;
        BI.each(groups, function(i, group){
            var item = {
                text: group.value,
                value: group
            };
            BI.some(tables, function(j, table){
                if(group.value === table.table_name &&
                    self.connectionName === table.connection_name){
                    item.extraCls = "package-table-selected";
                    return true;
                }
            });
            formatGroup.push(item)
        });
        return formatGroup;
    },

    _createPageCard: function(page){
        var self = this;
        var group = this.allTables[page - 1].group;
        var pageTables = BI.createWidget({
            type: "bi.button_group",
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_MULTI,
            items: BI.createItems(this._formatGroup(group), {
                type: "bi.database_table",
                cls: "bi-table-ha-button"
            }),
            layouts: [{
                type: "bi.left",
                scrollable: true,
                hgap: 10,
                vgap: 10
            }]
        });
        pageTables.on(BI.ButtonGroup.EVENT_CHANGE, function(value, ob){
            if(ob.isSelected()){
                self.selectedTables.push(value)
            } else {
                BI.some(self.selectedTables, function(i, v){
                    if(BI.isEqual(v, value)){
                        self.selectedTables.splice(i, 1);
                        return true;
                    }
                })
            }
            self.fireEvent(BI.DatabaseTablesPane.EVENT_CHANGE);
        });
        pageTables.setValue(this.selectedTables);
        this.allPageTables.push(pageTables);
        return BI.createWidget({
            type: "bi.vtape",
            items: [{
                el: pageTables,
                height: "fill"
            }, {
                el: {
                    type: "bi.default"
                },
                height: 50
            }]
        });
    },

    _createLinkFailPane: function(message){
        var self = this;
        self.wrapper.empty();
        var detailButton = BI.createWidget({
            type: "bi.text_button",
            text: BI.i18nText("BI-Detail_Info"),
            cls: "test-connection-fail-detail-button",
            handler: function(){
                if(detailButton.getText() === BI.i18nText("BI-Detail_Info")){
                    detailButton.setText(BI.i18nText("BI-Close_Detail_Info"));
                    detailInfo.setVisible(true);
                } else {
                    detailButton.setText(BI.i18nText("BI-Detail_Info"));
                    detailInfo.setVisible(false);
                }
            }
        });
        var retryButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Re_Connection"),
            level: "ignore",
            width: 90,
            height: 28,
            handler: function(){
                self._reConnectionLink();
            }
        });
        var detailInfo = BI.createWidget({
            type: "bi.left",
            width: 498,
            height: 100,
            cls: "test-fail-detail-info",
            items: [{
                type: "bi.label",
                text: message,
                whiteSpace: "normal",
                textAlign: "left",
                textHeight: 20
            }],
            hgap: 10
        });
        detailInfo.setVisible(false);

        self.wrapper.addItem({
            type: "bi.center_adapt",
            items: [{
                type: "bi.vertical",
                cls: "test-fail-pane",
                items: [{
                    type: "bi.center_adapt",
                    cls: "data-link-test-fail-icon",
                    items: [{
                        type: "bi.icon",
                        width: 126,
                        height: 126
                    }]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Test_Connection_Failed"),
                    cls: "test-connection-fail-comment"
                }, {
                    type: "bi.horizontal_float",
                    items: [{
                        type: "bi.horizontal",
                        items: [detailButton, retryButton],
                        hgap: 5
                    }],
                    height: 30
                }, detailInfo],
                width: 500,
                height: 340,
                vgap: 10
            }],
            top: 0,
            left: 0,
            right: 0,
            bottom: 0
        });
    },

    _reConnectionLink: function(){
        var self = this;
        this.loading();
        Data.BufferPool.getTablesByConnectionName(this.connectionName, function(res){
            self.populate(self.connectionName, res);
            self.loaded();
        });
    },

    getValue: function(){
        var self = this;
        var allSelectedTablesDetail = [];
        BI.each(this.selectedTables, function(i, tableName){
            allSelectedTablesDetail.push({
                table_name: tableName.value,
                schema_name: tableName.schema || "null",
                table_name_text: tableName.value,
                connection_name: self.connectionName
            })
        });
        return allSelectedTablesDetail;
    },

    setValue: function(v){
        BI.each(this.allPageTables, function(i, pageTables){
            pageTables.setValue(v);
        });
    },

    getAllTables: function(){
        var self = this;
        if(BI.isNotNull(this.schemaCombo)){
            var index = 0;
            BI.some(this.dataLinkTables, function(i, tables){
                if(tables.schema === self.schemaCombo.getValue()[0]){
                    index = i;
                    return true;
                }
            });
            return this.dataLinkTables[index];
        }
        return this.dataLinkTables[0];
    },

    getConnectionName: function(){
        return this.connectionName;
    },

    setComboEnable: function(v){
        BI.isNotNull(this.schemaCombo) && this.schemaCombo.setEnable(v);
    },

    getSelectedTables: function(){
        return this.selectedTables;
    },

    setSelectedTables: function(selectedTables){
        this.selectedTables = selectedTables;
    },

    refreshSelectedTables: function(){
        var self = this;
        BI.each(this.allPageTables, function(i, pageTables){
            pageTables.setValue(self.selectedTables);
        });
    }

});
BI.DatabaseTablesPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.database_tables_pane", BI.DatabaseTablesPane);
