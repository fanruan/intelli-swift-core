ExcelTableView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ExcelTableView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-excel-table bi-mvc-layout"
        })
    },

    _init: function () {
        ExcelTableView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var items = [[{
            text: "第一行第一列"
        }, {
            text: "第一行第一列"
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

        var table1 = BI.createWidget({
            type: "bi.excel_table",
            columnSize: ["", "", ""],
            items: items,
            isNeedMerge: true,
            mergeCols: [0, 1, 2]
        });
        var popup = BI.createWidget({
            type: "bi.popup_view",
            logic: {
                dynamic: false
            },
            stopPropagation: true,
            el: table1
        });
        popup.element.css({"top": "50px", left: "50px", bottom: "50px", right: "50px"});
        var combo = BI.createWidget({
            type: "bi.combo",
            isNeedAdjustWidth: false,
            adjustLength: 20,
            el: {
                type: "bi.button",
                text: "打开EXCEl表",
                height: 25
            },
            popup: popup,
            direction: "left,custom"
        });
        combo.on(BI.Combo.EVENT_AFTER_POPUPVIEW, function () {
            table1.resizeHeader();
        });
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: combo,
                left: 50,
                top: 100
            }]
        })
    }
});

ExcelTableModel = BI.inherit(BI.Model, {});