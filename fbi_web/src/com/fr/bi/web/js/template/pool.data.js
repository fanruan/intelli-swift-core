Pool = {
    source: ${source},
    groups: ${groups},
    packages: ${packages},
    connections: ${connections},
    relations: ${relations},
    translations: ${translations},
    tables: ${tables},
    fields: ${fields},
    excel_views: ${excel_views}
};
Pool.foreignRelations = {};
$.each(Pool.relations, function (p, item) {
    $.each(item, function (f, relation) {
        Pool.foreignRelations[f] = Pool.foreignRelations[f] || {};
        Pool.foreignRelations[f][p] = relation;
    })
});