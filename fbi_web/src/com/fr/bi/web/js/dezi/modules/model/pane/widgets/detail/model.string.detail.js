//控件详细设置界面选择图表类型
BIDezi.StringDetailModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.StringDetailModel.superclass._defaultConfig.apply(this, arguments), {
            name: "",
            dimensions: {},
            view: {},
            type: BICst.WIDGET.STRING,
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
            var changedDimensions = changed.dimensions;
            var prevDimensions = prev.dimensions;
            if(BI.isNotEmptyObject(changedDimensions)){
                BI.Broadcasts.send(BICst.BROADCAST.SRC_PREFIX + BI.firstObject(changedDimensions)._src.id);
            }
            if(BI.isNotEmptyObject(prevDimensions)){
                BI.Broadcasts.send(BICst.BROADCAST.SRC_PREFIX + BI.firstObject(prevDimensions)._src.id);
            }
            this.set("value", {});
        }
    },

    splice: function (old, key1, key2) {
        if (key1 === "dimensions") {
            var views = this.get("view");
            views[BICst.REGION.DIMENSION1] = [];
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
            var addDimensions = this.get("addDimension");
            var dimensions = this.get("dimensions");
            var view = this.get("view");
            var srcs = BI.isArray(addDimensions.src) ? addDimensions.src : [addDimensions.src];
            var dIds = BI.isArray(addDimensions.dId) ? addDimensions.dId : [addDimensions.dId];
            BI.each(dIds, function (idx, dId) {
                if (!dimensions[dId]) {
                    var src = srcs[idx];
                    dimensions = {};
                    dimensions[dId] = {
                        name: src.name,
                        _src: src._src,
                        type: src.type,
                        sort: {type: BICst.SORT.ASC, target_id: dId}
                    };
                    view[BICst.REGION.DIMENSION1] = [dId];
                }
            });
            this.set({
                "dimensions": dimensions,
                "view": view
            });
            return true;
        }
        return false;
    },

    _init: function () {
        BIDezi.StringDetailModel.superclass._init.apply(this, arguments);
    }
});