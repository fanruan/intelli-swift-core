/**
 * @class BIShow.DetailTableDetailModel
 * @extend BI.Model
 * 明细表详细设置
 */
BIShow.DetailTableDetailModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.DetailTableDetailModel.superclass._defaultConfig.apply(this, arguments), {
            view: {
                "10000": []
            },
            dimensions: {},
            name: "",
            type: BICst.WIDGET.DETAIL,
            settings: {},
            target_relation: {},
            page: 0
        })
    },

    _init: function () {
        BIShow.DetailTableDetailModel.superclass._init.apply(this, arguments);
    },


    splice: function (old, key1, key2) {
    },

    local: function () {
        var self = this;
        if (this.has("sorted")) {
            var sorted = this.get("sorted");
            view[BICst.REGION.DIMENSION1] = sorted.dimensions;
            this.set("view", view);
            return true;
        }
        return false;
    },

    change: function () {

    },

    refresh: function () {

    }
});
