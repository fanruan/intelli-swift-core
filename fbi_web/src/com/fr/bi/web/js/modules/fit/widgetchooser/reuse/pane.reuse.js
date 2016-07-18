/**
 * 组件复用
 *
 * Created by GUY on 2015/9/16.
 * @class BI.ReusePane
 * @extends BI.Widget
 */
BI.ReusePane = BI.inherit(BI.Widget, {

    constants: {
        CONTROL_TYPE: [BICst.WIDGET.QUERY, BICst.WIDGET.RESET]
    },

    _defaultConfig: function () {
        return BI.extend(BI.ReusePane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-reuse-pane",
            itemsCreator: BI.emptyFn
        });
    },

    _init: function () {
        BI.ReusePane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.items = [];

        this.searcher = BI.createWidget({
            type: "bi.simple_select_data_searcher",
            element: this.element,
            el: {
                el: {
                    behaviors: {
                        "redmark": function(val, ob){
                            return true;
                        }
                    }
                }
            },
            popup: {
                segment: {
                    items: []
                }
            },
            itemsCreator: function (op, populate) {
                if (BI.isKey(op.keyword)) {
                    var result = self._getSearchResult(op.keyword);
                    populate(result.finded, result.matched);
                    return;
                }
                if (!op.node) {//根节点
                    self._getInitialFoldersAndTemplates(populate);
                    return;
                }
                if (BI.isNotNull(op.node.isParent)) {
                    if(op.node.nodeType === BI.ReusePane.FOLDER){
                        populate(self._findChildItemsFromItems(op.node.id, op.node.layer + 1));
                        return;
                    }
                    if(op.node.nodeType === BI.ReusePane.TEMPLATE){
                        self._getWidgets(op.node.id, op.node.layer + 1, populate);
                    }
                }
            }
        });
        this.searcher.populate();
    },

    _getSearchResult: function (keyword) {
        var self = this;
        var result = BI.filter(this.items, function(idx, item){
            return BI.has(item, "buildUrl");
        });
        result = BI.map(result, function(idx, item){
            return {
                id: item.id,
                pId: item.pId,
                buildUrl: item.buildUrl,
                type: "bi.multilayer_icon_arrow_node",
                iconCls: "file-font",
                text: item.text,
                title: item.text,
                isParent: true,
                value: item.id,
                layer: 0,
                nodeType: BI.ReusePane.TEMPLATE
            };
        });
        var map = [];
        var searchResult = [];
        var maxLevel = 0;
        result = BI.Func.getSearchResult(result, keyword);
        BI.each(result.finded, function (j, finded) {
            if (!map[finded.pId]) {
                var tmpLevel = self._getAllParentItems(searchResult, 0, finded.pId);
                if(maxLevel <  tmpLevel){
                    maxLevel = tmpLevel;
                }
                map[finded.pId] = true;
            }
        });
        BI.each(searchResult, function(idx, item){
            item.layer = maxLevel - item.layer;
        });
        BI.each(result.finded, function(idx, item){
            var tar = BI.find(searchResult, function (idx, it) {
                return item.pId === it.id
            });
            item.layer = BI.isNull(tar) ? 0 : tar.layer + 1;
        });
        searchResult = searchResult.concat(result.finded);
        return {
            finded: searchResult,
            matched: result.matched
        };
    },

    _getAllParentItems: function (result, layer, pId) {
        if(pId === "-1"){
            return layer;
        }
        var item = BI.find(this.items, function(idx, item){
            return item.id === pId + "";
        });
        if(BI.isNotNull(item)){
            var r = BI.find(result, function (idx, it) {
                return it.id == item.id;
            });
            if(BI.isNull(r)){
                result.push({
                    id: item.id,
                    pId: item.pId,
                    type: "bi.multilayer_icon_arrow_node",
                    iconCls: BI.has(item, "buildUrl") ? "file-font" : "folder-font",
                    text: item.text,
                    title: item.text,
                    isParent: true,
                    open: true,
                    value: item.id,
                    layer: layer + 1,
                    nodeType: BI.has(item, "buildUrl") ? BI.ReusePane.TEMPLATE : BI.ReusePane.FOLDER
                });
            }
        }
        return this._getAllParentItems(result, layer + 1, item.pId);
    },

    _findChildItemsFromItems: function(pId, layer){
        var items = BI.filter(this.items, function(idx, items){
            return items.pId === pId + "";
        });
        items = BI.map(items, function (idx, item) {
            return {
                id: item.id,
                pId: item.pId,
                type: "bi.multilayer_icon_arrow_node",
                iconCls: BI.has(item, "buildUrl") ? "file-font" : "folder-font",
                text: item.text,
                title: item.text,
                isParent: true,
                value: item.id,
                layer: layer,
                nodeType: BI.has(item, "buildUrl") ? BI.ReusePane.TEMPLATE : BI.ReusePane.FOLDER
            };
        });
        return items;
    },

    _getWidgets: function (id, layer, callback) {
        var self = this, o = this.options;
        BI.Utils.getWidgetsByTemplateId(id, function(data){
            var result = [];
            BI.map(data, function(wId, widget){
                if(!BI.contains(self.constants.CONTROL_TYPE, widget.type)){
                    result.push({
                        id: wId,
                        pId: id,
                        isParent: false,
                        layer: layer,
                        nodeType: BI.ReusePane.WIDGET,

                        type: "bi.drag_widget_item",
                        widget: widget,
                        drag: o.drag,
                        stop: o.stop,
                        helper: o.helper
                    });
                }
            });
            callback(result);
        });
    },

    /**
     * 第一次初始化时获取第一层文件夹和模板
     * @param callback
     * @private
     */
    _getInitialFoldersAndTemplates: function (callback) {
        var self = this, o = this.options;
        if (BI.isEmptyArray(this.items)) {
            BI.Utils.getAllTemplates(function(items){
                var currentTemplateId = BI.Utils.getCurrentTemplateId();
                self.items = BI.filter(items, function(idx, item){
                   return item.id != currentTemplateId;
                });
                callback(self._findChildItemsFromItems(-1, 0));
            });
        } else {
            callback(self._findChildItemsFromItems(-1, 0));
        }
    },

    populate: function () {
        this.items = [];
        this.searcher.populate.apply(this.searcher, arguments)
    },

    setValue: function (v) {

    },

    getValue: function () {

    }
});

BI.extend(BI.ReusePane, {
    FOLDER: 0,
    TEMPLATE: 1,
    WIDGET: 2
});
BI.ReusePane.EVENT_CHANGE = "ReusePane.EVENT_CHANGE";
$.shortcut('bi.reuse_pane', BI.ReusePane);