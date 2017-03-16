/**
 * Created by roy on 15/11/24.
 */
BIConf.BusinessPackageGroupModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIConf.BusinessPackageGroupModel.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-business-package-group-model"
        })
    },
    _init: function () {
        BIConf.BusinessPackageGroupModel.superclass._init.apply(this, arguments);
    },

    change: function (changed) {
        if (BI.isNotNull(changed.groups)) {
            Data.SharingPool.put("groups", this.get("groups"));
        }
    },

    //load: function () {
    //    Data.SharingPool.put("groups", this.get("groups"));
    //    Data.SharingPool.put("packages", this.get("packages"));
    //},


    readURL: function () {
        return this.cmd("get_business_package_group");
    },

    updateURL: function () {
        return this.cmd("update_package_group");
    }


});