/**
 * Created by Young's on 2016/5/31.
 */
BI.AllReportsCardItem = BI.inherit(BI.Widget, {
    _constant: {
        PATH_CHOOSER: "__hangout_report_popover__"
    },

    _defaultConfig: function(){
        return BI.extend(BI.AllReportsCardItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-all-reports-card-item",
            height: 130,
            width: 140
        })
    },

    _init: function(){
        BI.AllReportsCardItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var report = o.report;
        this.status = report.status;
        this.model = new BI.AllReportsItemModel({
            roles: o.roles,
            users: o.users
        });
        if(this.status === BICst.REPORT_STATUS.APPLYING) {
            this.hangoutIcon = BI.createWidget({
                type: "bi.icon_button",
                cls: "report-apply-hangout-ing-font normal-mark",
                title: BI.i18nText("BI-Hangout_Now"),
                invisible: true,
                stopPropagation: true,
                width: 20,
                height: 20,
                iconWidth: 16,
                iconHeight: 16
            });
            this.hangoutIcon.on(BI.IconButton.EVENT_CHANGE, function () {
                self._onClickHangout();
            });
        }

        if(this.status !== BICst.REPORT_STATUS.NORMAL) {
            this.markIcon = BI.createWidget({
                type: "bi.icon_change_button",
                cls: "mark-icon",
                width: 16,
                height: 16
            });
            this._refreshMarkIcon();
        }
        
        var userName = this.model.getUserNameByUserId(report.createBy);
        var departName = this.model.getDepartNameByUserId(report.createBy);
        var roleName = this.model.getRoleByUserId(report.createBy);
        
        var infoIcon = BI.createWidget({
            type: "bi.combo",
            el: {
                type: "bi.icon_button",
                cls: "rename-font normal-mark",
                iconWidth: 16,
                iconHeight: 16,
                width: 20,
                height: 20
            },
            trigger: "hover",
            direction: "right",
            popup: {
                el: {
                    type: "bi.vertical",
                    cls: "info-card",
                    items: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Users") + ": " + userName,
                        title: userName,
                        textAlign: "left",
                        height: 30,
                        hgap: 5
                    }, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Role") + ": " + roleName,
                        title: roleName,
                        textAlign: "left",
                        height: 30,
                        hgap: 5
                    }, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Department") + ": " + departName,
                        title: departName,
                        textAlign: "left",
                        height: 30,
                        hgap: 5
                    }, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Last_Modify_Date") + ": " + FR.date2Str(new Date(report.lastModify), "yyyy.MM.dd HH:mm:ss"),
                        title: FR.date2Str(new Date(report.lastModify), "yyyy.MM.dd HH:mm:ss"),
                        textAlign: "left",
                        height: 30,
                        hgap: 5
                    }],
                    width: 200
                }
            }
        });
        infoIcon.setVisible(false);

        var card = BI.createWidget({
            type: "bi.icon_button",
            cls: "card-view-report-icon",
            iconWidth: 90,
            iconHeight: 75
        });

        card.on(BI.IconButton.EVENT_CHANGE, function () {
            FS.tabPane.addItem({
                title: report.text,
                src: FR.servletURL + report.buildUrl
            });
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: card,
                left: 30,
                top: 20
            }, {
                el: {
                    type: "bi.label",
                    text: report.text,
                    cls: "",
                    height: 30,
                    textAlign: "center"
                },
                left: 10,
                right: 10,
                bottom: 0
            }, {
                el: this.hangoutIcon || BI.createWidget(),
                right: 5,
                top: 30
            }, {
                el: infoIcon,
                right: 5,
                top: 5
            }, {
                el: this.markIcon || BI.createWidget(),
                top: 20,
                left: 40
            }]
        });

        this.element.hover(function () {
            self.hangoutIcon && self.hangoutIcon.setVisible(true);
            infoIcon.setVisible(true);
        }, function () {
            self.hangoutIcon && self.hangoutIcon.setVisible(false);
            infoIcon.setVisible(false);
        });
    },

    _refreshMarkIcon: function(){
        if(this.status === BICst.REPORT_STATUS.APPLYING) {
            this.markIcon.setIcon("report-apply-hangout-ing-font");
            this.markIcon.setTitle(BI.i18nText("BI-Report_Hangout_Applying"));
            return;
        }
        this.markIcon.setIcon("report-hangout-font");
        this.markIcon.setTitle(BI.i18nText("BI-Hangouted"));
    },

    _onClickHangout: function(){
        var self = this, o = this.options;
        BI.Popovers.remove(this._constant.PATH_CHOOSER);
        var pathChooser = BI.createWidget({
            type: "bi.report_hangout_path_chooser",
            reportName: this.options.report.text
        });
        pathChooser.on(BI.ReportHangoutPathChooser.EVENT_SAVE, function(){
            o.onHangout(this.getValue());
            self.hangoutIcon.destroy();
            self.status = BICst.REPORT_STATUS.HANGOUT;
            self._refreshMarkIcon();
        });
        BI.Popovers.create(this._constant.PATH_CHOOSER, pathChooser, {width: 400, height: 340}).open(this._constant.PATH_CHOOSER);
    }
});
$.shortcut("bi.all_reports_card_item", BI.AllReportsCardItem);