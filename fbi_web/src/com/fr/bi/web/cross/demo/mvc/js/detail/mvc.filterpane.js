/**
 * Created by young on 15/8/31.
 */
FilterPaneView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(FilterPaneView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-down-list-combo bi-mvc-layout"
        })
    },
    _init: function () {
        FilterPaneView.superclass._init.apply(this, arguments);
    },

    _createFilterPane: function () {
        var filter = BI.createWidget({
            type: "bi.filter_pane",
            items: [{
                id: "3beb41be9c67d80d",
                value: 81,
                children: [{
                    id: 1,
                    type: "bi.label",
                    value: "节点1"
                }, {
                    id: "74cf470c15a7cb23",
                    value: 80,
                    children: [{
                        id: 2,
                        type: "bi.label",
                        value: "节点2"
                    }, {
                        id: 3,
                        type: "bi.label",
                        value: "节点3"
                    }]
                }, {
                    id: 4,
                    type: "bi.label",
                    value: "节点4"
                }]
            }]
        });

        return filter;
    },

    _render: function (vessel) {

        var filter = this._createFilterPane();

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 30,
            vgap: 20,
            items: [{
                el: {
                    type: "bi.left",
                    items: [filter]
                }
            }, {
                type: "bi.button",
                text: "过滤结构getValue()",
                height: 30,
                handler: function () {
                    BI.Msg.alert("过滤结构", JSON.stringify(filter.getValue()));
                }
            }]
        })
    }
});
FilterPaneModel = BI.inherit(BI.Model, {});