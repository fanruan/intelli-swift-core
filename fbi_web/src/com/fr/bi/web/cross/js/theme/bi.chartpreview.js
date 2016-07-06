/**
 * 图表预览
 *
 * Created by GUY on 2016/7/6.
 * @class FS.ChartPreview
 * @extends BI.Widget
 */
FS.ChartPreview = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(FS.ChartPreview.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-fs-chart-preview"
        });
    },

    _init: function () {
        FS.ChartPreview.superclass._init.apply(this, arguments);
        var chart1 = this._createAxis();
        var chart2 = this._createScatter();
        var chart3 = this._createLine();
        var chart4 = this._createCombine();

        BI.createWidget({
            type: "bi.grid",
            element: this.element,
            items: [[chart1, chart2], [chart3, chart4]]
        })
    },

    _createTitle: function (index) {

    },

    _getData1: function () {
        return [[{
            "data": [{
                "x": "江苏省",
                "y": 23,
                "value": "江苏省",
                "seriesName": "常州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "常州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "常州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "常州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "常州市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "常州市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 12,
                "value": "江苏省",
                "seriesName": "海门市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "海门市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "海门市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "海门市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "海门市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "海门市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "杭州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "杭州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "杭州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 56, "value": "浙江省", "seriesName": "杭州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "杭州市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "杭州市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "湖州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "湖州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "湖州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 15, "value": "浙江省", "seriesName": "湖州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "湖州市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "湖州市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 15,
                "value": "江苏省",
                "seriesName": "南通市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "南通市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "南通市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "南通市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "南通市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "南通市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "宁波市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "宁波市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "宁波市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 66, "value": "浙江省", "seriesName": "宁波市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "宁波市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "宁波市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "上海市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 17, "value": "上海市", "seriesName": "上海市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "上海市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "上海市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "上海市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "上海市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "绍兴市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "绍兴市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "绍兴市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 34, "value": "浙江省", "seriesName": "绍兴市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "绍兴市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "绍兴市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 85,
                "value": "江苏省",
                "seriesName": "苏州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "苏州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "苏州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "苏州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "苏州市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "苏州市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 35,
                "value": "江苏省",
                "seriesName": "太仓市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "太仓市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "太仓市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "太仓市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "太仓市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "太仓市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 14,
                "value": "江苏省",
                "seriesName": "泰州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "泰州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "泰州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "泰州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "泰州市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "泰州市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "天津市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "天津市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 15,
                "value": "天津市",
                "seriesName": "天津市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "天津市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "天津市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "天津市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 57,
                "value": "江苏省",
                "seriesName": "扬州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "扬州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "扬州市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "扬州市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "扬州市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "扬州市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "重庆市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "重庆市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "重庆市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 0, "value": "浙江省", "seriesName": "重庆市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 78,
                "value": "重庆市",
                "seriesName": "重庆市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "重庆市", "stack": "b8d4e892344ddf77"
        }, {
            "data": [{
                "x": "江苏省",
                "y": 0,
                "value": "江苏省",
                "seriesName": "舟山市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "上海市", "y": 0, "value": "上海市", "seriesName": "舟山市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "天津市",
                "y": 0,
                "value": "天津市",
                "seriesName": "舟山市",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "浙江省", "y": 97, "value": "浙江省", "seriesName": "舟山市", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "重庆市",
                "y": 0,
                "value": "重庆市",
                "seriesName": "舟山市",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "舟山市", "stack": "b8d4e892344ddf77"
        }]];
    },

    _getConfig1: function () {
        return {
            "chart_color": ["#19a0da", "#65bbe6", "#b2daf3", "#338ede", "#5a99e6"],
            "chart_style": 0,
            "chart_inner_radius": 0,
            "chart_total_angle": 360,
            "left_y_axis_style": 1,
            "x_axis_style": 1,
            "right_y_axis_style": 1,
            "right_y_axis_second_style": 1,
            "left_y_axis_number_level": 1,
            "number_of_pointer": 1,
            "dashboard_number_level": 1,
            "x_axis_number_level": 1,
            "right_y_axis_number_level": 1,
            "right_y_axis_second_number_level": 1,
            "left_y_axis_unit": "",
            "dashboard_unit": "",
            "x_axis_unit": "",
            "right_y_axis_unit": "",
            "right_y_axis_second_unit": "",
            "show_left_y_axis_title": false,
            "show_right_y_axis_title": false,
            "show_right_y_axis_second_title": false,
            "left_y_axis_title": "回款金额",
            "right_y_axis_title": "",
            "right_y_axis_second_title": "",
            "left_y_axis_reversed": false,
            "right_y_axis_reversed": false,
            "right_y_axis_second_reversed": false,
            "show_x_axis_title": false,
            "x_axis_title": "省",
            "text_direction": "0",
            "chart_legend": 1,
            "show_data_label": false,
            "show_data_table": false,
            "show_grid_line": true,
            "show_zoom": false,
            "cordon": [[], [], [], []],
            "tooltip": ""
        };
    },

    _getData2: function () {
        return [[{
            "name": "常州市",
            "data": [{"x": 77, "y": 23, "seriesName": "常州市", "targetIds": ["f80a62a6d4680200", "91597d222c224da6"]}]
        }, {
            "name": "海门市",
            "data": [{"x": 38, "y": 12, "seriesName": "海门市", "targetIds": ["f80a62a6d4680200", "91597d222c224da6"]}]
        }, {
            "name": "杭州市",
            "data": [{"x": 25, "y": 56, "seriesName": "杭州市", "targetIds": ["f80a62a6d4680200", "91597d222c224da6"]}]
        }, {
            "name": "湖州市",
            "data": [{"x": 27, "y": 15, "seriesName": "湖州市", "targetIds": ["f80a62a6d4680200", "91597d222c224da6"]}]
        }, {
            "name": "南通市",
            "data": [{"x": 54, "y": 15, "seriesName": "南通市", "targetIds": ["f80a62a6d4680200", "91597d222c224da6"]}]
        }, {
            "name": "宁波市",
            "data": [{"x": 22, "y": 66, "seriesName": "宁波市", "targetIds": ["f80a62a6d4680200", "91597d222c224da6"]}]
        }, {
            "name": "上海市",
            "data": [{"x": 39, "y": 17, "seriesName": "上海市", "targetIds": ["f80a62a6d4680200", "91597d222c224da6"]}]
        }, {
            "name": "绍兴市",
            "data": [{"x": 81, "y": 34, "seriesName": "绍兴市", "targetIds": ["f80a62a6d4680200", "91597d222c224da6"]}]
        }, {
            "name": "苏州市",
            "data": [{"x": 20, "y": 85, "seriesName": "苏州市", "targetIds": ["f80a62a6d4680200", "91597d222c224da6"]}]
        }, {
            "name": "太仓市",
            "data": [{"x": 11, "y": 35, "seriesName": "太仓市", "targetIds": ["f80a62a6d4680200", "91597d222c224da6"]}]
        }, {
            "name": "泰州市",
            "data": [{"x": 42, "y": 14, "seriesName": "泰州市", "targetIds": ["f80a62a6d4680200", "91597d222c224da6"]}]
        }, {
            "name": "天津市",
            "data": [{"x": 43, "y": 15, "seriesName": "天津市", "targetIds": ["f80a62a6d4680200", "91597d222c224da6"]}]
        }, {
            "name": "扬州市",
            "data": [{"x": 78, "y": 57, "seriesName": "扬州市", "targetIds": ["f80a62a6d4680200", "91597d222c224da6"]}]
        }, {
            "name": "重庆市",
            "data": [{"x": 20, "y": 78, "seriesName": "重庆市", "targetIds": ["f80a62a6d4680200", "91597d222c224da6"]}]
        }, {
            "name": "舟山市",
            "data": [{"x": 25, "y": 97, "seriesName": "舟山市", "targetIds": ["f80a62a6d4680200", "91597d222c224da6"]}]
        }]];
    },

    _getConfig2: function () {
        return {
            "chart_color": ["#19a0da", "#65bbe6", "#b2daf3", "#338ede", "#5a99e6", "#9bbff2", "#4278e5", "#688eed", "#96adf2", "#4356e6", "#6772f0", "#a0a3fa", "#19a0da", "#65bbe6", "#b2daf3", "#338ede", "#5a99e6", "#9bbff2", "#4278e5", "#688eed", "#96adf2", "#4356e6", "#6772f0", "#a0a3fa", "#19a0da", "#65bbe6", "#b2daf3", "#338ede", "#5a99e6", "#9bbff2", "#4278e5", "#688eed"],
            "chart_style": 0,
            "chart_inner_radius": 0,
            "chart_total_angle": 360,
            "left_y_axis_style": 1,
            "x_axis_style": 1,
            "right_y_axis_style": 1,
            "right_y_axis_second_style": 1,
            "left_y_axis_number_level": 1,
            "number_of_pointer": 1,
            "dashboard_number_level": 1,
            "x_axis_number_level": 1,
            "right_y_axis_number_level": 1,
            "right_y_axis_second_number_level": 1,
            "left_y_axis_unit": "",
            "dashboard_unit": "",
            "x_axis_unit": "",
            "right_y_axis_unit": "",
            "right_y_axis_second_unit": "",
            "show_left_y_axis_title": false,
            "show_right_y_axis_title": false,
            "show_right_y_axis_second_title": false,
            "left_y_axis_title": "回款金额",
            "right_y_axis_title": "",
            "right_y_axis_second_title": "",
            "left_y_axis_reversed": false,
            "right_y_axis_reversed": false,
            "right_y_axis_second_reversed": false,
            "show_x_axis_title": false,
            "x_axis_title": "省",
            "text_direction": "0",
            "chart_legend": 1,
            "show_data_label": false,
            "show_data_table": false,
            "show_grid_line": true,
            "show_zoom": false,
            "cordon": [[], []],
            "tooltip": "function(){ return this.seriesName+'<div>(X)合同总价:'+ this.x +'</div><div>(Y)回款金额:'+ this.y +'</div>'}"
        };
    },

    _getData3: function () {
        return [[{
            "data": [{
                "x": "常州市",
                "y": 23,
                "value": "常州市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "海门市", "y": 12, "value": "海门市", "seriesName": "回款金额", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "杭州市",
                "y": 56,
                "value": "杭州市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "湖州市", "y": 15, "value": "湖州市", "seriesName": "回款金额", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "南通市",
                "y": 15,
                "value": "南通市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "宁波市", "y": 66, "value": "宁波市", "seriesName": "回款金额", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "上海市",
                "y": 17,
                "value": "上海市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "绍兴市", "y": 34, "value": "绍兴市", "seriesName": "回款金额", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "苏州市",
                "y": 85,
                "value": "苏州市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "太仓市", "y": 35, "value": "太仓市", "seriesName": "回款金额", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "泰州市",
                "y": 14,
                "value": "泰州市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "天津市", "y": 15, "value": "天津市", "seriesName": "回款金额", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "扬州市",
                "y": 57,
                "value": "扬州市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "重庆市", "y": 78, "value": "重庆市", "seriesName": "回款金额", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "舟山市",
                "y": 97,
                "value": "舟山市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "回款金额"
        }, {
            "data": [{
                "x": "常州市",
                "y": 77,
                "value": "常州市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }, {"x": "海门市", "y": 38, "value": "海门市", "seriesName": "合同总价", "targetIds": ["91597d222c224da6"]}, {
                "x": "杭州市",
                "y": 25,
                "value": "杭州市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }, {"x": "湖州市", "y": 27, "value": "湖州市", "seriesName": "合同总价", "targetIds": ["91597d222c224da6"]}, {
                "x": "南通市",
                "y": 54,
                "value": "南通市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }, {"x": "宁波市", "y": 22, "value": "宁波市", "seriesName": "合同总价", "targetIds": ["91597d222c224da6"]}, {
                "x": "上海市",
                "y": 39,
                "value": "上海市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }, {"x": "绍兴市", "y": 81, "value": "绍兴市", "seriesName": "合同总价", "targetIds": ["91597d222c224da6"]}, {
                "x": "苏州市",
                "y": 20,
                "value": "苏州市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }, {"x": "太仓市", "y": 11, "value": "太仓市", "seriesName": "合同总价", "targetIds": ["91597d222c224da6"]}, {
                "x": "泰州市",
                "y": 42,
                "value": "泰州市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }, {"x": "天津市", "y": 43, "value": "天津市", "seriesName": "合同总价", "targetIds": ["91597d222c224da6"]}, {
                "x": "扬州市",
                "y": 78,
                "value": "扬州市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }, {"x": "重庆市", "y": 20, "value": "重庆市", "seriesName": "合同总价", "targetIds": ["91597d222c224da6"]}, {
                "x": "舟山市",
                "y": 25,
                "value": "舟山市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }], "name": "合同总价"
        }]];
    },

    _getConfig3: function () {
        return {
            "chart_color": ["#19a0da", "#65bbe6", "#b2daf3", "#338ede", "#5a99e6", "#9bbff2", "#4278e5", "#688eed", "#96adf2", "#4356e6", "#6772f0", "#a0a3fa", "#19a0da", "#65bbe6", "#b2daf3", "#338ede", "#5a99e6", "#9bbff2", "#4278e5", "#688eed", "#96adf2", "#4356e6", "#6772f0", "#a0a3fa", "#19a0da", "#65bbe6", "#b2daf3", "#338ede", "#5a99e6", "#9bbff2", "#4278e5", "#688eed"],
            "chart_style": 0,
            "chart_inner_radius": 0,
            "chart_total_angle": 360,
            "left_y_axis_style": 1,
            "x_axis_style": 1,
            "right_y_axis_style": 1,
            "right_y_axis_second_style": 1,
            "left_y_axis_number_level": 1,
            "number_of_pointer": 1,
            "dashboard_number_level": 1,
            "x_axis_number_level": 1,
            "right_y_axis_number_level": 1,
            "right_y_axis_second_number_level": 1,
            "left_y_axis_unit": "",
            "dashboard_unit": "",
            "x_axis_unit": "",
            "right_y_axis_unit": "",
            "right_y_axis_second_unit": "",
            "show_left_y_axis_title": false,
            "show_right_y_axis_title": false,
            "show_right_y_axis_second_title": false,
            "left_y_axis_title": "回款金额",
            "right_y_axis_title": "",
            "right_y_axis_second_title": "",
            "left_y_axis_reversed": false,
            "right_y_axis_reversed": false,
            "right_y_axis_second_reversed": false,
            "show_x_axis_title": false,
            "x_axis_title": "省",
            "text_direction": "0",
            "chart_legend": 1,
            "show_data_label": false,
            "show_data_table": false,
            "show_grid_line": true,
            "show_zoom": false,
            "cordon": [[], [], [], []],
            "tooltip": ""
        };
    },

    _getData4: function () {
        return [[{
            "data": [{
                "x": "常州市",
                "y": 77,
                "value": "常州市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }, {"x": "海门市", "y": 38, "value": "海门市", "seriesName": "合同总价", "targetIds": ["91597d222c224da6"]}, {
                "x": "杭州市",
                "y": 25,
                "value": "杭州市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }, {"x": "湖州市", "y": 27, "value": "湖州市", "seriesName": "合同总价", "targetIds": ["91597d222c224da6"]}, {
                "x": "南通市",
                "y": 54,
                "value": "南通市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }, {"x": "宁波市", "y": 22, "value": "宁波市", "seriesName": "合同总价", "targetIds": ["91597d222c224da6"]}, {
                "x": "上海市",
                "y": 39,
                "value": "上海市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }, {"x": "绍兴市", "y": 81, "value": "绍兴市", "seriesName": "合同总价", "targetIds": ["91597d222c224da6"]}, {
                "x": "苏州市",
                "y": 20,
                "value": "苏州市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }, {"x": "太仓市", "y": 11, "value": "太仓市", "seriesName": "合同总价", "targetIds": ["91597d222c224da6"]}, {
                "x": "泰州市",
                "y": 42,
                "value": "泰州市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }, {"x": "天津市", "y": 43, "value": "天津市", "seriesName": "合同总价", "targetIds": ["91597d222c224da6"]}, {
                "x": "扬州市",
                "y": 78,
                "value": "扬州市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }, {"x": "重庆市", "y": 20, "value": "重庆市", "seriesName": "合同总价", "targetIds": ["91597d222c224da6"]}, {
                "x": "舟山市",
                "y": 25,
                "value": "舟山市",
                "seriesName": "合同总价",
                "targetIds": ["91597d222c224da6"]
            }], "name": "合同总价"
        }], [{
            "data": [{
                "x": "常州市",
                "y": 23,
                "value": "常州市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "海门市", "y": 12, "value": "海门市", "seriesName": "回款金额", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "杭州市",
                "y": 56,
                "value": "杭州市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "湖州市", "y": 15, "value": "湖州市", "seriesName": "回款金额", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "南通市",
                "y": 15,
                "value": "南通市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "宁波市", "y": 66, "value": "宁波市", "seriesName": "回款金额", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "上海市",
                "y": 17,
                "value": "上海市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "绍兴市", "y": 34, "value": "绍兴市", "seriesName": "回款金额", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "苏州市",
                "y": 85,
                "value": "苏州市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "太仓市", "y": 35, "value": "太仓市", "seriesName": "回款金额", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "泰州市",
                "y": 14,
                "value": "泰州市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "天津市", "y": 15, "value": "天津市", "seriesName": "回款金额", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "扬州市",
                "y": 57,
                "value": "扬州市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }, {"x": "重庆市", "y": 78, "value": "重庆市", "seriesName": "回款金额", "targetIds": ["f80a62a6d4680200"]}, {
                "x": "舟山市",
                "y": 97,
                "value": "舟山市",
                "seriesName": "回款金额",
                "targetIds": ["f80a62a6d4680200"]
            }], "name": "回款金额"
        }]];
    },

    _getConfig4: function () {
        return {
            "chart_color": ["#19a0da", "#65bbe6", "#b2daf3", "#338ede", "#5a99e6", "#9bbff2", "#4278e5", "#688eed", "#96adf2", "#4356e6", "#6772f0", "#a0a3fa", "#19a0da", "#65bbe6", "#b2daf3", "#338ede", "#5a99e6", "#9bbff2", "#4278e5", "#688eed", "#96adf2", "#4356e6", "#6772f0", "#a0a3fa", "#19a0da", "#65bbe6", "#b2daf3", "#338ede", "#5a99e6", "#9bbff2", "#4278e5", "#688eed"],
            "chart_style": 0,
            "chart_inner_radius": 0,
            "chart_total_angle": 360,
            "left_y_axis_style": 1,
            "x_axis_style": 1,
            "right_y_axis_style": 1,
            "right_y_axis_second_style": 1,
            "left_y_axis_number_level": 1,
            "number_of_pointer": 1,
            "dashboard_number_level": 1,
            "x_axis_number_level": 1,
            "right_y_axis_number_level": 1,
            "right_y_axis_second_number_level": 1,
            "left_y_axis_unit": "",
            "dashboard_unit": "",
            "x_axis_unit": "",
            "right_y_axis_unit": "",
            "right_y_axis_second_unit": "",
            "show_left_y_axis_title": false,
            "show_right_y_axis_title": false,
            "show_right_y_axis_second_title": false,
            "left_y_axis_title": "回款金额",
            "right_y_axis_title": "",
            "right_y_axis_second_title": "",
            "left_y_axis_reversed": false,
            "right_y_axis_reversed": false,
            "right_y_axis_second_reversed": false,
            "show_x_axis_title": false,
            "x_axis_title": "省",
            "text_direction": "0",
            "chart_legend": 1,
            "show_data_label": false,
            "show_data_table": false,
            "show_grid_line": true,
            "show_zoom": false,
            "cordon": [[], [], [], []],
            "tooltip": ""
        };
    },

    _createAxis: function () {
        this.chart1 = BI.createWidget({
            type: "bi.compare_axis_chart"
        });
        return this.chart1;
    },

    _createScatter: function () {
        this.chart2 = BI.createWidget({
            type: "bi.scatter_chart"
        });
        return this.chart2;
    },

    _createLine: function () {
        this.chart3 = BI.createWidget({
            type: "bi.line_chart"
        });
        return this.chart3;
    },

    _createCombine: function () {
        this.chart4 = BI.createWidget({
            type: "bi.axis_chart"
        });
        return this.chart4;
    },

    populate: function (data) {
        var config1 = this._getConfig1();
        var config2 = this._getConfig2();
        var config3 = this._getConfig3();
        var config4 = this._getConfig4();

        if (BI.isKey(data.chartStyle)) {
            config1.chart_style = data.chartStyle;
            config2.chart_style = data.chartStyle;
            config3.chart_style = data.chartStyle;
            config4.chart_style = data.chartStyle;
        }
        if (BI.isKey(data.defaultColor)) {
            var finded = BI.find(data.styleList, function (i, style) {
                return data.defaultColor = style.value;
            });
            if (finded) {
                config1.chart_color = finded.colors;
                config2.chart_color = finded.colors;
                config3.chart_color = finded.colors;
                config4.chart_color = finded.colors;
            }
        }

        this.chart1.populate(this._getData1(), config1);
        this.chart2.populate(this._getData2(), config2);
        this.chart3.populate(this._getData3(), config3);
        this.chart4.populate(this._getData4(), config4, [[13], [5]]);
    }
});
$.shortcut('fs.chart_preview', FS.ChartPreview);