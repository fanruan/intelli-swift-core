/**
 * Created by zcf on 2017/2/9.
 */
BI.MultiRelationGrid = BI.inherit(BI.Single, {

    _constant: {
        LGAP_AND_RGAP: 20,
        BGAP_AND_TGAP: 20,
        TITLE_HEIGHT: 40,
        ITEM_HEIGHT: 30,
        TOP: 10,
        LEFT: 10,
        RIGHT: 10,
        BOTTOM: 10
    },

    _defaultConfig: function () {
        return BI.extend(BI.MultiRelationGrid.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-multi-relation",
            relations: []
        });
    },

    _init: function () {
        BI.MultiRelationGrid.superclass._init.apply(this, arguments);

        var self = this, c = this._constant;
        this.value = [];
        this.isTitleMap = {};
        this.keyWord = "";
        this.currentRelation = [];

        this.grid = BI.createWidget({
            type: "bi.grid_view",
            overflowX: false,
            items: this._getGridItems(),
            rowHeightGetter: function (i) {
                if (self.isTitleMap[i]) {
                    return c.TITLE_HEIGHT;
                }
                return c.ITEM_HEIGHT;
            },
            columnWidthGetter: function () {
                return self._getWidth();
            }
        });

        BI.ResizeDetector.addResizeListener(this, function () {
            self.resize();
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.grid,
                top: c.TOP,
                left: c.LEFT,
                right: c.RIGHT,
                bottom: c.BOTTOM
            }]
        })
    },

    _getGridItems: function () {
        var self = this;
        var relations = this.options.relations;
        var items = [];
        this.isTitleMap = {};
        var heightInt = 0;

        BI.each(relations, function (i, relationsArray) {
            var titleLabel = {
                type: "bi.multi_relation_title",
                relationsArray: relationsArray
            };
            items.push([titleLabel]);
            self.isTitleMap[heightInt] = true;
            heightInt++;
            BI.each(relationsArray, function (i, relation) {
                var relationItems = {
                    type: "bi.multi_relation_item",
                    relations: relation,
                    isSelect: function () {
                        return self._isSelect(relation);
                    },
                    onEventChange: function (relation, type, value, ob) {
                        self.currentRelation = relation;
                        self._clickRelationItem(relation);
                        self.fireEvent(BI.Controller.EVENT_CHANGE, type, value, ob);
                        self.fireEvent(BI.MultiRelationGrid.EVENT_CHANGE);
                    },
                    getKeyWord: function () {
                        return self.keyWord;
                    }
                };
                items.push([relationItems]);
                self.isTitleMap[heightInt] = false;
                heightInt++;
            });
        });
        return items;
    },

    _isSelect: function (relation) {
        var value = this.value;
        return BI.some(value, function (i, v) {
            return BI.isEqual(v, relation);
        })
    },

    _deleteValue: function (relation) {
        var value = [];
        BI.each(this.value, function (i, rel) {
            if (!BI.isEqual(rel, relation)) {
                value.push(rel);
            }
        });
        this.value = value;
    },

    _addValue: function (relation) {
        this.value.push(relation)
    },

    _clickRelationItem: function (relation) {
        if (this._isSelect(relation)) {
            this._deleteValue(relation);
        } else {
            this._addValue(relation);
        }
    },

    _getWidth: function () {
        var c = this._constant;
        return this.element.width() - c.LGAP_AND_RGAP;
    },

    _getHeight: function () {
        var c = this._constant;
        return this.element.height() - c.BGAP_AND_TGAP;
    },

    setKeyWord: function (keyWord) {
        this.keyWord = keyWord;
    },

    setValue: function (v) {
        this.value = v;
    },

    setRelations: function (relations) {
        this.options.relations = relations;
    },

    getValue: function () {
        return this.value;
    },

    getItems: function () {
        return this.attr("relations");
    },

    getCurrentRelation: function () {
        return this.currentRelation;
    },

    restore: function () {
        this.grid.restore();
    },

    doBehavior: function () {
    },

    resize: function () {
        var self = this;
        BI.nextTick(function () {
            var height = self._getHeight();
            var width = self._getWidth();
            self.grid.setHeight(height);
            self.grid.setWidth(width);
            self.grid.setEstimatedColumnSize(width);
            self.grid.populate();
        })
    },

    populate: function () {
        var items = this._getGridItems();
        this.grid.populate(items);
    }
});
BI.MultiRelationGrid.EVENT_CHANGE = "MultiRelationGrid.EVENT_CHANGE";
$.shortcut('bi.multi_relation_grid', BI.MultiRelationGrid);