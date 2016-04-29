LeftRightVerticalAdaptView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(LeftRightVerticalAdaptView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-left-right-vertical-adapt bi-mvc-layout"
        })
    },

    _init: function () {
        LeftRightVerticalAdaptView.superclass._init.apply(this, arguments);
    },

    _createLayout: function () {
        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            lhgap: 10,
            rhgap: 30,
            items: {
                left: [{
                    type: "bi.label",
                    text: "左边的垂直居中",
                    cls: "layout-bg1",
                    width: 100,
                    height: 30
                }, {
                    type: "bi.label",
                    text: "左边的垂直居中",
                    cls: "layout-bg2",
                    width: 100,
                    height: 30
                }],
                right: [{
                    type: "bi.label",
                    text: "右边的垂直居中",
                    cls: "layout-bg1",
                    width: 100,
                    height: 30
                }, {
                    type: "bi.label",
                    text: "右边的垂直居中",
                    cls: "layout-bg2",
                    width: 100,
                    height: 30
                }]
            }
        })
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.grid",
            element: vessel,
            columns: 1,
            rows: 1,
            items: [{
                column: 0,
                row: 0,
                el: this._createLayout()
            }]
        })
    }
});

LeftRightVerticalAdaptModel = BI.inherit(BI.Model, {});