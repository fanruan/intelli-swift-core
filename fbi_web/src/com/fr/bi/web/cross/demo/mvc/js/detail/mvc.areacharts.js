AreaChartsView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(AreaChartsView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-adapt-editor bi-mvc-layout"
        })
    },

    _init: function(){
        AreaChartsView.superclass._init.apply(this, arguments);
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
        var c7 = BI.createWidget({
            type: "bi.combine_chart",
            width: 600,
            height: 300
        });
        var c8 = BI.createWidget({
            type: "bi.combine_chart",
            width: 600,
            height: 300
        });
        var c9 = BI.createWidget({
            type: "bi.combine_chart",
            width: 600,
            height: 300
        });
        var c10 = BI.createWidget({
            type: "bi.combine_chart",
            width: 600,
            height: 300
        });
        var c11 = BI.createWidget({
            type: "bi.combine_chart",
            width: 600,
            height: 300
        });
        //面积图
        c7.setTypes([[BICst.WIDGET.AREA, BICst.WIDGET.AREA],[BICst.WIDGET.AREA, BICst.WIDGET.AREA]]);
        c7.populate(items);
        //堆积面积
        c8.setTypes([[BICst.WIDGET.AREA, BICst.WIDGET.AREA]]);
        c8.populate(stackedItems1);
        //百分比堆积面积，单轴多系列
        c9.setTypes([[BICst.WIDGET.AREA, BICst.WIDGET.AREA, BICst.WIDGET.AREA]]);
        c9.populate(stackByPercentItems1);
        //对比面积图
        c11.setTypes([[BICst.WIDGET.AREA, BICst.WIDGET.AREA], [BICst.WIDGET.AREA, BICst.WIDGET.AREA]]);
        c11.populate(compareitems);
        //范围面积
        c10.setTypes([[BICst.WIDGET.AREA, BICst.WIDGET.AREA,BICst.WIDGET.AREA, BICst.WIDGET.AREA]]);
        c10.populate([[{"targetIds":[],"name":"qw","data":[{"x":"qq","y":5},{"x":"aa","y":7},{"x":"bb","y":4},{"x":"cc","y":2},{"x":"dd","y":10},{"x":"ee","y":5}],"fillColorOpacity":0,"stack":"stackedArea","fillColor":false,"lineWidth":0},{"data":[{"x":"qq","y":3},{"x":"aa","y":-4},{"x":"bb","y":6},{"x":"cc","y":7},{"x":"dd","y":-3},{"x":"ee","y":4}],"name":"差值","stack":"stackedArea","fillColor":"#5caae4"}]]);
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [c10]
        });
    }
});
AreaChartsModel = BI.inherit(BI.Model, {

});