/**
 * create by young
 * 已选用户列表
 */
BI.SelectedUserGroupList = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.SelectedUserGroupList.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-selected-user-group-list"
        })
    },

    _init: function(){
        BI.SelectedUserGroupList.superclass._init.apply(this, arguments);
        this.userlist = BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            hgap: 5,
            vgap: 2
        });
    },

    populate: function(items){
        var self = this;
        this.userlist.empty();
        BI.each(items, function(role, users){
            var group = BI.createWidget({
                type: "bi.button_group",
                items: BI.createItems(users, {
                    type: "bi.selected_user_button"
                }),
                layouts: [{
                    type: "bi.left",
                    hgap: 5
                }]
            });
            group.on(BI.Controller.EVENT_CHANGE, function(){
                self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
            });
            var container = BI.createWidget({
                type: "bi.vertical",
                items: [{
                    type: "bi.left",
                    items: [{
                        type: "bi.label",
                        cls: "role-name",
                        text: role,
                        height: 30
                    }, {
                        type: "bi.label",
                        cls: "role-count",
                        text: "(" + users.length + ")",
                        height: 30
                    }]
                }, group],
                hgap: 2,
                vgap: 2
            });
            self.userlist.addItem(container);
        })
    }
});
BI.SelectedUserGroupList.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.selected_user_group_list", BI.SelectedUserGroupList);