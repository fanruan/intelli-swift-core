/**
 * @class FR.BIDataLikePane 展示数据连接的界面
 * @extend BIView
 * @type {*|void|Object}
 */
BIConf.DataLinkPaneView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BIConf.DataLinkPaneView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-data-link-pane"
        })
    },

    _render: function (vessel) {
        var self = this;
        this.dataLinksTable = BI.createWidget({
            type: "bi.preview_table",
            cls: "data-links-table",
            columnSize: [0.15, 0.55, 0.20, 0.10],
            rowSize: 30,
            header: [[{
                type: "bi.label",
                text: BI.i18nText("BI-Data_Connection_Name"),
                textAlign: "left",
                height: 30,
                hgap: 10
            }, {
                type: "bi.label",
                text: "URL",
                textAlign: "left",
                height: 30,
                hgap: 10
            }, {
                type: "bi.label",
                text: BI.i18nText('BI-User_Name'),
                textAlign: "left",
                height: 30,
                hgap: 10
            }, {
                type: "bi.label"
            }]]
        });
        var newDataLink = BI.createWidget({
            type: "bi.add_data_link_combo"
        });

        newDataLink.on(BI.AddDataLinkCombo.EVENT_CHANGE, function (v) {
            var id = BI.UUID();
            if (v === BICst.DATABASE.ORACLE ||
                v === BICst.DATABASE.SQL_SERVER ||
                v === BICst.DATABASE.OTHERS ||
                v === BICst.DATABASE.DB2 ||
                v === BICst.DATABASE.POSTGRE) {
                var addSchemaLink = BI.createWidget({
                    type: "bi.add_schema_data_link",
                    info: {
                        database: v
                    }
                });
                addSchemaLink.on(BI.AddSchemaDataLink.EVENT_SAVE, function (data) {
                    self.model.set("addLink", data);
                });
                BI.Popovers.create(id, addSchemaLink).open(id);
            } else {
                var dataLink = BI.createWidget({
                    type: "bi.add_data_link",
                    info: {
                        database: v
                    }
                });
                dataLink.on(BI.AddDataLink.EVENT_SAVE, function (data) {
                    self.model.set("addLink", data);
                });
                BI.Popovers.create(id, dataLink).open(id);
            }

        });
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: this.dataLinksTable,
                left: 20,
                right: 20,
                top: 10
            }, {
                el: newDataLink,
                top: -40,
                right: 20
            }]
        });
    },

    _init: function () {
        BIConf.DataLinkPaneView.superclass._init.apply(this, arguments);
    },

    _createTableItems: function (dataLinks) {
        var self = this, items = [];
        BI.each(dataLinks, function (i, info) {
            var item = [];
            item.push({
                type: "bi.label",
                text: info.name,
                height: 30,
                textAlign: "left",
                hgap: 10
            });
            item.push({
                type: "bi.label",
                text: info.url,
                height: 30,
                textAlign: "left",
                hgap: 10
            });
            item.push({
                type: "bi.label",
                text: info.user,
                height: 30,
                textAlign: "left",
                hgap: 10
            });
            item.push({
                type: "bi.center_adapt",
                items: [{
                    type: "bi.icon_button",
                    title: BI.i18nText("BI-Setting"),
                    cls: "data-link-set-font icon-font",
                    handler: function () {
                        self.set("set", info.id);
                    }
                }, {
                    type: "bi.icon_button",
                    title: BI.i18nText("BI-Test_Connection"),
                    cls: "data-link-test-font icon-font",
                    handler: function () {
                        self.model.set("test", info.id);
                    }
                }, {
                    type: "bi.icon_button",
                    title: BI.i18nText("BI-Copy"),
                    cls: "data-link-copy-font icon-font",
                    handler: function () {
                        self.model.set("copy", info.id);
                    }
                }, {
                    type: "bi.icon_button",
                    cls: "data-link-remove-font icon-font",
                    title: BI.i18nText("BI-Remove"),
                    handler: function () {
                        var dataLinkName = info.name;
                        BI.Msg.confirm(BI.i18nText('BI-Sure_Delete_Connection'), BI.i18nText('BI-Sure_Delete_Connection') + ":" + dataLinkName, function (v) {
                            if (BI.isNotNull(v) && v === true) {
                                self.model.set("delete", info.id);
                            }
                        })
                    }
                }]
            });
            items.push(item);
        });
        return items;
    },

    _testDataLink: function (id) {
        BI.createWidget({
            type: "bi.test_link_loading_mask",
            masker: BICst.BODY_ELEMENT,
            link: this.model.get("links")[id]
        });
    },

    _updateDataLink: function (id) {
        var self = this;
        if (BI.isNotNull(this.model.get("links")[id].schema)) {
            BI.Popovers.remove(id);
            var schemaLink = BI.createWidget({
                type: "bi.add_schema_data_link",
                info: BI.extend(self.model.get("links")[id], {
                    id: id
                })
            });
            schemaLink.on(BI.AddSchemaDataLink.EVENT_SAVE, function (data) {
                var links = self.model.get("links");
                links[id] = data;
                self.model.set("links", links);
            });
            BI.Popovers.create(id, schemaLink).open(id);
        } else {
            BI.Popovers.remove(id);
            var dataLink = BI.createWidget({
                type: "bi.add_data_link",
                info: BI.extend(self.model.get("links")[id], {
                    id: id
                })

            });
            dataLink.on(BI.AddDataLink.EVENT_SAVE, function (data) {
                var links = self.model.get("links");
                links[id] = data;
                self.model.set("links", links);
            });
            BI.Popovers.create(id, dataLink).open(id);
        }
    },

    _copyDataLink: function (id) {
        var self = this;
        var newId = BI.UUID();
        if (BI.isNotNull(this.model.get("links")[id].schema)) {
            var schemaLink = BI.createWidget({
                type: "bi.add_schema_data_link",
                info: BI.extend(self.model.get("links")[id], {
                    id: newId,
                    copy: true,
                    name: self.model.createDistinctLinkName(id)
                })
            });
            schemaLink.on(BI.AddSchemaDataLink.EVENT_SAVE, function (data) {
                self.model.set("addLink", data);
            });
            BI.Popovers.create(newId, schemaLink).open(newId);
        } else {
            var dataLink = BI.createWidget({
                type: "bi.add_data_link",
                info: BI.extend(self.model.get("links")[id], {
                    id: newId,
                    copy: true,
                    name: self.model.createDistinctLinkName(id)
                })
            });
            dataLink.on(BI.AddDataLink.EVENT_SAVE, function (data) {
                self.model.set("addLink", data);
            });
            BI.Popovers.create(newId, dataLink).open(newId);
        }
    },

    _refreshLinksTable: function () {
        var links = this.model.get("links");
        //排序一下
        var sortLinks = [];
        var linksArray = BI.keys(links);
        var sortedArray = BI.sortBy(linksArray);
        BI.each(sortedArray, function (i, arr) {
            sortLinks.push(BI.extend(links[arr], {id: arr}));
        });
        this.dataLinksTable.populate(this._createTableItems(sortLinks));
    },

    local: function () {
        if (this.model.has("delete")) {
            this.model.get("delete");
            return true;
        }
        if (this.model.has("test")) {
            this._testDataLink(this.model.get("test"));
            return true;
        }
        if (this.model.has("set")) {
            this._updateDataLink(this.model.get("set"));
            return true;
        }
        if (this.model.has("copy")) {
            this._copyDataLink(this.model.get("copy"));
            return true;
        }
        if (this.model.has("addLink")) {
            this.model.get("addLink");
            return true;
        }
        return false;
    },

    load: function () {
        this._refreshLinksTable();
    },

    refresh: function () {
        this.readData(true);
    },

    change: function () {
        this._refreshLinksTable();
    }
});