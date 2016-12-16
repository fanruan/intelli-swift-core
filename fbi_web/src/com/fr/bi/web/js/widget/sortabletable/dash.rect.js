/**
 * 列hover时的虚线框
 */
BI.DashRect = BI.inherit(BI.Widget, {

    _const: {
        perColumnSize: 100,
        dragButtonWidth: 24,
        dragButtonHeight: 24,
        lineCount: 6
    },

    _defaultConfig: function () {
        return BI.extend(BI.DashRect.superclass._defaultConfig.apply(this, arguments), {
            baseCls:"bi-dash-react",
            gap:5,
            height:null,
            width:null,
            lineWidth: 3,
            drag:null
        })
    },

    _init: function () {
        BI.DashRect.superclass._init.apply(this, arguments);
        var o = this.options, c = this._const;
        this.drag = BI.createWidget({
            type: "bi.triangle_drag_button",
            cls: "drag-header",
            width: c.dragButtonWidth,
            height: c.dragButtonHeight,
            lineCount: c.lineCount,
            drag: o.drag
        })
        this.north = BI.createWidget({
            type:"bi.horizontal_line",
            gap: o.gap,
            height: o.lineWidth,
            width: o.width
        })
        this.south = BI.createWidget({
            type:"bi.horizontal_line",
            gap: o.gap,
            height: o.lineWidth,
            width: o.width
        })
        this.east = BI.createWidget({
            type:"bi.vertical_line",
            gap: o.gap,
            height: o.height,
            width: o.lineWidth,
        })
        this.west = BI.createWidget({
            type:"bi.vertical_line",
            gap: o.gap,
            height: o.height,
            width: o.lineWidth
        })
        this.layout = BI.createWidget({
            type:"bi.absolute",
            element : this.element,
            items : [{
                el : this.north,
                top : 0
            }, {
                el : this.drag,
                top : 0,
                left : 0
            }, {
                el : this.south,
                bottom :0
            }, {
                el : this.east,
                right:0
            }, {
                el : this.west,
                left:0
            }]
        })
        this.setMove()
    },

    setMove: function(){
        this.south.setMove();
        this.north.setMove();
        this.east.setMove();
        this.west.setMove();
    },

    setWidth : function (w) {
        var o = this.options;
        o.width = w;
        this.north.setLength(w);
        this.south.setLength(w);
    },

    setHeight : function (w) {
        var o = this.options;
        o.height = w;
        this.east.setLength(w - o.lineWidth);
        this.west.setLength(w - o.lineWidth);
    },

    reInitDrag : function () {
        this.drag.reInitDrag();
    }
})
$.shortcut("bi.dash_react", BI.DashRect);