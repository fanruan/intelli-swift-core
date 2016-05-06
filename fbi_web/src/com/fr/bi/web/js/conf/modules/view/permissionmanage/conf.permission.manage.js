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
        this.tab.setVisible(false);
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
            type: "bi.package_tree"
        });
        this.packageTree.on(BI.PackageTree.EVENT_CHANGE, function () {
            Data.SharingPool.put("packageId",self.packageTree.getValue());
            self.tab.populate(JSON.parse(self.packageTree.getValue()));
            self._setHeadTitle(JSON.parse(self.packageTree.getValue()));
            self.tab.setVisible(true);

        });
        this.packageTree.populate();
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
    _setHeadTitle: function (packageId) {
        var self = this;
        var packStructure = Data.SharingPool.get("packStructure");
        BI.each(packStructure, function (key) {
            if (packageId == packStructure[key].id) {
                self.title.setText((packStructure[key].text) + '  ' + BI.i18nText('BI-Permissions_Setting'));
                return;
            }
        });
    },
    _buildAuthorityTabs: function () {
        var self = this;
        self.tab = BI.createWidget({
            type: "bi.authority_pane",
        });
        return this.tab;
    }
})

