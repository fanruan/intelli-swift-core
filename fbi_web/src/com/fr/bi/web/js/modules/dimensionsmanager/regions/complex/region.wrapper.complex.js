/**
 * Created by Young's on 2016/9/12.
 */
BI.ComplexRegionWrapper = BI.inherit(BI.Widget, {

    constants: {
        TITLE_ICON_HEIGHT: 20,
        TITLE_ICON_WIDTH: 20,
        REGION_HEIGHT_NORMAL: 25,
        REGION_DIMENSION_GAP: 5
    },

    _defaultConfig: function () {
        return BI.extend(BI.ComplexRegionWrapper.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-complex-region-wrapper",
            titleName: ""
        })
    },

    _init: function () {
        BI.ComplexRegionWrapper.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.regions = {};
        this.center = BI.createWidget({
            type: "bi.vertical",
            cls: "regions-container",
            scrolly: true,
            width: "100%",
            height: "100%",
            hgap: this.constants.REGION_DIMENSION_GAP,
            vgap: this.constants.REGION_DIMENSION_GAP
        });

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.border",
                    items: {
                        west: {
                            el: {
                                type: "bi.label",
                                cls: "region-north-title",
                                text: o.titleName,
                                height: this.constants.REGION_HEIGHT_NORMAL
                            },
                            height: this.constants.REGION_HEIGHT_NORMAL,
                            left: this.constants.REGION_DIMENSION_GAP
                        }
                    },
                    cls: "region-north"
                },
                height: this.constants.REGION_HEIGHT_NORMAL
            }, {
                type: "bi.default",
                items: [this.center]
            }]
        });
    },

    refreshRegion: function(type, dimensions) {
        var o = this.options;
        if (BI.isNull(this.regions[type])) {
            this.regions[type] = BI.createWidget({
                type: "bi.complex_dimension_region",
                dimensionCreator: o.dimensionCreator,
                wId: o.wId,
                regionType: o.regionType
            });
            this.center.addItem(this.regions[type]);
        }
        this.regions[type].populate(dimensions);
    },
    
    getRegions: function() {
        return this.regions;  
    },

    populate: function () {

    }
});
$.shortcut("bi.complex_region_wrapper", BI.ComplexRegionWrapper);