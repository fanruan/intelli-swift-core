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
        var items = [[
            {
                data: [
                    {"y": 19485000, "x": -29, "z": 19485000},
                    {"y": 24779420, "x": -285, "z": 24779420},
                    {"y": 11665800, "x": -35, "z": 11665800}],
                name: "长期协议"
            }, {
                data: [
                    {"y": 19485000, "x": -45, "z": 19485000},
                    {"y": 24779420, "x": -260, "z": 24779420},
                    {"y": 11665800, "x": -20, "z": 11665800}],
                name: "购买合同"
            }, {
                data: [
                    {"y": 19485000, "x": 29, "z": 19485000},
                    {"y": 24779420, "x": 285, "z": 24779420},
                    {"y": 11665800, "x": 35, "z": 11665800}],
                name: "长期协议订单"
            }, {
                data: [
                    {"y": 19485000, "x": 45, "z": 19485000},
                    {"y": 24779420, "x": 260, "z": 24779420},
                    {"y": 11665800, "x": 20, "z": 11665800}],
                name: "购买服务"
            }]];
        var items1 = [[
            {
                data: [
                    {"y": 19485000, "x": 29, "z": 19485000},
                    {"y": 24779420, "x": 285, "z": 24779420},
                    {"y": 11665800, "x": 35, "z": 11665800}],
                name: "长期协议",
                stack: "123"
            }, {
                data: [
                    {"y": 19485000, "x": -45, "z": 19485000},
                    {"y": 24779420, "x": -260, "z": 24779420},
                    {"y": 11665800, "x": -20, "z": 11665800}],
                name: "购买合同",
                stack: "123"
            }]];
        var items2 = [[
            {
                data: [
                    {"y": 19485000, "x": 29, "z": 19485000},
                    {"y": 24779420, "x": 285, "z": 24779420},
                    {"y": 11665800, "x": 35, "z": 11665800}],
                name: "长期协议"
            }, {
                data: [
                    {"y": 19485000, "x": 45, "z": 19485000},
                    {"y": 24779420, "x": 260, "z": 24779420},
                    {"y": 11665800, "x": 20, "z": 11665800}],
                name: "购买合同"
            }, {
                data: [
                    {"y": 19485000, "x": 29, "z": 19485000},
                    {"y": 24779420, "x": 285, "z": 24779420},
                    {"y": 11665800, "x": 35, "z": 11665800}],
                name: "长期协议订单"
            }, {
                data: [
                    {"y": 19485000, "x": 45, "z": 19485000},
                    {"y": 24779420, "x": 260, "z": 24779420},
                    {"y": 11665800, "x": 20, "z": 11665800}],
                name: "购买服务"
            }]];
        var i = BI.UUID(), j = BI.UUID();
        var stackedItems =  BI.map(items2, function (idx, item) {
            return BI.map(item, function (id, it) {
                if(id > 0){
                    return BI.extend({}, it, {stack: i});
                }else{
                    return BI.extend({}, it, {stack: j});
                }
            });
        });
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
        //条形图 单轴多系列
        c1.setTypes([[BICst.WIDGET.BAR, BICst.WIDGET.BAR]]);
        c1.populate(items1);
        //堆积条形
        //c2.setTypes([[BICst.WIDGET.BAR, BICst.WIDGET.BAR, BICst.WIDGET.BAR, BICst.WIDGET.BAR]]);
        //c2.populate(stackedItems);
        //c3.setTypes([[BICst.WIDGET.BAR, BICst.WIDGET.BAR, BICst.WIDGET.BAR, BICst.WIDGET.BAR]]);
        //c3.populate(items);
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [c1]
        });
    }
});
BarChartsModel = BI.inherit(BI.Model, {});