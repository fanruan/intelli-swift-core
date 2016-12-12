/**
 * Created by fay on 2016/11/16.
 */
BI.DimensionRegionWrapper = BI.inherit(BI.AbstractWrapper, {

    constants: {
        TITLE_ICON_HEIGHT: 20,
        TITLE_ICON_WIDTH: 20,
        REGION_HEIGHT_NORMAL: 25,
        REGION_DIMENSION_GAP: 5
    },

    _defaultConfig: function () {
        return BI.extend(BI.DimensionRegionWrapper.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-region",
            titleName: ""
        });
    },

    _init: function () {
        BI.DimensionRegionWrapper.superclass._init.apply(this, arguments);
    },

    _appendEmptyRegion: function () {
        var self = this, o = this.options;
        var emptyRegion = this.center.getNodeById(BI.DimensionEmptyRegion.ID);
        if (BI.isNotNull(emptyRegion)) {
            this.center.removeItems([emptyRegion.getValue()]);
        }
        emptyRegion = BI.createWidget({
            type: "bi.dimension_empty_region",
            id: BI.DimensionEmptyRegion.ID,
            wrapperType: o.wrapperType,
            wId: o.wId
        });
        emptyRegion.on(BI.DimensionEmptyRegion.EVENT_CHANGE, function (data) {
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
            type: "bi.dimension_region",
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

    refreshRegion: function (type, dimensions) {
        var self = this, o = this.options;
        if (BI.isNull(this.regions[type])) {
            this.regions[type] = BI.createWidget({
                type: "bi.dimension_region",
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
    }
});
$.shortcut("bi.dimension_region_wrapper", BI.DimensionRegionWrapper);