/**
 * Created by roy on 16/3/21.
 */
BI.TreeRegion = BI.inherit(BI.AbstractRegion, {
    _defaultConfig: function () {
        var conf = BI.TreeRegion.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            extraCls: "bi-tree-region bi-dimension-region"
        })

    },

    _init: function () {
        var self = this, o = this.options;
        BI.TreeRegion.superclass._init.apply(this, arguments);
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


    _getFields: function () {
        var dimensions = this.getValue();
        var fieldIds = [];
        BI.each(dimensions, function (i, did) {
            fieldIds.push(BI.Utils.getFieldIDByDimensionID(did))
        });
        return fieldIds;
    },

    _getRegionType: function () {
        return BICst.REGION.DIMENSION1;
    },

    _setDimensionRelation: function (dId, options) {
        var self = this, o = this.options;
        options || (options = {});
        o.dimensionCreator(dId, this._getRegionType(), options);
    },

    _createDimension: function (dId, options) {
        var self = this, o = this.options;
        options || (options = {});
        var dim = o.dimensionCreator(dId, this._getRegionType(), options);
        var container = BI.createWidget({
            type: "bi.absolute",
            cls: "dimension-container",
            data: {
                dId: dId
            },
            height: 25,
            items: [{
                el: dim,
                left: 0,
                top: 0,
                right: 0,
                bottom: 0
            }]
        });
        return container;
    },


    getValue: function () {
        var result = [];
        var dimensions = $(".dimension-container", this.center.element);
        BI.each(dimensions, function (i, dom) {
            result.push($(dom).data("dId"));
        });
        return result;
    }
});
BI.TreeRegion.EVENT_RELATION_SET = "EVENT_RELATION_SET";
$.shortcut("bi.tree_region", BI.TreeRegion);