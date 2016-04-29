NumberPagerView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(NumberPagerView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-number-pager bi-mvc-layout"
        })
    },

    _init: function () {
        NumberPagerView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.vertical",
            hgap: 200,
            vgap: 50,
            element: vessel,
            items: [{
                type: "bi.label",
                height: 30,
                text: "知道总页数的分页， (测试条件：总页数为3)"
            }, {
                type: "bi.number_pager",
                pages: 3,
                curr: 1
            }, {
                type: "bi.label",
                height: 30,
                text: "不知道总页数的分页， (测试条件：总页数为3)"
            }, {
                type: "bi.number_pager",
                pages: false,
                curr: 1,
                hasNext: function(v){
                    return v<3;
                },
                hasPrev: function(v){
                    return v > 1
                },
                firstPage: 1,
                lastPage: function(){
                    return 3;
                }
            }]
        })
    }
});

NumberPagerModel = BI.inherit(BI.Model, {});