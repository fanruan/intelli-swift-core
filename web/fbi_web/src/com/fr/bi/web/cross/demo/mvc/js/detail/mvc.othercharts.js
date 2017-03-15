OtherChartsView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(OtherChartsView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-adapt-editor bi-mvc-layout"
        })
    },

    _init: function(){
        OtherChartsView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var items2 = [[{
            "data": [{"size": 80, "x": "69", "y": 50}, {"size": 16, "x": "97", "y": 24}, {
                "size": 65,
                "x": "45",
                "y": 19
            }, {"size": 64, "x": "-84", "y": -79}, {"size": 12, "x": "90", "y": 5}, {
                "size": 99,
                "x": "-63",
                "y": 32
            }, {"size": 36, "x": "62", "y": 49}, {"size": 31, "x": "16", "y": 47}, {
                "size": 94,
                "x": "9",
                "y": 92
            }, {"size": 10, "x": "-62", "y": 48}, {"size": 41, "x": "3", "y": 55}, {
                "size": 94,
                "x": "-47",
                "y": 66
            }, {"size": 63, "x": "-98", "y": -23}, {"size": 69, "x": "-29", "y": -69}, {
                "size": 85,
                "x": "-22",
                "y": 89
            }, {"size": 19, "x": "20", "y": -80}, {"size": 21, "x": "58", "y": 44}, {
                "size": 68,
                "x": "88",
                "y": -46
            }, {"size": 91, "x": "71", "y": 38}, {"size": 22, "x": "32", "y": -66}, {
                "size": 10,
                "x": "-57",
                "y": -88
            }, {"size": 62, "x": "39", "y": 85}, {"size": 42, "x": "-55", "y": 49}, {
                "size": 21,
                "x": "-40",
                "y": -51
            }, {"size": "-", "x": "", "y": "-"}, {"size": "-", "x": "", "y": "-"}], "name": "type1"
        }, {
            "data": [{"size": 98, "x": "-67", "y": -16}, {"size": 11, "x": "-9", "y": 92}, {
                "size": 63,
                "x": "-13",
                "y": -29
            }, {"size": 90, "x": "91", "y": -89}, {"size": 38, "x": "-8", "y": 109}, {
                "size": 77,
                "x": "-79",
                "y": -15
            }, {"size": 66, "x": "81", "y": -18}, {"size": 100, "x": "-68", "y": -41}, {
                "size": 82,
                "x": "-54",
                "y": 56
            }, {"size": 75, "x": "21", "y": -19}, {"size": 21, "x": "38", "y": -9}, {
                "size": 59,
                "x": "-24",
                "y": 85
            }, {"size": 18, "x": "33", "y": -88}, {"size": 72, "x": "-13", "y": -39}, {
                "size": 97,
                "x": "11",
                "y": -23
            }, {"size": 40, "x": "48", "y": -53}, {"size": 45, "x": "25", "y": 36}, {
                "size": 84,
                "x": "-36",
                "y": 16
            }, {"size": 48, "x": "36", "y": -74}, {"size": 51, "x": "-87", "y": 10}, {
                "size": 31,
                "x": "4",
                "y": -32
            }, {"size": 21, "x": "12", "y": -55}, {"size": 99, "x": "-25", "y": -18}, {
                "size": 65,
                "x": "13",
                "y": 5
            }, {"size": 60, "x": "-81", "y": 105}, {"size": 103, "x": "-22", "y": -11}], "name": "type2"
        }]];
        var items1 = [[{
            data: [
                {"x": 19485000, "y": 29, "z": 19485000},
                {"x": 24779420, "y": 285, "z": 24779420},
                {"x": 11665800, "y": 35, "z": 11665800},
                {"x": 22665800, "y": 45, "z": 14665800}],
            name: "长期协议"
        }]];
        var c1 = BI.createWidget({
            type: "bi.combine_chart",
            width: 600,
            height: 300
        });
        var c2 = BI.createWidget({
            type: "bi.combine_chart",
            width: 600,
            height: 300
        });
        var c3 = BI.createWidget({
            type: "bi.combine_chart",
            width: 600,
            height: 300
        });
        var c4 = BI.createWidget({
            type: "bi.combine_chart",
            width: 600,
            height: 300
        });
        var c5 = BI.createWidget({
            type: "bi.combine_chart",
            width: 600,
            height: 300
        });
        var c6 = BI.createWidget({
            type: "bi.combine_chart",
            width: 600,
            height: 300
        });
        //柱形图组合，多轴多系列
        //c1.setTypes([[BICst.WIDGET.PIE]]);
        //c1.populate(items1);
        //柱形图组合，单轴轴多系列
        //c2.setTypes([[BICst.WIDGET.DONUT]]);
        //c2.populate(items1);
        //堆积柱状图，多轴多系列
        c3.setTypes([[BICst.WIDGET.BUBBLE,BICst.WIDGET.BUBBLE,BICst.WIDGET.BUBBLE,BICst.WIDGET.BUBBLE]]);
        c3.populate(items2);
        //百分比堆积柱状，单轴多系列
        c4.setTypes([[BICst.WIDGET.SCATTER,BICst.WIDGET.SCATTER,BICst.WIDGET.SCATTER,BICst.WIDGET.SCATTER]]);
        c4.populate(items2);
        //对比柱形图，多轴多系列
        c5.setTypes([[BICst.WIDGET.RADAR]]);
        c5.populate(items1);
        //瀑布图
        c6.setTypes([[BICst.WIDGET.DASHBOARD]]);
        c6.populate(items1);
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [c3,c3,c5, c6]
        });
    }
});
OtherChartsModel = BI.inherit(BI.Model, {

});