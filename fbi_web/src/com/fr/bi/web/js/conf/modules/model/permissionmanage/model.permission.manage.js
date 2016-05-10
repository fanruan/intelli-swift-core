/**
 * Created by wuk on 16/4/18.
 */
BIConf.PermissionManageModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return _.extend(BIConf.PermissionManageModel.superclass._defaultConfig.apply(this, arguments), {})
    },
    _init: function () {
        BIConf.PermissionManageModel.superclass._init.apply(this, arguments);
    },

    load: function (data) {

    },

    local: function () {
        return false;
    },
    change: function (changed) {
    }
});
/**
 * Created by wuk on 16/4/18.
 */
BIConf.PermissionManageModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return _.extend(BIConf.PermissionManageModel.superclass._defaultConfig.apply(this, arguments), {})
    },
    _init: function () {
        BIConf.PermissionManageModel.superclass._init.apply(this, arguments);
    },
    load: function (data) {
        Data.SharingPool.put("groups", data.groups);
        Data.SharingPool.put("packages", data.packages);
        var packageStructure = BI.Utils.getAllGroupedPackagesTreeAsync();
        Data.SharingPool.put("packageStructure", packageStructure);
        this.set({
            packageTree : packageStructure
        })
    },

    local: function () {
        return false;
    },
    readURL: function () {
        return this.cmd("get_business_package_group");
    },
    change: function (changed) {
        var self = this;
        if (BI.isNotNull(changed.packages)) {
            Data.SharingPool.put("packages", self.get("packages"));
        }
        if (BI.isNotNull(changed.groups)) {
            Data.SharingPool.put("groups", self.get("groups"));
        }
    }
});
