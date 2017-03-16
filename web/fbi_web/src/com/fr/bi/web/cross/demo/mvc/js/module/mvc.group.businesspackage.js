/**
 * Created by roy on 15/11/20.
 */
BusinessPackageGroupView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BusinessPackageGroupView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-business-package-group-view"
        })
    },
    _init: function () {
        BusinessPackageGroupView.superclass._init.apply(this, arguments);
    },
    _render: function (vessel) {
        var self = this;
        //var packButton = BI.createWidget({
        //    type: "bi.business_package_button",
        //    text: "asdfasdfasdfasdfasdfasfasdfasfasdfasfasdfa",
        //    table_count: 23
        //});
        //
        //var packAddButton = BI.createWidget({
        //    type: "bi.business_pack_add"
        //
        //});
        //
        //var packExpander = BI.createWidget({
        //    type: "bi.business_package_expander",
        //    item: {
        //        id: 1,
        //        value: "group1",
        //        content: [
        //            {
        //                id: 11,
        //                value: "group1"
        //            },
        //            {
        //                id: 12,
        //                value: "group2"
        //            }, {
        //                id: 13,
        //                value: "group3"
        //            }
        //
        //        ]
        //    }
        //});
        //
        //this.businessPackageGroupPane = BI.createWidget({
        //    type: "bi.business_package_group_pane",
        //    height: 500
        //});
        //
        //var populateButton = BI.createWidget({
        //    type: "bi.button",
        //    value: "populate pane",
        //    handler: function () {
        //        self.businessPackageGroupPane.populate(
        //            [
        //                {
        //                    value: "groupName1",
        //                    content: [{value: "first"}, {value: "second"}, {value: "third"}]
        //                },
        //                {
        //                    value: "groupName2",
        //                    content: [{value: "first"}, {value: "second"}, {value: "third"}]
        //                }
        //            ]
        //        );
        //    }
        //});
        //
        //var wIds = BI.Utils.getAllWidgetIDs();
        //var dId = BI.Utils.getAllDimDimensionIDs(wIds[0])[0];
        //var businessPackageGroupWidget = BI.createWidget({
        //    type: "bi.business_package_group",
        //    dId:dId,
        //    height:600
        //});

        var packageButton = BI.createWidget({
            type:"bi.business_package_button",
            value:"package"
        });

        BI.createWidget({
            type:"bi.left",
            element:vessel,
            items:[{
                el:packageButton
            }]
        });

        //BI.createWidget({
        //    type: "bi.vertical",
        //    element: vessel,
        //    items: [{
        //        el: {
        //            type: "bi.left",
        //            items: [packButton,
        //                packAddButton]
        //        }
        //    },
        //        packExpander,
        //        populateButton,
        //        self.businessPackageGroupPane,
        //        businessPackageGroupWidget
        //    ]
        //});


        //businessPackageGroupWidget.populate(
        //    [
        //    {
        //        value: "groupName1",
        //        content: [{value: "first"}, {value: "second"}, {value: "third"}]
        //    },
        //    {
        //        value: "groupName2",
        //        content: [{value: "first"}, {value: "second"}, {value: "third"}]
        //    }
        //])
    }
});

BusinessPackageGroupModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BusinessPackageGroupModel.superclass._defaultConfig.apply(this, arguments), {})
    },
    _init: function () {
        BusinessPackageGroupModel.superclass._init.apply(this, arguments);
    }
});