/**
 * @class BIDezi.DetailTableDetailModel
 * @extend BI.Model
 * 明细表详细设置
 */
BIDezi.DetailTableDetailModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.DetailTableDetailModel.superclass._defaultConfig.apply(this, arguments), {
            name: "",
            view: {
                "10000": []
            },
            dimensions: {},
            type: BICst.WIDGET.DETAIL,
            settings: {},
            filter_value: {}
        })
    },

    _init: function () {
        BIDezi.DetailTableDetailModel.superclass._init.apply(this, arguments);
    },


    splice: function (old, key1, key2) {
        if (key1 === "dimensions") {
            var views = this.get("view");
            BI.each(views, function (region, arr) {
                BI.remove(arr, function (i, id) {
                    return key2 === id
                })
            });
            var dimensions = this.get("dimensions");
            //this._setDefaultRelation(dimensions, key2, old);
            var allIds = BI.keys(dimensions);
            var filterValue = this.get("filter_value");
            BI.each(filterValue, function (id, filter) {
                !allIds.contains(id) && delete filterValue[id];
            });
            var sortSequence = this.get("sort_sequence") || [];
            BI.remove(sortSequence, function (i, item) {
                return item === key2;
            });
            this.set({
                view: views,
                dimensions: dimensions,
                filter_value: filterValue,
                sort_sequence: sortSequence
            });
        }
        if (key1 === "dimensions") {
            BI.Broadcasts.send(BICst.BROADCAST.SRC_PREFIX + old._src.id);
            BI.Broadcasts.send(BICst.BROADCAST.DIMENSIONS_PREFIX + this.get("id"));
            //全局维度增删事件
            BI.Broadcasts.send(BICst.BROADCAST.DIMENSIONS_PREFIX);
        }
    },

    change: function (changed, prev) {
        var self = this;
        if (BI.has(changed, "dimensions")) {
            if (BI.size(changed.dimensions) !== BI.size(prev.dimensions)) {
                BI.Broadcasts.send(BICst.BROADCAST.DIMENSIONS_PREFIX + this.get("id"));
                //全局维度增删事件
                BI.Broadcasts.send(BICst.BROADCAST.DIMENSIONS_PREFIX);
            }
            if (BI.size(changed.dimensions) > BI.size(prev.dimensions)) {
                var result = BI.filter(changed.dimensions, function (did, dimension) {
                    return !BI.has(prev.dimensions, did);
                });
                if (BI.isNotEmptyArray(result)) {
                    BI.each(result, function(idx, dimension){
                        BI.Broadcasts.send(BICst.BROADCAST.SRC_PREFIX + dimension._src.id, true);
                    });
                }

            }
        }
    },

    _createDimName: function (fieldName) {
        return BI.Func.createDistinctName(this.get("dimensions"), fieldName);
    },

    local: function () {
        var self = this;
        if (this.has("addDimension")) {
            var addDimensions = this.get("addDimension");
            var dimensions = this.get("dimensions");
            var view = this.get("view");
            var srcs = BI.isArray(addDimensions.src) ? addDimensions.src : [addDimensions.src];
            var dIds = BI.isArray(addDimensions.dId) ? addDimensions.dId : [addDimensions.dId];
            BI.each(dIds, function (idx, dId) {
                if (!dimensions[dId]) {
                    var src = srcs[idx];
                    dimensions[dId] = {
                        name: self._createDimName(src.name),
                        _src: src._src,
                        type: src.type,
                        sort: {type: BICst.SORT.NONE, target_id: dId},
                        group: {type: BICst.GROUP.NO_GROUP},
                        used: true
                    };

                    //设置所有纬度指标的target_relation
                    self._setDefaultRelation(dimensions, dId);
                    view[BICst.REGION.DIMENSION1] || (view[BICst.REGION.DIMENSION1] = []);
                    if (!BI.contains(view[BICst.REGION.DIMENSION1], dId)) {
                        view[BICst.REGION.DIMENSION1].push(dId);
                    }

                    //维度复用时所带信息
                    if (BI.has(src, "group")) {
                        dimensions[dId].group = src.group;
                    }
                    if (BI.has(src, "sort")) {
                        dimensions[dId].sort = src.sort;
                    }
                    if (BI.has(src, "filter_value")) {
                        dimensions[dId].filter_value = src.filter_value;
                    }
                }
            });
            this.set({
                "dimensions": dimensions,
                "view": view
            });
            return true;
        }


        if (this.has("setRelation")) {
            var setRelation = this.get("setRelation");
            var dimensions = this.get("dimensions");
            var relationItem = setRelation.relationItem;
            var target_relation = relationItem.target_relation;
            var targetTableId = relationItem.targetTableId;
            var dId = setRelation.dId;
            var dim = dimensions[dId];
            var dimension_map = {};
            dimension_map[targetTableId] = {};
            dimension_map[targetTableId].target_relation = target_relation[dId] || [];
            dim.dimension_map = dimension_map;
            this.set("dimensions", dimensions);
            return true;
        }

        if (this.has("sorted")) {
            var sorted = this.get("sorted");
            view[BICst.REGION.DIMENSION1] = sorted.dimensions;
            this.set("view", view);
            return true;
        }
        return false;
    },


    _getDefaultRelation: function (dimension, targetTableId) {
        var self = this;
        var relationMap = {};
        var tableId = BI.Utils.getTableIdByFieldID(dimension._src.field_id);
        var paths = BI.Utils.getPathsFromTableAToTableB(tableId, targetTableId);
        relationMap[targetTableId] = {};
        if (tableId === targetTableId) {
            relationMap[targetTableId].target_relation = [];
        } else {
            if (BI.size(paths) >= 1) {
                relationMap[targetTableId].target_relation = paths[0];
            } else {
                relationMap[targetTableId].target_relation = [];
            }
        }
        return relationMap;
    },


    _setDefaultRelation: function (dimensions, dId) {
        var self = this;
        //除却当前参与计算的维度所在表
        var viewTableIds = [];
        //包括当前参与计算的维度所在表
        var allTableIds = [];
        //当前公共子表
        var currentCommonTableId = null;
        var map =  BI.Utils.getDimensionMapByDimensionID(BI.firstKey(dimensions));
        if(BI.isNotNull(map)){
            currentCommonTableId = BI.firstKey(map);
        }
        BI.each(dimensions, function (did, dimension) {
            var tableId = BI.Utils.getTableIdByFieldID(dimension._src.field_id);
            if (BI.isNotNull(tableId)) {
                if(did !== dId){
                    viewTableIds.pushDistinct(tableId);
                    allTableIds.pushDistinct(tableId);
                }else{
                    allTableIds.pushDistinct(tableId);
                }
            }
        });
        var isFromSameTable = false;
        if(BI.has(dimensions, dId)){
            isFromSameTable = BI.isNotNull(BI.find(viewTableIds, function(idx, tableId){
                return tableId === BI.Utils.getTableIdByFieldID(dimensions[dId]._src.field_id)
            }));
        }
        var defaultCommonTable = BI.firstObject(BI.Utils.getCommonForeignTablesByTableIDs(allTableIds));
        if(isFromSameTable === false){
            BI.each(dimensions, function (did, dimension) {
                dimension.dimension_map = self._getDefaultRelation(dimension, defaultCommonTable);
            });
        }else{
            dimensions[dId].dimension_map = self._getDefaultRelation(dimensions[dId], currentCommonTableId)
        }
    },

    refresh: function () {

    }
});