/**
 * 选择文本字段
 *
 * Created by GUY on 2015/11/10.
 * @class BI.FineBIService
 * @extends BI.Widget
 */

BI.FineBIService = BI.inherit(BI.Widget, {

    _const: {
        data: [{
            type: "bi.finebi_service_expander",
            title: {
                text: BI.i18nText("BI-PrePare_Data"),
                value: "BI-PrePare_Data"
            },
            items: [{
                text: BI.i18nText("BI-Data_Connection_Man"),
                value: "BI-Data_Connection_Man"
            }]
        }, {
            type: "bi.finebi_service_expander",
            title: {
                text: BI.i18nText("BI-Package_Data"),
                value: "BI-Package_Data"
            },
            items: [{
                text: BI.i18nText("BI-Packages_Man"),
                value: "BI-Packages_Man"
            }, {
                text: BI.i18nText("BI-Multi_Path_Man"),
                value: "BI-Multi_Path_Man"
            }]
        }, {
            type: "bi.finebi_service_expander",
            title: {
                text: BI.i18nText("BI-Permissions_Setting"),
                value: "BI-Permissions_Setting"
            },
            items: [{
                text: BI.i18nText("BI-Permissions_Man"),
                value: "BI-Permissions_Man"
            }]
        }, {
            type: "bi.finebi_service_expander",
            title: {
                text: BI.i18nText("BI-Cube_Information"),
                value: "BI-Cube_Information"
            },
            items: [{
                text: BI.i18nText("BI-Cube_Updates_Setting"),
                value: "BI-Cube_Updates_Setting"
            }]
        }]
    },

    _defaultConfig: function () {
        return BI.extend(BI.FineBIService.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-finebi-service"
        });
    },

    _init: function () {
        BI.FineBIService.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.button_tree = BI.createWidget({
            type: "bi.button_tree",
            element: this.element,
            items: this._createItems(),
            layouts: [{
                type: "bi.vertical"
            }]
        });
        this.button_tree.on(BI.ButtonTree.EVENT_CHANGE, function () {
            self.fireEvent(BI.FineBIService.EVENT_CHANGE, arguments);
        });
    },

    /**
     * 数据连接
     * 业务包管理
     * 多路径设置
     * 权限配置管理
     * FineIndex更新
     */
    _createItems: function () {
        var authNodes = Data.SharingPool.get("authNodes");
        var items = [];
        this.leaves = [];
        if (BI.isNotNull(authNodes)) {
            var connItems = [], packItems = [], authItems = [], fineIndexItems = [];
            if (BI.isNotNull(authNodes[BICst.DATA_CONFIG_AUTHORITY.DATA_CONNECTION])) {
                connItems.push({
                    text: BI.i18nText("BI-Data_Connection_Man"),
                    value: "BI-Data_Connection_Man"
                });
            }
            if (connItems.length > 0) {
                items.push({
                    type: "bi.finebi_service_expander",
                    title: {
                        text: BI.i18nText("BI-PrePare_Data"),
                        value: "BI-PrePare_Data"
                    },
                    items: connItems
                })
            }
            if (BI.isNotNull(authNodes[BICst.DATA_CONFIG_AUTHORITY.PACKAGE_MANAGER])) {
                packItems.push({
                    text: BI.i18nText("BI-Packages_Man"),
                    value: "BI-Packages_Man"
                });
            }
            if (authNodes[BICst.DATA_CONFIG_AUTHORITY.MULTI_PATH_SETTING] === true) {
                packItems.push({
                    text: BI.i18nText("BI-Multi_Path_Man"),
                    value: "BI-Multi_Path_Man"
                })
            }
            if (packItems.length > 0) {
                items.push({
                    type: "bi.finebi_service_expander",
                    title: {
                        text: BI.i18nText("BI-Package_Data"),
                        value: "BI-Package_Data"
                    },
                    items: packItems
                })
            }
            if (authNodes[BICst.DATA_CONFIG_AUTHORITY.PACKAGE_AUTHORITY] === true) {
                authItems.push({
                    text: BI.i18nText("BI-Permissions_Man"),
                    value: "BI-Permissions_Man"
                })
            }
            if (authItems.length > 0) {
                items.push({
                    type: "bi.finebi_service_expander",
                    title: {
                        text: BI.i18nText("BI-Permissions_Setting"),
                        value: "BI-Permissions_Setting"
                    },
                    items: authItems
                })
            }
            if (authNodes[BICst.DATA_CONFIG_AUTHORITY.FINE_INDEX_UPDATE] === true) {
                fineIndexItems.push({
                    text: BI.i18nText("BI-Cube_Updates_Setting"),
                    value: "BI-Cube_Updates_Setting"
                })
            }
            if (fineIndexItems.length > 0) {
                items.push({
                    type: "bi.finebi_service_expander",
                    title: {
                        text: BI.i18nText("BI-Cube_Information"),
                        value: "BI-Cube_Information"
                    },
                    items: fineIndexItems
                })
            }
            this.leaves = connItems.concat(packItems).concat(authItems).concat(fineIndexItems);
        }
        return items;
    },

    checkValue: function (v) {
        var found = BI.some(this.leaves, function (i, leaf) {
            return leaf.value === v;
        });
        if (!found) {
            v = this.leaves[0].value;
        }
        return v;
    },

    setValue: function (v) {
        this.button_tree.setValue(v);
    },

    getValue: function () {
        return this.button_tree.getValue();
    }
});
BI.FineBIService.EVENT_CHANGE = "FineBIService.EVENT_CHANGE";
$.shortcut('bi.finebi_service', BI.FineBIService);