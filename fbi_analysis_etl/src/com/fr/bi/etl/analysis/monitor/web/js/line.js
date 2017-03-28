BI.MonitorLine = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.MonitorLine.superclass._defaultConfig.apply(this, arguments), {
            baseCls:"bi-monitor-svg-line",
            status:1,
            direction:0,
            x : 0,
            y : 0,
            width : 100,
            height : 100
        })
    },

    _init : function () {
        BI.MonitorLine.superclass._init.apply(this, arguments);
        var o = this.options;
        var cls = o.baseCls + "-" + o.status;
        this.element.addClass(cls);
        if(o.generating){
            this.element.addClass(o.baseCls + "-generate");
        }
        o.height = Math.max(5, o.height);
        o.width = Math.max(5, o.width);
        this.svg = BI.createWidget({
            type:"bi.svg",
            element: this,
            height: o.height,
            width: o.width
        })
        var path = this._createPath();
        this.svg.path(path);
    },

    addHover : function () {
        var o = this.options;
        this.element.addClass(o.baseCls + "-hover")
        BI.each(o.tables, function (idx, item) {
            item.addHover();
        })
    },

    removeHover : function () {
        var o = this.options;
        this.element.removeClass(o.baseCls + "-hover");
        BI.each(o.tables, function (idx, item) {
            item.removeHover();
        })
    },

    _createPath : function () {
        var o = this.options;
        if(o.direction === 0) {
            return "M" + o.x+","+o.y+"C" + (o.x + o.w)/2 +","+o.y + " " + (o.x + o.w)/2 + ","+  o.h + " "+o.w + "," + o.h;
        } else {
            return "M" + o.x+","+o.y+"C" + o.x +","+(o.y+o.h)/2 + " " + o.w + ","+  +(o.y+o.h)/2  + " "+o.w + "," + o.h;
        }
    }

})
BI.shortcut("bi.monitor_line", BI.MonitorLine);