/**
 * Created by Young's on 2016/5/31.
 */
BI.AllReportsListItem = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.AllReportsListItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-all-reports-list-item"
        })
    },

    _init: function () {
        BI.AllReportsListItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var report = o.report;

        var hangoutIcon = BI.createWidget({
            type: "bi.icon_button",
            cls: 'delete-template-font template-item-icon',
            title: BI.i18nText("BI-Remove"),
            invisible: true,
            stopPropagation: true
        });
        hangoutIcon.on(BI.IconButton.EVENT_CHANGE, function () {

        });
        hangoutIcon.setVisible(false);

        var infoIcon = BI.createWidget({
            type: "bi.combo",
            el: {
                type: "bi.icon_button",
                cls: "rename-font",
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

        var timeText = BI.createWidget({
            type: 'bi.label',
            cls: "template-file-item-date",
            text: FR.date2Str(new Date(report.lastModify), "yyyy.MM.dd HH:mm:ss")
        });

        this.element.hover(function () {
            hangoutIcon.setVisible(true);
            infoIcon.setVisible(true);
        }, function () {
            hangoutIcon.setVisible(false);
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
                        type: "bi.center_adapt",
                        items: [{
                            type: "bi.icon_button",
                            cls: "file-font",
                            iconWidth: 16,
                            iconHeight: 16
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
                            left: [hangoutIcon, infoIcon],
                            right: [timeText]
                        },
                        llgap: 20,
                        rrgap: 20
                    },
                    width: 260
                }]
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
$.shortcut("bi.all_reports_list_item", BI.AllReportsListItem);