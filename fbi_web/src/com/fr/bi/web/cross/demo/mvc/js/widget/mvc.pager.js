PagerView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(PagerView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-pager bi-mvc-layout"
        })
    },

    _init: function(){
        PagerView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                type: "bi.label",
                height: 30,
                text: "默认的分页"
            }, {
                type: "bi.pager",
                height: 50,
                pages: 18,
                groups: 5,
                curr: 6,
                first: "首页",
                last: "尾页"
            }, {
                type: "bi.label",
                height: 30,
                text: "显示上一页、下一页、首页、尾页"
            }, {
                type: "bi.pager",
                dynamicShow: false,
                height: 50,
                pages: 18,
                groups: 5,
                curr: 1,
                first: "首页>",
                last: "<尾页"
            }, {
                type: "bi.label",
                height: 30,
                text: "显示上一页、下一页"
            }, {
                type: "bi.pager",
                dynamicShow: false,
                dynamicShowFirstLast: true,
                height: 50,
                pages: 18,
                groups: 5,
                curr: 1,
                first: "首页>",
                last: "<尾页"
            }, {
                type: "bi.label",
                height: 30,
                text: "自定义上一页、下一页"
            }, {
                type: "bi.pager",
                dynamicShow: false,
                height: 50,
                pages: 18,
                groups: 5,
                curr: 6,
                prev: {
                    type: "bi.button",
                    cls: "",
                    text: "上一页",
                    value: "prev",
                    once: false,
                    height: 30,
                    handler: function(){

                    }
                },
                next: {
                    type: "bi.button",
                    cls: "",
                    text: "下一页",
                    value: "next",
                    once: false,
                    handler: function(){

                    }
                }
            }, {
                type: "bi.label",
                height: 30,
                text: "不知道总页数的情况(测试条件 1<=page<=3)"
            }, {
                type: "bi.pager",
                dynamicShow: false,
                height: 50,
                pages: false,
                curr: 1,
                prev: {
                    type: "bi.button",
                    cls: "",
                    text: "上一页",
                    value: "prev",
                    once: false,
                    height: 30,
                    handler: function(){

                    }
                },
                next: {
                    type: "bi.button",
                    cls: "",
                    text: "下一页",
                    value: "next",
                    once: false,
                    handler: function(){

                    }
                },
                hasPrev: function(v){
                    return v>1;
                },
                hasNext: function(v){
                    return v<3;
                }
            }]
        })
    }
});

PagerModel = BI.inherit(BI.Model, {

});