NumberIntervalCustomGroupView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(NumberIntervalCustomGroupView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-pager bi-mvc-layout"
        })
    },

    _init: function () {
        NumberIntervalCustomGroupView.superclass._init.apply(this, arguments);
    },

    _render:function(vessel){

        var self = this;
        BI.createWidget({
            type: "bi.center_adapt",
            element: vessel,
            items: [{
                el: {
                    type: "bi.text_button",
                    cls: "float-box-button",
                    text: "点击弹出FloatBox",
                    width: 200,
                    height: 80,
                    handler: function(){
                        FloatBoxes.open("windy", "windyTest", {}, self);
                    }
                }
            }]
        })

    }
});

NumberIntervalCustomGroupModel = BI.inherit(BI.Model,{

});


NumberIntervalCustomGroupChildView = BI.inherit(BI.BarFloatSection, {
    _defaultConfig: function(){
        return BI.extend(NumberIntervalCustomGroupChildView.superclass._defaultConfig.apply(this, arguments),{
            baseCls: "bi-float-box-child"
        })
    },

    _init: function(){
        NumberIntervalCustomGroupChildView.superclass._init.apply(this, arguments);
    },

    rebuildNorth : function(north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Grouping_Setting_Detail", 25,50000),
            height: 50,
            textAlign: "left",
            lgap: 10
        });
        return true;
    },

    rebuildCenter : function(center) {
        var self = this;
        this.group = BI.createWidget({
            type:"bi.number_custom_group",
            min:25,
            max:50000
        });

        this.group.on(BI.NumberIntervalCustomGroup.EVENT_ERROR,function(){
            self.saveButton.setValue(BI.i18nText("BI-Correct_The_Errors_Red"));
            self.saveButton.setEnable(false);
        });

        this.group.on(BI.NumberIntervalCustomGroup.EVENT_VALID,function(){
            self.saveButton.setValue(BI.i18nText("BI-Save"));
            self.saveButton.setEnable(true);
        });

        this.group.on(BI.NumberIntervalCustomGroup.EVENT_EMPTY_GROUP,function(){
            self.saveButton.setEnable(false);
        });

        BI.createWidget({
            type : 'bi.vertical',
            element : center,
            items:[this.group]
        })
    },

    rebuildSouth : function(south){
        var self = this;

        this.saveButton = BI.createWidget({
            type:"bi.button",
            text:BI.i18nText("BI-Save"),
            height:30,
            level: 'common',
            readonly:false,
            handler:function(v){
                self.model.set("group",self.group.getValue());
                self.close(v);
            }
        });

        this.cancelButton = BI.createWidget({
            type:"bi.button",
            text:BI.i18nText("BI-Cancel"),
            height:30,
            level: 'ignore',
            readonly:false,
            handler:function(v){
                self.close(v);
            }
        });

        BI.createWidget({
            type: "bi.right",
            element: south,
            items: [this.saveButton,
                    {
                        el: this.cancelButton,
                        rgap:10
                    }
            ]
        });
    },

    refresh: function(){
        this.group.populate(this.model.get("group"));
    }
})

NumberIntervalCustomGroupChildModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(NumberIntervalCustomGroupChildModel.superclass._defaultConfig.apply(this, arguments),{
            group : {
                group_type:BICst.NUMBER_INTERVAL_CUSTOM_GROUP_AUTO,
                group_value:{
                    group_interval:10000
                }
            }
        })
    },

    _init: function(){
        NumberIntervalCustomGroupChildModel.superclass._init.apply(this, arguments);
    }
})