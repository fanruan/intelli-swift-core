/**
 * Created by 小灰灰 on 2016/3/23.
 */
BI.SelectDataLevel8NodeController = BI.inherit(BI.Controller, {

    _defaultConfig: function () {
        return BI.extend(BI.SelectDataLevel8NodeController.superclass._defaultConfig.apply(this, arguments), {})
    },

    getMarkArguments: function (id, text) {
        if (BI.isNotEmptyArray(BI.Utils.getFieldIDsOfTableID(id))) {
            return [];
        } else {
            return [text];
        }
    },
    _init: function () {
        BI.SelectDataLevel8NodeController.superclass._init.apply(this, arguments)
        var self = this;
        self.renameController = new BI.ETLRenamePopoverController();
    },

    _renameChecker: function (v, id) {
        return !BI.Utils.getAllETLTableNames(id).contains(v);
    },

    setWidget: function (widget) {
        this.widget = widget;
    },

    startChecker: function (id) {
        var self = this;
        Data.BufferPool.putAnalysisTableStatusFn(id, function(percent) {
            self.widget.showLoading(percent);
        });
    },


    _showRenamePop: function (id, text) {
        var self = this;
        var namePopover = BI.createWidget({
            type: "bi.etl_table_name_popover",
            renameChecker: function (v) {
                return self._renameChecker(v, id)
            }
        });
        namePopover.on(BI.PopoverSection.EVENT_CLOSE, function () {
            BI.Layers.hide(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
        })
        namePopover.on(BI.ETLTableNamePopover.EVENT_CHANGE, function (v, des) {
            BI.ETLReq.reqRenameTable({id: id, name: v, describe: des}, BI.emptyFn);
        });
        BI.Popovers.remove("etlTableName");
        BI.Popovers.create("etlTableName", namePopover, {
            width: 450,
            height: 370,
            container: BI.Layers.create(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER)
        }).open("etlTableName");
        BI.Layers.show(ETLCst.ANALYSIS_POPUP_FOLATBOX_LAYER);
        namePopover.populate(text, BI.isNotEmptyArray(BI.Utils.getFieldIDsOfTableID(id)) ? BI.Utils.getDescribe(id) : '');
        namePopover.setTemplateNameFocus();
    }
})