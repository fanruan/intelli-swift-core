//vertical布局
CardView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(CardView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-card bi-mvc-layout"
        })
    },

    _init: function(){
        CardView.superclass._init.apply(this, arguments);
    },

    _createButtons: function(){
        var self = this;
        var buttons = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(this.model.get("items"), {
                type: "bi.text_button",
                hgap: 20
            }),
            height: 40,
            layout: {
                type: "bi.left",
                hgap: 20,
                vgap: 10
            }
        });
        buttons.on(BI.ButtonGroup.EVENT_CHANGE, function(){
            self.cards.showCardByName(buttons.getValue()[0]);
        });
        return buttons;
    },

    _createCards: function(){
        return this.cards = BI.createWidget({
            type: "bi.card",
            items: [{
                el: {
                    type: "bi.center",
                    items: [{
                        type: "bi.label",
                        text: "This is Card 1",
                        whiteSpace: "normal"
                    }],
                    height: 200,
                    cls: "layout-bg1"
                },
                cardName: "card1"
            },{
                el: {
                    type: "bi.center",
                    items: [{
                        type: "bi.label",
                        text: "This is Card 2",
                        whiteSpace: "normal"
                    }],
                    height: 200,
                    cls: "layout-bg2"
                },
                cardName: "card2"
            }]
        })
    },

    _render: function(vessel){
        BI.createWidget({
            element: vessel,
            type: "bi.vertical",
            items: [this._createButtons(), this._createCards()],
            hgap: 100,
            vgap: 20
        });
        this.cards.showCardByName("card1");
    }
});

CardModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(CardModel.superclass._defaultConfig.apply(this, arguments), {
            items: [{
                cls: "layout-bg1 mvc-button",
                text: "Card-1",
                value: "card1",
                height: 30,
                selected: true
            }, {
                cls: "layout-bg2 mvc-button",
                text: "Card-2",
                value: "card2",
                height: 30
            }]
        })
    },

    _init: function(){
        CardModel.superclass._init.apply(this, arguments);
    }
});