/**
 * Created by 小灰灰 on 2016/4/11.
 */
BI.ETLTableNamePopover = BI.inherit(BI.ETLTableRenamePopover, {
    rebuildNorth: function (north) {
        var self = this, o = this.options;
        BI.createWidget({
            type: "bi.label",
            element: north,
            text:  BI.i18nText('BI-ETL_Save_Title'),
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
                    return BI.i18nText("BI-Table_Name_Already_Exist");
                }
            },
            validationChecker: function (v) {
                if (v.indexOf("\'") > -1) {
                    return false;
                }
                return o.renameChecker.call(this, v);
            }
        });

        this.name.on(BI.SignEditor.EVENT_ERROR, function (v) {
            if (BI.isEmptyString(v)) {
                self.sure.setWarningTitle(BI.i18nText("BI-Report_Name_Not_Null"));
            } else {
                self.sure.setWarningTitle(BI.i18nText("BI-Template_Name_Already_Exist"));
            }
            self.sure.setEnable(false);
        });
        this.name.on(BI.SignEditor.EVENT_VALID, function () {
            self.sure.setEnable(true);
        });

        this.describe = BI.createWidget({
            type:"bi.textarea_editor",
            cls: "rename-input",
            width: self._constants.EDITOR_WIDTH,
            height: self._constants.EDITOR_HEIGHT * 3,
        })

        BI.createWidget({
            type: "bi.vertical",
            cls: "bi-etl-rename-center",
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
            }, {
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Group") + ' :',
                    height: self._constants.LABEL_HEIGHT,
                    width: self._constants.LABEL_WIDTH,
                    textAlign: "left",
                    cls: "rename-label",
                    hgap: self._constants.HGAP
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-MYETL"),
                    height: self._constants.EDITOR_HEIGHT,
                    width: self._constants.EDITOR_WIDTH,
                    textAlign: "left",
                    cls: "rename-label",
                    hgap: self._constants.HGAP
                }]
            },{
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Describe") + ' :',
                    height: self._constants.LABEL_HEIGHT,
                    width: self._constants.LABEL_WIDTH,
                    textAlign: "left",
                    cls: "rename-label",
                    hgap: self._constants.HGAP
                }, this.describe]
            }],
            vgap: self._constants.VGAP
        })
    },


    end: function(){
        this.fireEvent(BI.ETLTableNamePopover.EVENT_CHANGE, this.name.getValue(), this.describe.getValue());
    }

});
BI.ETLTableNamePopover.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.etl_table_name_popover", BI.ETLTableNamePopover);