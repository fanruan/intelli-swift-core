BI.VerticalLine = BI.inherit(BI.Widget, {


    _defaultConfig: function () {
        return BI.extend(BI.VerticalLine.superclass._defaultConfig.apply(this, arguments), {
            baseCls:"bi-svg-line-vertical",
            move:true,
            gap:5,
            line:8,
            height:200,
            width:3,
            stroke: "red",
            fill:"white",
            time:100,
            step: 1
        })
    },

    _init: function () {
        BI.VerticalLine.superclass._init.apply(this, arguments);
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
        if(w === this.options.height){
            return;
        }
        BI.HorizontalLine.superclass.setHeight.apply(this, arguments);
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
        for(var j = 0; j < o.width; j++) {
            for(var i = startPos - o.line; i < o.height; i+= o.line) {
                path +="M"+ j + "," + (i + j)
                path +="L" + j + "," + (i + j + o.line)
                i+= o.gap
            }
        }
        return path;
    }

})
$.shortcut("bi.vertical_line", BI.VerticalLine);