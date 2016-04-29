//控件详细设置界面选择图表类型
BIDezi.YearDetailModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.YearDetailModel.superclass._defaultConfig.apply(this, arguments), {
            dimensions: {},
            view: {},
            name: "",
            type: BICst.Widget.YEAR,
            value: null
        });
    },

    _static: function () {

    },

    change : function(changed){

    },

    splice: function(old, key1, key2){
        if(key1 === "dimensions"){
            var views = this.get("view");
            BI.each(views, function (region, arr) {
                BI.remove(arr, function (i, id) {
                    return key2 == id;
                })
            });
            this.set("view", views);
        }
    },

    local: function () {
        if (this.has("addDimension")) {
            var dimension = this.get("addDimension");
            var src = dimension.src;
            var dId = dimension.dId;
            var dimensions = this.get("dimensions");
            if(!dimensions[dId]){
                //维度指标基本属性
                dimensions[dId] = {
                    name: src.name,
                    _src: src._src,
                    type: src.type
                };
            }
            this.set("dimensions", dimensions);
            return true;
        }
        return false;
    },

    _init: function () {
        BIDezi.YearDetailModel.superclass._init.apply(this, arguments);
    }
});