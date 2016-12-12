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