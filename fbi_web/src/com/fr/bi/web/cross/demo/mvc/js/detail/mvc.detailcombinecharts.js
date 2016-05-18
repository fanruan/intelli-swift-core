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
        //柱形图组合，多轴多系列
        var c1 = BI.createWidget({
            type: "bi.combine_chart",
            width: 600,
            height: 300
        });
        c1.populate(data);
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [c1]
        })
    }
});
DetailCombineChartModel = BI.inherit(BI.Model, {

});