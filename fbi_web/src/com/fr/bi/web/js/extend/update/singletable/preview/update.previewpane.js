/**
 * Created by Young's on 2016/4/25.
 */
BI.UpdatePreviewPane = BI.inherit(BI.BarPopoverSection, {
    _constants: {
        SHOW_RESULT: 1,
        SHOW_SQL: 2,

        PART_ADD: 1,
        PART_DELETE: 2,
        PART_MODIFY: 3
    },

    _defaultConfig: function(){
        return BI.extend(BI.UpdatePreviewPane.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function(){
        BI.UpdatePreviewPane.superclass._init.apply(this, arguments);
        this.model = new BI.UpdatePreviewPaneModel({
            table: this.options.table
        })
    },

    _createPane: function(v) {
        switch (v) {
            case this._constants.SHOW_RESULT:
                this.warningContainer = BI.createWidget({
                    type: "bi.vertical"
                });
                this.previewTable = BI.createWidget({
                    type: "bi.preview_table",
                    items: [],
                    header: []
                });
                return BI.createWidget({
                    type: "bi.absolute",
                    items: [{
                        el: {
                            type: "bi.vertical",
                            cls: "preview-content",
                            items: [this.warningContainer, this.previewTable]
                        },
                        top: 29,
                        left: 0,
                        bottom: 0,
                        right: 0
                    }]
                });
            case this._constants.SHOW_SQL:
                this.sqlContainer = BI.createWidget({
                    type: "bi.vertical"
                });
                return BI.createWidget({
                    type: "bi.absolute",
                    items: [{
                        el: {
                            type: "bi.vertical",
                            cls: "preview-content",
                            items: [this.sqlContainer]
                        },
                        top: 29,
                        left: 0,
                        bottom: 0,
                        right: 0
                    }]
                });
        }
    },

    rebuildNorth: function(north){
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: this.model.getTableName() + BI.i18nText("BI-Preview"),
            height: 50,
            textAlign: "left"
        })
    },

    rebuildCenter: function(center) {
        var buttons = BI.createWidget({
            type: "bi.button_group",
            height: 30,
            items: BI.createItems([{
                text: BI.i18nText("BI-Execute_Result"),
                value: this._constants.SHOW_RESULT
            }, {
                text: BI.i18nText("BI-Execute_Sql"),
                value: this._constants.SHOW_SQL
            }], {
                type: "bi.text_button",
                cls: "tab-button",
                height: 28,
                width: 120
            }),
            layouts: [{
                type: "bi.left",
                rgap: 2
            }]
        });

        this.tab = BI.createWidget({
            type: "bi.tab",
            element: center,
            cls: "bi-update-preview-pane",
            direction: "custom",
            defaultShowIndex: this._constants.SHOW_SQL,
            tab: buttons,
            cardCreator: BI.bind(this._createPane, this)
        });
        this.tab.setSelect(this._constants.SHOW_RESULT);
        BI.createWidget({
            type: "bi.absolute",
            element: this.tab,
            items: [{
                el: buttons,
                top: 0,
                left: 0,
                right: 0
            }]
        })
    },

    rebuildSouth: function(south) {
        var self = this;
        BI.createWidget({
            type: "bi.right",
            element: south,
            items: [{
                type: "bi.vertical_adapt",
                items: [{
                    type: "bi.button",
                    text: BI.i18nText("BI-Sure"),
                    height: 30,
                    handler: function(){
                        self.close();
                    }
                }],
                hgap: 5,
                height: "100%"
            }]
        })
    },

    close: function(){
        this.fireEvent(BI.UpdatePreviewPane.EVENT_CHANGE);
    },

    populate: function(sql, type) {
        var self = this;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: this.tab,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.getUpdatePreviewSqlResult({sql: sql, table: this.options.table}, function(res){
            var data = res.data, fieldNames = res.field_names, sql = res.sql, error = res.error;
            //sql
            self.sqlContainer.empty();
            self.sqlContainer.addItem({
                type: "bi.label",
                text: sql,
                cls: "sql-statement",
                whiteSpace: "normal",
                textAlign: "left",
                textHeight: 20,
                hgap: 10
            });

            self.warningContainer.empty();
            //warning
            if(BI.isNull(data) || BI.isNull(fieldNames)) {
                self.previewTable.populate([], []);
                self.warningContainer.addItem({
                    type: "bi.label",
                    text: BI.i18nText("BI-Cannot_Execute_Sql") + ":" + error,
                    cls: "warning-comment",
                    textAlign: "left",
                    whiteSpace: "normal",
                    hgap: 10,
                    textHeight: 20
                });
            } else {
                var lackFields = self.model.getLackFields(fieldNames);
                var extraFields = self.model.getExtraFields(fieldNames);
                if(lackFields.length > 0) {
                    self.warningContainer.addItem({
                        type: "bi.label",
                        text: "1、" + BI.i18nText("BI-Sql_Result_Less_Cube") + lackFields,
                        cls: "warning-comment",
                        whiteSpace: "normal",
                        textAlign: "left",
                        textHeight: 20,
                        hgap: 10
                    })
                }
                if(extraFields.length > 0) {
                    self.warningContainer.addItem({
                        type: "bi.label",
                        text: "2、" + BI.i18nText("BI-Cube_Less_Sql_Result") + extraFields + BI.i18nText("BI-Fields_Wonnot_Action_Update"),
                        cls: "warning-comment",
                        whiteSpace: "normal",
                        textAlign: "left",
                        textHeight: 20,
                        hgap: 10
                    })
                }
                var header = [], items = [];
                BI.each(fieldNames, function(i, fieldName){
                    header.push({
                        text: fieldName
                    })
                });
                BI.each(data, function(i, row){
                    var item = [];
                    BI.each(row, function(j, cell){
                        item.push({
                            text: cell
                        });
                    });
                    items.push(item);
                });
                self.previewTable.populate(items, [header]);
            }
            mask.destroy();
        })
    }
});
BI.UpdatePreviewPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.update_preview_pane", BI.UpdatePreviewPane);