/**
 * Cube日志错误信息Node
 *
 * Created by GUY on 2016/3/31.
 * @class BI.CubeLogNode
 * @extends BI.Widget
 */
BI.CubeLogNode = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CubeLogNode.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-log-wrong-info-node",
            driver: BI.createWidget(),
            dataType: 0,
            text: "",
            second: 0,
            id: "",
            pId: "",
            open: false,
            level: 0
        });
    },

    _init: function () {
        BI.CubeLogNode.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.driver = this.options.driver;
        this._subscribeEvent(this.options.dataType);

        this.node = BI.createWidget({
            type: "bi.triangle_group_node",
            height: 40,
            // element: this.element,
            id: o.id,
            pId: o.pId,
            open: o.open,
            text: o.text,
            value: o.value
        });
        this.node.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: BI.createWidget(),
                width: o.level * 10
            }, {
                el: this.node
            }],
            height: 40
        });
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
        this.node.setText(this.options.text + ": (" + data.totalTime + ")");
    },

    populate: function (items, keyword, context) {
        // this.node.setText(this._formatText(context.el.text, context.el.second));
    },

    isSelected: function () {
        return this.node.isSelected();
    },

    setSelected: function (b) {
        return this.node.setSelected(b);
    },

    isOpened: function () {
        return this.node.isOpened();
    },

    setOpened: function (v) {
        this.node.setOpened(v);
    }
});
$.shortcut('bi.cube_log_node', BI.CubeLogNode);