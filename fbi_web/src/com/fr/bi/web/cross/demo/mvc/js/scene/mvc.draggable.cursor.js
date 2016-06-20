/**
 * Created by Young's on 2016/6/15.
 */
DraggableCursorView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(DraggableCursorView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-broadcast mvc-layout"
        })
    },

    _init: function () {
        DraggableCursorView.superclass._init.apply(this, arguments);
    },
    
    _render: function(vessel){
        var drag = BI.createWidget({
            type: "bi.label",
            width: 100,
            height: 30,
            text: "dragging"
        });
        drag.element.draggable({
            cursor: "move",
            cursorAt: {left: 10, top: 10}
        });
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: drag,
                top: 100,
                left: 100
            }]
        })
    }
});

DraggableCursorModel = BI.inherit(BI.Model, {
    
});