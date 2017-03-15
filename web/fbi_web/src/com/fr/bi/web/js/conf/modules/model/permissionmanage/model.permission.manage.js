/**
 * Created by kary on 16/4/18.
 */
BIConf.PermissionManageModel = BI.inherit(BI.Model, {

    _defaultConfig: function () {
        return BI.extend(BIConf.PermissionManageModel.superclass._defaultConfig.apply(this, arguments), {
            packageIds:'',
            allRoles:''
        })
    },

    _init: function(){
        BIConf.PermissionManageModel.superclass._init.apply(this, arguments);
    },

    change: function (changed) {

    },

    load: function (data) {
        Data.SharingPool.put("authority_settings", data);
    },

    local: function () {
        
    },

    readURL: function () {
        return this.cmd("get_authority_settings");
    }

});
