Collection_View = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(Collection_View.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-collection-view bi-mvc-layout"
        })
    },

    _init: function () {
        Collection_View.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var items = [];
        var cellCount = 100;
        for (var i = 0; i < cellCount; i++) {
            items[i] = {
                type: "bi.label",
                text: i
            };
        }
        var grid = BI.createWidget({
            type: "bi.collection_view",
            items: items,
            cellSizeAndPositionGetter: function (index) {
                return {
                    x: index % 10 * 50,
                    y: Math.floor(index / 10) * 50,
                    width: 50,
                    height: 50
                }
            }
        });
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: grid,
                left: 10,
                right: 10,
                top: 10,
                bottom: 10
            }]
        })
    }
});

Collection_Model = BI.inherit(BI.Model, {});