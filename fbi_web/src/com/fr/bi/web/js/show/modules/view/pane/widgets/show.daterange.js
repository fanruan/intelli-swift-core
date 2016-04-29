/**
 * @class BIShow.DateRangeView
 * @extends BI.View
 * @type {*|void|Object}
 */
BIShow.DateRangeView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return {
            baseCls: "bi-dashboard-widget"
        }
    },

    _init: function () {
        BIShow.DateRangeView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;
        this.title = this._buildWidgetTitle();

        this.combo = BI.createWidget({
            type: "bi.time_interval"
        });

        this.combo.on(BI.TimeInterval.EVENT_CHANGE, function () {
            self.model.set("filter_value", {
                filter_type: BICst.FILTER_DATE.BELONG_DATE_RANGE,
                filter_value: this.getValue()
            });
        });

        this.widget = BI.createWidget({
            type: "bi.vtape",
            element: vessel,
            items: [{
                el: this.title,
                height: 32
            }, this.combo]
        })
    },

    _buildWidgetTitle: function () {
        var self = this, o = this.options;
        if (!this.title) {
            var text = this.title = BI.createWidget({
                type: "bi.label",
                cls: "dashboard-title-text",
                text: BI.Utils.getWidgetNameByID(this.model.get("id")),
                textAlign: "left",
                height: 32
            });

            var expand = BI.createWidget({
                type: "bi.icon_button",
                width: 32,
                height: 32,
                cls: "dashboard-widget-combo-detail-set-font dashboard-title-detail"
            });
            expand.on(BI.IconButton.EVENT_CHANGE, function () {
                self._expandWidget();
            });

            var combo = BI.createWidget({
                type: "bi.widget_combo",
                widgetType: BICst.Widget.DATE
            });
            combo.on(BI.WidgetCombo.EVENT_CHANGE, function (type) {
                switch (type) {
                    case BICst.DASHBOARD_WIDGET_DELETE:
                        self.model.destroy();
                        break;
                    case BICst.DASHBOARD_WIDGET_EXPAND:
                        self._expandWidget();
                        break;
                    case BICst.DASHBOARD_CONTROL_CLEAR:
                        break;
                    case BICst.DASHBOARD_WIDGET_COPY:
                        self.model.copy();
                        break;
                }
            });

            return BI.createWidget({
                type: "bi.border",
                cls: "dashboard-title",
                items: {
                    center: text,
                    east: {
                        el: BI.createWidget({
                            type: "bi.center_adapt",
                            cls: "operator-region",
                            items: [expand, combo]
                        }),
                        width: 96
                    }
                }
            });
        } else {
            this.title.setValue(BI.Utils.getWidgetNameByID(this.model.get("id")));
        }
    },

    _expandWidget: function () {
        var wId = this.model.get("id");
        var type = this.model.get("type");
        this.addSubVessel("detail", "body", {
            isLayer: true
        }).skipTo("detail", "detail", "detail", {}, {
            id: wId
        })
    },

    listenEnd: function () {

    },

    change: function (changed) {

    },

    local: function () {
        return false;
    },

    refresh: function () {
        this._buildWidgetTitle();
        var filter = this.model.get("filter_value");
        this.combo.setValue(filter.filter_value || {});
    }
});
