SwitcherView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SwitcherView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-switcher bi-mvc-layout"
        })
    },

    _init: function () {
        SwitcherView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        var adapter = BI.createWidget({
            type: "bi.label",
            cls: "layout-bg2",
            text: "将在该处弹出switcher"
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: adapter,
                top: 50,
                left: 20,
                width: 200,
                height: 300
            }]
        })
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 30,
            vgap: 20,
            items: [{
                type: "bi.switcher",
                el: {
                    type: "bi.button",
                    height: 25,
                    text: "Switcher"
                },
                popup: {
                    cls: "mvc-border layout-bg5",
                    items: BI.createItems([{
                        text: "项目1",
                        value: 1
                    }, {
                        text: "项目2",
                        value: 2
                    }, {
                        text: "项目3",
                        value: 3
                    }, {
                        text: "项目4",
                        value: 4
                    }], {
                        type: "bi.single_select_item",
                        height: 25
                    })
                },
                adapter: adapter
            }]
        })
    }
});

SwitcherModel = BI.inherit(BI.Model, {});