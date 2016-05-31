/**
 * Created by Young's on 2016/5/30.
 */
BI.AllReportsFilter = BI.inherit(BI.Widget, {

    _constant: {
        REPORT_STATUS_APPLYING: 1,
        REPORT_STATUS_NORMAL: 2,
        REPORT_STATUS_HANGOUT: 3
    },

    _defaultConfig: function(){
        return BI.extend(BI.AllReportsFilter.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-all-reports-filter"
        })
    },

    _init: function(){
        BI.AllReportsFilter.superclass._init.apply(this, arguments);
        var self = this;
        this.depart = BI.createWidget({
            type: "bi.value_chooser_combo",
            itemsCreator: BI.bind(this._departCreator, this),
            height: 30,
            width: 180
        });
        this.role = BI.createWidget({
            type: "bi.value_chooser_combo",
            itemsCreator: BI.bind(this._rolesCreator, this),
            height: 30,
            width: 180
        });
        this.name = BI.createWidget({
            type: "bi.value_chooser_combo",
            itemsCreator: BI.bind(this._usersCreator, this),
            height: 30,
            width: 180
        });
        this.status = BI.createWidget({
            type: "bi.value_chooser_combo",
            itemsCreator: BI.bind(this._statusCreator, this),
            height: 30,
            width: 180
        });
        this.lastModify = BI.createWidget({
            type: "bi.time_interval",
            height: 30,
            width: 380
        });
        var reset = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Reset"),
            height: 30,
            width: 90
        });
        reset.on(BI.Button.EVENT_CHANGE, function () {
            self.depart.setValue();
            self.role.setValue();
            self.name.setValue();
            self.status.setValue();
            self.lastModify.setValue();
        });
        this.filterPane = BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            cls: "filter-pane",
            items: [{
                el: {
                    type: "bi.left",
                    cls: "inner-wrapper",
                    scrollable: true,
                    items: [{
                        type: "bi.left",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Department"),
                            cls: "filter-type",
                            height: 30,
                            hgap: 10
                        }, this.depart]
                    }, {
                        type: "bi.left",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Role"),
                            cls: "filter-type",
                            height: 30,
                            hgap: 10
                        }, this.role]
                    }, {
                        type: "bi.left",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Users"),
                            cls: "filter-type",
                            height: 30,
                            hgap: 10
                        }, this.name]
                    }, {
                        type: "bi.left",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Status"),
                            cls: "filter-type",
                            height: 30,
                            hgap: 10
                        }, this.status]
                    }, {
                        type: "bi.left",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Last_Modify_Date"),
                            cls: "filter-type",
                            height: 30,
                            hgap: 10
                        }, this.lastModify]
                    }, reset],
                    rgap: 30,
                    tgap: 10
                },
                top: 0,
                left: 0,
                bottom: 0,
                right: 0
            }]
        });
    },

    _departCreator: function(op, callback){
        var items = [];
        BI.each(this.departs, function(i, depart){
            items.push({
                text: depart.text,
                value: depart.id,
                title: depart.text
            });
        });
        callback(items);
    },

    _rolesCreator: function(options, callback){
        var items = [];
        BI.each(this.roles, function(i, role){
            var roleName = role.text;
            if(BI.isNotNull(role.departmentname)){
                roleName = role.departmentname + role.postname
            }
            items.push({
                text: roleName,
                value: role.id,
                title: roleName
            });
        });
        callback(items);
    },

    _usersCreator: function(options, callback) {
        var items = [];
        BI.each(this.users, function(i, user){
            items.push({
                text: user.realname,
                value: user.id,
                title: user.realname
            });
        });
        callback(items);
    },

    _statusCreator: function(options, callback){
        var items = [{
            text: BI.i18nText(""),
            value: this._constant.REPORT_STATUS_APPLYING,
            title: BI.i18nText("")
        }, {
            text: BI.i18nText(""),
            value: this._constant.REPORT_STATUS_NORMAL,
            title: BI.i18nText("")
        }, {
            text: BI.i18nText(""),
            value: this._constant.REPORT_STATUS_HANGOUT,
            title: BI.i18nText("")
        }];
        callback(items);
    },

    getValue: function(){
        return {
            departs: this.depart.getValue(),
            roles: this.role.getValue(),
            users: this.name.getValue(),
            status: this.status.getValue(),
            last_modify: this.lastModify.getValue()
        }
    },

    populate: function(departs, roles, users){
        this.departs = departs;
        this.roles = roles;
        this.users = users;
    }
});
$.shortcut("bi.all_reports_filter", BI.AllReportsFilter);