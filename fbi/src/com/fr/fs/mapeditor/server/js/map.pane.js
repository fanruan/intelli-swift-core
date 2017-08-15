/**
 * Created by eason on 2017/6/28.
 */
(function(){

    FR.$defaultImport('/com/fr/fs/web/css/manager/fs_reportmgr.css', 'css');

    var DO_NOTHING = function(){};
    var GEO_ROOT = "assets/map/geographic/";
    var IMAGE_ROOT = "assets/map/image/";
    var JSON_EXTENSION = ".json";
    var GEO_MAP = "geographic_map";
    var IMAGE_MAP = "image_map";
    var MAP_WIDTH = 906;
    var DIR_WIDTH = 231;
    var DIR_HEIGHT = 450;
    var DIR_MAP_GAP = 20;
    var DIR_X = 40;
    var DIR_Y = 75;
    var TILE_LAYER_VALUE = '1';
    var WMS_LAYER_VALUE = '2';

    var EDITOR = {

        geoContext:null,

        imageContext:null,

        geoDirTableTree:null,

        imageDirTableTree:null,

        init:function(renderEl){

            this.renderEl = renderEl;

            DATA.initData(function(){
                EDITOR.tabPane = new FS.LTabPane({
                    width: '100%',
                    height: '100%',
                    style: 'blue',
                    marginLeft: 0,
                    renderEl: renderEl,
                    items: [
                        EDITOR.createEditorTab(GEO_MAP),
                        EDITOR.createEditorTab(IMAGE_MAP),
                        EDITOR.createMapLayerTab()
                    ],
                    initAfterActions: [
                        function () {
                            EDITOR._createMap(GEO_MAP);

                            EDITOR._createTableTree(GEO_MAP);
                        },

                        function(){
                            EDITOR._createMap(IMAGE_MAP);

                            EDITOR._createTableTree(IMAGE_MAP);
                        },

                        function(){
                            EDITOR._createLayerTableTree();
                            EDITOR._createLayerEditor();
                        }
                    ]
                });
            });
        },

        createMapLayerTab:function(){
            var layerDirPane = {
                type: 'panel',
                doSize: false,
                y: DIR_Y,
                x: DIR_X,
                width: DIR_WIDTH,
                height: DIR_HEIGHT,
                title: FR.i18nText("BI-FS-Module_Map_Editor_GIS_LAYER"),
                border: true,
                contentHtml: $('<div/>').addClass('layer_dir_panel_content'),
                tools: [{
                    iconCls: 'fs-module-add-white',
                    width: 24,
                    height: 24,
                    handler: function () {
                        var addDirDialog = EDITOR.createJSONDialog(FR.i18nText('BI-FS-Module_Map_Editor_Create_GIS_LAYER'), FR.i18nText("BI-FS-Module_Map_Editor_GIS_LAYER_NAME"), onOK, DO_NOTHING);
                        addDirDialog.setVisible(true);
                        function onOK(){
                            var dirname = addDirDialog.getWidgetByName("nameText").getValue();
                            CONTROL.addLayerEntry(dirname);
                        }

                    }
                }]
            };

            var layerEditorPane = {
                type: 'panel',
                widgetName: 'layerEditorPane',
                doSize: false,
                border: false,
                x: DIR_X + DIR_MAP_GAP + DIR_WIDTH,
                y: DIR_Y,
                width: MAP_WIDTH,
                height: DIR_HEIGHT,
                contentHtml: $('<div/>').addClass('map_layer_container').css("position", "absolute").css("width", MAP_WIDTH + "px").css("height", DIR_HEIGHT + "px")
            };

            return {
                title: FR.i18nText("BI-FS-Module_Map_Editor_GIS_LAYER"),
                content: {
                    type: 'absolute',
                    scrollable: true,
                    width: '100%',
                    height: '100%',
                    top: 15,
                    left: 20,
                    items: [layerDirPane, layerEditorPane]
                }
            }

        },

        createEditorTab:function(mapType){
            var tabTitle = FR.i18nText("BI-FS-Module_Map_Editor_Geographic");
            var dialogTitle = FR.i18nText('BI-FS-Module_Map_Editor_Create_Geo_Map');
            var dirClass = 'geo_map_dir_panel_content';
            var mapContainerClass = "geographic_map_container";

            if(mapType == IMAGE_MAP){
                tabTitle = FR.i18nText("BI-FS-Module_Map_Editor_Custom_Image");
                dialogTitle = FR.i18nText("BI-FS-Module_Map_Editor_Create_Image_Map");
                dirClass = 'image_map_dir_panel_content';
                mapContainerClass = "image_map_container";
            }

            var mapEditorPane = {
                type: 'panel',
                doSize: false,
                y: DIR_Y,
                x: DIR_X,
                width: DIR_WIDTH,
                height: DIR_HEIGHT,
                title: tabTitle,
                border: true,
                contentHtml: $('<div/>').addClass(dirClass),
                tools: [{
                    //添加目录按钮及其事件
                    iconCls: 'fs-module-add-white',
                    width: 24,
                    height: 24,
                    handler: function () {

                        var dirTableTree = mapType == GEO_MAP ? EDITOR.geoDirTableTree : EDITOR.imageDirTableTree;

                        var selected = dirTableTree.getSelectedNodes()[0];

                        if (!selected) {
                            FR.Msg.alert(FR.i18nText("FS-Generic-Simple_Alert"), FR.i18nText("FS-Report-Do_Select_Dir"));
                            return;
                        }

                        var addDirDialog = EDITOR.createJSONDialog(dialogTitle, FR.i18nText('BI-FS-Module_Map_Editor_File_Name'), onOK, DO_NOTHING);
                        addDirDialog.setVisible(true);

                        function onOK(){
                            var dirname = addDirDialog.getWidgetByName("nameText").getValue();
                            CONTROL.addEntry(mapType, dirname, selected);
                        }

                    }
                }]
            };

            var cptMgrPane = {
                type: 'panel',
                widgetName: 'mapEditorPane',
                doSize: false,
                border: false,
                x: DIR_X + DIR_MAP_GAP + DIR_WIDTH,
                y: DIR_Y,
                width: MAP_WIDTH,
                height: DIR_HEIGHT,
                contentHtml: $('<div/>').addClass(mapContainerClass).css("position", "absolute").css("width", MAP_WIDTH + "px").css("height", DIR_HEIGHT + "px")
            };

            return {
                title: tabTitle,
                content: {
                    type: 'absolute',
                    scrollable: true,
                    width: '100%',
                    height: '100%',
                    top: 15,
                    left: 20,
                    items: [mapEditorPane, cptMgrPane]
                }
            }
        },

        createConfirmEditDialog:function(okCallback, cancelCallback){
            var dirDialog = new FR.Dialog({
                text4OK: FR.i18nText("FS-Generic-Simple_OK"),
                text4Cancel: FR.i18nText("FS-Generic-Simple_Cancel"),
                onOK: function () {
                    okCallback.apply(this);
                },
                onCancel: function () {
                    cancelCallback.apply(this);
                },
                destroyOnClose: true,
                animate: false,
                width: 300,
                height: 130,
                confirm: true,
                border: true,
                closable: true,
                contentWidget: {
                    type: 'absolute',
                    items: [
                        {
                            type: 'llabel',
                            value: FR.i18nText('BI-FS-Module_Map_Editor_Save_Edit'),
                            height: 21,
                            width: 300,
                            textalign: 'left',
                            x: 0,
                            y: 16
                        }]
                }
            });
            return dirDialog;
        },

        createJSONDialog: function (title, labelText, okCallback, cancelCallback) {
            var hGap = 10, labelWidth = 80;
            var dirDialog = new FR.Dialog({
                title: title,
                text4OK: FR.i18nText("FS-Generic-Simple_OK"),
                text4Cancel: FR.i18nText("FS-Generic-Simple_Cancel"),
                onOK: function () {
                    okCallback.apply(this);
                },
                onCancel: function () {
                    cancelCallback.apply(this);
                },
                destroyOnClose: true,
                animate: false,
                width: 300,
                height: 150,
                confirm: true,
                border: true,
                closable: true,
                contentWidget: {
                    type: 'absolute',
                    items: [
                        {
                            type: 'llabel',
                            value: labelText,
                            height: 21,
                            width: labelWidth,
                            textalign: 'right',
                            x: 0,
                            y: 16
                        },
                        {
                            type: 'text',
                            height: 21,
                            width: 130,
                            widgetName: 'nameText',
                            x: hGap + labelWidth,
                            y: 16
                        }]
                }
            });
            return dirDialog;
        },

        _createLayerTableTree:function(){

            var entrySetting = {
                view: {
                    showIcon: false  //不显示每个节点前的ICON
                },
                data: {
                    simpleData: {

                    }
                },
                callback: {
                    onClick:function(e, treeId, treeNode){
                        CONTROL.showLayerEntry(treeNode);
                    }
                }
            };

            EDITOR.layerTableTree = new FS.TableTree({
                treeID: 'layerTableTree',
                renderEl: $(".layer_dir_panel_content"),
                setting: entrySetting,
                height: '100%',
                width: '100%',
                fit: true,
                alwaysShowTools: false,
                Nodes: DATA.layerTreeData,
                itemHoverCls: 'fs_reportmgr_tabletree_item_hover',
                tools: [
                    {
                        rootToolHide: false,
                        iconCls: 'fs-module-edit-white',
                        hoverCls: 'fs-module-edit-white-over',
                        onToolClick: function (e) {

                            var nodeID = $(this).data('nodeID');
                            var preEditNode = EDITOR.layerTableTree.getNodeById(nodeID);
                            EDITOR.layerTableTree.selectNode(preEditNode);

                            var editDirDialog = EDITOR.createJSONDialog(FR.i18nText("FR-Designer_Rename"), FR.i18nText('BI-FS-Module_Map_Editor_File_Name'), function(){
                                CONTROL.editLayerEntry(preEditNode, this.getWidgetByName("nameText").getValue());
                            }, DO_NOTHING);

                            editDirDialog.setVisible(true);

                            editDirDialog.getWidgetByName("nameText").setValue(preEditNode.name);

                            e.stopEvent();
                        }
                    },
                    {
                        rootToolHide: false,
                        iconCls: 'fs-module-delete-white',
                        hoverCls: 'fs-module-delete-white-over',
                        //删除按钮事件
                        onToolClick: function (e) {
                            var nodeID = $(this).data('nodeID');
                            var preDeleteNode = EDITOR.layerTableTree.getNodeById(nodeID);
                            EDITOR.layerTableTree.selectNode(preDeleteNode);

                            FR.Msg.confirm(FR.i18nText("FS-Frame-Delete_Data"), FR.i18nText("FS-Generic-Sure_To_Delete") + '?', function (result) {
                                result && CONTROL.deleteLayerEntry(preDeleteNode);
                            });
                            e.stopEvent();
                        }
                    }
                ]
            });
        },

        _createTableTree: function (mapType) {
            var zTreeData = mapType == GEO_MAP ? DATA.geographicTreeData : DATA.imageTreeData;
            var treeName = mapType == GEO_MAP ? 'geoDirTableTree' : 'imageDirTableTree';
            var dirClass = mapType == GEO_MAP ? '.geo_map_dir_panel_content' : '.image_map_dir_panel_content';

            var entrySetting = {
                view: {
                    showIcon: false  //不显示每个节点前的ICON
                },
                data: {
                    simpleData: {

                    }
                },
                callback: {
                    onClick:function(e, treeId, treeNode){
                        CONTROL.showEntry(mapType, treeNode);
                    }
                }
            };

            EDITOR[treeName] = new FS.TableTree({
                treeID: treeName,
                renderEl: $(dirClass),
                setting: entrySetting,
                height: '100%',
                width: '100%',
                fit: true,
                alwaysShowTools: false,
                Nodes: zTreeData,
                itemHoverCls: 'fs_reportmgr_tabletree_item_hover',
                tools: [
                    {
                        rootToolHide: true,
                        iconCls: 'fs-module-edit-white',
                        hoverCls: 'fs-module-edit-white-over',
                        onToolClick: function (e) {

                            var nodeID = $(this).data('nodeID');

                            var dirTree = mapType == GEO_MAP ? EDITOR.geoDirTableTree : EDITOR.imageDirTableTree;

                            var preEditNode = dirTree.getNodeById(nodeID);

                            dirTree.selectNode(preEditNode);

                            var editDirDialog = EDITOR.createJSONDialog(FR.i18nText("FR-Designer_Rename"), FR.i18nText('BI-FS-Module_Map_Editor_File_Name'), function(){
                                CONTROL.editEntry(mapType, preEditNode, this.getWidgetByName("nameText").getValue());
                            }, DO_NOTHING);

                            editDirDialog.setVisible(true);

                            editDirDialog.getWidgetByName("nameText").setValue(preEditNode.name);

                            e.stopEvent();
                        }
                    },
                    {
                        rootToolHide: true,
                        iconCls: 'fs-module-delete-white',
                        hoverCls: 'fs-module-delete-white-over',
                        //删除按钮事件
                        onToolClick: function (e) {
                            var nodeID = $(this).data('nodeID');
                            var dirTree = mapType == GEO_MAP ? EDITOR.geoDirTableTree : EDITOR.imageDirTableTree;
                            var preDeleteNode = dirTree.getNodeById(nodeID);

                            dirTree.selectNode(preDeleteNode);

                            FR.Msg.confirm(FR.i18nText("FS-Frame-Delete_Data"), FR.i18nText("FS-Generic-Sure_To_Delete") + '?', function (result) {
                                result && CONTROL.deleteEntry(mapType, preDeleteNode);
                            });
                            e.stopEvent();
                        }
                    }
                ]
            });

            var root = EDITOR[treeName].getTreeObj().getNodes()[0];

            if(root && root.children && root.children.length){
                root = root.children[0];
            }

            EDITOR[treeName].selectNode(root);
            CONTROL.showEntry(mapType, root);
        },

        _createMap:function(mapType){

            if(mapType == GEO_MAP){
                EDITOR.geoContext = GEOJSON_UI.initGeographicMapUI(d3.select('.geographic_map_container'));
            }else{
                EDITOR.imageContext = GEOJSON_UI.initImageMapUI(d3.select('.image_map_container'));
            }
        },

        _switchTo:function(layerType){

            var radioGroup = EDITOR.tileLayerContext.radioGroup;

            var selectedIndex = layerType == TILE_LAYER_VALUE ? 0 : 1;
            radioGroup.buttonArray.forEach(function(button, i){
                button.setSelectedWithoutEvent(selectedIndex == i);
            });

            EDITOR.tileLayerContext.tileLayerEl.html('');

            EDITOR.tileLayerContext.wmsLayerEl.html('');

            layerType == TILE_LAYER_VALUE ? EDITOR._switchToTileLayer() : EDITOR._switchToWmsLayer();
        },

        _removeAllLayers:function(){
            var map = EDITOR.tileLayerContext.map;

            map._tileLayer && map.removeLayer(map._tileLayer);
            map._attribution && map._attribution.removeFrom(map);
            map._wmsLayer && map.removeLayer(map._wmsLayer);

            map._tileLayer = map._attribution = map._wmsLayer = null;
        },

        _loadTileLayer:function(layerUrl, attribution){

            EDITOR._removeAllLayers();

            var map = EDITOR.tileLayerContext.map;

            map._tileLayer = (new L.TileLayer(layerUrl)).addTo(map);

            map._attribution = (new L.Control.Attribution()).addTo(map);

            map._attribution.addAttribution(attribution);
        },

        _loadWmlsLayer:function(layerUrl, layers){

            EDITOR._removeAllLayers();

            if(layerUrl){

                var wmsLayer = [];

                for(var layerName in layers){
                    layers[layerName] && wmsLayer.push(layerName);
                }

                var map = EDITOR.tileLayerContext.map;

                map._wmsLayer = (new L.TileLayer.WMS(layerUrl, {layers: wmsLayer.join(',')})).addTo(map);
            }

        },

        _switchToTileLayer:function(){
            var container = EDITOR.tileLayerContext.tileLayerEl;
            var selected = EDITOR.layerTableTree.getSelectedNodes()[0];

            if(!selected){
                return;
            }

            var labelEl = container.append("div").style({"position":"absolute", "top":"20px", "width":"343px", "height":"20px"});
            var textEl = container.append("div").style({"position":"absolute", "top":"50px", "width":"343px", "height":"30px"});

            var attrLabelEl = container.append("div").style({"position":"absolute", "top":"100px", "width":"343px", "height":"20px"});
            var attrLtextEl = container.append("div").style({"position":"absolute", "top":"130px", "width":"343px", "height":"30px"});

            new FR.Label({
                renderEl : labelEl.node(),
                width : 343, height : 20, value : "URL", textalign : "left", verticalcenter : true, fontsize : 16});

            var textEditor = new FR.TextEditor({renderEl:textEl.node(), width : 343, height : 30});
                textEditor.setText(selected.layerURL);

            new FR.Label({
                renderEl : attrLabelEl.node(),
                width : 343, height : 20, value : "Attribution", textalign : "left", verticalcenter : true, fontsize : 16});

            var attrTextEditor = new FR.TextEditor({renderEl:attrLtextEl.node(), width : 343, height : 30});
            attrTextEditor.setText(selected.isTileLayer ? selected.attribution : '');

            container.append("div").attr("class", "addButton").style("top","170px").style("left", "0px").text(FR.i18nText("BI-FS-Module_Map_Editor_Load_Layers")).on("click", function(){

                CONTROL.saveMapLayer({
                    name:selected.name,
                    isTileLayer:true,
                    layerURL:textEditor.getText(),
                    attribution:attrTextEditor.getText(),
                    layers:{}
                }, selected);

                EDITOR._loadTileLayer(textEditor.getText(), attrTextEditor.getText());

            });

            EDITOR._loadTileLayer(selected.layerURL, selected.attribution);
        },

        _layerToItems:function(layers){
            var items = [];
            for(var key in layers){
                items.push({
                    text:key,
                    value:layers[key]
                })
            }
            return items;
        },

        changeAllState: function (radioGroup, layers) {
            if (radioGroup.buttonArray) {
                for (var i = 0, len = radioGroup.buttonArray.length; i < len; i++) {
                    var button = radioGroup.buttonArray[i];
                    button.setSelectedWithoutEvent(layers[button.getText()]);
                }
            }
        },

        _switchToWmsLayer:function(){
            var container = EDITOR.tileLayerContext.wmsLayerEl;
            var selected = EDITOR.layerTableTree.getSelectedNodes()[0];

            if(!selected){
                return;
            }

            var labelEl = container.append("div").style({"position":"absolute", "top":"20px", "width":"343px", "height":"20px"});
            var textEl = container.append("div").style({"position":"absolute", "top":"50px", "width":"343px", "height":"30px"});

            var layerRadioEl = container.append("div").style({"position":"absolute", "top":"90px"});

            new FR.Label({
                renderEl : labelEl.node(),
                width : 343, height : 20, value : "URL", textalign : "left", verticalcenter : true, fontsize : 16});

            var textEditor = new FR.TextEditor({renderEl:textEl.node(), width : 343, height : 30});
            textEditor.setText(selected.isTileLayer ? '' : selected.layerURL);

            var items = EDITOR._layerToItems(selected.layers);
            var listeners = [{eventName: FR.Events.AFTEREDIT, action: function(){

                for(var layerName in selected.layers){
                    selected.layers[layerName] = false;
                }

                var textArray = this.getText();
                for(var i = textArray.length - 1; i >= 0; i--){
                    selected.layers[textArray[i]] = true;
                }

                CONTROL.saveMapLayer({
                    name:selected.name,
                    isTileLayer:false,
                    layerURL:selected.layerURL,
                    attribution:'',
                    layers:selected.layers
                }, selected);

                EDITOR._loadWmlsLayer(selected.layerURL, selected.layers);
            }}];

            var layerRadioGroup = new FR.CheckBoxGroup({renderEl : layerRadioEl.node(), width : 343, height : 20 * items.length, returnArray:true, columnsInRow : 1, items:items, listeners:listeners});
            EDITOR.changeAllState(layerRadioGroup, selected.layers);

            var loadButton = container.append("div").attr("class", "addButton").style("top", (20 * items.length + 100) + 'px').style("left", "0px").text(FR.i18nText("BI-FS-Module_Map_Editor_Load_Layers")).on("click", function(){

                loadButton.text(FR.i18nText("BI-FS-Module_Map_Editor_Loading_Layers"));

                CONTROL.updateWMSLayers({
                    name:selected.name,
                    layerURL:textEditor.getText()
                }, selected, function(){

                    layerRadioEl.html('');

                    var items = EDITOR._layerToItems(selected.layers);

                    layerRadioGroup = new FR.CheckBoxGroup({renderEl : layerRadioEl.node(), width : 343, height : 20 * items.length, returnArray:true, columnsInRow : 1, items:items, listeners:listeners});

                    EDITOR.changeAllState(layerRadioGroup, selected.layers);

                    loadButton.style("top", (20 * items.length + 100) + 'px').text(FR.i18nText("BI-FS-Module_Map_Editor_Load_Layers"));
                });
            });

            EDITOR._loadWmlsLayer(selected.isTileLayer ? '' : selected.layerURL, selected.layers);
        },

        _createLayerEditor:function(){
            var layerPane = d3.select(".map_layer_container");

            var radioEl = layerPane.append('div').style({"position":"absolute", "width":"343px", "height":"36px"});
            layerPane.append("div").style({"position":"absolute", "top":"37px", "width":"343px", "height":"0px", "border":"1px solid #c5c5c5"});

            var tileLayerEl = layerPane.append('div').style({"position":"absolute", "top":"38px"});
            var wmsLayerEl = layerPane.append('div').style({"position":"absolute", "top":"38px"});

            var mapContainer = layerPane.append('div').style({"position":"absolute", "left":"363px", "width":"542px", "height":"450px"});

            var radioGroup = new FR.RadioGroup({
                renderEl : radioEl.node(),
                width : 343,
                height : 36,
                columnsInRow : 0,
                assureSelect:true,
                items:[{text:'tile layer', value:TILE_LAYER_VALUE}, {text:'WMS', value:WMS_LAYER_VALUE}],
                listeners:[
                    {
                        eventName: FR.Events.AFTEREDIT,
                        action: function () {
                            EDITOR._switchTo(this.getValue());
                        }
                    }
                ]
            });

            var map = L.map(mapContainer.node()).setView([39.9, 116.3], 8);

            EDITOR.tileLayerContext = {
                radioGroup:radioGroup,
                tileLayerEl:tileLayerEl,
                wmsLayerEl:wmsLayerEl,
                map:map
            };

            var root = EDITOR.layerTableTree.getTreeObj().getNodes()[0];
            EDITOR.layerTableTree.selectNode(root);
            CONTROL.showLayerEntry(root);
        }
    };

    //缓存下来的各种数据
    var DATA = {
        geographicTreeData:[],
        imageTreeData:[],
        layerTreeData:[],

        initData: function (callback) {
            FR.ajax({
                type: 'POST',
                url: FR.servletURL + '?op=map_entry&cmd=getfolder',
                dataType: 'json',
                success: function (data, status) {
                    if (status === 'success') {
                        DATA.geographicTreeData = data.geographic;
                        DATA.imageTreeData = data.image;
                        DATA.layerTreeData = data.layer;
                        callback();
                    }
                }
            });
        }
    };

    var CONTROL = {

        getFullPath:function(maType, node){

            var rootPath = maType == GEO_MAP ? GEO_ROOT : IMAGE_ROOT;

            if(node.isRoot){
                return rootPath;
            }

            var path = node.name, isFolder = node.isFolder;
            while((node = node.getParentNode()) && !node.isRoot){
                path = node.name + '/' + path;
            }

            return rootPath + path + (isFolder ? "" : JSON_EXTENSION);
        },

        getNewPath:function(mapType, node, name){

            var isFolder = node.isFolder;

            while((node = node.getParentNode()) && !node.isRoot){
                name = node.name + '/' + name;
            }

            return (mapType == GEO_MAP ? GEO_ROOT : IMAGE_ROOT) + name + (isFolder ? "" : JSON_EXTENSION);
        },

        getMapContext:function(mapType){
            return mapType == GEO_MAP ? EDITOR.geoContext : EDITOR.imageContext;
        },

        getDirTableTree:function(mapType){
            return mapType == GEO_MAP ? EDITOR.geoDirTableTree : EDITOR.imageDirTableTree;
        },

        showEntry: function(mapType, treeNode){

            var context = CONTROL.getMapContext(mapType);

            if(EDITOR.lastTreeNode != treeNode){

                var lastTreeNode = EDITOR.lastTreeNode;

                EDITOR.lastTreeNode = treeNode;

                if(context.isDirty() && lastTreeNode){

                    if(mapType == GEO_MAP){
                        EDITOR.geoContext.updateWithListTable();
                    }

                    var context = CONTROL.getMapContext(mapType);
                    var jsonData = context.getMapJSONData();

                    var dialog = EDITOR.createConfirmEditDialog(function(){

                        CONTROL._saveMap(lastTreeNode, mapType, jsonData);

                        afterConfirm(treeNode, mapType);

                    }, function(){

                        afterConfirm(treeNode, mapType);

                    });

                    dialog.setVisible(true);

                    return ;
                }
            }

            afterConfirm(treeNode, mapType);

            function afterConfirm(showNode, type){

                if(!showNode.isValid || showNode.isRoot){
                    context.clear();
                    return ;
                }

                FR.ajax({
                    type: 'POST',
                    url: FR.servletURL + '?op=map_entry&cmd=get_json',
                    dataType: 'json',
                    data:{
                        nodePath:CONTROL.getFullPath(type, showNode)
                    },
                    success: function (data, status) {
                        if(status === 'success'){
                            context.setJSONData(data);
                        }
                    }
                });
            }
        },

        deleteEntry:function(mapType, node){
            FR.ajax({
                type: 'POST',
                url: FR.servletURL + '?op=map_entry&cmd=delete_entry',
                data:{
                    dirPath:CONTROL.getFullPath(mapType, node)
                },
                success: function (data, status) {
                    if (status === 'success') {
                        var dirTableTree = CONTROL.getDirTableTree(mapType);

                        dirTableTree.removeSelectedNodes();

                        var root = dirTableTree.getTreeObj().getNodes()[0];

                        dirTableTree.selectNode(root);

                        CONTROL.showEntry(mapType, root)
                    }
                }
            });
        },

        editEntry:function(mapType, node, name){
            FR.ajax({
                type: 'POST',
                url: FR.servletURL + '?op=map_entry&cmd=edit_entry',
                data:{
                    oldPath:CONTROL.getFullPath(mapType, node),
                    newPath:CONTROL.getNewPath(mapType, node, name)
                },
                success: function (data, status) {
                    if (status === 'success') {
                        node.name = name;
                        CONTROL.getDirTableTree(mapType).updateNode(node);
                    }
                }
            });
        },

        addEntry: function (mapType, addedEntryName, selectedNode) {
            FR.ajax({
                type: 'POST',
                url: FR.servletURL + '?op=map_entry&cmd=add_entry',
                dataType: 'json',
                data:{
                    parentPath:CONTROL.getFullPath(mapType, selectedNode),
                    name:addedEntryName,
                    isImageMap:mapType == IMAGE_MAP
                },
                success: function (data, status) {
                    if (status === 'success') {

                        CONTROL.getMapContext(mapType).initJSONState();

                        selectedNode.isFolder = true;

                        var added = CONTROL.getDirTableTree(mapType).addSingleNode(selectedNode, data, selectedNode.open);

                        CONTROL.getDirTableTree(mapType).selectNode(added);
                    }
                }
            });
        },

        saveImageBackground:function(formNode, removeInputFunc){
            var selected = EDITOR.imageDirTableTree.getSelectedNodes()[0];
            var path = CONTROL.getFullPath(IMAGE_MAP, selected);

            var $form = $(formNode);

            $('input:file', $form).attr("name", path);

            var options = {
                type:'json',
                url: FR.servletURL + '?op=map_entry&cmd=save_background_image'
            };

            var callback = function (data, status){
                if (status === 'success') {

                    removeInputFunc();

                    var context = CONTROL.getMapContext(IMAGE_MAP);

                    context.setJSONData(['', '', FR.jsonDecode(FR.htmlDecode(data.responseText))]);
                }
            };

            FR.domFormSubmit($form, options, callback);
        },

        importExcelData:function(formNode, excelPreview, callback){

            var selected = EDITOR.geoDirTableTree.getSelectedNodes()[0];

            var path = CONTROL.getFullPath(IMAGE_MAP, selected);

            var $form = $(formNode);

            $('input:file', $form).attr("name", path);

            var options = {
                type:'json',
                url: FR.servletURL + '?op=map_entry&cmd=import_excel_data'
            };

            FR.domFormSubmit($form, options, function(data, status){
                if (status === 'success') {
                    excelPreview(FR.jsonDecode(data.responseText));
                }

                callback();
            });
        },

        saveGeographicMap:function(){

            //确保list内容和地图上标记点的内容一致
            EDITOR.geoContext.updateWithListTable();

            var selected = EDITOR.geoDirTableTree.getSelectedNodes()[0];
            var jsonData = EDITOR.geoContext.getMapJSONData();

            CONTROL._saveMap(selected, GEO_MAP, jsonData, true);
        },

        saveImageMap:function(){

            var selected = EDITOR.imageDirTableTree.getSelectedNodes()[0];
            var jsonData = EDITOR.imageContext.getMapJSONData();

            CONTROL._saveMap(selected, IMAGE_MAP, jsonData, true);
        },

        _saveMap:function(selected, mapType, jsonData, showAlert){
            if(selected.isRoot){
                return;
            }
            FR.ajax({
                type: 'POST',
                url: FR.servletURL + '?op=map_entry&cmd=save_json_data',
                data:{
                    dirPath:CONTROL.getFullPath(mapType, selected),
                    jsonData:FR.jsonEncode(jsonData)
                },
                success: function (data, status) {

                    CONTROL.getMapContext(mapType).cleanContext();

                    showAlert && FR.Msg.alert("", status === 'success' ? FR.i18nText("BI-FS-Module_Map_Editor_Save_Success") : FR.i18nText("BI-FS-Module_Map_Editor_Save_Error"));
                }
            });
        },

        cancelGeographicMap:function(){
            CONTROL._cancelMap(GEO_MAP);
        },

        cancelImageMap:function(){
            CONTROL._cancelMap(IMAGE_MAP);
        },

        _cancelMap:function(mapType){

            var dirTableTree = CONTROL.getDirTableTree(mapType);

            var selected = dirTableTree.getSelectedNodes()[0];

            CONTROL.showEntry(mapType, selected);

        },

        addListTableItem:function(item){

            if(EDITOR.tabPane.getSelectedIndex() === 0){

                var listTable = EDITOR.geoContext.listTable;

                listTable.add(item);
            }

        },

        getItemConfig:function(feature){

            return {
                name:feature.properties.name || '',
                lng:feature.geometry.coordinates[0],
                lat:feature.geometry.coordinates[1]
            };
        },

        removeListTableItem:function(feature){

            if(EDITOR.tabPane.getSelectedIndex() === 0){

                var listTable = EDITOR.geoContext.listTable;
                listTable.removeItem(CONTROL.getItemConfig(feature));

            }
        },

        updateListTableItem:function(feature, name){
            if(EDITOR.tabPane.getSelectedIndex() === 0){

                var listTable = EDITOR.geoContext.listTable;

                var config = CONTROL.getItemConfig(feature);
                var item = listTable.getItem(config);

                if(item){
                    config.name = name;
                    listTable.templater.set(item, config);
                    item._values = config;
                }

            }
        },

        //层级的一些操作
        showLayerEntry:function(node){
            if(node){
                EDITOR._switchTo(node.isTileLayer ? TILE_LAYER_VALUE : WMS_LAYER_VALUE);
            }
        },

        addLayerEntry:function(name){
            FR.ajax({
                type: 'POST',
                url: FR.servletURL + '?op=map_entry&cmd=add_layer_entry',
                dataType: 'json',
                data:{
                    name:name
                },
                success: function (data, status) {
                    if (status === 'success') {
                        var added = EDITOR.layerTableTree.addSingleNode(null, data);

                        EDITOR.layerTableTree.selectNode(added);

                        CONTROL.showLayerEntry(added);
                    }
                }
            });

        },

        deleteLayerEntry:function(node){

            FR.ajax({
                type: 'POST',
                url: FR.servletURL + '?op=map_entry&cmd=delete_layer_entry',
                data:{
                    name:node.name
                },
                success: function (data, status) {
                    if (status === 'success') {

                        EDITOR.layerTableTree.removeSelectedNodes();

                        var root = EDITOR.layerTableTree.getTreeObj().getNodes()[0];

                        EDITOR.layerTableTree.selectNode(root);

                        CONTROL.showLayerEntry(root);
                    }
                }
            });

        },

        editLayerEntry:function(node, name){
            FR.ajax({
                type: 'POST',
                url: FR.servletURL + '?op=map_entry&cmd=edit_layer_entry',
                data:{
                    oldName:node.name,
                    newName:name
                },
                success: function (data, status) {
                    if (status === 'success') {
                        node.name = name;
                        CONTROL.layerTableTree.updateNode(node);
                    }
                }
            });
        },

        _updateTileLayerNode:function(config, node){
            node.name = config.name;
            node.isTileLayer = config.isTileLayer;
            node.attribution = config.attribution;
            node.layerURL = config.layerURL;
            node.layers = config.layers;
        },

        saveMapLayer:function(config, node){

            FR.ajax({
                type: 'POST',
                url: FR.servletURL + '?op=map_entry&cmd=save_layer_entry',
                data:config,
                dataType: 'json',
                success: function (data, status) {
                    if(status === 'success'){
                        CONTROL._updateTileLayerNode(config, node);
                    }
                }
            });
        },

        updateWMSLayers:function(paras, node, callback){

            FR.ajax({
                type: 'POST',
                url: FR.servletURL + '?op=map_entry&cmd=update_wms_layers',
                data:paras,
                dataType: 'json',
                success: function (data, status) {
                    if(status === 'success'){

                        CONTROL._updateTileLayerNode(data, node);

                        EDITOR._loadWmlsLayer(node.layerURL, node.layers);

                        callback();
                    }
                }
            });

        }
    };

    EDITOR.init($("#geojsonio"));

    window.MAP_EDITOR_CONTROL = CONTROL;

})();