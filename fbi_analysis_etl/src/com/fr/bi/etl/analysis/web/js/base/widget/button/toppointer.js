BI.TopPointer = FR.extend(BI.Widget, {
    _defaultConfig: function() {
        var conf = BI.TopPointer.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            tagName: "div",
            baseCls: (conf.baseCls || "") + " x-pointer-top b-font horizon-center",
            height : 20,
            width : 10,
            stroke: "red",
            fill:"white"
        })
    },


    _init : function() {
        BI.TopPointer.superclass._init.apply(this, arguments);
        var o = this.options;
        var svg = BI.createWidget({
            type:"bi.svg",
            element:this.element,
            height: o.height,
            width: o.width
        })
        var path="M0," + o.height+"L"+ o.width/2 +",0L" + o.width +"," + o.height;
        svg.path(path).attr({
            stroke: o.stroke,
            fill: o.fill
        })
    }
});

$.shortcut("bi.top_pointer", BI.TopPointer);