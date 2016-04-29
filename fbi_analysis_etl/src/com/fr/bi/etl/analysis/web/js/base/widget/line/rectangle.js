BI.Rectangle  = BI.inherit(BI.Widget, {


    _defaultConfig: function () {
        return BI.extend(BI.Rectangle.superclass._defaultConfig.apply(this, arguments), {
            baseCls:"bi-svg-rectangle",
            move:true,
            gap:5,
            line:8,
            height:null,
            width:null,
            lineWidth:3,
            stroke: "#009de3",
            fill:"white",
            time:100,
            step: 1,
            container:null,
            drag:null,
            dragWidth: 24,
            dragHeight :24,
            dragLineCount:6
        })
    },

    _init: function () {
        BI.Rectangle.superclass._init.apply(this, arguments);
        var o = this.options;
        if(BI.isNull(o.container)){
            o.container = this.element;
        }
        o.container.addClass(o.baseCls);
        this.drag = BI.createWidget({
            type:"bi.drag_svg_button",
            width: o.dragWidth,
            height: o.dragHeight,
            lineCount: o.dragLineCount,
            stroke: o.stroked,
            drag: o.drag,
            fill : o.fill
        })
        this.north = BI.createWidget({
            type:"bi.horizontal_line",
            move: o.move,
            gap: o.gap,
            line: o.line,
            height: o.lineWidth,
            width: o.width,
            stroke: o.stroked,
            fill : o.fill,
            step: o.step,
            time: o.time
        })
        this.south = BI.createWidget({
            type:"bi.horizontal_line",
            move: o.move,
            gap: o.gap,
            line: o.line,
            height: o.lineWidth,
            width: o.width,
            stroke: o.stroked,
            fill : o.fill,
            step: 0 - o.step,
            time: o.time
        })
        this.east = BI.createWidget({
            type:"bi.vertical_line",
            move: o.move,
            gap: o.gap,
            line: o.line,
            height: o.height,
            width: o.lineWidth,
            stroke: o.stroked,
            fill : o.fill,
            step:  o.step,
            time: o.time
        })
        this.west = BI.createWidget({
            type:"bi.vertical_line",
            move: o.move,
            gap: o.gap,
            line: o.line,
            height: o.height,
            width: o.lineWidth,
            stroke: o.stroked,
            fill : o.fill,
            step: 0 -  o.step,
            time: o.time
        })
        this.layout = BI.createWidget({
            type:"bi.absolute",
            element : o.container,
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
        this.setMove(o.move)
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

    stroke : function(w, h, t, l) {
        var o = this.options;
        if(o.width === w && o.height === h && o.top === t && o.left === l) {
            return;
        }
        this.setWidth(w);
        this.setHeight(h)
        this.setPosition(t, l);
        this.rePaint()
    },

    reInitDrag : function () {
      this.drag.reInitDrag();
    },

    moveLeft : function (l) {
        if(l === 0) {
            return;
        }
        this.options.left += l;
        this.rePaint();
    },

    rePaint : function () {
        var o = this.options;
        var top = o.top;
        var left = o.left;
        this._setMove(top > -9999 && left > -9999 && this.options.move);
        var items = [{
            el : this.north,
            top : top,
            left : left
        }, {
            el : this.drag,
            top: top,
            left:left
        }, {
            el : this.south,
            top : top + o.height- o.lineWidth,
            left : left
        }, {
            el : this.east,
            top : top,
            left:left + o.width
        }, {
            el : this.west,
            top : top,
            left:left
        }]
        this.layout.populate(items);
    },

    setPosition : function(top, left) {
        var o = this.options;
        o.top = top;
        o.left = left;
    },

    _setMove : function(move) {

        this.south.setMove(move);
        this.north.setMove(move);
        this.east.setMove(move);
        this.west.setMove(move);
    },

    setMove:function(move) {
        this.options.move = move;
        this._setMove(move)
    },


    hide:function () {
        this.stroke(0,0,-9999,-9999);
    }


})
$.shortcut("bi.rectangle", BI.Rectangle);