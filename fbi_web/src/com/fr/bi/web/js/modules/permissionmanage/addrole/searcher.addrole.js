/**
 * Created by Young's on 2016/5/17.
 */
BI.AddRoleSearcher = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.AddRoleSearcher.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-add-role-searcher"
        })
    },

    _init: function(){
        BI.AddRoleSearcher.superclass._init.apply(this, arguments);
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

        var searcher = BI.createWidget({
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
        searcher.setAdapter(this.roles);
        
        var cancelButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Cancel"),
            height: 30
        });
        cancelButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.AddRoleSearcher.EVENT_CANCEL);
        });
        
        var saveButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Sen_Confirm_Use_Selected_1", 0),
            height: 30
        });
        saveButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.AddRoleSearcher.EVENT_SAVE, self.roles.getValue());
        });
        
        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.left",
                    items: [searcher],
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
        this.populate();
    },
    
    populate: function(){
        var self = this, o = this.options;
        var allRoles = BI.Utils.getAuthorityRoles();
        var sortedRoles = BI.sortBy(allRoles, function(index, item) {
            return item.departmentid;
        });
        var settedRoles = o.roles;
        var items = [];
        BI.each(sortedRoles, function(i, role) {
            items.push({
                type: "bi.text_button",
                cls: "role-item",
                text: role.department_name  + ", " + role.post_name,
                value: role.id,
                height: 30,
                hgap: 5
            });
        });
        this.roles.populate(items);
    }
});
BI.AddRoleSearcher.EVENT_CANCEL = "EVENT_CANCEL";
BI.AddRoleSearcher.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.add_role_searcher", BI.AddRoleSearcher);