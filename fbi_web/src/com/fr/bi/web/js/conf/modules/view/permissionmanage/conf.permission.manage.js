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
        ;
        this.main = BI.createWidget({
            type: "bi.border",
            element: vessel,
            items: {
                west: {el: this._builtPackageTree(), width: 400},
                center: {el: this._buildAuthorityPane()}
            }
        });
    },
    change: function (changed) {
        if (changed.isShow){
        }
    },
    load: function () {
    },

    local: function () {
        return false;
    },
    refresh: function () {
    },

    _builtPackageTree: function () {
        var self = this;
        this.packageTree = BI.createWidget({
            type: "bi.package_authority_tree"
        });
        /*单选模式下,直接点击某个业务包,在右侧权限管理页面,否则显示初始页面,初始页面分批量和单选两种*/
        this.packageTree.on(BI.PackageAndAuthorityTree.EVENT_CHANGE, function () {
            self.model.set('packageIds', JSON.parse(self.packageTree.getPackageIds()));
            self.model.set('selectType',self.packageTree.getSelectType());
            if (self.packageTree.getSelectType() == 0 && JSON.parse(self.packageTree.getPackageIds()).length == 1) {
                // self.authorityInitPane.setVisible(false);
                // self.authorityRolePane.setVisible(true);
            } if(self.packageTree.getSelectType() == 1) {
                // self.authorityInitPane.setVisible(false);
                // self.authorityRolePane.setVisible(true);
            }

            self._setHeadTitle(JSON.parse(self.packageTree.getPackageIds()), self.packageTree.getSelectType());
        })
        return this.packageTree;
    },
    _buildAuthorityPane: function () {
        var self=this;
        this.authorityInitPane = BI.createWidget({
            type: 'bi.authority_pane_init_main'
        });
        this.authorityRolePane = BI.createWidget({
            type: 'bi.authority_pane_role_main'
        });
        this.authorityPane = BI.createWidget({
            type: "bi.border",
            items: {
                north: {el: this._showTitle(), height: 40},
                center: {
                    // el: BI.createWidget({
                    //     type: "bi.adaptive",
                    //     items: [self.authorityInitPane, self.authorityRolePane]
                    // }),
                    el:this.authorityInitPane,
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: -40
                }
            }
        });
        // self.authorityInitPane.setVisible(true);
        // self.authorityRolePane.setVisible(false);

        return this.authorityPane;
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
            case BI.PackageAndAuthorityTree.SelectType.SingleSelect:
                self.title.setText(packageId.length != 0 ? BI.Utils.getPackageNameByID4Conf(packageId[0]) : '' + BI.i18nText('BI-Permissions_Setting'));
                break;
            case BI.PackageAndAuthorityTree.SelectType.MultiSelect:
                self.title.setText(BI.i18nText('BI-Permissions_Setting') + '配置   ' + packageId.length + '个业务包');
                break;
        }
    },
});
