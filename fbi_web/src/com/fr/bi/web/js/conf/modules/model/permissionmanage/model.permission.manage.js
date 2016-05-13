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
    load: function () {
       
    },

    local: function () {
        if (this.has('allRoles')){
            return true;
        }
        return false;
    },
    change: function () {
    },
    refresh: function () {
        var self=this;
        BI.Utils.getAllAuthority(function (data) {
            self.set("allRoles", data);
        });
    }

});
