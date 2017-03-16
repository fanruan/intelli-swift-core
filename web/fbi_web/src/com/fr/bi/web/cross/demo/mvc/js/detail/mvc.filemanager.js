FileManagerView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(FileManagerView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-file-manager bi-mvc-layout"
        })
    },

    _init: function () {
        FileManagerView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var filemanager = BI.createWidget({
            type: "bi.file_manager",
            items: [
                {
                    id: 1,
                    value: "根目录",
                    children: [
                        {
                            id: 11,
                            value: "第一级子目录",
                            children: [
                                {id: 111, value: "文件2"},
                                {id: 112, value: "文件3"}
                            ]
                        },
                        {id: 12, value: "文件1"}
                    ]
                }
            ],
            width: 700,
            height: 500
        });
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [filemanager, {
                type: "bi.button",
                text: "getValue",
                handler: function () {
                    BI.Msg.toast(JSON.stringify(filemanager.getValue()));
                }
            }]
        })
    }
});

FileManagerModel = BI.inherit(BI.Model, {});