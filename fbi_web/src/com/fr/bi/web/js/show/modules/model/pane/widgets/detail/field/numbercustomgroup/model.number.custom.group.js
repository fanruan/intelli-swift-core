/**
 * @class BIShow.NumberCustomGroupModel
 * @extend BI.Model
 * 指标过滤model
 */
BIShow.NumberCustomGroupModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BIShow.NumberCustomGroupModel.superclass._defaultConfig.apply(this, arguments), {

        });
    },

    _init: function(){
        BIShow.NumberCustomGroupModel.superclass._init.apply(this, arguments);
    }
});