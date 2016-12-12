/**
 * Created by fay on 2016/11/16.
 */
BI.AbstractWrapper = BI.inherit(BI.Widget, {

    constants: {
        TITLE_ICON_HEIGHT: 20,
        TITLE_ICON_WIDTH: 20,
        REGION_HEIGHT_NORMAL: 25,
        REGION_DIMENSION_GAP: 5
    },

    _defaultConfig: function () {
        return BI.extend(BI.AbstractWrapper.superclass._defaultConfig.apply(this, arguments), {
            cls: "bi-region-wrapper",
            titleName: "",
            wId: ""
        });
    },

    _init: function () {
        BI.AbstractWrapper.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.regions = {};
        var titleName = BI.createWidget({
            type: "bi.icon_text_item",
            logic: {
                dynamic: true
            },
            cls: "region-north-title " + this._getFieldClass(o.regionType),
            text: o.titleName,
            height: this.constants.REGION_HEIGHT_NORMAL
        });
        var north = BI.createWidget({
            type: "bi.border",
            items: {
                west: {
                    el: titleName,
                    height: this.constants.REGION_HEIGHT_NORMAL,
                    left: this.constants.REGION_DIMENSION_GAP
                }
            },
            cls: "region-north"
        });
        this.center = BI.createWidget({
            type: "bi.button_group",
            layouts: [{
                type: "bi.vertical",
                cls: "regions-container",
                scrolly: true,
                width: "100%",
                height: "100%",
                hgap: this.constants.REGION_DIMENSION_GAP,
                vgap: this.constants.REGION_DIMENSION_GAP
            }]
        });

        this._appendEmptyRegion();
        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: north,
                height: this.constants.REGION_HEIGHT_NORMAL
            }, {
                type: "bi.default",
                items: [this.center]
            }]
        });
    },

    _appendEmptyRegion: function () {

    },

    _addRegionAndDimension: function (data) {

    },

    //排序
    sortRegion: function() {
        var self = this, o = this.options;
        var sortedRegions = this.center.element.sortable("toArray");
        var originalRegionKeys = BI.keys(this.regions);
        var originalRegions = {};
        BI.each(this.regions, function (idx, region) {
            originalRegions[region.getRegionType()] = region;
        });
        self.regions = {};
        BI.each(sortedRegions, function(i, regionType) {
            self.regions[regionType] = originalRegions[originalRegionKeys[i]];
        });
    },

    refreshRegion: function (type, dimensions) {

    },

    _getFieldClass: function (type) {
        switch (type) {
            case BICst.REGION.DIMENSION1:
                return "classify-font";
            case BICst.REGION.DIMENSION2:
                return "classify-font";
            case BICst.REGION.TARGET1:
            case BICst.REGION.TARGET2:
            case BICst.REGION.TARGET3:
                return "series-font";
            default:
                return "classify-font";
        }
    },


    getEmptyRegionValue: function () {
        return this.emptyRegion && this.emptyRegion.getDimensions();
    },

    getWrapperType: function () {
        return this.options.wrapperType;
    },

    getCenterArea: function() {
        return this.center;
    },

    getRegions: function () {
        return this.regions;
    },

    populate: function () {

    }
});
BI.AbstractWrapper.EVENT_CHANGE = "ABSTRACT_WRAPPER_EVENT_CHANGE";