/**
 * Created by windy on 2016/10/25.
 */
BI.PointerMapRegion = BI.inherit(BI.Widget, {

    constants: {
        TITLE_ICON_HEIGHT: 20,
        TITLE_ICON_WIDTH: 20,
        REGION_HEIGHT_NORMAL: 25,
        REGION_DIMENSION_GAP: 5,
        SUB_REGION_COUNT: 3,
        USE_POINT: 1,
        NOT_USE_POINT: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.PointerMapRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-point-map-region",
            titleName: ""
        });
    },

    _init: function () {
        BI.PointerMapRegion.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.pointRegions = {};
        this.regions = {};
        this.usePont = BI.createWidget({
            type: "bi.multi_select_item",
            logic: {
                dynamic: true
            },
            text: BI.i18nText("BI-Use_Lat_Lng")
        })

        this.usePont.on(BI.BasicButton.EVENT_CHANGE, function(){
            self._changeRegionState(this.isSelected());
            self.fireEvent(BI.PointerMapRegion.EVENT_CHANGE);
        });

        this.tab = BI.createWidget({
            type: "bi.tab",
            cardCreator: BI.bind(this._createTabs, this)
        });

        this.tab.setSelect(this.constants.NOT_USE_POINT);

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
                        },
                        east: {
                            el: this.usePont,
                            height: this.constants.REGION_HEIGHT_NORMAL,
                            right: this.constants.REGION_DIMENSION_GAP
                        }
                    },
                    cls: "region-north"
                },
                height: this.constants.REGION_HEIGHT_NORMAL
            }, {
                type: "bi.default",
                items: [this.tab]
            }]
        });
    },

    _createTabs: function(v){
        var self = this, c = this.constants, o = this.options;
        switch (v){
            case c.NOT_USE_POINT:
                this.center = BI.createWidget({
                    type: "bi.main_point_map_region",
                    dimensionCreator: o.dimensionCreator,
                    wId: o.wId,
                    regionType: BICst.REGION.DIMENSION1
                });
                return this.center;
            case c.USE_POINT:
                this.pointCenter = BI.createWidget({
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
                return this.pointCenter;

        }
    },

    _assertPointRegions: function(){
        var self = this, o = this.options;
        if(BI.size(this.pointRegions) === 0){
            BI.each(BI.makeArray(this.constants.SUB_REGION_COUNT, null), function(i, v){
                var newRegionType = BI.parseInt(BICst.REGION.DIMENSION1) + i + "";
                self.pointRegions[newRegionType] = BI.createWidget({
                    type: "bi.sub_point_map_region",
                    dimensionCreator: o.dimensionCreator,
                    wId: o.wId,
                    regionType: newRegionType
                })
                self.pointCenter.addItems([self.pointRegions[newRegionType]]);
            })
        }
    },

    _assertRegions: function(){
        if(!BI.has(this.regions, BICst.REGION.DIMENSION1)){
            this.regions[BICst.REGION.DIMENSION1] = this.center;
        }
    },

    _changeRegionState: function(usePoint){
        var self = this, o = this.options, c = this.constants;
        if(usePoint === true){
            var value = this.tab.getValue();
            this.tab.setSelect(c.USE_POINT);
            this._assertPointRegions();
            BI.each(this.pointRegions, function(key, region){
                if(key === BICst.REGION.DIMENSION1){
                    region.populate(value);
                }else{
                    region.populate();
                }
            })
        }else{
            var value = this.getValue();
            this.tab.setSelect(c.NOT_USE_POINT);
            this._assertRegions();
            this.tab.populate(value);
        }
    },

    getRegions: function () {
        return this.usePont.isSelected() ?  this.pointRegions : this.regions;
    },

    getSortableCenter: function(){
        return this.center;
    },

    getValue: function () {
        var c = this.constants;
        var dimensions = $(".dimension-container", this.tab.getSelect() === c.USE_POINT ? this.pointCenter.element : this.center.element);
        var result = [];
        BI.each(dimensions, function (i, dom) {
            result.push($(dom).data("dId"));
        });
        return result;
    },

    populate: function (type, dimensions) {
        var c = this.constants;
        if(this.usePont.isSelected()){
            this.tab.setSelect(c.USE_POINT);
            this._assertPointRegions();
            this.pointRegions[type].populate(dimensions);
        }else{
            this.tab.setSelect(c.NOT_USE_POINT);
            this._assertRegions();
            this.regions[type].populate(dimensions);
        }
    }
});
BI.PointerMapRegion.EVENT_CHANGE = "PointerMapRegion.EVENT_CHANGE";
$.shortcut("bi.pointer_map_region", BI.PointerMapRegion);