/**
 * Created by Young's on 2016/6/20.
 */
BI.DragIconCombo = BI.inherit(BI.Widget, {
    
    constants: {
        valueMore: -100
    },
    
    _defaultConfig: function(){
        return BI.extend(BI.DragIconCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-drag-icon-combo"
        })
    },
    
    _init: function(){
        BI.DragIconCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var item = o.item;
        var el = BI.extend(item, {
            type: "bi.icon_button",
            height: 30,
            width: 36
        });
        if (item.value !== this.constants.valueMore) {
            el.type = "bi.drag_icon_button";
            el.drag = o.drag;
            el.stop = o.stop;
            el.helper = o.helper;
        }
        var childIcons = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(item.children, {
                type: "bi.drag_icon_button",
                height: 30,
                width: 36,
                drag: o.drag,
                stop: o.stop,
                helper: o.helper
            }),
            layouts: [{
                type: "bi.vertical"
            }, {
                type: "bi.center_adapt",
                height: 40
            }]
        });
        this.dragCombo = BI.createWidget({
            type: "bi.icon_combo",
            element: this.element,
            direction: "right",
            adjustLength: -1,
            height: 30,
            width: 36,
            minWidth: 50,
            maxWidth: 50,
            el: {
                el: el
            },
            popup: childIcons
        });
    },
    
    setEnable: function(v){
        this.dragCombo.setEnable(v);
    }
});
$.shortcut("bi.drag_icon_combo", BI.DragIconCombo);