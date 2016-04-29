/**
 * @class BI.StringGroupDimension
 * @extends BI.Widget
 * @author windy
 */
BI.StringGroupDimension = BI.inherit(BI.AbstractDimension, {

    _defaultConfig: function () {
        return BI.extend(BI.StringGroupDimension.superclass._defaultConfig.apply(this, arguments), {
            table: {}
        })
    },

    _init: function () {
        BI.StringGroupDimension.superclass._init.apply(this, arguments);
    },

    _createCombo: function () {
        var self = this, o = this.options;
        o.groupOrSummary = 0;
        o.fieldType = BICst.COLUMN.STRING;
        var combo = BI.createWidget({
            type: "bi.group_string_combo",
            dimension: o.model.getDimension(o.dId)
        });
        combo.on(BI.GroupStringCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.STATISTICS_GROUP_STRING_COMBO.GROUP_BY_VALUE:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.ID_GROUP});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_STRING_COMBO.GROUP_BY_CUSTOM:
                    self._setGroups();
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_STRING_COMBO.DELETE:
                    self.fireEvent(BI.AbstractDimension.EVENT_DESTROY);
                    break;
            }
        });
        return combo;
    },

    _setGroups: function(){
        var self = this, o = this.options;
        var id = o.dId;
        BI.Popovers.remove(id);
        var popup = BI.createWidget({
            type: "bi.custom_group_popup",
            fieldName: o.fieldName,
            table: this.options.table
        });
        popup.on(BI.CustomGroupPopup.EVENT_CHANGE, function(v){
            o.model.setDimensionGroupById(o.dId, v);
        });
        BI.Popovers.create(id, popup).open(id);
        popup.populate(o.model.getDimensionGroupById(o.dId));
    }
});
$.shortcut("bi.string_group_dimension", BI.StringGroupDimension);