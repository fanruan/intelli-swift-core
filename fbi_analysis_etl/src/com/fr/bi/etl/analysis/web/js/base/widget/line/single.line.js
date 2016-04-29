BI.SingleLine  = BI.inherit(BI.Widget, {


    _defaultConfig: function () {
        return BI.extend(BI.SingleLine.superclass._defaultConfig.apply(this, arguments), {
            baseCls:"bi-svg-single-line",
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
            direction : 0,
            len : 300
        })
    },

    _init: function () {
        BI.SingleLine.superclass._init.apply(this, arguments);
        var o = this.options;
        if(BI.isNull(o.container)){
            o.container = this.element;
        }
        o.container.addClass(o.baseCls);
        this.line = BI.createWidget({
            type: o.direction === 0  ? "bi.horizontal_line" : "bi.vertical_line",
            move: o.move,
            gap: o.gap,
            line: o.line,
            height: o.direction === 0 ? o.lineWidth : o.len,
            width: o.direction === 0 ? o.len : o.lineWidth ,
            stroke: o.stroked,
            fill : o.fill,
            step: o.step,
            time: o.time,
            cls: o.cls
        })
        this.layout = BI.createWidget({
            type:"bi.absolute",
            element : o.container,
            items : [{
                el : this.line,
                top : 0
            }]
        })
        this.setMove(o.move)
    },

    setLen : function (l) {
        var o = this.options;
        o.len = l;
        this.line.setLength(l);
    },

    stroke : function(len, t, l) {
        var o = this.options;
        if(o.len === len && o.top === t && o.left === l) {
            return;
        }
        this.setLen(len);
        this.setPosition(t, l);
        this.rePaint()
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
            el : this.line,
            top : top,
            left : left
        }]
        this.layout.populate(items);
    },

    setPosition : function(top, left) {
        var o = this.options;
        o.top = top;
        o.left = left;
    },

    _setMove : function(move) {
        this.line.setMove(move);
    },

    setMove:function(move) {
        this.options.move = move;
        this._setMove(move)
    },


    hide:function () {
        this.stroke(0,-9999,-9999);
    }


})
$.shortcut("bi.single_line", BI.SingleLine);