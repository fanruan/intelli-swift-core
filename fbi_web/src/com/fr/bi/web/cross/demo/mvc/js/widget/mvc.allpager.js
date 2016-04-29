AllPagerView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(AllPagerView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-all-pager bi-mvc-layout"
        })
    },

    _init: function () {
        AllPagerView.superclass._init.apply(this, arguments);
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
                text: " (测试条件：总页数为3)"
            }, {
                type: "bi.all_pager",
                pages: 3,
                curr: 1
            }]
        })
    }
});

AllPagerModel = BI.inherit(BI.Model, {});