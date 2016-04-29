/**
 * Created by GUY on 2015/6/24.
 */

//详细设置界面选择图表类型
BIDezi.DetailModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.DetailModel.superclass._defaultConfig.apply(this, arguments), {
            dimensions: {},
            view: {},
            name: "",
            type: BICst.Widget.TABLE,
            settings: {},
            filter_value: {}
        });
    },

    _init: function () {
        BIDezi.DetailModel.superclass._init.apply(this, arguments);
    },

    similar: function (ob, key) {
        if (key === "dimensions") {
            ob.name = this._createDimName(ob.name);
            return ob;
        }
    },

    splice: function (old, key1, key2) {
        var self = this;
        if (key1 === "dimensions") {
            var views = this.get("view"), dimensions = this.get("dimensions");
            BI.each(views, function (region, arr) {
                if ((region === BICst.REGION.DIMENSION1 || region === BICst.REGION.DIMENSION2) && arr.contains(key2)) {
                    var linkageValues = BI.Utils.getLinkageValuesByID(self.get("id"));
                    self.set("clicked", linkageValues);
                }
                BI.remove(arr, function (i, id) {
                    return key2 == id
                })
            });
            BI.each(dimensions, function (i, dimension) {
                if (BI.isNotNull(dimension.dimension_map)) {
                    delete dimension.dimension_map[key2];
                }
            });
            var allIds = BI.keys(dimensions);
            var filterValue = this.get("filter_value");
            BI.each(filterValue, function(id, filter){
                !allIds.contains(id) && delete filterValue[id];
            });
            this.set({"dimensions": dimensions, view: views, filter_value: filterValue});
        }
        if (key1 === "dimensions") {
            BI.Broadcasts.send(this.get("id") + "usable");
            //全局的表使用广播
            BI.Broadcasts.send(BICst.BROADCAST.TABLE_USABLE);
        }
    },

    duplicate: function (copy, key1, key2) {
        if (key1 === "dimensions") {
            var views = this.get("view");
            BI.each(views, function (region, arr) {
                BI.each(arr, function (i, id) {
                    if (key2 == id) {
                        arr.push(copy);
                        return false;
                    }
                })
            });
            this.set("view", views);
        }
    },

    change: function (changed, prev) {
        var self = this;
        if (BI.has(changed, "dimensions")) {
            //勾选 添加
            var currDims = changed.dimensions, preDims = prev.dimensions;
            BI.some(currDims, function (dId, dimension) {
                if (BI.Utils.isDimensionByDimensionID(dId) && (BI.isNull(preDims[dId]) || preDims[dId].used !== dimension.used)) {
                    var linkageValues = BI.Utils.getLinkageValuesByID(self.get("id"));
                    self.set("clicked", linkageValues);
                    return true;
                }
            });
        }
        if (BI.has(changed, "dimensions")) {
            if (BI.size(changed.dimensions) !== BI.size(prev.dimensions)) {
                BI.Broadcasts.send(self.get("id") + "usable");
                BI.Broadcasts.send(BICst.BROADCAST.TABLE_USABLE);
            }
        }
    },

    refresh: function () {

    },

    _createDimName: function (fieldName) {
        return BI.Func.createDistinctName(this.get("dimensions"), fieldName);
    },


    local: function () {
        var self = this;
        if (this.has("addDimension")) {
            var dimensions = this.get("dimensions");
            var dimension = this.get("addDimension");
            var view = this.get("view");
            var src = dimension.src;
            var dId = dimension.dId;
            var regionType = dimension.regionType;
            if (!dimensions[dId]) {
                //维度指标基本属性
                dimensions[dId] = src;
                dimensions[dId].name = this._createDimName(src.name);
                dimensions[dId].used = true;
                //构造dimension_map
                var fId = src._src.field_id;

                //view中添加dimension
                view[regionType] || (view[regionType] = []);
                if (!BI.contains(view[regionType], dId)) {
                    view[regionType].push(dId);
                }
                if (regionType >= BICst.REGION.TARGET1) {//拖的是指标
                    var targetTableId = BI.Utils.getTableIdByFieldID(fId);
                    BI.each(dimensions, function (idx, dimension) {
                        if (idx === dId) {
                            return;
                        }
                        dimension.dimension_map = dimension.dimension_map || {};
                        if (BI.Utils.isDimensionByDimensionID(idx)) {
                            var dimensionTableId = BI.Utils.getTableIDByDimensionID(idx);
                            var path = BI.Utils.getPathsFromTableAToTableB(dimensionTableId, targetTableId);
                            if (path.length === 1) {
                                var target_relation = path[0];
                                dimension.dimension_map[dId] = {
                                    _src: dimension._src,
                                    target_relation: target_relation
                                };
                            }
                        }
                    })
                }
                if (regionType < BICst.REGION.TARGET1) {//拖的是维度
                    dimensions[dId].dimension_map = {};
                    var dimensionTableId = BI.Utils.getTableIdByFieldID(fId);
                    BI.each(dimensions, function (idx, dimension) {
                        if (idx === dId) {
                            return;
                        }
                        if (!BI.Utils.isDimensionByDimensionID(idx)) {
                            var path = BI.Utils.getPathsFromTableAToTableB(dimensionTableId, BI.Utils.getTableIDByDimensionID(idx));
                            if (path.length === 1) {
                                var target_relation = path[0];
                                dimensions[dId].dimension_map[idx] = {
                                    _src: {
                                        field_id: fId
                                    },
                                    target_relation: target_relation
                                };
                            }
                        }
                    })
                }
                this.set({
                    dimensions: dimensions,
                    view: view
                });
            }
            return true;
        }
        return false;
    }
});