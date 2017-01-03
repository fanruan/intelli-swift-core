/**
 * Created by roy on 15/11/25.
 */
BIConf.AllBusinessPackagesPaneView = BI.inherit(BI.View, {

    constants: {
        ONE_PACKAGE_LAYER: "__one_package_layer__"
    },

    _defaultConfig: function () {
        return BI.extend(BIConf.AllBusinessPackagesPaneView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-all-business-packages"
        })
    },

    getName: function () {
        return "packageViewPane";
    },

    events: {},

    _init: function () {
        BIConf.AllBusinessPackagesPaneView.superclass._init.apply(this, arguments);
        this.packages = {};
    },

    _render: function (vessel) {
        var self = this;
        this.groupPane = BI.createWidget({
            type: "bi.business_package_manage"
        });

        this.groupPane.on(BI.BusinessPackageManage.EVENT_BATCH_GROUP, function () {
            if (BI.isNull(self.packageManagePane)) {
                self.packageManagePane = BI.createWidget({
                    type: "bi.business_package_group"
                });
                self.packageManagePane.on(BI.BusinessPackageGroup.EVENT_CHANGE, function () {
                    self.model.set("groups", this.getValue());
                    this.setVisible(false);
                });

                self.packageManagePane.on(BI.BusinessPackageGroup.EVENT_CANCEL, function () {
                    this.setVisible(false);
                });
                self.packageManagePane.on(BI.BusinessPackageGroup.EVENT_CONFIRM, function (packageName, packageID) {
                    var groups = self.packageManagePane.getValue();
                    groups.changedPackage = {};
                    groups.changedPackage.newPackageName = packageName;
                    groups.changedPackage.packageID = packageID;
                    self.model.set("groups", groups);
                });
                self.packageManagePane.populate();
                BI.createWidget({
                    type: "bi.absolute",
                    element: this.element,
                    items: [{
                        el: self.packageManagePane,
                        left: 0,
                        top: 0,
                        right: 0,
                        bottom: 0
                    }]
                });
            } else {
                self.packageManagePane.setVisible(true);
                self.packageManagePane.populate();
            }


        });

        this.groupPane.on(BI.BusinessPackageManage.EVENT_PACKAGE_ADD, function (groupID, groupName) {
            BI.Layers.remove(self.constants.ONE_PACKAGE_LAYER);
            var id = BI.UUID();
            var name = BI.Func.createDistinctName(self.model.get("packages"), BI.i18nText(BICst.PACKAGE));
            //立刻添加到sharing pool中
            if (BI.isNotNull(groupID) && BI.isNotEmptyString(groupID)) {

            }
            var packages = Data.SharingPool.cat("packages");
            packages[id] = {
                id: id,
                tables: [],
                name: name
            };
            var onePackage = BI.createWidget({
                type: "bi.one_package",
                element: BI.Layers.create(self.constants.ONE_PACKAGE_LAYER),
                id: id,
                name: name,
                gid: groupID
            });
            BI.Layers.show(self.constants.ONE_PACKAGE_LAYER);
            onePackage.populate();
            onePackage.on(BI.OnePackage.EVENT_SAVE, function () {
                self.refresh();
                BI.Layers.remove(self.constants.ONE_PACKAGE_LAYER);
            });
            onePackage.on(BI.OnePackage.EVENT_CUBE_SAVE, function () {
                self.refresh();
            });
            onePackage.on(BI.OnePackage.EVENT_CANCEL, function () {
                BI.Layers.remove(self.constants.ONE_PACKAGE_LAYER);
            });
        });

        this.groupPane.on(BI.BusinessPackageManage.EVENT_PACKAGE_DELETE, function (packageID) {
            var packName = BI.Utils.getPackageNameByID4Conf(packageID);
            BI.Msg.confirm("", BI.i18nText("BI-Is_Delete_Package") + ":" + packName + "?", function (v) {
                if (v === true) {
                    self.model.set("delete", packageID);
                }
            });
        });


        this.groupPane.on(BI.BusinessPackageManage.EVENT_CHANGE, function (groupID, packID) {
            //使用modules入口
            BI.Layers.remove(self.constants.ONE_PACKAGE_LAYER);
            var onePackage = BI.createWidget({
                type: "bi.one_package",
                element: BI.Layers.create(self.constants.ONE_PACKAGE_LAYER),
                id: packID,
                gid: groupID
            });
            BI.Layers.show(self.constants.ONE_PACKAGE_LAYER);
            onePackage.populate();
            onePackage.on(BI.OnePackage.EVENT_SAVE, function () {
                self.refresh();
                BI.Layers.remove(self.constants.ONE_PACKAGE_LAYER);
            });
            onePackage.on(BI.OnePackage.EVENT_CUBE_SAVE, function () {
                self.refresh();
            });
            onePackage.on(BI.OnePackage.EVENT_CANCEL, function () {
                BI.Layers.remove(self.constants.ONE_PACKAGE_LAYER);
            });
        });


        this.groupPane.on(BI.BusinessPackageManage.EVENT_GROUP_ADD, function () {
            var groups = self.model.get("groups");
            var groupName = BI.Func.createDistinctName(groups, BI.i18nText('BI-Grouping'));
            self.groupPane.addGroupWidget(groupName);
            self.model.set("groups", self.groupPane.getValue());
        });

        this.groupPane.on(BI.BusinessPackageManage.EVENT_CLICK_DELETE, function () {
            self.model.set("groups", self.groupPane.getValue());
        });


        this.groupPane.on(BI.BusinessPackageManage.EVENT_GROUP_NAME_CONFIRM, function () {
            self.model.set("groups", self.groupPane.getValue());
        });

        this.groupPane.on(BI.BusinessPackageManage.EVENT_EDITOR_CONFIRM, function (packageName, packageID) {
            var groups = self.groupPane.getValue();
            groups.changedPackage = {};
            groups.changedPackage.newPackageName = packageName;
            groups.changedPackage.packageID = packageID;
            self.model.set("groups", groups);
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: self.groupPane,
                right: 0,
                top: -35,
                left: 0,
                bottom: 0
            }
            ]

        });
    },


    load: function () {
        var groupedItems = this.model.get("groups");
        var allPackages = this.model.get("packages");
        this.groupPane.populate(BI.sortBy(groupedItems, "init_time"), BI.sortBy(allPackages, "position"));
        // this.mask.destroy();
    },

    change: function (changed) {
        var self = this;
        if (BI.isNotNull(changed.groups)) {
            this.model.update({
                data: {
                    groups: self.model.get("groups")
                },
                complete: function () {
                    self.refresh();
                }
            })
        }
    },

    local: function () {
        var self = this;
        if (this.model.has("delete")) {
            var id = self.model.get("delete");
            self.model.update();
            self.model.patch({
                data: {
                    id: id
                },
                complete: function () {
                    BI.Utils.getTranslationsRelationsFields(function (data) {
                        Data.SharingPool.put("relations", data.relations);
                        Data.SharingPool.put("fields", data.fields);
                        Data.SharingPool.put("translations", data.translations);
                    });
                    self.refresh();
                }
            });
            return true;
        }
        return false;
    },

    listenEnd: function () {
        return this;
    },


    refresh: function () {
        // this.mask = BI.createWidget({
        //     type: "bi.loading_mask",
        //     masker: this.element,
        //     text: BI.i18nText("BI-Loading")
        // });
        this.readData(true);
    }
});
