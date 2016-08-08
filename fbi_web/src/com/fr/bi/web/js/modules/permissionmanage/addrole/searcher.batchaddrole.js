/**
 * Created by Young's on 2016/5/17.
 */
BI.BatchAddRoleSearcher = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.BatchAddRoleSearcher.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-batch-add-role-searcher"
        })
    },

    _init: function(){
        BI.BatchAddRoleSearcher.superclass._init.apply(this, arguments);
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
             self.saveButton.setText(BI.i18nText("BI-Sen_Confirm_Use_Selected_1", this.getValue().length));
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
                },
                matcher: {
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
            self.fireEvent(BI.BatchAddRoleSearcher.EVENT_CANCEL);
        });
        
        this.saveButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Sen_Confirm_Use_Selected_1", 0),
            height: 30
        });
        this.saveButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.BatchAddRoleSearcher.EVENT_SAVE, self.roles.getValue());
        });
        
        this.selectedTree = BI.createWidget({
            type: "bi.display_tree",
            cls: "selected-packages"
        });
        
        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.absolute",
                    items: [{
                        el: this.selectedTree,
                        top: 0,
                        left: 0,
                        bottom: 0,
                        right: 19
                    }]
                },
                width: 220
            }, {
                el: {
                    type: "bi.vtape",
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
                                right: [this.saveButton]
                            },
                            vgap: 10

                        },
                        height: 50
                    }]
                },
                width: "fill"
            }]
        });
    },
    
    populate: function(packages){
        var self = this, o = this.options;
        var allRoles = BI.Utils.getAuthorityRoles();
        var sortedRoles = BI.sortBy(allRoles, function(index, item) {
            return item.departmentid;
        });
        var items = [];
        BI.each(sortedRoles, function(i, role) {
            var roleName = role.text || (role.department_name  + "," + role.post_name);
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
        
        var treeItems = [];
        var allGroupIds = BI.Utils.getConfPackageGroupIDs();
        var pgMap = {}, gIds = [];
        BI.each(allGroupIds, function(i, gId){
            var pids = BI.Utils.getConfGroupChildrenByGroupId(gId);
            BI.each(pids, function(i, pack){
                if(packages.contains(pack.id)) {
                    var gs = pgMap[pack.id] || [];
                    pgMap[pack.id] = BI.uniq(gs.concat([gId]));
                    !gIds.contains(gId) && gIds.push(gId);
                }
            });
        });

        BI.each(gIds, function(i, gId){
            treeItems.push({
                id: gId,
                text: BI.Utils.getConfGroupNameByGroupId(gId),
                open: true
             });
        });

        var hasUngroup = false;
        BI.each(BI.uniq(packages), function(i, pId) {
            var gIds = pgMap[pId];
            if(BI.isNull(pgMap[pId])) {
                hasUngroup = true;
                gIds = [-1];
            }
            BI.each(gIds, function(i, gid){
                treeItems.push({
                    id: pId,
                    text: BI.Utils.getPackageNameByID4Conf(pId),
                    pId: gid
                });
            })
        });
        if(hasUngroup === true) {
            treeItems.push({
                id: -1,
                text: BI.i18nText("BI-Ungrouped"),
                open: true
            });
        }
        this.selectedTree.initTree(treeItems);
        this.saveButton.setText(BI.i18nText("BI-Sen_Confirm_Use_Selected_1", this.roles.getValue().length));
    }
});
BI.BatchAddRoleSearcher.EVENT_CANCEL = "EVENT_CANCEL";
BI.BatchAddRoleSearcher.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.batch_add_role_searcher", BI.BatchAddRoleSearcher);