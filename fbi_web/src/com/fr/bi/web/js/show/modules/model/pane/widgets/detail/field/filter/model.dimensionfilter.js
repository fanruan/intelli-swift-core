/**
 * @class BIShow.DimensionFilterModel
 * @extend BI.Model
 * 指标过滤model
 */
BIShow.DimensionFilterModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.DimensionFilterModel.superclass._defaultConfig.apply(this, arguments), {});
    },

    _init: function () {
        BIShow.DimensionFilterModel.superclass._init.apply(this, arguments);
    },

    local: function () {
        //if(this.has("changeCondition")){
        //    var conditions = this.get("changeCondition");
        //    if(BI.isEmpty(conditions)){
        //        this.unset("andor");
        //        this.unset("condition");
        //        return true;
        //    }
        //    this.set("andor", conditions.andor);
        //    this.set("condition", conditions.condition);
        //    return true;
        //}
        return false;
    }
});
