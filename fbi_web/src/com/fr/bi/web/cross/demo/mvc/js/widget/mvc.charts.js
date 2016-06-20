ChartsView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ChartsView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-charts bi-mvc-layout",
            width: 960,
            height: 540
        })
    },

    _init: function () {
        ChartsView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var chart = BI.createWidget({
            type: 'bi.chart'
        });
        chart.on(BI.Chart.EVENT_CHANGE, function(){
            alert("wqwqw");
        });
        var data = [{
            "data": [
                {"x": "孙林", "y": 789},
                {"x": "金士鹏", "y": 156},
                {"x": "张珊", "y": 289},
                {"x": "孙阳", "y": 562},
                {"x": "袁成洁", "y": 546},
                {"x": "张颖", "y": 218},
                {"x": "王伟", "y": 541},
                {"x": "张武", "y": 219},
                {"x": "韩文", "y": 345}
            ],
            "name": "测试"
        }];

        BI.createWidget({
            type: "bi.grid",
            element: vessel,
            items: [[chart, {
                type: "bi.vertical",
                items: [{
                    type: "bi.button",
                    height: 25,
                    text: "populate",
                    handler: function () {
                        var d = BI.deepClone(data);
                        for (var i = 0; i < 9; i++) {
                            d[0].data[i].y = Math.floor(Math.random() * 1000);
                        }
                        chart.populate(d);
                    }
                }]
            }]]
        });
        chart.populate(data);
    }
});

ChartsModel = BI.inherit(BI.Model, {});