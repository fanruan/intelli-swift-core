SkipPagerView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SkipPagerView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-skip-pager bi-mvc-layout"
        })
    },

    _init: function () {
        SkipPagerView.superclass._init.apply(this, arguments);
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
                type: "bi.skip_pager",
                pages: 3,
                curr: 1
            }, {
                type: "bi.label",
                height: 30,
                text: "不知道总页数的分页， (测试条件：总页数为3)"
            }, {
                type: "bi.skip_pager",
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

SkipPagerModel = BI.inherit(BI.Model, {});