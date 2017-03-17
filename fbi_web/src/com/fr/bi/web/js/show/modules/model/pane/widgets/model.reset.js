/**
 * Created by Young's on 2016/5/5.
 */
BIShow.ResetModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BIShow.ResetModel.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function(){
        BIShow.ResetModel.superclass._init.apply(this, arguments);
    }
});