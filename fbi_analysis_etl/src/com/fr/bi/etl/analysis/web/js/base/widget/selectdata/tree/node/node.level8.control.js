/**
 * Created by 小灰灰 on 2016/3/23.
 */
BI.SelectDataLevel8NodeController = BI.inherit(BI.Controller, {

    _defaultConfig: function () {
        return BI.extend(BI.SelectDataLevel8NodeController.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BI.SelectDataLevel8NodeController.superclass._init.apply(this, arguments)
        var self = this;
        self.renameController = new BI.ETLRenamePopoverController();
    },

    _renameChecker : function (v) {
        return !BI.Utils.getAllETLTableNames().contains(v);
    },

    setWidget : function( widget) {
        this.widget = widget;
    },

    _showWarningPop : function (id) {
        var self = this;
        var warningPopover = BI.createWidget({
            type: "bi.etl_table_name_warning_popover",
            text : BI.i18nText('BI-Is_Delete_Table')
        });
        warningPopover.on(BI.ETLTableNamePopover.EVENT_CHANGE, function () {
            BI.ETLReq.reqDeleteTable({id: id}, BI.emptyFn);
        });
        BI.Popovers.remove("etlRemove");
        BI.Popovers.create("etlRemove", warningPopover, {width : 400, height : 320, container:self.widget.element}).open("etlRemove");
    },

    afterClickList : function (v, option) {
        var self = this;
        switch (v){
            case ETLCst.ANALYSIS_TABLE_SET.EDIT :
                BI.ETLReq.reqEditTable({id: option.id}, function (res) {
                    BI.createWidget({
                        type : "bi.analysis_etl_main",
                        element:BI.Layers.create(ETLCst.ANALYSIS_LAYER, "body"),
                        model:res
                    })  
                })
                return;
            case ETLCst.ANALYSIS_TABLE_SET.RENAME :
                self.renameController.showPopover(option.text, self._renameChecker, function (value) {
                    BI.ETLReq.reqRenameTable({id: option.id, name : value}, BI.emptyFn);
                });
                return;
            case ETLCst.ANALYSIS_TABLE_SET.DELETE :
                self._showWarningPop(option.id);
                return;
            case ETLCst.ANALYSIS_TABLE_SET.COPY :
                BI.ETLReq.reqSaveTable({id: option.id,new_id : BI.UUID(),name : BI.Utils.createDistinctName(BI.Utils.getAllETLTableNames(), option.text)}, BI.emptyFn);
                return;
        }

    }
})