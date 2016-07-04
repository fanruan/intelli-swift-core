/**
 * Created by GUY on 2016/4/26.
 * @class BI.DetailDetailSelectDataLevel0Node4RealTime
 * @extends BI.AbstractDetailDetailSelectDataNode4RealTime
 */
BI.DetailDetailSelectDataLevel0Node4RealTime = BI.inherit(BI.AbstractDetailDetailSelectDataNode4RealTime, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailDetailSelectDataLevel0Node4RealTime.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-detail-select-data-level0-node-4-realtime"
        })
    },
    _init: function () {
        BI.DetailDetailSelectDataLevel0Node4RealTime.superclass._init.apply(this, arguments);
    },

    _createNode: function () {
        var o = this.options;
        return BI.createWidget({
            type: "bi.select_data_level0_node",
            element: this.element,
            warningTitle: BI.i18nText("BI-Time_Only_Use_One_Table"),
            id: o.id,
            pId: o.pId,
            open: o.open,
            text: o.text,
            value: o.value
        });
    }
});

$.shortcut("bi.detail_select_data_level0_node_4_realtime", BI.DetailDetailSelectDataLevel0Node4RealTime);