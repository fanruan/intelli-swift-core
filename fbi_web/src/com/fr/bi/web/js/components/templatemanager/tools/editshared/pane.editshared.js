/**
 * Created by Young's on 2016/6/28.
 */
BI.EditSharedPane = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function () {
        return BI.extend(BI.EditSharedPane.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BI.EditSharedPane.superclass._init.apply(this, arguments);

    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Shared_To_Users"),
            height: 50,
            textAlign: "left"
        })
    },

    rebuildCenter: function (center) {
        var self = this, o = this.options;
        this.sharedList = BI.createWidget({
            type: "bi.selected_user_group_list",
            element: center
        });
        this.sharedList.on(BI.Controller.EVENT_CHANGE, function(type, value){
             //删除用户
            BI.each(self.usersMap, function(roleName, users){
                var newUsers = [];
                BI.each(users, function(i, user) {
                    if(user.user_id.toString() !== value) {
                        newUsers.push(user);
                    }
                });
                if(newUsers.length === 0 ) {
                    delete self.usersMap[roleName];
                } else {
                    self.usersMap[roleName] = newUsers;
                }
            });
            this.populate(self.usersMap);
        });
        this._refreshSharedList(o.shared);
    },

    _refreshSharedList: function (users) {
        var self = this;
        this.usersMap = {};
        BI.each(users, function (i, item) {
            if (BI.isNotNull(item.roles) && BI.isNotEmptyArray(item.roles)) {
                BI.each(item.roles, function (j, role) {
                    if (BI.isNull(self.usersMap[role])) {
                        self.usersMap[role] = [];
                    }
                    self.usersMap[role].push(item);
                })
            }
            var department = item.user_department;
            if (BI.isNotNull(department) && department !== "") {
                if (BI.isNull(self.usersMap[department])) {
                    self.usersMap[department] = [];
                }
                self.usersMap[department].push(item);
            }
            if ((BI.isNull(item.roles) || item.roles.length === 0) && (BI.isNull(department) || department === "")) {
                if (BI.isNull(self.usersMap[BI.i18nText("BI-User_Without_Role")])) {
                    self.usersMap[BI.i18nText("BI-User_Without_Role")] = [];
                }
                self.usersMap[BI.i18nText("BI-User_Without_Role")].push(item);
            }
        });
        this.sharedList.populate(self.usersMap);
    },

    end: function () {
        this.fireEvent(BI.EditSharedPane.EVENT_SAVE, this.usersMap);
    },
    
    close: function(){
        this.fireEvent(BI.EditSharedPane.EVENT_CLOSE);
    },

    getValue: function(){
        var users = [];
        BI.each(this.usersMap, function(roleName, us){
            BI.each(us, function(i, user){
                users.push(user.user_id); 
            });
        });
        return BI.uniq(users);
    }
});
BI.EditSharedPane.EVENT_CLOSE = "EVENT_CLOSE";
BI.EditSharedPane.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.edit_shared_pane", BI.EditSharedPane);