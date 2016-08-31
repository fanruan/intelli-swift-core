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
            type:"bi.icon_button",
            cls:"layout-bg1",
            height:26,
            width:14
        });
        var leftLayout=BI.createWidget({
            type:"bi.float_center_adapt",
            items:[this.leftButton]
        });

        this.rightButton=BI.createWidget({
            type:"bi.text_button",
            cls:"layout-bg2",
            height:26,
            width:14
        });
        var rightLayout=BI.createWidget({
            type:"bi.float_center_adapt",
            items:[this.rightButton]
        });

        this.centerButtonGroup=BI.createWidget({
            //type:"bi.button"
            type:"bi.global_style_prediction_style_button_group"
        });


        var centerItems=BI.createWidget({
            type:"bi.htape",
            //height:160,
            items:[{
                el:leftLayout,
                width:30
            },{
                el:this.centerButtonGroup
            },{
                el:rightLayout,
                width:30
            }]
        });


        this.bottomItem=BI.createWidget({
            type:"bi.button_group",
            items:[],
            layouts:[{
                type:"bi.horizontal_adapt"
            }],
            height:40
        });
        var buttonOne=BI.createWidget({
            type:"bi.canvas",
            height:10,
            width:25
        });
        buttonOne.circle(5,5,5,"#ffffff");
        buttonOne.circle(20,5,5,"#cccccc");
        buttonOne.stroke();
        this.bottomItem.populate([buttonOne]);

        BI.createWidget({
            type:"bi.vtape",
            element:this.element,
            items:[{
                el:{
                    type:"bi.label"
                },
                height:9
            },{
                el:centerItems,
                //tgap:14,
                height:160
            },{
                el:this.bottomItem
            }]
        })
    },


    populate:function () {
        
    }
});
$.shortcut("bi.global_style_index_prediction_style",BI.GlobalStyleIndexPredictionStyle);