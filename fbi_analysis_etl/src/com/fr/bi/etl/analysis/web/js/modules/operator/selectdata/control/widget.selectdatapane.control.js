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