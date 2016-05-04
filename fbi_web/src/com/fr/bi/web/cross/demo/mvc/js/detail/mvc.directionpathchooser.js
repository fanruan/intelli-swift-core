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
        var pathchooser = BI.createWidget({
            type: "bi.direction_path_chooser",
            width: 800,
            height: 400,
            items: [
                    [{region: "区域A3", value: "学号"},

                        {region: "区域A1", value: "IDA1", text: "ID"},
                        {region: "区域A2", value: "IDA2", text: "ID"},
                        {region: "区域A", value: "IDA", text: "ID"}],
                    [{region: "区域A3", value: "学号1", direction: -1},
                        {region: "区域A1", value: "IDA1", text: "ID", direction: -1},
                        {region: "区域A", value: "IDA5", text: "ID5"}]
            ]
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