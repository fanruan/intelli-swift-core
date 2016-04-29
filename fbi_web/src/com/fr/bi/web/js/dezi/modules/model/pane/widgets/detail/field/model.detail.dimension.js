/**
 * @class BIDezi.DetailDimensionModel
 * @extend BI.Model
 *
 */
BIDezi.DetailDimensionModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BIDezi.DetailDimensionModel.superclass._defaultConfig.apply(this, arguments), {

        });
    },

    _init: function(){
        BIDezi.DetailDimensionModel.superclass._init.apply(this, arguments);
    },

    local: function(){
        if(this.has("valueChange")){
            var value = this.get("valueChange");
            this.set("value", value);
            return true;
        }
        return false;
    }
});