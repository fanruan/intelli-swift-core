/**
 * Created by zcf on 2017/3/8.
 */
BI.CubeLogChartTablePane = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CubeLogChartTablePane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: 'bi-cube-log-chart-table-pane',
            driver: BI.createWidget(),
            dataType: 1
        })
    },

    _init: function () {
        BI.CubeLogChartTablePane.superclass._init.apply(this, arguments);

        var self = this;

        this.driver = this.options.driver;
        this._subscribeEvent(this.options.dataType);

        this.updatingLabel = BI.createWidget({
            type: "bi.label",
            width: 400,
            height: 30,
            text: ""
        });

        // this.chart = BI.createWidget({
        //     type: "bi.accumulate_bar_chart",
        //     width: 400,
        //     height: 300
        // });
        this.table = BI.createWidget({
            type: "bi.table_view",
            headerRowSize: 40,
            rowSize: 30,
            height: 200,
            header: [],
            items: []
        });
        BI.createWidget({
            type: "bi.vertical",
            hgap: 100,
            element: this.element,
            width: "100%",
            items: [this.updatingLabel, this.table]
        })
    },

    _subscribeTableTransportEvent: function () {
        var self = this;
        this.driver.on(BI.DealWithCubeLogDataDriver.EVENT_CUBE_TABLE_TRANSPORT_UPDATED, function () {
            self._populate(this.getCubeLogTableTransportItems());
        });
    },

    _subscribeTableFieldIndexEvent: function () {
        var self = this;
        this.driver.on(BI.DealWithCubeLogDataDriver.EVENT_CUBE_TABLE_FIELD_INDEX_UPDATED, function () {
            self._populate(this.getCubeLogTableFieldIndexItems());
        });
    },

    _subscribeRelationIndexEvent: function () {
        var self = this;
        this.driver.on(BI.DealWithCubeLogDataDriver.EVENT_CUBE_RELATION_INDEX_UPDATED, function () {
            self._populate(this.getCubeLogRelationIndexItems());
        });
    },

    _subscribeEvent: function (dataType) {
        switch (dataType) {
            case BI.CubeLog.READ_DB_NODE:
                this._subscribeTableTransportEvent();
                break;
            case BI.CubeLog.INDEX_NODE:
                this._subscribeTableFieldIndexEvent();
                break;
            case BI.CubeLog.RELATION_NODE:
                this._subscribeRelationIndexEvent();
                break;
            default:
                BI.emptyFn();
        }
    },

    _populate: function (data) {
        this.table.populate(data.tableItems, data.tableHeader);
    },

    populate: function () {
    }
});
$.shortcut("bi.cube_log_chart_table_pane", BI.CubeLogChartTablePane);