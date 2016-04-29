/**
 * create by young
 */
BI.FolderMoveToPane = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend(BI.FolderMoveToPane.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function(){
        BI.FolderMoveToPane.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function(north){
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Move_Template_To"),
            height: 50,
            textAlign: "left"
        })
    },

    rebuildCenter: function(center){
        var self = this, o = this.options;
        var items = o.items, selectedItems = o.selectedItems;
        var folderName = BI.createWidget({
            type: "bi.label",
            cls: "selected-folder",
            text: " “” ",
            textAlign: "left",
            height: 30
        });
        var tree = new BI.Tree();
        tree.initTree(BI.Tree.transformToTreeFormat(this._formatItems(items, selectedItems)));
        var folderTree = BI.createWidget({
            type: "bi.multilayer_select_level_tree",
            items: tree.toJSON(),
            cls: "folder-tree"
        });
        folderTree.on(BI.MultiLayerSelectLevelTree.EVENT_CHANGE, function(){
            folderName.setText("“" + tree.search(this.getValue()[0]).get("data").text + "”");
            self.toFolder = this.getValue()[0];
        });

        BI.createWidget({
            type: "bi.vtape",
            cls: "bi-move-to-pane",
            element: center,
            items: [{
                el: folderName,
                height: 30
            }, {
                el: {
                    type: "bi.vertical",
                    cls: "folder-tree-container",
                    items: [folderTree]
                },
                height: "fill"
            }],
            hgap: 10,
            vgap: 5
        });

    },

    end: function(){
        this.fireEvent(BI.FolderMoveToPane.EVENT_MOVE, this.options.selectedItems, this.toFolder);
    },

    _formatItems: function(items, selectedItems){
        var formatItems = [];
        function getFolderChildFolders(folderId){
            var folders = [];
            BI.each(items, function(i, item){
                if(BI.isNull(item.buildUrl) && folderId === item.pId){
                    folders.push(item.id);
                    folders = folders.concat(getFolderChildFolders(item.id));
                }
            });
            return folders;
        }
        BI.each(items, function(i, item){
            var allFolders = [];
            BI.each(selectedItems, function(j, sItem){
                allFolders.push(sItem);
                allFolders = allFolders.concat(getFolderChildFolders(sItem));
            });
            if(BI.isNull(item.buildUrl) && !allFolders.contains(item.id)){
                formatItems.push({
                    id: item.id,
                    pId: item.pId,
                    text: item.text,
                    value: item.value
                })
            }
        });
        formatItems.push({
            id: BI.FileManagerNav.ROOT_CREATE_BY_ME,
            text: BI.i18nText("BI-Created_By_Me"),
            value: BI.FileManagerNav.ROOT_CREATE_BY_ME
        });
        return formatItems;
    }
});
BI.FolderMoveToPane.EVENT_MOVE = "EVENT_MOVE";
$.shortcut("bi.folder_move_to_pane", BI.FolderMoveToPane);