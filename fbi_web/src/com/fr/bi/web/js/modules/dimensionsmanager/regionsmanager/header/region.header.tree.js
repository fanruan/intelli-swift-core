/**
 * Created by GUY on 2016/3/17.
 * @class BI.TreeRegionHeader
 * @extends BI.RegionHeader
 */
BI.TreeRegionHeader = BI.inherit(BI.RegionHeader, {

    _defaultConfig: function () {
        return BI.extend(BI.TreeRegionHeader.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-tree-region-header",
            titleName: "",
            wId: "",
            viewType: BICst.REGION.DIMENSION1
        });
    },

    _init: function () {
        BI.TreeRegionHeader.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.relationButton = BI.createWidget({
            type: "bi.text_button",
            textHeight: 30,
            value: BI.i18nText("BI-Field_Relation_Setting")
        });

        this.relationButton.on(BI.TextButton.EVENT_CHANGE, function () {
            self.popup = BI.createWidget({
                type: "bi.field_relation_setting_with_preview_popup",
                wId: o.wId
            });
            self.popup.on(BI.FieldRelationSettingWithPreviewPopup.EVENT_CHANGE, function () {
                BI.each(self.getValue(), function (i, dId) {
                    self._setDimensionRelation(dId, {
                        relationItem: self.popup.getValue()
                    })
                });


            });
            BI.Popovers.remove("relation_set");
            BI.Popovers.create("relation_set", self.popup).open("relation_set");
        });


        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.right",
                    hgap: 10,
                    items: [{
                        el: this.relationButton
                    }],
                    height: 25
                },
                top: 0,
                left: 0,
                right: 0
            }]
        })
    },

    _setDimensionRelation: function (dId, options) {
        var self = this, o = this.options;
        o.dimensionCreator(dId, o.viewType, options);
    },
});
$.shortcut("bi.tree_region_header", BI.TreeRegionHeader);