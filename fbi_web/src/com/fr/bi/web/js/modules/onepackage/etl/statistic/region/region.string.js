/**
 * @class BI.StringRegion
 * @extend BI.Widget
 * 文本控件拖字段区域
 */
BI.StringRegion = BI.inherit(BI.Widget, {

    constants: {
        TITLE_ICON_WIDTH: 20,
        TITLE_ICON_HEIGHT: 20,
        REGION_HEIGHT_NORMAL: 25,
        REGION_DIMENSION_GAP: 5,
        SIZE_A_SEAT: "size-a-seat"
    },

    _defaultConfig: function(){
        return BI.extend(BI.StringRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-string-region",
            titleName: "",
            iconClass: ""
        })
    },

    _init: function(){
        BI.StringRegion.superclass._init.apply(this, arguments);
        var self = this,o = this.options;

        var nav = BI.createWidget({
            type: "bi.center",
            items: [{
                type: "bi.label",
                cls: "bi-region-north-title",
                text: o.titleName,
                height: this.constants.REGION_HEIGHT_NORMAL
            }],
            cls: o.iconClass + " bi-region-nav"
        });

        this.comment = BI.createWidget({
            type: "bi.label",
            cls: "bi-region-comment",
            text: o.titleName,
            height: this.constants.REGION_HEIGHT_NORMAL
        });

        this.center = BI.createWidget({
            type: "bi.button_group",
            scrolly: true,
            cls: "dimension-region-container region-block full-size",
            layouts: [{
                type: "bi.vertical",
                hgap: this.constants.REGION_DIMENSION_GAP,
                vgap: this.constants.REGION_DIMENSION_GAP
            }]
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.center,
            items: [{
                el: this.comment,
                top: 0,
                left: 0,
                right: 0
            }]
        });

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: nav,
                height: this.constants.REGION_HEIGHT_NORMAL
            }, this.center]
        })
    },

    //按照排序后的顺序把维度get出去
    getAllDimensions: function(){
        return this.center.element.sortable("toArray", {"attribute": 'dimensionid'});
    },

    getRegion: function () {
        return this.center;
    },

    addDimension: function (dimension) {
        this.center.addItems([dimension]);
    },

    addPlaceHolder: function () {
        if(BI.isNull(this.placeHolder)){
            this.placeHolder = BI.createWidget({
                type: "bi.label",
                cls: "ui-sortable-place-holder",
                height: this.constants.REGION_HEIGHT_NORMAL,
                text: " ",
                value: this.constants.SIZE_A_SEAT
            });
        }
        this.center.addItems([this.placeHolder]);
    },

    removePlaceHolder: function() {
        if(BI.isNotNull(this.placeHolder)){
            this.center.removeItems(this.constants.SIZE_A_SEAT);
            this.placeHolder = null;
        }
    },

    setCommentVisible: function (b) {
        this.comment.setVisible(b);
    },

    getRegionType: function () {
        return this.options.regionType;
    }
});
$.shortcut("bi.string_region", BI.StringRegion);