/**
 * @class BIDezi.DetailTableView
 * @extends BI.View
 * @type {*|void|Object}
 */
BIDezi.DetailTableView = BI.inherit(BI.View, {

    _constants: {
        SHOW_CHART: 1,
        SHOW_FILTER: 2,
        TOOL_ICON_WIDTH: 20,
        TOOL_ICON_HEIGHT: 20
    },
    _defaultConfig: function () {
        return BI.extend(BIDezi.DetailTableView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-widget"
        })
    },

    _init: function () {
        BIDezi.DetailTableView.superclass._init.apply(this, arguments);
        var self = this, wId = this.model.get("id");
        BI.Broadcasts.on(BICst.BROADCAST.REFRESH_PREFIX + wId, function () {
            self._refreshTableAndFilter();
        });
        BI.Broadcasts.on(BICst.BROADCAST.LINKAGE_PREFIX + wId, function (dId, v) {
            var clicked = self.model.get("clicked") || {};
            var allFromIds = BI.Utils.getAllLinkageFromIdsByID(BI.Utils.getWidgetIDByDimensionID(dId));
            //这条链上所有的其他clicked都应当被清掉
            BI.each(clicked, function (cid, click) {
                if (allFromIds.contains(cid)) {
                    delete clicked[cid];
                }
            });
            if (BI.isNull(v)) {
                delete clicked[dId];
            } else {
                clicked[dId] = v;
            }
            self.model.set("clicked", clicked, {
                notrefresh: true
            });
            self._refreshTableAndFilter();
        });
        BI.Broadcasts.on(BICst.BROADCAST.RESET_PREFIX + wId, function () {
            self.model.set("clicked", {});
        });

        //全局样式的修改
        BI.Broadcasts.on(BICst.BROADCAST.GLOBAL_STYLE_PREFIX, function (globalStyle) {
            self._refreshGlobalStyle(globalStyle);
        });
    },


    _render: function (vessel) {
        var self = this;
        this._buildWidgetTitle();
        this._createTools();
        this.table = BI.createWidget({
            type: "bi.detail_table",
            wId: this.model.get("id"),
            status: BICst.WIDGET_STATUS.EDIT
        });
        this.tablePopulate = BI.debounce(BI.bind(this.table.populate, this.table), 0);
        this.tableResize = BI.debounce(BI.bind(this.table.resize, this.table), 0);
        this.table.on(BI.DetailTable.EVENT_CHANGE, function (ob) {
            self.model.set(ob);
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
                el: this.table,
                left: 10,
                right: 10,
                top: 50,
                bottom: 10
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
                cls: BI.Utils.getWSNamePosByID(id) === BICst.DASHBOARD_WIDGET_NAME_POS_CENTER ?
                    "dashboard-title-center" : "dashboard-title-left",
                value: BI.Utils.getWidgetNameByID(id),
                textAlign: "left",
                height: 25,
                allowBlank: false,
                errorText: function (v) {
                    if (BI.isNotNull(v) && BI.trim(v) !== "") {
                        return BI.i18nText("BI-Widget_Name_Can_Not_Repeat");
                    }
                    return BI.i18nText("BI-Widget_Name_Can_Not_Null");
                },
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
                    right: 10,
                    top: 10
                }]
            });
            this.title.on(BI.ShelterEditor.EVENT_CHANGE, function () {
                self.model.set("name", this.getValue());
            });
        } else {
            this.title.setValue(BI.Utils.getWidgetNameByID(id));
        }
    },

    _createTools: function () {
        var self = this, wId = this.model.get("id");

        this.maximize = BI.createWidget({
            type: "bi.maximization",
            wId: wId,
            status: BICst.WIDGET_STATUS.EDIT
        });
        this.maximize.on(BI.Maximization.EVENT_SET, function (widget) {
            self.model.set(widget);
        });
        this.maximize.on(BI.Maximization.EVENT_SET_TITLE_NAME, function (name) {
            self.model.set("name", name);
            self.title.setValue(name);
        });
        this.maximize.on(BI.Maximization.EVENT_CHANGE, function (type) {
            switch (type) {
                case BICst.DASHBOARD_WIDGET_EXPAND:
                    self._expandWidget();
                    break;
                case BICst.DASHBOARD_WIDGET_SHOW_NAME:
                    self._onClickShowName();
                    break;
                case BICst.DASHBOARD_WIDGET_RENAME:
                    self.title.focus();
                    break;
                case BICst.DASHBOARD_WIDGET_NAME_POS_LEFT:
                    self._onClickNamePosLeft();
                    break;
                case BICst.DASHBOARD_WIDGET_NAME_POS_CENTER:
                    self._onClickNamePosCenter();
                    break;
                case BICst.DASHBOARD_WIDGET_COPY:
                    self.model.copy();
                    break;
                case BICst.DASHBOARD_WIDGET_DELETE:
                    self._onClickDelete();
                    break;
            }
        });

        var filter = BI.createWidget({
            type: "bi.icon_button",
            width: this._constants.TOOL_ICON_WIDTH,
            height: this._constants.TOOL_ICON_HEIGHT,
            title: BI.i18nText("BI-Show_Filters"),
            cls: "widget-tools-filter-font dashboard-title-detail"
        });
        filter.on(BI.IconButton.EVENT_CHANGE, function () {
            self._onClickFilter();
        });

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
            wId: wId
        });
        combo.on(BI.WidgetCombo.EVENT_CHANGE, function (type) {
            switch (type) {
                case BICst.DASHBOARD_WIDGET_EXPAND:
                    self._expandWidget();
                case BICst.DASHBOARD_WIDGET_SHOW_NAME:
                    self._onClickShowName();
                    break;
                case BICst.DASHBOARD_WIDGET_RENAME:
                    self.title.focus();
                    break;
                case BICst.DASHBOARD_WIDGET_NAME_POS_LEFT:
                    self._onClickNamePosLeft();
                    break;
                case BICst.DASHBOARD_WIDGET_NAME_POS_CENTER:
                    self._onClickNamePosCenter();
                    break;
                case BICst.DASHBOARD_WIDGET_FILTER:
                    self._onClickFilter();
                    break;
                case BICst.DASHBOARD_WIDGET_EXCEL:
                    window.location = FR.servletURL + "?op=fr_bi_dezi&cmd=bi_export_excel&sessionID=" + Data.SharingPool.get("sessionID") + "&name="
                        + window.encodeURIComponent(self.model.get("name"));
                    break;
                case BICst.DASHBOARD_WIDGET_COPY :
                    self.model.copy();
                    break;
                case BICst.DASHBOARD_WIDGET_DELETE :
                    self._onClickDelete();
                    break;
            }
        });

        this.tools = BI.createWidget({
            type: "bi.left",
            cls: "operator-region",
            items: [this.maximize, filter, expand, combo],
            lgap: 10
        });
        this.tools.setVisible(false);
    },

    _onClickShowName: function () {
        var settings = this.model.get("settings");
        settings.showName = !BI.Utils.getWSShowNameByID(this.model.get("id"));
        this.model.set("settings", settings);
        this._refreshLayout();
    },

    _onClickNamePosLeft: function () {
        var settings = this.model.get("settings");
        settings.name_pos = BICst.DASHBOARD_WIDGET_NAME_POS_LEFT;
        this.model.set("settings", settings);
        this._refreshTitlePosition();
    },

    _onClickNamePosCenter: function () {
        var settings = this.model.get("settings");
        settings.name_pos = BICst.DASHBOARD_WIDGET_NAME_POS_CENTER;
        this.model.set("settings", settings);
        this._refreshTitlePosition();
    },

    _onClickDelete: function () {
        var self = this;
        BI.Msg.confirm("", BI.i18nText("BI-Sure_Delete_Current_Component") + self.model.get("name") + "?", function (v) {
            if (v === true) {
                self.model.destroy();
            }
        });
    },

    _onClickFilter: function () {
        var self = this;
        if (BI.isNull(this.filterPane)) {
            this.filterPane = BI.createWidget({
                type: "bi.widget_filter",
                wId: this.model.get("id")
            });
            this.filterPane.on(BI.WidgetFilter.EVENT_REMOVE_FILTER, function (widget) {
                self.model.set(widget);
                if (BI.isNotNull(widget.clicked)) {
                    self._refreshTableAndFilter();
                }
            });
            BI.createWidget({
                type: "bi.absolute",
                element: this.table,
                items: [{
                    el: this.filterPane,
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0
                }]
            });
            return;
        }
        this.filterPane.setVisible(!this.filterPane.isVisible());
    },

    _refreshGlobalStyle: function (globalStyle) {
        this._refreshTitlePosition();
    },

    _refreshTableAndFilter: function () {
        BI.isNotNull(this.filterPane) && this.filterPane.populate();
        this.tablePopulate();
    },

    _refreshLayout: function () {
        var showTitle = BI.Utils.getWSShowNameByID(this.model.get("id"));
        if (showTitle === false) {
            this.title.setVisible(false);
            this.widget.attr("items")[0].top = 0;
            this.widget.attr("items")[2].top = 20;
        } else {
            this.title.setVisible(true);
            this.widget.attr("items")[0].top = 10;
            this.widget.attr("items")[2].top = 35;
        }
        this.widget.resize();
        this.tableResize();
    },

    _refreshTitlePosition: function () {
        var pos = BI.Utils.getWSNamePosByID(this.model.get("id"));
        var cls = pos === BICst.DASHBOARD_WIDGET_NAME_POS_CENTER ?
            "dashboard-title-center" : "dashboard-title-left";
        this.title.element.removeClass("dashboard-title-left")
            .removeClass("dashboard-title-center").addClass(cls);
    },

    _refreshWidgetTitle: function () {
        var id = this.model.get("id");
        var titleSetting = this.model.get("settings").widgetNameStyle || {};
        this.title.setTextStyle(titleSetting.titleWordStyle || {});

        this.titleWrapper.element.css({"background": this._getBackgroundValue(titleSetting.titleBG)});
    },

    _refreshWidgetBG: function () {
        var widgetBG = this.model.get("settings").widgetBG || {};
        this.element.css({"background": this._getBackgroundValue(widgetBG)})
    },

    _getBackgroundValue: function (bg) {
        if (!bg) {
            return "";
        }
        switch (bg.type) {
            case BICst.BACKGROUND_TYPE.COLOR:
                return bg.value;
            case BICst.BACKGROUND_TYPE.IMAGE:
                return "url(" + FR.servletURL + "?op=fr_bi&cmd=get_uploaded_image&image_id=" + bg["value"] + ")";
        }
        return "";
    },

    _expandWidget: function () {
        var wId = this.model.get("id");
        var type = this.model.get("type");
        this.addSubVessel("detail", "body", {
            isLayer: true
        }).skipTo("detail", "detail", "detail", {}, {
            id: wId
        })
        BI.Broadcasts.send(BICst.BROADCAST.DETAIL_EDIT_PREFIX + wId);
    },


    listenEnd: function () {

    },

    change: function (changed, prev, context, options) {
        if (options.notrefresh === true) {
            return;
        }
        if (BI.has(changed, "bounds")) {
            this.tableResize();
        }
        if (BI.has(changed, "filter_value")) {
            this._refreshTableAndFilter();
            this.maximize.populate();
        }
        if (BI.has(changed, "dimensions") ||
            BI.has(changed, "sort_sequence")) {
            this.tablePopulate();
            this.maximize.populate();
        }
        if (BI.has(changed, "settings") && (changed.settings.widgetNameStyle !== prev.settings.widgetNameStyle)) {
            this._refreshWidgetTitle();
        }
        if (BI.has(changed, "settings") && (changed.settings.widgetBG !== prev.settings.widgetBG)) {
            this._refreshWidgetBG();
        }
        if (BI.has(changed, "clicked")) {
            this._refreshTableAndFilter();
        }
        if (BI.has(changed, "clicked")) {
            this._refreshTableAndFilter();
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
        this._buildWidgetTitle();
        this._refreshWidgetTitle();
        this._refreshWidgetBG();
        this.tablePopulate();
        this._refreshLayout();
        this._refreshTitlePosition();

        this._refreshGlobalStyle();
    }
});