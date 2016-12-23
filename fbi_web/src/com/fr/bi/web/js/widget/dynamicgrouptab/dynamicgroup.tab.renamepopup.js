/**
 * Created by 小灰灰 on 2016/3/23.
 */
BI.DynamicGroupTabRenamePopup = BI.inherit(BI.BarPopoverSection, {
    _constants:{
        NORTH_HEIGHT : 50,
        EDITOR_WIDTH : 260,
        EDITOR_HEIGHT : 28,
        LABEL_WIDTH : 100,
        LABEL_HEIGHT : 30,
        VGAP : 20,
        HGAP : 10
    },

    _defaultConfig: function () {
        var conf = BI.DynamicGroupTabRenamePopup.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            cls: "bi-dynamic-tab-rename-popup",
            nameChecker: BI.emptyFn
        })
    },

    _init: function () {
        BI.DynamicGroupTabRenamePopup.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function (north) {
        var self = this, o = this.options;
        BI.createWidget({
            type: "bi.label",
            element: north,
            text:  BI.i18nText('BI-Rename'),
            textAlign: "left",
            height : self._constants.NORTH_HEIGHT
        });
        return true
    },

    rebuildCenter: function (center) {
        var self = this, o = this.options;
        self.name = BI.createWidget({
            type: "bi.sign_editor",
            width: self._constants.EDITOR_WIDTH,
            height: self._constants.EDITOR_HEIGHT,
            cls: "rename-input",
            allowBlank: false,
            errorText: function (v) {
                if (v === "") {
                    return BI.i18nText("BI-Report_Name_Not_Null");
                } else {
                    return BI.i18nText("BI-Template_Name_Already_Exist");
                }
            },
            validationChecker: function (v) {
                if (v.indexOf("\'") > -1) {
                    return false;
                }
                return o.nameChecker.call(this, v);
            }
        });

        BI.createWidget({
            type: "bi.vertical",
            element: center,
            items: [{
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Name") + ' :',
                    height: self._constants.LABEL_HEIGHT,
                    width: self._constants.LABEL_WIDTH,
                    textAlign: "left",
                    cls: "rename-label",
                    hgap: self._constants.HGAP
                }, this.name]
            }],
            vgap: self._constants.VGAP
        })
    },

    end: function(){
        this.fireEvent(BI.DynamicGroupTabRenamePopup.EVENT_CHANGE, this.name.getValue());
    },

    populate: function (name) {
        this.name.setValue(name);
    }

});
BI.DynamicGroupTabRenamePopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.dynamic_group_tab_rename_popover", BI.DynamicGroupTabRenamePopup);