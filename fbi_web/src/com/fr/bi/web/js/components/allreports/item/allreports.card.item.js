/**
 * Created by Young's on 2016/5/31.
 */
BI.AllReportsCardItem = BI.inherit(BI.Widget, {
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
        this.model = new BI.AllReportsItemModel({
            roles: o.roles,
            users: o.users
        });
        var hangoutButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "remove-report-font tool-delete-icon",
            title: BI.i18nText("BI-Remove"),
            iconWidth: 20,
            iconHeight: 20,
            stopPropagation: true
        });
        hangoutButton.on(BI.IconButton.EVENT_CHANGE, function () {
            FS.tabPane.addItem({
                title: report.text,
                src: FR.servletURL + report.buildUrl + "&edit=_bi_edit_"
            });
        });
        hangoutButton.setVisible(false);
        
        var userName = this.model.getUserNameByUserId(report.createBy);
        var departName = this.model.getDepartNameByUserId(report.createBy);
        var roleName = this.model.getRoleByUserId(report.createBy);
        
        var infoIcon = BI.createWidget({
            type: "bi.combo",
            el: {
                type: "bi.icon_button",
                cls: "rename-font",
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

        var packageButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "card-view-report-icon",
            iconWidth: 90,
            iconHeight: 75
        });

        packageButton.on(BI.IconButton.EVENT_CHANGE, function () {

        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: packageButton,
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
                el: hangoutButton,
                right: 0,
                top: 0
            }, {
                el: infoIcon,
                right: 0,
                top: 30
            }]
        });

        this.element.hover(function(){
            hangoutButton.setVisible(true);
            infoIcon.setVisible(true);
        }, function(){
            hangoutButton.setVisible(false);
            infoIcon.setVisible(false);
        });
    },

    
});
$.shortcut("bi.all_reports_card_item", BI.AllReportsCardItem);