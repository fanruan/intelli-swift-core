ExcelVView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ExcelVView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-excel-view"
        })
    },

    _init: function () {
        ExcelVView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var ids = BI.Utils.getAllFieldIDs();
        var items = [[{
            text: "第一行第一列",
            value: ids[0]
        }, {
            text: "第一行第一列",
            value: ids[1]
        }, {
            text: "第一行第一列"
        }], [{
            text: "第一行第一列"
        }, {
            text: "第二行第二列"
        }, {
            text: "第二行第三列"
        }], [{
            text: "第二行第一列"
        }, {
            text: "第三行第二列"
        }, {
            text: "第三行第三列"
        }], [{
            text: "第二行第一列"
        }, {
            text: "第四行第二列"
        }, {
            text: "第四行第三列"
        }], [{
            text: "第五行第一列"
        }, {
            text: "第五行第二列"
        }, {
            text: "第五行第三列"
        }], [{
            text: "第六行第一列"
        }, {
            text: "第六行第二列"
        }, {
            text: "第六行第三列"
        }], [{
            text: "第七行第一列"
        }, {
            text: "第七行第二列"
        }, {
            text: "第七行第三列"
        }], [{
            text: "第八行第一列"
        }, {
            text: "第八行第二列"
        }, {
            text: "第八行第三列"
        }], [{
            text: "第九行第一列"
        }, {
            text: "第九行第二列"
        }, {
            text: "第九行第三列"
        }], [{
            text: "第十行第一列"
        }, {
            text: "第十行第二列"
        }, {
            text: "第十行第三列"
        }], [{
            text: "第十一行第一列"
        }, {
            text: "第十一行第二列"
        }, {
            text: "第十一行第三列"
        }], [{
            text: "第十二行第一列"
        }, {
            text: "第十二行第二列"
        }, {
            text: "第十二行第三列"
        }], [{
            text: "第十三行第一列"
        }, {
            text: "第十三行第二列"
        }, {
            text: "第十三行第三列"
        }], [{
            text: "第十四行第一列"
        }, {
            text: "第十四行第二列"
        }, {
            text: "第十四行第三列"
        }], [{
            text: "第十五行第一列"
        }, {
            text: "第十五行第二列"
        }, {
            text: "第十五行第三列"
        }], [{
            text: "第十六行第一列"
        }, {
            text: "第十六行第二列"
        }, {
            text: "第十六行第三列"
        }], [{
            text: "第十七行第一列"
        }, {
            text: "第十七行第二列"
        }, {
            text: "第十七行第三列"
        }], [{
            text: "第十八行第一列"
        }, {
            text: "第十八行第二列"
        }, {
            text: "第十八行第三列"
        }]];
        var excelview = BI.createWidget({
            type: "bi.excel_view",
            items: items,
            width: 200,
            height: 25
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: excelview,
                left: 200,
                top: 100
            }]
        })
    }
});

ExcelVModel = BI.inherit(BI.Model, {});