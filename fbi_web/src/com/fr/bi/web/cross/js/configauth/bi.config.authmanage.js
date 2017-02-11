/**
 * 嵌入到平台的 数据配置权限管理
 * Created by Young's on 2017/2/10.
 */
(function () {
    /**
     * 平台权限tab页接口
     */
    FS.Plugin.PrivilegeManageTabProvider.items.push({
        // tab视图
        tab: function (contentWidth, contentHeight, viewOnly, hasTool, onCustomSelect) {
            var tools = [{
                name: 'view',
                //编辑按钮事件
                onToolClick: function (e, treeNode, isSelect) {
                    if (FS.isGradeAuthorityOpen()) {
                        //有授权
                        if (!isSelect && (treeNode["design"] !== 0)) {
                            FSPM.VIEW.reportTableTree.cancelSelectedNodeWithCascade(treeNode, 1);
                        }
                    }
                    if (FS.isEditReportAuthorityOpen()) {
                        //有编辑
                        if (!isSelect && (treeNode["edit"] !== 0)) {
                            FSPM.VIEW.reportTableTree.cancelSelectedNodeWithCascade(treeNode, tools.length - 1);
                        }
                    }
                    e.stopEvent();
                }
            }];
            if (FS.isGradeAuthorityOpen()) {
                //开启了分级授权才有授权工具
                tools.push({
                    name: 'design',
                    //编辑按钮事件
                    onToolClick: function (e, treeNode, isSelect) {
                        if (isSelect && (treeNode["view"] !== 1)) {
                            FSPM.VIEW.reportTableTree.selectNodeWithCascade(treeNode, true, 0);
                        }
                        if (FS.isEditReportAuthorityOpen()) {
                            if (!isSelect && (treeNode["edit"] !== 0)) {
                                FSPM.VIEW.reportTableTree.cancelSelectedNodeWithCascade(treeNode, tools.length - 1);
                            }
                        }
                        e.stopEvent();
                    }
                });
            }
            var dataSetting = getEntryTableTreeSetting(null, hasTool ? tools.length : 0);
            var toolConfig = {
                tooltype: 'check',
                toolMargin: 10,
                alwaysShowTools: true,
                tools: tools,
                onItemToolClick: function () {
                    FSPM.VIEW.saveEntryCache();
                }
            };
            if (viewOnly) {
                toolConfig.toolUnEditAble = true;
            }
            var result = {
                title: BI.i18nText("BI-Data_Setting"),
                content: {
                    type: 'fstabletree',
                    height: contentHeight,
                    width: contentWidth,
                    treeID: "mytree",
                    setting: dataSetting,
                    widgetName: "mywidget"
                },
                width: 100
            };
            if (hasTool) {
                $.extend(result.content, toolConfig);
            }
            return result;

            function getRootNamesById(id) {
                switch (id) {
                    case BICst.DATA_CONFIG_AUTHORITY.DATA_CONNECTION:
                        return BI.i18nText("BI-Data_Connection");
                    case BICst.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER:
                        return BI.i18nText("BI-Packages_Man");
                    case BICst.DATA_CONFIG_AUTHORITY.PACKAGE_AUTHORITY:
                        return BI.i18nText("BI-Package_Authority_Manager");
                    case BICst.DATA_CONFIG_AUTHORITY.MULTI_PATH_SETTING:
                        return BI.i18nText("BI-Multi_Path_Man");
                    case BICst.DATA_CONFIG_AUTHORITY.FINE_INDEX_UPDATE:
                        return BI.i18nText("BI-Cube_Updates_Setting");
                }
            }

            function getEntryTableTreeSetting(clickFunc, toolLength) {
                function filter(parentNode, node, newNodes, result) {
                    if (!newNodes) {
                        return null;
                    }

                    if (!result) {
                        result = [];
                    }
                    for (var i = 0, l = newNodes.length; i < l; i++) {
                        var item = newNodes[i];
                        var zItem = {};
                        var rootName = getRootNamesById(item.id);
                        zItem.id = item.id;
                        zItem.pId = item.pId;
                        zItem.name = BI.isNotNull(rootName) ? rootName : item.text;
                        zItem.open = false;
                        zItem.type = item.type;
                        zItem.description = item.description;
                        zItem.isAllChildrenIn = item.isAllChildrenIn;
                        if (item.ChildNodes && item.ChildNodes.length > 0) {
                            filter(parentNode, node, item.ChildNodes, result);
                        }
                        if (FSPM.CONTROL._alreadyExists(zItem.id, result)) {
                            continue;
                        }
                        result.push(zItem);
                    }
                    return result;
                };
                return {
                    async: {
                        enable: true,
                        url: FR.servletURL + "?op=fr_bi_base&cmd=get_data_config_authorities",
                        dataFilter: filter,
                        data: {
                            serverID: FS.serverID
                        }
                    },
                    view: {
                        showIcon: false,  //不显示每个节点前的ICON
                        selectedMulti: toolLength > 0
                    },
                    data: {
                        simpleData: {
                            enable: true,
                            idKey: "id",
                            pidKey: "pId",
                            rootPId: "0-1"
                        }
                    },
                    callback: {
                        onClick: clickFunc,
                        beforeClick: function () {
                            if (toolLength > 0) {
                                return false;
                            }
                        }
                    }
                };
            }
        },
        // 保存回调
        save: function () {
            FR.Msg.toast("The custom tab data has been saved");
        },
        // 表头工具栏点击事件
        authorityToolAction: function (col) {
            FR.Msg.toast("BI");
        }
    });
})();
