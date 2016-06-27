//控件详细设置界面选择图表类型
BIDezi.NumberDetailModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.NumberDetailModel.superclass._defaultConfig.apply(this, arguments), {
            dimensions: {},
            view: {},
            type: BICst.WIDGET.NUMBER,
            value: {}
        });
    },

    _static: function () {

    },

    change: function (changed, prev) {
        if (BI.has(changed, "dimensions")) {
            if (BI.size(changed.dimensions) !== BI.size(prev.dimensions)) {
                BI.Broadcasts.send(BICst.BROADCAST.DIMENSIONS_PREFIX + this.get("id"));
                BI.Broadcasts.send(BICst.BROADCAST.DIMENSIONS_PREFIX);
            }
            if (BI.size(changed.dimensions) > BI.size(prev.dimensions)) {
                var result = BI.find(changed.dimensions, function (did, dimension) {
                    return !BI.has(prev.dimensions, did);
                });
                BI.Broadcasts.send(BICst.BROADCAST.SRC_PREFIX + result._src.id, true);
            }
        }
    },

    splice: function (old, key1, key2) {
        if (key1 === "dimensions") {
            var views = this.get("view");
            BI.each(views, function (region, arr) {
                BI.remove(arr, function (i, id) {
                    return key2 == id;
                })
            });
            this.set("view", views);
        }
        if (key1 === "dimensions") {
            BI.Broadcasts.send(BICst.BROADCAST.SRC_PREFIX + old._src.id);
            BI.Broadcasts.send(BICst.BROADCAST.DIMENSIONS_PREFIX + this.get("id"));
            //全局维度增删事件
            BI.Broadcasts.send(BICst.BROADCAST.DIMENSIONS_PREFIX);
        }
    },

    local: function () {
        if (this.has("addDimension")) {
            var dimension = this.get("addDimension");
            var src = dimension.src;
            var dId = dimension.dId;
            var dimensions = this.get("dimensions");
            //只能拖入一种类型的字段且只能拖入一次
            var srcIds = [];
            BI.each(dimensions, function (dId, dim) {
                srcIds.push(dim._src.id);
            });
            if (!dimensions[dId] && !srcIds.contains(src._src.id)) {
                //维度指标基本属性
                dimensions[dId] = {
                    name: src.name,
                    _src: src._src,
                    type: src.type
                };
                this.set("dimensions", dimensions);
            }
            return true;
        }
        return false;
    },

    _init: function () {
        BIDezi.NumberDetailModel.superclass._init.apply(this, arguments);
    }
});