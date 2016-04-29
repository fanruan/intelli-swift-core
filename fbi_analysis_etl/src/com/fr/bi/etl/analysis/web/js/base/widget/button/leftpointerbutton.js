/**
 * @class BI.LeftPointerButton
 * @extends BI.BasicButton
 * 图标的button
 */
BI.LeftPointerButton = FR.extend(BI.BasicButton, {

    _defaultConfig: function () {
        var conf = BI.LeftPointerButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-left-pointer-button",
            pointerWidth:10,
            width : 200,
            height: 20,
            forceNotSelected:true,
            iconCls : "icon-add"
        })
    },

    _switchOptions : function() {
        var o = this.options;
        o.title = o.text;
        o.shadow = this._createShadowProfile();
    },

    _createShadowProfile : function (){
        var o = this.options;
        var pointer =  BI.createWidget({
            type: "bi.center_adapt",
            width: o.pointerWidth,
            height: o.height,
            items: [{
                el: {
                    type: "bi.left_pointer",
                    width: o.pointerWidth,
                    height: o.height
                }
            }]
        })

        var icon_text = BI.createWidget({
            type:"bi.layout",
            cls : "bi-button-mask",
            width:o.width - o.pointerWidth,
            height: o.height
        })

       var mask =  BI.createWidget({
            type:"bi.left",
            width:o.width,
            height: o.height,
            items:[pointer, icon_text]
        })

        return {
            items : [mask],
            type:"bi.center_adapt",
            cls: "bi-pointer-button-mask"
        }
    },

    _init: function () {
        this._switchOptions();
        BI.LeftPointerButton.superclass._init.apply(this, arguments);
        var o = this.options;
        BI.createWidget({
            type:"bi.absolute",
            element: this.element,
            width:o.width,
            height: o.height,
            items:[{
                el: this._createBackground(),
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }, {
                el:this._createButton(),
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }]
        })
    },

    _createButton : function () {
        var o = this.options;
        var text = BI.createWidget({
            type: "bi.label",
            cls: "list-item-text",
            textAlign: "left",
            text: o.text,
            height: o.height
        })
        text.element.css("max-width", o.width - o.pointerWidth - o.height + "px")
        var icon = BI.createWidget({
            type:"bi.center_adapt",
            cls : o.iconCls,
            height: o.height,
            items : [BI.createWidget ({
                type : "bi.icon"
            }), text]
        })
        return BI.createWidget({
            type:"bi.float_center_adapt",
            height: o.height,
            width: o.width,
            items:[icon]
        })
    },

    _createBackground : function(){
        var o = this.options;
        var right_back_ground = BI.createWidget({
            type:"bi.layout",
            cls:"bi-pointer-button-background",
            width:o.width - o.pointerWidth,
            height: o.height
        })
        var pointer = BI.createWidget({
            type: "bi.center_adapt",
            width: o.pointerWidth,
            height: o.height,
            items: [{
                el: {
                    type: "bi.left_pointer",
                    width: o.pointerWidth,
                    height: o.height
                }
            }]
        })

        return BI.createWidget({
            type:"bi.left",
            width:o.width,
            height: o.height,
            items:[pointer, right_back_ground]
        })
    }

});
$.shortcut("bi.left_pointer_button", BI.LeftPointerButton);