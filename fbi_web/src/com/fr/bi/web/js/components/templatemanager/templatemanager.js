/**
 * 模板管理
 *
 * Created by GUY on 2016/2/1.
 * @class BI.TemplateManager
 * @extends BI.Pane
 * @abstract
 */
BI.TemplateManager = BI.inherit(BI.Pane, {

    _defaultConfig: function () {
        return BI.extend(BI.TemplateManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: 'bi-template-manager',
            items: []
        });
    },

    _init: function () {
        BI.TemplateManager.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.TemplateManagerModel({items: o.items});
        this._createTools();
        //管理员没有分享
        //FS.config.isAdmin === true && (this.shareButton.setVisible(false));

        //导航
        this.nav = BI.createWidget({
            type: "bi.file_manager_nav",
            items: this.model.getAllItems()
        });
        this.nav.on(BI.FileManagerNav.EVENT_CHANGE, function (id, obj) {
            self.model.setCurrentNodeId(id);
            self.halfCheck.setSelected(false);
            self._refreshNavAndList();
        });

        this.halfCheck = BI.createWidget({
            type: "bi.multi_select_bar",
            width: 36,
            text: ""
        });
        this.halfCheck.on(BI.MultiSelectBar.EVENT_CHANGE, function () {
            if (this.isSelected()) {
                self.folderAndFileList.setNotSelectedValue([]);
            } else {
                self.folderAndFileList.setValue([]);
            }
        });

        //排序
        this.sortCombo = BI.createWidget({
            type: "bi.folder_file_sort_combo",
            items: [{
                iconClass: "time-font",
                text: BI.i18nText("BI-Sort_By_Time"),
                value: BI.TemplateManager.SORT_BY_TIME
            }, {
                iconClass: "letter-font",
                text: BI.i18nText("BI-Sort_By_Letter"),
                value: BI.TemplateManager.SORT_BY_NAME
            }]
        });
        this.sortCombo.setValue(BI.TemplateManager.SORT_BY_TIME);
        this.sortCombo.on(BI.IconCombo.EVENT_CHANGE, function () {
            self.model.setSortType(this.getValue()[0]);
            self.model.sortAndRefreshTree();
            var v = self.list.getValue();
            self.list.populate(self.model.getCurrentNodeTreeJSON());
            self.list.setValue(v);
        });

        //视图
        this.viewType = BI.createWidget({
            type: "bi.segment",
            cls: "folder-report-view",
            items: BI.createItems([{
                cls: "folder-list-view folder-view",
                value: BI.TemplateManager.LIST_VIEW
            }, {
                cls: "folder-card-view folder-view",
                value: BI.TemplateManager.CARD_VIEW
            }], {
                type: "bi.icon_button",
                width: 25,
                height: 25
            }),
            width: 60,
            height: 25
        });
        this.viewType.setValue(BI.TemplateManager.LIST_VIEW);
        this.viewType.on(BI.Segment.EVENT_CHANGE, function (v) {
            //self.halfCheck.setSelected(false);
            self.folderAndFileList.changeViewType(v);
            var value = self.folderAndFileList.getValue();
            self.list.populate(self.model.getCurrentNodeTreeJSON());
            self.folderAndFileList.setValue(value);
        });

        //文件夹和模板
        this.folderAndFileList = BI.createWidget({
            type: "bi.template_manager_button_group",
            items: BI.deepClone(this.model.getAllItems()),
            folderChecker: function (name, id) {
                return self.model.checkFolderName(name, id);
            },
            reportChecker: function (name, id) {
                return self.model.checkReportName(name, id);
            }
        });
        //this.folderAndFileList.populate();

        //list的几个事件
        //全选、半选
        this.folderAndFileList.on(BI.Controller.EVENT_CHANGE, function () {
            self._listChangeHalfCheck();
        });
        //点击
        this.folderAndFileList.on(BI.TemplateManagerButtonGroup.EVENT_CHANGE, function (id) {
            self._onClickListItem(id);
        });
        //重命名
        this.folderAndFileList.on(BI.TemplateManagerButtonGroup.EVENT_FOLDER_RENAME, function (id, name, type) {
            self._onRename(id, name, type);
        });
        //删除
        this.folderAndFileList.on(BI.TemplateManagerButtonGroup.EVENT_DELETE, function (id, type) {
            self._onRemove(id, type);
        });
        this.folderAndFileList.on(BI.TemplateManagerButtonGroup.EVENT_HANGOUT, function (id) {
            self._onHangout(id);
        });

        this.list = BI.createWidget({
            type: "bi.list_pane",
            cls: "report-folder-list",
            el: this.folderAndFileList,
            items: this.model.getAllItems()
        });
        this._refreshNavAndList();

        var navAndList = BI.createWidget({
            type: "bi.absolute",
            cls: "template-manager-pane",
            items: [{
                el: {
                    type: "bi.center_adapt",
                    items: [this.halfCheck],
                    width: 50,
                    height: 40
                },
                top: 0,
                left: 0
            }, {
                el: this.list,
                left: 0,
                right: 0,
                top: 40,
                bottom: 0
            }, {
                el: this.nav,
                left: 50,
                right: 100,
                top: 0
            }, {
                el: this.sortCombo,
                top: 5,
                right: 20
            }, {
                el: this.viewType,
                top: 5,
                right: 60
            }]
        });
        this.searcher.setAdapter(navAndList);

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.vtape",
                    items: [{
                        el: {
                            type: "bi.left_right_vertical_adapt",
                            lrgap: 10,
                            items: {
                                left: [{
                                    el: this.shareButton,
                                    rgap: 10
                                }, {
                                    el: this.moveButton,
                                    rgap: 10
                                }, {
                                    el: this.newButton
                                }],
                                right: [this.searcher]
                            }
                        },
                        height: 50
                    }, {
                        el: navAndList,
                        height: "fill"
                    }]
                },
                left: 10,
                right: 10,
                top: 0,
                bottom: 10
            }]
        });
    },

    _listChangeHalfCheck: function () {
        if (this.folderAndFileList.getValue().length === 0) {
            this.halfCheck.setSelected(false);
        } else if (this.folderAndFileList.getNotSelectedValue().length === 0) {
            this.halfCheck.setSelected(true);
        } else {
            this.halfCheck.setHalfSelected(true);
        }
    },

    /**
     * 点击文件夹
     * @param id
     * @private
     */
    _onClickListItem: function (id) {
        this.model.setCurrentNodeId(id);
        this.halfCheck.setSelected(false);
        this._refreshNavAndList();
    },

    /**
     * 重命名
     * @param id
     * @param name
     * @param type
     * @private
     */
    _onRename: function (id, name, type) {
        this.model.renameNode(id, name);
        this._refreshNavTreeData();
        this.fireEvent(BI.TemplateManager.EVENT_FOLDER_RENAME, id, name, this.model.getCurrentNodeId(), type);
    },

    /**
     * 删除
     * @param id
     * @param type
     * @private
     */
    _onRemove: function (id, type) {
        var self = this;
        //删除当前节点及其所有子节点
        var mes = BI.i18nText("BI-Confirm_Delete_Folder");
        if (type === BI.TemplateManagerButtonGroup.DELETE_REPORT) {
            mes = BI.i18nText("BI-Confirm_Delete_Report")
        }
        BI.Msg.confirm(BI.i18nText("BI-Confirm_Delete"), mes, function (flag) {
            if (flag === true) {
                self.model.removeNode(id, type);
                self._refreshNavTreeData();
                self.populate();
                self.fireEvent(BI.TemplateManager.EVENT_DELETE, id, type);
            }
        });
    },

    _onHangout: function (id) {
        this.model.hangoutReport(id);
        this._refreshNavTreeData();
        this.fireEvent(BI.TemplateManager.EVENT_HANGOUT, id, this.model.getStatusById(id));
    },

    /**
     * 分享——移动——新建
     * @private
     */
    _createTools: function () {
        var self = this;
        //分享
        this.shareButton = BI.createWidget({
            type: 'bi.button',
            level: "ignore",
            iconClass: 'share-font',
            text: BI.i18nText("BI-Template_Share_To"),
            height: 30
        });
        this.shareButton.on(BI.Button.EVENT_CHANGE, function () {
            //是否处于搜索状态  判断是否有模板的存在
            var selectedItems = self.searcher.isSearching() ? self.searcherResultPane.getSelectedItems() : self._getAllSelectedReports();
            if (BI.isNull(selectedItems) || BI.isEmptyArray(selectedItems)) {
                BI.Msg.toast(BI.i18nText("BI-Selected_Folders_No_Report"));
            } else {
                var id = BI.UUID();
                var shareBox = BI.createWidget({
                    type: "bi.share_report_pane"
                });
                shareBox.on(BI.ShareReportPane.EVENT_SHARE, function (users) {
                    self.fireEvent(BI.TemplateManager.EVENT_SHARE, selectedItems, users);
                });
                BI.Popovers.create(id, shareBox, {width: 600, height: 500}).open(id);
            }
        });

        //移动
        this.moveButton = BI.createWidget({
            type: 'bi.button',
            iconClass: 'move-font',
            level: "ignore",
            text: BI.i18nText("BI-Move_To"),
            height: 30
        });
        this.moveButton.on(BI.Button.EVENT_CHANGE, function () {
            //是否处于搜索状态
            var selectedItems = self.searcher.isSearching() ? self.searcherResultPane.getSelectedItems() : self.folderAndFileList.getValue();
            if (BI.isNull(selectedItems) || BI.isEmptyArray(selectedItems)) {
                BI.Msg.toast(BI.i18nText("BI-No_Selected_File"));
            } else {
                var id = BI.UUID();
                var moveTo = BI.createWidget({
                    type: "bi.folder_move_to_pane",
                    items: self.model.getAllItems(),
                    selectedItems: selectedItems
                });
                moveTo.on(BI.FolderMoveToPane.EVENT_MOVE, function (selectedFolders, toFolder) {
                    var noRenames = self.model.moveFolder(selectedFolders, toFolder);
                    self._refreshNavTreeData();
                    self.populate();
                    if (noRenames.length === 0) {
                        BI.Msg.toast(BI.i18nText("BI-Move_Fail_Name_Repeat"));
                    } else if (noRenames.length === selectedFolders.length) {
                        BI.Msg.toast(BI.i18nText("BI-Move_Successfully"));
                    } else {
                        BI.Msg.toast(BI.i18nText("BI-Move_Part_Fail_Name_Repeat"));
                    }
                    self.fireEvent(BI.TemplateManager.EVENT_MOVE, noRenames, toFolder);
                });
                BI.Popovers.create(id, moveTo, {width: 400, height: 320}).open(id);
            }
        });

        //新建
        this.newButton = BI.createWidget({
            type: 'bi.button',
            iconClass: 'new-file-font',
            level: "ignore",
            text: BI.i18nText("BI-Create_Folder"),
            height: 30
        });
        this.newButton.on(BI.Button.EVENT_CHANGE, function () {
            //更新this.allItems数据 将新建的插入到最前面

            var id = BI.UUID();
            var item = {
                id: id,
                pId: self.model.getCurrentNodeId(),
                text: self.model.createDistinctFolderName(),
                value: id,
                lastModify: new Date()
            };
            self.model.addItem(item);
            self.nav.refreshTreeData(self.model.getAllItems());
            var newItem = BI.createWidget(BI.extend(BI.deepClone(item), {
                type: self.viewType.getValue()[0] === BI.TemplateManager.LIST_VIEW ? "bi.folder_list_view_item" : "bi.folder_card_view_item",
                onClickItem: function () {
                    self._onClickListItem(item.id);
                },
                onRenameFolder: function (name) {
                    self._onRename(id, name, BI.TemplateManagerButtonGroup.RENAME_FOLDER);
                },
                onDeleteFolder: function () {
                    self._onRemove(item.id, BI.TemplateManagerButtonGroup.DELETE_FOLDER);
                },
                validationChecker: function (name) {
                    return self.model.checkFolderName(name, id);
                }
            }));
            self.list.prependItems([newItem]);
            newItem.focus();
        });

        //搜索
        this.searcherResultPane = BI.createWidget({
            type: "bi.report_search_result_pane",
            reportChecker: function (name, id) {
                return self.model.checkReportName(name, id);
            }
        });
        this.searcher = BI.createWidget({
            type: "bi.searcher",
            el: {
                type: "bi.search_editor",
                watermark: BI.i18nText("BI-Search_In_Create_By_Me")
            },
            width: 230,
            onSearch: function (op, callback) {
                //搜索只是搜索所有的模板，文件夹不参与
                var keyword = op.keyword.toLowerCase();
                callback(self.model.searchReportByKeyword(keyword), keyword, self.viewType.getValue()[0]);
            },
            isAutoSearch: false,
            isAutoSync: false,
            popup: this.searcherResultPane
        });
        this.searcher.on(BI.Searcher.EVENT_START, function () {
            self.newButton.setVisible(false);
        });
        this.searcher.on(BI.Searcher.EVENT_STOP, function () {
            self.newButton.setVisible(true);
        });
        this.searcherResultPane.on(BI.ReportSearchResultPane.EVENT_REPORT_RENAME, function (id, name) {
            self._onRename(id, name, BI.TemplateManagerButtonGroup.RENAME_REPORT);
            self._refreshNavAndList();
        });
        this.searcherResultPane.on(BI.ReportSearchResultPane.EVENT_DELETE, function (id) {
            self._onRemove(id, BI.TemplateManagerButtonGroup.DELETE_REPORT);
        });
    },


    /**
     * 获取所有选中的模板，不包括文件夹，包括文件夹内部所有
     * @private
     */
    _getAllSelectedReports: function () {
        var self = this;
        var selectedItems = this.folderAndFileList.getValue();
        var reports = [];
        BI.each(selectedItems, function (i, id) {
            var treeNode = self.model.getTreeNodeById(id);
            if (BI.isNotNull(treeNode.data.buildUrl)) {
                reports.push(id);
            } else {
                reports = reports.concat(self.model.getReportsByParentId(id));
            }
        });
        return reports;
    },

    /**
     * 刷新dom
     * @private
     */
    _refreshNavAndList: function () {
        this.nav.populate(this.model.getTreeNodeById(this.model.getCurrentNodeId()));
        var selectedItems = this.folderAndFileList.getValue();
        this.list.populate(this.model.getCurrentNodeTreeJSON());
        this.folderAndFileList.setValue(selectedItems);
        this._listChangeHalfCheck();
    },

    _refreshNavTreeData: function () {
        this.nav.refreshTreeData(this.model.getAllItems());
    },

    populate: function () {
        // nav and list
        this._refreshNavAndList();
        // search pane
        if (this.searcher.isSearching()) {
            var keyword = this.searcher.getKeyword();
            this.searcherResultPane.populate(this.model.searchReportByKeyword(keyword), keyword, this.viewType.getValue()[0]);
            this._refreshNavAndList();
        }
    }
});
BI.extend(BI.TemplateManager, {
    SORT_BY_TIME: 1,
    SORT_BY_NAME: 2,
    LIST_VIEW: 1,
    CARD_VIEW: 2
});
BI.TemplateManager.EVENT_FOLDER_RENAME = "BI.TemplateManager.EVENT_FOLDER_RENAME";
BI.TemplateManager.EVENT_DELETE = "BI.TemplateManager.EVENT_DELETE";
BI.TemplateManager.EVENT_MOVE = "BI.TemplateManager.EVENT_MOVE";
BI.TemplateManager.EVENT_SHARE = "BI.TemplateManager.EVENT_SHARE";
BI.TemplateManager.EVENT_HANGOUT = "BI.TemplateManger.EVENT_HANGOUT";
$.shortcut('bi.template_manager', BI.TemplateManager);