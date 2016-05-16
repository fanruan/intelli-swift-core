/**
 * @class BIShow.DetailTableView
 * @extends BI.View
 * @type {*|void|Object}
 */
BIShow.DetailTableView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return {
            baseCls: "bi-dashboard-widget"
        }
    },


    _init: function () {
        BIShow.DetailTableView.superclass._init.apply(this, arguments);
    },


    _render: function (vessel) {
        var self = this;
        this.title = this._buildWidgetTitle();

        this.table = BI.createWidget({
            type: "bi.detail_table",
            wId: this.model.get("id")
        });

        this.table.on(BI.DetailTable.EVENT_CHANGE, function(v){
            self.model.set("page", v);
        });

        this.widget = BI.createWidget({
            type: "bi.vtape",
            element: vessel,
            items: [{
                el: this.title,
                height: 32
            }, this.table]
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

            var filter = BI.createWidget({
                type: "bi.icon_button",
                cls: "filter-font dashboard-title-filter",
                width: 32,
                height: 32
            });
            filter.on(BI.IconButton.EVENT_CHANGE, function () {

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
                widgetType: BICst.WIDGET.TABLE
            });
            combo.on(BI.WidgetCombo.EVENT_CHANGE, function () {
                var type = this.getValue()[0];
                switch (type) {
                    case BICst.DASHBOARD_WIDGET_DELETE :
                        self.model.destroy();
                        break;
                    case BICst.DASHBOARD_WIDGET_EXPAND :
                        self._expandWidget();
                        break;
                    case BICst.DASHBOARD_WIDGET_SHRINK :
                        break;
                    case BICst.DASHBOARD_WIDGET_LINKAGE :
                        break;
                    case BICst.DASHBOARD_DETAIL_WIDGET_DRILL :
                        break;
                    case BICst.DASHBOARD_WIDGET_EXCEL :
                        break;
                    case BICst.DASHBOARD_CONTROL_RANG_ASC :
                        break;
                    case BICst.DASHBOARD_CONTROL_RANG_DESC :
                        break;
                    case BICst.DASHBOARD_TIEM_CONTROL_DETAIL :
                        break;

                    case BICst.DASHBOARD_TIEM_CONTROL_YEAR :
                        break;
                    case BICst.DASHBOARD_TIEM_CONTROL_SEASON :
                        break;
                    case BICst.DASHBOARD_TIEM_CONTROL_MONTH :
                        break;
                    case BICst.DASHBOARD_TIEM_CONTROL_YMD :
                        break;
                    case BICst.DASHBOARD_WIDGET_EASY_SETTING :
                        break;
                    case BICst.DASHBOARD_CONTROL_CLEAR :
                        break;

                    case BICst.DASHBOARD_WIDGET_COPY :
                        //var id = this.model.getEditing();
                        break;
                    case BICst.DASHBOARD_TABLE_FREEZE :
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
                            items: [filter, expand, combo]
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
        this.table.populate();
    }
});
