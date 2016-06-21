BI.ChartDisplayModel = BI.inherit(FR.OB, {

    constants: {
        BUBBLE_REGION_COUNT: 4,
        SCATTER_REGION_COUNT: 3
    },

    _init: function(){
        BI.ChartDisplayModel.superclass._init.apply(this, arguments);
    },

    _getShowTarget: function () {
        var self = this, o = this.options;
        var view = BI.Utils.getWidgetViewByID(o.wId);
        this.cataDid = BI.find(view[BICst.REGION.DIMENSION1], function (idx, did) {
            return BI.Utils.isDimensionUsable(did);
        });
        this.seriesDid = BI.find(view[BICst.REGION.DIMENSION2], function (idx, did) {
            return BI.Utils.isDimensionUsable(did);
        });
        this.targetIds = [];
        BI.each(view, function (regionType, arr) {
            if (regionType >= BICst.REGION.TARGET1) {
                self.targetIds = BI.concat(self.targetIds, arr);
            }
        });
        return this.targetIds = BI.filter(this.targetIds, function (idx, tId) {
            return BI.Utils.isDimensionUsable(tId);
        });

    },

    _formatDataForMap: function (data) {
        var self = this, o = this.options;
        var targetIds = this._getShowTarget();
        var result = [];
        if (BI.has(data, "c")) {
            var obj = (data.c)[0];
            var view = BI.Utils.getWidgetViewByID(o.wId);
            var columnSizeArray = BI.makeArray(BI.isNull(obj) ? 0 : BI.size(obj.s), 0);
            result = BI.map(columnSizeArray, function (idx, value) {
                var type = null;
                if (BI.has(view, BICst.REGION.TARGET2) && BI.contains(view[BICst.REGION.TARGET2], targetIds[idx])) {
                    type = BICst.WIDGET.BUBBLE;
                }
                var adjustData = BI.map(data.c, function (id, item) {
                    var res = {};
                    if (BI.has(view, BICst.REGION.TARGET2) && BI.contains(view[BICst.REGION.TARGET2], targetIds[idx])) {
                        switch(type){
                            case BICst.WIDGET.BUBBLE:
                            case BICst.WIDGET.AXIS:
                            case BICst.WIDGET.PIE:
                            default:
                                res = {
                                    name: item.n,
                                    size: item.s[idx],
                                    targetIds: [targetIds[idx]]
                                };
                        }
                    }else{
                        res = {
                            name: item.n,
                            value: item.s[idx],
                            targetIds: [targetIds[idx]]
                        };
                    }
                    if(BI.has(item, "c")){
                        res.drill = {};
                        res.drill.series = self._formatDataForMap(item);
                        res.drill.geo = {
                            data: BICst.MAP_PATH[BICst.MAP_NAME[res.name]]
                        };
                    }
                    return res;
                });
                var obj = {};
                obj.data = adjustData;
                BI.isNotNull(type) && (obj.type = "bubble");
                obj.name = BI.Utils.getDimensionNameByID(targetIds[idx]);
                return obj;
            });
        }
        return result;
    },

    _formatDataForGISMap: function(){

    },

    _formatDataForAxis: function (da) {
        var self = this, o = this.options;
        var data = this._formatDataForCommon(da);
        if (BI.isEmptyArray(data)) {
            return [];
        }
        var view = BI.Utils.getWidgetViewByID(o.wId);
        if(BI.has(view, BICst.REGION.DIMENSION2) && BI.isNotEmptyArray(view[BICst.REGION.DIMENSION2]) && this.targetIds.length === 1){
            return [data];
        }
        var array = [];
        BI.each(this.targetIds, function (idx, tId) {
            if (BI.has(view, BICst.REGION.TARGET1) && BI.contains(view[BICst.REGION.TARGET1], tId)) {
                array.length === 0 && array.push([]);
                array[0].push(data[idx]);
            }
            if(BI.has(view, BICst.REGION.TARGET2) && BI.contains(view[BICst.REGION.TARGET2], tId)) {
                while (array.length < 2){array.push([]);}
                array[1].push(data[idx]);
            }
            if(BI.has(view, BICst.REGION.TARGET3) && BI.contains(view[BICst.REGION.TARGET3], tId)){
                while (array.length < 3){array.push([]);}
                array[2].push(data[idx]);
            }
        });
        return array;
    },

    _formatDataForBubble: function (data) {
        var self = this, o = this.options;
        var targetIds = this._getShowTarget();
        var view = BI.Utils.getWidgetViewByID(o.wId);
        var result = BI.find(view, function (region, arr) {
            return BI.isEmptyArray(arr);
        });
        if (BI.isNotNull(result) || BI.size(view) < this.constants.BUBBLE_REGION_COUNT) {
            return [];
        }
        return [BI.map(data.c, function (idx, item) {
            var obj = {};
            var name = item.n;
            var dGroup = BI.Utils.getDimensionGroupByID(self.cataDid);
            if (BI.isNotNull(dGroup) && dGroup.type === BICst.GROUP.YMD) {
                var date = new Date(BI.parseInt(name));
                name = date.print("%Y-%X-%d");
            }
            obj.data = [{
                x: item.s[1],
                y: item.s[0],
                size: item.s[2],
                targetIds: [targetIds[0], targetIds[1], targetIds[2]]
            }];
            obj.name = name;
            return obj;
        })];
    },

    _formatDataForScatter: function (data) {
        var self = this, o = this.options;
        var targetIds = this._getShowTarget();
        var view = BI.Utils.getWidgetViewByID(o.wId);
        var result = BI.find(view, function (region, arr) {
            return BI.isEmptyArray(arr);
        });
        if (BI.isNotNull(result) || BI.size(view) < this.constants.SCATTER_REGION_COUNT) {
            return [];
        }
        return [BI.map(data.c, function (idx, item) {
            var obj = {};
            var name = item.n;
            var dGroup = BI.Utils.getDimensionGroupByID(self.cataDid);
            if (BI.isNotNull(dGroup) && dGroup.type === BICst.GROUP.YMD) {
                var date = new Date(BI.parseInt(name));
                name = date.print("%Y-%X-%d");
            }
            obj.name = name;
            obj.data = [{
                x: item.s[1],
                y: item.s[0],
                targetIds: [targetIds[0], targetIds[1]]
            }];
            return obj;
        })];
    },

    _formatDataForCommon: function (data) {
        var self = this, o = this.options;
        var targetIds = this._getShowTarget();
        var cataGroup = BI.Utils.getDimensionGroupByID(self.cataDid);
        var seriesGroup = BI.Utils.getDimensionGroupByID(self.seriesDid);
        if (BI.has(data, "t")) {
            var top = data.t, left = data.l;
            return BI.map(top.c, function (id, tObj) {
                var data = BI.map(left.c, function (idx, obj) {
                    var x = obj.n;
                    if (BI.isNotNull(cataGroup) && cataGroup.type === BICst.GROUP.YMD) {
                        var date = new Date(BI.parseInt(x));
                        x = date.print("%Y-%X-%d");
                    }
                    return {
                        "x": x,
                        "y": obj.s.c[id].s[0],
                        targetIds: [targetIds[0]]
                    };
                });
                var name = tObj.n;
                if (BI.isNotNull(seriesGroup) && seriesGroup.type === BICst.GROUP.YMD) {
                    var date = new Date(BI.parseInt(name));
                    name = date.print("%Y-%X-%d");
                }
                var obj = {};
                obj.data = data;
                obj.name = name;
                return obj;
            });
        }
        if (BI.has(data, "c")) {
            var obj = (data.c)[0];
            var columnSizeArray = BI.makeArray(BI.isNull(obj) ? 0 : BI.size(obj.s), 0);
            return BI.map(columnSizeArray, function (idx, value) {
                var adjustData = BI.map(data.c, function (id, item) {
                    var x = item.n;
                    if (BI.isNotNull(cataGroup) && cataGroup.type === BICst.GROUP.YMD) {
                        var date = new Date(BI.parseInt(x));
                        x = date.print("%Y-%X-%d");
                    }
                    return {
                        x: x,
                        y: item.s[idx],
                        targetIds: [targetIds[idx]]
                    };
                });
                var obj = {};
                obj.data = adjustData;
                obj.name = BI.Utils.getDimensionNameByID(targetIds[idx]);
                return obj;
            });
        }
        if(BI.has(data, "s")){
            var type = BI.Utils.getWidgetTypeByID(o.wId);
            if(type === BICst.WIDGET.PIE){
                var adjustData = BI.map(data.s, function (idx, value) {
                    return {
                        x: BI.Utils.getDimensionNameByID(targetIds[idx]),
                        y: value,
                        targetIds: [targetIds[idx]]
                    };
                });
                var obj = {};
                obj.data = adjustData;
                return [obj];
            }else{
                return BI.map(data.s, function (idx, value) {
                    return {
                        name: BI.Utils.getDimensionNameByID(targetIds[idx]),
                        data: [{
                            x: "",
                            y: value,
                            targetIds: [targetIds[idx]]
                        }]
                    };
                });
            }
        }
        return [];
    },

    _formatDataForDashBoard: function (data) {
        var self = this, o = this.options;
        var targetIds = this._getShowTarget();
        var cataGroup = BI.Utils.getDimensionGroupByID(self.cataDid);
        if (BI.has(data, "c")) {
            var adjustData = BI.map(data.c, function (id, item) {
                var seriesName = item.n;
                if (BI.isNotNull(cataGroup) && cataGroup.type === BICst.GROUP.YMD) {
                    var date = new Date(BI.parseInt(seriesName));
                    seriesName = date.print("%Y-%X-%d");
                }
                var data = [{
                    x: BI.Utils.getDimensionNameByID(targetIds[0]),
                    y: item.s[0],
                    targetIds: [targetIds[0]]
                }];
                var obj = {};
                obj.data = data;
                obj.name = seriesName;
                return obj;
            });
            return BI.isEmptyArray(adjustData) ? [] : [adjustData];
        }
        if (BI.has(data, "s")) {
            var obj = {};
            obj.name = "";
            obj.data = BI.map(data.s, function (idx, value) {
                return {
                    x: BI.Utils.getDimensionNameByID(targetIds[idx]),
                    y: value,
                    targetIds: [targetIds[idx]]
                };
            });
            return BI.isEmptyArray(obj.data) ? [] : [[obj]];
        }
        return [];
    },

    _formatDataForAccumulateAxis: function(data){
        var items = this._formatDataForAxis(data);
        return BI.map(items, function(idx, item){
            var i = BI.UUID();
            return BI.map(item, function(id, it){
                return BI.extend({}, it, {stack: i});
            });
        });
    },

    _formatDataForPercentAccumulateAxis: function(data){
        var items = this._formatDataForAxis(data);
        return BI.map(items, function(idx, item){
            var i = BI.UUID();
            return BI.map(item, function(id, it){
                return BI.extend({}, it, {stack: i, stackByPercent: true});
            });
        });
    },

    _formatDataForCompareAxis: function(data){
        var items = this._formatDataForAxis(data);
        return BI.map(items, function(idx, item){
            return BI.map(item, function(id, it){
                if(idx > 0){
                    return BI.extend({}, it, {reversed: true, xAxis: 0});
                }else{
                    return BI.extend({}, it, {reversed: false, xAxis: 1});
                }
            });
        });
    },

    _formatDataForFallAxis: function(data){
        var o = this.options;
        var items = this._formatDataForCommon(data);
        var tables = [], sum = 0;
        var colors = BI.Utils.getWSChartColorByID(o.wId) || [];
        if(BI.isEmptyArray(colors)){
            colors = ["rgb(152, 118, 170)", "rgb(0, 157, 227)"];
        }
        BI.each(items, function(idx, item){
            BI.each(item.data, function(i, t){
                if(t.y < 0){
                    tables.push([t.x, t.y, sum + t.y, t.targetIds]);
                }else{
                    tables.push([t.x, t.y, sum, t.targetIds]);
                }
                sum += t.y;
            });
        });

        return [BI.map(BI.makeArray(2, null), function(idx, item){
            return {
                "data": BI.map(tables, function(id, cell){
                    var axis = BI.extend({targetIds: cell[3]}, {
                        x: cell[0],
                        y: Math.abs(cell[2 - idx])
                    });
                    if(idx === 1){
                        axis.color = cell[2 - idx] < 0 ? colors[1] : colors[0];
                    }else{
                        axis.color = "rgba(0,0,0,0)";
                        axis.borderColor = "rgba(0,0,0,0)";
                        axis.borderWidth = 0;
                        axis.clickColor = "rgba(0,0,0,0)";
                        axis.mouseOverColor = "rgba(0,0,0,0)";
                        axis.tooltip = {
                            enable: false
                        }
                    }
                    return axis;
                }),
                stack: "stackedFall",
                name: ""
            };
        })];
    },

    _formatDataForRangeArea: function(data){
        var o = this.options;
        var items = this._formatDataForCommon(data);
        if(BI.isEmptyArray(items)){
            return [];
        }
        if(items.length === 1){
            return [items];
        }
        var colors = BI.Utils.getWSChartColorByID(o.wId) || [];
        if(BI.isEmptyArray(colors)){
            colors = ["#5caae4"];
        }
        var seriesMinus = [];
        BI.each(items[0].data, function(idx, item){
            var res = items[1].data[idx].y - item.y;
            seriesMinus.push({
                x: items[1].data[idx].x,
                y: res,
                targetIds: items[1].data[idx].targetIds
            });
        });
        items[1] = {
            data: seriesMinus,
            name: items[1].name,
            stack: "stackedArea",
            fillColor: colors[0]
        };
        BI.each(items, function(idx, item){
            if(idx === 0){
                BI.extend(item, {
                    name: items[0].name,
                    fillColorOpacity: 0,
                    stack: "stackedArea",
                    marker: {enabled: false},
                    fillColor: false
                });
            }
        });
        return [items];
    },

    _formatDataForBar: function(data){
        var items = this._formatDataForAxis(data);
        BI.each(items, function(idx, item){
            BI.each(item, function(id, it){
                BI.each(it.data, function(i, t){
                    var tmp = t.x;
                    t.x = t.y;
                    t.y = tmp;
                })
            });
        });
        return items;
    },

    _formatDataForAccumulateBar: function(data){
        var items = this._formatDataForBar(data);
        BI.each(items, function(idx, item){
            var id = BI.UUID();
            BI.each(item, function(i, it){
                it.stack = id;
            });
        });
        return items;
    },

    _formatDataForCompareBar: function(data){
        var items = this._formatDataForBar(data);
        var result = [];
        var i = BI.UUID();
        BI.each(items, function (idx, item) {
            BI.each(item, function (id, it) {
                BI.each(it.data, function (i, t) {
                    if (idx === 0) {
                        t.x = -t.x;
                    }
                });
                it.stack = i;
            })
        });
        BI.each(items, function(idx, item){
            result = BI.concat(result, item);
        });
        return [result];
    },

    _formatDataForForceBubble: function (data) {
        var items = this._formatDataForCommon(data);
        return BI.isEmptyArray(items) ? items : [items];
    },

    getToolTip: function (type) {
        var o = this.options;
        switch (type) {
            case BICst.WIDGET.SCATTER:
                if(this.targetIds < 2){
                    return "";
                }else{
                    return "function(){ return this.seriesName+'<div>(X)" + BI.Utils.getDimensionNameByID(this.targetIds[1]) +":'+ this.x +'</div><div>(Y)"
                        + BI.Utils.getDimensionNameByID(this.targetIds[0]) +":'+ this.y +'</div>'}";
                }
            case BICst.WIDGET.BUBBLE:
                if(this.targetIds < 3){
                    return "";
                }else{
                    return "function(){ return this.seriesName+'<div>(X)" + BI.Utils.getDimensionNameByID(this.targetIds[1]) +":'+ this.x +'</div><div>(Y)"
                        + BI.Utils.getDimensionNameByID(this.targetIds[0]) +":'+ this.y +'</div><div>(大小)" + BI.Utils.getDimensionNameByID(this.targetIds[2])
                        + ":'+ this.size +'</div>'}";
                }
            case BICst.WIDGET.MAP:
                var view = BI.Utils.getWidgetViewByID(o.wId);
                var tIds = [];
                BI.each(view[BICst.REGION.TARGET2], function (idx, dId) {
                    if (BI.Utils.isDimensionUsable(dId)) {
                        tIds.push(dId);
                    }
                });
                BI.each(view[BICst.REGION.TARGET1], function (idx, dId) {
                    if (BI.Utils.isDimensionUsable(dId)) {
                        tIds.push(dId);
                    }
                });
                var tip = "function(){ return this.name";
                BI.each(tIds, function(idx, tId){
                    tip += " + '<div>";
                    tip += BI.Utils.getDimensionNameByID(tId) + ":'";
                    tip += BI.Utils.getRegionTypeByDimensionID(tId) === BICst.REGION.TARGET1 ? "+ this.value + " : "+ this.size + ";
                    tip += "'</div>'";
                });
                tip += "}";
                return tip;
            default:
                return "";
        }
    },

    getCordon: function () {
        var o = this.options;
        var cordon = {};
        var result = [];
        BI.each(BI.Utils.getAllDimensionIDs(o.wId), function(idx, dId){
            var items = BI.map(BI.Utils.getDimensionCordonByID(dId), function(id, cor){
                return {
                    text: cor.cordon_name,
                    value: cor.cordon_value,
                    color: cor.cordon_color
                }
            });
            var regionType = BI.Utils.getRegionTypeByDimensionID(dId);
            if(BI.isNotEmptyArray(items)){
                BI.has(cordon, regionType) === false && (cordon[regionType] = []);
                cordon[regionType] = BI.concat(cordon[regionType], items);
            }
        });
        var type = BI.Utils.getWidgetTypeByID(o.wId);
        if(type === BICst.WIDGET.SCATTER || type === BICst.WIDGET.BUBBLE){
            result.push(BI.isNull(cordon[BICst.REGION.TARGET2]) ? [] : cordon[BICst.REGION.TARGET2]);
            result.push(BI.isNull(cordon[BICst.REGION.TARGET1]) ? [] : cordon[BICst.REGION.TARGET1]);
        }
        result.push(BI.isNull(cordon[BICst.REGION.DIMENSION1]) ? [] : cordon[BICst.REGION.DIMENSION1]);
        result.push(BI.isNull(cordon[BICst.REGION.TARGET1]) ? [] : cordon[BICst.REGION.TARGET1]);
        result.push(BI.isNull(cordon[BICst.REGION.TARGET2]) ? [] : cordon[BICst.REGION.TARGET2]);
        result.push(BI.isNull(cordon[BICst.REGION.TARGET3]) ? [] : cordon[BICst.REGION.TARGET3]);
        return result;
    },

    parseChartData: function (data) {
        var self = this, o = this.options;
        switch (BI.Utils.getWidgetTypeByID(o.wId)) {
            case BICst.WIDGET.BUBBLE:
                return this._formatDataForBubble(data);
            case BICst.WIDGET.SCATTER:
                return this._formatDataForScatter(data);
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
                return this._formatDataForAxis(data);
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.ACCUMULATE_RADAR:
                return this._formatDataForAccumulateAxis(data);
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                return this._formatDataForPercentAccumulateAxis(data);
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.COMPARE_AREA:
                return this._formatDataForCompareAxis(data);
            case BICst.WIDGET.FALL_AXIS:
                return this._formatDataForFallAxis(data);
            case BICst.WIDGET.RANGE_AREA:
                return this._formatDataForRangeArea(data);
            case BICst.WIDGET.BAR:
                return this._formatDataForBar(data);
            case BICst.WIDGET.ACCUMULATE_BAR:
                return this._formatDataForAccumulateBar(data);
            case BICst.WIDGET.COMPARE_BAR:
                return this._formatDataForCompareBar(data);
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                return this._formatDataForAxis(data);
            case BICst.WIDGET.DASHBOARD:
                return this._formatDataForDashBoard(data);
            case BICst.WIDGET.FORCE_BUBBLE:
                return this._formatDataForForceBubble(data);
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.MAP:
                var da = this._formatDataForMap(data);
                return BI.isEmptyArray(da) ? da : [da];
            case BICst.WIDGET.GIS_MAP:
                return this._formatDataForGISMap(data)
        }
    },

    getWidgetData: function(type, callback){
        var self = this, o = this.options;
        var view = BI.Utils.getWidgetViewByID(o.wId);
        var options = {};
        BI.Utils.getWidgetDataByID(o.wId, function (jsonData) {
            var data = self.parseChartData(jsonData.data);
            var types = [];
            var targetIds = self._getShowTarget();
            var count = 0;
            BI.each(data, function (idx, da) {
                var t = [];
                BI.each(da, function (id, d) {
                    if (type === BICst.WIDGET.MULTI_AXIS_COMBINE_CHART || type === BICst.WIDGET.COMBINE_CHART) {
                        var chart = BI.Utils.getDimensionStyleOfChartByID(targetIds[count] || targetIds[0]) || {};
                        t.push(chart.type || BICst.WIDGET.AXIS);
                    } else {
                        t.push(type);
                    }
                    count++;
                });
                types.push(t);
            });
            if(BI.isEmptyArray(types)){
                types.push([type]);
            }
            BI.each(data, function(idx, item){
                var i = BI.UUID();
                var type = types[idx];
                BI.each(item, function(id, it){
                    type[id] === BICst.WIDGET.ACCUMULATE_AXIS && BI.extend(it, {stack: i});
                });
            });
            if(type === BICst.WIDGET.MAP || type === BICst.WIDGET.GIS_MAP){
                options.geo = {data: BICst.MAP_PATH[BI.Utils.getWidgetSubTypeByID(o.wId)] || BICst.MAP_PATH[BICst.MAP_TYPE.CHINA]}
            }
            if(type === BICst.WIDGET.GIS_MAP){
                options.geo = {
                    "tileLayer": "http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}",
                };
            }
            callback(types, data, options);
        }, {
            expander: {
                x: {
                    type: true,
                    value: [[]]
                },
                y: {
                    type: true,
                    value: [[]]
                }
            },
            page: -1
        });
    },

    getLinkageInfo: function(obj){
        var dId = [], clicked = [];
        switch (BI.Utils.getWidgetTypeByID(this.options.wId)) {
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.SCATTER:
                dId = obj.targetIds;
                clicked = [{
                    dId: this.cataDid,
                    value: [obj.seriesName]
                }];
                break;
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DONUT:
                dId = obj.targetIds;
                clicked = [{
                    dId: this.cataDid,
                    value: [obj.seriesName]
                }];
                if (BI.isNotNull(this.seriesDid)) {
                    clicked.push({
                        dId: this.seriesDid,
                        value: [obj.x]
                    })
                }
                break;
            default:
                dId = obj.targetIds;
                clicked = [{
                    dId: this.cataDid,
                    value: [obj.x]
                }];
                if (BI.isNotNull(this.seriesDid)) {
                    clicked.push({
                        dId: this.seriesDid,
                        value: [obj.seriesName]
                    })
                }
                break;
        }
        return {dId: dId, clicked: clicked};
    }

});