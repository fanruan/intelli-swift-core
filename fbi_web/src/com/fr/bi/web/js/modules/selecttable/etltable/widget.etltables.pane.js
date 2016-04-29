/**
 * @class BI.SelectTableETLPane
 * @extend BI.Widget
 * 选择表的etl流
 */
BI.SelectTableETLPane = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.SelectTableETLPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-table-etl-pane"
        })
    },

    _init: function(){
        BI.SelectTableETLPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var allTables = o.etl;
        this.selectedTables = [];
        var items = [];
        BI.each(allTables, function(i, tables){
            self.tItems = [];
            self._itemCreator(tables);
            items.push({
                type: "bi.branch_relation",
                items: self.tItems,
                centerOffset: -30
            })
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.float_center_adapt",
                    cls: "etl-tables",
                    scrollable: true,
                    items: items
                },
                top: 20,
                right: 20,
                left: 0,
                bottom: 0
            }]
        });
    },

    _itemCreator: function(tables, pId){
        var self = this, o = this.options;
        var currentId = o.currentId;
        BI.each(tables, function(i, table){
            var tableName = table.table_name;
            BI.isNull(tableName) && (tableName = self._getETLTableNameByTable(table));
            var tableInfo = BI.extend(table, {
                table_name: tableName,
                isFinal: BI.isNull(pId)
            });

            var etlTable = BI.createWidget({
                type: "bi.etl_flow_chart_button",
                tableInfo: tableInfo
            });
            if(currentId === table.id){
                etlTable.setEnable(false);
            }
            etlTable.on(BI.ETLFlowChartButton.EVENT_CHANGE, function(v){
                if(v === true){
                    self.selectedTables.push(tableInfo);
                } else {
                    BI.some(self.selectedTables, function(i, selectedTable){
                        if(tableInfo.id === selectedTable.id){
                            self.selectedTables.splice(i, 1);
                            return true;
                        }
                    });
                }
                self.fireEvent(BI.SelectTableETLPane.EVENT_CHANGE);
            });
            self.tItems.push({
                pId: pId,
                id: table.id,
                value: tableInfo,
                el: etlTable,
                width: etlTable.getWidth(),
                height: etlTable.getHeight()
            });
            if(BI.isNotNull(table.tables)){
                self._itemCreator(table.tables, table.id);
            }
        })
    },

    _getETLTableNameByTable: function(table){
        var tables = table.tables;
        var tableName = [];
        function getDefaultName(tables){
            //只取tables[0]
            if(BI.isNotNull(tables[0].etl_type)){
                tableName.push("_" + tables[0].etl_type);
                getDefaultName(tables[0].tables);
            } else {
                tableName.push(tables[0].table_name);
            }
        }
        BI.isNotNull(tables) && getDefaultName(tables);
        //反向遍历
        tableName.reverse();
        var tableNameString = "";
        BI.each(tableName, function(i, name){
            tableNameString += name;
        });
        return tableNameString + "_" + table.etl_type;
    },

    getValue: function(){
        return this.selectedTables;
    }
});
BI.SelectTableETLPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.select_table_etl_pane", BI.SelectTableETLPane);