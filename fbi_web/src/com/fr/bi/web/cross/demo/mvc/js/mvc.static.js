//static函数
StaticView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(StaticView.superclass._defaultConfig.apply(this, arguments),{
            baseCls: "bi-static"
        })
    },

    _init: function(){
        StaticView.superclass._init.apply(this, arguments);
    },

    change: function(changed){
        if(changed.type){
            this._showText();
        }
    },

    _showText: function(){
        this.leftLabel.setText(this.model.get("imageView"));
        this.rightLabel.setText(this.model.get("sortIcon", this.model.get("type")));
    },

    _createTop: function(){
        var self = this;
        this.buttons = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(this.model.get("buttons"), {
                type: "bi.text_button",
                height: 40,
                textAlign: "center",
                hgap: 20
            }),
            layout: {
                type: "bi.center",
                height: 60,
                hgap: 30,
                vgap: 10
            }
        });
        this.buttons.on(BI.ButtonGroup.EVENT_CHANGE, function(v){
            self.model.set("type", v[0]);
        });
        return this.buttons;
    },

    _createCenter: function(){
        this.leftLabel = BI.createWidget({
            type: "bi.label",
            cls: "green-button",
            height: 40
        });
        return this.leftLabel;
    },

    _createBottom: function(){
        this.rightLabel = BI.createWidget({
                type: "bi.label",
                cls: "blue-button",
                height: 40
        });
        return this.rightLabel;
    },

    _createOuter: function(){
        return BI.createWidget({
            type: "bi.vertical",
            items: [
                this._createTop(),
                this._createCenter(),
                this._createBottom()
            ],
            hgap: 20,
            vgap: 30
        })
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.center",
            element: vessel,
            items: [this._createOuter()],
            hgap: 100,
            vgap: 100
        });
        this._showText();
    }

});

StaticModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(StaticModel.superclass._defaultConfig.apply(this, arguments),{
            sort: {
                1: 100,
                2: 101
            },
            type: 1
        })
    },

    _static: function(){
        return {
            buttons: [{
                text: "green-button",
                value: 1,
                cls: "green-button mvc-button",
                selected: true
            },{
                text: "blue-button",
                value: 2,
                cls: "blue-button mvc-button"
            }],

            colorView: {
                1: "通过一般静态函数获得 - green",
                2: "通过一般静态函数获得 - blue"
            },

            sortIcons: {
                100: "通过带有参数的静态函数获得 - Green",
                101: "通过带有参数的静态函数获得 - Blue"
            },

            "imageView": function(){
                var type = this.get("type");
                var colorView = this.get("colorView");
                return colorView[type];
            },

            "sortIcon": function(type){
                var sort = this.get("sort");
                var t = sort[type];
                return this.get("sortIcons")[t];
            }
        }
    },

    _init: function(){
        StaticModel.superclass._init.apply(this, arguments);
    }
});