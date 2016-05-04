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
                [{"region":"T4","text":"Name","value":"6aece09515d96690Name"},
                    {"region":"T1","text":"ID","value":"3e9bd22b517429f7ID","direction":-1},
                    {"region":"T3","text":"ID","value":"da331a613ae22505ID","direction":-1}],
                [{"region":"T4","text":"Name","value":"6aece09515d96690Name"},
                    {"region":"T1","text":"ID","value":"3e9bd22b517429f7ID","direction":-1},
                    {"region":"T2","text":"ID","value":"a6da3db624125d37ID","direction":-1},
                    {"region":"T3","text":"ID","value":"da331a613ae22505ID","direction":-1}]
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