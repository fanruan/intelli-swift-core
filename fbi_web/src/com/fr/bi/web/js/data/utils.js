Data.Utils = {
    /**
     * 数据转化方法
     * @param data 原始数据
     * @param widget 组件信息
     * @param {Object} op 配置信息
     * @returns {{types: Array, data: [], options: {}}} 转化后的图表类型信息,对应的数据信息,配置信息
     */
    convertDataToChartData: function (data, widget, op) {
        var res = this.convertDataToWidgetData(data, widget, op);
        return this.convertWidgetDataToChartData(widget.type, res.data, res.options, res.types);
    },

    /**
     * 数据转化方法
     * @param data 原始数据
     * @param widget 组件信息
     * @returns {{types: Array, data: [], options: {}}} 转化后的图表类型信息,对应的数据信息,配置信息
     */
    convertDataToWidgetData: function (data, widget, op) {
        var options = {};
        var type = widget.type;
        var dimsInfo = refreshDimsInfo();
        var dimIds = dimsInfo.dimIds;
        var crossDimIds = dimsInfo.crossDimIds;
        var cataDid = BI.find(widget.view[BICst.REGION.DIMENSION1], function (idx, did) {
            return widget.dimensions[did].used;
        });
        var seriesDid = BI.find(widget.view[BICst.REGION.DIMENSION2], function (idx, did) {
            return widget.dimensions[did].used;
        });
        var drillcataDimId = getDrillDimensionId(getDrill()[cataDid]);
        var drillseriDimId = getDrillDimensionId(getDrill()[seriesDid]);
        var cataGroup = BI.isNull(widget.dimensions[cataDid]) ? null : widget.dimensions[cataDid].group;
        var seriesGroup = BI.isNull(widget.dimensions[seriesDid]) ? null : widget.dimensions[seriesDid].group;
        if (BI.isNotNull(drillcataDimId)) {
            cataGroup = widget.dimensions[drillcataDimId].group;
        }
        if (BI.isNotNull(drillseriDimId)) {
            seriesGroup = widget.dimensions[drillseriDimId].group;
        }
        var targetIds = getShowTarget();
        var data = parseChartData(data);
        var types = [];
        var count = 0;
        BI.each(data, function (idx, da) {
            var t = [];
            BI.each(da, function (id, d) {
                if (type === BICst.WIDGET.MULTI_AXIS_COMBINE_CHART || type === BICst.WIDGET.COMBINE_CHART) {
                    var chart = widget.dimensions[targetIds[count] || targetIds[0]].style_of_chart || {};
                    t.push(chart.type || BICst.WIDGET.AXIS);
                } else {
                    t.push(type);
                }
                count++;
            });
            types.push(t);
        });
        if (BI.isEmptyArray(types)) {
            types.push([type]);
        }
        BI.each(data, function (idx, item) {
            var i = BI.UUID();
            var type = types[idx];
            BI.each(item, function (id, it) {
                (type[id] === BICst.WIDGET.ACCUMULATE_AREA || type[id] === BICst.WIDGET.ACCUMULATE_AXIS) && BI.extend(it, {stack: i});
            });
        });
        if (type === BICst.WIDGET.MAP) {
            var subType = widget.sub_type || BICst.MAP_TYPE.CHINA;
            options.initDrillPath = [BICst.MAP_TYPE_NAME[subType]];
            var drill = BI.values(getDrill())[0];
            BI.each(drill, function (idx, dri) {
                options.initDrillPath.push(dri.values[0].value[0]);
            });
            options.geo = {
                data: BICst.MAP_PATH[subType],
                name: BICst.MAP_TYPE_NAME[subType] || BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHINA]
            }
        }
        if (type === BICst.WIDGET.GIS_MAP) {
            options.geo = {
                "tileLayer": "http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}"
            };
        }
        var dimensionIds = BI.keys(widget.dimensions);
        var lnglat = null;
        if (dimensionIds.length !== 0) {
            lnglat = widget.dimensions[dimensionIds[0]].position;
        }
        var click = function (obj) {
            op.click(obj);
        };
        var ws = widget.settings || {};
        return {
            types: types,
            data: data,
            options: BI.extend(ws, {
                click: click
            }, options, {
                cordon: getCordon(),
                tooltip: getToolTip(),
                lnglat: BI.isNotNull(lnglat) ? lnglat.type : lnglat
            })
        };

        function parseChartData(data) {
            switch (type) {
                case BICst.WIDGET.ACCUMULATE_AXIS:
                case BICst.WIDGET.ACCUMULATE_AREA:
                case BICst.WIDGET.ACCUMULATE_RADAR:
                case BICst.WIDGET.AXIS:
                case BICst.WIDGET.LINE:
                case BICst.WIDGET.AREA:
                case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                case BICst.WIDGET.COMPARE_AXIS:
                case BICst.WIDGET.COMPARE_AREA:
                case BICst.WIDGET.FALL_AXIS:
                case BICst.WIDGET.RANGE_AREA:
                case BICst.WIDGET.BAR:
                case BICst.WIDGET.ACCUMULATE_BAR:
                case BICst.WIDGET.COMPARE_BAR:
                case BICst.WIDGET.COMBINE_CHART:
                case BICst.WIDGET.DONUT:
                case BICst.WIDGET.RADAR:
                case BICst.WIDGET.PIE:
                case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                case BICst.WIDGET.FORCE_BUBBLE:
                    return formatDataForAxis(data);
                case BICst.WIDGET.DASHBOARD:
                    return formatDataForDashBoard(data);
                case BICst.WIDGET.BUBBLE:
                    return formatDataForBubble(data);
                case BICst.WIDGET.SCATTER:
                    return formatDataForScatter(data);
                case BICst.WIDGET.MAP:
                    var da = formatDataForMap(data, 0);
                    return BI.isEmptyArray(da) ? da : [da];
                case BICst.WIDGET.GIS_MAP:
                    var da = formatDataForGISMap(data);
                    return BI.isEmptyArray(da) ? da : [da];
            }
        }

        function getDrill() {
            var clicked = widget.clicked;
            var drills = {};
            var dIds = BI.keys(widget.dimensions);
            BI.each(clicked, function (dId, value) {
                if (dIds.contains(dId) && isDimensionByDimensionID(dId)) {
                    drills[dId] = value;
                }
            });
            return drills;
        }

        function isDimensionByDimensionID(dId) {
            var region = 0;
            BI.some(widget.view, function (reg, arr) {
                if (arr.contains(dId)) {
                    region = reg;
                    return true;
                }
            });
            return BI.parseInt(region) >= BI.parseInt(BICst.REGION.DIMENSION1) &&
                BI.parseInt(BICst.REGION.TARGET1) > BI.parseInt(region);
        }

        function getShowTarget() {
            var view = widget.view;
            var targetIds = [];
            BI.each(view, function (regionType, arr) {
                if (regionType >= BICst.REGION.TARGET1) {
                    targetIds = BI.concat(targetIds, arr);
                }
            });
            return BI.filter(targetIds, function (idx, tId) {
                return widget.dimensions[tId].used;
            });
        }

        function formatDataForAxis(da) {
            var data = formatDataForCommon(da);
            if (BI.isEmptyArray(data)) {
                return [];
            }
            var view = widget.view;
            var array = [];
            BI.each(targetIds, function (idx, tId) {
                if (BI.has(view, BICst.REGION.TARGET1) && BI.contains(view[BICst.REGION.TARGET1], tId)) {
                    array.length === 0 && array.push([]);
                    if (checkSeriesExist()) {
                        array[0] = data;
                    } else {
                        array[0].push(data[idx])
                    }
                }
                if (BI.has(view, BICst.REGION.TARGET2) && BI.contains(view[BICst.REGION.TARGET2], tId)) {
                    while (array.length < 2) {
                        array.push([]);
                    }
                    if (checkSeriesExist()) {
                        array[1] = data;
                    } else {
                        array[1].push(data[idx])
                    }
                }
                if (BI.has(view, BICst.REGION.TARGET3) && BI.contains(view[BICst.REGION.TARGET3], tId)) {
                    while (array.length < 3) {
                        array.push([]);
                    }
                    if (checkSeriesExist()) {
                        array[2] = data;
                    } else {
                        array[2].push(data[idx])
                    }
                }
            });
            return array;
        }

        function checkSeriesExist() {
            var view = widget.view;
            var result = BI.find(view[BICst.REGION.DIMENSION2], function (idx, dId) {
                return widget.dimensions[dId].used;
            });
            return BI.isNotNull(result);
        }

        function getDrillDimensionId(drill) {
            if (BI.isEmptyArray(drill) || BI.isNull(drill)) {
                return null;
            }
            return drill[drill.length - 1].dId;
        }

        function refreshDimsInfo() {
            var dimIds = [];
            var crossDimIds = [];
            var view = widget.view;
            var drill = getDrill();

            BI.each(view[BICst.REGION.DIMENSION1], function (i, dId) {
                widget.dimensions[dId].used && (dimIds.push(dId));
            });
            BI.each(view[BICst.REGION.DIMENSION2], function (i, dId) {
                widget.dimensions[dId].used && (crossDimIds.push(dId));
            });
            BI.each(drill, function (drId, drArray) {
                if (drArray.length !== 0) {
                    var dIndex = dimIds.indexOf(drId), cIndex = crossDimIds.indexOf(drId);
                    BI.remove(dimIds, drId);
                    BI.remove(crossDimIds, drId);
                    BI.each(drArray, function (i, dr) {
                        var tempDrId = dr.dId;
                        if (i === drArray.length - 1) {
                            if (getRegionTypeByDimensionID(drId) === BICst.REGION.DIMENSION1) {
                                dimIds.splice(dIndex, 0, tempDrId);
                            } else {
                                crossDimIds.splice(cIndex, 0, tempDrId);
                            }
                        } else {
                            BI.remove(dimIds, tempDrId);
                            BI.remove(crossDimIds, tempDrId);
                        }
                    });
                }
            });
            return {
                dimIds: dimIds,
                crossDimIds: crossDimIds
            };
        }

        function getRegionTypeByDimensionID(dId) {
            var view = widget.view;
            return BI.findKey(view, function (regionType, dIds) {
                if (BI.contains(dIds, dId)) {
                    return true
                }
            });
        }

        function formatDataForCommon(data) {
            if (BI.has(data, "t")) {
                var top = data.t, left = data.l;
                return BI.map(top.c, function (id, tObj) {
                    var name = tObj.n, seriesName = tObj.n;
                    if (BI.isNotNull(seriesGroup) && seriesGroup.type === BICst.GROUP.YMD) {
                        var date = new Date(BI.parseInt(name));
                        name = date.print("%Y-%X-%d");
                    }
                    var data = BI.map(left.c, function (idx, obj) {
                        var value = obj.n, x = obj.n;
                        if (BI.isNotNull(cataGroup) && cataGroup.type === BICst.GROUP.YMD) {
                            var date = new Date(BI.parseInt(x));
                            x = date.print("%Y-%X-%d");
                        }
                        return {
                            "x": x,
                            "y": (BI.isFinite(obj.s.c[id].s[0]) ? obj.s.c[id].s[0] : 0),
                            "value": value,
                            seriesName: seriesName,
                            targetIds: [targetIds[0]]
                        };
                    });
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
                        var value = item.n, x = item.n;
                        if (BI.isNotNull(cataGroup) && cataGroup.type === BICst.GROUP.YMD) {
                            var date = new Date(BI.parseInt(x));
                            x = date.print("%Y-%X-%d");
                        }
                        return {
                            x: x,
                            y: (BI.isFinite(item.s[idx]) ? item.s[idx] : 0),
                            value: value,
                            seriesName: widget.dimensions[targetIds[idx]].name,
                            targetIds: [targetIds[idx]]
                        };
                    });
                    var obj = {};
                    obj.data = adjustData;
                    obj.name = widget.dimensions[targetIds[idx]].name;
                    return obj;
                });
            }
            if (BI.has(data, "s")) {
                var type = widget.type;
                if (type === BICst.WIDGET.PIE) {
                    var adjustData = BI.map(data.s, function (idx, value) {
                        return {
                            x: widget.dimensions[targetIds[idx]].name,
                            y: (BI.isFinite(value) ? value : 0),
                            targetIds: [targetIds[idx]]
                        };
                    });
                    var obj = {};
                    obj.data = adjustData;
                    return [obj];
                } else {
                    return BI.map(data.s, function (idx, value) {
                        return {
                            name: widget.dimensions[targetIds[idx]].name,
                            data: [{
                                x: "",
                                y: (BI.isFinite(value) ? value : 0),
                                targetIds: [targetIds[idx]]
                            }]
                        };
                    });
                }
            }
            return [];
        }

        function formatDataForDashBoard(data) {
            if (BI.has(data, "c")) {
                var adjustData = BI.map(data.c, function (id, item) {
                    var seriesName = item.n;
                    if (BI.isNotNull(cataGroup) && cataGroup.type === BICst.GROUP.YMD) {
                        var date = new Date(BI.parseInt(seriesName));
                        seriesName = date.print("%Y-%X-%d");
                    }
                    var data = [{
                        x: widget.dimensions[targetIds[0]].name,
                        y: (BI.isFinite(item.s[0]) ? item.s[0] : 0),
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
                        x: widget.dimensions[targetIds[idx]].name,
                        y: (BI.isFinite(value) ? value : 0),
                        targetIds: [targetIds[idx]]
                    };
                });
                return BI.isEmptyArray(obj.data) ? [] : [[obj]];
            }
            return [];
        }

        function formatDataForBubble(data) {
            var view = widget.view;
            var result = BI.find(view, function (region, arr) {
                return BI.isEmptyArray(arr);
            });
            if (BI.isNotNull(result) || BI.size(view) < 4) {
                return [];
            }
            return [BI.map(data.c, function (idx, item) {
                var obj = {};
                var name = item.n, seriesName = item.n;
                var dGroup = widget.dimensions[cataDid].group;
                if (BI.isNotNull(dGroup) && dGroup.type === BICst.GROUP.YMD) {
                    var date = new Date(BI.parseInt(name));
                    name = date.print("%Y-%X-%d");
                }
                obj.data = [{
                    x: (BI.isFinite(item.s[1]) ? item.s[1] : 0),
                    y: (BI.isFinite(item.s[0]) ? item.s[0] : 0),
                    z: (BI.isFinite(item.s[2]) ? item.s[2] : 0),
                    seriesName: seriesName,
                    targetIds: [targetIds[0], targetIds[1], targetIds[2]]
                }];
                obj.name = name;
                return obj;
            })];
        }

        function formatDataForScatter(data) {
            var view = widget.view;
            var result = BI.find(view, function (region, arr) {
                return BI.isEmptyArray(arr);
            });
            if (BI.isNotNull(result) || BI.size(view) < 3) {
                return [];
            }
            return [BI.map(data.c, function (idx, item) {
                var obj = {};
                var name = item.n, seriesName = item.n;
                var dGroup = widget.dimensions[cataDid].group;
                if (BI.isNotNull(dGroup) && dGroup.type === BICst.GROUP.YMD) {
                    var date = new Date(BI.parseInt(name));
                    name = date.print("%Y-%X-%d");
                }
                obj.name = name;
                obj.data = [{
                    x: (BI.isFinite(item.s[1]) ? item.s[1] : 0),
                    y: (BI.isFinite(item.s[0]) ? item.s[0] : 0),
                    seriesName: seriesName,
                    targetIds: [targetIds[0], targetIds[1]]
                }];
                return obj;
            })];
        }

        function formatDataForMap(data, currentLayer) {
            var result = [];
            currentLayer++;
            if (BI.has(data, "c")) {
                var obj = (data.c)[0];
                var view = widget.view;
                var columnSizeArray = BI.makeArray(BI.isNull(obj) ? 0 : BI.size(obj.s), 0);
                result = BI.map(columnSizeArray, function (idx, value) {
                    var type = null;
                    if (BI.has(view, BICst.REGION.TARGET2) && BI.contains(view[BICst.REGION.TARGET2], targetIds[idx])) {
                        type = BICst.WIDGET.BUBBLE;
                    }
                    var adjustData = BI.map(data.c, function (id, item) {
                        var res = {};
                        if (BI.has(view, BICst.REGION.TARGET2) && BI.contains(view[BICst.REGION.TARGET2], targetIds[idx])) {
                            switch (type) {
                                case BICst.WIDGET.BUBBLE:
                                case BICst.WIDGET.AXIS:
                                case BICst.WIDGET.PIE:
                                default:
                                    res = {
                                        x: item.n,
                                        y: (BI.isFinite(item.s[idx]) ? item.s[idx] : 0),
                                        targetIds: [targetIds[idx]],
                                        dId: dimIds[currentLayer - 1],
                                        drillDid: dimIds[currentLayer]
                                    };
                            }
                        } else {
                            res = {
                                x: item.n,
                                y: (BI.isFinite(item.s[idx]) ? item.s[idx] : 0),
                                targetIds: [targetIds[idx]],
                                dId: dimIds[currentLayer - 1],
                                drillDid: dimIds[currentLayer]
                            };
                        }
                        if (BI.has(item, "c")) {
                            res.drilldown = {};
                            res.drilldown.series = formatDataForMap(item, currentLayer);
                            res.drilldown.geo = {
                                data: BICst.MAP_PATH[BICst.MAP_NAME[res.x]],
                                name: res.x
                            };
                        }
                        return res;
                    });
                    var obj = {};
                    obj.data = adjustData;
                    BI.isNotNull(type) && (obj.type = "bubble");
                    obj.name = widget.dimensions[targetIds[idx]].name;
                    return obj;
                });
            }
            return result;
        }

        function formatDataForGISMap(data) {
            if (BI.has(data, "t")) {
                var top = data.t, left = data.l;
                var init = BI.map(top.c, function (id, tObj) {
                    var data = [];
                    BI.each(left.c, function (idx, obj) {
                        var x = obj.n;
                        BI.each(obj.s.c[id].s, function (i, o) {
                            if (BI.isNotNull(o) && BI.isNotNull(x)) {
                                data.push({
                                    "x": x,
                                    "z": tObj.n,
                                    "y": o,
                                    targetIds: [targetIds[i]]
                                });
                            }
                        });
                    });
                    var name = tObj.n;
                    var obj = {};
                    obj.data = data;
                    obj.name = name;
                    return obj;
                });
                var result = [];
                var size = 0;
                if (init.length > 0) {
                    size = targetIds.length;
                }
                BI.each(BI.makeArray(size, null), function (idx, index) {
                    var res = {data: [], name: widget.dimensions[targetIds[idx]].name};
                    BI.each(init, function (id, obj) {
                        res.data.push(obj.data[idx]);
                    });
                    result.push(res);
                });
                return result;
            }
            if (BI.has(data, "c")) {
                var obj = (data.c)[0];
                var columnSizeArray = BI.makeArray(BI.isNull(obj) ? 0 : BI.size(obj.s), 0);
                return BI.map(columnSizeArray, function (idx, value) {
                    var adjustData = BI.map(data.c, function (id, item) {
                        var x = item.n;
                        return {
                            x: x,
                            y: item.s[idx],
                            targetIds: [targetIds[idx]]
                        };
                    });
                    var obj = {};
                    obj.data = adjustData;
                    obj.name = widget.dimensions[targetIds[idx]].name;
                    return obj;
                });
            }
            return [];
        }

        function getLinkageInfo(obj) {
            var DimsInfo = refreshDimsInfo();
            var crossDimIds = DimsInfo.crossDimIds;
            var dimIds = DimsInfo.dimIds;
            var dId = [], clicked = [];
            switch (widget.type) {
                case BICst.WIDGET.BUBBLE:
                case BICst.WIDGET.SCATTER:
                    dId = obj.targetIds;
                    clicked = [{
                        dId: obj.dId || this.dimIds[0],
                        value: [obj.seriesName]
                    }];
                    break;
                case BICst.WIDGET.MAP:
                case BICst.WIDGET.GIS_MAP:
                    dId = obj.targetIds;
                    clicked = [{
                        dId: obj.dId || dimIds[0],
                        value: [obj.x]
                    }];
                    break;
                default:
                    dId = obj.targetIds;
                    clicked = [{
                        dId: obj.dId || dimIds[0],
                        value: [obj.value || obj.x]
                    }];
                    if (BI.isNotNull(seriesDid)) {
                        clicked.push({
                            dId: obj.dId || crossDimIds[0],
                            value: [obj.seriesName]
                        })
                    }
                    break;
            }
            return {dId: dId, clicked: clicked};
        }

        function getToolTip() {
            switch (widget.type) {
                case BICst.WIDGET.SCATTER:
                    if (targetIds.length < 2) {
                        return "";
                    } else {
                        return "function(){ return this.seriesName+'<div>(X)" + widget.dimensions[targetIds[1]].name + ":'+ this.x +'</div><div>(Y)"
                            + widget.dimensions[targetIds[0]].name + ":'+ this.y +'</div>'}";
                    }
                case BICst.WIDGET.BUBBLE:
                    if (targetIds.length < 3) {
                        return "";
                    } else {
                        return "function(){ return this.seriesName+'<div>(X)" + widget.dimensions[targetIds[1]].name + ":'+ this.x +'</div><div>(Y)"
                            + widget.dimensions[targetIds[0]].name + ":'+ this.y +'</div><div>(" + BI.i18nText("BI-Size") + ")" + widget.dimensions[targetIds[2]].name
                            + ":'+ this.size +'</div>'}";
                    }
                default:
                    return "";
            }
        }

        function getCordon() {
            var cordon = {};
            var result = [];
            BI.each(BI.keys(widget.dimensions), function (idx, dId) {
                if (widget.dimensions[dId].used === false) {
                    return;
                }
                var items = BI.map(widget.dimensions[dId].cordon, function (id, cor) {
                    return {
                        text: cor.cordon_name,
                        value: cor.cordon_value,
                        color: cor.cordon_color
                    }
                });
                var regionType = getRegionTypeByDimensionID(dId);
                if (BI.isNotEmptyArray(items)) {
                    BI.has(cordon, regionType) === false && (cordon[regionType] = []);
                    cordon[regionType] = BI.concat(cordon[regionType], items);
                }
            });
            var type = widget.type;
            if (type === BICst.WIDGET.SCATTER || type === BICst.WIDGET.BUBBLE) {
                result.push(BI.isNull(cordon[BICst.REGION.TARGET2]) ? [] : cordon[BICst.REGION.TARGET2]);
                result.push(BI.isNull(cordon[BICst.REGION.TARGET1]) ? [] : cordon[BICst.REGION.TARGET1]);
                return result;
            }
            if (type === BICst.WIDGET.BAR || type === BICst.WIDGET.ACCUMULATE_BAR) {
                result.push(BI.isNull(cordon[BICst.REGION.TARGET1]) ? [] : cordon[BICst.REGION.TARGET1]);
                result.push(BI.isNull(cordon[BICst.REGION.DIMENSION1]) ? [] : cordon[BICst.REGION.DIMENSION1]);
                return result;
            }
            if (type === BICst.WIDGET.COMPARE_BAR) {
                var negativeAxis = BI.isNull(cordon[BICst.REGION.TARGET1]) ? [] : cordon[BICst.REGION.TARGET1];
                var positiveAxis = BI.isNull(cordon[BICst.REGION.TARGET2]) ? [] : cordon[BICst.REGION.TARGET2];
                result.push(BI.concat(negativeAxis, positiveAxis));
                result.push(BI.isNull(cordon[BICst.REGION.DIMENSION1]) ? [] : cordon[BICst.REGION.DIMENSION1]);
                return result;
            }
            result.push(BI.isNull(cordon[BICst.REGION.DIMENSION1]) ? [] : cordon[BICst.REGION.DIMENSION1]);
            result.push(BI.isNull(cordon[BICst.REGION.TARGET1]) ? [] : cordon[BICst.REGION.TARGET1]);
            result.push(BI.isNull(cordon[BICst.REGION.TARGET2]) ? [] : cordon[BICst.REGION.TARGET2]);
            result.push(BI.isNull(cordon[BICst.REGION.TARGET3]) ? [] : cordon[BICst.REGION.TARGET3]);
            return result;
        }
    },

    convertWidgetDataToChartData: function (type, data, options, types) {
        options || (options = {});
        var constants = ChartConstants();
        var config = {
            left_y_axis_title: options.left_y_axis_title || "",
            right_y_axis_title: options.right_y_axis_title || "",
            chart_color: options.chart_color || [],
            chart_style: options.chart_style || constants.NORMAL,
            chart_line_type: options.chart_line_type || constants.NORMAL,
            left_y_axis_style: options.left_y_axis_style || constants.NORMAL,
            right_y_axis_style: options.right_y_axis_style || constants.NORMAL,
            show_x_axis_title: options.show_x_axis_title || false,
            show_left_y_axis_title: options.show_left_y_axis_title || false,
            show_right_y_axis_title: options.show_right_y_axis_title || false,
            left_y_axis_reversed: options.left_y_axis_reversed || false,
            right_y_axis_reversed: options.right_y_axis_reversed || false,
            left_y_axis_number_level: options.left_y_axis_number_level || constants.NORMAL,
            right_y_axis_number_level: options.right_y_axis_number_level || constants.NORMAL,
            x_axis_unit: options.x_axis_unit || "",
            left_y_axis_unit: options.left_y_axis_unit || "",
            right_y_axis_unit: options.right_y_axis_unit || "",
            x_axis_title: options.x_axis_title || "",
            chart_legend: options.chart_legend || constants.LEGEND_BOTTOM,
            show_data_label: options.show_data_label || false,
            show_data_table: options.show_data_table || false,
            show_grid_line: BI.isNull(options.show_grid_line) ? true : options.show_grid_line,
            show_zoom: options.show_zoom || false,
            text_direction: options.text_direction || 0,
            chart_radar_type: options.chart_radar_type || constants.NORMAL,
            cordon: options.cordon || [],
            right_y_axis_second_title: options.right_y_axis_second_title || "",
            right_y_axis_second_style: options.right_y_axis_second_style || constants.NORMAL,
            show_right_y_axis_second_title: options.show_right_y_axis_second_title || false,
            right_y_axis_second_reversed: options.right_y_axis_second_reversed || false,
            right_y_axis_second_number_level: options.right_y_axis_second_number_level || constants.NORMAL,
            right_y_axis_second_unit: options.right_y_axis_second_unit || "",
            chart_pie_type: options.chart_pie_type || constants.NORMAL,
            chart_inner_radius: options.chart_inner_radius || 0,
            chart_total_angle: options.chart_total_angle || BICst.PIE_ANGLES.TOTAL,
            dashboard_number_level: options.dashboard_number_level || constants.NORMAL,
            dashboard_unit: options.dashboard_unit || "",
            chart_dashboard_type: options.chart_dashboard_type || constants.NORMAL,
            number_of_pointer: options.number_of_pointer || constants.ONE_POINTER,
            bands_styles: options.style_conditions || [],
            auto_custom_style: options.auto_custom || constants.AUTO,
            x_axis_style: options.x_axis_style || constants.NORMAL,
            x_axis_number_level: options.x_axis_number_level || constants.NORMAL,
            tooltip: options.tooltip || "",
            geo: options.geo || {data: BICst.MAP_PATH[BICst.MAP_TYPE.CHINA], name: BI.i18nText("BI-China")},
            theme_color: options.theme_color || "#65bce7",
            map_styles: options.map_styles,
            auto_custom: BI.isNull(options.map_styles) ? false : options.auto_custom,
            initDrillPath: options.initDrillPath || [],
            lnglat: options.lnglat || constants.LNG_FIRST,
            click: options.click,
            map_bubble_color: options.map_bubble_color || "#65bce7",
            max_scale: options.max_scale || "",
            min_scale: options.min_scale || "",
            show_percentage: options.show_percentage || constants.SHOW
        };

        var maxes = [];
        var max = null;
        var min = null;
        switch (type) {
            case BICst.WIDGET.ACCUMULATE_AXIS:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.AXIS);
                    });
                    t.push(type);
                });
                var items = BI.map(data, function (idx, item) {
                    var i = BI.UUID();
                    return BI.map(item, function (id, it) {
                        return BI.extend({}, it, {stack: i});
                    });
                });
                var opts = formatItems(items, t);
                return formatConfigForAccumulateAxisArea(opts[1], opts[0], BICst.WIDGET.AXIS);
            case BICst.WIDGET.ACCUMULATE_AREA:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.AREA);
                    });
                    t.push(type);
                });
                var items = BI.map(data, function (idx, item) {
                    var i = BI.UUID();
                    return BI.map(item, function (id, it) {
                        return BI.extend({}, it, {stack: i});
                    });
                });
                var opts = formatItems(items, t);
                return formatConfigForAccumulateAxisArea(opts[1], opts[0], BICst.WIDGET.AREA);
            case BICst.WIDGET.ACCUMULATE_RADAR:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.RADAR);
                    });
                    t.push(type);
                });
                var items = BI.map(data, function (idx, item) {
                    var i = BI.UUID();
                    return BI.map(item, function (id, it) {
                        return BI.extend({}, it, {stack: i});
                    });
                });
                var opts = formatItems(items, t);
                return formatConfigForAccumulateRadar(opts[1], opts[0]);
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.COMBINE_CHART:
                var opts = formatItems(data, types);
                return formatConfigForAxis(opts[1], opts[0]);
            case BICst.WIDGET.LINE:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.LINE);
                    });
                    t.push(type);
                });
                var opts = formatItems(data, t);
                return formatConfigForLine(opts[1], opts[0]);
            case BICst.WIDGET.AREA:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.AREA);
                    });
                    t.push(type);
                });
                var opts = formatItems(data, t);
                return formatConfigForArea(opts[1], opts[0]);
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.AXIS);
                    });
                    t.push(type);
                });
                var items = BI.map(data, function (idx, item) {
                    var i = BI.UUID();
                    return BI.map(item, function (id, it) {
                        return BI.extend({}, it, {stack: i, stackByPercent: true});
                    });
                });
                var opts = formatItems(items, t);
                return formatConfigForPercent(opts[1], opts[0], BICst.WIDGET.AXIS);
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.AREA);
                    });
                    t.push(type);
                });
                var items = BI.map(data, function (idx, item) {
                    var i = BI.UUID();
                    return BI.map(item, function (id, it) {
                        return BI.extend({}, it, {stack: i, stackByPercent: true});
                    });
                });
                var opts = formatItems(items, t);
                return formatConfigForPercent(opts[1], opts[0], BICst.WIDGET.AREA);
            case BICst.WIDGET.COMPARE_AXIS:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.AXIS);
                    });
                    t.push(type);
                });
                BI.each(data, function (idx, item) {
                    BI.each(item, function (id, it) {
                        if (idx > 0) {
                            BI.extend(it, {reversed: true, xAxis: 1});
                        } else {
                            BI.extend(it, {reversed: false, xAxis: 0});
                        }
                    });
                });
                var opts = formatItems(data, t);
                return formatConfigForCompare(opts[1], opts[0], BICst.WIDGET.AXIS);
            case BICst.WIDGET.COMPARE_AREA:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.AREA);
                    });
                    t.push(type);
                });
                BI.each(data, function (idx, item) {
                    BI.each(item, function (id, it) {
                        if (idx > 0) {
                            BI.extend(it, {reversed: true, xAxis: 1});
                        } else {
                            BI.extend(it, {reversed: false, xAxis: 0});
                        }
                    });
                });
                var opts = formatItems(data, t);
                return formatConfigForCompare(opts[1], opts[0], BICst.WIDGET.AREA);
            case BICst.WIDGET.FALL_AXIS:
                var items = [];
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.AXIS);
                    });
                    t.push(type);
                });
                if (BI.isEmptyArray(data)) {
                } else {
                    items = data[0];
                    var tables = [], sum = 0;
                    var colors = config.chart_color || [];
                    if (BI.isEmptyArray(colors)) {
                        colors = ["rgb(152, 118, 170)", "rgb(0, 157, 227)"];
                    }
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (i, t) {
                            if (t.y < 0) {
                                tables.push([t.x, t.y, sum + t.y, t.targetIds]);
                            } else {
                                tables.push([t.x, t.y, sum, t.targetIds]);
                            }
                            sum += t.y;
                        });
                    });

                    items = [BI.map(BI.makeArray(2, null), function (idx, item) {
                        return {
                            "data": BI.map(tables, function (id, cell) {
                                var axis = BI.extend({targetIds: cell[3]}, {
                                    x: cell[0],
                                    y: Math.abs(cell[2 - idx])
                                });
                                if (idx === 1) {
                                    axis.color = cell[2 - idx] < 0 ? colors[1] : colors[0];
                                } else {
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
                }
                var opts = formatItems(items, t);
                return formatConfigForFall(opts[1], opts[0]);
            case BICst.WIDGET.RANGE_AREA:
                var items = [];
                var t = [];
                var type = [];
                BI.each(data, function (idx, axisItems) {
                    type.push(BICst.WIDGET.AREA);
                });
                if (BI.isNotEmptyArray(type)) {
                    t.push(type);
                }
                BI.each(data, function (idx, item) {
                    items = BI.concat(items, item);
                });
                if (BI.isEmptyArray(items)) {
                    return [];
                }
                if (items.length === 1) {
                } else {
                    var colors = config.chart_color || [];
                    if (BI.isEmptyArray(colors)) {
                        colors = ["#5caae4"];
                    }
                    var seriesMinus = [];
                    BI.each(items[0].data, function (idx, item) {
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
                    BI.each(items, function (idx, item) {
                        if (idx === 0) {
                            BI.extend(item, {
                                name: items[0].name,
                                fillColorOpacity: 0,
                                stack: "stackedArea",
                                marker: {enabled: false},
                                fillColor: "#000000"
                            });
                        }
                    });
                }
                var opts = formatItems([items], t);
                return formatConfigForRange(opts[1], opts[0]);
            case BICst.WIDGET.BAR:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.BAR);
                    });
                    t.push(type);
                });
                BI.each(data, function (idx, item) {
                    BI.each(item, function (id, it) {
                        BI.each(it.data, function (i, t) {
                            var tmp = t.x;
                            t.x = t.y;
                            t.y = tmp;
                        })
                    });
                });
                var opts = formatItems(data, t);
                return formatConfigForBar(opts[1], opts[0]);
            case BICst.WIDGET.ACCUMULATE_BAR:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.BAR);
                    });
                    t.push(type);
                });
                BI.each(data, function (idx, item) {
                    var stackId = BI.UUID();
                    BI.each(item, function (id, it) {
                        it.stack = stackId;
                        BI.each(it.data, function (i, t) {
                            var tmp = t.x;
                            t.x = t.y;
                            t.y = tmp;
                        })
                    });
                });
                var opts = formatItems(data, t);
                return formatConfigForAccumulateBar(opts[1], opts[0]);
            case BICst.WIDGET.COMPARE_BAR:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.BAR);
                    });
                    t.push(type);
                });
                var result = [];
                var i = BI.UUID();
                BI.each(data, function (idx, item) {
                    BI.each(item, function (id, it) {
                        BI.each(it.data, function (i, t) {
                            var tmp = t.x;
                            t.x = t.y;
                            t.y = tmp;
                            if (idx === 0) {
                                t.x = -t.x;
                            }
                        });
                        it.stack = i;
                    })
                });
                BI.each(data, function (idx, item) {
                    result = BI.concat(result, item);
                });
                var opts = formatItems([result], t);
                return formatConfigForCompareBar(opts[1], opts[0]);
            case BICst.WIDGET.DONUT:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.DONUT);
                    });
                    t.push(type);
                });
                var opts = formatItems(data, t);
                return formatConfigForDonut(opts[1], opts[0]);
            case BICst.WIDGET.RADAR:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.RADAR);
                    });
                    t.push(type);
                });
                var opts = formatItems(data, t);
                return formatConfigForRadar(opts[1], opts[0]);
            case BICst.WIDGET.PIE:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.PIE);
                    });
                    t.push(type);
                });
                var opts = formatItems(data, t);
                return formatConfigForPie(opts[1], opts[0]);
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                var opts = formatItems(data, types);
                return formatConfigForMultiAxis(opts[1], opts[0]);
            case BICst.WIDGET.FORCE_BUBBLE:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.FORCE_BUBBLE);
                    });
                    t.push(type);
                });
                var opts = formatItems(data, t);
                return formatConfigForForceBubble(opts[1], opts[0]);
            case BICst.WIDGET.DASHBOARD:
                var items = data;
                if (data.length === 0) {
                } else {
                    if (config.chart_dashboard_type === constants.NORMAL || config.chart_dashboard_type === constants.HALF_DASHBOARD) {
                        var result = [];
                        if (config.number_of_pointer === constants.ONE_POINTER && items[0].length === 1) {//单个系列
                            BI.each(items[0][0].data, function (idx, da) {
                                result.push({
                                    data: [{
                                        x: items[0][0].name,
                                        y: da.y
                                    }],
                                    name: da.x
                                })
                            });
                            items = [result];
                        }
                        if (config.number_of_pointer === constants.MULTI_POINTER && items[0].length > 1) {//多个系列
                            BI.each(items, function (idx, item) {
                                BI.each(item, function (id, it) {
                                    var data = it.data[0];
                                    data.x = it.name;
                                    result.push(data);
                                })
                            });
                            items = [[{
                                data: result,
                                name: ""
                            }]];
                        }
                    } else {
                        var others = [];
                        BI.each(items[0], function (idx, item) {
                            BI.each(item.data, function (id, da) {
                                others.push({
                                    data: [{
                                        x: item.name,
                                        y: da.y
                                    }],
                                    name: da.x
                                })
                            })
                        });
                        items = [others];
                    }
                }
                var t = [];
                BI.each(items, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.DASHBOARD);
                    });
                    t.push(type);
                });
                var opts = formatItems(items, t);
                return formatConfigForDashboard(opts[1], opts[0]);
            case BICst.WIDGET.BUBBLE:
                BI.each(data, function (idx, item) {
                    BI.each(item, function (id, it) {
                        BI.each(it.data, function (i, da) {
                            da.size = da.z;
                        })
                    })
                });
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.BUBBLE);
                    });
                    t.push(type);
                });
                var opts = formatItems(data, t);
                return formatConfigForBubble(opts[1], opts[0]);
            case BICst.WIDGET.SCATTER:
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.SCATTER);
                    });
                    t.push(type);
                });
                var opts = formatItems(data, t);
                return formatConfigForScatter(opts[1], opts[0]);
            case BICst.WIDGET.MAP:
                BI.each(data, function (idx, item) {
                    BI.each(item, function (id, it) {
                        BI.each(it.data, function (i, da) {
                            if ((BI.isNull(max) || da.y > max) && id === 0) {
                                max = da.y;
                            }
                            if ((BI.isNull(min) || da.y < min) && id === 0) {
                                min = da.y;
                            }
                            if (BI.has(it, "type") && it.type == "bubble") {
                                da.name = da.x;
                                da.size = da.y;
                            } else {
                                da.name = da.x;
                                da.value = da.y;
                            }
                            if (BI.has(da, "drilldown")) {
                                _formatDrillItems(da.drilldown);
                            }
                        })
                    })
                });
                var t = [];
                BI.each(data, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function (id, item) {
                        type.push(BICst.WIDGET.MAP);
                    });
                    t.push(type);
                });
                var opts = formatItems(data, t);
                return formatConfigForMap(opts[1], opts[0]);
            case BICst.WIDGET.GIS_MAP:
                var results = [];
                BI.each(data, function (idx, item) {
                    var result = [];
                    BI.each(item, function (id, it) {
                        var res = [];
                        BI.each(it.data, function (i, da) {
                            var lnglat = da.x.split(",");
                            if (config.lnglat === constants.LAT_FIRST) {
                                var lng = lnglat[1];
                                lnglat[1] = lnglat[0];
                                lnglat[0] = lng;
                            }
                            da.lnglat = lnglat;
                            da.value = da.y;
                            da.name = BI.isNotNull(da.z) ? da.z : da.lnglat;
                            if (_checkLngLatValid(da.lnglat)) {
                                res.push(da);
                            }
                        });
                        if (BI.isNotEmptyArray(res)) {
                            result.push(BI.extend(it, {
                                data: res
                            }));
                        }
                    });
                    if (BI.isNotEmptyArray(result)) {
                        results.push(result);
                    }
                });
                var t = [];
                BI.each(results, function (idx, axisItems) {
                    var type = [];
                    BI.each(axisItems, function () {
                        type.push(BICst.WIDGET.GIS_MAP);
                    });
                    t.push(type);
                });
                var opts = formatItems(results, t);
                return formatConfigForGIS(opts[1], opts[0]);
        }

        function _checkLngLatValid(lnglat) {
            if (lnglat.length < 2) {
                return false;
            }
            return lnglat[0] <= 180 && lnglat[0] >= -180 && lnglat[1] <= 90 && lnglat[1] >= -90;
        }

        function _formatDrillItems(items) {
            BI.each(items.series, function (idx, da) {
                BI.each(da.data, function (idx, data) {
                    if (BI.has(da, "type") && da.type == "bubble") {
                        data.name = data.x;
                        data.size = data.y;
                    } else {
                        data.name = data.x;
                        data.value = data.y;
                    }
                    if (BI.has(data, "drilldown")) {
                        _formatDrillItems(data.drilldown);
                    }
                })
            })
        }

        function formatConfigForMap(configs, items) {
            formatRangeLegend();
            delete configs.legend;
            configs.plotOptions.dataLabels.enabled = config.show_data_label;
            configs.plotOptions.tooltip.shared = true;
            configs.plotOptions.bubble.color = config.map_bubble_color;
            //config.plotOptions.color = BI.isArray(config.theme_color) ? config.theme_color : [config.theme_color];
            var formatterArray = [];
            BI.backEach(items, function (idx, item) {
                if (BI.has(item, "settings")) {
                    formatterArray.push(formatToolTipAndDataLabel(item.settings.format || c.NORMAL, item.settings.num_level || constants.NORMAL));
                }
            });
            configs.plotOptions.tooltip.formatter = function () {
                var tip = this.name;
                BI.each(this.points, function (idx, point) {
                    var value = point.size || point.y;
                    tip += ('<div>' + point.seriesName + ':' + (window.FR ? FR.contentFormat(value, formatterArray[idx]) : value) + '</div>');
                });
                return tip;
            };
            configs.plotOptions.dataLabels.formatter.valueFormat = function () {
                return window.FR ? FR.contentFormat(arguments[0], formatterArray[0]) : arguments[0];
            };

            configs.geo = config.geo;
            if (config.initDrillPath.length > 1) {
                configs.initDrillPath = config.initDrillPath;
            }
            configs.chartType = "areaMap";
            delete configs.xAxis;
            delete configs.yAxis;
            var find = BI.find(items, function (idx, item) {
                return BI.has(item, "type") && item.type === "areaMap";
            });
            if (BI.isNull(find)) {
                items.push({
                    type: "areaMap",
                    data: []
                })
            }

            return BI.extend(configs, {
                series: items
            });

            function formatRangeLegend() {
                switch (config.chart_legend) {
                    case BICst.CHART_LEGENDS.BOTTOM:
                        configs.rangeLegend.enabled = true;
                        configs.rangeLegend.visible = true;
                        configs.rangeLegend.position = "bottom";
                        break;
                    case BICst.CHART_LEGENDS.RIGHT:
                        configs.rangeLegend.enabled = true;
                        configs.rangeLegend.visible = true;
                        configs.rangeLegend.position = "right";
                        break;
                    case BICst.CHART_LEGENDS.NOT_SHOW:
                        configs.rangeLegend.enabled = true;
                        configs.rangeLegend.visible = false;
                        break;
                }
                configs.rangeLegend.continuous = false;

                configs.rangeLegend.range = getRangeStyle(config.map_styles, config.auto_custom, config.theme_color);
                configs.rangeLegend.formatter = function () {
                    return this.to;
                }
            }

            function formatToolTipAndDataLabel(format, numberLevel) {
                var formatter = '#.##';
                switch (format) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (numberLevel === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    if (format === constants.NORMAL) {
                        formatter = '#0%'
                    } else {
                        formatter += '%';
                    }
                }
                return formatter;
            }

            function getRangeStyle(styles, change, defaultColor) {
                var range = [], color = null, defaultStyle = {};
                var conditionMax = null, conditionMin = null, max = null, min = null;

                BI.each(items, function (idx, item) {
                    BI.each(item.data, function (id, it) {
                        if (BI.isNull(min) || min > it.y) {
                            min = it.y
                        }
                        if (BI.isNull(max) || max < it.y) {
                            max = it.y
                        }
                    })
                });

                switch (change) {
                    case BICst.SCALE_SETTING.AUTO:
                        defaultStyle.color = defaultColor;
                        return defaultStyle;
                    case BICst.SCALE_SETTING.CUSTOM:
                        if (styles.length !== 0) {
                            BI.each(styles, function (idx, style) {
                                range.push({
                                    color: style.color,
                                    from: style.range.min,
                                    to: style.range.max
                                });
                                color = style.color;
                                conditionMax = style.range.max
                            });

                            conditionMin = BI.parseInt(styles[0].range.min);
                            if (conditionMin !== 0) {
                                range.push({
                                    color: "#808080",
                                    from: 0,
                                    to: conditionMin
                                });
                            }

                            var maxScale = _calculateValueNiceDomain(0, max)[1];

                            if (conditionMax < maxScale) {
                                range.push({
                                    color: color,
                                    from: conditionMax,
                                    to: maxScale
                                });
                            }
                            return range;
                        } else {
                            defaultStyle.color = defaultColor;
                            return defaultStyle;
                        }
                }
            }

            function _calculateValueNiceDomain(minValue, maxValue) {
                minValue = Math.min(0, minValue);
                var tickInterval = _linearTickInterval(minValue, maxValue);

                return _linearNiceDomain(minValue, maxValue, tickInterval);
            }

            function _linearTickInterval(minValue, maxValue, m) {

                m = m || 5;
                var span = maxValue - minValue;
                var step = Math.pow(10, Math.floor(Math.log(span / m) / Math.LN10));
                var err = m / span * step;

                if (err <= .15) step *= 10; else if (err <= .35) step *= 5; else if (err <= .75) step *= 2;

                return step;
            }

            function _linearNiceDomain(minValue, maxValue, tickInterval) {
                minValue = VanUtils.accMul(Math.floor(minValue / tickInterval), tickInterval);
                maxValue = VanUtils.accMul(Math.ceil(maxValue / tickInterval), tickInterval);
                return [minValue, maxValue];
            }
        }

        function formatConfigForGIS(configs, items) {
            delete configs.dataSheet;
            delete configs.legend;
            delete configs.zoom;
            configs.plotOptions.dataLabels.enabled = config.show_data_label;
            configs.plotOptions.dataLabels.useHtml = true;
            configs.plotOptions.dataLabels.formatter = "function() { var a = '<div style = " + '"padding: 5px; background-color: rgba(0,0,0,0.4980392156862745);border-color: rgb(0,0,0); border-radius:2px; border-width:0px;">' + "' + this.name + ','" + "+ this.value +'</div>'; return a;}";
            configs.plotOptions.tooltip.shared = true;
            configs.plotOptions.tooltip.formatter = "function(){var tip = BI.isArray(this.name) ? '' : this.name; BI.each(this.points, function(idx, point){tip += ('<div>' + point.seriesName + ':' + (point.size || point.y) + '</div>');});return tip; }";
            configs.geo = {
                "tileLayer": "http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}"
            };
            configs.chartType = "pointMap";
            configs.plotOptions.icon = {
                iconUrl: BICst.GIS_ICON_PATH,
                iconSize: [24, 24]
            };

            configs.plotOptions.marker = {
                symbol: BICst.GIS_ICON_PATH,
                width: 24,
                height: 24,
                enable: true
            };
            delete configs.xAxis;
            delete configs.yAxis;
            return BI.extend(configs, {
                series: items
            });
        }

        function formatConfigForScatter(configs, items) {
            var xAxis = [{
                type: "value",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "bottom",
                gridLineWidth: 0
            }];
            var yAxis = [{
                type: "value",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "left",
                gridLineWidth: 0
            }];
            configs.yAxis = yAxis;
            configs.xAxis = xAxis;
            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            configs.plotOptions.tooltip.formatter = config.tooltip;
            formatCordon();
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }
            configs.plotOptions.dataLabels.enabled = config.show_data_label;
            configs.plotOptions.dataLabels.formatter.identifier = "${X}${Y}";

            configs.yAxis[0].formatter = formatTickInXYaxis(config.left_y_axis_style, constants.LEFT_AXIS);
            formatNumberLevelInYaxis(config.left_y_axis_number_level, constants.LEFT_AXIS);
            configs.yAxis[0].title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.LEFT_AXIS);
            configs.yAxis[0].title.text = config.show_left_y_axis_title === true ? config.left_y_axis_title + configs.yAxis[0].title.text : configs.yAxis[0].title.text;
            configs.yAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.yAxis[0].title.rotation = constants.ROTATION;

            configs.xAxis[0].formatter = formatTickInXYaxis(config.x_axis_style, constants.X_AXIS);
            formatNumberLevelInXaxis(config.x_axis_number_level, constants.X_AXIS);
            configs.xAxis[0].title.text = getXYAxisUnit(config.x_axis_number_level, constants.X_AXIS);
            configs.xAxis[0].title.text = config.show_x_axis_title === true ? config.x_axis_title + configs.xAxis[0].title.text : configs.xAxis[0].title.text;
            configs.xAxis[0].title.align = "center";
            configs.xAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.chartType = "scatter";

            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatCordon() {
                BI.each(config.cordon, function (idx, cor) {
                    if (idx === 0 && xAxis.length > 0) {
                        var magnify = calcMagnify(config.x_axis_number_level);
                        xAxis[0].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px"
                                    },
                                    "text": t.text,
                                    "align": "top"
                                }
                            });
                        });
                    }
                    if (idx > 0 && yAxis.length >= idx) {
                        var magnify = 1;
                        switch (idx - 1) {
                            case constants.LEFT_AXIS:
                                magnify = calcMagnify(config.left_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS:
                                magnify = calcMagnify(config.right_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS_SECOND:
                                magnify = calcMagnify(config.right_y_axis_second_number_level);
                                break;
                        }
                        yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px"
                                    },
                                    "text": t.text,
                                    "align": "left"
                                }
                            });
                        });
                    }
                })
            }

            function formatNumberLevelInXaxis(type) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (!BI.isNumber(da.x)) {
                                da.x = BI.parseFloat(da.x);
                            }
                            da.x = da.x || 0;
                            da.x = da.x.div(magnify);
                            da.x = da.x.toFixed(constants.FIX_COUNT);
                            if (constants.MINLIMIT.sub(da.x) > 0) {
                                da.x = 0;
                            }
                        })
                    })
                }
            }

            function formatNumberLevelInYaxis(type, position) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (position === item.yAxis) {
                                if (!BI.isNumber(da.y)) {
                                    da.y = BI.parseFloat(da.y);
                                }
                                da.y = da.y || 0;
                                da.y = da.y.div(magnify);
                                da.y = da.y.toFixed(constants.FIX_COUNT);
                                if (constants.MINLIMIT.sub(da.y) > 0) {
                                    da.y = 0;
                                }
                            }
                        })
                    })
                }
                if (type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    configs.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                if (position === constants.RIGHT_AXIS) {
                    config.right_y_axis_unit !== "" && (unit = unit + config.right_y_axis_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.LEFT_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                if (position === constants.X_AXIS) {
                    if (config.x_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                return "function(){return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0];}"
            }
        }

        function formatConfigForBubble(configs, items) {
            var xAxis = [{
                type: "value",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "bottom",
                gridLineWidth: 0
            }];
            var yAxis = [{
                type: "value",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "left",
                gridLineWidth: 0
            }];
            configs.yAxis = yAxis;
            configs.xAxis = xAxis;
            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            configs.plotOptions.tooltip.formatter = config.tooltip;
            formatCordon();
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }
            configs.plotOptions.dataLabels.enabled = config.show_data_label;

            configs.yAxis[0].formatter = formatTickInXYaxis(config.left_y_axis_style, constants.LEFT_AXIS);
            formatNumberLevelInYaxis(config.left_y_axis_number_level, constants.LEFT_AXIS);
            configs.yAxis[0].title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.LEFT_AXIS);
            configs.yAxis[0].title.text = config.show_left_y_axis_title === true ? config.left_y_axis_title + configs.yAxis[0].title.text : configs.yAxis[0].title.text;
            configs.yAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.yAxis[0].title.rotation = constants.ROTATION;

            configs.xAxis[0].formatter = formatTickInXYaxis(config.x_axis_style, constants.X_AXIS);
            formatNumberLevelInXaxis(config.x_axis_number_level);
            configs.xAxis[0].title.text = getXYAxisUnit(config.x_axis_number_level, constants.X_AXIS);
            configs.xAxis[0].title.text = config.show_x_axis_title === true ? config.x_axis_title + configs.xAxis[0].title.text : configs.xAxis[0].title.text;
            configs.xAxis[0].title.align = "center";
            configs.xAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.chartType = "bubble";

            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatCordon() {
                BI.each(config.cordon, function (idx, cor) {
                    if (idx === 0 && xAxis.length > 0) {
                        var magnify = calcMagnify(config.x_axis_number_level);
                        xAxis[0].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "top"
                                }
                            });
                        });
                    }
                    if (idx > 0 && yAxis.length >= idx) {
                        var magnify = 1;
                        switch (idx - 1) {
                            case constants.LEFT_AXIS:
                                magnify = calcMagnify(config.left_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS:
                                magnify = calcMagnify(config.right_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS_SECOND:
                                magnify = calcMagnify(config.right_y_axis_second_number_level);
                                break;
                        }
                        yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "left"
                                }
                            });
                        });
                    }
                })
            }

            function formatNumberLevelInXaxis(type) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (!BI.isNumber(da.x)) {
                                da.x = BI.parseFloat(da.x);
                            }
                            da.x = da.x || 0;
                            da.x = da.x.div(magnify);
                            da.x = da.x.toFixed(constants.FIX_COUNT);
                            if (constants.MINLIMIT.sub(da.x) > 0) {
                                da.x = 0;
                            }
                        })
                    })
                }
            }

            function formatNumberLevelInYaxis(type, position) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (position === item.yAxis) {
                                if (!BI.isNumber(da.y)) {
                                    da.y = BI.parseFloat(da.y);
                                }
                                da.y = da.y || 0;
                                da.y = da.y.div(magnify);
                                da.y = da.y.toFixed(constants.FIX_COUNT);
                                if (constants.MINLIMIT.sub(da.y) > 0) {
                                    da.y = 0;
                                }
                            }
                        })
                    })
                }
                if (type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    //config.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                if (position === constants.RIGHT_AXIS) {
                    config.right_y_axis_unit !== "" && (unit = unit + config.right_y_axis_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.LEFT_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                if (position === constants.X_AXIS) {
                    if (config.x_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                return "function(){return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0];}"
            }
        }

        function formatConfigForDashboard(configs, items) {
            var gaugeAxis = [{
                "minorTickColor": "rgb(226,226,226)",
                "tickColor": "rgb(186,186,186)",
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                "showLabel": true
            }];

            formatChartDashboardStyle();
            configs.chartType = "dashboard";
            delete configs.xAxis;
            delete configs.yAxis;
            return BI.extend(configs, {
                series: items
            });

            function formatChartDashboardStyle() {
                configs.gaugeAxis = gaugeAxis;
                var bands = getBandsStyles(config.bands_styles, config.auto_custom_style);
                var valueLabel = {
                    formatter: {
                        identifier: "${CATEGORY}${SERIES}${VALUE}"
                    }
                };
                var percentageLabel = BI.extend(configs.plotOptions.percentageLabel , {
                    enabled: config.show_percentage === BICst.PERCENTAGE.SHOW
                });
                switch (config.chart_dashboard_type) {
                    case BICst.CHART_SHAPE.HALF_DASHBOARD:
                        setPlotOptions("pointer_semi", bands, configs.plotOptions.valueLabel);
                        break;
                    case BICst.CHART_SHAPE.PERCENT_DASHBOARD:
                        setPlotOptions("ring", bands, valueLabel, percentageLabel);
                        changeMaxMinScale();
                        break;
                    case BICst.CHART_SHAPE.PERCENT_SCALE_SLOT:
                        setPlotOptions("slot", bands, valueLabel, percentageLabel);
                        changeMaxMinScale();
                        break;
                    case BICst.CHART_SHAPE.HORIZONTAL_TUBE:
                        BI.extend(valueLabel, {
                            align: "bottom"
                        });
                        BI.extend(percentageLabel, {
                            align: "bottom"
                        });
                        setPlotOptions("thermometer", bands, valueLabel, percentageLabel, "horizontal", "vertical");
                        changeMaxMinScale();
                        break;
                    case BICst.CHART_SHAPE.VERTICAL_TUBE:
                        BI.extend(valueLabel, {
                            align: "left"
                        });
                        setPlotOptions("thermometer", bands, valueLabel, percentageLabel, "vertical", "horizontal");
                        changeMaxMinScale();
                        break;
                    case BICst.CHART_SHAPE.NORMAL:
                    default:
                        setPlotOptions("pointer", bands, configs.plotOptions.valueLabel);
                        break;
                }
                formatNumberLevelInYaxis(config.dashboard_number_level, constants.LEFT_AXIS);
                if (config.dashboard_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    configs.plotOptions.valueLabel.formatter.valueFormat = function () {
                        return (window.FR ? FR.contentFormat(arguments[0], '#0.00%') : arguments[0]);
                    };
                    configs.gaugeAxis[0].formatter = function () {
                        return (window.FR ? FR.contentFormat(arguments[0], '#0.00%') : arguments[0]) + getXYAxisUnit(config.dashboard_number_level, constants.DASHBOARD_AXIS);
                    };
                } else {
                    configs.gaugeAxis[0].formatter = function () {
                        return this + getXYAxisUnit(config.dashboard_number_level, constants.DASHBOARD_AXIS);
                    };
                }
            }

            function setPlotOptions(style, bands, valueLabel, percentageLabel, thermometerLayout, layout) {
                configs.plotOptions.style = style;
                configs.plotOptions.bands = bands;
                configs.plotOptions.valueLabel = valueLabel;
                configs.plotOptions.percentageLabel = percentageLabel;
                configs.plotOptions.thermometerLayout = thermometerLayout;
                configs.plotOptions.layout = layout;
            }

            function changeMaxMinScale() {
                gaugeAxis[0].max = config.max_scale === "" ? gaugeAxis[0].max : config.max_scale;
                gaugeAxis[0].min = config.min_scale === "" ? gaugeAxis[0].min : config.min_scale;
            }

            function formatNumberLevelInYaxis(type, position) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (position === item.yAxis) {
                                if (!BI.isNumber(da.y)) {
                                    da.y = BI.parseFloat(da.y);
                                }
                                da.y = da.y || 0;
                                da.y = da.y.div(magnify);
                                da.y = da.y.toFixed(constants.FIX_COUNT);
                                if (constants.MINLIMIT.sub(da.y) > 0) {
                                    da.y = 0;
                                }
                            }
                        })
                    })
                }
                if (type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    configs.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.DASHBOARD_AXIS) {
                    config.dashboard_unit !== "" && (unit = unit + config.dashboard_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function getBandsStyles(styles, change) {
                var min = 0, bands = [], color = null, max = null, conditionMax = null;

                BI.each(items, function (idx, item) {
                    var data = item.data[0];
                    if ((BI.isNull(max) || BI.parseFloat(data.y) > BI.parseFloat(max))) {
                        max = data.y
                    }
                });

                switch (change) {

                    case BICst.SCALE_SETTING.AUTO:
                        break;
                    case BICst.SCALE_SETTING.CUSTOM:
                        if (styles.length === 0) {
                            return bands
                        } else {
                            BI.each(styles, function (idx, style) {
                                bands.push({
                                    color: style.color,
                                    from: style.range.min,
                                    to: style.range.max
                                });
                                color = style.color;
                                conditionMax = style.range.max
                            });
                            min = BI.parseInt(styles[0].range.min);
                            bands.push({
                                color: "#808080",
                                from: 0,
                                to: min
                            });

                            var maxScale = _calculateValueNiceDomain(0, max)[1];

                            bands.push({
                                color: color,
                                from: conditionMax,
                                to: maxScale
                            });

                            return bands;
                        }
                }
            }

            function _calculateValueNiceDomain(minValue, maxValue) {

                minValue = Math.min(0, minValue);

                var tickInterval = _linearTickInterval(minValue, maxValue);

                return _linearNiceDomain(minValue, maxValue, tickInterval);
            }

            function _linearTickInterval(minValue, maxValue, m) {

                m = m || 5;
                var span = maxValue - minValue;
                var step = Math.pow(10, Math.floor(Math.log(span / m) / Math.LN10));
                var err = m / span * step;

                if (err <= .15) step *= 10; else if (err <= .35) step *= 5; else if (err <= .75) step *= 2;

                return step;
            }

            function _linearNiceDomain(minValue, maxValue, tickInterval) {

                minValue = VanUtils.accMul(Math.floor(minValue / tickInterval), tickInterval);

                maxValue = VanUtils.accMul(Math.ceil(maxValue / tickInterval), tickInterval);

                return [minValue, maxValue];
            }
        }

        function formatConfigForForceBubble(configs, items) {
            configs.colors = config.chart_color;
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }

            configs.plotOptions.force = true;
            configs.plotOptions.dataLabels.enabled = true;
            configs.plotOptions.dataLabels.formatter.identifier = "${CATEGORY}${VALUE}";
            configs.chartType = "bubble";
            delete configs.xAxis;
            delete configs.yAxis;
            return BI.extend(configs, {
                series: items
            });
        }

        function formatConfigForMultiAxis(configs, items) {

            var xAxis = [{
                type: "category",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "bottom",
                gridLineWidth: 0
            }];

            var yAxis = [];
            BI.each(types, function (idx, type) {
                if (BI.isEmptyArray(type)) {
                    return;
                }
                var newYAxis = {
                    type: "value",
                    title: {
                        style: {
                            "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                            "color": "#808080",
                            "fontSize": "12px",
                            "fontWeight": ""
                        }
                    },
                    labelStyle: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                    },
                    position: idx > 0 ? "right" : "left",
                    lineWidth: 1,
                    axisIndex: idx,
                    gridLineWidth: 0
                };
                yAxis.push(newYAxis);
            });

            configs.yAxis = yAxis;
            configs.xAxis = xAxis;

            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            formatCordon();
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }
            configs.plotOptions.dataLabels.enabled = config.show_data_label;
            configs.dataSheet.enabled = config.show_data_table;
            configs.xAxis[0].showLabel = !configs.dataSheet.enabled;
            configs.zoom.zoomTool.visible = config.show_zoom;
            if (config.show_zoom === true) {
                delete configs.dataSheet;
                delete configs.zoom.zoomType;
            }

            BI.each(configs.yAxis, function (idx, axis) {
                switch (axis.axisIndex) {
                    case constants.LEFT_AXIS:
                        axis.reversed = config.left_y_axis_reversed;
                        axis.formatter = formatTickInXYaxis(config.left_y_axis_style, constants.LEFT_AXIS);
                        formatNumberLevelInYaxis(config.left_y_axis_number_level, idx);
                        axis.title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.LEFT_AXIS);
                        axis.title.text = config.show_left_y_axis_title === true ? config.left_y_axis_title + axis.title.text : axis.title.text;
                        axis.gridLineWidth = config.show_grid_line === true ? 1 : 0;
                        axis.title.rotation = constants.ROTATION;
                        axis.labelStyle.color = axis.lineColor = axis.tickColor = configs.colors[0];
                        break;
                    case constants.RIGHT_AXIS:
                        axis.reversed = config.right_y_axis_reversed;
                        axis.formatter = formatTickInXYaxis(config.right_y_axis_style, constants.RIGHT_AXIS);
                        formatNumberLevelInYaxis(config.right_y_axis_number_level, idx);
                        axis.title.text = getXYAxisUnit(config.right_y_axis_number_level, constants.RIGHT_AXIS);
                        axis.title.text = config.show_right_y_axis_title === true ? config.right_y_axis_title + axis.title.text : axis.title.text;
                        axis.gridLineWidth = config.show_grid_line === true ? 1 : 0;
                        axis.title.rotation = constants.ROTATION;
                        axis.labelStyle.color = axis.lineColor = axis.tickColor = configs.colors[1];
                        break;
                    case constants.RIGHT_AXIS_SECOND:
                        axis.reversed = config.right_y_axis_second_reversed;
                        axis.formatter = formatTickInXYaxis(config.right_y_axis_second_style, constants.RIGHT_AXIS_SECOND);
                        formatNumberLevelInYaxis(config.right_y_axis_second_number_level, idx);
                        axis.title.text = getXYAxisUnit(config.right_y_axis_second_number_level, constants.RIGHT_AXIS_SECOND);
                        axis.title.text = config.show_right_y_axis_second_title === true ? config.right_y_axis_second_title + axis.title.text : axis.title.text;
                        axis.gridLineWidth = config.show_grid_line === true ? 1 : 0;
                        axis.title.rotation = constants.ROTATION;
                        axis.labelStyle.color = axis.lineColor = axis.tickColor = configs.colors[2];
                        break;
                }
            });
            configs.xAxis[0].title.text = config.x_axis_title;
            configs.xAxis[0].labelRotation = config.text_direction;
            configs.xAxis[0].title.text = config.show_x_axis_title === true ? configs.xAxis[0].title.text : "";
            configs.xAxis[0].title.align = "center";
            configs.xAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;

            var lineItem = [];
            var otherItem = [];
            BI.each(items, function (idx, item) {
                item.color = [configs.yAxis[idx].labelStyle.color];
                if (item.type === "line") {
                    lineItem.push(item);
                } else {
                    otherItem.push(item);
                }
            });

            return BI.extend(configs, {
                series: BI.concat(otherItem, lineItem)
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatCordon() {
                BI.each(config.cordon, function (idx, cor) {
                    if (idx === 0 && xAxis.length > 0) {
                        var magnify = calcMagnify(config.x_axis_number_level);
                        xAxis[0].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "top"
                                }
                            });
                        });
                    }
                    if (idx > 0 && yAxis.length >= idx) {
                        var magnify = 1;
                        switch (idx - 1) {
                            case constants.LEFT_AXIS:
                                magnify = calcMagnify(config.left_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS:
                                magnify = calcMagnify(config.right_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS_SECOND:
                                magnify = calcMagnify(config.right_y_axis_second_number_level);
                                break;
                        }
                        yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "left"
                                }
                            });
                        });
                    }
                })
            }

            function formatNumberLevelInYaxis(type, position) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (position === item.yAxis) {
                                if (!BI.isNumber(da.y)) {
                                    da.y = BI.parseFloat(da.y);
                                }
                                da.y = da.y || 0;
                                da.y = da.y.div(magnify);
                                da.y = da.y.toFixed(constants.FIX_COUNT);
                                if (constants.MINLIMIT.sub(da.y) > 0) {
                                    da.y = 0;
                                }
                            }
                        })
                    })
                }
                if (type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    configs.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                if (position === constants.RIGHT_AXIS) {
                    config.right_y_axis_unit !== "" && (unit = unit + config.right_y_axis_unit)
                }
                if (position === constants.RIGHT_AXIS_SECOND) {
                    config.right_y_axis_second_unit !== "" && (unit = unit + config.right_y_axis_second_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.LEFT_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                if (position === constants.RIGHT_AXIS) {
                    if (config.right_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                if (position === constants.RIGHT_AXIS_SECOND) {
                    if (config.right_y_axis_second_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '" + formatter + "') : (-1) * arguments[0];}"
            }
        }

        function formatConfigForPie(configs, items) {
            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            formatChartPieStyle();

            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    //config.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }

            configs.plotOptions.dataLabels.enabled = config.show_data_label;

            configs.chartType = "pie";
            delete configs.xAxis;
            delete configs.yAxis;
            configs.plotOptions.dataLabels.align = "outside";
            configs.plotOptions.dataLabels.connectorWidth = "outside";
            configs.plotOptions.dataLabels.formatter.identifier = "${VALUE}${PERCENT}";
            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatChartPieStyle() {
                switch (config.chart_pie_type) {
                    case BICst.CHART_SHAPE.EQUAL_ARC_ROSE:
                        configs.plotOptions.roseType = "sameArc";
                        break;
                    case BICst.CHART_SHAPE.NOT_EQUAL_ARC_ROSE:
                        configs.plotOptions.roseType = "differentArc";
                        break;
                    case BICst.CHART_SHAPE.NORMAL:
                    default:
                        delete configs.plotOptions.roseType;
                        break;
                }
                configs.plotOptions.innerRadius = config.chart_inner_radius;
                configs.plotOptions.endAngle = config.chart_total_angle;
            }
        }

        function formatConfigForDonut(configs, items) {
            configs.colors = config.chart_color;
            configs.style = formatChartStyle();

            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }

            configs.plotOptions.dataLabels.enabled = config.show_data_label;

            configs.plotOptions.innerRadius = "50.0%";
            configs.chartType = "pie";
            configs.plotOptions.dataLabels.align = "outside";
            configs.plotOptions.dataLabels.connectorWidth = "outside";
            configs.plotOptions.dataLabels.formatter.identifier = "${VALUE}${PERCENT}";
            delete configs.xAxis;
            delete configs.yAxis;
            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }
        }

        function formatConfigForAccumulateBar(configs, items) {

            var xAxis = [{
                type: "value",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                formatter: "function(){if(this>0) return this; else return this*(-1); }",
                gridLineWidth: 0
            }];
            var yAxis = [{
                type: "category",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                gridLineWidth: 0,
                position: "left"
            }];

            configs.yAxis = yAxis;
            configs.xAxis = xAxis;
            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            formatCordon();
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }
            configs.plotOptions.dataLabels.enabled = config.show_data_label;
            configs.dataSheet.enabled = config.show_data_table;
            configs.xAxis[0].showLabel = !configs.dataSheet.enabled;
            configs.zoom.zoomTool.visible = config.show_zoom;
            if (config.show_zoom === true) {
                delete configs.dataSheet;
                delete configs.zoom.zoomType;
            }
            configs.yAxis[0].title.text = getXYAxisUnit(config.x_axis_number_level, constants.LEFT_AXIS);
            configs.yAxis[0].title.text = config.show_left_y_axis_title === true ? config.x_axis_title + configs.yAxis[0].title.text : configs.yAxis[0].title.text;
            configs.yAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.yAxis[0].title.rotation = constants.ROTATION;

            configs.yAxis[0].labelRotation = config.text_direction;
            configs.xAxis[0].formatter = formatTickInXYaxis(config.left_y_axis_style, constants.X_AXIS);
            formatNumberLevelInXaxis(config.left_y_axis_number_level);
            configs.xAxis[0].title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.X_AXIS);
            configs.xAxis[0].title.text = config.show_left_y_axis_title === true ? config.left_y_axis_title + configs.xAxis[0].title.text : configs.xAxis[0].title.text;
            configs.xAxis[0].title.align = "center";
            configs.xAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.chartType = "bar";
            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatCordon() {
                BI.each(config.cordon, function (idx, cor) {
                    if (idx === 0 && xAxis.length > 0) {
                        var magnify = calcMagnify(config.left_y_axis_number_level);
                        xAxis[0].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "top"
                                }
                            });
                        });
                    }
                    if (idx > 0 && yAxis.length >= idx) {
                        var magnify = 1;
                        switch (idx - 1) {
                            case constants.LEFT_AXIS:
                                magnify = calcMagnify(config.x_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS:
                                magnify = calcMagnify(config.right_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS_SECOND:
                                magnify = calcMagnify(config.right_y_axis_second_number_level);
                                break;
                        }
                        yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "left"
                                }
                            });
                        });
                    }
                })
            }

            function formatNumberLevelInXaxis(type) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (!BI.isNumber(da.x)) {
                                da.x = BI.parseFloat(da.x);
                            }
                            da.x = da.x || 0;
                            da.x = da.x.div(magnify);
                            da.x = da.x.toFixed(constants.FIX_COUNT);
                            if (constants.MINLIMIT.sub(da.x) > 0) {
                                da.x = 0;
                            }
                        })
                    })
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.RIGHT_AXIS) {
                    config.right_y_axis_unit !== "" && (unit = unit + config.right_y_axis_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.X_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '" + formatter + "') : (-1) * arguments[0];}"
            }
        }

        function formatConfigForCompareBar(configs, items) {

            var xAxis = [{
                type: "value",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                formatter: "function(){if(this>0) return this; else return this*(-1); }",
                gridLineWidth: 0
            }];
            var yAxis = [{
                type: "category",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                gridLineWidth: 0,
                position: "left"
            }];
            configs.yAxis = yAxis;
            configs.xAxis = xAxis;
            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            formatCordon();
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }
            configs.plotOptions.dataLabels.enabled = config.show_data_label;
            configs.dataSheet.enabled = config.show_data_table;
            if (configs.dataSheet.enabled === true) {
                configs.xAxis[0].showLabel = false;
            }
            configs.zoom.zoomTool.visible = config.show_zoom;
            if (configs.show_zoom === true) {
                delete configs.dataSheet;
                delete configs.zoom.zoomType;
            }
            configs.plotOptions.tooltip.formatter.valueFormat = "function(){if(this > 0){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0];} else {return window.FR ? (-1) * FR.contentFormat(arguments[0], '#.##') : (-1) * arguments[0];}}";


            configs.yAxis[0].title.text = getXYAxisUnit(config.x_axis_number_level, constants.LEFT_AXIS);
            configs.yAxis[0].title.text = config.show_left_y_axis_title === true ? config.x_axis_title + configs.yAxis[0].title.text : configs.yAxis[0].title.text;
            configs.yAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.yAxis[0].title.rotation = constants.ROTATION;

            configs.yAxis[0].labelRotation = config.text_direction;
            configs.xAxis[0].formatter = formatTickInXYaxis(config.left_y_axis_style, constants.X_AXIS);
            formatNumberLevelInXaxis(config.left_y_axis_number_level);
            configs.xAxis[0].title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.X_AXIS);
            configs.xAxis[0].title.text = config.show_left_y_axis_title === true ? config.left_y_axis_title + configs.xAxis[0].title.text : configs.xAxis[0].title.text;
            configs.xAxis[0].title.align = "center";
            configs.xAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.chartType = "bar";
            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatCordon() {
                BI.each(config.cordon, function (idx, cor) {
                    if (idx === 0 && xAxis.length > 0) {
                        var magnify = calcMagnify(config.left_y_axis_number_level);
                        xAxis[0].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "top"
                                }
                            });
                        });
                    }
                    if (idx > 0 && yAxis.length >= idx) {
                        var magnify = 1;
                        switch (idx - 1) {
                            case constants.LEFT_AXIS:
                                magnify = calcMagnify(config.x_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS:
                                magnify = calcMagnify(config.right_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS_SECOND:
                                magnify = calcMagnify(config.right_y_axis_second_number_level);
                                break;
                        }
                        yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "left"
                                }
                            });
                        });
                    }
                })
            }

            function formatNumberLevelInXaxis(type) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (!BI.isNumber(da.x)) {
                                da.x = BI.parseFloat(da.x);
                            }
                            da.x = da.x || 0;
                            da.x = da.x.div(magnify);
                            da.x = da.x.toFixed(constants.FIX_COUNT);
                            if (constants.MINLIMIT.sub(Math.abs(da.x)) > 0) {
                                da.x = 0;
                            }
                        })
                    })
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.RIGHT_AXIS) {
                    config.right_y_axis_unit !== "" && (unit = unit + config.right_y_axis_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.X_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                        return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "').substring(1) : arguments[0].substring(1);}"
                    }
                }
                return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (FR.contentFormat(arguments[0], '" + formatter + "') + '').substring(1) : (arguments[0] + '').substring(1);}"
            }
        }

        function formatConfigForBar(configs, items) {
            var xAxis = [{
                type: "value",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                formatter: "function(){if(this>0) return this; else return this*(-1); }",
                gridLineWidth: 0
            }];
            var yAxis = [{
                type: "category",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                gridLineWidth: 0,
                position: "left"
            }];
            configs.yAxis = yAxis;
            configs.xAxis = xAxis;
            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            formatCordon();
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }
            configs.plotOptions.dataLabels.enabled = config.show_data_label;
            configs.dataSheet.enabled = config.show_data_table;
            if (configs.dataSheet.enabled === true) {
                configs.xAxis[0].showLabel = false;
            }
            configs.zoom.zoomTool.visible = config.show_zoom;
            if (config.show_zoom === true) {
                delete configs.dataSheet;
                delete configs.zoom.zoomType;
            }

            //分类轴

            configs.show_left_y_axis_title === true && (configs.yAxis[0].title.text = config.x_axis_title);
            configs.yAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.yAxis[0].title.rotation = constants.ROTATION;
            configs.yAxis[0].labelRotation = config.text_direction;

            //值轴
            configs.xAxis[0].formatter = formatTickInXYaxis(config.left_y_axis_style, constants.X_AXIS);
            formatNumberLevelInXaxis(config.left_y_axis_number_level);
            configs.xAxis[0].title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.X_AXIS);
            configs.xAxis[0].title.text = config.show_x_axis_title === true ? config.left_y_axis_title + configs.xAxis[0].title.text : configs.xAxis[0].title.text;
            configs.xAxis[0].title.align = "center";
            configs.xAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.chartType = "bar";
            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatCordon() {
                BI.each(config.cordon, function (idx, cor) {
                    if (idx === 0 && xAxis.length > 0) {
                        var magnify = calcMagnify(config.left_y_axis_number_level);
                        xAxis[0].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Arial",
                                        "color": "rgba(0,0,0,1.0)",
                                        "fontSize": "9pt",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "top"
                                }
                            });
                        });
                    }
                    if (idx > 0 && yAxis.length >= idx) {
                        var magnify = 1;
                        switch (idx - 1) {
                            case constants.LEFT_AXIS:
                                magnify = calcMagnify(config.x_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS:
                                magnify = calcMagnify(config.right_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS_SECOND:
                                magnify = calcMagnify(config.right_y_axis_second_number_level);
                                break;
                        }
                        yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "left"
                                }
                            });
                        });
                    }
                })
            }

            function formatNumberLevelInXaxis(type) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (!BI.isNumber(da.x)) {
                                da.x = BI.parseFloat(da.x);
                            }
                            da.x = da.x || 0;
                            da.x = da.x.div(magnify);
                            da.x = da.x.toFixed(constants.FIX_COUNT);
                            if (constants.MINLIMIT.sub(da.x) > 0) {
                                da.x = 0;
                            }
                        })
                    })
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.RIGHT_AXIS) {
                    config.right_y_axis_unit !== "" && (unit = unit + config.right_y_axis_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.X_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '" + formatter + "') : (-1) * arguments[0];}"
            }
        }

        function formatConfigForRange(configs, items) {
            var xAxis = [{
                type: "category",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "bottom",
                gridLineWidth: 0
            }];
            var yAxis = [{
                type: "value",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "left",
                gridLineWidth: 0
            }];
            configs.yAxis = yAxis;
            configs.xAxis = xAxis;
            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            formatCordon();
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }
            configs.plotOptions.dataLabels.enabled = config.show_data_label;


            configs.yAxis[0].reversed = config.left_y_axis_reversed;
            configs.yAxis[0].formatter = formatTickInXYaxis(config.left_y_axis_style, constants.LEFT_AXIS);
            formatNumberLevelInYaxis(config.left_y_axis_number_level, constants.LEFT_AXIS);
            configs.yAxis[0].title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.LEFT_AXIS);
            configs.yAxis[0].title.text = config.show_left_y_axis_title === true ? config.left_y_axis_title + configs.yAxis[0].title.text : configs.yAxis[0].title.text;
            configs.yAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.yAxis[0].title.rotation = constants.ROTATION;

            configs.xAxis[0].title.text = config.x_axis_title;
            configs.xAxis[0].labelRotation = config.text_direction;
            configs.xAxis[0].title.text = config.show_x_axis_title === true ? configs.xAxis[0].title.text : "";
            configs.xAxis[0].title.align = "center";
            configs.xAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.chartType = "area";
            configs.plotOptions.tooltip.formatter.identifier = "${CATEGORY}${VALUE}";
            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatCordon() {
                BI.each(config.cordon, function (idx, cor) {
                    if (idx === 0 && xAxis.length > 0) {
                        var magnify = calcMagnify(config.x_axis_number_level);
                        xAxis[0].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "top"
                                }
                            });
                        });
                    }
                    if (idx > 0 && yAxis.length >= idx) {
                        var magnify = 1;
                        switch (idx - 1) {
                            case constants.LEFT_AXIS:
                                magnify = calcMagnify(config.left_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS:
                                magnify = calcMagnify(config.right_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS_SECOND:
                                magnify = calcMagnify(config.right_y_axis_second_number_level);
                                break;
                        }
                        yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "left"
                                }
                            });
                        });
                    }
                })
            }

            function formatNumberLevelInYaxis(type, position) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (position === item.yAxis) {
                                if (!BI.isNumber(da.y)) {
                                    da.y = BI.parseFloat(da.y);
                                }
                                da.y = da.y || 0;
                                da.y = da.y.div(magnify);
                                da.y = da.y.toFixed(constants.FIX_COUNT);
                                if (constants.MINLIMIT.sub(da.y) > 0) {
                                    da.y = 0;
                                }
                            }
                        })
                    })
                }
                if (type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    configs.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.LEFT_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '" + formatter + "') : (-1) * arguments[0];}"
            }
        }

        function formatConfigForFall(configs, items) {

            var xAxis = [{
                type: "category",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "bottom",
                gridLineWidth: 0
            }];
            var yAxis = [{
                type: "value",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "left",
                gridLineWidth: 0
            }];
            configs.yAxis = yAxis;
            configs.xAxis = xAxis;
            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            formatCordon();
            configs.legend.enabled = false;
            configs.plotOptions.dataLabels.enabled = config.show_data_label;
            configs.dataSheet.enabled = config.show_data_table;
            if (configs.dataSheet.enabled === true) {
                configs.xAxis[0].showLabel = false;
            }
            configs.zoom.zoomTool.visible = config.show_zoom;
            if (configs.show_zoom === true) {
                delete configs.dataSheet;
                delete configs.zoom.zoomType;
            }


            configs.yAxis[0].reversed = config.left_y_axis_reversed;
            configs.yAxis[0].formatter = formatTickInXYaxis(config.left_y_axis_style, constants.LEFT_AXIS);
            formatNumberLevelInYaxis(config.left_y_axis_number_level, constants.LEFT_AXIS);
            configs.yAxis[0].title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.LEFT_AXIS);
            configs.yAxis[0].title.text = config.show_left_y_axis_title === true ? config.left_y_axis_title + configs.yAxis[0].title.text : configs.yAxis[0].title.text;
            configs.yAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.yAxis[0].title.rotation = constants.ROTATION;

            configs.xAxis[0].title.text = config.x_axis_title;
            configs.xAxis[0].labelRotation = config.text_direction;
            configs.xAxis[0].title.text = config.show_x_axis_title === true ? configs.xAxis[0].title.text : "";
            configs.xAxis[0].title.align = "center";
            configs.xAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;

            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatCordon() {
                BI.each(config.cordon, function (idx, cor) {
                    if (idx === 0 && xAxis.length > 0) {
                        var magnify = calcMagnify(config.x_axis_number_level);
                        xAxis[0].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "top"
                                }
                            });
                        });
                    }
                    if (idx > 0 && yAxis.length >= idx) {
                        var magnify = 1;
                        switch (idx - 1) {
                            case constants.LEFT_AXIS:
                                magnify = calcMagnify(config.left_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS:
                                magnify = calcMagnify(config.right_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS_SECOND:
                                magnify = calcMagnify(config.right_y_axis_second_number_level);
                                break;
                        }
                        yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "left"
                                }
                            });
                        });
                    }
                })
            }

            function formatNumberLevelInYaxis(type, position) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (position === item.yAxis) {
                                if (!BI.isNumber(da.y)) {
                                    da.y = BI.parseFloat(da.y);
                                }
                                da.y = da.y || 0;
                                da.y = da.y.div(magnify);
                                da.y = da.y.toFixed(constants.FIX_COUNT);
                                if (constants.MINLIMIT.sub(da.y) > 0) {
                                    da.y = 0;
                                }
                            }
                        })
                    })
                }
                if (type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    configs.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                if (position === constants.RIGHT_AXIS) {
                    config.right_y_axis_unit !== "" && (unit = unit + config.right_y_axis_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.LEFT_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                if (position === constants.RIGHT_AXIS) {
                    if (config.right_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '" + formatter + "') : (-1) * arguments[0];}"
            }
        }

        function formatConfigForCompare(configs, items, cType) {

            var xAxis = [{
                type: "category",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "bottom",
                gridLineWidth: 0
            }, {
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "top",
                gridLineWidth: 0,
                type: "category",
                showLabel: false
            }];

            var types = [];
            BI.each(items, function (idx, axisItems) {
                var type = [];
                BI.each(axisItems, function (id, item) {
                    type.push(cType);
                });
                types.push(type);
            });

            var yAxis = [];
            BI.each(types, function (idx, type) {
                if (BI.isEmptyArray(type)) {
                    return;
                }
                var newYAxis = {
                    type: "value",
                    title: {
                        style: {
                            "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                            "color": "#808080",
                            "fontSize": "12px",
                            "fontWeight": ""
                        }
                    },
                    labelStyle: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                    },
                    position: idx > 0 ? "right" : "left",
                    lineWidth: 1,
                    axisIndex: idx,
                    reversed: idx > 0,
                    gridLineWidth: 0
                };
                yAxis.push(newYAxis);
            });
            configs.yAxis = yAxis;
            configs.xAxis = xAxis;
            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            formatChartLineStyle();
            formatCordon();
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }
            configs.plotOptions.dataLabels.enabled = config.show_data_label;
            configs.dataSheet.enabled = config.show_data_table;
            configs.xAxis[0].showLabel = !configs.dataSheet.enabled;
            configs.zoom.zoomTool.visible = config.show_zoom;
            if (config.show_zoom === true) {
                delete configs.dataSheet;
                delete configs.zoom.zoomType;
            }


            BI.each(configs.yAxis, function (idx, axis) {
                switch (axis.axisIndex) {
                    case constants.LEFT_AXIS:
                        axis.reversed = false;
                        axis.formatter = formatTickInXYaxis(config.left_y_axis_style, constants.LEFT_AXIS);
                        formatNumberLevelInYaxis(config.left_y_axis_number_level, idx);
                        axis.title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.LEFT_AXIS);
                        axis.title.text = config.show_left_y_axis_title === true ? config.left_y_axis_title + axis.title.text : axis.title.text;
                        axis.gridLineWidth = config.show_grid_line === true ? 1 : 0;
                        axis.title.rotation = constants.ROTATION;
                        break;
                    case constants.RIGHT_AXIS:
                        axis.reversed = true;
                        axis.formatter = formatTickInXYaxis(config.right_y_axis_style, constants.RIGHT_AXIS);
                        formatNumberLevelInYaxis(config.right_y_axis_number_level, idx);
                        axis.title.text = getXYAxisUnit(config.right_y_axis_number_level, constants.RIGHT_AXIS);
                        axis.title.text = config.show_right_y_axis_title === true ? config.right_y_axis_title + axis.title.text : axis.title.text;
                        axis.gridLineWidth = config.show_grid_line === true ? 1 : 0;
                        axis.title.rotation = constants.ROTATION;
                        break;
                }
                var res = _calculateValueNiceDomain(0, maxes[axis.axisIndex]);
                axis.max = res[1].mul(2);
                axis.min = res[0].mul(2);
                axis.tickInterval = BI.parseFloat((BI.parseFloat(axis.max).sub(BI.parseFloat(axis.min)))).div(5);
            });

            configs.xAxis[0].title.text = config.x_axis_title;
            configs.xAxis[0].labelRotation = config.text_direction;
            configs.xAxis[0].title.text = config.show_x_axis_title === true ? configs.xAxis[0].title.text : "";
            configs.xAxis[0].title.align = "center";
            configs.xAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;


            return BI.extend(configs, {
                series: items
            });

            function _calculateValueNiceDomain(minValue, maxValue) {

                minValue = Math.min(0, minValue);

                var tickInterval = _linearTickInterval(minValue, maxValue);

                return _linearNiceDomain(minValue, maxValue, tickInterval);
            }

            function _linearTickInterval(minValue, maxValue, m) {

                m = m || 5;
                var span = maxValue - minValue;
                var step = Math.pow(10, Math.floor(Math.log(span / m) / Math.LN10));
                var err = m / span * step;

                if (err <= .15) step *= 10; else if (err <= .35) step *= 5; else if (err <= .75) step *= 2;

                return step;
            }

            function _linearNiceDomain(minValue, maxValue, tickInterval) {

                minValue = VanUtils.accMul(Math.floor(minValue / tickInterval), tickInterval);

                maxValue = VanUtils.accMul(Math.ceil(maxValue / tickInterval), tickInterval);

                return [minValue, maxValue];
            }

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatCordon() {
                BI.each(config.cordon, function (idx, cor) {
                    if (idx === 0 && xAxis.length > 0) {
                        var magnify = calcMagnify(config.x_axis_number_level);
                        xAxis[0].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "top"
                                }
                            });
                        });
                    }
                    if (idx > 0 && yAxis.length >= idx) {
                        var magnify = 1;
                        switch (idx - 1) {
                            case constants.LEFT_AXIS:
                                magnify = calcMagnify(config.left_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS:
                                magnify = calcMagnify(config.right_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS_SECOND:
                                magnify = calcMagnify(config.right_y_axis_second_number_level);
                                break;
                        }
                        yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "left"
                                }
                            });
                        });
                    }
                })
            }

            function formatChartLineStyle() {
                switch (config.chart_line_type) {
                    case BICst.CHART_SHAPE.RIGHT_ANGLE:
                        configs.plotOptions.curve = false;
                        configs.plotOptions.step = true;
                        break;
                    case BICst.CHART_SHAPE.CURVE:
                        configs.plotOptions.curve = true;
                        configs.plotOptions.step = false;
                        break;
                    case BICst.CHART_SHAPE.NORMAL:
                    default:
                        configs.plotOptions.curve = false;
                        configs.plotOptions.step = false;
                        break;
                }
            }

            function formatNumberLevelInYaxis(type, position) {
                var magnify = calcMagnify(type);
                BI.each(items, function (idx, item) {
                    var max = null;
                    BI.each(item.data, function (id, da) {
                        if (position === item.yAxis) {
                            if (!BI.isNumber(da.y)) {
                                da.y = BI.parseFloat(da.y);
                            }
                            da.y = da.y || 0;
                            da.y = da.y.div(magnify);
                            da.y = da.y.toFixed(constants.FIX_COUNT);
                            if (constants.MINLIMIT.sub(da.y) > 0) {
                                da.y = 0;
                            }
                            if ((BI.isNull(max) || BI.parseFloat(da.y) > BI.parseFloat(max))) {
                                max = da.y;
                            }
                        }
                    });
                    if (BI.isNotNull(max)) {
                        maxes.push(max);
                    }
                });
                if (type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    configs.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                if (position === constants.RIGHT_AXIS) {
                    config.right_y_axis_unit !== "" && (unit = unit + config.right_y_axis_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.LEFT_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                if (position === constants.RIGHT_AXIS) {
                    if (config.right_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '" + formatter + "') : (-1) * arguments[0];}"
            }
        }

        function formatConfigForPercent(configs, items, cType) {
            var xAxis = [{
                type: "category",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "bottom",
                gridLineWidth: 0
            }];
            var yAxis = [{
                type: "value",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "left",
                gridLineWidth: 0
            }];
            configs.yAxis = yAxis;
            configs.xAxis = xAxis;
            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            formatCordon();
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }
            configs.plotOptions.dataLabels.enabled = config.show_data_label;
            configs.dataSheet.enabled = config.show_data_table;
            configs.xAxis[0].showLabel = configs.dataSheet.enabled;
            configs.zoom.zoomTool.visible = config.show_zoom;
            if (config.show_zoom === true) {
                delete configs.dataSheet;
                delete configs.zoom.zoomType;
            }


            configs.yAxis[0].reversed = config.left_y_axis_reversed;
            configs.yAxis[0].formatter = formatTickInXYaxis(config.left_y_axis_style, constants.LEFT_AXIS);
            formatNumberLevelInYaxis(config.left_y_axis_number_level, constants.LEFT_AXIS);
            configs.yAxis[0].title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.LEFT_AXIS);
            configs.yAxis[0].title.text = config.show_left_y_axis_title === true ? config.left_y_axis_title + configs.yAxis[0].title.text : configs.yAxis[0].title.text;
            configs.yAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.yAxis[0].title.rotation = constants.ROTATION;

            configs.xAxis[0].title.text = config.x_axis_title;
            configs.xAxis[0].labelRotation = config.text_direction;
            configs.xAxis[0].title.text = config.show_x_axis_title === true ? configs.xAxis[0].title.text : "";
            configs.xAxis[0].title.align = "center";
            configs.xAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.chartType = "area";
            configs.plotOptions.tooltip.formatter.identifier = "${CATEGORY}${SERIES}${PERCENT}";
            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatCordon() {
                BI.each(config.cordon, function (idx, cor) {
                    if (idx === 0 && xAxis.length > 0) {
                        var magnify = calcMagnify(config.x_axis_number_level);
                        xAxis[0].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "top"
                                }
                            });
                        });
                    }
                    if (idx > 0 && yAxis.length >= idx) {
                        var magnify = 1;
                        switch (idx - 1) {
                            case constants.LEFT_AXIS:
                                magnify = calcMagnify(config.left_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS:
                                magnify = calcMagnify(config.right_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS_SECOND:
                                magnify = calcMagnify(config.right_y_axis_second_number_level);
                                break;
                        }
                        yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "left"
                                }
                            });
                        });
                    }
                })
            }

            function formatNumberLevelInYaxis(type, position) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (position === item.yAxis) {
                                if (!BI.isNumber(da.y)) {
                                    da.y = BI.parseFloat(da.y);
                                }
                                da.y = da.y || 0;
                                da.y = da.y.div(magnify);
                                da.y = da.y.toFixed(constants.FIX_COUNT);
                                if (constants.MINLIMIT.sub(da.y) > 0) {
                                    da.y = 0;
                                }
                            }
                        })
                    })
                }
                if (type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    configs.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.LEFT_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '" + formatter + "') : (-1) * arguments[0];}"
            }
        }

        function formatConfigForLine(configs, items) {
            var xAxis = [{
                type: "category",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "bottom",
                gridLineWidth: 0
            }];

            var yAxis = [];
            BI.each(types, function (idx, type) {
                if (BI.isEmptyArray(type)) {
                    return;
                }
                var newYAxis = {
                    type: "value",
                    title: {
                        style: {
                            "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                            "color": "#808080",
                            "fontSize": "12px",
                            "fontWeight": ""
                        }
                    },
                    labelStyle: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                    },
                    position: idx > 0 ? "right" : "left",
                    lineWidth: 1,
                    axisIndex: idx,
                    gridLineWidth: 0
                };
                yAxis.push(newYAxis);
            });

            configs.yAxis = yAxis;
            configs.xAxis = xAxis;

            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            formatCordon();
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }
            configs.plotOptions.dataLabels.enabled = config.show_data_label;
            configs.dataSheet.enabled = config.show_data_table;
            configs.xAxis[0].showLabel = !configs.dataSheet.enabled;
            configs.zoom.zoomTool.visible = config.show_zoom;
            if (config.show_zoom === true) {
                delete configs.dataSheet;
                delete configs.zoom.zoomType;
            }


            BI.each(configs.yAxis, function (idx, axis) {
                switch (axis.axisIndex) {
                    case constants.LEFT_AXIS:
                        axis.reversed = config.left_y_axis_reversed;
                        axis.formatter = formatTickInXYaxis(config.left_y_axis_style, constants.LEFT_AXIS);
                        formatNumberLevelInYaxis(config.left_y_axis_number_level, idx);
                        axis.title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.LEFT_AXIS);
                        axis.title.text = config.show_left_y_axis_title === true ? config.left_y_axis_title + axis.title.text : axis.title.text;
                        axis.gridLineWidth = config.show_grid_line === true ? 1 : 0;
                        axis.title.rotation = constants.ROTATION;
                        break;
                    case constants.RIGHT_AXIS:
                        axis.reversed = config.right_y_axis_reversed;
                        axis.formatter = formatTickInXYaxis(config.right_y_axis_style, constants.RIGHT_AXIS);
                        formatNumberLevelInYaxis(config.right_y_axis_number_level, idx);
                        axis.title.text = getXYAxisUnit(config.right_y_axis_number_level, constants.RIGHT_AXIS);
                        axis.title.text = config.show_right_y_axis_title === true ? config.right_y_axis_title + axis.title.text : axis.title.text;
                        axis.gridLineWidth = config.show_grid_line === true ? 1 : 0;
                        axis.title.rotation = constants.ROTATION;
                        break;
                }
            });
            configs.xAxis[0].labelRotation = config.text_direction;
            configs.xAxis[0].title.text = config.show_x_axis_title === true ? configs.xAxis[0].title.text : "";
            configs.xAxis[0].title.align = "center";
            configs.xAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;

            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatCordon() {
                BI.each(config.cordon, function (idx, cor) {
                    if (idx === 0 && xAxis.length > 0) {
                        var magnify = calcMagnify(config.x_axis_number_level);
                        xAxis[0].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "top"
                                }
                            });
                        });
                    }
                    if (idx > 0 && yAxis.length >= idx) {
                        var magnify = 1;
                        switch (idx - 1) {
                            case constants.LEFT_AXIS:
                                magnify = calcMagnify(config.left_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS:
                                magnify = calcMagnify(config.right_y_axis_number_level);
                                break;
                        }
                        yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "left"
                                }
                            });
                        });
                    }
                })
            }

            function formatNumberLevelInYaxis(type, position) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (position === item.yAxis) {
                                if (!BI.isNumber(da.y)) {
                                    da.y = BI.parseFloat(da.y);
                                }
                                da.y = da.y || 0;
                                da.y = da.y.div(magnify);
                                da.y = da.y.toFixed(constants.FIX_COUNT);
                                if (constants.MINLIMIT.sub(da.y) > 0) {
                                    da.y = 0;
                                }
                            }
                        })
                    })
                }
                if (type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    configs.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                if (position === constants.RIGHT_AXIS) {
                    config.right_y_axis_unit !== "" && (unit = unit + config.right_y_axis_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.LEFT_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                if (position === constants.RIGHT_AXIS) {
                    if (config.right_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '" + formatter + "') : (-1) * arguments[0];}"
            }
        }

        function formatConfigForArea(configs, items) {
            var xAxis = [{
                type: "category",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "bottom",
                gridLineWidth: 0
            }];

            var types = [];
            BI.each(items, function (idx, axisItems) {
                var type = [];
                BI.each(axisItems, function (id, item) {
                    type.push(BICst.WIDGET.AREA);
                });
                types.push(type);
            });

            var yAxis = [];
            BI.each(types, function (idx, type) {
                if (BI.isEmptyArray(type)) {
                    return;
                }
                var newYAxis = {
                    type: "value",
                    title: {
                        style: {
                            "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                            "color": "#808080",
                            "fontSize": "12px",
                            "fontWeight": ""
                        }
                    },
                    labelStyle: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                    },
                    position: idx > 0 ? "right" : "left",
                    lineWidth: 1,
                    axisIndex: idx,
                    gridLineWidth: 0
                };
                yAxis.push(newYAxis);
            });

            configs.yAxis = yAxis;
            configs.xAxis = xAxis;

            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            formatCordon();
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }
            configs.plotOptions.dataLabels.enabled = config.show_data_label;
            configs.dataSheet.enabled = config.show_data_table;
            configs.xAxis[0].showLabel = !configs.dataSheet.enabled;
            configs.zoom.zoomTool.visible = config.show_zoom;
            if (config.show_zoom === true) {
                delete configs.dataSheet;
                delete configs.zoom.zoomType;
            }
            BI.each(configs.yAxis, function (idx, axis) {
                switch (axis.axisIndex) {
                    case constants.LEFT_AXIS:
                        axis.reversed = config.left_y_axis_reversed;
                        axis.formatter = formatTickInXYaxis(config.left_y_axis_style, constants.LEFT_AXIS);
                        formatNumberLevelInYaxis(config.left_y_axis_number_level, idx);
                        axis.title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.LEFT_AXIS);
                        axis.title.text = config.show_left_y_axis_title === true ? config.left_y_axis_title + axis.title.text : axis.title.text;
                        axis.gridLineWidth = config.show_grid_line === true ? 1 : 0;
                        axis.title.rotation = constants.ROTATION;
                        break;
                    case constants.RIGHT_AXIS:
                        axis.reversed = config.right_y_axis_reversed;
                        axis.formatter = formatTickInXYaxis(config.right_y_axis_style, constants.RIGHT_AXIS);
                        formatNumberLevelInYaxis(config.right_y_axis_number_level, idx);
                        axis.title.text = getXYAxisUnit(config.right_y_axis_number_level, constants.RIGHT_AXIS);
                        axis.title.text = config.show_right_y_axis_title === true ? config.right_y_axis_title + axis.title.text : axis.title.text;
                        axis.gridLineWidth = config.show_grid_line === true ? 1 : 0;
                        axis.title.rotation = constants.ROTATION;
                        break;
                }
            });
            configs.xAxis[0].title.text = config.x_axis_title;
            configs.xAxis[0].labelRotation = config.text_direction;
            configs.xAxis[0].title.text = config.show_x_axis_title === true ? configs.xAxis[0].title.text : "";
            configs.xAxis[0].title.align = "center";
            configs.xAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;

            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatCordon() {
                BI.each(config.cordon, function (idx, cor) {
                    if (idx === 0 && xAxis.length > 0) {
                        var magnify = calcMagnify(config.x_axis_number_level);
                        xAxis[0].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "top"
                                }
                            });
                        });
                    }
                    if (idx > 0 && yAxis.length >= idx) {
                        var magnify = 1;
                        switch (idx - 1) {
                            case constants.LEFT_AXIS:
                                magnify = calcMagnify(config.left_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS:
                                magnify = calcMagnify(config.right_y_axis_number_level);
                                break;
                        }
                        yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "left"
                                }
                            });
                        });
                    }
                })
            }

            function formatNumberLevelInYaxis(type, position) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (position === item.yAxis) {
                                if (!BI.isNumber(da.y)) {
                                    da.y = BI.parseFloat(da.y);
                                }
                                da.y = da.y || 0;
                                da.y = da.y.div(magnify);
                                da.y = da.y.toFixed(constants.FIX_COUNT);
                                if (constants.MINLIMIT.sub(da.y) > 0) {
                                    da.y = 0;
                                }
                            }
                        })
                    })
                }
                if (type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    configs.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                if (position === constants.RIGHT_AXIS) {
                    config.right_y_axis_unit !== "" && (unit = unit + config.right_y_axis_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.LEFT_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                if (position === constants.RIGHT_AXIS) {
                    if (config.right_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '" + formatter + "') : (-1) * arguments[0];}"
            }
        }

        function formatConfigForAccumulateRadar(configs, items) {
            var radiusAxis = [{
                type: "value",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                formatter: "function(){if(this>0) return this; else return this*(-1); }",
                gridLineWidth: 0,
                position: "bottom"
            }];

            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            formatChartRadarStyle();
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }
            configs.plotOptions.dataLabels.enabled = config.show_data_label;

            configs.radiusAxis = radiusAxis;
            configs.radiusAxis[0].formatter = formatTickInXYaxis(config.left_y_axis_style, constants.LEFT_AXIS);
            formatNumberLevelInYaxis(config.left_y_axis_number_level, constants.LEFT_AXIS);
            configs.radiusAxis[0].title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.LEFT_AXIS);
            configs.radiusAxis[0].title.text = config.show_left_y_axis_title === true ? config.left_y_axis_title + configs.radiusAxis[0].title.text : configs.radiusAxis[0].title.text;
            configs.radiusAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.chartType = "radar";
            configs.plotOptions.columnType = true;
            delete configs.xAxis;
            delete configs.yAxis;
            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatChartRadarStyle() {
                switch (config.chart_radar_type) {
                    case BICst.CHART_SHAPE.POLYGON:
                        configs.plotOptions.shape = "polygon";
                        break;
                    case BICst.CHART_SHAPE.CIRCLE:
                        configs.plotOptions.shape = "circle";
                        break;
                }
            }

            function formatNumberLevelInYaxis(type, position) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (position === item.yAxis) {
                                if (!BI.isNumber(da.y)) {
                                    da.y = BI.parseFloat(da.y);
                                }
                                da.y = da.y || 0;
                                da.y = da.y.div(magnify);
                                da.y = da.y.toFixed(constants.FIX_COUNT);
                                if (constants.MINLIMIT.sub(da.y) > 0) {
                                    da.y = 0;
                                }
                            }
                        })
                    })
                }
                if (type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    configs.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                if (position === constants.RIGHT_AXIS) {
                    config.right_y_axis_unit !== "" && (unit = unit + config.right_y_axis_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.LEFT_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                if (position === constants.RIGHT_AXIS) {
                    if (config.right_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '" + formatter + "') : (-1) * arguments[0];}"
            }
        }

        function formatConfigForRadar(configs, items) {
            var radiusAxis = [{
                type: "value",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                formatter: "function(){if(this>0) return this; else return this*(-1); }",
                gridLineWidth: 0,
                position: "bottom"
            }];

            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            formatChartRadarStyle();
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }
            configs.plotOptions.dataLabels.enabled = config.show_data_label;

            configs.radiusAxis = radiusAxis;
            configs.radiusAxis[0].formatter = formatTickInXYaxis(config.left_y_axis_style, constants.LEFT_AXIS);
            formatNumberLevelInYaxis(config.left_y_axis_number_level, constants.LEFT_AXIS);
            configs.radiusAxis[0].title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.LEFT_AXIS);
            configs.radiusAxis[0].title.text = config.show_left_y_axis_title === true ? config.left_y_axis_title + configs.radiusAxis[0].title.text : configs.radiusAxis[0].title.text;
            configs.radiusAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.chartType = "radar";
            delete configs.xAxis;
            delete configs.yAxis;
            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatChartRadarStyle() {
                switch (config.chart_radar_type) {
                    case BICst.CHART_SHAPE.POLYGON:
                        configs.plotOptions.shape = "polygon";
                        break;
                    case BICst.CHART_SHAPE.CIRCLE:
                        configs.plotOptions.shape = "circle";
                        break;
                }
            }

            function formatNumberLevelInYaxis(type, position) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (position === item.yAxis) {
                                if (!BI.isNumber(da.y)) {
                                    da.y = BI.parseFloat(da.y);
                                }
                                da.y = da.y || 0;
                                da.y = da.y.div(magnify);
                                da.y = da.y.toFixed(constants.FIX_COUNT);
                                if (constants.MINLIMIT.sub(da.y) > 0) {
                                    da.y = 0;
                                }
                            }
                        })
                    })
                }
                if (type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    configs.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                if (position === constants.RIGHT_AXIS) {
                    config.right_y_axis_unit !== "" && (unit = unit + config.right_y_axis_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.LEFT_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                if (position === constants.RIGHT_AXIS) {
                    if (config.right_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '" + formatter + "') : (-1) * arguments[0];}"
            }
        }

        function formatConfigForAccumulateAxisArea(configs, items, ctype) {
            var xAxis = [{
                type: "category",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                }
            }];

            var yAxis = [];
            var types = [];
            BI.each(data, function (idx, axisItems) {
                var type = [];
                BI.each(axisItems, function (id, item) {
                    type.push(ctype);
                });
                types.push(type);
            });
            BI.each(types, function (idx, type) {
                if (BI.isEmptyArray(type)) {
                    return;
                }
                var newYAxis = {
                    type: "value",
                    title: {
                        style: {
                            "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                            "color": "#808080",
                            "fontSize": "12px",
                            "fontWeight": ""
                        }
                    },
                    labelStyle: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                    },
                    position: idx > 0 ? "right" : "left",
                    lineWidth: 1,
                    axisIndex: idx,
                    gridLineWidth: 0
                };
                yAxis.push(newYAxis);
            });

            configs.yAxis = yAxis;
            configs.xAxis = xAxis;

            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            formatChartLineStyle();
            formatCordon();
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }
            configs.plotOptions.dataLabels.enabled = config.show_data_label;
            configs.dataSheet.enabled = config.show_data_table;
            configs.xAxis[0].showLabel = !configs.dataSheet.enabled;
            configs.zoom.zoomTool.visible = config.show_zoom;
            if (config.show_zoom === true) {
                delete configs.dataSheet;
                delete configs.zoom.zoomType;
            }

            BI.each(configs.yAxis, function (idx, axis) {
                switch (axis.axisIndex) {
                    case constants.LEFT_AXIS:
                        axis.reversed = config.left_y_axis_reversed;
                        axis.formatter = formatTickInXYaxis(config.left_y_axis_style, constants.LEFT_AXIS);
                        formatNumberLevelInYaxis(config.left_y_axis_number_level, idx);
                        axis.title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.LEFT_AXIS);
                        axis.title.text = config.show_left_y_axis_title === true ? config.left_y_axis_title + axis.title.text : axis.title.text;
                        axis.gridLineWidth = config.show_grid_line === true ? 1 : 0;
                        axis.title.rotation = constants.ROTATION;
                        break;
                    case constants.RIGHT_AXIS:
                        axis.reversed = config.right_y_axis_reversed;
                        axis.formatter = formatTickInXYaxis(config.right_y_axis_style, constants.RIGHT_AXIS);
                        formatNumberLevelInYaxis(config.right_y_axis_number_level, idx);
                        axis.title.text = getXYAxisUnit(config.right_y_axis_number_level, constants.RIGHT_AXIS);
                        axis.title.text = config.show_right_y_axis_title === true ? config.right_y_axis_title + axis.title.text : axis.title.text;
                        axis.gridLineWidth = config.show_grid_line === true ? 1 : 0;
                        axis.title.rotation = constants.ROTATION;
                        break;
                }
            });

            configs.xAxis[0].title.text = config.x_axis_title;
            configs.xAxis[0].labelRotation = config.text_direction;
            configs.xAxis[0].title.text = config.show_x_axis_title === true ? configs.xAxis[0].title.text : "";
            configs.xAxis[0].title.align = "center";
            configs.xAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;
            configs.chartType = "area";
            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatChartLineStyle() {
                switch (config.chart_line_type) {
                    case BICst.CHART_SHAPE.RIGHT_ANGLE:
                        configs.plotOptions.curve = false;
                        configs.plotOptions.step = true;
                        break;
                    case BICst.CHART_SHAPE.CURVE:
                        configs.plotOptions.curve = true;
                        configs.plotOptions.step = false;
                        break;
                    case BICst.CHART_SHAPE.NORMAL:
                    default:
                        configs.plotOptions.curve = false;
                        configs.plotOptions.step = false;
                        break;
                }
            }

            function formatNumberLevelInYaxis(type, position) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (position === item.yAxis) {
                                if (!BI.isNumber(da.y)) {
                                    da.y = BI.parseFloat(da.y);
                                }
                                da.y = da.y || 0;
                                da.y = da.y.div(magnify);
                                da.y = da.y.toFixed(constants.FIX_COUNT);
                                if (constants.MINLIMIT.sub(da.y) > 0) {
                                    da.y = 0;
                                }
                            }
                        })
                    })
                }
                if (type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    configs.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                if (position === constants.RIGHT_AXIS) {
                    config.right_y_axis_unit !== "" && (unit = unit + config.right_y_axis_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.LEFT_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                if (position === constants.RIGHT_AXIS) {
                    if (config.right_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '" + formatter + "') : (-1) * arguments[0];}"
            }

            function formatCordon() {
                BI.each(config.cordon, function (idx, cor) {
                    if (idx === 0 && xAxis.length > 0) {
                        var magnify = calcMagnify(config.x_axis_number_level);
                        xAxis[0].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "top"
                                }
                            });
                        });
                    }
                    if (idx > 0 && yAxis.length >= idx) {
                        var magnify = 1;
                        switch (idx - 1) {
                            case constants.LEFT_AXIS:
                                magnify = calcMagnify(config.left_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS:
                                magnify = calcMagnify(config.right_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS_SECOND:
                                magnify = calcMagnify(config.right_y_axis_second_number_level);
                                break;
                        }
                        yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "left"
                                }
                            });
                        });
                    }
                })
            }
        }


        function formatConfigForAxis(configs, items) {

            var xAxis = [{
                type: "category",
                title: {
                    style: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                        "color": "#808080",
                        "fontSize": "12px",
                        "fontWeight": ""
                    }
                },
                labelStyle: {
                    "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                },
                position: "bottom",
                gridLineWidth: 0
            }];

            var yAxis = [];
            BI.each(types, function (idx, type) {
                if (BI.isEmptyArray(type)) {
                    return;
                }
                var newYAxis = {
                    type: "value",
                    title: {
                        style: {
                            "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                            "color": "#808080",
                            "fontSize": "12px",
                            "fontWeight": ""
                        }
                    },
                    labelStyle: {
                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                    },
                    position: idx > 0 ? "right" : "left",
                    lineWidth: 1,
                    axisIndex: idx,
                    gridLineWidth: 0
                };
                yAxis.push(newYAxis);
            });
            configs.yAxis = yAxis;
            configs.xAxis = xAxis;
            configs.colors = config.chart_color;
            configs.style = formatChartStyle();
            formatCordon();
            switch (config.chart_legend) {
                case BICst.CHART_LEGENDS.BOTTOM:
                    configs.legend.enabled = true;
                    configs.legend.position = "bottom";
                    configs.legend.maxHeight = constants.LEGEND_HEIGHT;
                    break;
                case BICst.CHART_LEGENDS.RIGHT:
                    configs.legend.enabled = true;
                    configs.legend.position = "right";
                    break;
                case BICst.CHART_LEGENDS.NOT_SHOW:
                default:
                    configs.legend.enabled = false;
                    break;
            }
            configs.plotOptions.dataLabels.enabled = config.show_data_label;
            configs.dataSheet.enabled = config.show_data_table;
            configs.xAxis[0].showLabel = !configs.dataSheet.enabled;
            configs.zoom.zoomTool.visible = config.show_zoom;
            if (config.show_zoom === true) {
                delete configs.dataSheet;
                delete configs.zoom.zoomType;
            }

            BI.each(configs.yAxis, function (idx, axis) {
                switch (axis.axisIndex) {
                    case constants.LEFT_AXIS:
                        axis.reversed = config.left_y_axis_reversed;
                        axis.formatter = formatTickInXYaxis(config.left_y_axis_style, constants.LEFT_AXIS);
                        formatNumberLevelInYaxis(config.left_y_axis_number_level, idx);
                        axis.title.text = getXYAxisUnit(config.left_y_axis_number_level, constants.LEFT_AXIS);
                        axis.title.text = config.show_left_y_axis_title === true ? config.left_y_axis_title + axis.title.text : axis.title.text;
                        axis.gridLineWidth = config.show_grid_line === true ? 1 : 0;
                        axis.title.rotation = constants.ROTATION;
                        break;
                    case constants.RIGHT_AXIS:
                        axis.reversed = config.right_y_axis_reversed;
                        axis.formatter = formatTickInXYaxis(config.right_y_axis_style, constants.RIGHT_AXIS);
                        formatNumberLevelInYaxis(config.right_y_axis_number_level, idx);
                        axis.title.text = getXYAxisUnit(config.right_y_axis_number_level, constants.RIGHT_AXIS);
                        axis.title.text = config.show_right_y_axis_title === true ? config.right_y_axis_title + axis.title.text : axis.title.text;
                        axis.gridLineWidth = config.show_grid_line === true ? 1 : 0;
                        axis.title.rotation = constants.ROTATION;
                        break;
                }
            });
            configs.xAxis[0].title.text = config.x_axis_title;
            configs.xAxis[0].labelRotation = config.text_direction;
            configs.xAxis[0].title.text = config.show_x_axis_title === true ? configs.xAxis[0].title.text : "";
            configs.xAxis[0].title.align = "center";
            configs.xAxis[0].gridLineWidth = config.show_grid_line === true ? 1 : 0;

            return BI.extend(configs, {
                series: items
            });

            function formatChartStyle() {
                switch (config.chart_style) {
                    case BICst.CHART_STYLE.STYLE_GRADUAL:
                        return "gradual";
                    case BICst.CHART_STYLE.STYLE_NORMAL:
                    default:
                        return "normal";
                }
            }

            function formatCordon() {
                BI.each(config.cordon, function (idx, cor) {
                    if (idx === 0 && xAxis.length > 0) {
                        var magnify = calcMagnify(config.x_axis_number_level);
                        xAxis[0].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "top"
                                }
                            });
                        });
                    }
                    if (idx > 0 && yAxis.length >= idx) {
                        var magnify = 1;
                        switch (idx - 1) {
                            case constants.LEFT_AXIS:
                                magnify = calcMagnify(config.left_y_axis_number_level);
                                break;
                            case constants.RIGHT_AXIS:
                                magnify = calcMagnify(config.right_y_axis_number_level);
                                break;
                        }
                        yAxis[idx - 1].plotLines = BI.map(cor, function (i, t) {
                            return BI.extend(t, {
                                value: t.value.div(magnify),
                                width: 1,
                                label: {
                                    "style": {
                                        "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                        "color": "#808080",
                                        "fontSize": "12px",
                                        "fontWeight": ""
                                    },
                                    "text": t.text,
                                    "align": "left"
                                }
                            });
                        });
                    }
                })
            }

            function formatNumberLevelInYaxis(type, position) {
                var magnify = calcMagnify(type);
                if (magnify > 1) {
                    BI.each(items, function (idx, item) {
                        BI.each(item.data, function (id, da) {
                            if (position === item.yAxis) {
                                if (!BI.isNumber(da.y)) {
                                    da.y = BI.parseFloat(da.y);
                                }
                                da.y = da.y || 0;
                                da.y = da.y.div(magnify);
                                da.y = da.y.toFixed(constants.FIX_COUNT);
                                if (constants.MINLIMIT.sub(da.y) > 0) {
                                    da.y = 0;
                                }
                            }
                        })
                    })
                }
                if (type === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                    configs.plotOptions.tooltip.formatter.valueFormat = "function(){return window.FR ? FR.contentFormat(arguments[0], '#0%') : arguments[0]}";
                }
            }

            function calcMagnify(type) {
                var magnify = 1;
                switch (type) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                    case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                        magnify = 1;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        magnify = 10000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        magnify = 1000000;
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        magnify = 100000000;
                        break;
                }
                return magnify;
            }

            function getXYAxisUnit(numberLevelType, position) {
                var unit = "";
                switch (numberLevelType) {
                    case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
                        unit = "";
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                        unit = BI.i18nText("BI-Wan");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                        unit = BI.i18nText("BI-Million");
                        break;
                    case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                        unit = BI.i18nText("BI-Yi");
                        break;
                }
                if (position === constants.X_AXIS) {
                    config.x_axis_unit !== "" && (unit = unit + config.x_axis_unit)
                }
                if (position === constants.LEFT_AXIS) {
                    config.left_y_axis_unit !== "" && (unit = unit + config.left_y_axis_unit)
                }
                if (position === constants.RIGHT_AXIS) {
                    config.right_y_axis_unit !== "" && (unit = unit + config.right_y_axis_unit)
                }
                return unit === "" ? unit : "(" + unit + ")";
            }

            function formatTickInXYaxis(type, position) {
                var formatter = '#.##';
                switch (type) {
                    case constants.NORMAL:
                        formatter = '#.##';
                        break;
                    case constants.ZERO2POINT:
                        formatter = '#0';
                        break;
                    case constants.ONE2POINT:
                        formatter = '#0.0';
                        break;
                    case constants.TWO2POINT:
                        formatter = '#0.00';
                        break;
                }
                if (position === constants.LEFT_AXIS) {
                    if (config.left_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                if (position === constants.RIGHT_AXIS) {
                    if (config.right_y_axis_number_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT) {
                        if (type === constants.NORMAL) {
                            formatter = '#0%'
                        } else {
                            formatter += '%';
                        }
                    }
                }
                return "function(){if(this>=0) return window.FR ? FR.contentFormat(arguments[0], '" + formatter + "') : arguments[0]; else return window.FR ? (-1) * FR.contentFormat(arguments[0], '" + formatter + "') : (-1) * arguments[0];}"
            }
        }

        function ChartConstants() {
            return {
                LEFT_AXIS: 0,
                RIGHT_AXIS: 1,
                X_AXIS: 3,
                ROTATION: -90,
                NORMAL: 1,
                LEGEND_BOTTOM: 4,
                ZERO2POINT: 2,
                ONE2POINT: 3,
                TWO2POINT: 4,
                STYLE_NORMAL: 21,
                MINLIMIT: 1e-5,
                LEGEND_HEIGHT: 80,
                RIGHT_AXIS_SECOND: 2,
                DASHBOARD_AXIS: 4,
                ONE_POINTER: 1,
                MULTI_POINTER: 2,
                HALF_DASHBOARD: 9,
                PERCENT_DASHBOARD: 10,
                PERCENT_SCALE_SLOT: 11,
                VERTICAL_TUBE: 12,
                HORIZONTAL_TUBE: 13,
                LNG_FIRST: 3,
                LAT_FIRST: 4,
                FIX_COUNT: 6,
                AUTO: 1,
                SHOW: 1
            }
        }

        function formatItems(items, types) {
            var result = [];
            var yAxisIndex = 0;
            BI.each(items, function (i, belongAxisItems) {
                var combineItems = combineChildItems(types[i], belongAxisItems);
                BI.each(combineItems, function (j, axisItems) {
                    if (BI.isArray(axisItems)) {
                        result = BI.concat(result, axisItems);
                    } else {
                        result.push(BI.extend(axisItems, {"yAxis": yAxisIndex}));
                    }
                });
                if (BI.isNotEmptyArray(combineItems)) {
                    yAxisIndex++;
                }
            });
            var config = combineConfig();
            config.plotOptions.click = function () {
                options.click(this.pointOption);
            };
            return [result, config];

            function combineChildItems(types, items) {
                var calItems = BI.values(items);
                return BI.map(calItems, function (idx, item) {
                    return formatChildItem(types[idx], item);
                });
            }

            function formatChildItem(type, items) {
                var item = {};
                switch (type) {
                    case BICst.WIDGET.BAR:
                    case BICst.WIDGET.ACCUMULATE_BAR:
                    case BICst.WIDGET.COMPARE_BAR:
                        item = BI.extend({"type": "bar"}, items);
                        break;
                    case BICst.WIDGET.BUBBLE:
                    case BICst.WIDGET.FORCE_BUBBLE:
                        item = BI.extend({"type": "bubble"}, items);
                        break;
                    case BICst.WIDGET.SCATTER:
                        item = BI.extend({"type": "scatter"}, items);
                        break;
                    case BICst.WIDGET.AXIS:
                    case BICst.WIDGET.ACCUMULATE_AXIS:
                    case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                    case BICst.WIDGET.COMPARE_AXIS:
                    case BICst.WIDGET.FALL_AXIS:
                        item = BI.extend({"type": "column"}, items);
                        break;
                    case BICst.WIDGET.LINE:
                        item = BI.extend({"type": "line"}, items);
                        break;
                    case BICst.WIDGET.AREA:
                    case BICst.WIDGET.ACCUMULATE_AREA:
                    case BICst.WIDGET.COMPARE_AREA:
                    case BICst.WIDGET.RANGE_AREA:
                    case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                        item = BI.extend({"type": "area"}, items);
                        break;
                    case BICst.WIDGET.DONUT:
                        item = BI.extend({"type": "pie"}, items);
                        break;
                    case BICst.WIDGET.RADAR:
                    case BICst.WIDGET.ACCUMULATE_RADAR:
                        item = BI.extend({"type": "radar"}, items);
                        break;
                    case BICst.WIDGET.PIE:
                        item = BI.extend({"type": "pie"}, items);
                        break;
                    case BICst.WIDGET.DASHBOARD:
                        item = BI.extend({"type": "gauge"}, items);
                        break;
                    case BICst.WIDGET.MAP:
                        item = BI.extend({"type": "areaMap"}, items);
                        break;
                    case BICst.WIDGET.GIS_MAP:
                        item = BI.extend({"type": "pointMap"}, items);
                        break;
                    default:
                        item = BI.extend({"type": "column"}, items);
                        break;
                }
                return item;
            }

            function combineConfig() {
                return {
                    "plotOptions": {
                        "rotatable": false,
                        "startAngle": 0,
                        "borderRadius": 0,
                        "endAngle": 360,
                        "innerRadius": "0.0%",

                        "layout": "horizontal",
                        "hinge": "rgb(101,107,109)",
                        "dataLabels": {
                            "style": "{fontFamily:Microsoft YaHei, color: #808080, fontSize: 12pt}",
                            "formatter": {
                                "identifier": "${VALUE}",
                                "valueFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                                "seriesFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                                "percentFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##%') : arguments[0]}",
                                "categoryFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                                "XFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                                "YFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                                "sizeFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                            },
                            "align": "outside",
                            "enabled": false
                        },
                        "percentageLabel": {
                            "formatter": {
                                "identifier": "${PERCENT}",
                                "valueFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                                "seriesFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                                "percentFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##%') : arguments[0]}",
                                "categoryFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}"
                            },
                            "style": {
                                "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                "color": "#808080",
                                "fontSize": "12px"
                            },
                            "align": "bottom",
                            "enabled": true
                        },
                        "valueLabel": {
                            "formatter": {
                                "identifier": "${SERIES}${VALUE}",
                                "valueFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                                "seriesFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                                "percentFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##%') : arguments[0]}",
                                "categoryFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}"
                            },
                            "backgroundColor": "rgb(255,255,0)",
                            "style": {
                                "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                "color": "#808080",
                                "fontSize": "12px"
                            },
                            "align": "inside",
                            "enabled": true
                        },
                        "hingeBackgroundColor": "rgb(220,242,249)",
                        "seriesLabel": {
                            "formatter": {
                                "identifier": "${CATEGORY}",
                                "valueFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                                "seriesFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                                "percentFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##%') : arguments[0]}",
                                "categoryFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}"
                            },
                            "style": {
                                "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                "color": "#808080",
                                "fontSize": "12px"
                            },
                            "align": "bottom",
                            "enabled": true
                        },
                        "style": "pointer",
                        "paneBackgroundColor": "rgb(252,252,252)",
                        "needle": "rgb(229,113,90)",


                        "large": false,
                        "connectNulls": false,
                        "shadow": true,
                        "curve": false,
                        "sizeBy": "area",
                        "tooltip": {
                            "formatter": {
                                "identifier": "${SERIES}${X}${Y}${SIZE}{CATEGORY}${SERIES}${VALUE}",
                                "valueFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0];}",
                                "seriesFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                                "percentFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##%') : arguments[0]}",
                                "categoryFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                                "XFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                                "sizeFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                                "YFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}"
                            },
                            "shared": false,
                            "padding": 5,
                            "backgroundColor": "rgba(0,0,0,0.4980392156862745)",
                            "borderColor": "rgb(0,0,0)",
                            "shadow": false,
                            "borderRadius": 2,
                            "borderWidth": 0,
                            "follow": false,
                            "enabled": true,
                            "animation": true,
                            style: {
                                "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3",
                                "color": "#c4c6c6",
                                "fontSize": "12px",
                                "fontWeight": ""
                            }
                        },
                        "maxSize": 70,
                        "fillColorOpacity": 0.5,
                        "marker": {"symbol": "circle", "radius": 4.5, "enabled": true},
                        "step": false,
                        "force": false,
                        "minSize": 15,
                        "displayNegative": true,
                        "categoryGap": "16.0%",
                        "borderColor": "rgb(255,255,255)",
                        "borderWidth": 1,
                        "gap": "22.0%",
                        "animation": true,
                        "lineWidth": 2,

                        bubble: {
                            "large": false,
                            "connectNulls": false,
                            "shadow": true,
                            "curve": false,
                            "sizeBy": "area",
                            "maxSize": 70,
                            "minSize": 15,
                            "lineWidth": 0,
                            "animation": true,
                            "fillColorOpacity": 0.699999988079071,
                            "marker": {
                                "symbol": "circle",
                                "radius": 28.39695010101295,
                                "enabled": true
                            }
                        }
                    },
                    dTools: {
                        enabled: 'true',
                        style: {
                            fontFamily: "Microsoft YaHei, Hiragino Sans GB W3",
                            color: "#1a1a1a",
                            fontSize: "12px"
                        },
                        backgroundColor: 'white'
                    },
                    dataSheet: {
                        enabled: false,
                        "borderColor": "rgb(0,0,0)",
                        "borderWidth": 1,
                        "formatter": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                        style: {
                            "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#808080", "fontSize": "12px"
                        }
                    },
                    "borderColor": "rgb(238,238,238)",
                    "shadow": false,
                    "legend": {
                        "borderColor": "rgb(204,204,204)",
                        "borderRadius": 0,
                        "shadow": false,
                        "borderWidth": 0,

                        "style": {
                            "fontFamily": "Microsoft YaHei, Hiragino Sans GB W3", "color": "#1a1a1a", "fontSize": "12px"
                        },
                        "position": "right",
                        "enabled": true
                    },
                    "rangeLegend": {
                        "range": {
                            "min": 0,
                            "color": [
                                [
                                    0,
                                    "rgb(182,226,255)"
                                ],
                                [
                                    0.5,
                                    "rgb(109,196,255)"
                                ],
                                [
                                    1,
                                    "rgb(36,167,255)"
                                ]
                            ],
                            "max": 266393
                        },
                        "enabled": false
                    },
                    "zoom": {"zoomType": "xy", "zoomTool": {"visible": false, "resize": true, "from": "", "to": ""}},
                    "plotBorderColor": "rgba(255,255,255,0)",
                    "tools": {
                        "hidden": true,
                        "toImage": {"enabled": true},
                        "sort": {"enabled": true},
                        "enabled": false,
                        "fullScreen": {"enabled": true}
                    },
                    "plotBorderWidth": 0,
                    "colors": ["rgb(99,178,238)", "rgb(118,218,145)"],
                    "borderRadius": 0,
                    "borderWidth": 0,
                    "style": "normal",
                    "plotShadow": false,
                    "plotBorderRadius": 0
                };
            }
        }
    }
};
