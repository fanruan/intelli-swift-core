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
        c10.setTypes([[BICst.WIDGET.AREA, BICst.WIDGET.AREA]]);
        c10.populate([[{"data": [{"x": "1", "y": 16.5}, {"x": "2", "y": 17.8}, {"x": "3", "y": 13.5}, {"x": "4", "y": 10.5}, {
            "x": "5",
            "y": 9.2
        }, {"x": "6", "y": 11.6}, {"x": "7", "y": 10.7}, {"x": "8", "y": 11}, {"x": "9", "y": 11.6}, {
            "x": "10",
            "y": 11.8
        }, {"x": "11", "y": 12.6}, {"x": "12", "y": 13.6}, {"x": "13", "y": 11.4}, {"x": "14", "y": 13.2}, {
            "x": "15",
            "y": 14.2
        }, {"x": "16", "y": 13.1}, {"x": "17", "y": 12.2}, {"x": "18", "y": 12}, {"x": "19", "y": 12}, {
            "x": "20",
            "y": 12.7
        }, {"x": "21", "y": 12.4}, {"x": "22", "y": 12.6}, {"x": "23", "y": 11.9}, {"x": "24", "y": 11}, {
            "x": "25",
            "y": 10.8
        }, {"x": "26", "y": 11.8}, {"x": "27", "y": 10.8}, {"x": "28", "y": 12.5}, {"x": "29", "y": 13}, {
            "x": "30",
            "y": 14
        }], name: "low",
            fillColorOpacity: 0,
            stack: "123",
        fillColor: "rgb(99,178,238)"}, {"data": [{"x": "1", "y": 25}, {"x": "2", "y": 25.7}, {"x": "3", "y": 24.8}, {"x": "4", "y": 21.4}, {
            "x": "5",
            "y": 23.8
        }, {"x": "6", "y": 21.8}, {"x": "7", "y": 23.7}, {"x": "8", "y": 23.3}, {"x": "9", "y": 23.7}, {
            "x": "10",
            "y": 20.7
        }, {"x": "11", "y": 22.4}, {"x": "12", "y": 19.6}, {"x": "13", "y": 22.6}, {"x": "14", "y": 25}, {
            "x": "15",
            "y": 21.6
        }, {"x": "16", "y": 17.1}, {"x": "17", "y": 15.5}, {"x": "18", "y": 20.8}, {"x": "19", "y": 17.1}, {
            "x": "20",
            "y": 18.3
        }, {"x": "21", "y": 19.4}, {"x": "22", "y": 19.9}, {"x": "23", "y": 20.2}, {"x": "24", "y": 19.3}, {
            "x": "25",
            "y": 17.8
        }, {"x": "26", "y": 18.5}, {"x": "27", "y": 16.1}, {"x": "28", "y": 21}, {"x": "29", "y": 22.5}, {
            "x": "30",
            "y": 23
        }], name: "high",
            stack: "123"}]]);
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [c7,c8,c9,c11, c10]
        });
    }
});
AreaChartsModel = BI.inherit(BI.Model, {

});