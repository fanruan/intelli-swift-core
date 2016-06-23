/**
 * Created by Young's on 2016/5/31.
 */
BI.AllReportsListItem = BI.inherit(BI.Widget, {

    _constant: {
        PATH_CHOOSER: "__hangout_report_popover__"
    },

    _defaultConfig: function () {
        return BI.extend(BI.AllReportsListItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-all-reports-list-item"
        })
    },

    _init: function () {
        BI.AllReportsListItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var report = o.report;
        this.status = report.status;
        this.model = new BI.AllReportsItemModel({
            roles: o.roles,
            users: o.users
        });
        if (this.status === BICst.REPORT_STATUS.APPLYING) {
            this.hangoutIcon = BI.createWidget({
                type: "bi.icon_button",
                cls: "report-apply-hangout-ing-font normal-mark",
                title: BI.i18nText("BI-Hangout_Now"),
                invisible: true,
                stopPropagation: true,
                width: 40,
                height: 40,
                iconWidth: 16,
                iconHeight: 16
            });
            this.hangoutIcon.on(BI.IconButton.EVENT_CHANGE, function () {
                self._onClickHangout();
            });
        }

        if (this.status !== BICst.REPORT_STATUS.NORMAL) {
            this.markIcon = BI.createWidget({
                type: "bi.icon_change_button",
                cls: "mark-icon",
                width: 12,
                height: 12
            });
            this._refreshMarkIcon();
        }

        var userName = this.model.getUserNameByUserId(report.createBy);
        var roleName = this.model.getRoleByUserId(report.createBy);
        var departName = this.model.getDepartNameByUserId(report.createBy);

        var infoIcon = BI.createWidget({
            type: "bi.combo",
            el: {
                type: "bi.icon_button",
                cls: "rename-font normal-mark",
                iconWidth: 16,
                iconHeight: 16,
                width: 40,
                height: 40
            },
            trigger: "hover",
            direction: "left",
            adjustXOffset: 100,
            popup: {
                el: {
                    type: "bi.horizontal_auto",
                    cls: "info-card",
                    items: [{
                        type: "bi.horizontal",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Users") + ":",
                            height: 30,
                            width: 40
                        }, {
                            type: "bi.label",
                            text: userName,
                            textAlign: "left",
                            textHeight: 30,
                            whiteSpace: "normal",
                            hgap: 5
                        }]
                    }, {
                        type: "bi.horizontal",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Role") + ":",
                            height: 30,
                            width: 40
                        }, {
                            type: "bi.label",
                            text: roleName.toString(),
                            textAlign: "left",
                            textHeight: 30,
                            whiteSpace: "normal",
                            hgap: 5
                        }]
                    }, {
                        type: "bi.horizontal",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Department") + ":",
                            height: 30,
                            width: 40
                        }, {
                            type: "bi.label",
                            text: departName.toString(),
                            textAlign: "left",
                            textHeight: 30,
                            whiteSpace: "normal",
                            hgap: 5
                        }]
                    }, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Last_Modify_Date") + ": " + FR.date2Str(new Date(report.lastModify), "yyyy.MM.dd HH:mm:ss"),
                        textAlign: "left",
                        height: 30,
                        hgap: 5
                    }],
                    width: 220
                }
            }
        });
        infoIcon.setVisible(false);

        var timeText = BI.createWidget({
            type: "bi.label",
            cls: "template-file-item-date",
            text: FR.date2Str(new Date(report.lastModify), "yyyy.MM.dd HH:mm:ss"),
            height: 40
        });

        this.element.hover(function () {
            self.hangoutIcon && self.hangoutIcon.setVisible(true);
            infoIcon.setVisible(true);
        }, function () {
            self.hangoutIcon && self.hangoutIcon.setVisible(false);
            infoIcon.setVisible(false);
        });

        this.reportName = BI.createWidget({
            type: "bi.text_button",
            text: report.text,
            textAlign: "left",
            height: 40
        });
        this.reportName.on(BI.TextButton.EVENT_CHANGE, function () {
            FS.tabPane.addItem({
                title: report.text,
                src: FR.servletURL + report.buildUrl
            });
        });

        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            height: 40,
            items: [
                {
                    el: {
                        type: "bi.absolute",
                        items: [{
                            el: {
                                type: "bi.icon_button",
                                cls: (report.description === "true" ? "real-time-font" : "file-font") + " normal-mark",
                                iconWidth: 16,
                                iconHeight: 16
                            },
                            top: 12,
                            left: 12
                        }, {
                            el: this.markIcon || BI.createWidget(),
                            bottom: 6,
                            right: 6
                        }],
                        height: 40,
                        width: 40
                    },
                    width: 40
                }, {
                    el: this.reportName,
                    width: "fill"
                }, {
                    el: {
                        type: "bi.absolute",
                        items: [{
                            el: this.hangoutIcon || BI.createWidget(),
                            top: 0,
                            left: 0
                        }, {
                            el: infoIcon,
                            top: 0,
                            left: 60
                        }, {
                            el: timeText,
                            top: 0,
                            right: 10
                        }]
                    },
                    width: 260
                }]
        });
    },

    _refreshMarkIcon: function () {
        if (this.status === BICst.REPORT_STATUS.APPLYING) {
            this.markIcon.setIcon("report-apply-hangout-ing-font");
            this.markIcon.setTitle(BI.i18nText("BI-Report_Hangout_Applying"));
            return;
        }
        this.markIcon.setIcon("report-hangout-font");
        this.markIcon.setTitle(BI.i18nText("BI-Hangouted"));
    },

    _onClickHangout: function () {
        var self = this, o = this.options;
        BI.Popovers.remove(this._constant.PATH_CHOOSER);
        var pathChooser = BI.createWidget({
            type: "bi.report_hangout_path_chooser",
            reportName: this.options.report.text
        });
        pathChooser.on(BI.ReportHangoutPathChooser.EVENT_SAVE, function () {
            o.onHangout(this.getValue());
            self.hangoutIcon.destroy();
            self.status = BICst.REPORT_STATUS.HANGOUT;
            self._refreshMarkIcon();
        });
        BI.Popovers.create(this._constant.PATH_CHOOSER, pathChooser, {
            width: 400,
            height: 340
        }).open(this._constant.PATH_CHOOSER);
    }
});
$.shortcut("bi.all_reports_list_item", BI.AllReportsListItem);