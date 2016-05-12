/**
 * @class BIDezi.StringWidgetView
 * @extends BI.View
 * @type {*|void|Object}
 */
BIDezi.StringWidgetView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.StringWidgetView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-widget"
        })
    },

    _init: function () {
        BIDezi.StringWidgetView.superclass._init.apply(this, arguments);
        var self = this;
        BI.Broadcasts.on(this.model.get("id"), function(){
            self._resetValue();
        });
    },


    _render: function (vessel) {
        var self = this;
        this._buildWidgetTitle();
        this._createTools();
        
        this.combo = BI.createWidget({
            type: "bi.select_data_combo",
            wId: this.model.get("id")
        });

        this.combo.on(BI.SelectDataCombo.EVENT_CONFIRM, function () {
            self.model.set("value", this.getValue());
        });

        this.widget = BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: this.tools,
                top: 10
            }, {
                el: this.title,
                top: 10,
                left: 10
            }, {
                el: this.combo,
                top: 10,
                right: 10
            }]
        });
        this.widget.element.hover(function(){
            self.tools.setVisible(true);
        }, function(){
            self.tools.setVisible(false);
        });
        
    },

    _buildWidgetTitle: function () {
        var self = this;
        var id = this.model.get("id");
        if (!this.title) {
            this.title = BI.createWidget({
                type: "bi.shelter_editor",
                cls: "dashboard-title-left",
                value: BI.Utils.getWidgetNameByID(id),
                textAlign: "left",
                height: 30,
                allowBlank: false,
                errorText: BI.i18nText("BI-Control_Widget_Name_Can_Not_Repeat"),
                validationChecker: function(v){
                    return BI.Utils.checkWidgetNameByID(v, id);
                }
            });
            this.title.on(BI.ShelterEditor.EVENT_CHANGE, function(){
                self.model.set("name", this.getValue());
            });
        } else {
            this.title.setValue(BI.Utils.getWidgetNameByID(this.model.get("id")));
        }
    },

    _createTools: function(){
        this.tools = BI.createWidget({
            type: "bi.widget_combo",
            cls: "operator-region",
            wId: this.model.get("id")
        });
        this.tools.on(BI.WidgetCombo.EVENT_CHANGE, function (type) {
            switch (type) {
                case BICst.DASHBOARD_WIDGET_EXPAND:
                    self._expandWidget();
                    break;
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
        this.tools.setVisible(false);
    },

    _refreshLayout: function(){
        var self = this;
        var bounds = this.model.get("bounds");
        var height = bounds.height, width = bounds.width;
        var widgetName = this.model.get("name");
        // var nameWidth = BI.DOM.getTextSizeWidth(widgetName, 16);
        var minComboWidth = 70;     //默认combo的最小宽度
        var minNameWidth = 30;      //默认editor的最小宽度

        var label = BI.createWidget({
            type: "bi.label",
            cls: "temp-label",
            text: widgetName
        });
        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [label]
        });
        BI.defer(function(){
            var nameWidth = label.element.width();
            label.destroy();
            if(height < 90) {
                self.widget.attr("items")[0].left = 10;
                self.widget.attr("items")[0].right = "";
                self.widget.attr("items")[2].top = 10;
                if(width < minComboWidth + minNameWidth) {
                    self.combo.setVisible(false);
                } else if(width < nameWidth) {
                    self.combo.setVisible(true);
                    self.widget.attr("items")[1].right = minComboWidth + 10;
                    self.widget.attr("items")[2].left = width - 10 - minComboWidth;
                } else {
                    self.combo.setVisible(true);
                    self.widget.attr("items")[1].right = width - 30 - nameWidth;
                    self.widget.attr("items")[2].left = 30 + nameWidth;
                }
            } else {
                self.widget.attr("items")[0].left = "";
                self.widget.attr("items")[0].right = 10;
                self.widget.attr("items")[1].right = 10;
                self.widget.attr("items")[2].top = 50;
                self.widget.attr("items")[2].left = 10;
            }
            self.widget.resize();
        });
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
        this.model.set("value");
        this.refresh();
    },

    splice: function(){
        BI.Utils.broadcastAllWidgets2Refresh();
    },

    listenEnd: function () {

    },

    change: function (changed) {
        if(BI.has(changed, "bounds")) {
            this._refreshLayout();
        }
        if(BI.has(changed, "value")) {
            BI.Utils.broadcastAllWidgets2Refresh();
        }
    },

    local: function () {
        return false;
    },

    refresh: function () {
        this._refreshLayout();
        this._buildWidgetTitle();
        this.combo.setValue(this.model.get("value"));
    }
});