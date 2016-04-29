/**
 * @class BI.ConfNumberIntervalCustomGroupItem
 * @extend BI.Widget
 */

BI.ConfNumberIntervalCustomGroupItem = BI.inherit(BI.Single,{

    constants:{
        hgap:5,
        editorWidth:155,
        rgap:10,
        iconSize:30,
        left:20
    },

    _defaultConfig:function(){
        return BI.extend(BI.ConfNumberIntervalCustomGroupItem.superclass._defaultConfig.apply(this,arguments),{
            extraCls:"bi-number-custom-group-item",
            group_name:"",
            min:0,
            max:0,
            closemin:true,
            closemax:false
        });
    },

    _init:function(){
        BI.ConfNumberIntervalCustomGroupItem.superclass._init.apply(this,arguments);

        var self = this,o = this.options;

        this.editor = BI.createWidget({
            type:"bi.text_editor",
            value: o.group_name,
            hgap: this.constants.hgap
        });

        this.numberInterval = BI.createWidget({
            type:"bi.numerical_interval",
            min: o.min,
            max: o.max,
            closemin: o.closemin,
            closemax: o.closemax
        });

        this.deleteIcon = BI.createWidget({
            type:"bi.icon_button",
            cls:"close-red-font group-item-icon"
        });

        this.deleteIcon.on(BI.IconButton.EVENT_CHANGE,function(){
            self.destroy();
            self.fireEvent(BI.ConfNumberIntervalCustomGroupItem.EVENT_DESTROY);
        });

        this.deleteIcon.setVisible(false);

        BI.createWidget({
            type:"bi.htape",
            element:this.element,
            items:[{
                el:{
                    type: "bi.absolute",
                    items: [{
                        el:this.editor,
                        top: this.constants.hgap,
                        left: this.constants.rgap,
                        right:0,
                        bottom:this.constants.hgap
                    }]
                },
                width:this.constants.editorWidth
            },{
                type: "bi.absolute",
                items: [{
                    el: this.numberInterval,
                    top: this.constants.hgap,
                    left: 15,
                    right:0,
                    bottom:this.constants.hgap
                }]
            },{
                el:{
                    type:"bi.center_adapt",
                    items:[this.deleteIcon]
                },
                width:this.constants.iconSize
            }]
        });

        this.element.hover(function(){
            self.deleteIcon.setVisible(true);
        },function(){
            self.deleteIcon.setVisible(false);
        });

        this.numberInterval.on(BI.NumericalInterval.EVENT_CHANGE,function(){
            self.fireEvent(BI.ConfNumberIntervalCustomGroupItem.EVENT_CHANGE);
        });

        this.numberInterval.on(BI.NumericalInterval.EVENT_VALID,function(){
            self.fireEvent(BI.ConfNumberIntervalCustomGroupItem.EVENT_VALID);
        });

        this.numberInterval.on(BI.NumericalInterval.EVENT_ERROR,function(){
            self.fireEvent(BI.ConfNumberIntervalCustomGroupItem.EVENT_ERROR);
        });

    },

    isValid:function(){
        return this.numberInterval.isValid();
    },

    setSmallIntervalEnable:function(){
        this.numberInterval.setMinEnable(false);
        this.numberInterval.setCloseMinEnable(false);
    },

    getValue:function(){
        return BI.extend({group_name:this.editor.getValue()},this._filterValue());
    },

    _filterValue:function(){
        var value = this.numberInterval.getValue();
        if(BI.isEmpty(value.min)){
            delete value["min"];
            delete value["closemin"];
        }
        if(BI.isEmpty(value.max)){
            delete value["max"];
            delete value["closemax"]
        }
        return value;
    },

    setValue:function(ob){
        this.editor.setValue(ob.group_name);
        this.numberInterval.setValue({
            closemax:ob.closemax,
            closemin:ob.closemin,
            min:ob.min,
            max:ob.max
        });
    },

    destroy:function(){
        BI.ConfNumberIntervalCustomGroupItem.superclass.destroy.apply(this,arguments);
    }
});

BI.ConfNumberIntervalCustomGroupItem.EVENT_VALID = "EVENT_VALID";
BI.ConfNumberIntervalCustomGroupItem.EVENT_CHANGE = "EVENT_CHANGE";
BI.ConfNumberIntervalCustomGroupItem.EVENT_ERROR = "EVENT_ERROR";
BI.ConfNumberIntervalCustomGroupItem.EVENT_DESTROY = "EVENT_DESTROY";

$.shortcut("bi.conf_number_custom_group_item",BI.ConfNumberIntervalCustomGroupItem);