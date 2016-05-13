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
                center: {el: this._buildAuthorityPane(), height: 500, width: 40}
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
        /*单选模式下,直接点击某个业务包,在右侧权限管理页面,否则显示初始页面,初始页面分批量和单选两种*/
        this.packageTree.on(BI.PackageANdAuthorityTree.EVENT_CHANGE, function () {
            if (0 == self.packageTree.getSelectType() && JSON.parse(self.packageTree.getPackageIds()).length == 1) {
                self.authorityPaneInitMain.populate(JSON.parse(self.packageTree.getPackageIds()), i);
            } else {
            self.authorityPaneInitMain.populate(JSON.parse(self.packageTree.getPackageIds()), self.packageTree.getSelectType());
        }
            /*修改标题*/
            self._setHeadTitle(JSON.parse(self.packageTree.getPackageIds()), self.packageTree.getSelectType());
        });
        return this.packageTree;
    },
    _buildAuthorityPane: function () {
        return this.pane = BI.createWidget({
            type: "bi.border",
            items: {
                north: {el: this._showTitle(), width: 40, height: 40},
                center: {
                    el: this._buildAuthorityTabs(),
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: -40,
                    height: 500,
                    width: 400
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
    _setHeadTitle: function (packageId,selectType) {
        var self = this;
        switch (selectType){
            case BI.PackageANdAuthorityTree.SelectType.SingleSelect:
                self.title.setText(BI.Utils.getPackageNameByID4Conf(packageId[0]) + '  ' + BI.i18nText('BI-Permissions_Setting'));
                break;
            case BI.PackageANdAuthorityTree.SelectType.MultiSelect:
                self.title.setText(BI.i18nText('BI-Permissions_Setting') + '配置   ' + packageId.length + '个业务包');
                break;
        }
    },
    _buildAuthorityTabs: function () {

        this.tabs= BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                el:this.authorityPaneInitMain
            }, {
                el: this.authorityPaneRoleMain
            }],
        });
        this.authorityPaneInitMain = BI.createWidget({
            type: "bi.authority_pane_init_main"
        });
        this.authorityPaneInitMain.on(BI.AuthorityPaneInitMain.EVENT_CHANGE, function () {
            self.authorityPaneInitMain.setSelect(1);
            self.authorityPaneInitMain.populate(JSON.parse(self.packageTree.getPackageIds()), BI.AuthorityPaneEditSelectedView.EidtType.MultiEdit);
        });
        this.authorityPaneRoleMain = BI.createWidget({
            type: "bi.authority_pane_role_main"
        });
        this.authorityPaneInitMain.on(BI.AuthorityPaneInitMain.EVENT_CHANGE, function () {
            self.authorityPaneInitMain.populate(JSON.parse(self.packageTree.getPackageIds()), BI.AuthorityPaneEditSelectedView.EidtType.MultiEdit);
        });
        return this.tabs;
    }
});
