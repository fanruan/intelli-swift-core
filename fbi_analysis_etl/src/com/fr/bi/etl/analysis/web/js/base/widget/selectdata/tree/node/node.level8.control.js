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
        return !(['a', '1', 'pony'].contains(v));
    },

    setWidget : function( widget) {
        this.widget = widget;
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
                BI.ETLReq.reqDeleteTable({id: option.id}, BI.emptyFn);
                return;
            case ETLCst.ANALYSIS_TABLE_SET.COPY :
                BI.ETLReq.reqSaveTable({id: option.id,new_id : BI.UUID(),name : option.text +'copy'}, BI.emptyFn);
                return;
        }

    }
})