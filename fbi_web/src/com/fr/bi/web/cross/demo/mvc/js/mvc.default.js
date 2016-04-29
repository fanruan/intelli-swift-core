//第一个样例
DefaultView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(DefaultView.superclass._defaultConfig.apply(this, arguments),{
            baseCls: "bi-default"
        })
    },

    _init: function(){
        DefaultView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var self = this;
        BI.createWidget({
            type: "bi.center",
            element: vessel,
            hgap: 200,
            vgap: 150,
            items: [
                {
                    type: "bi.grid",
                    columns:2,
                    rows: 2,
                    items: BI.createItems([{
                        column:0,
                        row:0,
                        el:{
                            type: "bi.link",
                            text: "underscore.js",
                            cls: "nav-button mvc-button button1",
                            href: "http://www.css88.com/doc/underscore/"
                        }
                    },{
                        column:1,
                        row:0,
                        el:{
                            text: "Font",
                            cls: "nav-button mvc-button button2",
                            handler: function(){
                                self.skipTo("font");
                            }
                        }
                    },{
                        column:0,
                        row:1,
                        el:{
                            text: "国际化",
                            cls: "nav-button mvc-button button3",
                            handler: function(){
                                self.skipTo("i18n");
                            }
                        }
                    },{
                        column:1,
                        row:1,
                        el:{
                            text: "数据结构——对比前一版本",
                            cls: "nav-button mvc-button button5",
                            handler: function(){
                                self.skipTo("dataStructure");
                            }
                        }
                    }], {
                        type: "bi.text_button",
                        whiteSpace: "normal"
                    })
                }
            ]
        })
    }
})

DefaultModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(DefaultModel.superclass._defaultConfig.apply(this, arguments),{

        })
    },

    _init: function(){
        DefaultModel.superclass._init.apply(this, arguments);
    }

});