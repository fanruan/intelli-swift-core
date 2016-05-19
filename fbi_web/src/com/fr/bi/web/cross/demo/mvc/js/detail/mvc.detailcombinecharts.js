DetailCombineChartView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(DetailCombineChartView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-adapt-editor bi-mvc-layout"
        })
    },

    _init: function(){
        DetailCombineChartView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        //var data = {"s":[277876430,277876430],"c":[{"s":[19485000,19485000],"x":1,"n":"长期协议"},{"s":[24779420,24779420],"x":1,"n":"长期协议订单"},{"s":[11665800,11665800],"x":1,"n":"服务协议"},{"s":[221946210,221946210],"x":1,"n":"购买合同"}],"x":5};
        //var data1 = {"s":[966,277876430,277876430],"c":[{"s":[29,19485000,19485000],"x":1,"n":"长期协议"},{"s":[285,24779420,24779420],"x":1,"n":"长期协议订单"},{"s":[35,11665800,11665800],"x":1,"n":"服务协议"},{"s":[617,221946210,221946210],"x":1,"n":"购买合同"}],"x":5};
        var data =  [[
            {"长期协议": [{"x":19485000,"y":29,"z":19485000}, {"x":24779420,"y":285,"z":24779420},{"x":11665800,"y":35,"z":11665800}]}, {"购买合同": [{"x":19485000,"y":45,"z":19485000}, {"x":24779420,"y":260,"z":24779420},{"x":11665800,"y":20,"z":11665800}]}
        ], [
            {"长期协议订单": [{"x":19485000,"y":29,"z":19485000}, {"x":24779420,"y":285,"z":24779420},{"x":11665800,"y":35,"z":11665800}]}, {"购买服务": [{"x":19485000,"y":45,"z":19485000}, {"x":24779420,"y":260,"z":24779420},{"x":11665800,"y":20,"z":11665800}]}
        ]];
        var data1 = [[
            {"长期协议": [{"x":19485000,"y":29,"z":19485000}, {"x":24779420,"y":285,"z":24779420},{"x":11665800,"y":35,"z":11665800}]}, {"购买合同": [{"x":19485000,"y":45,"z":19485000}, {"x":24779420,"y":260,"z":24779420},{"x":11665800,"y":20,"z":11665800}]}
        ]];
        var data2 = [[{"长期协议": [{"x":2000,"y":900,"z":19485000}, {"x":2001,"y":350,"z":24779420},{"x":2002,"y":400,"z":11665800}, {"x":2003,"y":-110,"z":11665800}]}]];
        var c1 = BI.createWidget({
            type: "bi.combine_chart",
            width: 600,
            height: 300
        });
        //柱形图组合，多轴多系列
        //c1.setTypes([[BICst.WIDGET.AXIS, BICst.WIDGET.AXIS, BICst.WIDGET.AXIS],[BICst.WIDGET.AXIS, BICst.WIDGET.AXIS, BICst.WIDGET.AXIS]]);
        //c1.populate(data);
        //柱形图组合，单轴轴多系列
        //c1.setTypes([[BICst.WIDGET.BAR, BICst.WIDGET.BAR, BICst.WIDGET.BAR]]);
        //c1.populate(data1);
        //堆积柱状图，多轴多系列
        //c1.setTypes([[BICst.WIDGET.ACCUMULATE_BAR, BICst.WIDGET.ACCUMULATE_BAR, BICst.WIDGET.ACCUMULATE_BAR],[BICst.WIDGET.ACCUMULATE_BAR, BICst.WIDGET.ACCUMULATE_BAR, BICst.WIDGET.ACCUMULATE_BAR]]);
        //c1.populate(data);
        //百分比堆积柱状，单轴多系列
        //c1.setTypes([[BICst.WIDGET.PERCENT_ACCUMULATE_BAR, BICst.WIDGET.PERCENT_ACCUMULATE_BAR, BICst.WIDGET.PERCENT_ACCUMULATE_BAR]]);
        //c1.populate(data1);
        //对比柱状图，多系列
        //var compareBAR = BI.createWidget({
        //    type: "bi.compare_BAR_chart",
        //    width: 600,
        //    height: 300
        //});
        //compareBAR.populate(data);
        //瀑布图
        var fall = BI.createWidget({
            type: "bi.fall_axis_chart",
            width: 600,
            height: 300
        });
        fall.populate(data2);
        //条形图 单轴多系列
        //c1.setTypes([[BICst.WIDGET.BAR, BICst.WIDGET.BAR, BICst.WIDGET.BAR]]);
        //c1.populate(data1);
        //对比条形图
        //var bar = BI.createWidget({
        //    type: "bi.compare_bar_chart",
        //    width: 600,
        //    height: 300
        //});
        //bar.populate(data);
        //面积图
        //c1.setTypes([[BICst.WIDGET.AREA, BICst.WIDGET.AREA, BICst.WIDGET.AREA],[BICst.WIDGET.AREA, BICst.WIDGET.AREA, BICst.WIDGET.AREA]]);
        //c1.populate(data);
        //范围面积图
        //var area = BI.createWidget({
        //    type: "bi.range_area_chart",
        //    width: 600,
        //    height: 300
        //});
        //area.populate(data2);
        //气泡图
        //c1.setTypes([[BICst.WIDGET.BUBBLE, BICst.WIDGET.BUBBLE, BICst.WIDGET.BUBBLE]]);
        //c1.populate(data1);
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [fall]
        });
    }
});
DetailCombineChartModel = BI.inherit(BI.Model, {

});