/**
 * Created by Young's on 2016/6/4.
 */
BI.ShareToMeSingleUser = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ShareToMeSingleUser.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-share-to-me-single-user"
        })
    },

    _init: function () {
        BI.ShareToMeSingleUser.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.reports = BI.createWidget({
            type: "bi.button_group"
        });
        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                type: "bi.label",
                text: o.userName,
                textAlign: "left",
                cls: "user-name",
                height: 50,
                hgap: 10
            }, this.reports]
        })
    },

    populate: function (viewType) {
        var reports = this.options.reports;
        var items = [];
        if (viewType === BI.ShareToMe.LIST_VIEW) {
            BI.each(reports, function (i, report) {
                items.push({
                    type: "bi.htape",
                    cls: "report-item-list",
                    items: [{
                        el: {
                            type: "bi.icon_button",
                            cls: (report.description === "true" ? "real-time-font" : "file-font") + " normal-mark",
                            iconWidth: 16,
                            iconHeight: 16
                        },
                        width: 40
                    }, {
                        el: {
                            type: "bi.text_button",
                            text: report.text,
                            title: report.text,
                            cls: "report-name",
                            textAlign: "left",
                            hgap: 10,
                            height: 40,
                            handler: function () {
                                FS.tabPane.addItem({
                                    title: report.text,
                                    src: FR.servletURL + report.buildUrl + "&show=_bi_show_"
                                });
                            }
                        }
                    }, {
                        el: {
                            type: "bi.label",
                            text: FR.date2Str(new Date(report.lastModify), "yyyy.MM.dd HH:mm:ss"),
                            cls: "time-text",
                            height: 40
                        },
                        width: 150
                    }],
                    height: 40
                })
            });
            this.reports.attr("layouts", [{
                type: "bi.vertical"
            }]);
            this.reports.populate(items);
            return;
        }
        BI.each(reports, function (i, report) {
            items.push({
                type: "bi.absolute",
                cls: "report-item-card",
                height: 130,
                width: 140,
                items: [{
                    el: {
                        type: "bi.icon_button",
                        cls: "card-view-report-icon",
                        title: report.text,
                        iconWidth: 90,
                        iconHeight: 75,
                        handler: function () {
                            FS.tabPane.addItem({
                                title: report.text,
                                src: FR.servletURL + report.buildUrl + "&show=_bi_show_"
                            });
                        }
                    },
                    left: 30,
                    top: 20
                }, {
                    el: {
                        type: "bi.label",
                        text: report.text,
                        title: report.text,
                        height: 30,
                        cls: "report-name"
                    },
                    bottom: 0,
                    left: 10,
                    right: 10
                }]
            })
        });
        this.reports.attr("layouts", [{
            type: "bi.left",
            hgap: 10,
            vgap: 10
        }]);
        this.reports.populate(items);

    }
});
$.shortcut("bi.share_to_me_single_user", BI.ShareToMeSingleUser);