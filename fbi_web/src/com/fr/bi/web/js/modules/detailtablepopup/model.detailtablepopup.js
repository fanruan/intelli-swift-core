/**
 *
 * Created by GUY on 2016/5/14.
 * @class BI.DetailTablePopupModel
 * @extends FR.OB
 */
BI.DetailTablePopupModel = BI.inherit(FR.OB, {

    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePopupModel.superclass._defaultConfig.apply(this, arguments), {
            dId: ""
        });
    },

    _init: function () {
        BI.DetailTablePopupModel.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.id = BI.UUID();
        this.wId = BI.Utils.getWidgetIDByDimensionID(o.dId);
        this.dimensionsIds = BI.Utils.getAllDimDimensionIDs(this.wId);
    },

    getId: function () {
        return this.id;
    },

    getAllDimensionIDs: function(){

    },

    getTableIDByDimensionID: function(){

    }
});