ColumnChartsView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(ColumnChartsView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-adapt-editor bi-mvc-layout"
        })
    },

    _init: function(){
        ColumnChartsView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var items =  [[
            {
                data: [
                    {"x": 19485000, "y": 29, "z": 19485000},
                    {"x": 24779420, "y": 285, "z": 24779420},
                    {"x": 11665800, "y": 35, "z": 11665800}],
                name: "长期协议"
            }, {
                data: [
                    {"x": 19485000, "y": 45, "z": 19485000},
                    {"x": 24779420, "y": 260, "z": 24779420},
                    {"x": 11665800, "y": 20, "z": 11665800}],
                name: "购买合同"
            }], [{
                data: [
                    {"x":19485000,"y":29,"z":19485000},
                    {"x":24779420,"y":285,"z":24779420},
                    {"x":11665800,"y":35,"z":11665800}],
                name: "长期协议订单"
            },  {
                data: [
                    {"x":19485000,"y":45,"z":19485000},
                    {"x":24779420,"y":260,"z":24779420},
                    {"x":11665800,"y":20,"z":11665800}],
                name: "购买服务"
        }]];
        var stackedItems = BI.map(items, function(idx, item){
            var i = BI.UUID();
            return BI.map(item, function(id, it){
                return BI.extend({}, it, {stack: i});
            });
        });
        var compareitems = BI.map(items, function(idx, item){
            return BI.map(item, function(id, it){
                if(idx > 0){
                    return BI.extend({}, it, {reversed: true});
                }else{
                    return BI.extend({}, it, {reversed: false});
                }
            });
        });
        var items1 =  [[
            {
                data: [
                    {"x": 19485000, "y": 29, "z": 19485000},
                    {"x": 24779420, "y": 285, "z": 24779420},
                    {"x": 11665800, "y": 35, "z": 11665800}],
                name: "长期协议"
            }, {
                data: [
                    {"x": 19485000, "y": 45, "z": 19485000},
                    {"x": 24779420, "y": 260, "z": 24779420},
                    {"x": 11665800, "y": 20, "z": 11665800}],
                name: "购买合同"
            }]];
        var stackedItems1 = BI.map(items1, function(idx, item){
            var i = BI.UUID();
            return BI.map(item, function(id, it){
                return BI.extend({}, it, {stack: i});
            });
        });
        var stackByPercentItems1 = BI.map(items1, function(idx, item){
            var i = BI.UUID();
            return BI.map(item, function(id, it){
                return BI.extend({}, it, {stack: i, stackByPercent: true});
            });
        });
        //"长期协议": [{"x":2000,"y":900,"z":19}, {"x":2001,"y":350,"z":24},{"x":2002,"y":400,"z":11}, {"x":2003,"y":-110,"z":11}]}]];
        var items3 = [[
            {
                data: [
                    {"x":2000,"y":0},
                    {"x":2001,"y":900},
                    {"x":2002,"y":1250},
                    {"x":2003,"y":1540}],
                name: "",
                stack: "xolumn",
                color: "rgba(0,0,0,0)",
                borderColor: "rgba(0,0,0,0)",
                borderWidth: 0,
                clickColor: "rgba(0,0,0,0)",
                mouseOverColor: "rgba(0,0,0,0)",
                tooltip: {
                    enable: false
                }
            }, {
                data: [{"x":2000,"y":900},
                    {"x":2001,"y":350},
                    {"x":2002,"y":400},
                    {"x":2003,"y":110, color: "rgb(152, 118, 170)"}],
                name: "",
                stack: "xolumn",
                color: "rgb(0, 157, 227)"
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
        c1.setTypes([[BICst.WIDGET.AXIS, BICst.WIDGET.AXIS, BICst.WIDGET.AXIS],[BICst.WIDGET.AXIS, BICst.WIDGET.AXIS, BICst.WIDGET.AXIS]]);
        c1.populate(items);
        //柱形图组合，单轴轴多系列
        c2.setTypes([[BICst.WIDGET.AXIS, BICst.WIDGET.AXIS, BICst.WIDGET.AXIS]]);
        c2.populate(items1);
        //堆积柱状图，多轴多系列
        c3.setTypes([[BICst.WIDGET.AXIS, BICst.WIDGET.AXIS, BICst.WIDGET.AXIS],[BICst.WIDGET.AXIS, BICst.WIDGET.AXIS, BICst.WIDGET.AXIS]]);
        c3.populate(stackedItems);
        //百分比堆积柱状，单轴多系列
        c4.setTypes([[BICst.WIDGET.AXIS, BICst.WIDGET.AXIS, BICst.WIDGET.AXIS]]);
        c4.populate(stackByPercentItems1);
        //对比柱形图，多轴多系列
        c5.setTypes([[BICst.WIDGET.AXIS, BICst.WIDGET.AXIS, BICst.WIDGET.AXIS],[BICst.WIDGET.REVERSED_AXIS, BICst.WIDGET.REVERSED_AXIS, BICst.WIDGET.REVERSED_AXIS]]);
        c5.populate(compareitems);
        //瀑布图
        c6.setTypes([[BICst.WIDGET.AXIS, BICst.WIDGET.AXIS]]);
        c6.populate(items3);
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [c1, c2, c3, c4, c5, c6]
        });
    }
});
ColumnChartsModel = BI.inherit(BI.Model, {

});