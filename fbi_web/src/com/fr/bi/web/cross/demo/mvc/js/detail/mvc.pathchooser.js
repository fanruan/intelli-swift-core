PathChooserView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(PathChooserView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-path-chooser bi-mvc-layout"
        })
    },

    _init: function () {
        PathChooserView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var pathchooser = BI.createWidget({
            type: "bi.path_chooser",
            width: 800,
            height: 400,
            items: //    [
            //    [{region: "区域X", value: "X1"},
            //        {region: "区域Q", value: "Q"},
            //        {region: "区域A", value: "A"},
            //        {region: "区域B", value: "B"},
            //        {region: "区域D", value: "D"},
            //        {region: "区域E", value: "E"},
            //        {region: "区域G", value: "G"},
            //        {region: "区域I", value: "I"},
            //        {region: "区域J", value: "J"}],
            //    [{region: "区域X", value: "X"},
            //        {region: "区域Q", value: "Q"},
            //        {region: "区域A", value: "A"},
            //        {region: "区域B", value: "B"},
            //        {region: "区域C", value: "C"},
            //        {region: "区域D", value: "D"},
            //        {region: "区域E", value: "E"},
            //        {region: "区域G", value: "G"},
            //        {region: "区域I", value: "I"},
            //        {region: "区域J", value: "J"}],
            //    [{region: "区域X", value: "X"},
            //        //{region: "区域Q", value: "Q"},
            //        {region: "区域A", value: "A"},
            //        {region: "区域C", value: "C"},
            //        {region: "区域D", value: "D"},
            //        {region: "区域E", value: "E"},
            //        {region: "区域G", value: "G"},
            //        {region: "区域I", value: "I"},
            //        {region: "区域J", value: "J"}],
            //    [{region: "区域X", value: "X"},
            //        {region: "区域Q", value: "Q"},
            //        {region: "区域A", value: "A"},
            //        {region: "区域B", value: "B"},
            //        {region: "区域D", value: "D"},
            //        {region: "区域E", value: "E1"},
            //        {region: "区域H", value: "H"},
            //        {region: "区域I", value: "I"},
            //        {region: "区域J", value: "J"}],
            //    [{region: "区域X", value: "X"},
            //        {region: "区域Q", value: "Q"},
            //        {region: "区域A", value: "A"},
            //        {region: "区域B", value: "B"},
            //        {region: "区域C", value: "C"},
            //        {region: "区域D", value: "D"},
            //        {region: "区域E", value: "E1"},
            //        {region: "区域H", value: "H"},
            //        {region: "区域I", value: "I"},
            //        {region: "区域J", value: "J"}],
            //    [{region: "区域X", value: "X"},
            //        {region: "区域Q", value: "Q"},
            //        {region: "区域A", value: "A"},
            //        {region: "区域C", value: "C"},
            //        {region: "区域D", value: "D"},
            //        {region: "区域E", value: "E1"},
            //        {region: "区域H", value: "H"},
            //        {region: "区域I", value: "I"},
            //        {region: "区域J", value: "J"}],
            //    [{region: "区域X", value: "X"},
            //        {region: "区域Q", value: "Q"},
            //        {region: "区域A", value: "A"},
            //        {region: "区域B", value: "B"},
            //        {region: "区域D", value: "D"},
            //        {region: "区域F", value: "F"},
            //        {region: "区域H", value: "H"},
            //        {region: "区域I", value: "I"},
            //        {region: "区域J", value: "J"}],
            //    [{region: "区域X", value: "X"},
            //        {region: "区域Q", value: "Q"},
            //        {region: "区域A", value: "A"},
            //        {region: "区域B", value: "B"},
            //        {region: "区域C", value: "C"},
            //        {region: "区域D", value: "D"},
            //        {region: "区域F", value: "F"},
            //        {region: "区域H", value: "H"},
            //        {region: "区域I", value: "I"},
            //        {region: "区域J", value: "J"}],
            //    [{region: "区域X", value: "X", text: "X"},
            //        {region: "区域Q", value: "Q", text: "Q"},
            //        {region: "区域A", value: "A", text: "A"},
            //        {region: "区域C", value: "C", text: "C"},
            //        {region: "区域D", value: "D", text: "D"},
            //        {region: "区域F", value: "F", text: "F"},
            //        {region: "区域H", value: "H", text: "H"},
            //        {region: "区域I", value: "I", text: "I"},
            //        {region: "区域J", value: "J", text: "J"}]
            //]
                [
                    [{region: "区域A3", value: "学号"},
                        {region: "区域A2", value: "IDA2", text: "ID"},
                        {region: "区域A1", value: "IDA1", text: "ID"},
                        {region: "区域A", value: "IDA", text: "ID"}],
                    [{region: "区域A3", value: "学号"},
                        {region: "区域A1", value: "IDA1", text: "ID"},
                        {region: "区域A", value: "IDA", text: "ID"}],
                    [{region: "区域A3", value: "学号1"},
                        {region: "区域A1", value: "IDA1", text: "ID"},
                        {region: "区域A", value: "IDA", text: "ID"}]
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
                    handler: function () {
                        BI.Msg.toast(JSON.stringify(pathchooser.getValue()));
                    }
                },
                left: 100,
                bottom: 10
            }]
        })
    }
});

PathChooserModel = BI.inherit(BI.Model, {});