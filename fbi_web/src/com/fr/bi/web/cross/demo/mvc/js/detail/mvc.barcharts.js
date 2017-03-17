BarChartsView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BarChartsView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-adapt-editor bi-mvc-layout"
        })
    },

    _init: function () {
        BarChartsView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var items = [[{
            "data": [{
                "x": "江苏省",
                "y": 23,
                "value": "江苏省",
                "seriesName": "常州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "常州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "常州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "常州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "常州市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "常州市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 12,
                "value": "江苏省",
                "seriesName": "海门市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "海门市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "海门市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "海门市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "海门市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "海门市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "杭州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "杭州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "杭州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 56, "value": "浙江省", "seriesName": "杭州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "杭州市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "杭州市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "湖州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "湖州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "湖州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 15, "value": "浙江省", "seriesName": "湖州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "湖州市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "湖州市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 15,
                "value": "江苏省",
                "seriesName": "南通市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "南通市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "南通市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "南通市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "南通市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "南通市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "宁波市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "宁波市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "宁波市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 66, "value": "浙江省", "seriesName": "宁波市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "宁波市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "宁波市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "上海市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 17, "value": "上海市", "seriesName": "上海市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "上海市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "上海市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "上海市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "上海市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "绍兴市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "绍兴市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "绍兴市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 34, "value": "浙江省", "seriesName": "绍兴市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "绍兴市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "绍兴市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 85,
                "value": "江苏省",
                "seriesName": "苏州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "苏州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "苏州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "苏州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "苏州市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "苏州市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 35,
                "value": "江苏省",
                "seriesName": "太仓市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "太仓市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "太仓市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "太仓市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "太仓市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "太仓市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 14,
                "value": "江苏省",
                "seriesName": "泰州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "泰州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "泰州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "泰州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "泰州市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "泰州市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "天津市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "天津市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 15,
                "value": "天津市",
                "seriesName": "天津市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "天津市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "天津市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "天津市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 57,
                "value": "江苏省",
                "seriesName": "扬州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "扬州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "扬州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "扬州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "扬州市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "扬州市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "重庆市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "重庆市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "重庆市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "重庆市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 78,
                "value": "重庆市",
                "seriesName": "重庆市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "重庆市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "舟山市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "舟山市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "舟山市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 97, "value": "浙江省", "seriesName": "舟山市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "舟山市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "舟山市", "stack": "b8d4e892344ddf77"
        }]];
        var c1 = BI.createWidget({
            type: "bi.compare_axis_chart",
            width: 600,
            height: 300
        });
        c1.populate(items);
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [c1]
        });
    }
});
BarChartsModel = BI.inherit(BI.Model, {});