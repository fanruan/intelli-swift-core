/**
 * @class BIDezi.NumberCustomGroupModel
 * @extend BI.Model
 * 指标过滤model
 */
BIDezi.NumberCustomGroupModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BIDezi.NumberCustomGroupModel.superclass._defaultConfig.apply(this, arguments), {

        });
    },

    _init: function(){
        BIDezi.NumberCustomGroupModel.superclass._init.apply(this, arguments);
    }
});