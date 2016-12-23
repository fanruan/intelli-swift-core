/**
 * Created by roy on 16/9/29.
 */
/**
 * @class BI.RefreshTableLoadingMask
 * @extend BI.Widget
 * 刷新业务包表字段遮罩层
 */
BI.RefreshTableLoadingMask = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.RefreshTableLoadingMask.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-test-link-loading-mask",
            tableId: ""
        })
    },

    _init: function () {
        BI.RefreshTableLoadingMask.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.maskId = BI.UUID();
        var mask = BI.Maskers.create(this.maskId, o.masker, {offset: o.offset});
        BI.Maskers.show(this.maskId);

        this.wrapper = BI.createWidget({
            type: "bi.vertical",
            width: 500,
            height: 340,
            vgap: 10
        });
        this._refreshTable();

        BI.createWidget({
            type: "bi.absolute",
            element: mask,
            cls: "bi-test-link-loading-mask",
            items: [{
                el: {
                    type: "bi.layout",
                    cls: "mask-pane"
                },
                top: 0,
                left: 0,
                bottom: 0,
                right: 0
            }, {
                el: {
                    type: "bi.center_adapt",
                    items: [this.wrapper]
                },
                top: 0,
                left: 0,
                right: 0,
                bottom: 0
            }]
        })
    },

    _refreshTable: function () {
        var self = this;
        this.wrapper.empty();
        this.isCancel = false;
        var items = [{
            type: "bi.center_adapt",
            cls: "loading-bar-icon",
            items: [{
                type: "bi.icon",
                width: 208,
                height: 15
            }]
        }, {
            type: "bi.label",
            cls: "loading-comment",
            text: BI.i18nText("BI-Refreshing"),
            height: 30
        }, {
            type: "bi.center_adapt",
            items: [{
                type: "bi.button",
                level: "ignore",
                text: BI.i18nText("BI-Cancel"),
                height: 28,
                width: 90,
                handler: function () {
                    self.isCancel = true;
                    BI.Maskers.remove(self.maskId);
                }
            }]
        }];
        this.wrapper.populate(items);
        BI.Utils.getTablesDetailInfoByTables4Refresh([this.options.table], function (data) {
            if (self.isCancel === true) {
                self.isCancel = false;
                return;
            }
            var fields = data[0].fields;
            if (fields[0].length !== 0) {
                setTimeout(function () {
                    BI.Maskers.remove(self.maskId);
                }, 500);
                self.fireEvent(BI.RefreshTableLoadingMask.EVENT_REFRESH_SUCCESS, data[0]);
            } else {
                self.wrapper.empty();
                var cancelButton = BI.createWidget({
                    type: "bi.button",
                    text: BI.i18nText("BI-Cancel"),
                    level: "ignore",
                    width: 90,
                    height: 28,
                    handler: function () {
                        BI.Maskers.remove(self.maskId);
                        self.fireEvent(BI.RefreshTableLoadingMask.EVENT_CANCEL_REFRESH);
                    }
                });
                var retryButton = BI.createWidget({
                    type: "bi.button",
                    text: BI.i18nText("BI-Re_Connection"),
                    level: "ignore",
                    width: 90,
                    height: 28,
                    handler: function () {
                        self._refreshTable();
                    }
                });

                self.wrapper.populate([{
                    type: "bi.center_adapt",
                    cls: "data-link-test-fail-icon",
                    items: [{
                        type: "bi.icon",
                        width: 126,
                        height: 126
                    }]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Refresh_Table_Failure"),
                    cls: "test-connection-fail-comment"
                }, {
                    type: "bi.horizontal_float",
                    items: [{
                        type: "bi.horizontal",
                        items: [cancelButton, retryButton],
                        hgap: 5
                    }],
                    height: 30,
                    hgap: 5
                }]);
            }
        })
    }


});
BI.RefreshTableLoadingMask.EVENT_CANCEL_REFRESH = "EVENT_CANCEL_REFRESH";
BI.RefreshTableLoadingMask.EVENT_REFRESH_SUCCESS = "EVENT_REFRESH_SUCCESS";
$.shortcut("bi.refresh_table_loading_mask", BI.RefreshTableLoadingMask);