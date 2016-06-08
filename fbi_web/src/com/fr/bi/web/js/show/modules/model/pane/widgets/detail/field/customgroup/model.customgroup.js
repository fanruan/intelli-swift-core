BIShow.CustomGroupModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BIShow.CustomGroupModel.superclass._defaultConfig.apply(this, arguments), {

        });
    },

    _init: function(){
        BIShow.CustomGroupModel.superclass._init.apply(this, arguments);
    }
});