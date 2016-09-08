/**
 * Created by fay on 2016/9/8.
 */
BI.DataLabelImageButtonGroup = BI.inherit(BI.ButtonGroup, {
    unShiftItems: function (items) {
        var o = this.options;
        var btns = this._btnsCreator.apply(this, arguments);
        this.buttons = BI.concat(btns, this.buttons);

        this.populate(this.buttons);
    },
});
$.shortcut('bi.data_label_image_button_group', BI.DataLabelImageButtonGroup);