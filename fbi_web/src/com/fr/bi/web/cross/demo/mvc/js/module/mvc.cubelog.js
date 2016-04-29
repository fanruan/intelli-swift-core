CubeLogView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(CubeLogView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-cube-log"
        })
    },

    _init: function () {
        CubeLogView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var items = [
            {id: 1, pId: -1, type: "bi.cube_log_wrong_info_node", open: true, isParent: true},
            {id: 2, pId: -1, text: "数据库转移数据所用时间", second: 23000, isParent: true},
            {id: 3, pId: -1, text: "表中数据生成索引所用时间", second: 23, isParent: true},
            {id: 4, pId: -1, text: "表中字段关联所用时间", second: 1000, isParent: true},
            {id: 11, pId: 1, type: "bi.cube_log_wrong_info_item", items: [{value: "错误信息1"}, {value: "错误信息2"}]},
            {id: 21, pId: 2, text: "A>B>C"},
            {id: 22, pId: 2, text: "A1>B1>C1"},
            {id: 31, pId: 3, text: "A>B>C"},
            {id: 32, pId: 3, text: "A1>B1>C1"},
            {id: 41, pId: 4, text: "A>B>C"},
            {id: 42, pId: 4, text: "A1>B1>C1"}
        ];

        var cubelog = BI.createWidget({
            type: "bi.cube_log_tree",
            items: BI.deepClone(items)
        });

        BI.createWidget({
            type: "bi.center",
            element: vessel,
            hgap: 50,
            vgap: 50,
            items: [{
                type: "bi.grid",
                items: [[cubelog]]
            }]
        });
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: {
                    type: "bi.button",
                    text: "populate",
                    handler: function () {
                        var t = BI.deepClone(items);
                        t.push({id: 23, pId: 2, text: "A2>B2>C2"});
                        cubelog.populate(t);
                    },
                    height: 30
                },
                right: 0,
                bottom: 0,
                left: 0
            }]
        })
    }
});

CubeLogModel = BI.inherit(BI.Model, {});