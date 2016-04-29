/**
 * Created by 小灰灰 on 2016/3/23.
 */
BI.ETLRenamePopoverController = BI.inherit(BI.Controller, {

    _defaultConfig: function () {
        return BI.extend(BI.ETLRenamePopoverController.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BI.ETLRenamePopoverController.superclass._init.apply(this, arguments)
    },

    setWidget : function( widget) {
        this.widget = widget;
    },

    showPopover : function (name, renameChecker, callback) {
        var renamePopover = BI.createWidget({
            type: "bi.etl_table_rename_popover",
            renameChecker : renameChecker
        });
        renamePopover.on(BI.ETLTableRenamePopover.EVENT_CHANGE, function (v) {
            if (BI.isFunction(callback)){
                callback.apply(this, [v]);
            }
        });
        BI.Popovers.remove("etlTableRename");
        BI.Popovers.create("etlTableRename", renamePopover, {width : 400, height : 320}).open("etlTableRename");
        renamePopover.populate(name);
        renamePopover.setTemplateNameFocus();
    }
})
