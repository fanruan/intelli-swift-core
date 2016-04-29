/**
 * @class BIConf.UpdateCubePaneModel
 * @extend BI.Model
 * cube更新界面
 */
BIConf.UpdateCubePaneModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BIConf.UpdateCubePaneModel.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function(){
        BIConf.UpdateCubePaneModel.superclass._init.apply(this, arguments);
    },

    local: function(){
        if(this.has("immediateUpdate")){
            this.get("immediateUpdate");
            this.patch({
                noset: true
            });
            return true;
        }
        return false;
    },

    refresh: function(){

    },

    change: function(){

    },

    readURL: function(){

    },

    patchURL: function(){
        return this.cmd("set_cube_generate");
    },

    updateURL: function(){
        return this.cmd("get_cube_generate_status");
    }
});