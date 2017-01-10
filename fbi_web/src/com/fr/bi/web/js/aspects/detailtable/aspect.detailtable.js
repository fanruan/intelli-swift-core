BI.DetailTableAspect = function () {
    var self = this;
    var assertTip = function () {
        if (!self.tipPane) {
            self.tipPane = BI.createWidget({
                type: "bi.layout",
                height: 100
            });
            self.textLabel = BI.createWidget({
                type: "bi.label",
                text: BI.i18nText("BI-Empty_Widget_Tip"),
                cls: "empty-widget-tip",
                height: 30
            });
            self.contactAdmin = BI.createWidget({
                type: "bi.label",
                text: BI.i18nText("BI-Please_Contact_Admin"),
                cls: "contact-admin-tip",
                height: 30
            });
            self.mainPane = BI.createWidget({
                type: "bi.center_adapt",
                cls: "widget-tip-pane",
                items: [{
                    type: "bi.vertical",
                    items: [self.tipPane, self.textLabel, self.contactAdmin]
                }]
            });
            BI.createWidget({
                type: "bi.absolute",
                element: self.element,
                scrollable: false,
                items: [{
                    el: self.mainPane,
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0
                }]
            })
        }
    };

    BI.aspect.before(this, "populate", function () {
        var allUsableDims = BI.Utils.getAllUsableDimensionIDs(self.options.wId);
        var cls = "table-detail-tip-background";
        if (allUsableDims.length === 0) {
            assertTip();
            self.mainPane.setVisible(true);
            self.textLabel.setText(BI.i18nText("BI-Empty_Widget_Tip"));
            self.contactAdmin.setVisible(false);
            self.tipPane.element.removeClass().addClass(cls);
            self.textLabel.setVisible(self.options.status === BICst.WIDGET_STATUS.EDIT);
            return false;
        }
        if (!BI.Utils.isAllFieldsExistByWidgetID(self.options.wId)) {
            assertTip();
            self.mainPane.setVisible(true);
            self.textLabel.setVisible(true);
            self.textLabel.setText(BI.i18nText("BI-Data_Miss_Tip"));
            self.contactAdmin.setVisible(true);
            self.tipPane.element.removeClass().addClass("data-miss-background");
            return false;
        }
        self.mainPane && self.mainPane.setVisible(false);
    })
};