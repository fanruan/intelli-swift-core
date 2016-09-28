/**
 * Created by zcf on 2016/9/22.
 */
BI.Slider=BI.inherit(BI.Widget,{
    _defaultConfig:function () {
        return BI.extend(BI.Slider.superclass._defaultConfig.apply(this,arguments),{
            baseCls:"bi-slider"
        });
    },
    _init:function () {
        BI.extend(BI.Slider.superclass._init.apply(this,arguments));
        var self=this;
        this.active=false;
        this.slider=BI.createWidget({
            type:"bi.icon_button",
            cls:"slider-normal-icon",
            element:this.element,
            iconWidth:30,
            iconHeight:30,
            height:30,
            width:30
        });
        this.slider.element.hover(function () {
            if(!self.active){
                self.setIcon(true);
            }
        },function () {
            if(!self.active){
                self.setIcon(false);
            }
        });
    },
    setIcon:function (bool) {
        if(bool){
            this.slider.element.removeClass("slider-normal-icon").addClass("slider-active-icon");
        }else {
            this.slider.element.removeClass("slider-active-icon").addClass("slider-normal-icon");
        }
    },
    setMoveState:function (bool) {
        this.active=bool;
        this.setIcon(bool);
    }
});
$.shortcut("bi.slider",BI.Slider);