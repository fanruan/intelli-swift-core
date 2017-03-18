/**
 * @class BI.RelationSetPane
 * @extend BI.BarPopoverSection
 * 设置关联
 */
BI.RelationSetPane = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function () {
        return BI.extend(BI.RelationSetPane.superclass._defaultConfig.apply(this, arguments), {
            warningTitle: BI.i18nText("BI-Please_Select_Relation_Between_Tables")
        });
    },

    _init: function () {
        BI.RelationSetPane.superclass._init.apply(this, arguments);
    },

    end: function () {
        this.fireEvent(BI.RelationSetPane.EVENT_CHANGE, this.relationPane.getValue());
    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: this.options.field.field_name + BI.i18nText("BI-Link_Infor"),
            height: 50,
            textAlign: "left",
            lgap: 10
        });
        return true;
    },

    rebuildCenter: function (center) {
        var self = this, o = this.options;
        this.relationPane = BI.createWidget({
            type: "bi.relation_pane",
            element: center,
            field: o.field,
            relations: o.relations
        });
        this.relationPane.on(BI.RelationPane.EVENT_VALID, function () {
            self.setConfirmButtonEnable(true);
        });
        this.relationPane.on(BI.RelationPane.EVENT_ERROR, function () {
            self.setConfirmButtonEnable(false);
        });
    }
});
BI.RelationSetPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.relation_set_pane", BI.RelationSetPane);