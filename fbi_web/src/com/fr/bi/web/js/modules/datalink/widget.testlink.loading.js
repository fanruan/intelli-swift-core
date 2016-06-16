/**
 * @class BI.TestLinkLoadingMask
 * @extend BI.Widget
 * 测试数据连接
 */
BI.TestLinkLoadingMask = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.TestLinkLoadingMask.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-test-link-loading-mask"
        })
    },

    _init: function(){
        BI.TestLinkLoadingMask.superclass._init.apply(this, arguments);
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
        this._testConnection();

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

    _testConnection: function(){
        var self = this;
        var link = this.options.link;
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
            cls: "link-name",
            text: link.name,
            height: 30
        }, {
            type: "bi.label",
            cls: "loading-comment",
            text: BI.i18nText("BI-Test_Connection_Loading"),
            height: 30
        }, {
            type: "bi.center_adapt",
            items: [{
                type: "bi.button",
                level: "ignore",
                text: BI.i18nText("BI-Cancel"),
                height: 28,
                width: 90,
                handler: function(){
                    self.isCancel = true;
                    BI.Maskers.remove(self.maskId);
                }
            }]
        }];
        this.wrapper.populate(items);
        BI.Utils.getTestConnectionByLink(link, function(data){
            if(self.isCancel === true){
                self.isCancel = false;
                return;
            }
            if(data.success === true){
                self.wrapper.empty();
                self.wrapper.populate([{
                    type: "bi.center_adapt",
                    cls: "data-link-test-success-icon",
                    items: [{
                        type: "bi.icon",
                        width: 50,
                        height: 50
                    }]
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Test_Connection_Success"),
                    cls: "test-connection-success-comment",
                    height: 30
                }]);
                setTimeout(function(){
                    BI.Maskers.remove(self.maskId);
                }, 3000);
                if(BI.isNotNull(data.schemas)){
                    self.fireEvent(BI.TestLinkLoadingMask.EVENT_SCHEMA_SUCCESS, data.schemas);
                }
            } else {
                var info = data.failureDetail;
                self.wrapper.empty();
                var detailButton = BI.createWidget({
                    type: "bi.text_button",
                    text: BI.i18nText("BI-Detail_Info"),
                    cls: "test-connection-fail-detail-button",
                    handler: function(){
                        if(detailButton.getText() === BI.i18nText("BI-Detail_Info")){
                            detailButton.setText(BI.i18nText("BI-Close_Detail_Info"));
                            detailInfo.setVisible(true);
                        } else {
                            detailButton.setText(BI.i18nText("BI-Detail_Info"));
                            detailInfo.setVisible(false);
                        }
                    }
                });
                var cancelButton = BI.createWidget({
                    type: "bi.button",
                    text: BI.i18nText("BI-Cancel"),
                    level: "ignore",
                    width: 90,
                    height: 28,
                    handler: function(){
                        BI.Maskers.remove(self.maskId);
                    }
                });
                var retryButton = BI.createWidget({
                    type: "bi.button",
                    text: BI.i18nText("BI-Re_Connection"),
                    level: "ignore",
                    width: 90,
                    height: 28,
                    handler: function(){
                        self._testConnection();
                    }
                });
                var detailInfo = BI.createWidget({
                    type: "bi.left",
                    width: 498,
                    height: 100,
                    cls: "test-fail-detail-info",
                    items: [{
                        type: "bi.label",
                        text: info,
                        whiteSpace: "normal",
                        textAlign: "left",
                        textHeight: 20
                    }],
                    hgap: 10
                });
                detailInfo.setVisible(false);

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
                    text: BI.i18nText("BI-Test_Connection_Failed"),
                    cls: "test-connection-fail-comment"
                }, {
                    type: "bi.horizontal_float",
                    items: [{
                        type: "bi.horizontal",
                        items: [detailButton, cancelButton, retryButton],
                        hgap: 5
                    }],
                    height: 30,
                    hgap: 5
                }, detailInfo]);
            }
        })
    }

});
BI.TestLinkLoadingMask.EVENT_CANCEL_TEST = "EVENT_CANCEL_TEST";
BI.TestLinkLoadingMask.EVENT_SCHEMA_SUCCESS = "EVENT_SCHEMA_SUCCESS";
$.shortcut("bi.test_link_loading_mask", BI.TestLinkLoadingMask);