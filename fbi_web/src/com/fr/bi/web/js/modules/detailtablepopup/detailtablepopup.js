/**
 * 指标弹出明细表
 *
 * Created by GUY on 2016/5/13.
 * @class BI.DetailTablePopup
 * @extends BI.Widget
 */
BI.DetailTablePopup = BI.inherit(BI.Widget, {

    constants: {
        DETAIL_NORTH_HEIGHT: 40,
        DETAIL_TAB_HEIGHT: 40,
        DETAIL_WEST_WIDTH: 280,
        DETAIL_DATA_STYLE_HEIGHT: 200,
        DETAIL_GAP_NORMAL: 10,
        DETAIL_PANE_HORIZONTAL_GAP: 20
    },

    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-table-popup",
            dId: ""
        });
    },

    _init: function () {
        BI.DetailTablePopup.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var id = BI.UUID();
        var wId = BI.Utils.getWidgetIDByDimensionID(o.dId);
        var dimensionsIds = BI.Utils.getAllDimDimensionIDs(wId);
        var data = {};
        var currentId = o.dId;
        if(BI.Utils.isCalculateTargetByDimensionID(currentId)){
            currentId = BI.Utils.getExpressionValuesByDimensionID(currentId);
        }
        data.id = id;
        data.dimensions = {};
        data.view = {};
        data.view[BICst.REGION.DIMENSION1] = dimensionsIds.concat(currentId);
        BI.each(dimensionsIds.concat(currentId), function (i, dId) {
            var name = BI.Utils.getDimensionNameByID(dId);
            data.dimensions[dId] = {
                name: name,
                used: true,
                _src: BI.Utils.getDimensionSrcByID(dId)
            }
        });
        this.view = BI.Factory.createView("", BIDezi.Views.get("/detailtablepopup"), data, {
            element: this.element
        }, null);
        this.view.populate();
        this.view.on("end", function () {
            self.fireEvent(BI.DetailTablePopup.EVENT_COMPLETE);
        });
    },

    destroy: function () {
        this.view.model.destroy();
        BI.DetailTablePopup.superclass.destroy.apply(this, arguments);
    }
});

BI.extend(BI.DetailTablePopup, {
    SHARING_POOL_KEY: "detailtablepopupwidget"
});

BI.DetailTablePopup.EVENT_COMPLETE = "DetailTablePopup.EVENT_COMPLETE";
$.shortcut('bi.detail_table_popup', BI.DetailTablePopup);