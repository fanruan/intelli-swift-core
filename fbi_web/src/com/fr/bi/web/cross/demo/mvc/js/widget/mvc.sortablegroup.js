SortableGroupView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SortableGroupView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-skip-pager bi-mvc-layout"
        })
    },

    _init: function () {
        SortableGroupView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var group = BI.createWidget({
            type: "bi.sortable_group",
            height: 200,
            items: BI.createItems([{
                value: 1
            }, {
                value: 2
            }, {
                value: 3
            }], {
                type: "bi.button"
            })
        });
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 200,
            vgap: 50,
            items: [group, {
                type: "bi.button",
                value: "add",
                handler: function(){
                    group.addItems([{
                        type: "bi.button",
                        value: BI.parseInt(Math.random() * 13) % 10
                    }])
                }
            }, {
                type: "bi.button",
                value: "getValue",
                handler: function(){
                    BI.Msg.alert("", group.getValue());
                }
            }, {
                type: "bi.horizontal_line",
                time: 10,
                lineWidth: 2,
                step: 1,
                move: true,
                direction: 1,
                //container: this.table.element,
                len: 0
            }]
        })
    }
});

SortableGroupModel = BI.inherit(BI.Model, {});