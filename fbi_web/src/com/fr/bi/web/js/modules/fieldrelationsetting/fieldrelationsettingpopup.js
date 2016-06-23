/**
 * Created by roy on 16/3/9.
 */
BI.FieldRelationSettingPopup = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function () {
        return BI.extend(BI.FieldRelationSettingPopup.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "field-relation-setting-popup",
            wId: ""
        })
    },

    _init: function () {
        var o = this.options;
        BI.FieldRelationSettingPopup.superclass._init.apply(this, arguments);
        this.model = new BI.FieldRelationSettingPopupModel({
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
        BI.FieldRelationSettingPopup.superclass.rebuildSouth.apply(this, arguments);
        this.sure.setText(BI.i18nText("BI-Save_Config"));
        this.sure.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.FieldRelationSettingPopup.EVENT_CHANGE)
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

        this.populate();
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
        this._createTabs();

    }


});
BI.FieldRelationSettingPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.field_relation_setting_popup", BI.FieldRelationSettingPopup);