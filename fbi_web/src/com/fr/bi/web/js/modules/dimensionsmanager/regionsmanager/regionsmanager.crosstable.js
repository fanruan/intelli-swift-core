/**
 * 区域管理器
 *
 * Created by GUY on 2016/3/17.
 * @class BI.CrossTableRegionsManager
 * @extends BI.RegionsManager
 */
BI.CrossTableRegionsManager = BI.inherit(BI.RegionsManager, {

    _defaultConfig: function () {
        return BI.extend(BI.CrossTableRegionsManager.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-cross-table-regions-manager",
            dimensionCreator: BI.emptyFn,
            wId: ""
        });
    },

    _init: function () {
        BI.CrossTableRegionsManager.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var d1Header = this._createDimension1RegionHeader();
        var d2Header = this._createDimension2RegionHeader();
        var tHeader = this._createTargetRegionHeader();
        this.wrappers[BICst.REGION.DIMENSION1] = this._createDimension1RegionWrapper();
        this.wrappers[BICst.REGION.DIMENSION2] = this._createDimension2RegionWrapper();
        this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper();
        var items = [{
            type: "bi.vtape",
            cls: "dimension-region-manager",
            items: [{
                el: d1Header,
                height: 26
            }, {
                el: this.wrappers[BICst.REGION.DIMENSION1]
            }]
        }, {
            type: "bi.vtape",
            cls: "dimension-region-manager",
            items: [{
                el: d2Header,
                height: 26
            }, {
                el: this.wrappers[BICst.REGION.DIMENSION2]
            }]
        }, {
            type: "bi.vtape",
            cls: "target-region-manager",
            items: [{
                el: tHeader,
                height: 26
            }, {
                el: this.wrappers[BICst.REGION.TARGET1]
            }]
        }];

        BI.createWidget({
            type: "bi.float_center",
            element: this.element,
            hgap: 10,
            vgap: 10,
            items: items
        })
    },

    _createDimension1RegionHeader: function () {
        var self = this, o = this.options;
        var header = BI.createWidget({
            type: "bi.region_header",
            height: 26,
            titleName: BI.i18nText("BI-Row_Header"),
            dimensionCreator: o.dimensionCreator,
            wId: o.wId,
            viewType: BICst.REGION.DIMENSION1
        });
        return header;
    },

    _createDimension2RegionHeader: function () {
        var self = this, o = this.options;
        var header = BI.createWidget({
            type: "bi.region_header",
            height: 26,
            titleName: BI.i18nText("BI-Column_Header"),
            dimensionCreator: o.dimensionCreator,
            wId: o.wId,
            viewType: BICst.REGION.DIMENSION2
        });
        return header;
    },

    _createTargetRegionHeader: function () {
        var self = this, o = this.options;
        var header = BI.createWidget({
            type: "bi.calculate_target_region_header",
            height: 26,
            titleName: BI.i18nText("BI-Target"),
            dimensionCreator: o.dimensionCreator,
            wId: o.wId,
            viewType: BICst.REGION.TARGET1
        });
        return header;
    },

    _createDimension1RegionWrapper: function () {
        var self = this, o = this.options;

        var region = BI.createWidget({
            type: "bi.dimension_region",
            dimensionCreator: function (dId, op) {
                return o.dimensionCreator(dId, BICst.REGION.DIMENSION1, op)
            },
            containment: this,
            wId: o.wId,
            viewType: BICst.REGION.DIMENSION1,
            regionType: BICst.REGION.DIMENSION1
        });
        region.on(BI.AbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.RegionsManager.EVENT_CHANGE, arguments);
        });
        return region;
    },

    _createDimension2RegionWrapper: function () {
        var self = this, o = this.options;

        var region = BI.createWidget({
            type: "bi.dimension_region",
            dimensionCreator: function (dId, op) {
                return o.dimensionCreator(dId, BICst.REGION.DIMENSION2, op)
            },
            containment: this,
            wId: o.wId,
            viewType: BICst.REGION.DIMENSION2,
            regionType: BICst.REGION.DIMENSION2
        });
        region.on(BI.AbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.RegionsManager.EVENT_CHANGE, arguments);
        });
        return region;
    },

    _createTargetRegionWrapper: function () {
        var self = this, o = this.options;

        var region = BI.createWidget({
            type: "bi.target_region",
            dimensionCreator: function (dId, op) {
                return o.dimensionCreator(dId, BICst.REGION.TARGET1, op)
            },
            containment: this,
            wId: o.wId,
            viewType: BICst.REGION.TARGET1,
            regionType: BICst.REGION.TARGET1
        });
        region.on(BI.AbstractRegion.EVENT_CHANGE, function () {
            self.fireEvent(BI.RegionsManager.EVENT_CHANGE, arguments);
        });
        return region;
    }
});

$.shortcut('bi.cross_table_regions_manager', BI.CrossTableRegionsManager);