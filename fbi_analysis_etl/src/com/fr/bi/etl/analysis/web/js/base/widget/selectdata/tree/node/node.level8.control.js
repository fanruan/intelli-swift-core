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

    afterClickList : function (v) {
        var self = this;
        switch (v){
            case ETLCst.ANALYSIS_TABLE_SET.EDIT :
                BI.createWidget({
                    type : "bi.analysis_etl_main",
                    element:BI.Layers.create(ETLCst.ANALYSIS_LAYER, "body"),
                    data:v
                })
                return;
            case ETLCst.ANALYSIS_TABLE_SET.RENAME :
                self.renameController.showPopover('rr', self._renameChecker);
                return;
        }

    }
})