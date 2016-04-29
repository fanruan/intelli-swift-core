/**
 * Created by GUY on 2015/7/3.
 */
BIShow.DimensionModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.DimensionModel.superclass._defaultConfig.apply(this, arguments), {
            _src: {},
            sort: {type: BICst.SORT.ASC},
            group: {type: BICst.GROUP.ID_GROUP},
            dimension_map: {},
            settings: {},
            type: "",
            name: "",
            used: true
        });
    },

    _init: function () {
        BIShow.DimensionModel.superclass._init.apply(this, arguments);
    },

    refresh: function () {

    },


    change: function (change) {
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
                var sortObject = self.get("sort");
                if (!BI.isNotNull(sortObject)) {
                    sortObject = {}
                }
                sortObject.type = BICst.SORT.CUSTOM;
                sortObject.details = [];
                BI.each(groupObject.details, function (i, groupitem) {
                    sortObject.details.push(groupitem.value)
                });
                self.set("sort", sortObject)
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
        }
        return false;
    }
});
