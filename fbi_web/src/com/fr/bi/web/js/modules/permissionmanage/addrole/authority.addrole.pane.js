/**
 * Created by Young's on 2016/5/16.
 */
BI.AuthorityAddRolePane = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.AuthorityAddRolePane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-authority-add-role-pane"
        })
    },

    _init: function(){
        BI.AuthorityAddRolePane.superclass._init.apply(this, arguments);
        var self = this;
        var addRoleButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("Add_Role"),
            height: 30
        });
        addRoleButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.AuthorityAddRolePane.EVENT_ADD_ROLE);
        });

        this.roles = BI.createWidget({
            type: "bi.button_group",
            items: [],
            layouts: [{
                type: "bi.left"
            }]
        });

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.left",
                    items: [addRoleButton],
                    vgap: 10
                },
                height: 50
            }, {
                el: this.roles,
                height: "fill"
            }]
        })
    },
    
    populate: function(){
        
    }
});
BI.AuthorityAddRolePane.EVENT_ADD_ROLE = "EVENT_ADD_ROLE";
$.shortcut("bi.authority_add_role_pane", BI.AuthorityAddRolePane);