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
            text: BI.i18nText("BI-Loading")
        });

        this.multiRelation = BI.createWidget({
            type: "bi.multi_relation"
        });

        this.multiRelation.on(BI.MultiRelation.EVENT_CHANGE, function () {
            var disabledRelation = self.multiRelation.getNotSelectedValue();
            var availableRelation = self.multiRelation.getValue();
            self.cubeLabel.setValue(BI.i18nText("BI-Generate_Cube_First"));
            self.model.set({
                disabledRelations: disabledRelation,
                availableRelations: availableRelation
            })
        });

        this.searchMultiRelation = BI.createWidget({
            type: "bi.multi_relation"
        });


        this.searcher = BI.createWidget({
            type: "bi.searcher",
            adapter: this.multiRelation,
            searcher: this.searchMultiRelation,
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
                            var foreignId = relation.foreignKey.field_id;
                            var fieldName = BI.Utils.getFieldNameByFieldId4Conf(foreignId);
                            var tableName = BI.Utils.getTableNameByFieldId4Conf(foreignId);
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
                                var primaryId = relation.primaryKey.field_id;
                                fieldName = BI.Utils.getFieldNameByFieldId4Conf(primaryId);
                                tableName = BI.Utils.getTableNameByFieldId4Conf(primaryId);
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
                                type: "bi.vertical_adapt",
                                items: [{
                                    el: {
                                        type: "bi.label",
                                        cls: "multi-path-label",
                                        value: BI.i18nText("BI-Multi_Path_Use_Check_Path") + ":"
                                    }
                                }, {
                                    el: self.cubeLabel
                                }],
                                height: 30,
                                hgap: 20
                            },
                            top: 0,
                            left: 0,
                            right: 0
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
                    disabledRelations: self.model.get("disabledRelations"),
                    availableRelations: self.model.get("availableRelations")
                }
            });
        }
    },

    local: function () {
    },

    load: function () {
        var self = this, c = this._constant;
        var cubeEnd = self.model.get("cubeEnd");
        var relations = self.model.get("relations");
        var availableRelations = self.model.get("availableRelations");
        self.mask.destroy();
        self.multiRelation.populate(relations, availableRelations);
        self.cubeLabel.setValue(BI.i18nText("BI-Multi_Path_Use_Cur_Cube_Version") + ": " + cubeEnd);
        BI.size(relations) > 0 ? this.tab.setSelect(c.HAS_MULTI_PATH) : this.tab.setSelect(c.NONE_MULTI_PATH);
    },

    refresh: function () {
        this.readData(true);
    }
});