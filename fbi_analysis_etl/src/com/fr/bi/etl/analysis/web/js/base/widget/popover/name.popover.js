/**
 * Created by 小灰灰 on 2016/4/11.
 */
BI.ETLTableNamePopover = BI.inherit(BI.ETLTableRenamePopover, {
    rebuildNorth: function (north) {
        var self = this, o = this.options;
        BI.createWidget({
            type: "bi.label",
            element: north,
            text:  BI.i18nText('BI-ETL_Save_Title'),
            textAlign: "left",
            height : self._constants.NORTH_HEIGHT
        });
        return true
    },

    end: function(){
        this.fireEvent(BI.ETLTableNamePopover.EVENT_CHANGE, this.name.getValue());
    }

});
BI.ETLTableNamePopover.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.etl_table_name_popover", BI.ETLTableNamePopover);