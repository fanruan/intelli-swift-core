/**
 * 自循环列主要操作面板
 * @class BI.CircleLevelPane
 * @extends BI.LoadingPane
 */

BI.CircleLevelPane = BI.inherit(BI.LoadingPane, {

    constants: {
        itemComboHeight: 30,
        CIRCLE_GAP_TEN: 10,
        CIRCLE_GAP_TWENTY: 20,
        buttonWidth: 90
    },

    _defaultConfig: function(){
        return BI.extend(BI.CircleLevelPane.superclass._defaultConfig.apply(this, arguments),  {
            baseCls: "bi-circle-level-pane"
        });
    },

    _init: function(){
        BI.CircleLevelPane.superclass._init.apply(this, arguments);

        var self = this;

        this.label = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Id_Column_Layer_Base"),
            cls: "operator-level-pane-label",
            textAlign: "left",
            lgap: this.constants.CIRCLE_GAP_TEN,
            textHeight: this.constants.itemComboHeight,
            height: this.constants.itemComboHeight
        });

        this.button_group = BI.createWidget({
            type: "bi.button_tree",
            layouts: [{
                type: "bi.vertical",
                scrolly: false
            }]
        });

        this.saveButton = BI.createWidget({
            type: "bi.button",
            value: BI.i18nText("BI-Save"),
            height: this.constants.itemComboHeight,
            width: this.constants.buttonWidth
        });

        this.saveButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.CircleLevelPane.EVENT_SAVE_CLICK);
        });

        this.button_group.on(BI.Controller.EVENT_CHANGE, function(type){
            if(type === BI.Events.CONFIRM){
                self.saveButton.setEnable(self.isValid());
                self.saveButton.setWarningTitle(self.isValid() ? "" : BI.i18nText("BI-Please_Rename_Field"));
            }
        });

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            vgap: 10,
            items: [{
                type: "bi.absolute",
                items: [{
                    el: {
                        type: "bi.vertical",
                        items: [this.label, this.button_group]
                    },
                    top: 0,
                    left: 0,
                    right: 3,
                    bottom: 0
                }]
            }, {
                type: "bi.right",
                items:[this.saveButton],
                height: this.constants.itemComboHeight,
                hgap: this.constants.CIRCLE_GAP_TEN
            }]
        });
    },

    getValue: function(){
        return {
            floors: this.button_group.getValue(),
            field_length: this.field_length
        };
    },

    isValid: function(){
        return BI.isNull(BI.find(this.button_group.getAllButtons(), function(idx, button){
            return button.isValid() === false;
        }));
    },

    populate: function(items){
        var self = this;
        this.loading();
        BI.Utils.getCircleLayerLevelInfo(items.table, items.layerInfo, function(res){
            self.field_length = BI.isNull(res.field_length) ? "" : res.field_length;
            res = BI.map(res.floors, function(idx, floor){
                return {
                    id: idx,
                    value: BI.i18nText("BI-Layer_Level") + (idx + 1),
                    circle: true,
                    length: BI.has(res, "length") ? res.length[idx] : 1,
                    items: BI.map(floor, function(id, item){
                        return {
                            value: item
                        }
                    })
                };
            });

            self.button_group.populate(BI.createItems(res, {
                type: "bi.circle_display_editor",
                validationChecker: function (v, id) {
                    var result = BI.find(self.button_group.getValue(), function (idx, value) {
                        return v === value.name && id != idx;
                    });
                    return BI.isNull(result);
                },
                onFocus: function () {
                    self.saveButton.setEnable(false);
                    self.saveButton.setWarningTitle(BI.i18nText("BI-Please_Rename_Field"));
                }
            }));

            if (!BI.isEmpty(items.layerInfo.parentid_field_name)) {
                self.label.setText(BI.i18nText("BI-Id_Column_Layer_Base") + items.layerInfo.id_field_name + "  " + BI.i18nText("BI-ParentID_Column_Text") +
                items.layerInfo.parentid_field_name);
            } else {
                self.label.setText(BI.i18nText("BI-Id_Column_Layer_Base") + items.layerInfo.id_field_name);
            }

            self.loaded();
        });
    }
});

BI.CircleLevelPane.EVENT_SAVE_CLICK = "EVENT_SAVE_CLICK";
$.shortcut("bi.circle_level_pane", BI.CircleLevelPane);