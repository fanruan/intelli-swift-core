BI.HorizontalLine = BI.inherit(BI.Widget, {


    _defaultConfig: function () {
        return BI.extend(BI.HorizontalLine.superclass._defaultConfig.apply(this, arguments), {
            baseCls:"bi-svg-line-horizontal",
            move:true,
            gap:5,
            line:8,
            height:3,
            width:200,
            stroke: "red",
            fill:"white",
            time:100,
            step: 1
        })
    },

    _init: function () {
        BI.HorizontalLine.superclass._init.apply(this, arguments);
        var o = this.options;
        this.svg = BI.createWidget({
            type:"bi.svg",
            element:this.element,
            height: o.height,
            width: o.width
        })
        this.setMove(o.move)
    },

    setLength : function (w) {
        if(w === this.options.width){
            return;
        }
        BI.HorizontalLine.superclass.setWidth.apply(this, arguments);
        var o = this.options;
        this.setMove(o.move)
    },


    setMove:function(move) {
        this.options.move = move
        var o = this.options;
        var self = this;
        clearInterval(this.timer)
        this.timer = null;
        var tempPath =  {}
        if(move === false) {
            self.svg.clear();
            this.svg.path(this._createPath(0))
        } else {
            var i = 0;
            var per = o.line + o.gap;
            this.timer = setInterval(function(e){
                self.svg.clear();
                var index = (i+= o.step) % per
                var path = tempPath[index];
                if(BI.isUndefined(path)) {
                    path =  self._createPath(index)
                    tempPath[index] = path;
                }
                self.svg.path(path)
            }, o.time)
        }
    },



    _createPath : function (startPos) {
        var o = this.options;
        var path ="";
        for(var j = 0; j < o.height; j++) {
            for(var i = startPos - o.line; i < o.width; i+= o.line) {
                path +="M" + (i + j)+ ","+ j
                path +="L" + (i + j + o.line) + "," + j
                i+= o.gap
            }
        }
        return path;
    }

})
$.shortcut("bi.horizontal_line", BI.HorizontalLine);