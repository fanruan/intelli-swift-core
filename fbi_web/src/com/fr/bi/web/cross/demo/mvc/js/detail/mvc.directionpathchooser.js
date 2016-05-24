DirectionPathChooserView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(DirectionPathChooserView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-direction-path-chooser bi-mvc-layout"
        })
    },

    _init: function () {
        DirectionPathChooserView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var a = [
            [
                {
                    "region": "合同信息",
                    "text": "客户ID",
                    "value": "defa1f7ba8b2684a客户ID"
                }, {
                    "region": "客户信息",
                    "text": "主键",
                    "value": "1f4711c201ef1842",
                    "direction": -1
                }, {
                    "region": "合同的回款信息",
                    "text": "合同ID",
                    "value": "e351e9f1d8147947合同ID",
                    "direction": -1
        }]];
        var pathchooser = BI.createWidget({
            type: "bi.direction_path_chooser",
            width: 800,
            height: 400,
            items:  [
                [
                    {
                        "region": "合同信息",
                        "text": "客户ID",
                        "value": "defa1f7ba8b2684a客户ID"
                    }, {
                    "region": "客户信息",
                    "text": "主键",
                    "value": "1f4711c201ef1842",
                    "direction": -1
                }, {
                    "region": "合同的回款信息",
                    "text": "合同ID",
                    "value": "e351e9f1d8147947合同ID",
                    "direction": -1
                }]]
        });
        pathchooser.setValue();
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: pathchooser,
                left: 100,
                top: 100
            }, {
                el: {
                    type: "bi.button",
                    text: "getValue",
                    height: 30,
                    handler: function () {
                        BI.Msg.toast(JSON.stringify(pathchooser.getValue()));
                    }
                },
                left: 100,
                bottom: 10
            }, {
                el: {
                    type: "bi.button",
                    text: "setValue",
                    height: 30,
                    handler: function () {
                        pathchooser.setValue(['学号1', 'IDA1', 'IDA']);
                    }
                },
                left: 300,
                bottom: 10
            }]
        })
    }
});

DirectionPathChooserModel = BI.inherit(BI.Model, {});