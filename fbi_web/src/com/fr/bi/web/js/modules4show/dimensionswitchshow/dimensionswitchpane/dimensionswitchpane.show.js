/**
 * Created by zcf on 2016/11/4.
 */
BI.DimensionSwitchPaneShow = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DimensionSwitchPaneShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-switch-pane-show",
            wId: "",
            dimensionCreator: BI.emptyFn
        })
    },

    _init: function () {
        BI.DimensionSwitchPaneShow.superclass._init.apply(this, arguments);

        var self = this, o = this.options;

        this.model = new BI.DimensionSwitchPaneShowModel({
            wId: o.wId
        });
        //用来缓存各个面板
        this.cardLayouts = {};

        this.tagButtons = BI.createWidget({
            type: "bi.button_group",
            cls: "button-group-bottom",
            items: [],
            height: 50,
            width: 260,
            layouts: [{
                type: "bi.inline",
                tgap: 5,
                hgap: 2
            }]
        });
        this.tagButtons.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.cards.showCardByName(self.tagButtons.getValue()[0]);
        });

        this.cards = BI.createWidget({
            type: "bi.dimension_switch_card_show"
        });

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                height: 50,
                el: this.tagButtons
            }, {
                height: "fill",
                el: this.cards
            }]
        });
    },

    _getCls: function () {
        return "dimensions-container" + this.options.wId;
    },

    _createTagButton: function (name, cardName) {
        var self = this;
        var button = BI.createWidget({
            type: "bi.dimension_tag_button",
            cls: this._getCls(),
            text: name,
            value: cardName,
            drop: {
                accept: "." + this._getCls(),
                tolerance: "pointer",
                over: function (event, ui) {
                    var v = button.getValue();
                    if (self._isAllowShowCard(v)) {
                        self.tagButtons.setValue([v]);
                        self.cards.showCardByName(v);
                    }
                }
            }
        });
        return button;
    },

    _isAllowShowCard: function (buttonType) {
        var dType = [BICst.REGION.DIMENSION1, BICst.REGION.DIMENSION2];
        var tType = [BICst.REGION.TARGET1, BICst.REGION.TARGET2, BICst.REGION.TARGET3];
        return (BI.contains(dType, this.dragType) && BI.contains(dType, buttonType)) || (BI.contains(tType, this.dragType) && BI.contains(tType, buttonType));
    },

    _createCard: function (dimensionType, dimensions) {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.dimension_switch_region_show",
            dimensionType: dimensionType,
            cls: this._getCls(),
            dimensionCreator: o.dimensionCreator
        });
        region.getSortableCenter().element.sortable({
            connectWith: "." + this._getCls(),
            tolerance: "pointer",
            scroll: false,
            placeholder: {
                element: function ($currentItem) {
                    var holder = BI.createWidget({
                        type: "bi.label",
                        cls: "ui-sortable-place-holder",
                        height: $currentItem.height() - 2
                    });
                    holder.element.css({"margin": "5px"});
                    return holder.element;
                },
                update: function () {
                }
            },
            items: "." + this._getCls(),
            update: function (event, ui) {
                self.fireEvent(BI.DimensionSwitchPaneShow.EVENT_CHANGE);
            },
            start: function (event, ui) {
                //设置拖动的维度type
                self.dragType = region.getDimensionType();
                //设置button是否灰化
                var buttons = self.tagButtons.getAllButtons();
                BI.each(buttons, function (i, button) {
                    if (!self._isAllowShowCard(button.getValue())) {
                        button.setEnable(false);
                    } else {
                        //不灰化设置高亮
                        button.setHighLightBg(true);
                    }
                });
            },
            stop: function (event, ui) {
                //取消所有button灰化和高亮
                var buttons = self.tagButtons.getAllButtons();
                BI.each(buttons, function (i, button) {
                    button.setEnable(true);
                    button.setHighLightBg(false);
                });
            },
            over: function (event, ui) {
            }
        });
        region.populate(dimensions);
        return region;
    },

    getValue: function () {
        var self = this;
        var view = {};
        var allCardName = this.cards.getAllCardNames();
        BI.each(allCardName, function (i, type) {
            var card = self.cards.getCardByName(type);
            view[type] = card.getValue();
        });
        return view;
    },

    populate: function () {
        var self = this;
        this.model.populate();
        var views = this.model.getView();
        var buttons = [];
        BI.each(views, function (dimensionType, dimensions) {
            if (!self.cardLayouts[dimensionType]) {
                self.cardLayouts[dimensionType] = self._createCard(dimensionType, dimensions);
            }
            var button = self._createTagButton(self.model.getRegionNameByType(dimensionType), dimensionType);
            buttons.push(button);
        });
        this.tagButtons.populate(buttons);
        this.cards.populate(self.cardLayouts);
        buttons[0].setSelected(true);
        this.cards.showCardByName(buttons[0].getValue());
    }
});
BI.DimensionSwitchPaneShow.EVENT_CHANGE = "BI.DimensionSwitchPaneShow.EVENT_CHANGE";
$.shortcut("bi.dimension_switch_pane_show", BI.DimensionSwitchPaneShow);