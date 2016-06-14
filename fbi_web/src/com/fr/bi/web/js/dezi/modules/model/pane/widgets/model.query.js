/**
 * Created by Young's on 2016/5/5.
 */
BIDezi.QueryModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.QueryModel.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BIDezi.QueryModel.superclass._init.apply(this, arguments);
        //初始添加进去的时候，应该要把所有的控件过滤条件缓存起来
        Data.SharingPool.put("control_filters", BI.Utils.getControlCalculations());
    },

    local: function () {
        if (this.has("expand")) {
            this.get("expand");
            return true;
        }
        return false;
    }
});