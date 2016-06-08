/**
 * Created by GUY on 2015/6/24.
 */

//详细设置界面选择图表类型
BIShow.DetailModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.DetailModel.superclass._defaultConfig.apply(this, arguments), {
            dimensions: {},
            view: {},
            name: "",
            type: BICst.WIDGET.TABLE,
            settings: {},
            filter_value: {}
        });
    },

    _init: function () {
        BIShow.DetailModel.superclass._init.apply(this, arguments);
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
            BI.each(filterValue, function (id, filter) {
                !allIds.contains(id) && delete filterValue[id];
            });
            this.set({"dimensions": dimensions, view: views, filter_value: filterValue});
        }
        if (key1 === "dimensions") {
            BI.Broadcasts.send(BICst.BROADCAST.SRC_PREFIX + old._src.id);
            BI.Broadcasts.send(BICst.BROADCAST.DIMENSIONS_PREFIX + this.get("id"));
            //全局维度增删事件
            BI.Broadcasts.send(BICst.BROADCAST.DIMENSIONS_PREFIX);
        }
    },

    duplicate: function (copy, key1, key2) {
        if (key1 === "dimensions") {
            var views = this.get("view");
            BI.each(views, function (region, arr) {
                BI.each(arr, function (i, id) {
                    if (key2 == id) {
                        arr = arr.splice(i + 1, 0, copy);
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
                this.set("clicked", {}, {silent: true});
            }
        }
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
        if (BI.has(changed, "dimensions")) {
            var wType = this.get("type");
            if (wType !== BICst.WIDGET.TABLE &&
                wType !== BICst.WIDGET.CROSS_TABLE &&
                wType !== BICst.WIDGET.COMPLEX_TABLE) {
                //分类和系列
                var dims = BI.deepClone(changed.dimensions), preDims = BI.deepClone(prev.dimensions);
                var view = this.get("view");
                var preDim1Select = [], preDim2Select = [];
                BI.each(preDims, function (dId, dim) {
                    if (dim.used === true) {
                        if (BI.Utils.getRegionTypeByDimensionID(dId) === BICst.REGION.DIMENSION1) {
                            preDim1Select.push(dId);
                        }
                        if (BI.Utils.getRegionTypeByDimensionID(dId) === BICst.REGION.DIMENSION2) {
                            preDim2Select.push(dId);
                        }
                    }
                });
                var dim1Change = false, dim2Change = false;
                BI.each(dims, function (dId, dim) {
                    var rType = BI.Utils.getRegionTypeByDimensionID(dId);
                    if (dim.used === true) {
                        if (rType === BICst.REGION.DIMENSION1) {
                            if(!preDim1Select.contains(dId)){
                                //添加维度
                                if(BI.isNotEmptyArray(preDim1Select) && BI.size(dims) !== BI.size(preDims)){
                                    dims[dId].used = false;
                                }else{
                                    //维度间切换
                                    dim1Change = true;
                                }
                            }
                        }
                        if (rType === BICst.REGION.DIMENSION2) {
                            if(!preDim2Select.contains(dId)){
                                if(BI.isNotEmptyArray(preDim2Select) && BI.size(dims) !== BI.size(preDims)){
                                    dims[dId].used = false;
                                }else{
                                    dim2Change = true;
                                }
                            }
                        }
                    }
                });
                if (dim1Change === true) {
                    BI.each(preDim1Select, function (i, dId) {
                        dims[dId].used = false;
                    });
                }
                if (dim2Change === true) {
                    BI.each(preDim2Select, function (i, dId) {
                        dims[dId].used = false;
                    })
                }
                //指标两个以上勾选的时候，系列全部不勾选，并且注意要disable
                var usableT = BI.Utils.getAllUsableTargetDimensionIDs(this.get("id"));
                if(usableT.length > 1) {
                    BI.each(view[BICst.REGION.DIMENSION2], function(i, d){
                        dims[d].used = false;
                    })
                }
                this.set("dimensions", dims);
            }
        }
        if (BI.has(changed, "view") && !BI.has(changed, "dimensions")) {
            var wType = this.get("type");
            if (wType !== BICst.WIDGET.TABLE &&
                wType !== BICst.WIDGET.CROSS_TABLE &&
                wType !== BICst.WIDGET.COMPLEX_TABLE) {
                var view = changed.view, preView = prev.view;
                var dimensions = BI.deepClone(this.get("dimensions"));
                //region1、2中的维度增加的时候，先看原先中是否有已勾选了的，如果有将拖入的used设置为false
                BI.each(view, function (region, dims) {
                    if (region < BICst.REGION.TARGET1) {
                        var adds = [], isPreSelect = false;
                        BI.each(dims, function(i, dim){
                             if(BI.isNotNull(preView[region]) && !preView[region].contains(dim)){
                                 adds.push(dim);
                             }
                        });
                        if(adds.length > 0) {
                            BI.each(preView[region], function (i, dId) {
                                BI.Utils.isDimensionUsable(dId) && (isPreSelect = true);
                            });
                            if (isPreSelect === true) {
                                BI.each(adds, function(i, add){
                                    dimensions[add].used = false;
                                });
                            }
                        }
                    }
                });
                this.set("dimensions", dimensions);

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
                    if (BI.isNotEmptyString(fId)) {
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
                                        target_relation: [target_relation]
                                    };
                                }
                            }
                        });
                    }
                }
                if (regionType < BICst.REGION.TARGET1) {//拖的是维度
                    dimensions[dId].dimension_map = {};
                    if (BI.isNotEmptyString(fId)) {
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
                                        target_relation: [target_relation]
                                    };
                                }
                            }
                        });
                    }
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