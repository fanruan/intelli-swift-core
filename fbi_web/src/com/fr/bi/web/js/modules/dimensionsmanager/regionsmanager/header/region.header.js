/**
 * Created by GUY on 2016/3/17.
 * @class BI.RegionHeader
 * @extends BI.Widget
 */
BI.RegionHeader = BI.inherit(BI.Widget, {

    constants: {
        TITLE_ICON_HEIGHT: 20,
        TITLE_ICON_WIDTH: 20,
        REGION_HEIGHT_NORMAL: 25,
        REGION_DIMENSION_GAP: 5
    },

    _defaultConfig: function () {
        return BI.extend(BI.RegionHeader.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-region-header",
            titleName: "",
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        });
    },

    _init: function () {
        BI.RegionHeader.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var titleName = BI.createWidget({
            type: "bi.icon_text_item",
            invalid: true,
            cls: "region-header-title " + this._getFieldClass(o.viewType) + " " + this._getDimensionType(o.viewType),
            text: o.titleName,
            height: o.height - 1
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: titleName,
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }]
        })
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

    _getDimensionType: function (type) {
        switch (type) {
            case BICst.REGION.DIMENSION1:
            case BICst.REGION.DIMENSION2:
                return "dimension-region-header";
            case BICst.REGION.TARGET1:
            case BICst.REGION.TARGET2:
            case BICst.REGION.TARGET3:
                return "target-region-header";
            default:
                return "dimension-region-header";
        }
    }
});
$.shortcut("bi.region_header", BI.RegionHeader);