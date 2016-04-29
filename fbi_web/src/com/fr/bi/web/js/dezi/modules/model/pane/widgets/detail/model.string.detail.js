//控件详细设置界面选择图表类型
BIDezi.StringDetailModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.StringDetailModel.superclass._defaultConfig.apply(this, arguments), {
            dimensions: {},
            view: {},
            name: "",
            type: BICst.Widget.STRING,
            value: {}
        });
    },

    _static: function () {

    },

    change : function(changed){

    },

    splice: function(old, key1, key2){
        if(key1 === "dimensions"){
            var views = this.get("view");
            views[BICst.REGION.DIMENSION1] = [];
            this.set("view", views);
        }
    },

    local: function () {
        if (this.has("addDimension")) {
            var dimension = this.get("addDimension");
            var src = dimension.src;
            var dId = dimension.dId;
            var dimensions = this.get("dimensions");
            var view = this.get("view");
            //维度指标基本属性
            if(!dimensions[dId]){
                dimensions[dId] = {
                    name: src.name,
                    _src: src._src,
                    type: src.type,
                    sort: {type: BICst.SORT.ASC, target_id: dId}
                };
                view[BICst.REGION.DIMENSION1] = view[BICst.REGION.DIMENSION1] || [];
                view[BICst.REGION.DIMENSION1].push(dId);
                this.set({
                    "dimensions": dimensions,
                    "view": view
                });
            }
            return true;
        }
        return false;
    },

    _init: function () {
        BIDezi.StringDetailModel.superclass._init.apply(this, arguments);
    }
});