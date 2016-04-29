/**
 * @class BI.DatabaseTablesSearchResultPane
 * @extend BI.Widget
 * 数据库表的搜索结果面板
 */
BI.DatabaseTablesSearchResultPane = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.DatabaseTablesSearchResultPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-database-tables-search-result-pane"
        })
    },

    _init: function(){
        BI.DatabaseTablesSearchResultPane.superclass._init.apply(this, arguments);
        this.allPageTables = [];
        this.resultPane = BI.createWidget({
            type: "bi.absolute",
            element: this.element
        });
    },

    populate: function (searchResult, matchResult, keyword, selectedTables, connectionName) {
        var self = this;
        this.keyword = keyword;
        this.selectedTables = selectedTables;
        this.connectionName = connectionName;
        this.resultPane.empty();
        this.allTables = searchResult;
        var pager = BI.createWidget({
            type: "bi.database_tables_pager",
            pages: Math.ceil(this.allTables.length/100),
            height: 50
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: pager,
                right: 30,
                bottom: 0
            }]
        });
        this.tab = BI.createWidget({
            type: "bi.tab",
            direction: "custom",
            tab: pager,
            defaultShowIndex: 1,
            cardCreator: BI.bind(this._createPageCard, this)
        });
        this.resultPane.addItem({
            el: this.tab,
            top: 0,
            left: 0,
            right: 0,
            bottom: 50
        });
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
        var group = this.allTables.slice((page - 1) * 100, page*100);
        var pageTables = BI.createWidget({
            type: "bi.button_group",
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_MULTI,
            behaviors: {
                redmark: function(){
                    return true;
                }
            },
            layouts: [{
                type: "bi.left",
                scrollable: true,
                hgap: 10,
                vgap: 10
            }]
        });
        var items = BI.createItems(this._formatGroup(group), {
            type: "bi.database_table",
            cls: "bi-table-ha-button"
        });
        pageTables.populate(items, self.keyword);
        pageTables.setValue(self.selectedTables);
        pageTables.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
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

    empty: function () {
        this.resultPane.empty();
    }
});
$.shortcut("bi.database_tables_search_result_pane", BI.DatabaseTablesSearchResultPane);