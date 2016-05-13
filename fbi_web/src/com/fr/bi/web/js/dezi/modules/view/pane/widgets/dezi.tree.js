/**
 * Created by roy on 16/4/13.
 */
BIDezi.TreeWidgetView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.TreeWidgetView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-widget"
        })
    },

    _init: function () {
        BIDezi.TreeWidgetView.superclass._init.apply(this, arguments);
        var self = this;
        BI.Broadcasts.on(this.model.get("id"), function(){
            self._resetValue();
        });
    },

    _render: function (vessel) {
        var self = this;
        this.title = this._buildWidgetTitle();

        this.combo = BI.createWidget({
            type: "bi.multi_tree_combo",
            itemsCreator: function (op, callback) {
                var data = BI.extend({
                    floors: BI.size(self.model.get("dimensions"))
                }, op);
                BI.Utils.getWidgetDataByID(self.model.get("id"), function (jsonData) {
                    callback(jsonData);
                }, {tree_options: data})
            }
        });

        this.combo.on(BI.MultiTreeCombo.EVENT_CONFIRM, function () {
            self.model.set("value", self.combo.getValue());
        });

        this.widget = BI.createWidget({
            type: "bi.vtape",
            element: vessel,
            items: [{
                el: this.title,
                height: 32
            }, {
                type: "bi.vertical",
                items: [this.combo]
            }]
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

            var combo = BI.createWidget({
                type: "bi.widget_combo",
                wId: this.model.get("id")
            });
            combo.on(BI.WidgetCombo.EVENT_CHANGE, function (type) {
                switch (type) {
                    case BICst.DASHBOARD_WIDGET_EXPAND:
                        self._expandWidget();
                        return;
                    case BICst.DASHBOARD_CONTROL_RANG_ASC:
                        self.model.set("changeSort", {type: BICst.SORT.ASC});
                        break;
                    case BICst.DASHBOARD_CONTROL_RANG_DESC:
                        self.model.set("changeSort", {type: BICst.SORT.DESC});
                        break;
                    case BICst.DASHBOARD_CONTROL_CLEAR:
                        self._resetValue();
                        break;
                    case BICst.DASHBOARD_WIDGET_RENAME:
                        break;
                    case BICst.DASHBOARD_WIDGET_COPY:
                        self.model.copy();
                        break;
                    case BICst.DASHBOARD_WIDGET_DELETE:
                        self.model.destroy();
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
                            items: [combo]
                        }),
                        width: 32
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
    
    _resetValue: function(){
        this.model.set("value", {});
        this.refresh();
    },

    change: function (changed) {
        if (BI.has(changed, "value")) {
            BI.Utils.broadcastAllWidgets2Refresh();
        }
    },

    local: function () {
        return false;
    },

    refresh: function () {
        this._buildWidgetTitle();
        this.combo.setValue(this.model.get("value"));
    }

});