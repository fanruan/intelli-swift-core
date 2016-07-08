BI.ChartCombineFormatItemFactory = {
    combineItems: function(types, items){
        var calItems = BI.values(items);
        return BI.map(calItems, function(idx, item){
            return BI.ChartCombineFormatItemFactory.formatItems(types[idx], item);
        });
    },
    formatItems: function(type, items){
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
    },

    combineConfig: function(){
        return {
            "plotOptions": {
                "rotatable": false,
                "startAngle": 0,
                "borderRadius": 0,
                "endAngle": 360,
                "innerRadius": "0.0%",

                "layout": "horizontal",
                "hinge": "rgb(101,107,109)",
                "dataLabels":{
                    "style": "{fontFamily:Microsoft YaHei, color: #808080, fontSize: 12pt}",
                    "formatter": {
                        "identifier": "${VALUE}",
                        "valueFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                        "seriesFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                        "percentFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##%') : arguments[0]}",
                        "categoryFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                        "xFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                        "yFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
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
                        "fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px"
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
                        "fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px"
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
                        "fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px"
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
                        "xFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                        "sizeFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '#.##') : arguments[0]}",
                        "yFormat": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}"
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
                    style: {"fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#c4c6c6","fontSize":"12px","fontWeight":""}
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

                bubble:{
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
            dTools:{
                enabled:'true',
                style:{
                    fontFamily: "Microsoft YaHei, Hiragino Sans GB W3",
                    color: "#1a1a1a",
                    fontSize: "12px"
                },
                backgroundColor:'white'
            },
            dataSheet: {
                enabled: false,
                "borderColor": "rgb(0,0,0)",
                "borderWidth": 1,
                "formatter": "function(){return window.FR ? FR.contentFormat(arguments[0], '') : arguments[0]}",
                style:{
                    "fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#808080","fontSize":"12px"
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
                    "fontFamily":"Microsoft YaHei, Hiragino Sans GB W3","color":"#1a1a1a","fontSize":"12px"
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
    },

    /**
     * 获取数据并转化成图表所需数据
     * @param {String} wId   图表组件的id
     * @param {Number} type  图表类型
     * @param {Number} subType 图表子类型（针对地图）
     * @param {Boolean} isShowWidgetRealData 是否展示真实数据
     * @param {Function} getWidgetDataByID   由wId获取原始数据
     * @param {Function} getWidgetViewByID   获取view
     * @param {Function} getDimensionNameByID  获取指定指标的名称，用作系列名
     * @param {Function} getDimensionStyleOfChartByID  获取指定指标所在系列的图类型（针对组合图和多值轴组合图）
     * @param {Function} isDimensionUsable   当前维度指标是否被使用
     * @param callback  回调
     */
    getWidgetData: function(wId, type, subType, isShowWidgetRealData, getWidgetDataByID, getWidgetViewByID, getDimensionNameByID, getDimensionStyleOfChartByID, isDimensionUsable, callback){
        var options = {};
        var realData = isShowWidgetRealData || false;
        getWidgetDataByID(wId, function (jsonData) {
            if(BI.isNotNull(jsonData.error)) {
                callback(jsonData);
                return;
            }
            var targetIds = _getShowTarget(wId, getWidgetViewByID, isDimensionUsable);
            var data = parseChartData(wId, type, targetIds, getWidgetViewByID, getDimensionNameByID, isDimensionUsable, jsonData.data);
            var types = [];
            var count = 0;
            BI.each(data, function (idx, da) {
                var t = [];
                BI.each(da, function (id, d) {
                    if (type === BICst.WIDGET.MULTI_AXIS_COMBINE_CHART || type === BICst.WIDGET.COMBINE_CHART) {
                        var chart = getDimensionStyleOfChartByID(targetIds[count] || targetIds[0]) || {};
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
                    (type[id] === BICst.WIDGET.ACCUMULATE_AREA || type[id] === BICst.WIDGET.ACCUMULATE_AXIS) && BI.extend(it, {stack: i});
                });
            });
            if(type === BICst.WIDGET.MAP){
                options.geo = {
                    data: BICst.MAP_PATH[subType] || BICst.MAP_PATH[BICst.MAP_TYPE.CHINA],
                    geoName: BICst.MAP_TYPE_NAME[subType] || BICst.MAP_TYPE_NAME[BICst.MAP_TYPE.CHINA]
                }
            }
            if(type === BICst.WIDGET.GIS_MAP){
                options.geo = {
                    "tileLayer": "http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}"
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
            page: -1,
            real_data: realData
        });


        function _getShowTarget(wId, getWidgetViewByID, isDimensionUsable) {
            var view = getWidgetViewByID(wId);
            var targetIds = [];
            BI.each(view, function (regionType, arr) {
                if (regionType >= BICst.REGION.TARGET1) {
                    targetIds = BI.concat(targetIds, arr);
                }
            });
            return BI.filter(targetIds, function (idx, tId) {
                return isDimensionUsable(tId);
            });

        }

        function _formatDataForMap(wId, data, targetIds, currentLayer, getWidgetViewByID, getDimensionNameByID) {
            var result = [];
            currentLayer++;
            if (BI.has(data, "c")) {
                var obj = (data.c)[0];
                var view = getWidgetViewByID(wId);
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
                                        x: item.n,
                                        y: (BI.isFinite(item.s[idx]) ? item.s[idx] : 0),
                                        targetIds: [targetIds[idx]]
                                    };
                            }
                        }else{
                            res = {
                                x: item.n,
                                y: (BI.isFinite(item.s[idx]) ? item.s[idx] : 0),
                                targetIds: [targetIds[idx]]
                            };
                        }
                        if(BI.has(item, "c")){
                            res.drilldown = {};
                            res.drilldown.series = _formatDataForMap(wId, item, currentLayer, targetIds, getWidgetViewByID, getDimensionNameByID);
                            res.drilldown.geo = {
                                data: BICst.MAP_PATH[BICst.MAP_NAME[res.x]],
                                geoName: res.x
                            };
                        }
                        return res;
                    });
                    var obj = {};
                    obj.data = adjustData;
                    BI.isNotNull(type) && (obj.type = "bubble");
                    obj.name = getDimensionNameByID(targetIds[idx]);
                    return obj;
                });
            }
            return result;
        }

        function _formatDataForGISMap(data, targetIds, getDimensionNameByID){
            if (BI.has(data, "t")) {
                var top = data.t, left = data.l;
                var init = BI.map(top.c, function (id, tObj) {
                    var data = [];
                    BI.each(left.c, function (idx, obj) {
                        var x = obj.n;
                        BI.each(obj.s.c[id].s, function(i, o){
                            if(BI.isNotNull(o) && BI.isNotNull(x)){
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
                if(init.length > 0){
                    size = targetIds.length;
                }
                BI.each(BI.makeArray(size, null), function(idx, index){
                    var res = {data: [], name: getDimensionNameByID(targetIds[idx])};
                    BI.each(init, function(id, obj){
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
                    obj.name = getDimensionNameByID(targetIds[idx]);
                    return obj;
                });
            }
            return [];
        }

        function _formatDataForAxis(wId, type, targetIds, getWidgetViewByID, getDimensionNameByID, isDimensionUsable, da) {
            var data = _formatDataForCommon(type, targetIds, getDimensionNameByID, da);
            if (BI.isEmptyArray(data)) {
                return [];
            }
            var view = getWidgetViewByID(wId);
            var array = [];
            BI.each(targetIds, function (idx, tId) {
                if (BI.has(view, BICst.REGION.TARGET1) && BI.contains(view[BICst.REGION.TARGET1], tId)) {
                    array.length === 0 && array.push([]);
                    if(checkSeriesExist()){
                        array[0] = data;
                    }else{
                        array[0].push(data[idx])
                    }
                }
                if(BI.has(view, BICst.REGION.TARGET2) && BI.contains(view[BICst.REGION.TARGET2], tId)) {
                    while (array.length < 2){array.push([]);}
                    if(checkSeriesExist()){
                        array[1] = data;
                    }else{
                        array[1].push(data[idx])
                    }
                }
                if(BI.has(view, BICst.REGION.TARGET3) && BI.contains(view[BICst.REGION.TARGET3], tId)){
                    while (array.length < 3){array.push([]);}
                    if(checkSeriesExist()){
                        array[2] = data;
                    }else{
                        array[2].push(data[idx])
                    }
                }
            });
            return array;

            function checkSeriesExist(){
                var view = getWidgetViewByID(wId);
                var result = BI.find(view[BICst.REGION.DIMENSION2], function (idx, dId) {
                    return isDimensionUsable(dId);
                });
                return BI.isNotNull(result);
            }
        }

        function _formatDataForBubble(wId, targetIds, getWidgetViewByID, data) {
            var view = getWidgetViewByID(wId);
            var result = BI.find(view, function (region, arr) {
                return BI.isEmptyArray(arr);
            });
            if (BI.isNotNull(result) || BI.size(view) < 4) {
                return [];
            }
            return [BI.map(data.c, function (idx, item) {
                var obj = {};
                var name = item.n, seriesName = item.n;
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

        function _formatDataForScatter(wId, targetIds, getWidgetViewByID, data) {
            var view = getWidgetViewByID(wId);
            var result = BI.find(view, function (region, arr) {
                return BI.isEmptyArray(arr);
            });
            if (BI.isNotNull(result) || BI.size(view) < 3) {
                return [];
            }
            return [BI.map(data.c, function (idx, item) {
                var obj = {};
                var seriesName = item.n;
                obj.name = item.n;
                obj.data = [{
                    x: (BI.isFinite(item.s[1]) ? item.s[1] : 0),
                    y: (BI.isFinite(item.s[0]) ? item.s[0] : 0),
                    seriesName: seriesName,
                    targetIds: [targetIds[0], targetIds[1]]
                }];
                return obj;
            })];
        }

        function _formatDataForCommon(type, targetIds, getDimensionNameByID, data) {
            if (BI.has(data, "t")) {
                var top = data.t, left = data.l;
                return BI.map(top.c, function (id, tObj) {
                    var name = tObj.n, seriesName = tObj.n;
                    var data = BI.map(left.c, function (idx, obj) {
                        var x = obj.n;
                        return {
                            "x": x,
                            "y": (BI.isFinite(obj.s.c[id].s[0]) ? obj.s.c[id].s[0] : 0),
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
                        var x = item.n;
                        return {
                            x: x,
                            y: (BI.isFinite(item.s[idx]) ? item.s[idx] : 0),
                            seriesName: getDimensionNameByID(targetIds[idx]),
                            targetIds: [targetIds[idx]]
                        };
                    });
                    var obj = {};
                    obj.data = adjustData;
                    obj.name = getDimensionNameByID(targetIds[idx]);
                    return obj;
                });
            }
            if(BI.has(data, "s")){
                if(type === BICst.WIDGET.PIE){
                    var adjustData = BI.map(data.s, function (idx, value) {
                        return {
                            x: getDimensionNameByID(targetIds[idx]),
                            y: (BI.isFinite(value) ? value : 0),
                            targetIds: [targetIds[idx]]
                        };
                    });
                    var obj = {};
                    obj.data = adjustData;
                    return [obj];
                }else{
                    return BI.map(data.s, function (idx, value) {
                        return {
                            name: getDimensionNameByID(targetIds[idx]),
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

        function _formatDataForDashBoard(getDimensionNameByID, targetIds, data) {
            if (BI.has(data, "c")) {
                var adjustData = BI.map(data.c, function (id, item) {
                    var seriesName = item.n;
                    var data = [{
                        x: getDimensionNameByID(targetIds[0]),
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
                        x: getDimensionNameByID(targetIds[idx]),
                        y: (BI.isFinite(value) ? value : 0),
                        targetIds: [targetIds[idx]]
                    };
                });
                return BI.isEmptyArray(obj.data) ? [] : [[obj]];
            }
            return [];
        }

        function parseChartData(wId, type, targetIds, getWidgetViewByID, getDimensionNameByID, isDimensionUsable, data) {
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
                    return _formatDataForAxis(wId, type, targetIds, getWidgetViewByID, getDimensionNameByID, isDimensionUsable, data);
                case BICst.WIDGET.DASHBOARD:
                    return _formatDataForDashBoard(getDimensionNameByID, targetIds, data);
                case BICst.WIDGET.BUBBLE:
                    return _formatDataForBubble(wId, targetIds, getWidgetViewByID, data);
                case BICst.WIDGET.SCATTER:
                    return _formatDataForScatter(wId, targetIds, getWidgetViewByID, data);
                case BICst.WIDGET.MAP:
                    var da = _formatDataForMap(wId, data, targetIds, 0, getWidgetViewByID, getDimensionNameByID);
                    return BI.isEmptyArray(da) ? da : [da];
                case BICst.WIDGET.GIS_MAP:
                    var da = _formatDataForGISMap(data, targetIds, getDimensionNameByID);
                    return BI.isEmptyArray(da) ? da : [da];
            }
        }
    }
};