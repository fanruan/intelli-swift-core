BubbleView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ToastView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-bubble bi-mvc-layout"
        })
    },

    _init: function () {
        BubbleView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        var items = [
            {
                el: {
                    type: 'bi.button',
                    text: 'bubble测试',
                    height : 30,
                    handler: function(){
                        BI.Bubbles.show("singleBubble1", "bubble测试", this);
                    }
                }
            }, {
                el: {
                    type: 'bi.button',
                    text: 'bubble测试(居中显示)',
                    height : 30,
                    handler: function(){
                        BI.Bubbles.show("singleBubble2", "bubble测试", this, {
                            offsetStyle: "center"
                        });
                    }
                }
            }, {
                el: {
                    type: 'bi.button',
                    text: 'bubble测试(右边显示)',
                    height : 30,
                    handler: function(){
                        BI.Bubbles.show("singleBubble3", "bubble测试", this, {
                            offsetStyle: "right"
                        });
                    }
                }
            }
        ];
        BI.createWidget({
            type: "bi.left",
            element: vessel,
            vgap : 200,
            hgap : 20,
            items: items
        })
    }
});

BubbleModel = BI.inherit(BI.Model, {

});