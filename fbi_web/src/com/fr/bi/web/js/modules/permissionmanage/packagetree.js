/**
 * 可以单选多选切换的树
 *用于权限管理中业务包的显示
 * Created by wuk on 16/4/20.
 * @class BI.PackageTree
 * @extends BI.Widget
 */
BI.PackageTree = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.PackageTree.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-switch-tree"
        });
    },

    _init: function () {
        BI.PackageTree.superclass._init.apply(this, arguments);
        var self = this;
        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [
                {
                    el: this._buildNorth(),
                    height: 100
                }
                , {
                    el: this.tree = BI.createWidget({
                        type: "bi.switch_tree",
                        items: BI.Utils.getAllGroupedPackagesTreeSync()
                    })
                }
            ]
        });
        this.tree.on(BI.SwitchTree.EVENT_CHANGE, function () {
            self.fireEvent(BI.PackageTree.EVENT_CHANGE);
        });

    },
    getSelectType: function () {
        return this.tree.getSelect()
    },
    getPackageIds: function () {
        return JSON.stringify(this.tree.getValue());
    },
    _buildNorth: function () {
        var self = this;
        return BI.createWidget({
            type: "bi.htape",
            items: [
                {
                    el: {
                        type: "bi.label",
                        height: 25,
                        text: BI.i18nText('BI-Package_List')
                    },
                    width: 100
                },
                {
                    el: {
                        type: "bi.button",
                        height: 25,
                        text: BI.i18nText('BI-Muti_Setting'),
                        handler: function () {
                            self.tree.switchSelect();
                            self.fireEvent(BI.PackageTree.EVENT_CHANGE);
                        }

                    },
                    width: 25
                }
            ]
        });
    },
    populate: function (items) {
// this.tree.populate(items);
    }
});
BI.PackageTree.SelectType = {
    SingleSelect: BI.Selection.Single,
    MultiSelect: BI.Selection.Multi
}
BI.PackageTree.EVENT_CHANGE = "PackageTree.EVENT_CHANGE";
$.shortcut('bi.package_tree', BI.PackageTree);
