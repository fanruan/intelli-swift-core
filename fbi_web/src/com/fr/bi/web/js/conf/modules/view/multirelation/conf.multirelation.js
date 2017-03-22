/**
 *
 */
BIConf.MultiRelationView = BI.inherit(BI.View, {
    _constant: {
        NONE_MULTI_PATH: 0,
        HAS_MULTI_PATH: 1
    },
    _defaultConfig: function () {
        return BI.extend(BIConf.MultiRelationView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-multi-relation-view"
        })
    },

    _init: function () {
        BIConf.MultiRelationView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;
        this.tab = BI.createWidget({
            direction: "custom",
            type: "bi.tab",
            cardCreator: BI.bind(this._createTabs, this)
        });

        this.mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: self.element,
            text: BI.i18nText("BI-Basic_Loading")
        });

        this.multiRelation = BI.createWidget({
            type: "bi.multi_relation_grid"
        });

        this.multiRelation.on(BI.MultiRelationGrid.EVENT_CHANGE, function () {
            var availableRelation = self.multiRelation.getValue();
            self.model.set({
                availableRelations: availableRelation
            })
        });

        this.searchMultiRelation = BI.createWidget({
            type: "bi.multi_relation_grid"
        });


        this.searcher = BI.createWidget({
            type: "bi.searcher",
            adapter: this.multiRelation,
            chooseType: BI.ButtonGroup.CHOOSE_TYPE_MULTI,
            width: 230,
            isAutoSync: true,
            isAutoSearch: false,
            onSearch: function (op, callback) {
                var items = self.multiRelation.getItems();
                var availableItems = self.multiRelation.getValue();
                var result = [];
                var selectedResult = [];
                BI.each(items, function (i, item) {
                    var pathResult = [];
                    BI.each(item, function (idx, pathItem) {
                        BI.find(pathItem, function (index, relation) {
                            var fKey = relation.foreignKey;
                            var foreignId = fKey.field_id;
                            var fieldName = fKey[BICst.JSON_KEYS.FIELD_TRAN_NAME] || fKey.field_name;
                            var tableName = fKey[BICst.JSON_KEYS.TABLE_TRAN_NAME] || fKey.table_name;
                            var result = BI.Func.getSearchResult([fieldName], op.keyword);
                            if (BI.size(result.finded) >= 1 || BI.size(result.matched) >= 1) {
                                pathResult.push(BI.deepClone(pathItem));
                                if (BI.deepContains(availableItems, pathItem)) {
                                    selectedResult.push(pathItem);
                                }
                                return true
                            }
                            result = BI.Func.getSearchResult([tableName], op.keyword);
                            if (BI.size(result.finded) >= 1 || BI.size(result.matched) >= 1) {
                                pathResult.push(BI.deepClone(pathItem));
                                if (BI.deepContains(availableItems, pathItem)) {
                                    selectedResult.push(pathItem);
                                }
                                return true
                            }
                            if (index === 0) {
                                var pKey = relations.primaryKey;
                                var primaryId = pKey.field_id;
                                fieldName = pKey[BICst.JSON_KEYS.FIELD_TRAN_NAME] || pKey.field_name;
                                tableName = pKey[BICst.JSON_KEYS.TABLE_TRAN_NAME] || pKey.table_name;
                                result = BI.Func.getSearchResult([fieldName], op.keyword);
                                if (BI.size(result.finded) >= 1 || BI.size(result.matched) >= 1) {
                                    pathResult.push(BI.deepClone(pathItem));
                                    if (BI.deepContains(availableItems, pathItem)) {
                                        selectedResult.push(pathItem);
                                    }
                                    return true
                                }
                                result = BI.Func.getSearchResult([tableName], op.keyword);
                                if (BI.size(result.finded) >= 1 || BI.size(result.matched) >= 1) {
                                    if (BI.deepContains(availableItems, pathItem)) {
                                        selectedResult.push(pathItem);
                                    }
                                    pathResult.push(BI.deepClone(pathItem));
                                    return true
                                }

                            }
                        })
                    });
                    if (BI.isNotEmptyArray(pathResult)) {
                        result.push(pathResult);
                    }
                });
                callback(result, selectedResult, op.keyword)
            },
            popup: {
                type: "bi.multi_relation_searcher_view",
                searcher: this.searchMultiRelation
            }
        });
        self.searcher.on(BI.Searcher.EVENT_STOP, function () {
            self.multiRelation.populate();
        });

        this.cubeLabel = BI.createWidget({
            type: "bi.label",
            cls: "multi-relation-cube-label",
            value: "Cube Time"
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: this.tab,
                top: 0,
                bottom: 0,
                left: 0,
                right: 0
            }, {
                el: this.searcher,
                top: -45,
                right: 20
            }
            ]
        })
    },

    _createTabs: function (v) {
        var c = this._constant, self = this;
        switch (v) {
            case c.NONE_MULTI_PATH:
                return BI.createWidget({
                    type: "bi.absolute",
                    items: [{
                        el: {
                            type: "bi.label",
                            cls: "tip-label",
                            value: BI.i18nText("BI-No_Multi_Path")
                        },
                        top: 30,
                        left: 0,
                        right: 0
                    }]
                });
            case c.HAS_MULTI_PATH:
                return BI.createWidget({
                    type: "bi.absolute",
                    items: [
                        {
                            el: {
                                type: "bi.left",
                                items: [{
                                    el: {
                                        type: "bi.label",
                                        cls: "multi-path-label",
                                        value: BI.i18nText("BI-Multi_Path_Use_Check_Path") + ":"
                                    }
                                }, {
                                    el: self.cubeLabel
                                }],
                                hgap: 20,
                                vgap: 5
                            },
                            top: 10,
                            left: 10,
                            right: 10
                        }, {
                            el: this.multiRelation,
                            top: 30,
                            bottom: 0,
                            left: 0,
                            right: 0
                        }
                    ]
                })
        }
    },

    change: function (changed) {
        var self = this;
        if (BI.has(changed, "availableRelations")) {
            self.model.update({
                data: {
                    relation: self.multiRelation.getCurrentRelation()
                },
                complete: function () {
                    self.refresh();
                }
            });
        }
    },

    local: function () {
    },

    load: function () {
        var self = this, c = this._constant;
        var needGenerateCube = self.model.get("needGenerateCube");
        var cubeEnd = self.model.get("cubeEnd");
        var relations = self.model.get("relations");
        var availableRelations = self.model.get("availableRelations");
        relations = BI.sortBy(relations, function (i, item) {
            return BI.lastObject(item[0]).foreignKey[BICst.JSON_KEYS.TABLE_TRAN_NAME];
        });
        self.multiRelation.setRelations(relations);
        self.multiRelation.setValue(availableRelations);
        self.multiRelation.populate();
        self.cubeLabel.setValue(BI.i18nText("BI-Multi_Path_Use_Cur_Cube_Version") + ": " + new Date(cubeEnd).print("%Y-%X-%d,%H:%M:%S"));
        BI.size(relations) > 0 ? this.tab.setSelect(c.HAS_MULTI_PATH) : this.tab.setSelect(c.NONE_MULTI_PATH);
    },

    refresh: function () {
        var self = this;
        this.readData(true, {
            complete: function () {
                self.mask.destroy();
            }
        });
    }
});