/**
 * @class BIDezi.DetailTableDetailModel
 * @extend BI.Model
 * 明细表详细设置
 */
BIDezi.DetailTableDetailModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.DetailTableDetailModel.superclass._defaultConfig.apply(this, arguments), {
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
            this._setDefaultRelation(dimensions);
            var allIds = BI.keys(dimensions);
            var filterValue = this.get("filter_value");
            BI.each(filterValue, function (id, filter) {
                !allIds.contains(id) && delete filterValue[id];
            });
            this.set({
                view: views,
                dimensions: dimensions,
                filter_value: filterValue
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
                var result = BI.find(changed.dimensions, function (did, dimension) {
                    return !BI.has(prev.dimensions, did);
                });
                BI.Broadcasts.send(BICst.BROADCAST.SRC_PREFIX + result._src.id, true);
            }
        }
    },

    _createDimName: function (fieldName) {
        return BI.Func.createDistinctName(this.get("dimensions"), fieldName);
    },

    local: function () {
        var self = this;
        if (this.has("addDimension")) {
            var dimension = this.get("addDimension");
            var view = this.get("view");
            var src = dimension.src;
            var dId = dimension.dId;
            var dimensions = this.get("dimensions");
            if (!dimensions[dId]) {
                //维度指标基本属性
                dimensions[dId] = {
                    name: this._createDimName(src.name),
                    _src: src._src,
                    type: src.type,
                    sort: {type: BICst.SORT.NONE, target_id: dId},
                    group: {type: BICst.GROUP.NO_GROUP},
                    used: true
                };

                //设置所有纬度指标的target_relation
                this._setDefaultRelation(dimensions);
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
                this.set({
                    "dimensions": dimensions,
                    "view": view
                });
            }
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
            dimension_map[targetTableId].target_relation = target_relation[dId];
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
        if (BI.size(paths) >= 1) {
            relationMap[targetTableId].target_relation = paths[0];
        } else {
            relationMap[targetTableId].target_relation = [];
        }
        return relationMap;
    },


    _setDefaultRelation: function (dimensions) {
        var self = this;
        var viewTableIds = [];
        BI.each(dimensions, function (did, dimension) {
            var tableId = BI.Utils.getTableIdByFieldID(dimension._src.field_id);
            if (BI.isNotNull(tableId)) {
                (!BI.contains(viewTableIds, tableId)) && viewTableIds.push(tableId);
            }
        });
        BI.each(dimensions, function (did, dimension) {
            dimension.dimension_map = self._getDefaultRelation(dimension, BI.firstObject(BI.Utils.getCommonForeignTablesByTableIDs(viewTableIds)));
        });
    },

    refresh: function () {

    }
});
