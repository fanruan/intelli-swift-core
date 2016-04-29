I18nView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(I18nView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-i18n"
        })
    },

    _init: function(){
        I18nView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var self = this;

        BI.createWidget({
            type: "bi.border",
            element: vessel,
            items: {
                north: {
                    el: {
                        type: "bi.left_right_vertical_adapt",
                        items: {
                            left: [{
                                type: "bi.label",
                                text: "BI国际化汇总",
                                width: 200,
                                cls: "title"
                            }],
                            right: [{
                                type: "bi.button",
                                text: "退出",
                                height: 30,
                                handler: function(){
                                    self.notifyParentEnd();
                                }
                            }]
                        }
                    },
                    height: 50
                },
                center: this._createCenter()
            }
        })
    },

    _createCenter: function(){
        return {
            type: "bi.vertical",
            items: [{
                type: "bi.left",
                items: this._createI18NItems(),
                hgap: 10,
                vgap: 10
            }]
        }
    },

    _createI18NItems: function(){
        var self = this;
        var localeText = FR.i18n;
        //过滤出fbi开头的
        var biText = {};
        BI.each(localeText, function(key, value){
            if(key.startWith("BI-")){
                biText[key] = value;
            }
        });

        var i18n = [];
        var sort = [];
        //按照key排个序
        BI.each(biText, function(key, value){
            sort.push(key);
        });
        sort.sort();
        BI.each(sort, function(i, key){
            i18n.push({
                type: "bi.vertical",
                items: BI.createItems([{text: key, cls: "key", title: key}, {text: biText[key], cls: "value", title: biText[key]}], {
                    type: "bi.label",
                    width: 200,
                    height: 30
                })
            })
        });
        return i18n;
    }

});

I18nModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(I18nModel.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function(){
        I18nModel.superclass._init.apply(this, arguments);
    }

});