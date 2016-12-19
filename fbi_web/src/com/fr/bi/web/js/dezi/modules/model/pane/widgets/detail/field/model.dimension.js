/**
 * Created by GUY on 2015/7/3.
 */
BIDezi.DimensionModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.DimensionModel.superclass._defaultConfig.apply(this, arguments), {
            _src: {},
            dimension_map: {},
            settings: {},
            sort: {},
            group: {},
            type: "",
            name: "",
            used: true
        });
    },

    _init: function () {
        BIDezi.DimensionModel.superclass._init.apply(this, arguments);

        //这边做一个map, 用作日期类型分组改变时，将相应过滤条件的类型切换掉
        this.YMDChangeMap = {};
        this.otherChangeMap = {};

        this.YMDChangeMap[BICst.DIMENSION_FILTER_DATE.BELONG_VALUE] = BICst.DIMENSION_FILTER_STRING.BELONG_VALUE;
        this.YMDChangeMap[BICst.DIMENSION_FILTER_DATE.NOT_BELONG_VALUE] = BICst.DIMENSION_FILTER_STRING.NOT_BELONG_VALUE;
        this.YMDChangeMap[BICst.DIMENSION_FILTER_DATE.BEGIN_WITH] = BICst.DIMENSION_FILTER_STRING.BEGIN_WITH;
        this.YMDChangeMap[BICst.DIMENSION_FILTER_DATE.END_WITH] = BICst.DIMENSION_FILTER_STRING.END_WITH;
        this.YMDChangeMap[BICst.DIMENSION_FILTER_DATE.CONTAIN] = BICst.DIMENSION_FILTER_STRING.CONTAIN;
        this.YMDChangeMap[BICst.DIMENSION_FILTER_DATE.NOT_CONTAIN] = BICst.DIMENSION_FILTER_STRING.NOT_CONTAIN;

        this.otherChangeMap[BICst.DIMENSION_FILTER_STRING.BELONG_VALUE] = BICst.DIMENSION_FILTER_DATE.BELONG_VALUE;
        this.otherChangeMap[BICst.DIMENSION_FILTER_STRING.NOT_BELONG_VALUE] = BICst.DIMENSION_FILTER_DATE.NOT_BELONG_VALUE;
        this.otherChangeMap[BICst.DIMENSION_FILTER_STRING.BEGIN_WITH] = BICst.DIMENSION_FILTER_DATE.BEGIN_WITH;
        this.otherChangeMap[BICst.DIMENSION_FILTER_STRING.END_WITH] = BICst.DIMENSION_FILTER_DATE.END_WITH;
        this.otherChangeMap[BICst.DIMENSION_FILTER_STRING.CONTAIN] = BICst.DIMENSION_FILTER_DATE.CONTAIN;
        this.otherChangeMap[BICst.DIMENSION_FILTER_STRING.NOT_CONTAIN] = BICst.DIMENSION_FILTER_DATE.NOT_CONTAIN;
    },

    refresh: function () {

    },


    change: function (change, prev) {
        var self = this, groupsItems = [], sortItems = [], groupMap = {}, sortedItems = [];
        if (BI.isNotNull(change.sort)) {
            var sortObject = self.get("sort");
            sortItems = sortObject.details;
            var groupObject = self.get("group");
            if (BI.isNotNull(groupObject)) {
                groupsItems = groupObject.details;
                BI.each(groupsItems, function (i, groupItem) {
                    groupMap[groupItem.value] = groupItem;
                });
                BI.each(sortItems, function (i, sortItem) {
                    if (BI.isNotNull(groupMap[sortItem])) {
                        sortedItems.push(groupMap[sortItem]);
                    }
                });
                groupObject.details = sortedItems;

                if (groupObject.details.length > 0) {
                    self.set("group", groupObject)
                }

            }
        }

        if (BI.isNotNull(change.group)) {
            var groupObject = self.get("group");
            if (groupObject.type === BICst.GROUP.CUSTOM_GROUP) {
                var sortObject = {};
                sortObject.type = BICst.SORT.CUSTOM;
                sortObject.details = [];
                BI.each(groupObject.details, function (i, groupitem) {
                    sortObject.details.push(groupitem.value)
                });
                self.set("sort", sortObject)
            }
            if (this.get("type") === BICst.TARGET_TYPE.NUMBER) {
                var sort = this.get("sort");
                var group = this.get("group");
                if (group.type === BICst.GROUP.ID_GROUP) {
                    if (!BI.has(sort, "type") || sort.type === BICst.SORT.CUSTOM) {
                        self.set("sort", {type: BICst.SORT.ASC, sort_target: this.get("id")})
                    }
                }
                if (BI.isNotNull(prev.group) && prev.group.type === BICst.GROUP.ID_GROUP && change.group.type === BICst.GROUP.AUTO_GROUP) {
                    self.set("sort", {type: BICst.SORT.CUSTOM});
                }
                if (change.group.type === BICst.GROUP.CUSTOM_NUMBER_GROUP) {
                    var details = [];
                    var group_value = group.group_value || {};
                    BI.each(group_value.group_nodes, function (i, grp) {
                        details.push(grp.group_name);
                    });
                    if (BI.isNotNull(group_value.use_other)) {
                        details.push(group_value.use_other);
                    }
                    self.set("sort", {type: BICst.SORT.CUSTOM, details: details});
                }
            }
            if (this.get("type") === BICst.TARGET_TYPE.DATE) {
                var filter = this.get("filter_value");
                checkFilter(filter);
                this.set("filter_value", filter);
            }
        }

        if (BI.isNotNull(change.group)) {
            var groupObject = self.get("group");
            var accumulations = self.get("seriesAccumulation");
            if(groupObject.type === BICst.GROUP.CUSTOM_GROUP && BI.isNotEmptyArray(accumulations)) {
                groupsItems = groupObject.details;

                var allItems = [];

                //删除已被自定义分组的项
                BI.each(groupsItems, function (idx, items) {
                    BI.each(items.content, function (id, item) {
                        BI.each(accumulations, function (i, accumulation) {
                            var index = accumulation.items.indexOf(item.value);
                            if(index !== -1) {
                                accumulation.items.splice(index, 1);
                            }
                        })
                    })
                });

                BI.each(accumulations, function (idx, accumulation) {
                    allItems = BI.concat(allItems, accumulation.items);
                })

                //添加新增的自定义分组
                BI.each(groupsItems, function (idx, items) {
                    if(!BI.contains(allItems, items.value)) {
                        accumulations[0].items = BI.concat(accumulations[0].items, items.value);
                    }
                })

                self.set("seriesAccumulation", accumulations);
            }
        }

        function checkFilter(filter) {
            if(BI.isNull(filter)){
                return;
            }
            var filterType = filter.filter_type, filterValue = filter.filter_value;
            if (filterType === BICst.FILTER_TYPE.AND || filterType === BICst.FILTER_TYPE.OR) {
                BI.each(filterValue, function (i, value) {
                    checkFilter(value);
                });
                return;
            }
            if(BI.has(filter, "target_id") && filter.target_id === self.get("id")){
                switch (groupObject.type) {
                    case BICst.GROUP.Y:
                    case BICst.GROUP.M:
                    case BICst.GROUP.W:
                    case BICst.GROUP.S:
                        if(BI.has(self.YMDChangeMap, filter.filter_type)){
                            filter.filter_type = self.YMDChangeMap[filter.filter_type];
                        }
                        break;
                    case BICst.GROUP.YMD:
                        if(BI.has(self.otherChangeMap, filter.filter_type)){
                            filter.filter_type = self.otherChangeMap[filter.filter_type];
                        }
                        break;
                }
            }
        }
    },

    local: function () {
        if (this.has("changeSort")) {
            var sort = this.get("changeSort");
            this.set("sort", sort);
            return true;
        }
        if (this.has("changeGroup")) {
            var group = this.get("changeGroup");
            this.set("group", {type: group.type, details: []});
            return true;
        }
        return false;
    }
});