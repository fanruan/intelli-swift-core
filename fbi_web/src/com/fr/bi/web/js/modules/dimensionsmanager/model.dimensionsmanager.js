/**
 * 维度管理器Model
 *
 * Created by GUY on 2016/3/17.
 * @class BI.DimensionsManagerModel
 * @extends FR.OB
 */
BI.DimensionsManagerModel = BI.inherit(FR.OB, {

    _defaultConfig: function () {
        return BI.extend(BI.DimensionsManagerModel.superclass._defaultConfig.apply(this, arguments), {
            wId: ""
        });
    },

    _init: function () {
        BI.DimensionsManagerModel.superclass._init.apply(this, arguments);
        this.viewMap = {};
        this.dimensionsMap = {};
        this.populate();
    },

    _join: function (newType) {
        var self = this;
        var wId = this.options.wId;
        this.viewMap[newType] = this.viewMap[newType] || {};
        this.dimensionsMap[newType] = this.dimensionsMap[newType] || {};
        //向viewMap中添加新添加的dimension
        BI.each(this.viewMap[this.type], function (regionId, dIds) {
            BI.each(dIds, function (idx, dId) {
                var find = BI.find(self.viewMap[newType], function (regionId, ds) {
                    return BI.contains(ds, dId);
                });
                if (BI.isNull(find)) {
                    if (regionId < BICst.REGION.TARGET1) {
                        self.viewMap[newType][BICst.REGION.DIMENSION1] = self.viewMap[newType][BICst.REGION.DIMENSION1] || [];
                        self.viewMap[newType][BICst.REGION.DIMENSION1].push(dId);
                    } else {
                        self.viewMap[newType][BICst.REGION.TARGET1] = self.viewMap[newType][BICst.REGION.TARGET1] || [];
                        self.viewMap[newType][BICst.REGION.TARGET1].push(dId);
                    }
                }
            })
        });
        //从viewMap中删除已被删除的dimension
        BI.each(this.viewMap[newType], function (regionId, dIds) {
            BI.each(dIds, function (idx, dId) {
                var result = BI.find(self.viewMap[self.type], function (regionId, ds) {
                    return BI.contains(ds, dId);
                });
                if (BI.isNull(result)) {
                    BI.remove(dIds, dId);
                }
            })
        });

        // 维度used属性显示逻辑
        // 1、各个类型不受切换影响；2、表格不受影响；
        // 3、图表的分类或系列最多只能有一个显示；4、当图表的used指标多于1的时候，系列不可用也不勾选
        var dimensions = BI.Utils.getWidgetDimensionsByID(wId);

        //处理切换后的
        if (newType === BICst.WIDGET.TABLE ||
            newType === BICst.WIDGET.CROSS_TABLE ||
            newType === BICst.WIDGET.COMPLEX_TABLE) {
            //去掉被删掉的，添加新的
            var oldMap = this.dimensionsMap[newType];
            BI.each(oldMap, function(id, map){
                if(BI.isNull(dimensions[id])) {
                    delete oldMap[id];
                }
            });
            BI.each(dimensions, function(id, map){
                if(BI.isNull(oldMap[id])) {
                    oldMap[id] = BI.deepClone(map);
                }
            });
            return;
        }
        //图表
        var usedTargets = BI.Utils.getAllUsableTargetDimensionIDs(wId);
        BI.each(this.viewMap[newType], function (regionId, dIds) {
            var dim1Found = false, dim2Found = false, series = [];
            BI.each(dIds, function (i, dId) {
                if (regionId === BICst.REGION.DIMENSION1) {
                    if (dim1Found === true) {
                        dimensions[dId].used = false;
                        self.dimensionsMap[newType][dId] = dimensions[dId];
                    }
                    if (dim1Found === false) {
                        BI.Utils.isDimensionUsable(dId) && (dim1Found = true);
                        self.dimensionsMap[newType][dId] = dimensions[dId];
                    }
                    return;
                }
                if (regionId === BICst.REGION.DIMENSION2) {
                    if(usedTargets.length > 1) {
                        BI.Utils.isDimensionUsable(dId) && (dim2Found = true);
                        self.dimensionsMap[newType][dId] = dimensions[dId];
                    } else {
                        if (dim2Found === true) {
                            dimensions[dId].used = false;
                            self.dimensionsMap[newType][dId] = dimensions[dId];
                        }
                        if (dim2Found === false) {
                            BI.Utils.isDimensionUsable(dId) && (dim2Found = true);
                            self.dimensionsMap[newType][dId] = dimensions[dId];
                        }
                    }
                    return;
                }
                self.dimensionsMap[newType][dId] = dimensions[dId];
            });
        });
        var oldDims = self.dimensionsMap[newType];
        BI.each(oldDims, function(id, dim){
            if(BI.isNull(dimensions[id])){
                delete oldDims[id];
            }
        });
    },

    setType: function (type) {
        var t = type;
        if(type >= BICst.MAP_TYPE.WORLD){
            this.sub_type = t;
            t = BICst.WIDGET.MAP;
        }
        this._join(t);
        this.type = t;
    },

    getType: function () {
        return this.type;
    },

    setViews: function (view) {
        this.viewMap[this.type] = view;
        this.dimensionsMap[this.type] = BI.deepClone(BI.Utils.getWidgetDimensionsByID(this.options.wId));
    },

    getViews: function () {
        return this.viewMap[this.type];
    },

    getValue: function () {
        var v = {
            type: this.type,
            view: this.viewMap[this.type] || {},
            dimensions: this.dimensionsMap[this.type] || {}
        };
        this.type === BICst.WIDGET.MAP && BI.extend(v, {sub_type: this.sub_type});
        return v;
    },

    populate: function () {
        var o = this.options;
        this.type = BI.Utils.getWidgetTypeByID(o.wId);
        this.viewMap[this.type] = BI.Utils.getWidgetViewByID(o.wId);
        this.dimensionsMap[this.type] = BI.Utils.getWidgetDimensionsByID(o.wId);
    }
});