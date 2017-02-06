BI.AnalysisETLSelectDataPaneController = BI.inherit(BI.MVCController, {

    _construct: function () {
        this.id = BI.UUID();
    },

    setEnableTables: function (tables) {
        this.tables = tables;
        BI.Broadcasts.send(BICst.BROADCAST.SRC_PREFIX + this._getId());
    },

    _getId: function () {
        return this.id;
    },

    isEnable: function (tableId) {
        //todo 这边不能这么改 如果有两个螺旋分析A和B, 且B使用了A, 那么编辑B的时候此时总是return false
        //if (BI.isNotNull(Pool.current_edit_etl_used) && Pool.current_edit_etl_used.contains(tableId)) {
        //    return false;
        //}
        return BI.isNull(this.tables) || BI.isEmptyArray(this.tables) || BI.deepContains(this.tables, tableId);
    },


    registerEvents: function (item, tableId) {
        var enable = this.isEnable(tableId)
        item.setEnable(enable)
        var self = this;
        BI.Broadcasts.on(BICst.BROADCAST.SRC_PREFIX + this._getId(), function () {
            var enable = self.isEnable(tableId);
            item.setEnable(enable)
            if (enable === false) {
                item.fireEvent(BI.Controller.EVENT_CHANGE, BI.Events.COLLAPSE)
            }
        })
    },

    populate: function (widget, model) {
        var ids = BI.Utils.getAllPackageIDs();
        widget.service.setPackage(ids[0]);
    }

})