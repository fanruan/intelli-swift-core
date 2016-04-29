PaneModulesView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(PaneModulesView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-pane-modules bi-mvc-layout"
        })
    },

    _init: function () {
        PaneModulesView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var regions = [{
            extraCls: "bi-dimension-region",
            connect: "dimension-region-container",
            dimensionOrTargetClass: "bi-dimension-container",
            iconClass: 'region-row-icon',
            titleName: BI.i18nText('BI-Row_Header'),
            innerText: BI.i18nText('BI-Drag_Left_Field'),
            regionType: BICst.REGION.DIMENSION1
        }, {
            extraCls: "bi-dimension-region",
            connect: "dimension-region-container",
            dimensionOrTargetClass: "bi-dimension-container",
            iconClass: 'region-column-icon',
            titleName: BI.i18nText('BI-Column_Header'),
            innerText: BI.i18nText('BI-Drag_Left_Field'),
            regionType: BICst.REGION.DIMENSION2
        }, {
            extraCls: "bi-dimension-region",
            connect: "dimension-region-container",
            dimensionOrTargetClass: "bi-dimension-container",
            iconClass: 'region-row-icon',
            titleName: BI.i18nText('BI-Category'),
            innerText: BI.i18nText('BI-Drag_Left_Field'),
            regionType: BICst.REGION.DIMENSION1
        }, {
            extraCls: "bi-dimension-region",
            connect: "dimension-region-container",
            dimensionOrTargetClass: "bi-dimension-container",
            iconClass: 'region-column-icon',
            titleName: BI.i18nText('BI-Series'),
            innerText: BI.i18nText('BI-Drag_Left_Field'),
            regionType: BICst.REGION.DIMENSION2
        }, {
            extraCls: "bi-dimension-region",
            connect: "dimension-region-container",
            dimensionOrTargetClass: "bi-dimension-container",
            iconClass: 'region-column-icon',
            titleName: BI.i18nText('BI-Region_Name'),
            innerText: BI.i18nText('BI-Drag_Left_Field'),
            regionType: BICst.REGION.DIMENSION1
        }, {
            extraCls: "bi-target-region",
            connect: "target-region-container",
            dimensionOrTargetClass: "target-container",
            iconClass: 'region-target-number-icon',
            titleName: BI.i18nText('BI-Target'),
            innerText: BI.i18nText('BI-Drag_Left_Numberic_Field'),
            regionType: BICst.REGION.TARGET1
        }, {
            extraCls: "bi-target-region",
            connect: "target-region-container",
            dimensionOrTargetClass: "target-container",
            iconClass: 'region-target-number-icon',
            titleName: BI.i18nText('BI-Left_Value_Axis'),
            innerText: BI.i18nText('BI-Drag_Left_Numberic_Field'),
            regionType: BICst.REGION.TARGET1
        }, {
            extraCls: "bi-target-region",
            connect: "target-region-container",
            dimensionOrTargetClass: "target-container",
            iconClass: 'region-target-number-icon',
            titleName: BI.i18nText('BI-Right_Value_Axis'),
            innerText: BI.i18nText('BI-Drag_Left_Numberic_Field'),
            regionType: BICst.REGION.TARGET2
        }, {
            extraCls: "bi-target-region",
            connect: "target-region-container",
            dimensionOrTargetClass: "target-container",
            iconClass: 'region-target-number-icon',
            titleName: BI.i18nText('BI-X_Value'),
            innerText: BI.i18nText('BI-Drag_Left_Numberic_Field'),
            regionType: BICst.REGION.TARGET2
        }, {
            extraCls: "bi-target-region",
            connect: "target-region-container",
            dimensionOrTargetClass: "target-container",
            iconClass: 'region-target-number-icon',
            titleName: BI.i18nText('BI-Y_Value'),
            innerText: BI.i18nText('BI-Drag_Left_Numberic_Field'),
            regionType: BICst.REGION.TARGET1
        }, {
            extraCls: "bi-target-region",
            connect: "target-region-container",
            dimensionOrTargetClass: "target-container",
            iconClass: 'region-target-number-icon',
            titleName: BI.i18nText('BI-Bubble_Size'),
            innerText: BI.i18nText('BI-Drag_Left_Numberic_Field'),
            regionType: BICst.REGION.TARGET3
        }];
        BI.createWidget({
            type: "bi.vertical",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: BI.createItems(regions, {
                type: "bi.dimension_target_region"
            })
        })
    }
});

PaneModulesModel = BI.inherit(BI.Model, {});