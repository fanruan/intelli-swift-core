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

        var self=this;
        this.leftButton=BI.createWidget({
            type:"bi.global_style_canvas_button",
            direction:"left",
            initState:false
        });
        this.leftButton.on(BI.GlobalStyleCanvasButton.EVENT_CHANGE,function () {
            self.fireEvent(BI.GlobalStyleIndexPredictionStyle.PAGE_CHANGE,"left");
            self.bottomItem.populate([self._currentPager(3,1)]);
        });
        var leftLayout=BI.createWidget({
            type:"bi.float_center_adapt",
            items:[this.leftButton]
        });

        this.rightButton=BI.createWidget({
            type:"bi.global_style_canvas_button",
            direction:"right",
            initState:true
        });
        this.rightButton.on(BI.GlobalStyleCanvasButton.EVENT_CHANGE,function () {
            self.fireEvent(BI.GlobalStyleIndexPredictionStyle.PAGE_CHANGE,"right");
            self.bottomItem.populate([self._currentPager(3,2)]);
        });
        var rightLayout=BI.createWidget({
            type:"bi.float_center_adapt",
            items:[this.rightButton]
        });

        this.centerButtonGroup=BI.createWidget({
            type:"bi.button_group",
            items:[],
            layouts:[{
                type:"bi.inline",
                lgap: 5,
                rgap: 5,
                tgap: 5,
                bgap: 5
            }]
        });
        this.centerButtonGroup.on(BI.ButtonGroup.EVENT_CHANGE,function () {
            self.fireEvent(BI.GlobalStyleIndexPredictionStyle.EVENT_CHANGE,this)
        });
        this.centerButtonGroup.populate([this._createAdministratorStyle(),this._createPredictionStyleOne(),this._createPredictionStyleTwo(),this._createPredictionStyleThree(),this._createPredictionStyleFour()])

        var centerItems=BI.createWidget({
            type:"bi.htape",
            items:[{
                el:leftLayout,
                width:29
            },{
                el:this.centerButtonGroup
            },{
                el:rightLayout,
                width:29
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
        this.bottomItem.populate([self._currentPager(3,2)]);

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
                height:160
            },{
                el:this.bottomItem
            }]
        })
    },
    _createButton:function (value,title,cls) {
        return BI.createWidget({
            type:"bi.icon_button",
            cls:"button-shadow",
            title:title,
            value:value,
            height:70,
            width:110
        })
    },
    _getCanvasColour:function (isSelected) {
        if(isSelected){
            return "#ffffff"
        }else {
            return "#cccccc"
        }
    },
    _currentPager:function (totalNumber,currentNumber) {
        var self=this;
        var canvas=BI.createWidget({
            type:"bi.canvas",
            height:10,
            width:totalNumber*15-5
        });
        BI.each(BI.makeArray(totalNumber),function (i) {
            canvas.circle(i*15+5,5,5,self._getCanvasColour(i==(currentNumber-1)))
        });
        canvas.stroke();
        return canvas
    },
    _createAdministratorStyle:function () {
        return this._createButton(0,BI.i18nText("BI-Administrator_Set_Style"))
    },
    _createPredictionStyleOne:function () {
        return this._createButton(1,BI.i18nText("BI-Prediction_Style_One"))
    },
    _createPredictionStyleTwo:function () {
        return this._createButton(2,BI.i18nText("BI-Prediction_Style_Two"))
    },
    _createPredictionStyleThree:function () {
        return this._createButton(3,BI.i18nText("BI-Prediction_Style_Three"))
    },
    _createPredictionStyleFour:function () {
        return this._createButton(4,BI.i18nText("BI-Prediction_Style_Four"))
    },

    getValue:function () {
        var value=this.centerButtonGroup.getValue();
        return{
            "currentStyle":value[0]
        }
    },

    setValue:function () {

    },
    pageChange:function (direction) {
        console.log(direction)
    },
    populate:function () {
        
    }
});
BI.GlobalStyleIndexPredictionStyle.PAGE_CHANGE="BI.GlobalStyleIndexPredictionStyle.PAGE_CHANGE";
BI.GlobalStyleIndexPredictionStyle.EVENT_CHANGE="BI.GlobalStyleIndexPredictionStyle.EVENT_CHANGE";
$.shortcut("bi.global_style_index_prediction_style",BI.GlobalStyleIndexPredictionStyle);