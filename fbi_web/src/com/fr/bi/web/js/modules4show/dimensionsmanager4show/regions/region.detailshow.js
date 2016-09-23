/**
 * Created by Kary on 2016/4/11.
 */
BI.DetailRegionShow = BI.inherit(BI.AbstractRegion, {
    _defaultConfig: function () {
        var conf = BI.DetailRegionShow.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            extraCls: "bi-detail-region bi-dimension-region"
        })

    },

    _init: function () {
        BI.DetailRegionShow.superclass._init.apply(this, arguments);
    },


    _getFields: function () {
        var dimensions = this.getValue();
        var fieldIds = [];
        BI.each(dimensions, function (i, did) {
            fieldIds.push(BI.Utils.getFieldIDByDimensionID(did))
        });
        return fieldIds;
    },

    _getRegionType: function () {
        return BICst.REGION.DIMENSION1;
    },

    _setDimensionRelation: function (dId, options) {
        var self = this, o = this.options;
        options || (options = {});
        o.dimensionCreator(dId, this._getRegionType(), options);
    },

    _setCalculateTarget: function (dId, options) {
        var o = this.options;
        var dim = o.dimensionCreator(dId, this._getRegionType(), options);
        var container = BI.createWidget({
            type: "bi.absolute",
            cls: "dimension-container",
            data: {
                dId: dId
            },
            height: 25,
            items: [{
                el: dim,
                left: 0,
                top: 0,
                right: 0,
                bottom: 0
            }]
        });
        this.store[dId] = container;
        this.center.addItem(this.store[dId]);
    },

    _createDimension: function (dId, options) {
        var self = this, o = this.options;
        options || (options = {});
        var dim = o.dimensionCreator(dId, this._getRegionType(), options);
        var container = BI.createWidget({
            type: "bi.absolute",
            cls: "dimension-container",
            data: {
                dId: dId
            },
            height: 25,
            items: [{
                el: dim,
                left: 0,
                top: 0,
                right: 0,
                bottom: 0
            }]
        });
        return container;
    },


    _createCalTargetItem: function () {
        var calItem = {
            name: "",
            type: BICst.TARGET_TYPE.FORMULA
        };
        return calItem;
    },


    getValue: function () {
        var result = [];
        var dimensions = $(".dimension-container", this.center.element);
        BI.each(dimensions, function (i, dom) {
            result.push($(dom).data("dId"));
        });
        return result;
    }
});
BI.DetailRegionShow.EVENT_RELATION_SET = "EVENT_RELATION_SET";
$.shortcut("bi.detail_region_show", BI.DetailRegionShow);
