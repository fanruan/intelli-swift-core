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
        if(this.status !== BICst.REPORT_STATUS.NORMAL) {
            this.hangoutIcon = BI.createWidget({
                type: "bi.icon_change_button",
                cls: "normal-mark",
                invisible: true,
                stopPropagation: true
            });
            this.hangoutIcon.on(BI.IconChangeButton.EVENT_CHANGE, function () {
                self._onClickHangout();
            });
            this.hangoutIcon.setVisible(false);
            
            this.markIcon = BI.createWidget({
                type: "bi.icon_change_button",
                cls: "mark-icon",
                width: 12,
                height: 12
            });
            this._refreshHangout();
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
            popup: {
                el: {
                    type: "bi.vertical",
                    items: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Users") + ": " + userName,
                        title: BI.i18nText("BI-Users") + ": " + userName,
                        textAlign: "left",
                        height: 30,
                        hgap: 5
                    }, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Role") + ": " + roleName,
                        title: BI.i18nText("BI-Role") + ": " + roleName,
                        textAlign: "left",
                        height: 30,
                        hgap: 5
                    }, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Department") + ": " + departName,
                        title: BI.i18nText("BI-Department") + ": " + departName,
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

        var timeText = BI.createWidget({
            type: 'bi.label',
            cls: "template-file-item-date",
            text: FR.date2Str(new Date(report.lastModify), "yyyy.MM.dd HH:mm:ss")
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
                src: FR.servletURL + report.buildUrl + "&edit=_bi_edit_"
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
                                cls: "file-font normal-mark",
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
                        type: "bi.left_right_vertical_adapt",
                        items: {
                            left: [this.hangoutIcon || BI.createWidget(), infoIcon],
                            right: [timeText]
                        },
                        llgap: 20,
                        rrgap: 20
                    },
                    width: 260
                }]
        });
    },

    _onClickHangout: function(){
        BI.Popovers.remove(this._constant.PATH_CHOOSER);
        var pathChooser = BI.createWidget({
            type: "bi.report_hangout_path_chooser"
        });
        BI.Popovers.create(this._constant.PATH_CHOOSER, pathChooser).open(this._constant.PATH_CHOOSER);
    },

    _refreshHangout: function(){
        if(this.status === BICst.REPORT_STATUS.APPLYING) {
            this.hangoutIcon.setIcon("report-apply-hangout-ing-font");
            this.markIcon.setIcon("report-apply-hangout-ing-font");
            this.hangoutIcon.setTitle(BI.i18nText("BI-Hangout_Now"));
            this.markIcon.setTitle(BI.i18nText("BI-Report_Hangout_Applying"));
            return;
        }
        this.hangoutIcon.setIcon("report-cancel-hangout-font");
        this.hangoutIcon.setTitle(BI.i18nText("BI-Cancel_Hangout"));
        this.markIcon.setIcon("report-hangout-font");
        this.markIcon.setTitle(BI.i18nText("BI-Hangouted"));
    }
});
$.shortcut("bi.all_reports_list_item", BI.AllReportsListItem);