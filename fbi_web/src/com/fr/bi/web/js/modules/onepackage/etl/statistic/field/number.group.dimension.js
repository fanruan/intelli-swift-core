/**
 * @class BI.NumberGroupDimension
 * @extends BI.Widget
 * @author windy
 */
BI.NumberGroupDimension = BI.inherit(BI.AbstractDimension, {

    constants: {
        DIMENSION_BUTTON_HEIGHT: 25,
        COMBO_WIDTH: 25,
        LABEL_GAP : 5
    },

    _defaultConfig: function () {
        return BI.extend(BI.NumberGroupDimension.superclass._defaultConfig.apply(this, arguments), {
            table: {}
        })
    },

    _init: function () {
        BI.NumberGroupDimension.superclass._init.apply(this, arguments);
    },

    _createCombo: function () {
        var self = this, o = this.options;
        o.groupOrSummary = 0;
        o.fieldType = BICst.COLUMN.NUMBER;
        var combo = BI.createWidget({
            type: "bi.group_number_combo",
            dimension: o.model.getDimension(o.dId)
        });
        combo.on(BI.GroupNumberCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.GROUP_BY_VALUE:
                    o.model.setDimensionGroupById(o.dId, {type: BICst.GROUP.ID_GROUP});
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.GROUP_SETTING:
                    self._setGroups();
                    break;
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.DISPLAY:
                    o.model.setDimensionUsedById(o.dId, true);
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.HIDDEN:
                    o.model.setDimensionUsedById(o.dId, false);
                    self.checkStatus();
                    break;
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.RENAME:
                    self.nameEditor.focus();
                    break;
                case BICst.STATISTICS_GROUP_NUMBER_COMBO.DELETE:
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
            type: "bi.number_custom_group_popup",
            dId: o.dId,
            model: o.model
        });
        popup.on(BI.NumberCustomGroupPopup.EVENT_CHANGE, function(v){
            o.model.setDimensionGroupById(o.dId, v);
            self.checkStatus();
        });
        BI.Popovers.create(id, popup).open(id);
        popup.populate(o.model.getDimensionGroupById(o.dId));
    }
});
$.shortcut("bi.number_group_dimension", BI.NumberGroupDimension);