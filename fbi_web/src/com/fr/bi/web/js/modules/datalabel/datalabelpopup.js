/**
 * Created by fay on 2016/7/21.
 */
BI.DataLabelPopup = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend( BI.DataLabelPopup.superclass._defaultConfig.apply(this, arguments), {
            width: 600,
            height: 500
        })
    },

    _init: function () {
        BI.DataLabelPopup.superclass._init.apply(this, arguments);
    },

    rebuildNorth : function(north) {
        var o = this.options;
        var name = BI.Utils.getDimensionNameByID(o.dId);
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: "数据标签样式",
            height: 50,
            textAlign: "left",
            lgap: 10
        });
        return true;
    },

    rebuildCenter : function(center) {
        var o = this.options;
        this.targetFilterPane = BI.createWidget({
            type: "bi.data_label",
            element: center,
            dId: o.dId
        });
        return true;
    },

    // populate: function(){
    //     this.targetFilterPane.populate();
    // },

    // end: function(){
    //     this.fireEvent(BI.DataLabelPopup.EVENT_CHANGE, this.targetFilterPane.getValue()[0]);
    // }
});

BI.DataLabelPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.data_label_popup", BI.DataLabelPopup);