BIDezi.CustomGroupModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BIDezi.CustomGroupModel.superclass._defaultConfig.apply(this, arguments), {

        });
    },

    _init: function(){
        BIDezi.CustomGroupModel.superclass._init.apply(this, arguments);
    }
});