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
                        text: BI.i18nText("BI-Users") + ": " + self._getUserNameByUserId(report.createBy),
                        title: BI.i18nText("BI-Users") + ": " + self._getUserNameByUserId(report.createBy),
                        textAlign: "left",
                        height: 30,
                        hgap: 5
                    }, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Role") + ": " + self._getRoleByUserId(report.createBy),
                        title: BI.i18nText("BI-Role") + ": " + self._getRoleByUserId(report.createBy),
                        textAlign: "left",
                        height: 30,
                        hgap: 5
                    }, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Department") + ": " + self._getDepartNameByUserId(report.createBy),
                        title: BI.i18nText("BI-Department") + ": " + self._getDepartNameByUserId(report.createBy),
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

    _getRoleByUserId: function(id){
        var roles = this.options.roles;
        var roleNames = [];
        BI.each(roles, function(i, role){
            var roleName = role.text || (role.departmentname + role.postname);
            if(!roleNames.contains(roleName) && role.users.contains(id)){
                roleNames.push(roleName);
            }
        });
        return roleNames;
    },

    _getDepartNameByUserId: function(id) {
        var roles = this.options.roles;
        var roleNames = [];
        BI.each(roles, function(i, role){
            var roleName = role.text || role.departmentname;
            if(!roleNames.contains(roleName) && role.users.contains(id)){
                roleNames.push(roleName);
            }
        });
        return roleNames;
    },

    _getUserNameByUserId: function(id){
        var userName = "";
        var users = this.options.users;
        BI.some(users, function(i, user){
            if(id === user.id){
                userName = user.realname;
                return true;
            }
        });
        return userName;
    }
});
$.shortcut("bi.all_reports_card_item", BI.AllReportsCardItem);