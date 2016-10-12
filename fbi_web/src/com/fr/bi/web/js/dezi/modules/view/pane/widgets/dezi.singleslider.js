/**
 * Created by zcf on 2016/10/9.
 */
BIDezi.SingleSliderWidgetView = BI.inherit(BI.View, {
    _constants: {
        TOOL_ICON_WIDTH: 20,
        TOOL_ICON_HEIGHT: 20
    },
    _defaultConfig: function () {
        return BI.extend(BIDezi.SingleSliderWidgetView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-widget bi-control-widget"
        })
    },
    _init: function () {
        BIDezi.SingleSliderWidgetView.superclass._init.apply(this, arguments);
        var self = this;
        BI.Broadcasts.on(BICst.BROADCAST.RESET_PREFIX + this.model.get("id"), function () {
            self._resetValue();
        });
        //全局样式
        BI.Broadcasts.on(BICst.BROADCAST.GLOBAL_STYLE_PREFIX, function (globalStyle) {
            self._refreshGlobalStyle(globalStyle);
        });
    },
    _render: function (vessel) {
        var self = this;
        this._buildWidgetTitle();
        this._createTools();

        this.combo = BI.createWidget({
            type: "bi.select_data_single_slider",
            wId: this.model.get("id")
        });
        this.combo.on(BI.SelectDataSingleSlider.ENENT_CHANGE, function () {
            self.model.set("value", this.getValue());
        });

        this.widget = BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: this.tools,
                top: 0,
                right: 10
            }, {
                el: this.titleWrapper,
                top: 0,
                left: 0,
                right: 0
            }, {
                el: this.combo,
                top: 10,
                right: 10
            }]
        });
        this.widget.element.hover(function () {
            self.tools.setVisible(true);
        }, function () {
            if (!self.widget.element.parent().parent().parent().hasClass("selected")) {
                self.tools.setVisible(false);
            }
        });
        BI.Broadcasts.on(BICst.BROADCAST.WIDGET_SELECTED_PREFIX, function () {
            if (!self.widget.element.parent().parent().parent().hasClass("selected")) {
                self.tools.setVisible(false);
            }
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
                height: 25,
                allowBlank: false,
                errorText: BI.i18nText("BI-Control_Widget_Name_Can_Not_Repeat"),
                validationChecker: function (v) {
                    return BI.Utils.checkWidgetNameByID(v, id);
                }
            });
            this.titleWrapper = BI.createWidget({
                type: "bi.absolute",
                height: 35,
                cls: "dashboard-widget-title",
                items: [{
                    el: this.title,
                    left: 10,
                    top: 10,
                    right: 10
                }]
            });
            this.title.on(BI.ShelterEditor.EVENT_CHANGE, function () {
                self.model.set("name", this.getValue());
            });
        } else {
            this.title.setValue(BI.Utils.getWidgetNameByID(this.model.get("id")));
        }
    },

    _createTools: function () {
        var self = this;
        var expand = BI.createWidget({
            type: "bi.icon_button",
            width: this._constants.TOOL_ICON_WIDTH,
            height: this._constants.TOOL_ICON_HEIGHT,
            title: BI.i18nText("BI-Detailed_Setting"),
            cls: "widget-combo-detail-font dashboard-title-detail"
        });
        expand.on(BI.IconButton.EVENT_CHANGE, function () {
            self._expandWidget();
        });
        var combo = BI.createWidget({
            type: "bi.widget_combo",
            wId: this.model.get("id")
        });
        combo.on(BI.WidgetCombo.EVENT_CHANGE, function (type) {
            switch (type) {
                case BICst.DASHBOARD_WIDGET_EXPAND:
                    self._expandWidget();
                    break;
                // case BICst.DASHBOARD_CONTROL_CLEAR:
                //     self._resetValue();
                //     break;
                case BICst.DASHBOARD_WIDGET_RENAME:
                    self.title.focus();
                    break;
                case BICst.DASHBOARD_WIDGET_COPY:
                    self.model.copy();
                    break;
                case BICst.DASHBOARD_WIDGET_DELETE:
                    BI.Msg.confirm("", BI.i18nText("BI-Sure_Delete") + self.model.get("name") + "?", function (v) {
                        if (v === true) {
                            self.model.destroy();
                        }
                    });
                    break;
            }
        });
        this.tools = BI.createWidget({
            type: "bi.left",
            cls: "operator-region",
            items: [expand, combo],
            lgap: 10
        });
        this.tools.setVisible(false);
    },
    _expandWidget: function () {
        var wId = this.model.get("id");
        var type = this.model.get("type");
        this.addSubVessel("detail", "body", {
            isLayer: true
        }).skipTo("detail", "detail", "detail", {}, {
            id: wId
        });
        BI.Broadcasts.send(BICst.BROADCAST.DETAIL_EDIT_PREFIX + wId);
    },
    _resetValue: function () {
        this.model.set("value");
        this.refresh();
    },
    _refreshGlobalStyle: function () {
        this._refreshTitlePosition();
    },

    _refreshTitlePosition: function () {
        var pos = BI.Utils.getGSNamePos();
        var cls = pos === BICst.DASHBOARD_WIDGET_NAME_POS_CENTER ?
            "dashboard-title-center" : "dashboard-title-left";
        this.title.element.removeClass("dashboard-title-left")
            .removeClass("dashboard-title-center").addClass(cls);
    },
    _refreshLayout: function () {
        var bounds = this.model.get("bounds");
        var height = bounds.height, width = bounds.width;
        var widgetName = this.model.get("name");
        var minComboWidth = 70;     //默认combo的最小宽度
        var minNameWidth = 30;      //默认editor的最小宽度
        var nameWidth = BI.DOM.getTextSizeWidth(widgetName, 16);
        // width =  5 + 10 + (4 + nameWidth + 4) + 10 + comboWidth + 10 + 5
        if (height < 100) {
            // this.widget.attr("items")[0].left = 10;
            // this.widget.attr("items")[0].right = "";
            this.widget.attr("items")[2].top = 10;
            if (width < minComboWidth + minNameWidth + 48) {
                this.combo.setVisible(false);
                this.widget.attr("items")[1].right = 0;
            } else if (width < nameWidth + minComboWidth + 48) {
                this.combo.setVisible(true);
                this.widget.attr("items")[1].right = minComboWidth + 15;
                this.widget.attr("items")[2].left = width - 15 - minComboWidth;
            } else {
                this.combo.setVisible(true);
                this.widget.attr("items")[1].right = width - 43 - nameWidth;
                this.widget.attr("items")[2].left = 33 + nameWidth;
            }
        } else {
            // this.widget.attr("items")[0].left = "";
            // this.widget.attr("items")[0].right = 10;
            this.combo.setVisible(true);
            this.widget.attr("items")[1].right = 0;
            this.widget.attr("items")[2].top = 35;
            this.widget.attr("items")[2].left = 10;
        }
        this.widget.resize();
    },
    duplicate: function () {
        BI.Utils.broadcastAllWidgets2Refresh();
    },

    splice: function () {
        BI.Utils.broadcastAllWidgets2Refresh();
    },
    change: function (changed, prev, context, options) {
        if (BI.has(changed, "bounds")) {
            this._refreshLayout();
        }
        if (BI.has(changed, "dimension")) {
            BI.Utils.broadcastAllWidgets2Refresh();
            this.combo.populate();
        }
        if (BI.has(changed, "value")) {
            BI.Utils.broadcastAllWidgets2Refresh();
            this.combo.setValue();
        }
    },
    local: function () {
        if (this.model.has("expand")) {
            this.model.get("expand");
            this._expandWidget();
            return true;
        }
        return false;
    },
    refresh: function () {
        this._refreshLayout();
        this._buildWidgetTitle();
        this._refreshTitlePosition();
        this._refreshGlobalStyle();
        this.combo.populate();
    }
});