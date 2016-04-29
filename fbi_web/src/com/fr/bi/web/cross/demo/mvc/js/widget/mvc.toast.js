ToastView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ToastView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-toast bi-mvc-layout"
        })
    },

    _init: function () {
        ToastView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        var items = [
            {
                el: {
                    type: 'bi.button',
                    text: '简单Toast测试',
                    height : 30,
                    handler: function(){
                        BI.Msg.toast("这是一条简单的数据");
                    }
                }
            }, {
                el: {
                    type: 'bi.button',
                    text: '很长的Toast测试',
                    height : 30,
                    handler: function(){
                        BI.Msg.toast("这是一条很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长的数据")
                    }
                }
            }, {
                el: {
                    type: 'bi.button',
                    text: '非常长的Toast测试',
                    height : 30,
                    handler: function(){
                        BI.Msg.toast("这是一条非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长非常长的数据")
                    }
                }
            }, {
                el: {
                    type: 'bi.button',
                    text: '错误提示Toast测试',
                    level: "warning",
                    height : 30,
                    handler: function(){
                        BI.Msg.toast("错误提示Toast测试", "warning");
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

ToastModel = BI.inherit(BI.Model, {

});