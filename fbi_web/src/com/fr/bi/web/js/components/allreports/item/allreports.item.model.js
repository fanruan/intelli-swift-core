/**
 * Created by Young's on 2016/6/1.
 */
BI.AllReportsItemModel = BI.inherit(FR.OB, {
    _init: function(){
        BI.AllReportsItemModel.superclass._init.apply(this, arguments);
        var o = this.options;
        this.roles = o.roles;
        this.users = o.users;
    },

    getRoleByUserId: function(id){
        var roleNames = [];
        BI.each(this.roles, function(i, role){
            var roleName = role.text || (role.departmentname + role.postname);
            if(!roleNames.contains(roleName) && role.users.contains(id)){
                roleNames.push(roleName);
            }
        });
        return roleNames;
    },

    getDepartNameByUserId: function(id) {
        var roleNames = [];
        BI.each(this.roles, function(i, role){
            var roleName = role.text || role.departmentname;
            if(!roleNames.contains(roleName) && role.users.contains(id)){
                roleNames.push(roleName);
            }
        });
        return roleNames;
    },

    getUserNameByUserId: function(id){
        var userName = "";
        BI.some(this.users, function(i, user){
            if(id === user.id){
                userName = user.realname;
                return true;
            }
        });
        return userName;
    }
});