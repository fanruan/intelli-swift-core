BI.DetailTableAspect = function () {
    var self = this, o = this.options;
    var assertTip = function () {
        if (!self.tipPane) {
            self.tipPane = BI.createWidget({
                type: "bi.layout",
                cls: self.attr("status") === BICst.WIDGET_STATUS.EDIT ? "table-detail-text-tip-background" : "table-detail-tip-background"
            });
            BI.createWidget({
                type: "bi.absolute",
                element: self.element,
                items: [{
                    el: self.tipPane,
                    top: 0,
                    bottom: 0,
                    left: 0,
                    right: 0
                }]
            })
        }
    };

    BI.aspect.before(this, "populate", function () {
        if (BI.Utils.getAllUsableDimensionIDs(o.wId).length === 0) {
            assertTip();
            self.tipPane.setVisible(true);
            return false;
        }
        self.tipPane && self.tipPane.setVisible(false);
    })
};