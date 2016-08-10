/**
 * Created by roy on 16/4/26.
 */
BI.FieldRelationSettingWithPreviewPopup = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function () {
        return BI.extend(BI.FieldRelationSettingWithPreviewPopup.superclass._defaultConfig.apply(this, arguments), {
            wId: ""
        })
    },

    _init: function () {
        var o = this.options;
        BI.FieldRelationSettingWithPreviewPopup.superclass._init.apply(this, arguments);
        this.model = new BI.FieldRelationSettingWithPreviewPopupModel({
            wId: o.wId
        })

    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Field_Relation_Setting"),
            textAlign: "left",
            height: 50
        });
        return true
    },


    rebuildSouth: function (south) {
        var self = this;
        BI.FieldRelationSettingWithPreviewPopup.superclass.rebuildSouth.apply(this, arguments);
        this.sure.setText(BI.i18nText("BI-Save_Config"));
        this.sure.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.FieldRelationSettingWithPreviewPopup.EVENT_CHANGE)
        });
    },

    rebuildCenter: function (center) {
        var self = this;
        var fieldLabel = BI.createWidget({
            type: "bi.label",
            textHeight: 30,
            textAlign: "left",
            value: this.model.getLabelItem(),
            cls: "detail-table-path-setting-field-label"
        });

        var tipLabel = BI.createWidget({
            type: "bi.label",
            textHeight: 35,
            textAlign: "left",
            cls: "detail-table-path-setting-tip-label",
            value: BI.i18nText("BI-Detail_Setting_Path")
        });


        this.combo = BI.createWidget({
            type: "bi.detail_table_path_setting_combo"
        });

        this.combo.on(BI.DetailTablePathSettingCombo.EVENT_CHANGE, function () {
            self.model.setSelectedForeignTable(self.combo.getValue()[0]);
            self.model.refreshChoosePathMap();
            self.tree.populate();
            self._createTabs();
        });


        var tipMultiPathLabel = BI.createWidget({
            type: "bi.label",
            textHeight: 35,
            textAlign: "left",
            cls: "detail-table-path-setting-tip-label",
            value: BI.i18nText("BI-Detail_Setting_Path_Multi_Path_Exist")
        });

        this.tabPane = BI.createWidget({
            type: "bi.vertical",
            vgap: 10
        });

        this.tree = BI.createWidget({
            type: "bi.sync_tree",
            cls: "popup-view-tree",
            itemsCreator: function (op, callback) {
                var data = BI.extend({
                    floors: BI.size(self.model.getDimensions()),
                    type: BICst.TREE.TREE_REQ_TYPE.INIT_DATA
                }, op);
                var wId = self.model.getWidgetId();
                BI.Utils.getWidgetDataByWidgetInfo(self._createPreviewDimensions(), BI.Utils.getWidgetViewByID(wId), function (jsonData) {
                    callback(jsonData);
                }, {
                    type: BI.Utils.getWidgetTypeByID(wId),
                    settings: BI.Utils.getWidgetSettingsByID(wId),
                    page: -1,
                    tree_options: data,
                    id: wId
                });
            }
        });

        this.previewPane = BI.createWidget({
            type: "bi.vertical",
            cls: "preview-pane",
            items: [this.tree]
        });

        this.popup = BI.createWidget({
            type: "bi.vertical",
            element: center,
            vgap: 10,
            items: [
                fieldLabel,
                tipLabel,
                this.combo,
                tipMultiPathLabel,
                this.tabPane
            ]
        });

        this.popup = BI.createWidget({
            type: "bi.htape",
            element: center,
            hgap: 10,
            cls: "field-relation-setting-with-preview-popup",
            items: [{
                el: {
                    type: "bi.vertical",
                    vgap: 10,
                    items: [
                        fieldLabel,
                        tipLabel,
                        this.combo,
                        tipMultiPathLabel,
                        this.tabPane
                    ]
                }
            }, {
                el: {
                    type: "bi.vtape",
                    items: [{
                        el: {
                            type: "bi.label",
                            value: BI.i18nText("BI-Preview"),
                            textHeight: 40
                        },
                        height: 40

                    }, {
                        el: this.previewPane
                    }]
                },
                width: 210
            }]
        });

        this.populate();
    },

    _createPreviewDimensions: function () {
        var self = this;
        var wId = this.model.getWidgetId();
        var dimensions = BI.Utils.getWidgetDimensionsByID(wId);
        BI.each(dimensions, function (dId, dim) {
            var relationItem = self.model.getValue();
            var target_relation = relationItem.target_relation;
            var targetTableId = relationItem.targetTableId;
            var dimension_map = {};
            dimension_map[targetTableId] = {};
            dimension_map[targetTableId].target_relation = target_relation[dId];
            dim.dimension_map = dimension_map;
        });
        return dimensions;


    },

    _createTabs: function () {
        var self = this;
        var dimensions = this.model.getDimensions();
        var foreignTable = this.model.getSelectedForeignTable();
        this.tabs = {};
        BI.each(dimensions, function (i, dId) {
            var primaryTable = BI.Utils.getTableIDByDimensionID(dId);
            var paths = BI.Utils.getPathsFromTableAToTableB(primaryTable, foreignTable);
            var choosePath = self.model.getChoosePathFromDid(dId);
            self.tabs[dId] = BI.createWidget({
                type: "bi.detail_table_path_setting_tab",
                paths: paths,
                dId: dId,
                choosePath: choosePath
            });
            self.tabs[dId].on(BI.DetailTablePathSettingTab.EVENT_CHANGE, function (relations) {
                self.model.setChoosePath(dId, relations);
                self.tree.populate();
            })
        });
        this.tabPane.empty();
        this.tabPane.addItems(BI.values(self.tabs));
    },
    getValue: function () {
        return this.model.getValue();
    },

    populate: function () {
        var comboItems = this.model.getComboItems();
        this.combo.populate(comboItems);
        this.combo.setValue();
        this.combo.setValue(this.model.getSelectedForeignTable());
        this.tree.populate();
        this._createTabs();
    }


});
BI.FieldRelationSettingWithPreviewPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.field_relation_setting_with_preview_popup", BI.FieldRelationSettingWithPreviewPopup);