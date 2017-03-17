BIShow.CustomSortModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BIShow.CustomSortModel.superclass._defaultConfig.apply(this, arguments), {

        });
    },

    _init: function(){
        BIShow.CustomSortModel.superclass._init.apply(this, arguments);
    }
});