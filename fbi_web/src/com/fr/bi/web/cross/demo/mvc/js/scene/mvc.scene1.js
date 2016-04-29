//set、get函数
Scene1View = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(Scene1View.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-scene1 mvc-layout"
        })
    },

    _createSorted: function () {
        var items = [{value: BI.random(0, 1000)},
            {value: BI.random(0, 1000)},
            {value: BI.random(0, 1000)},
            {value: BI.random(0, 1000)},
            {value: BI.random(0, 1000)},
            {value: BI.random(0, 1000)},
            {value: BI.random(0, 1000)},
            {value: BI.random(0, 1000)}];

        return BI.createWidget({
            type: "bi.sort_list",
            width: "100%",
            height: "100%",
            itemsCreator: function (op, callback) {
                callback(BI.createItems(items, {
                    height: 30,
                    cls: "mvc-border"
                }));
            },
            el: {
                layouts: [{
                    type: "bi.vertical",
                    scrolly: true,
                    hgap: 10,
                    vgap: 5
                }]
            },
            hasNext: function () {
                return true;
            }
        })
    },

    _init: function () {
        Scene1View.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;
        this.sort1 = this._createSorted();
        this.sort2 = this._createSorted();

        BI.createWidget({
            type: "bi.left",
            hgap: 50,
            vgap: 50,
            element: vessel,
            items: [{
                type: "bi.default",
                width: 300,
                height: 200,
                items: [this.sort1]
            }, {
                type: "bi.default",
                width: 300,
                height: 200,
                items: [this.sort2]
            }, {
                type: "bi.button",
                height: 25,
                text: "getValue",
                handler: function () {
                    BI.Msg.alert("", JSON.stringify(self.sort1.getSortedValues()));
                }
            }]
        })
    }
})

Scene1Model = BI.inherit(BI.Model, {})