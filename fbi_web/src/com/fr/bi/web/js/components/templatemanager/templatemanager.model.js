/**
 * create by young
 * 处理BI.TemplateManger数据
 */
BI.TemplateManagerModel = BI.inherit(FR.OB, {
    _init: function () {
        BI.TemplateManagerModel.superclass._init.apply(this, arguments);
        this.allItems = this.options.items;
        this.sortType = BI.TemplateManager.SORT_BY_TIME;
        this.currentNodeId = BI.FileManagerNav.ROOT_CREATE_BY_ME;
        this.tree = new BI.Tree();
        this.sortAndRefreshTree();
    },
    
    resetAllItems: function(items) {
        this.allItems = items;
        this.sortAndRefreshTree();
    },

    searchReportByKeyword: function (keyword) {
        var reportItems = [];
        BI.each(this.allItems, function (i, item) {
            BI.isNotNull(item.buildUrl) && (reportItems.push(item));
        });
        var result = BI.Func.getSearchResult(reportItems, keyword);
        return result.matched.concat(result.finded);
    },

    getFoldersByParentId: function (folderId) {
        var self = this;
        var treeNode = self.tree.search(folderId);
        var folders = [];
        var children = treeNode.getChildren();
        BI.each(children, function (i, child) {
            if (BI.isNull(child.data.buildUrl)) {
                folders.push(child.id);
                folders = folders.concat(self.getFoldersByParentId(child.id));
            }
        });
        return folders;
    },

    getReportsByParentId: function (folderId) {
        var self = this;
        var treeNode = self.tree.search(folderId);
        var reports = [];
        var children = treeNode.getChildren();
        BI.each(children, function (i, child) {
            if (BI.isNotNull(child.data.buildUrl)) {
                reports.push(child.id);
            } else {
                reports = reports.concat(self.getReportsByParentId(child.id));
            }
        });
        return reports;
    },

    getStatusById: function (id) {
        var status = BICst.REPORT_STATUS.NORMAL;
        BI.some(this.allItems, function (i, item) {
            if (item.id === id) {
                status = item.status;
                return true;
            }
        });
        return status;
    },

    addItem: function (item) {
        this.allItems.push(item);
        this.sortAndRefreshTree();
    },

    removeNode: function (id, type) {
        var self = this;
        switch (type) {
            case BI.TemplateManagerButtonGroup.DELETE_FOLDER:
                var children = this.getFoldersByParentId(id).concat(this.getReportsByParentId(id)).concat(id);
                BI.each(children, function (i, cId) {
                    BI.some(self.allItems, function (j, item) {
                        if (item.id === cId) {
                            self.allItems.splice(j, 1);
                            return true;
                        }
                    })
                });
                break;
            case BI.TemplateManagerButtonGroup.DELETE_REPORT:
                BI.some(this.allItems, function (i, item) {
                    if (item.id === id) {
                        self.allItems.splice(i, 1);
                        return true;
                    }
                });
                break;
        }
        this.sortAndRefreshTree();
    },

    renameNode: function (id, name) {
        BI.some(this.allItems, function (i, item) {
            if (item.id === id) {
                item.text = name;
                item.lastModify = new Date().getTime();
                return true;
            }
        });
        this.sortAndRefreshTree();
    },

    hangoutReport: function (id) {
        BI.some(this.allItems, function (i, item) {
            if (item.id === id) {
                item.status = item.status === BICst.REPORT_STATUS.NORMAL
                    ? BICst.REPORT_STATUS.APPLYING : BICst.REPORT_STATUS.NORMAL;
                return true;
            }
        });
        this.sortAndRefreshTree();
    },

    editSharedUsers: function (id, users) {
        BI.each(this.allItems, function (i, item) {
            if (item.id === id) {
                var shared = item.shared;
                if (BI.isNotNull(shared) && shared.length !== 0) {
                    var nShared = [];
                    BI.each(shared, function (j, user) {
                        if(users.contains(user.user_id)) {
                            nShared.push(user);
                        }
                    });
                    item.shared = nShared;
                }
            }
        });
        this.sortAndRefreshTree();
    },

    moveFolder: function (selectedFolders, toFolder) {
        var self = this;
        var toFChildren = [];
        BI.each(this.allItems, function (i, item) {
            if (item.pId === toFolder) {
                toFChildren.push(item);
            }
        });
        var renames = [], allFolders = BI.deepClone(selectedFolders);
        BI.each(allFolders, function (i, sFolder) {
            //找到第一层的子节点，判断重名问题 分文件夹和模板
            var node = self.getTreeNodeById(sFolder);
            if (BI.isNotNull(node.data.buildUrl)) {
                BI.some(toFChildren, function (i, item) {
                    if (BI.isNotNull(item.buildUrl) && item.text === node.data.text) {
                        renames.push(sFolder);
                        return true;
                    }
                });
            } else {
                BI.some(toFChildren, function (i, item) {
                    if (BI.isNull(item.buildUrl) && item.text === node.data.text) {
                        renames.push(sFolder);
                        return true;
                    }
                });
            }
        });
        BI.each(renames, function (i, id) {
            BI.some(allFolders, function (j, f) {
                if (id === f) {
                    allFolders.splice(j, 1);
                    return true;
                }
            });
        });
        BI.each(this.allItems, function (i, item) {
            if (allFolders.contains(item.id)) {
                item.pId = toFolder;
            }
        });
        this.sortAndRefreshTree();
        return allFolders;
    },

    getAllItems: function () {
        return BI.deepClone(this.allItems);
    },

    getCurrentNodeTreeJSON: function () {
        return BI.deepClone(this.tree.toJSON(this.tree.search(this.currentNodeId)));
    },

    getTreeNodeById: function (id) {
        return this.tree.search(id);
    },

    checkFolderName: function (name, id) {
        var isValid = true;
        //当前目录内不重名
        var currNodeChildren = this.currentNodeId !== BI.FileManagerNav.ROOT_CREATE_BY_ME ? this.tree.search(this.currentNodeId).getChildren() : this.tree.getRoot().getChildren();
        BI.some(currNodeChildren, function (i, child) {
            if (BI.isNull(child.data.buildUrl) && id !== child.data.id && child.data.text === name) {
                isValid = false;
                return true;
            }
        });
        return isValid;
    },

    checkReportName: function (name, id) {
        var isValid = true;
        //原节点所在目录内不重名
        var node = this.tree.search(id);
        var pId = node.data.pId;
        var currNodeChildren = pId !== BI.FileManagerNav.ROOT_CREATE_BY_ME ? this.tree.search(pId).getChildren() : this.tree.getRoot().getChildren();
        BI.some(currNodeChildren, function (i, child) {
            if (BI.isNotNull(child.data.buildUrl) && id !== child.data.id && child.data.text === name) {
                isValid = false;
                return true;
            }
        });
        return isValid;
    },

    sortAndRefreshTree: function () {
        var files = [], folders = [];
        BI.each(this.allItems, function (i, item) {
            if (BI.isNotNull(item.buildUrl)) {
                files.push(item);
            } else {
                item.children = [];
                folders.push(item);
            }
        });
        if (this.sortType === BI.TemplateManager.SORT_BY_NAME) {
            folders = BI.sortBy(folders, "text");
            files = BI.sortBy(files, "text");
        } else {
            folders = BI.sortBy(folders, "lastModify").reverse();
            files = BI.sortBy(files, "lastModify").reverse();
        }
        this.tree.initTree(BI.Tree.transformToTreeFormat(BI.concat(folders, files)));
    },

    createDistinctFolderName: function () {
        var newName = BI.i18nText("BI-Create_Folder");
        var folderNames = [];
        var currNodeChildren = this.currentNodeId !== BI.FileManagerNav.ROOT_CREATE_BY_ME ?
            this.tree.search(this.currentNodeId).getChildren() :
            this.tree.getRoot().getChildren();
        BI.each(currNodeChildren, function (i, item) {
            if (BI.isNull(item.data.buildUrl)) {
                folderNames.push({name: item.data.text});
            }
        });
        return BI.Func.createDistinctName(folderNames, newName);
    },

    setCurrentNodeId: function (nodeId) {
        this.currentNodeId = nodeId;
    },

    getCurrentNodeId: function () {
        return this.currentNodeId;
    },

    setSortType: function (sortType) {
        this.sortType = sortType;
    },

    getSortType: function () {
        return this.sortType;
    }

});