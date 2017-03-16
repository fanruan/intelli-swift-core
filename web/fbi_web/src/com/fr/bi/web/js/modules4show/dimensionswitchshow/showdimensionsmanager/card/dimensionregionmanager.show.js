/**
 * Created by zcf on 2016/11/11.
 */
BI.DimensionSwitchCardShow = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DimensionSwitchCardShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: ""
        })
    },

    _init: function () {
        BI.DimensionSwitchCardShow.superclass._init.apply(this, arguments);

        this.layout = BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: []
        });
    },

    showCardByName: function (name) {
        var widgetName = this.nameMap[name];
        BI.each(this.layout.getWidgets(), function (id, widget) {
            if (widget.getName() === widgetName) {
                widget.setVisible(true);
            } else {
                widget.setVisible(false);
            }
        });
    },

    getAllCardNames: function () {
        var nameList = [];
        BI.each(this.nameMap, function (name, id) {
            nameList.push(name);
        });
        return nameList;
    },

    getCardByName: function (name) {
        var widgetName = this.nameMap[name];
        var card = {};
        BI.each(this.layout.getWidgets(), function (id, widget) {
            if (widget.getName() === widgetName) {
                card = widget;
            }
        });
        return card;
    },

    populate: function (cards) {
        var self = this;
        this.nameMap = {};
        var items = [];
        BI.each(cards, function (name, el) {
            self.nameMap[name] = el.getName();
            items.push({
                el: el,
                top: 0,
                left: 0
            });
        });
        this.layout.populate(items);
    }
});
$.shortcut("bi.dimension_switch_card_show", BI.DimensionSwitchCardShow);