/**
 * Created by GUY on 2016/5/16.
 * 
 * @class BI.DetailTablePopupDetailDimensionModel
 * @extend BI.Model
 *
 */
BI.DetailTablePopupDetailDimensionModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BI.DetailTablePopupDetailDimensionModel.superclass._defaultConfig.apply(this, arguments), {

        });
    },

    _init: function(){
        BI.DetailTablePopupDetailDimensionModel.superclass._init.apply(this, arguments);
    },

    change: function(changed){
        if(BI.has(changed, "name")){

        }
    },

    local: function(){
        return false;
    }
});