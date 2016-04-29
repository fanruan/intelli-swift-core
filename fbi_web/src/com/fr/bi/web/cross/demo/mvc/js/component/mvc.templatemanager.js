TemplateManagerView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TemplateManagerView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-template-manager bi-mvc-layout"
        })
    },

    _init: function () {
        TemplateManagerView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.template_manager",
            element: vessel,
            items: [
                {
                    id: "1",
                    value: "1",
                    text: "根目录",
                    lastModify: 1454316355142
                }, {
                    id: "11",
                    pId: "1",
                    value: "11",
                    text: "第一级子目录",
                    lastModify: 1454316355142
                }, {
                    id: "12",
                    pId: "1",
                    value: "12", text: "第二级子目录", lastModify: 1454316355142
                }, {
                    id: "111",
                    pId: "12",
                    buildUrl: "www.baidu.com",
                    value: "111", text: "文件2", lastModify: 1454316355142
                }, {
                    id: "112",
                    pId: "12",
                    buildUrl: "www.baidu.com",
                    value: "112", text: "文件3", lastModify: 1454316355142
                }
            ]
        })
    }
});

TemplateManagerModel = BI.inherit(BI.Model, {});