/**
 * @class BI.ETLTablePreview
 * @extend BI.BarPopoverSection
 * etl表数据预览
 */
BI.ETLTablePreview = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend(BI.ETLTablePreview.superclass._defaultConfig.apply(this, arguments), {

        });
    },

    _init: function(){
        BI.ETLTablePreview.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function(north){
        var table = this.options.table;
        var tableName = table.table_name;
        if(BI.isNull(tableName)){
            tableName = this._getTableName(table);
        }
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: tableName,
            textAlign: "left",
            height: 50
        })
    },

    _getTableName: function(table){
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
        getDefaultName(table.tables);
        //反向遍历
        tableName.reverse();
        var tableNameString = "";
        BI.each(tableName, function(i, name){
            tableNameString += name;
        });
        return tableNameString;
    },

    rebuildCenter: function(center){
        var table = this.options.table;
        BI.createWidget({
            type: "bi.etl_table_preview_center",
            table: table,
            element: center
        });
    },

    rebuildSouth: function(south){
        var self = this;
        BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "bi-etl-table-preview-south",
            element: south,
            items: {
                left: [{
                    type: "bi.label",
                    cls: "preview-comment",
                    text: BI.i18nText("BI-Table_Data_Preview_Comment"),
                    height: 30
                }],
                right: [{
                    type: "bi.button",
                    level: "ignore",
                    text: BI.i18nText("BI-Close"),
                    height: 30,
                    handler: function(){
                        self.close();
                    }
                }]
            }

        })
    }

});
$.shortcut("bi.etl_table_preview", BI.ETLTablePreview);