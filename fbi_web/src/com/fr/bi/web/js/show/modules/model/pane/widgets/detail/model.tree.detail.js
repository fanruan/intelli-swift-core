/**
 * Created by wuk on 16/4/15.
 */
BIShow.TreeDetailModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.TreeDetailModel.superclass._defaultConfig.apply(this, arguments), {
            dimensions: {},
            view: {},
            name: "",
            type: BICst.Widget.TREE,
            value: {}
        })
    },

    _init: function () {
        BIShow.TreeDetailModel.superclass._init.apply(this, arguments);
    },

    change : function(changed){

    },

    splice: function(old, key1, key2){
    },

    local: function () {
        return false;
    }

});
