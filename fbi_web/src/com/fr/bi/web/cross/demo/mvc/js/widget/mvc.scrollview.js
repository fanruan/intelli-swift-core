ScrollView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(ScrollView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-tab bi-mvc-layout"
        })
    },

    _init: function(){
        ScrollView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var scroll = BI.createWidget({
            type: "bi.scroll_view",
            height: 150,
            items: [{
                type: "bi.label",
                text: "测试1",
                height: 30
            },{
                type: "bi.label",
                text: "测试2",
                height: 30
            },{
                type: "bi.label",
                text: "测试3",
                height: 30
            },{
                type: "bi.label",
                text: "测试4",
                height: 30
            },{
                type: "bi.label",
                text: "测试5",
                height: 30
            }]
        });
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [scroll]
        })

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: {
                    type: "bi.button",
                    text: "添加元素",
                    height: 30,
                    handler: function () {
                        scroll.addItem({
                            type: "bi.label",
                            text: "测试" + BI.UUID(),
                            height: 30
                        })
                    }
                },
                bottom: 0,
                left: 0,
                right: 0
            }]
        })
    }
});

ScrollModel = BI.inherit(BI.Model, {

});