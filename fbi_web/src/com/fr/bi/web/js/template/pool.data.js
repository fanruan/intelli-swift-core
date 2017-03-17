Pool = {
    source: ${source},
    groups: ${groups},
    packages: ${packages},
    connections: ${connections},
    relations: ${relations},
    translations: ${translations},
    tables: ${tables},
    fields: ${fields},
    noAuthFields: ${noAuthFields},
    excel_views: ${excel_views}
};
Pool.foreignRelations = {};

for (var i in Pool.relations) {
    for(var j in Pool.relations[i]) {
        Pool.foreignRelations[j] = Pool.foreignRelations[j] || {};
        Pool.foreignRelations[j][i] = Pool.relations[i][j];
    }
}