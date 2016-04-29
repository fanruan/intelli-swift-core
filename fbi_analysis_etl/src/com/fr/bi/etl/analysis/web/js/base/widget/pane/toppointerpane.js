

BI.TopPointerPane = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.TopPointerPane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-analysis-etl-top-pointer-pane bi-animate-pointer-height-resize",
            contentItem :  {
                type:"bi.label",
                text:"help!!!"
            },
            pointerCls:"",
            height:300,
            pointerAreaHeight:10,
            pointerHeight:7,
            pointerWidth:30
        })
    },


    _init: function () {
        BI.TopPointerPane.superclass._init.apply(this, arguments)
        var o = this.options;

        this.pointer = BI.createWidget({
            type:"bi.top_pointer",
            cls : o.pointerCls,
            height: o.pointerHeight,
            width: o.pointerWidth
        })


        BI.createWidget({
            type:"bi.vertical",
            element:this.element,
            scrollable:false,
            scrollx:false,
            scrolly:false,
            items:[{
                type:"bi.vertical",
                scrollable:false,
                cls:"top-pointer",
                scrollx:false,
                scrolly:false,
                items : [{
                    type:"bi.absolute",
                    items:[this.pointer],
                    left:0,
                    top:o.pointerAreaHeight - o.pointerHeight
                }],
                height: o.pointerAreaHeight
            },BI.extend(o.contentItem, {
                height: o.height,
                cls:'bi-analysis-etl-top-pointer-pane-item'
            })],
            height: o.height + o.pointerHeight + 2
        })

    },


    show : function(pointerPos) {
        this.pointer.element.css({
            left:pointerPos + "px"
        })
        this.setVisible(true);
    },

    hide : function(){
        this.setVisible(false)
    }

})
$.shortcut("bi.top_pointer_pane", BI.TopPointerPane);