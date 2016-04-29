Data.Source = BISource = {

    getTableById: function (tId) {
        return Pool.source[tId];
    },

    addTable: function (table) {
        if (BI.isNull(table) || BI.isNull(table.id)) {
            return;
        }
        Pool.source[table.id] = table;
    }
};