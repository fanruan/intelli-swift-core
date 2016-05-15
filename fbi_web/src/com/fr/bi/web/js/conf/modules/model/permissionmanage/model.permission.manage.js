/**
 * Created by wuk on 16/4/18.
 */
BIConf.PermissionManageModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return _.extend(BIConf.PermissionManageModel.superclass._defaultConfig.apply(this, arguments), {
            packageIds:'',
            allRoles:''
        })
    },
    change: function (changed) {
    },
    load: function () {
    },

    local: function () {
        return false;
    },

    refresh: function () {
        var self = this;
        BI.Utils.getAllAuthority(function (data) {
            self.set("allRoles", data);
            self.set('packageIds', []);
        });

    }

});
