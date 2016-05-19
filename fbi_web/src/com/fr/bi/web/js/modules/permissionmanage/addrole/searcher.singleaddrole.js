/**
 * Created by Young's on 2016/5/17.
 */
BI.SingleAddRoleSearcher = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.SingleAddRoleSearcher.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-single-add-role-searcher"
        })
    },

    _init: function(){
        BI.SingleAddRoleSearcher.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.roles = BI.createWidget({
            type: "bi.button_group",
            items: [],
            chooseType: BI.Selection.Multi,
            layouts: [{
                type: "bi.left",
                scrollable: true,
                rgap: 10,
                vgap: 5
            }]
        });
        this.roles.on(BI.ButtonGroup.EVENT_CHANGE, function(){
             saveButton.setText(BI.i18nText("BI-Sen_Confirm_Use_Selected_1", this.getValue().length));
        });

        this.searcher = BI.createWidget({
            type: "bi.searcher",
            el: {
                type: "bi.search_editor",
                width: 260,
                height: 30
            },
            chooseType: BI.Selection.Multi,
            isAutoSearch: true,
            isAutoSync: true,
            popup: {
                type: "bi.searcher_view",
                searcher: {
                    type: "bi.button_group",
                    chooseType: BI.Selection.Multi,
                    behaviors: {
                        redmark: function () {
                            return true;
                        }
                    },
                    items: [],
                    layouts: [{
                        type: "bi.left",
                        rgap: 10,
                        vgap: 5
                    }]
                }
            }
        });
        this.searcher.setAdapter(this.roles);
        
        var cancelButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Cancel"),
            height: 30
        });
        cancelButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.SingleAddRoleSearcher.EVENT_CANCEL);
        });
        
        var saveButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Sen_Confirm_Use_Selected_1", 0),
            height: 30
        });
        saveButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.SingleAddRoleSearcher.EVENT_SAVE, self.roles.getValue());
        });
        
        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.left",
                    items: [this.searcher],
                    tgap: 10
                },
                height: 50
            }, {
                el: this.roles,
                height: "fill"
            }, {
                el: {
                    type: "bi.left_right_vertical_adapt",
                    items: {
                        left: [cancelButton],
                        right: [saveButton]
                    },
                    vgap: 10

                },
                height: 50
            }]
        });
    },
    
    populate: function(packageId){
        var allRoles = BI.Utils.getAuthorityRoles();
        var sortedRoles = BI.sortBy(allRoles, function(index, item) {
            return item.departmentid;
        });
        var settedRoles = BI.Utils.getPackageAuthorityByID(packageId);
        var items = [];
        BI.each(sortedRoles, function(i, role) {
            var found = BI.some(settedRoles, function(j, r){
                 if(r.role_id === role.id && r.role_type === role.role_type) {
                     return true;
                 }
            });
            if(found === true) {
                return;
            }
            var roleName = role.text || (role.department_name  + ", " + role.post_name);
            items.push({
                type: "bi.text_button",
                cls: "role-item",
                text: roleName,
                value: {
                    role_id: role.id,
                    role_type: role.role_type
                },
                height: 30,
                hgap: 5
            });
        });
        this.roles.populate(items);
        this.searcher.stopSearch();
    }
});
BI.SingleAddRoleSearcher.EVENT_CANCEL = "EVENT_CANCEL";
BI.SingleAddRoleSearcher.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.single_add_role_searcher", BI.SingleAddRoleSearcher);