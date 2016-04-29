/**
 * @class BI.PackageTablePane
 * @extend BI.Widget
 * 业务包选择表
 */
BI.PackageTablePane = BI.inherit(BI.Widget, {

    _constant: {
        TABLE_GAP: 10
    },

    _defaultConfig: function(){
        return BI.extend(BI.PackageTablePane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-package-table-pane"
        })
    },

    _init: function(){
        BI.PackageTablePane.superclass._init.apply(this, arguments);
        var self = this;
        var packId = this.options.packId;
        var wrapper = BI.createWidget({
            type: "bi.absolute",
            element: this.element
        });
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: self.element,
            text: BI.i18nText("BI-Loading")
        });
        Data.BufferPool.getTablesByPackId(packId, function(tables){
            mask.destroy();
            var tableItems = [];
            BI.each(tables, function(id, table){
                tableItems.push({
                    connName: table.connection_name,
                    text: self.getTableTranName(id, table),
                    value: BI.extend(table, {id: id})
                })
            });
            self.packageTables = BI.createWidget({
                type: "bi.button_group",
                cls: "package-tables",
                chooseType: BI.ButtonGroup.CHOOSE_TYPE_MULTI,
                items: BI.createItems(tableItems, {
                    type: "bi.database_table",
                    cls: "bi-table-ha-button"
                }),
                layouts: [{
                    type: "bi.left",
                    scrollable: true,
                    hgap: self._constant.TABLE_GAP,
                    vgap: self._constant.TABLE_GAP
                }]
            });
            self.packageTables.on(BI.ButtonGroup.EVENT_CHANGE, function(){
                self.fireEvent(BI.PackageTablePane.EVENT_CHANGE);
            });
            wrapper.addItem({
                el: self.packageTables,
                left: 0,
                right: 20,
                top: 20,
                bottom: 0
            });
        })
    },

    getTableTranName: function(id, table){
        var tableNameText = table.table_name;
        var translations = this.options.translations;
        //ETL 表
        if(table.connection_name === BICst.CONNECTION.ETL_CONNECTION){
            tableNameText = translations[id];
        } else if(BI.isNotNull(translations[id]) && translations[id] !== tableNameText){
            tableNameText = translations[id] + "(" + tableNameText + ")";
        }
        return tableNameText;
    },

    getValue: function(){
        return this.packageTables.getValue();
    }
});
BI.PackageTablePane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.package_table_pane", BI.PackageTablePane);