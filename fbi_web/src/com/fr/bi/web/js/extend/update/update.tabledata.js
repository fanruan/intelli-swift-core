/**
 * Created by Young's on 2016/4/22.
 */
BI.UpdateTableData = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend(BI.UpdateTableData.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function(){
        BI.UpdateTableData.superclass._init.apply(this, arguments);
        this.model = new BI.UpdateTableDataModel({
            table: this.options.table
        })
    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: this.model.getTableName() + BI.i18nText("BI-Setting"),
            cls: "",
            textAlign: "left",
            lgap: 10,
            height: 50
        })
    },
    
    rebuildCenter: function (center) {
        var self = this;
        var tables = this.model.getSourceTables();
        var tableIds = this.model.getSourceTableIds();
        this.settings = {};
        if(tables.length === 1) {
            var tableId = tableIds[0];
            var setting = BI.createWidget({
                type: "bi.update_single_table_setting",
                element: center,
                table: this.model.getTableBySourceTableId(tableId),
                currentTable:self.model.table,
                update_setting: this.model.getUpdateSettingBySourceTableId(tableId)
            });
            setting.on(BI.UpdateSingleTableSetting.EVENT_OPEN_PREVIEW, function(){
                BI.Popovers.close(self.model.getId());
            });
            setting.on(BI.UpdateSingleTableSetting.EVENT_CLOSE_PREVIEW, function(){
                BI.Popovers.open(self.model.getId());
            });
            setting.on(BI.UpdateSingleTableSetting.EVENT_CUBE_SAVE, function(obj){
                self.fireEvent(BI.UpdateTableData.EVENT_CUBE_SAVE,obj);
            });
            this.settings[tableIds[0]] = setting;
        } else {
            var items = [];
            BI.each(tables, function(i, table){
                items.push({
                    text: table.table_name,
                    value: table.md5
                })
            });
            var tButtons = BI.createWidget({
                type: "bi.button_group",
                cls: "tables-group",
                width: 150,
                items: BI.createItems(items, {
                    type: "bi.text_button",
                    height: 30,
                    cls: "table-tab-button",
                    textAlign: "left",
                    hgap: 5
                }),
                layouts: [{
                    type: "bi.vertical"
                }]
            });

            var tab = BI.createWidget({
                type: "bi.tab",
                element: center,
                cls: "bi-update-table-data-center",
                direction: "custom",
                tab: tButtons,
                cardCreator: function(id) {
                    if(tableIds.contains(id)) {
                        var setting = BI.createWidget({
                            type: "bi.update_single_table_setting",
                            table: self.model.getTableBySourceTableId(id),
                            currentTable:self.model.table,
                            update_setting: self.model.getUpdateSettingBySourceTableId(id)
                        });
                        setting.on(BI.UpdateSingleTableSetting.EVENT_CUBE_SAVE, function(obj){
                            self.fireEvent(BI.UpdateTableData.EVENT_CUBE_SAVE,obj);
                        });
                        self.settings[id] = setting;
                        return BI.createWidget({
                            type: "bi.absolute",
                            items: [{
                                el: setting,
                                top: 0,
                                left: 150,
                                right: 0,
                                bottom: 0
                            }]
                        })
                    }
                }
            });
            tab.setSelect(tableIds[0]);
            //这个tab需要后放上去
            BI.createWidget({
                type: "bi.absolute",
                element: center,
                items: [{
                    el: tButtons,
                    top: 0,
                    left: 0,
                    bottom: 0
                }]

            });
        }
    },

    end: function(){
        this.fireEvent(BI.UpdateTableData.EVENT_SAVE);
    },

    getValue: function(){
        var settings = this.model.getAllSettings();
        BI.each(this.settings, function(id, setting){
            settings[id] = setting.getValue();
        });
        return settings;
    }
});

BI.UpdateTableData.EVENT_SAVE = "EVENT_SAVE";
BI.UpdateTableData.EVENT_CUBE_SAVE = "EVENT_CUBE_SAVE";
$.shortcut("bi.update_table_data", BI.UpdateTableData);
