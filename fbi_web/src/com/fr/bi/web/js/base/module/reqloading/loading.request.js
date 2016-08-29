/**
 * Created by Young's on 2016/8/17.
 * 加载面板，一般只用于 requestAsync 中
 */
BI.RequstLoading = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.RequstLoading.superclass._defaultConfig.apply(this, arguments), {

        });
    },

    _init: function () {
        BI.RequstLoading.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var mask = BI.Maskers.create(BI.RequstLoading.MASK_ID);
        this.callback = o.callback;
        this.paneTab = BI.createWidget({
            type: "bi.tab",
            cardCreator: BI.bind(this._cardCreator, this),
            defaultShowIndex: BI.RequstLoading.ERROR,
            width: 220,
            height: 220
        });
        var tempIcon = BI.createWidget({
            type: "bi.icon_button",
            cls: "data-link-test-fail-icon",
            width: 0,
            height: 0
        });
        BI.createWidget({
            type: "bi.absolute",
            element: $('body'),
            items: [{
                el: tempIcon,
                bottom: 0
            }]
        });

        BI.createWidget({
            type: "bi.absolute",
            element: mask,
            cls: "bi-request-loading",
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
                    items: [this.paneTab]
                },
                top: 0,
                left: 0,
                right: 0,
                bottom: 0
            }]
        });
    },

    _cardCreator: function (v) {
        var self = this;
        switch (v) {
            case BI.RequstLoading.LOADING:
                return BI.createWidget({
                    type: "bi.vertical",
                    items: [{
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
                        text: BI.i18nText("BI-Loading"),
                        height: 30
                    }],
                    width: 208,
                    height: 200,
                    vgap: 10
                });
            case BI.RequstLoading.ERROR:
                var cancel = BI.createWidget({
                    type: "bi.button",
                    text: BI.i18nText("BI-Cancel"),
                    level: "ignore",
                    width: 90,
                    height: 28,
                    handler: function(){
                        BI.Maskers.hide(BI.RequstLoading.MASK_ID);
                    }
                });
                var retry = BI.createWidget({
                    type: "bi.button",
                    text: BI.i18nText("BI-Reload"),
                    level: "ignore",
                    width: 90,
                    height: 28,
                    handler: function(){
                        self.paneTab.setSelect(BI.RequstLoading.LOADING);
                        self.callback();
                    }
                });
                return BI.createWidget({
                    type: "bi.vertical",
                    items: [{
                        type: "bi.center_adapt",
                        cls: "data-link-test-fail-icon",
                        items: [{
                            type: "bi.icon",
                            width: 126,
                            height: 126
                        }]
                    }, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Load_Failed"),
                        cls: "load-fail-comment"
                    }, {
                        type: "bi.left_right_vertical_adapt",
                        items: {
                            left: [cancel],
                            right: [retry]
                        },
                        height: 30
                    }],
                    width: 208,
                    height: 200,
                    vgap: 10
                });
        }
    },

    showLoading: function () {
        BI.Maskers.show(BI.RequstLoading.MASK_ID);
        this.paneTab.setSelect(BI.RequstLoading.LOADING);
    },

    showError: function () {
        BI.Maskers.show(BI.RequstLoading.MASK_ID);
        this.paneTab.setSelect(BI.RequstLoading.ERROR);
    },
    
    setCallback: function(callback) {
        this.callback = callback;
    }
});
BI.extend(BI.RequstLoading, {
    MASK_ID: "___request__loading___",
    LOADING: 1,
    ERROR: 2
});
$.shortcut("bi.request_loading", BI.RequstLoading);