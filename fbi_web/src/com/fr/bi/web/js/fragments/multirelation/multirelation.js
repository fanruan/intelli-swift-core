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

        var self = this, o = this.options, c = this._constant;
        this.value = [];
        this.allRelations = [];
        // this.notSelectedValues = [];
        this.isTitleMap = {};
        this.keyWord = "";

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
        var self = this, o = this.options;
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
        // this.notSelectedValues.push(relation);
    },

    _addValue: function (relation) {
        // var notSelectedValues = [];
        // BI.each(this.notSelectedValues, function (i, rel) {
        //     if (!BI.isEqual(rel, relation)) {
        //         notSelectedValues.push(rel)
        //     }
        // });
        // this.notSelectedValues = notSelectedValues;
        this.value.push(relation)
    },

    _clickRelationItem: function (relation) {
        if (this._isSelect(relation)) {
            this._deleteValue(relation);
        } else {
            this._addValue(relation);
        }
    },

    _setAllRelation: function () {
        var relations = this.options.relations;
        var allRelation = [];
        BI.each(relations, function (i, rels) {
            BI.each(rels, function (i, relation) {
                allRelation.push(relation);
            })
        });
        this.allRelations = allRelation;
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

    // setNotSelectedValue: function (v) {
    //     this.notSelectedValues = v;
    // },

    setRelations: function (relations) {
        this.options.relations = relations;
        this._setAllRelation();
    },

    getValue: function () {
        return this.value;
    },

    getItems: function () {
        return this.attr("relations");
    },

    getNotSelectedValue: function () {//这里卡，复杂度为O(n^2),1000条关联耗时1秒多
        var self = this;
        var notSelectValue = [];
        BI.each(this.allRelations, function (i, relation) {
            if (!self._isSelect(relation)) {
                notSelectValue.push(relation)
            }
        });
        return notSelectValue;
    },

    restore: function () {
        this.grid.restore();
    },

    doBehavior: function () {
    },

    populate: function () {
        var self = this, c = this._constant;
        BI.nextTick(function () {//每次populate的时候可以自适应宽高，但还是无法实现窗口变化的时候自动调整。
            var height = self._getHeight();
            var width = self._getWidth();
            self.grid.setHeight(height);
            self.grid.setWidth(width);
            self.grid.setEstimatedColumnSize(width);
            var items = self._getGridItems();
            self.grid.populate(items);
        });
    }
});
BI.MultiRelationGrid.EVENT_CHANGE = "MultiRelationGrid.EVENT_CHANGE";
$.shortcut('bi.multi_relation_grid', BI.MultiRelationGrid);