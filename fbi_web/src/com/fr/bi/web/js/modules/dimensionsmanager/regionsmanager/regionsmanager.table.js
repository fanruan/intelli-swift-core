/**
 * 区域管理器
 *
 * Created by GUY on 2016/3/17.
 * @class BI.TableRegionsManager
 * @extends BI.RegionsManager
 */
BI.TableRegionsManager = BI.inherit(BI.RegionsManager, {

    _defaultConfig: function () {
        return BI.extend(BI.TableRegionsManager.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-table-regions-manager",
            dimensionCreator: BI.emptyFn,
            wId: ""
        });
    },

    _init: function () {
        BI.TableRegionsManager.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.wrappers = {};
        var dHeader = this._createDimensionRegionHeader();
        var tHeader = this._createTargetRegionHeader();
        this.wrappers[BICst.REGION.DIMENSION1] = this._createDimensionRegionWrapper();
        this.wrappers[BICst.REGION.TARGET1] = this._createTargetRegionWrapper();
        var items = [{
            type: "bi.vtape",
            cls: "dimension-region-manager",
            items: [{
                el: dHeader,
                height: 26
            }, {
                el: this.wrappers[BICst.REGION.DIMENSION1]
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

    _createDimensionRegionHeader: function () {
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

    _createDimensionRegionWrapper: function () {
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

    _createTargetRegionWrapper: function () {
        var self = this, o = this.options;
        var region = BI.createWidget({
            type: "bi.target_region",
            containment: this,
            dimensionCreator: function (dId, op) {
                return o.dimensionCreator(dId, BICst.REGION.TARGET1, op)
            },
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

$.shortcut('bi.table_regions_manager', BI.TableRegionsManager);