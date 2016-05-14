/**
 * Created by wuk on 16/4/18.
 */
BIConf.PermissionManageView = BI.inherit(BI.View, {

    _defaultConfig: function () {
        return BI.extend(BIConf.PermissionManageView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: ""
        })
    },
    _init: function () {
        BIConf.PermissionManageView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        this.main = BI.createWidget({
            type: "bi.border",
            element: vessel,
            items: {
                west: {el: this._builtPackageTree(), width: 400},
                center: {el: this._buildAuthorityPane()}
            }
        });
    },
    load: function () {
    },

    local: function () {
        return true;
    },
    refresh: function () {

    },

    _builtPackageTree: function () {
        var self = this;
        this.packageTree = BI.createWidget({
            type: "bi.package_authority_tree"
        });
        this.packageTree.on(BI.PackageANdAuthorityTree.EVENT_TYPE_CHANGE, function () {
            self.authorityTabs.setSelect(0);
            self.authorityPaneInitMain.populate(JSON.parse(self.packageTree.getPackageIds()), self.packageTree.getSelectType());
            /*修改标题*/
            self._setHeadTitle(JSON.parse(self.packageTree.getPackageIds()), self.packageTree.getSelectType());
        })
        this.packageTree.on(BI.PackageANdAuthorityTree.EVENT_SELECT_CHANGE, function () {
            if (self.packageTree.getSelectType()!=1) {
                self.authorityTabs.setSelect(0);
            }
            self.authorityPaneInitMain.populate(JSON.parse(self.packageTree.getPackageIds()), self.packageTree.getSelectType())
            /*修改标题*/
            self._setHeadTitle(JSON.parse(self.packageTree.getPackageIds()), self.packageTree.getSelectType());
        });
        return this.packageTree;
    },
    _buildAuthorityPane: function () {
        return this.pane = BI.createWidget({
            type: "bi.border",
            items: {
                north: {el: this._showTitle(), height: 40},
                center: {
                    el: this._buildAuthorityTabs(),
                    top: 0,
                    left: 0,
                    right: 0
                }
            }
        })
    },


    _showTitle: function () {
        this.title = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText('BI-Permissions_Setting')
        });
        return this.title;
    },
    //设置标题
    _setHeadTitle: function (packageId, selectType) {
        var self = this;
        switch (selectType) {
            case BI.PackageANdAuthorityTree.SelectType.SingleSelect:
                if (packageId.length != 0) {
                    self.title.setText(BI.Utils.getPackageNameByID4Conf(packageId[0]) + '  ' + BI.i18nText('BI-Permissions_Setting'));
                }
                break;
            case BI.PackageANdAuthorityTree.SelectType.MultiSelect:
                self.title.setText(BI.i18nText('BI-Permissions_Setting') + '配置   ' + packageId.length + '个业务包');
                break;
        }
    },
    _buildAuthorityTabs: function () {
        var self = this;
        self.authorityTabs = BI.createWidget({
            type: "bi.tab",
            tab: null,
            defaultShowIndex: 0,
            cardCreator: BI.bind(this._createTabs, this)
        });
        return this.authorityTabs;
    },
    _createTabs: function (type) {
        var self = this;
        this.authorityPaneRoleMain = BI.createWidget({
            type: "bi.authority_pane_role_main"
        });
        this.authorityPaneInitMain = BI.createWidget({
            type: "bi.authority_pane_init_main"
        });

        this.authorityPaneRoleMain.on(BI.authorityPaneRoleMain.EVENT_CHANGE, function () {
            alert('点击保存后的效果');
            self.authorityTabs.setSelect(0);
            self.authorityPaneInitMain.populate([], 0)
        })
        this.authorityPaneInitMain.on(BI.authorityPaneInitMain.EVENT_CHANGE, function () {
            self.authorityTabs.setSelect(1);
            self.authorityPaneRoleMain.populate(self.packageTree.getPackageIds());
        });

        switch (type) {
            case 0:
                return this.authorityPaneInitMain;
            case 1:
;
                return this.authorityPaneRoleMain;
        }
    }
});
