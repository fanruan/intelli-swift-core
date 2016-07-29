/**
 * Created by GUY on 2015/6/24.
 */

//详细设置界面选择图表类型
BIDezi.DetailModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.DetailModel.superclass._defaultConfig.apply(this, arguments), {
            dimensions: {},
            view: {},
            type: BICst.WIDGET.TABLE,
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
            var isTarget = false;
            var views = this.get("view"), dimensions = this.get("dimensions");
            BI.each(views, function (region, arr) {
                if ((region === BICst.REGION.DIMENSION1 || region === BICst.REGION.DIMENSION2) && arr.contains(key2)) {
                    var linkageValues = BI.Utils.getLinkageValuesByID(self.get("id"));
                    self.set("clicked", linkageValues);
                }
                BI.remove(arr, function (i, id) {
                    if(key2 === id){
                        isTarget = true;
                        return true;
                    }
                })
            });
            BI.each(dimensions, function (i, dimension) {
                if (BI.isNotNull(dimension.dimension_map)) {
                    delete dimension.dimension_map[key2];
                }
                if(isTarget === true){
                    checkFilter(dimension.filter_value);
                    if(BI.has(dimension, "sort") && BI.has(dimension.sort, "sort_target") && dimension.sort.sort_target === key2){
                        dimension.sort = {
                            type: BICst.SORT.ASC
                        };
                    }
                    var tSort = self.get("sort");
                    if(BI.isNotNull(tSort) && tSort.sort_target === key2) {
                        self.set("sort", {}, {silent: true});
                    }
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

        function checkFilter(filter) {
            if(BI.isNull(filter)){
                return;
            }
            var filterType = filter.filter_type, filterValue = filter.filter_value;
            if (filterType === BICst.FILTER_TYPE.AND || filterType === BICst.FILTER_TYPE.OR) {
                BI.each(filterValue, function (i, value) {
                    checkFilter(value);
                });
                return;
            }
            if(filter.target_id === key2){
                filter.filter_type = BICst.FILTER_TYPE.EMPTY_CONDITION;
                delete filter.target_id;
                delete filter.filter_value;
            }
        }
    },

    duplicate: function (copy, key1, key2) {
        if (key1 === "dimensions") {
            var regionType = BICst.REGION.DIMENSION1;
            var views = this.get("view"), dimensions = this.get("dimensions");
            BI.each(views, function (region, arr) {
                BI.each(arr, function (i, id) {
                    if (key2 === id) {
                        regionType = region;
                        arr = arr.splice(i + 1, 0, copy);
                        return false;
                    }
                })
            });
            if (regionType >= BICst.REGION.TARGET1) {//复制的是指标
                BI.each(dimensions, function (idx, dimension) {
                    if (BI.Utils.isDimensionByDimensionID(idx)) {
                        dimension.dimension_map = dimension.dimension_map || {};
                        var dimFieldId = BI.Utils.getFieldIDByDimensionID(idx);
                        var tsrFieldId = BI.Utils.getFieldIDByDimensionID(copy);
                        //var dimSrc = BI.Utils.getDimensionSrcByID(idx);
                        //var tarSrc = BI.Utils.getDimensionSrcByID(copy);
                        //if(BI.has(dimSrc, "relation")){
                        //    dimFieldId = BI.Utils.getForeignIdFromRelation(dimSrc.relation);
                        //}
                        //if(BI.has(tarSrc, "relation")){
                        //    tsrFieldId = BI.Utils.getForeignIdFromRelation(tarSrc.relation);
                        //}
                        var path = BI.Utils.getPathsFromFieldAToFieldB(dimFieldId, tsrFieldId);
                        if (path.length === 1) {
                            var target_relation = path[0];
                            dimension.dimension_map[copy] = {
                                _src: dimension._src,
                                target_relation: [target_relation]
                            };
                        }
                    }
                });
            }
            this.set({"dimensions": dimensions, "view": views});
            //this.set("view", views);
        }
        if (key1 === "dimensions") {
            var dimensions = this.cat("dimensions");
            BI.Broadcasts.send(BICst.BROADCAST.SRC_PREFIX + dimensions[copy]._src.id);
            BI.Broadcasts.send(BICst.BROADCAST.DIMENSIONS_PREFIX + this.get("id"));
            //全局维度增删事件
            BI.Broadcasts.send(BICst.BROADCAST.DIMENSIONS_PREFIX);
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
                var dims = BI.deepClone(changed.dimensions), preDims = BI.deepClone(prev.dimensions);
                var view = this.get("view");
                if(wType !== BICst.WIDGET.MAP){
                    //地图不参与分类和系列的单选复选join
                    //分类和系列join
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
                }
                //指标两个以上勾选的时候，系列全部不勾选，并且注意要disable
                var usableT = BI.Utils.getAllUsableTargetDimensionIDs(this.get("id"));
                var type = this.get("type");
                //然而gis地图无视此规则
                if(usableT.length > 1 && type !== BICst.WIDGET.GIS_MAP) {
                    BI.each(view[BICst.REGION.DIMENSION2], function(i, d){
                        dims[d].used = false;
                    });
                    //饼图和仪表盘有多个指标的时候，维度框不勾选且灰化= =
                    if(type === BICst.WIDGET.PIE || type === BICst.WIDGET.DASHBOARD){
                        BI.each(view[BICst.REGION.DIMENSION1], function(i, d){
                            dims[d].used = false;
                        });
                    }
                    //对比柱状/面积/条形图,范围面积,瀑布,气泡,力学,散点,漏斗这些指标区域是单选的
                    if(type === BICst.WIDGET.COMPARE_AXIS || type === BICst.WIDGET.COMPARE_AREA ||
                        type === BICst.WIDGET.COMPARE_BAR || type === BICst.WIDGET.RANGE_AREA || type === BICst.WIDGET.FALL_AXIS||
                        type === BICst.WIDGET.BUBBLE || type === BICst.WIDGET.FORCE_BUBBLE || type === BICst.WIDGET.MULTI_AXIS_COMBINE_CHART ||
                        type === BICst.WIDGET.SCATTER){
                        var preTar1Select = [], preTar2Select = [], preTar3Select = [];
                        BI.each(preDims, function (dId, dim) {
                            if (dim.used === true) {
                                if (BI.Utils.getRegionTypeByDimensionID(dId) === BICst.REGION.TARGET1) {
                                    preTar1Select.push(dId);
                                }
                                if (BI.Utils.getRegionTypeByDimensionID(dId) === BICst.REGION.TARGET2) {
                                    preTar2Select.push(dId);
                                }
                                if (BI.Utils.getRegionTypeByDimensionID(dId) === BICst.REGION.TARGET3) {
                                    preTar3Select.push(dId);
                                }
                            }
                        });
                        var tar1Change = false, tar2Change = false, tar3Change = false;
                        BI.each(dims, function (dId, dim) {
                            var rType = BI.Utils.getRegionTypeByDimensionID(dId);
                            if (dim.used === true) {
                                if (rType === BICst.REGION.TARGET1) {
                                    if(!preTar1Select.contains(dId)){
                                        //添加指标
                                        if(BI.isNotEmptyArray(preTar1Select) && BI.size(dims) !== BI.size(preDims)){
                                            dims[dId].used = false;
                                        }else{
                                            //指标间切换
                                            tar1Change = true;
                                        }
                                    }
                                }
                                if (rType === BICst.REGION.TARGET2) {
                                    if(!preTar2Select.contains(dId)){
                                        if(BI.isNotEmptyArray(preTar2Select) && BI.size(dims) !== BI.size(preDims)){
                                            dims[dId].used = false;
                                        }else{
                                            tar2Change = true;
                                        }
                                    }
                                }
                                if (rType === BICst.REGION.TARGET3) {
                                    if(!preTar3Select.contains(dId)){
                                        if(BI.isNotEmptyArray(preTar3Select) && BI.size(dims) !== BI.size(preDims)){
                                            dims[dId].used = false;
                                        }else{
                                            tar3Change = true;
                                        }
                                    }
                                }
                            }
                        });
                        if (tar1Change === true) {
                            BI.each(preTar1Select, function (i, dId) {
                                dims[dId].used = false;
                            });
                        }
                        if (tar2Change === true) {
                            BI.each(preTar2Select, function (i, dId) {
                                dims[dId].used = false;
                            })
                        }
                        if (tar3Change === true) {
                            BI.each(preTar3Select, function (i, dId) {
                                dims[dId].used = false;
                            })
                        }
                    }
                    //地图,由第二个指标区域决定其他区域的单选多选
                    if(type === BICst.WIDGET.MAP){
                        var preTar2Select = [], preTar1Select = [];
                        BI.each(preDims, function (dId, dim) {
                            if (dim.used === true) {
                                if (BI.Utils.getRegionTypeByDimensionID(dId) === BICst.REGION.TARGET1) {
                                    preTar1Select.push(dId);
                                }
                                if (BI.Utils.getRegionTypeByDimensionID(dId) === BICst.REGION.TARGET2) {
                                    preTar2Select.push(dId);
                                }
                            }
                        });
                        var tar2Change = false, tar1Change = false;
                        BI.each(dims, function (dId, dim) {
                            var rType = BI.Utils.getRegionTypeByDimensionID(dId);
                            if (dim.used === true) {
                                if (rType === BICst.REGION.TARGET1) {
                                    if(!preTar1Select.contains(dId)){
                                        if(BI.isNotEmptyArray(preTar1Select) && BI.size(dims) !== BI.size(preDims)){
                                        }else{
                                            tar1Change = true;
                                        }
                                    }
                                }
                                if (rType === BICst.REGION.TARGET2) {
                                    if(!preTar2Select.contains(dId)){
                                        if(BI.isNotEmptyArray(preTar2Select) && BI.size(dims) !== BI.size(preDims)){
                                            dims[dId].used = false;
                                        }else{
                                            tar2Change = true;
                                        }
                                    }
                                }
                            }
                        });
                        if (tar2Change === true) {
                            BI.each(preTar2Select, function (i, dId) {
                                dims[dId].used = false;
                            })
                        }
                        var find = BI.find(view[BICst.REGION.TARGET2], function(idx, dId){
                            return dims[dId].used === true;
                        });
                        if(BI.isNotNull(find)){
                            if (tar1Change === true) {
                                BI.each(preTar1Select, function (i, dId) {
                                    dims[dId].used = false;
                                })
                            }else{
                                var firstSelectedTarget = false;
                                BI.each(view[BICst.REGION.TARGET1], function(idx, dId){
                                    firstSelectedTarget === true && (dims[dId].used = false);
                                    if(dims[dId].used === true){
                                        firstSelectedTarget = true;
                                    }
                                })
                            }
                        }
                    }
                }
                this.set("dimensions", dims);
            }
        }

        //这边不能光靠changed中是否包含dimensions来确定是不是增加减少了dimension
        //因为指标复制会改变dimension的dimensionmap
        var hasDifferentDimension = true;
        if(!BI.has(changed, "dimensions")){
            hasDifferentDimension = false;
        }else{
            if(BI.size(changed.dimensions) !== BI.size(prev.dimensions)){
            }else{
                var dimensionIds = BI.keys(changed.dimensions);
                hasDifferentDimension = BI.isNotNull(BI.find(prev.dimensions, function(dId, dimension){
                    return !BI.contains(dimensionIds, dId);
                }));
            }
        }
        if (BI.has(changed, "view") && hasDifferentDimension === false) {
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
                if(wType === BICst.WIDGET.COMPARE_AXIS || wType === BICst.WIDGET.COMPARE_AREA ||
                    wType === BICst.WIDGET.COMPARE_BAR || wType === BICst.WIDGET.RANGE_AREA || wType === BICst.WIDGET.FALL_AXIS||
                    wType === BICst.WIDGET.BUBBLE || wType === BICst.WIDGET.FORCE_BUBBLE || wType === BICst.WIDGET.MULTI_AXIS_COMBINE_CHART ||
                    wType === BICst.WIDGET.SCATTER){
                    BI.each(view, function (region, dims) {
                        if (region >= BICst.REGION.TARGET1) {
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
                }
                if(wType === BICst.WIDGET.MAP){
                    BI.each(view, function (region, dims) {
                        if (region === BICst.REGION.TARGET2) {
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
                }
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
            var dimension = this.get("addDimension");
            var dimensions = this.get("dimensions");
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
                    BI.each(dimensions, function (idx, dimension) {
                        if (idx === dId) {
                            return;
                        }
                        if (BI.Utils.isDimensionByDimensionID(idx)) {
                            dimension.dimension_map = dimension.dimension_map || {};
                            var dimFieldId = BI.Utils.getFieldIDByDimensionID(idx);
                            //var dimSrc = BI.Utils.getDimensionSrcByID(idx);
                            //if(BI.has(dimSrc, "relation")){
                            //    dimFieldId = BI.Utils.getForeignIdFromRelation(dimSrc.relation);
                            //}
                            //if(BI.has(src._src, "relation")){
                            //    fId = BI.Utils.getForeignIdFromRelation(src._src.relation);
                            //}
                            var path = BI.Utils.getPathsFromFieldAToFieldB(dimFieldId, fId);
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
                if (regionType < BICst.REGION.TARGET1) {//拖的是维度
                    dimensions[dId].dimension_map = {};
                    if (BI.isNotEmptyString(fId)) {
                        BI.each(dimensions, function (idx, dimension) {
                            if (idx === dId) {
                                return;
                            }
                            if (!BI.Utils.isDimensionByDimensionID(idx)) {
                                var tarFieldId = BI.Utils.getFieldIDByDimensionID(idx);
                                //var tarSrc = BI.Utils.getDimensionSrcByID(idx);
                                //if(BI.has(tarSrc, "relation")){
                                //    tarFieldId = BI.Utils.getForeignIdFromRelation(tarSrc.relation);
                                //}
                                //if(BI.has(src._src, "relation")){
                                //    fId = BI.Utils.getForeignIdFromRelation(src._src.relation);
                                //}
                                var path = BI.Utils.getPathsFromFieldAToFieldB(fId, tarFieldId);
                                if (path.length === 1) {
                                    var target_relation = path[0];
                                    dimensions[dId].dimension_map[idx] = {
                                        _src: src._src,
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