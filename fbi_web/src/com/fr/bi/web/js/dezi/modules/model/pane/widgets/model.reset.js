/**
 * Created by Young's on 2016/5/5.
 */
BIDezi.ResetModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BIDezi.ResetModel.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function(){
        BIDezi.ResetModel.superclass._init.apply(this, arguments);
    }
});