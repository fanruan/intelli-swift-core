MessageView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(MessageView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-message bi-mvc-layout"
        })
    },

    _init: function () {
        MessageView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.center_adapt",
            element: vessel,
            items : [
                {
                    el : {
                        type : 'bi.button',
                        text : '点击我弹出一个消息框',
                        height : 30,
                        handler : function() {
                            BI.Msg.alert('测试消息框', '我是测试消息框的内容');
                        }
                    }
                }
            ]
        })
    }
});

MessageModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(MessageModel.superclass._defaultConfig.apply(this, arguments), {

        })
    },
    init : function() {
        MessageModel.superclass._init.apply(this, arguments);
    }
});