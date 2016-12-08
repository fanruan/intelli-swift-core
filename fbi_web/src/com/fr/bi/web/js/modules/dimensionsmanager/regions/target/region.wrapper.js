/**
 * Created by fay on 2016/11/16.
 */
BI.TargetRegionWrapper = BI.inherit(BI.AbstractWrapper, {

    constants: {
        TITLE_ICON_HEIGHT: 20,
        TITLE_ICON_WIDTH: 20,
        REGION_HEIGHT_NORMAL: 25,
        REGION_DIMENSION_GAP: 5
    },

    _defaultConfig: function () {
        return BI.extend(BI.TargetRegionWrapper.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target-region",
            titleName: ""
        });
    },

    _init: function () {
        BI.TargetRegionWrapper.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.calculateAddButton = BI.createWidget({
            type: "bi.text_button",
            height: 25,
            disabled: BI.isEmptyArray(BI.Utils.getAllTargetDimensionIDs(this.options.wId)),
            warningTitle: BI.i18nText("BI-There_Is_No_Target_for_Contruct_Calculate_Target"),
            value: BI.i18nText("BI-Add_Cal_Target")
        });

        this.calculateAddButton.on(BI.TextButton.EVENT_CHANGE, function () {
            var dId = BI.UUID();
            self.calpopup = BI.createWidget({
                type: "bi.calculate_target_popup_summary",
                wId: o.wId,
                targetId: dId
            });
            self.calpopup.on(BI.CalculateTargetPopupSummary.EVENT_CHANGE, function () {
                var targetValue = self.calpopup.getValue();
                self._setCalculateTarget(targetValue.dId, targetValue.data)

            });
            BI.Popovers.remove(dId + "calculate_set");
            BI.Popovers.create(dId + "calculate_set", self.calpopup).open(dId + "calculate_set");
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.right",
                    hgap: 10,
                    items: [{
                        el: this.calculateAddButton

                    }],
                    height: 25
                },
                top: 0,
                left: 0,
                right: 0
            }]
        })
    },

    _appendEmptyRegion: function () {
        var self = this, o = this.options;
        var emptyRegion = this.center.getNodeById(BI.TargetEmptyRegion.ID);
        if (BI.isNotNull(emptyRegion)) {
            this.center.removeItems([emptyRegion.getValue()]);
        }
        emptyRegion = BI.createWidget({
            type: "bi.target_empty_region",
            id: BI.TargetEmptyRegion.ID,
            wrapperType: o.wrapperType,
            wId: o.wId
        });
        emptyRegion.on(BI.TargetEmptyRegion.EVENT_CHANGE, function (data) {
            self._addRegionAndDimension(data);
        });
        this.emptyRegion = emptyRegion;
        this.center.addItems([emptyRegion]);
    },

    _addRegionAndDimension: function (data) {
        var self = this, o = this.options;
        var regionTypes = BI.keys(this.regions);
        var newRegionType = o.wrapperType;
        if (regionTypes.length !== 0) {
            //找到最大的 +1
            newRegionType = BI.parseInt(BI.sortBy(regionTypes)[regionTypes.length - 1]) + 1;
        }
        this.regions[newRegionType] = BI.createWidget({
            type: "bi.target_region",
            dimensionCreator: o.dimensionCreator,
            wId: o.wId,
            regionType: newRegionType
        });
        this.regions[newRegionType].on(BI.AbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.AbstractWrapper.EVENT_CHANGE);
        });
        this.center.addItems([this.regions[newRegionType]]);
        BI.each(data, function (i, dimension) {
            self.regions[newRegionType].addDimension(dimension.dId || BI.UUID(), dimension);
        });
        this._appendEmptyRegion();
    },

    _setCalculateTarget: function (dId, options) {
        var o = this.options;
        o.dimensionCreator(dId, this.options.wrapperType, options);
    },

    refreshRegion: function (type, dimensions) {
        var self = this, o = this.options;
        if (BI.isNull(this.regions[type])) {
            this.regions[type] = BI.createWidget({
                type: "bi.target_region",
                dimensionCreator: o.dimensionCreator,
                wId: o.wId,
                regionType: type
            });
            this.center.addItems([this.regions[type]]);
            this._appendEmptyRegion();
        }

        if (dimensions.length > 0) {
            this.regions[type].populate(dimensions);
        } else {
            this.regions[type].destroy();
            delete this.regions[type];
        }
        if(BI.isEmptyArray(BI.Utils.getAllTargetDimensionIDs(this.options.wId))){
            this.calculateAddButton.setEnable(false);
        }else{
            this.calculateAddButton.setEnable(true);
        }
    }
});
$.shortcut("bi.target_region_wrapper", BI.TargetRegionWrapper);