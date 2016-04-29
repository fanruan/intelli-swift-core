BroadcastView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BroadcastView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-broadcast mvc-layout"
        })
    },

    _init: function () {
        BroadcastView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        var button_group = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BI.deepClone(ITEMS), {
                type: "bi.multi_select_item"
            }),
            chooseType: BI.Selection.Multi,
            layouts: [{
                type: "bi.vertical"
            }]
        });
        button_group.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            BI.Broadcasts.send(BICst.BROADCAST.TEST, this.getValue());
        });
        var button = BI.createWidget({
            type: "bi.button",
            text: "这个按钮当不选中时灰化",
            handler: function () {
                BI.Msg.alert("", JSON.stringify(button_group.getValue()));
            }
        });
        BI.Broadcasts.on(BICst.BROADCAST.TEST, function (items) {
            button.setEnable(!BI.isEmpty(items))
        });
        BI.Broadcasts.send(BICst.BROADCAST.TEST, button_group.getValue());

        BI.createWidget({
            type: "bi.grid",
            element: vessel,
            items: [[{
                el: button_group
            }], [{
                el: {
                    type: "bi.vertical",
                    items: [button]
                }
            }]]
        })
    }
});

BroadcastModel = BI.inherit(BI.Model, {});