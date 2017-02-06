/**
 * 组件请求数据超时提示
 * Created by Young's on 2017/2/4.
 */
BI.TimeoutToast = BI.inherit(BI.Tip, {
    _defaultConfig: function () {
        return BI.extend(BI.TimeoutToast.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-timeout-toast"
        });
    },

    _init: function () {
        BI.TimeoutToast.superclass._init.apply(this, arguments);
        var self = this;
        this.requests = {};
        this.toast = BI.createWidget({
            type: "bi.vertical_adapt",
            element: this.element,
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Request_Time_Out_Toast_Tip")
            }, {
                type: "bi.text_button",
                cls: "cancel-button",
                width: 60,
                height: 22,
                text: BI.i18nText("BI-Cancel"),
                title: BI.i18nText("BI-Cancel"),
                handler: function () {
                    self.toast.element.slideUp(500);
                    BI.each(self.requests, function (i, reqArgs) {
                        var callbacks = reqArgs.callbacks;
                        if (BI.isNotNull(callbacks) && BI.isFunction(callbacks.done)) {
                            callbacks.done();
                        }
                    });
                    self.requests = {};
                }
            }, {
                type: "bi.text_button",
                cls: "retry-button",
                width: 60,
                height: 22,
                text: BI.i18nText("BI-Retry"),
                title: BI.i18nText("BI-Retry"),
                handler: function () {
                    self.toast.element.slideUp(500);
                    self._retryAll();
                }
            }, {
                type: "bi.icon_button",
                cls: "close-font",
                width: 20,
                height: 20,
                title: BI.i18nText("BI-Close"),
                handler: function () {
                    self.toast.element.slideUp(500);
                }
            }],
            width: 520,
            height: 30,
            hgap: 2
        });

        BI.createWidget({
            type: "bi.absolute",
            element: $("body"),
            items: [{
                el: this.toast,
                left: "50%",
                top: 0
            }]
        });
        this.toast.element.css({"margin-left": -1 * this.toast.element.outerWidth() / 2});
        this.toast.setVisible(false);
    },

    _retryAll: function () {
        var clonedRequests = BI.deepClone(this.requests);
        this.requests = {};
        BI.each(clonedRequests, function (i, reqArgs) {
            BI.Utils.getWidgetDataByID(reqArgs.wid, reqArgs.callbacks, reqArgs.options);
        });
    },

    addReq: function (id, wid, callbacks, options) {
        var self = this;
        if (BI.size(this.requests) === 0) {
            setTimeout(function () {
                if (BI.isNotNull(self.requests[id])) {
                    self.toast.element.slideDown(500);
                }
            }, 5 * 60 * 1000);  //5 min
        }
        this.requests[id] = {
            wid: wid,
            callbacks: callbacks,
            options: options
        };
    },

    removeReq: function (id) {
        if (BI.isNotNull(this.requests[id])) {
            delete this.requests[id];
        }
        if (BI.size(this.requests) === 0) {
            this.toast.element.slideUp(500);
        }
    },

    getReq: function (id) {
        return this.requests[id];
    }
});
$.shortcut("bi.timeout_toast", BI.TimeoutToast);