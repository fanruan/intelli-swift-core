/**
 * @class BIDezi.TargetFilterModel
 * @extend BI.Model
 * 指标过滤model
 */
BIDezi.TargetFilterModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BIDezi.TargetFilterModel.superclass._defaultConfig.apply(this, arguments), {

        });
    },

    _init: function(){
        BIDezi.TargetFilterModel.superclass._init.apply(this, arguments);
    },

    local: function(){
        if(this.has("changeCondition")){
            var conditions = this.get("changeCondition");
            if(BI.isEmpty(conditions)){
                this.unset("andor");
                this.unset("condition");
                return true;
            }
            this.set("andor", conditions.andor);
            this.set("condition", conditions.condition);
            return true;
        }
        return false;
    }
});