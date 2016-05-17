DetailChartView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(DetailChartView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-adapt-editor bi-mvc-layout"
        })
    },

    _init: function(){
        DetailChartView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        //var data = {"s":[277876430,277876430],"c":[{"s":[19485000,19485000],"x":1,"n":"长期协议"},{"s":[24779420,24779420],"x":1,"n":"长期协议订单"},{"s":[11665800,11665800],"x":1,"n":"服务协议"},{"s":[221946210,221946210],"x":1,"n":"购买合同"}],"x":5};
        //var data1 = {"s":[966,277876430,277876430],"c":[{"s":[29,19485000,19485000],"x":1,"n":"长期协议"},{"s":[285,24779420,24779420],"x":1,"n":"长期协议订单"},{"s":[35,11665800,11665800],"x":1,"n":"服务协议"},{"s":[617,221946210,221946210],"x":1,"n":"购买合同"}],"x":5};
        var data = [{
            "data": [{"x": "长期协议", "y": 19485000},
                {"x": "长期协议订单", "y": 24779420},
                {"x": "服务协议", "y": 11665800},
                {"x": "购买合同", "y": 221946210}],
            name: "系列1"
        }, {
            "data": [{"x": "长期协议", "y": 19485000},
                {"x": "长期协议订单", "y": 24779420},
                {"x": "服务协议", "y": 11665800},
                {"x": "购买合同","y": 221946210}],
            name: "系列2"}
        ];
        var data1 = [{"data":[{"x":19485000,"y":29,"size":19485000}],"name":"长期协议"},{"data":[{"x":24779420,"y":285,"size":24779420}],"name":"长期协议订单"},{"data":[{"x":11665800,"y":35,"size":11665800}],"name":"服务协议"},{"data":[{"x":221946210,"y":617,"size":221946210}],"name":"购买合同"}];
        var data2 = [{
            "data": [{"y": "长期协议", "x": 19485000},
                {"y": "长期协议订单", "x": 24779420},
                {"y": "服务协议", "x": 11665800},
                {"y": "购买合同", "x": 221946210}],
            name: "系列1"
        }, {
            "data": [{"y": "长期协议", "x": 19485000},
                {"y": "长期协议订单", "x": 24779420},
                {"y": "服务协议", "x": 11665800},
                {"y": "购买合同","x": 221946210}],
            name: "系列2"}
        ];
        var line = BI.createWidget({
            type: "bi.line_chart",
            width: 600,
            height: 300
        });
        var axis = BI.createWidget({
            type: "bi.axis_chart",
            width: 600,
            height: 300
        });
        var accumulateAxis = BI.createWidget({
            type: "bi.accumulate_axis_chart",
            width: 600,
            height: 300
        });
        var peraccumulateAxis = BI.createWidget({
            type: "bi.percent_accumulate_axis_chart",
            width: 600,
            height: 300
        });
        var bar = BI.createWidget({
            type: "bi.bar_chart",
            width: 600,
            height: 300
        });
        var accumulateBar = BI.createWidget({
            type: "bi.accumulate_bar_chart",
            width: 600,
            height: 300
        });
        var area = BI.createWidget({
            type: "bi.area_chart",
            width: 600,
            height: 300
        });
        var accumulateArea = BI.createWidget({
            type: "bi.accumulate_area_chart",
            width: 600,
            height: 300
        });
        var peraccumulateArea = BI.createWidget({
            type: "bi.percent_accumulate_area_chart",
            width: 600,
            height: 300
        });
        var bubble = BI.createWidget({
            type: "bi.bubble_chart",
            width: 600,
            height: 300
        });
        var forceBubble = BI.createWidget({
            type: "bi.force_bubble_chart",
            width: 600,
            height: 300
        });
        var scatter = BI.createWidget({
            type: "bi.scatter_chart",
            width: 600,
            height: 300
        });
        var radar = BI.createWidget({
            type: "bi.radar_chart",
            width: 600,
            height: 300
        });
        var accumulateradar = BI.createWidget({
            type: "bi.accumulate_radar_chart",
            width: 600,
            height: 300
        });
        line.populate(data);
        axis.populate(data);
        accumulateAxis.populate(data);
        peraccumulateAxis.populate(data);
        bar.populate(data2);
        accumulateBar.populate(data2);
        area.populate(data);
        accumulateArea.populate(data);
        peraccumulateArea.populate(data);
        bubble.populate(data1);
        forceBubble.populate(data1);
        scatter.populate(data1);
        radar.populate(data);
        accumulateradar.populate(data);
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [line, axis,accumulateAxis,peraccumulateAxis,bar,accumulateBar,
            area, accumulateArea, peraccumulateArea, bubble, forceBubble, radar,
            scatter, radar, accumulateradar]
        })
    }
});
DetailChartModel = BI.inherit(BI.Model, {

});