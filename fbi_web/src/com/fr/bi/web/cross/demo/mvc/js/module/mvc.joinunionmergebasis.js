JoinUnionMergeBasisView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(JoinUnionMergeBasisView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "join-union-merge-basis-view"
        })
    },

    _init: function(){
        JoinUnionMergeBasisView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var mergeBasis = BI.createWidget({
            type: "bi.table_add_union",
            tables: [{
                connection_name: "local",
                fields: [[{
                    class_type: 5,
                    field_name: "费用ID",
                    field_size: 50,
                    field_type: 1,
                    id: "88afa39c费用ID",
                    is_enable: true,
                    is_usable: true,
                    table_id: "88afa39c"
                }, {
                    class_type: 5,
                    field_name: "活动ID",
                    field_size: 50,
                    field_type: 1,
                    id: "88afa39c活动ID",
                    is_enable: true,
                    is_usable: true,
                    table_id: "88afa39c"
                }, {
                    class_type: 5,
                    field_name: "费用说明",
                    field_size: 255,
                    field_type: 1,
                    id: "88afa39c费用说明",
                    is_enable: true,
                    is_usable: true,
                    table_id: "88afa39c"
                }], [{
                    class_type: 0,
                    field_name: "费用类型",
                    field_size: 10,
                    field_type: 2,
                    id: "88afa39c费用类型",
                    is_enable: true,
                    is_usable: true,
                    table_id: "88afa39c"
                }, {
                    class_type: 1,
                    field_name: "费用金额",
                    field_size: 12,
                    field_type: 2,
                    id: "88afa39c费用金额",
                    is_enable: true,
                    is_usable: true,
                    table_id: "88afa39c"
                }, {
                    class_type: 0,
                    field_name: "是否有发票",
                    field_size: 10,
                    field_type: 2,
                    id: "88afa39c是否有发票",
                    is_enable: true,
                    is_usable: true,
                    table_id: "88afa39c"
                }], []],
                id: "bedcac7909c52544",
                md5: "88afa39c",
                relations: {},
                table_name: "activity_fee",
                translations: {}
            }, {
                connection_name: "local",
                fields: [[{
                    class_type: 5,
                    field_name: "费用ID",
                    field_size: 50,
                    field_type: 1,
                    id: "88afa39c费用ID",
                    is_enable: true,
                    is_usable: true,
                    table_id: "88afa39c"
                }, {
                    class_type: 5,
                    field_name: "活动ID",
                    field_size: 50,
                    field_type: 1,
                    id: "88afa39c活动ID",
                    is_enable: true,
                    is_usable: true,
                    table_id: "88afa39c"
                }, {
                    class_type: 5,
                    field_name: "费用说明",
                    field_size: 255,
                    field_type: 1,
                    id: "88afa39c费用说明",
                    is_enable: true,
                    is_usable: true,
                    table_id: "88afa39c"
                }], [{
                    class_type: 0,
                    field_name: "费用类型",
                    field_size: 10,
                    field_type: 2,
                    id: "88afa39c费用类型",
                    is_enable: true,
                    is_usable: true,
                    table_id: "88afa39c"
                }, {
                    class_type: 1,
                    field_name: "费用金额",
                    field_size: 12,
                    field_type: 2,
                    id: "88afa39c费用金额",
                    is_enable: true,
                    is_usable: true,
                    table_id: "88afa39c"
                }, {
                    class_type: 0,
                    field_name: "是否有发票",
                    field_size: 10,
                    field_type: 2,
                    id: "88afa39c是否有发票",
                    is_enable: true,
                    is_usable: true,
                    table_id: "88afa39c"
                }], []],
                id: "aedcac7909c52544",
                md5: "88afa39c",
                relations: {},
                table_name: "activity_fee",
                translations: {}
            }]
        });
        mergeBasis.populate([[1, 1], [2, 2], [3, 3], [4, 4], [5, 5]]);
        BI.createWidget({
            type: "bi.float_center_adapt",
            element: vessel,
            items: [mergeBasis]
        })
    }
});

JoinUnionMergeBasisModel = BI.inherit(BI.Model, {

});