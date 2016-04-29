BI.DragButton = FR.extend(BI.Widget, {
    _defaultConfig: function() {
        var conf = BI.DragButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            tagName: "div",
            baseCls: (conf.baseCls || "") + ETLCst.ANALYSIS_DRAG_CLASS + " b-font horizon-center",
            height : 30,
            width : 30,
            lineCount:5,
            stroke: "red",
            fill:"white",
            drag : null
        })
    },


    _init : function() {
        BI.DragButton.superclass._init.apply(this, arguments);
        var o = this.options;
        var svg = BI.createWidget({
            type:"bi.svg",
            element:this.element,
            height: o.height,
            width: o.width
        })
        var path = "";
        var h_step = o.height/ o.lineCount;
        var w_step = o.width/ o.lineCount;
        for(var i = 0; i < o.lineCount; i++){
            path +="M0," +(h_step * (i + 1));
            path +="L" + (w_step * (i + 1)) +",0"
        }
        svg.path(path).attr({
            stroke: o.stroke,
            fill: o.fill
        })
        this.reInitDrag();
    },

    reInitDrag : function () {
        var o = this.options;
        if(BI.isNotNull(o.drag)) {
            this.element.draggable(o.drag)
        }
    }
});

$.shortcut("bi.drag_svg_button", BI.DragButton);