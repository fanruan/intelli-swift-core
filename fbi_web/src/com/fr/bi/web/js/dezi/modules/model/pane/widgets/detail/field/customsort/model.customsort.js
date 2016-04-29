BIDezi.CustomSortModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BIDezi.CustomSortModel.superclass._defaultConfig.apply(this, arguments), {

        });
    },

    _init: function(){
        BIDezi.CustomSortModel.superclass._init.apply(this, arguments);
    }
});