/**
 * Created by 小灰灰 on 2016/4/11.
 */
BI.ETLTableWarningPopover = BI.inherit(BI.ETLTableNamePopover, {

    _defaultConfig : function () {
        var conf = BI.ETLTableWarningPopover.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            text : BI.i18nText("BI-ETL_Saving_Warning_Text")
        })
    },

    rebuildNorth: function (north) {
        var self = this, o = this.options;
        BI.createWidget({
            type: "bi.label",
            element: north,
            text:  BI.i18nText('BI-Warning'),
            textAlign: "left",
            height : self._constants.NORTH_HEIGHT
        });
        return true
    },

    rebuildCenter: function (center) {
        var self = this, o = this.options;
        BI.createWidget({
            type: "bi.vertical",
            cls: "bi-etl-rename-center",
            element: center,
            items: [{
                type: "bi.label",
                text: o.text,
                textAlign: "left",
                whiteSpace : "normal",
                cls: "rename-label",
                hgap: self._constants.HGAP
            }],
            vgap: self._constants.VGAP
        })
    },

    end: function(){
        this.fireEvent(BI.ETLTableWarningPopover.EVENT_CHANGE);
    }

});
BI.ETLTableWarningPopover.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.etl_table_name_warning_popover", BI.ETLTableWarningPopover);