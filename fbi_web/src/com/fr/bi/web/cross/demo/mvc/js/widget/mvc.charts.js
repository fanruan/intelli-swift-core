ChartsView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ChartsView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-charts bi-mvc-layout",
            width: 960,
            height: 540
        })
    },

    _init: function () {
        ChartsView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var data = [{
            "data": [
                {"x": "孙林", "y": 789},
                {"x": "金士鹏", "y": 156},
                {"x": "张珊", "y": 289},
                {"x": "孙阳", "y": 562},
                {"x": "袁成洁", "y": 546},
                {"x": "张颖", "y": 218},
                {"x": "王伟", "y": 541},
                {"x": "张武", "y": 219},
                {"x": "韩文", "y": 345}
            ],
            "name": "测试1",
            stack: 1
        }, {
            "data": [
                {"x": "孙林", "y": 789},
                {"x": "金士鹏", "y": 156},
                {"x": "张珊", "y": 289},
                {"x": "孙阳", "y": 562},
                {"x": "袁成洁", "y": 546},
                {"x": "张颖", "y": 218},
                {"x": "王伟", "y": 541},
                {"x": "张武", "y": 219},
                {"x": "韩文", "y": 345}
            ],
            "name": "测试2",
            stack: 1
        }];

        var chart = BI.createWidget({
            type: 'bi.combine_chart',
            width: 600,
            height: 400,
            items: [data],
            types: [[BICst.WIDGET.AXIS, BICst.WIDGET.AXIS]]
        });

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [chart]
        });
    }
});

ChartsModel = BI.inherit(BI.Model, {});