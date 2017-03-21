/**
 * Created by zcf on 2017/3/2.
 */
BI.CubeLogDetailPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CubeLogDetailPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-log-detail-pane",
            driver: BI.createWidget()
        });
    },

    _init: function () {
        BI.CubeLogDetailPane.superclass._init.apply(this, arguments);

        this.wrapper = BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            height: 250,
            width: "100%",
            items: []
        });

        this._createPane(this.options.items[0].dataType);
    },

    _createPane: function (dataType) {
        if (!this.pane) {
            dataType === BI.CubeLog.ERROR_MES_NODE ? this.pane = BI.createWidget({
                type: "bi.cube_log_error_pane",
                driver: this.options.driver
            }) : this.pane = BI.createWidget({
                type: "bi.cube_log_chart_table_pane",
                driver: this.options.driver,
                dataType: dataType
            });

            this.wrapper.addItems({
                el: this.pane,
                top: 0,
                bottom: 0,
                left: 0,
                right: 0
            })
        }
    },

    populate: function (items) {
    }
});
$.shortcut("bi.cube_log_detail_pane", BI.CubeLogDetailPane);