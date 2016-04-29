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
        var self = this;
        this.permissionTree = this._buildWest();
        BI.createWidget({
            type: "bi.border",
            element: vessel,
            items: {
                north: {el: this._buildNorth(), height: 40},
                west: {el: this.permissionTree},
                east: {el: this._buildEast()}
            }
        });
    },
    load: function () {
        var self = this;
        var packageTree = self.model.get("packageTree");
        console.log(packageTree);
        this.permissionTree.populate(packageTree);
    },

    local: function () {
        return true;
    },
    refresh: function () {
        this.readData(true);
    },
    _buildNorth: function () {
        return BI.createWidget({
            type: "bi.htape",
            items: [
                {
                    el: {
                        type: "bi.label",
                        heiht: 25,
                        text: "packageList"
                    },
                    width: 100
                },
                {
                    el: {
                        type: "bi.button",
                        height: 25,
                        text: "settings",
                        handler: function () {
                            switchTree.switchSelect();
                        }

                    },
                    width: 25
                }
            ]
        });
    },

    _buildWest: function () {
        var TREES = [];
        return switchTree = BI.createWidget({
            type: "bi.switch_tree",
            items: BI.deepClone(TREES)
        });


    },
    _buildEast: function () {
        return BI.createWidget({
            type: 'bi.editor'

        })
    }
})
