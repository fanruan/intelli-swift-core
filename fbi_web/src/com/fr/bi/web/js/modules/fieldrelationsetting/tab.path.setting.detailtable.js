/**
 * Created by roy on 16/3/12.
 */
BI.DetailTablePathSettingTab = BI.inherit(BI.Widget, {
    constant: {
        CHANGE_VALUE: 0,
        SAVE_VALUE: 1
    },
    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePathSettingTab.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "detail-table-path-setting-tab",
            paths: [],
            did: "",
            choosePath: []
        })
    },
    _init: function () {
        var o = this.options, self = this, c = this.constant;
        BI.DetailTablePathSettingTab.superclass._init.apply(this, arguments);
        this.tab = BI.createWidget({
            type: "bi.detail_table_path_setting_switch",
            items: [{
                type: "bi.text_button",
                text: BI.i18nText("BI-Change"),
                value: c.CHANGE_VALUE,
                cls: "save-button",
                textAlign: "center",
                textHeight: 30
            }, {
                type: "bi.button",
                text: BI.i18nText("BI-Detail_Set_Complete"),
                value: c.SAVE_VALUE,
                height: 30
            }]
        });


        this.tab.on(BI.DetailTablePathSettingSwitch.EVENT_CHANGE, function (v) {
            if (v === c.SAVE_VALUE) {
                var value = self.pathChooser.getValue();
                var relations = self._getPathRelation(value);
                o.choosePath = relations;
                self.pathShow.setValue(o.choosePath, o.dId);
                self.tabWidget.setSelect(v);
                self.fireEvent(BI.DetailTablePathSettingTab.EVENT_CHANGE, relations)
            } else {
                self.tabWidget.setSelect(v);
            }
        });

        this.tabWidget = BI.createWidget({
            type: "bi.tab",
            cardCreator: BI.bind(self._createTabs, this),
            logic: {
                dynamic: true
            }
        });


        this.tabWidget.on(BI.Tab.EVENT_CHANGE, function (value) {
            self.setValue(value);
        });


        this.pathShow = BI.createWidget({
            type: "bi.detail_table_path_item",
            relations: o.choosePath,
            dId: o.dId,
            height: 30
        });


        this.fieldPathLabel = BI.createWidget({
            type: "bi.label",
            cls: "tab-label-gray",
            textHeight: 30,
            hgap: 5,
            textAlign: "left"
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [
                {
                    el: {
                        type: "bi.htape",
                        items: [{
                            el: this.fieldPathLabel
                        }, {
                            el: this.tab,
                            width: 90
                        }
                        ],
                        height: 32
                    }
                },
                this.tabWidget]
        });

        this.populate();
    },

    _createPathChooserItem: function () {
        var self = this;
        var paths = this.options.paths;
        this.pathMap = {};
        this.pathChooserMap = {};
        var o = this.options;
        var pathChooserItem = [];
        BI.each(paths, function (i, path) {
            var pathItem = [];
            self.pathMap[i] = path;
            BI.backAny(path, function (i, relation) {
                var regionItem = {};
                var tableId = BI.Utils.getTableIdByFieldID(BI.Utils.getForeignIdFromRelation(relation));
                if (i === 0) {
                    regionItem.region = BI.Utils.getTableNameByID(tableId);
                    regionItem.value = BI.Utils.getForeignIdFromRelation(relation);
                    regionItem.text = BI.Utils.getFieldNameByID(BI.Utils.getForeignIdFromRelation(relation));
                    pathItem.push(regionItem);
                    var primaryTableId = BI.Utils.getTableIdByFieldID(BI.Utils.getPrimaryIdFromRelation(relation));
                    pathItem.push({
                        region: BI.Utils.getTableNameByID(primaryTableId),
                        value: o.dId,
                        text: BI.Utils.getDimensionNameByID(o.dId)
                    });
                    return
                }
                regionItem.region = BI.Utils.getTableNameByID(tableId);
                regionItem.value = BI.Utils.getForeignIdFromRelation(relation);
                regionItem.text = BI.Utils.getFieldNameByID(BI.Utils.getForeignIdFromRelation(relation));
                pathItem.push(regionItem);
            });
            self.pathChooserMap[i] = self._createPathChooserMapItem(pathItem);
            pathChooserItem.push(pathItem);
        });
        return pathChooserItem;
    },

    _createTabs: function (v) {
        var self = this, o = this.options, c = this.constant;
        switch (v) {
            case c.CHANGE_VALUE:
                this.pathChooser = BI.createWidget({
                    type: "bi.path_chooser",
                    items: self._createPathChooserItem()
                });
                this.pathChooser.setValue(self._getPathValue(o.choosePath));
                return BI.createWidget({
                    type: "bi.vertical",
                    vgap: 10,
                    items: [this.pathChooser]
                });
            case c.SAVE_VALUE:
                this.pathShow = BI.createWidget({
                    type: "bi.detail_table_path_item",
                    relations: o.choosePath,
                    dId: o.dId,
                    height: 30
                });
                return this.pathShow;
        }
    },

    _getPathRelation: function (pathChooserValue) {
        var key = BI.findKey(this.pathChooserMap, function (i, value) {
            if (BI.isEqual(pathChooserValue, value)) {
                return true;
            }
        });
        return this.pathMap[key] || [];
    },

    _getPathValue: function (path) {
        var key = BI.findKey(this.pathMap, function (i, value) {
            if (BI.isEqual(path, value)) {
                return true
            }
        });
        return this.pathChooserMap[key];
    },

    _getCommonForeignTable: function () {
        var o = this.options;
        if (BI.isNotNull(o.paths[0])) {
            var path = o.paths[0];
            return BI.Utils.getTableIdByFieldID(BI.Utils.getLastRelationForeignIdFromRelations(path))
        } else {
            return BI.Utils.getTableIDByDimensionID(o.dId);
        }
    },

    _createPathChooserMapItem: function (regionItem) {
        var result = [];
        BI.each(regionItem, function (i, item) {
            result.push(item.value);
        });
        return result;
    },

    setSelect: function (v) {
        this.tabWidget.setSelect(v);
    },

    setValue: function (v) {
        this.options.choosePath = v;
    },

    getValue: function () {

        return this.options.choosePath;
    },

    populate: function () {
        var self = this, o = this.options;
        var commonForeignTableName = BI.Utils.getTableNameByID(self._getCommonForeignTable());
        this.fieldPathLabel.setValue(commonForeignTableName + " > " + BI.Utils.getDimensionNameByID(o.dId));
        this.pathShow.populate();
        this.tabWidget.setSelect(self.constant.SAVE_VALUE);
    }


});
BI.DetailTablePathSettingTab.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_table_path_setting_tab", BI.DetailTablePathSettingTab);