HorizontalView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(HorizontalView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-horizontal bi-mvc-layout"
        })
    },

    _init: function () {
        HorizontalView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var horizontal = BI.createWidget({
            type: "bi.horizontal",
            rgap: -10,
            items: [{
                type: "bi.text_button",
                cls: "layout-bg" + BI.random(1, 8),
                text: "这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)",
                height: 30
            }, {
                type: "bi.text_button",
                cls: "layout-bg" + BI.random(1, 8),
                text: "这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)",
                height: 30
            }, {
                type: "bi.text_button",
                cls: "layout-bg" + BI.random(1, 8),
                text: "这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)",
                height: 30
            }, {
                type: "bi.text_button",
                cls: "layout-bg" + BI.random(1, 8),
                text: "这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)",
                height: 30
            }, {
                type: "bi.text_button",
                cls: "layout-bg" + BI.random(1, 8),
                text: "这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)",
                height: 30
            }]
        });
        horizontal.addItem({
            type: "bi.text_button",
            cls: "layout-bg" + BI.random(1, 8),
            text: "这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)",
            height: 30
        });
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [horizontal, {
                type: "bi.horizontal",
                items: [{
                    type: "bi.absolute",
                    items: [{
                        el: {
                            type: "bi.text_button",
                            cls: "layout-bg" + BI.random(1, 8),
                            text: "这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)",
                            height: 30
                        },
                        left: 0,
                        right: 0
                    }],
                    height: 30
                }, {
                    type: "bi.search_editor",
                    height: 30
                }, {
                    type: "bi.absolute",
                    items: [{
                        el: {
                            type: "bi.text_button",
                            cls: "layout-bg" + BI.random(1, 8),
                            text: "这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)这里设置了lgap(左边距)，rgap(右边距)，tgap(上边距)，bgap(下边距)",
                            height: 30
                        },
                        left: 0,
                        right: 0
                    }],
                    height: 30
                }]
            }],
            lgap: 20,
            rgap: 80,
            tgap: 80,
            bgap: 50
        })
    },

    refresh: function () {

    }
});

HorizontalModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(HorizontalModel.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        HorizontalModel.superclass._init.apply(this, arguments);
    }
});