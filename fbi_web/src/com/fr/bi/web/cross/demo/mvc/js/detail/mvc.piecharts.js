PieChartsView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(PieChartsView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-adapt-editor bi-mvc-layout"
        })
    },

    _init: function(){
        PieChartsView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var data =  [[
            {"长期协议": [{"x":19485000,"y":29,"z":19485000}, {"x":24779420,"y":285,"z":24779420},{"x":11665800,"y":35,"z":11665800}]}, {"购买合同": [{"x":19485000,"y":45,"z":19485000}, {"x":24779420,"y":260,"z":24779420},{"x":11665800,"y":20,"z":11665800}]}
        ], [
            {"长期协议订单": [{"x":19485000,"y":29,"z":19485000}, {"x":24779420,"y":285,"z":24779420},{"x":11665800,"y":35,"z":11665800}]}, {"购买服务": [{"x":19485000,"y":45,"z":19485000}, {"x":24779420,"y":260,"z":24779420},{"x":11665800,"y":20,"z":11665800}]}
        ]];
        var data1 = [[
            {"长期协议": [{"x":19485000,"y":29,"z":19485000}, {"x":24779420,"y":285,"z":24779420},{"x":11665800,"y":35,"z":11665800}]}, {"购买合同": [{"x":19485000,"y":45,"z":19485000}, {"x":24779420,"y":260,"z":24779420},{"x":11665800,"y":20,"z":11665800}]}
        ]];
        var data2 = [[{"长期协议": [{"x":2000,"y":900,"z":19}, {"x":2001,"y":350,"z":24},{"x":2002,"y":400,"z":11}, {"x":2003,"y":-110,"z":11}]}]];
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
        c1.setTypes([[BICst.WIDGET.PIE]]);
        c1.populate(data1);
        c2.setTypes([[BICst.WIDGET.DONUT]]);
        c2.populate(data1);
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                type: "bi.label",
                height: 50,
                text: "柱形图，多轴多系列"
            }, c1, {
                type: "bi.label",
                height: 50,
                text: "柱形图，单轴多系列"
            }, c2, {
                type: "bi.label",
                height: 50,
                text: "堆积柱形图，多轴多系列"
            }]
        });
    }
});
PieChartsModel = BI.inherit(BI.Model, {

});