/**
 * Created by zcf on 2016/8/30.
 */
BI.GlobalStyleIndexPredictionStyle=BI.inherit(BI.Widget,{
    _defaultConfig:function () {
        return BI.extend(BI.GlobalStyleIndexPredictionStyle.superclass._defaultConfig.apply(this,arguments),{
            baseCls:"bi-global-style-index-prediction-style"
        })
    },
    _init:function () {
        BI.GlobalStyleIndexPredictionStyle.superclass._init.apply(this,arguments);

        this.leftButton=BI.createWidget({
            type:"bi.text_button",
            cls:"layout-bg1",
            height:26,
            width:14
        });
        var leftLayout=BI.createWidget({
            type:"bi.float_center_adapt",
            width:30,
            height:150,
            items:[this.leftButton]
        });
        this.rightButton=BI.createWidget({
            type:"bi.text_button",
            cls:"layout-bg2",
            //level:"success",
            height:26,
            width:14
        });
        var rightLayout=BI.createWidget({
            type:"bi.float_center_adapt",
            width:30,
            height:150,
            items:[this.rightButton]
        });
        this.centerButtonGroup=BI.createWidget({
            type:"bi.button",
            height:150,
            width:360
        });
        var centerItems=BI.createWidget({
            type:"bi.horizontal",
            items:[
                leftLayout,
            ]
        });
        BI.createWidget({
            type:"bi.vertical",
            element:this.element,
            items:[{
                el:centerItems,
                tgap:14
            }]
        })
    },
    populate:function () {
        
    }
});
$.shortcut("bi.global_style_index_prediction_style",BI.GlobalStyleIndexPredictionStyle);