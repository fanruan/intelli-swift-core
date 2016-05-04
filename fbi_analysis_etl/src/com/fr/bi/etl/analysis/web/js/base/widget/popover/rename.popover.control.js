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
        renamePopover.on(BI.PopoverSection.EVENT_CLOSE, function () {
            BI.Layers.hide(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
        })
        BI.Popovers.remove("etlTableRename");
        BI.Popovers.create("etlTableRename", renamePopover, {width : 400, height : 320, container :  BI.Layers.create(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER)}).open("etlTableRename");
        BI.Layers.show(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
        renamePopover.populate(name);
        renamePopover.setTemplateNameFocus();
    }
})
