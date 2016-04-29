TooltipView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TooltipView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-tooltip bi-mvc-layout"
        })
    },

    _init: function () {
        TooltipView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [
                {
                    el: {
                        type: 'bi.button',
                        text: 'tooltip测试',
                        title: "tooltip测试",
                        height: 30,
                        handler: function () {

                        }
                    },
                    left: 0,
                    top: 0
                },
                {
                    el: {
                        type: 'bi.button',
                        text: 'tooltip测试',
                        title: "tooltip测试tooltip测试tooltip测试tooltip测试tooltip测试tooltip测试tooltip测试",
                        height: 30,
                        handler: function () {

                        }
                    },
                    left: 0,
                    bottom: 0
                },
                {
                    el: {
                        type: 'bi.button',
                        text: '错误tooltip测试',
                        title: "错误tooltip测试",
                        level: "warning",
                        tipType: "warning",
                        height: 30,
                        handler: function () {

                        }
                    },
                    right: 0,
                    top: 0
                },
                {
                    el: {
                        type: 'bi.button',
                        text: '默认灰化不显示tooltip',
                        title: "灰化tooltip测试",
                        disabled: true,
                        height: 30,
                        handler: function () {

                        }
                    },
                    right: 0,
                    bottom: 0
                }
            ]
        })
    }
});

TooltipModel = BI.inherit(BI.Model, {});