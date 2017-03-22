/**
 * Created by zcf on 2017/3/8.
 */
BI.CubeLogErrorPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CubeLogErrorPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-log-error-pane",
            driver: BI.createWidget()
        })
    },
    _init: function () {
        BI.CubeLogErrorPane.superclass._init.apply(this, arguments);

        this.driver = this.options.driver;
        this._subscribeEvent();

        this.wrapper = BI.createWidget({
            type: "bi.vertical",
            hgap: 100,
            width: "100%",
            items: []
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            height: "100%",
            width: "100%",
            items: [{
                el: this.wrapper,
                top: 0,
                bottom: 0,
                left: 0,
                right: 0
            }]
        })
    },

    _subscribeEvent: function () {
        var self = this;
        this.driver.on(BI.DealWithCubeLogDataDriver.EVENT_CUBE_ERROR_DATA_UPDATED, function () {
            self._populate(this.getCubeLogErrorDataItems());
        });
        this.driver.on(BI.DealWithCubeLogDataDriver.EVENT_CLEAR_ALL_DATA, function () {
            self._populate(this.getCubeLogErrorDataItems());
        });
    },

    _createLabel: function (text) {
        return {
            type: "bi.label",
            whiteSpace: "pre",
            textAlign: "left",
            text: text
        }
    },

    _populate: function (data) {
        var self = this;
        var items = [];
        BI.each(data, function (i, text) {
            items.push(self._createLabel(text));
        });
        this.wrapper.empty();
        this.wrapper.addItems(items);
        // console.log(data);
    },

    populate: function () {

    }
});
$.shortcut("bi.cube_log_error_pane", BI.CubeLogErrorPane);