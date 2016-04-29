/**
 * Created by Kary on 2016/4/11.
 */
BI.DetailRegionShow = BI.inherit(BI.AbstractRegion, {
    _defaultConfig: function () {
        var conf = BI.DetailRegionShow.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            extraCls: "bi-detail-region bi-dimension-region"
        })

    },

    _init: function () {
        var self = this, o = this.options;
        BI.DetailRegionShow.superclass._init.apply(this, arguments);
        this.relationButton = BI.createWidget({
            type: "bi.text_button",
            textHeight: 30,
            value: BI.i18nText("BI-Field_Relation_Setting")
        });

        this.relationButton.on(BI.TextButton.EVENT_CHANGE, function () {
            self.popup = BI.createWidget({
                type: "bi.field_relation_setting_popup",
                wId: o.wId
            });
            self.popup.on(BI.FieldRelationSettingPopup.EVENT_CHANGE, function () {
                BI.each(self.getValue(), function (i, dId) {
                    self._setDimensionRelation(dId, {
                        relationItem: self.popup.getValue()
                    })
                });


            });
            BI.Popovers.remove("relation_set");
            BI.Popovers.create("relation_set", self.popup).open("relation_set");
        });

        this.calculateAddButton = BI.createWidget({
            type: "bi.text_button",
            textHeight: 30,
            value: BI.i18nText("BI-Add_Cal_Target")
        });

        this.calculateAddButton.on(BI.TextButton.EVENT_CHANGE, function () {
            var dId = BI.UUID();
            self.calpopup = BI.createWidget({
                type: "bi.calculate_target_popup_detail",
                wId: o.wId,
                targetId: dId
            });
            self.calpopup.on(BI.CalculateTargetPopupDetail.EVENT_CHANGE, function () {
                var targetValue = self.calpopup.getValue();
                self._setCalculateTarget(targetValue.dId, targetValue.data)

            });
            BI.Popovers.remove(dId + "calculate_set");
            BI.Popovers.create(dId + "calculate_set", self.calpopup).open(dId + "calculate_set");
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.right",
                    hgap: 10,
                    items: [{
                        el: this.calculateAddButton

                    }, {
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

    _setCalculateTarget: function (dId, options) {
        var o = this.options;
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
        this.store[dId] = container;
        this.center.addItem(this.store[dId]);
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


    _createCalTargetItem: function () {
        var calItem = {
            name: "",
            type: BICst.TARGET_TYPE.FORMULA
        };
        return calItem;
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
BI.DetailRegionShow.EVENT_RELATION_SET = "EVENT_RELATION_SET";
$.shortcut("bi.detail_region_show", BI.DetailRegionShow);
