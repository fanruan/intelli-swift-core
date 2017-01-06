/**
 * Created by zcf on 2016/12/27.
 */
BI.ShowRegionManager = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ShowRegionManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-show-region-manager",
            wId: "",
            dimensionCreator: BI.emptyFn
        })
    },

    _init: function () {
        BI.ShowRegionManager.superclass._init.apply(this, arguments);

        var self = this, o = this.options;

        this.model = new BI.ShowRegionManagerModel({
            wId: o.wId
        });
        //用来缓存各个面板
        this.cardLayouts = {};
        this.isfirst = true;

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
        return "dimension-tag";
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

    _dragStart: function () {
        var self = this;
        //设置button是否灰化
        var buttons = this.tagButtons.getAllButtons();
        BI.each(buttons, function (i, button) {
            if (!self._isAllowShowCard(button.getValue())) {
                button.setEnable(false);
            } else {
                //不灰化设置高亮
                button.setHighLightBg(true);
            }
        });
    },

    _dragStop: function () {
        //取消所有button灰化和高亮
        var buttons = this.tagButtons.getAllButtons();
        BI.each(buttons, function (i, button) {
            button.setEnable(true);
            button.setHighLightBg(false);
        });
    },

    _createCard: function (viewType, regionType) {
        var self = this, o = this.options;

        var region;
        switch (regionType) {
            case BICst.REGION_TYPE.REGION_DIMENSION:
                region = this._createRegionDimension(viewType);
                break;
            case BICst.REGION_TYPE.REGION_TARGET:
                region = this._createRegionTarget(viewType);
                break;
            case BICst.REGION_TYPE.REGION_WRAPPER_DIMENSION:
                region = this._createRegionWrapperDimension(viewType);
                break;
            case BICst.REGION_TYPE.REGION_WRAPPER_TARGET:
                region = this._createRegionWrapperTarget(viewType);
                break;
        }
        region.on(BI.ShowAbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.ShowRegionManager.EVENT_CHANGE);
        });
        region.on(BI.ShowAbstractRegion.EVENT_START, function () {
            //设置拖动的维度type
            self.dragType = region.getViewType();
            self._dragStart();
        });
        region.on(BI.ShowAbstractRegion.EVENT_STOP, function () {
            self._dragStop();
        });
        return region;
    },

    _createRegionDimension: function (viewType) {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.show_dimension_region",
            width: "100%",
            dimensionCreator: function (dId, op) {
                return o.dimensionCreator(dId, viewType, op)
            },
            wId: o.wId,
            viewType: viewType,
            regionType: viewType
        });
        return region;
    },

    _createRegionTarget: function (viewType) {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.show_target_region",
            width: "100%",
            dimensionCreator: function (dId, op) {
                return o.dimensionCreator(dId, viewType, op)
            },
            wId: o.wId,
            viewType: viewType,
            regionType: viewType
        });
        return region;
    },

    _createRegionWrapperDimension: function (viewType) {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.show_dimension_region_wrapper",
            width: "100%",
            dimensionCreator: o.dimensionCreator,
            wId: o.wId,
            viewType: viewType
        });
        region.on(BI.ShowRegionWrapper.EVENT_CHANGE, function () {
            self.fireEvent(BI.ShowRegionManager.EVENT_CHANGE);
        });
        region.on(BI.ShowRegionWrapper.EVENT_START, function () {
            //设置拖动的维度type
            self.dragType = region.getViewType();
            self._dragStart();
        });
        region.on(BI.ShowRegionWrapper.EVENT_STOP, function () {
            self._dragStop();
        });
        return region;
    },

    _createRegionWrapperTarget: function (viewType) {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.show_target_region_wrapper",
            width: "100%",
            dimensionCreator: o.dimensionCreator,
            wId: o.wId,
            viewType: viewType
        });
        region.on(BI.ShowRegionWrapper.EVENT_CHANGE, function () {
            self.fireEvent(BI.ShowRegionManager.EVENT_CHANGE);
        });
        region.on(BI.ShowRegionWrapper.EVENT_START, function () {
            //设置拖动的维度type
            self.dragType = region.getViewType();
            self._dragStart();
        });
        region.on(BI.ShowRegionWrapper.EVENT_STOP, function () {
            self._dragStop();
        });
        return region;
    },

    _createTagButtons: function () {
        var self = this;
        if (!this.buttons) {
            var regionsType = this.model.getRegionsType();
            var buttons = [];
            BI.each(regionsType, function (viewType, regionType) {
                var button = self._createTagButton(self.model.getRegionNameByType(viewType), viewType);
                buttons.push(button);
            });
            this.buttons = buttons;
        }
    },

    _initPopulate: function () {
        if (this.isfirst) {
            this.tagButtons.populate(this.buttons);
            this.cards.populate(this.cardLayouts);
            this.buttons[0].setSelected(true);
            this.cards.showCardByName(this.buttons[0].getValue());
            this.isfirst = false;
        }
    },

    getValue: function () {
        var self = this;
        var views = {};
        var allCardName = this.cards.getAllCardNames();
        BI.each(allCardName, function (i, type) {
            var card = self.cards.getCardByName(type);
            BI.extend(views, card.getValue());
        });
        return {
            view: views
        };
    },

    populate: function () {
        var self = this;
        this.model.populate();
        var regionsType = this.model.getRegionsType();
        BI.each(regionsType, function (viewType, regionType) {
            if (!self.cardLayouts[viewType]) {
                self.cardLayouts[viewType] = self._createCard(viewType, regionType);
            }
            self.cardLayouts[viewType].populate();
        });
        this._createTagButtons();
        this._initPopulate();
    }
});
BI.ShowRegionManager.EVENT_CHANGE = "BI.ShowRegionManager.EVENT_CHANGE";
$.shortcut("bi.show_region_manager", BI.ShowRegionManager);