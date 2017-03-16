/**
 * Created by roy on 15/11/25.
 * @class  BIConf.AllBusinessPackagesPaneModel
 * @extends BI.Model
 * 管理业务包界面分组的界面
 */
BIConf.AllBusinessPackagesPaneModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIConf.AllBusinessPackagesPaneModel.superclass._defaultConfig.apply(this), {});
    },

    _init: function () {
        BIConf.AllBusinessPackagesPaneModel.superclass._init.apply(this, arguments);
    },

    _deletePackageById: function (id) {
        var packages = this.get("packages");
        var groups = this.get("groups");
        BI.each(groups, function (groupID, groupObj) {
            var groupChildren = groupObj.children;
            BI.any(groupChildren, function (i, pack) {
                if (pack.id === id) {
                    groupObj.children.splice(i, 1);
                    return true
                }
            })
        });
        delete packages[id];
        this.set({
            "groups": groups,
            "packages": packages
        })
    },


    local: function () {
        if (this.has("delete")) {
            var ids = this.get("delete");
            this._deletePackageById(ids);
            return true;
        }
        if (this.has("add")) {
            this.get("add");
            return true;
        }
        return false;
    },

    load: function (data) {
        Data.SharingPool.put("groups", data.groups);
        Data.SharingPool.put("packages", data.packages);
        this.set({
            groups:data.groups,
            packages:data.packages
        },{
            silent:true
        })
    },

    change: function (changed) {
        var self = this;
        if (BI.isNotNull(changed.packages)) {
            Data.SharingPool.put("packages", self.get("packages"));
        }
        if (BI.isNotNull(changed.groups)) {
            Data.SharingPool.put("groups", self.get("groups"));
        }
    },


    readURL: function () {
        return this.cmd("get_business_package_group");
    },

    updateURL: function () {
        return this.cmd("update_package_group");
    },

    patchURL: function () {
        return this.cmd("remove_business_package");
    }
});
