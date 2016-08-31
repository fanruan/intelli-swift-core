/**
 * Created by zcf on 2016/8/31.
 */
BI.GlobalStylePredictionStyleButtonGroup=BI.inherit(BI.Widget,{
    _defaultConfig:function () {
        return BI.extend(BI.GlobalStylePredictionStyleButtonGroup.superclass._defaultConfig.apply(this,arguments),{
            baseCls:"bi-global-style-prediction-style-button-group"
        })
    },
    _init:function () {
        BI.GlobalStylePredictionStyleButtonGroup.superclass._init.apply(this,arguments);

        var buttonOne=BI.createWidget({
            type:"bi.icon_button",
            cls:"button-shadow",
            text:"default",
            height:70,
            width:110
        });
        var buttonTwo=BI.createWidget({
            type:"bi.icon_button",
            cls:"button-shadow",
            text:"default",
            height:70,
            width:110
        });
        BI.createWidget({
            type:"bi.grid",
            element:this.element,
            columns: 3,
            rows: 2,
            items:[{
                column: 0,
                row: 0,
                el:this._buttonLayout(buttonOne)
            },{
                column: 1,
                row: 0,
                el:this._buttonLayout(buttonTwo)
            },{
                column: 2,
                row: 0,
                el:this._buttonLayout({
                    type:"bi.button",
                    cls:"button-shadow",
                    text:"default",
                    height:70,
                    width:110
                })
            },{
                column: 0,
                row: 1,
                el:this._buttonLayout({
                    type:"bi.button",
                    cls:"button-shadow",
                    text:"default",
                    height:70,
                    width:110
                })
            },{
                column: 1,
                row: 1,
                el:this._buttonLayout({
                    type:"bi.button",
                    cls:"button-shadow",
                    text:"default",
                    height:70,
                    width:110
                })
            },{
                column: 2,
                row: 1,
                el:this._buttonLayout({
                    type:"bi.button",
                    cls:"button-shadow",
                    text:"default",
                    height:70,
                    width:110
                })
            }]
        })
    },
    _buttonLayout:function (widget) {
        return BI.createWidget({
            type:"bi.float_center_adapt",
            items:[widget]
        })
    }
});
$.shortcut("bi.global_style_prediction_style_button_group",BI.GlobalStylePredictionStyleButtonGroup);