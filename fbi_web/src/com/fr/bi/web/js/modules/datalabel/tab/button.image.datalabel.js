/**
 * Created by fay on 2016/9/5.
 */
BI.DataLabelImageButton = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DataLabelImageButton.superclass._defaultConfig.apply(this, arguments), {

        });
    },

    _init: function () {
        BI.DataLabelImageButton.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.img = BI.createWidget({
            type: "bi.image_button",
            src: o.src,
            width: o.width,
            height: o.height
        });

        this.img.on(BI.ImageButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.DataLabelImageButton.EVENT_CHANGE, self.getSrc());
        });

        var icon = BI.createWidget({
            type: "bi.icon_button",
            cls: "image-set-delete close-font",
            width: o.iconWidth,
            height: o.iconHeight
        });

        icon.on(BI.IconButton.EVENT_CHANGE, function () {
            self.destroy();
            BI.DataLabelImageButton.superclass.destroy.apply(this, arguments);
            self.fireEvent(BI.DataLabelImageButton.DELETE_IMAGE, arguments);
        });
        icon.setVisible(false);

        var button = BI.createWidget({
            type: "bi.absolute",
            cls: "image-button",
            element: this.element,
            items: [{
                el: this.img
            }, {
                el: icon,
                right: 0
            }],
            width: o.width,
            height: o.height
        });

        button.element.hover(function () {
            icon.setVisible(true);
        }, function () {
            icon.setVisible(false);
        });
    },

    getSrc: function () {
        return this.img.getSrc();
    }
});

BI.DataLabelImageButton.EVENT_CHANGE = "BI.DataLabelImageButton_EVENT_CHANGE";
BI.DataLabelImageButton.DELETE_IMAGE = "BI.DataLabelImageButton.DELETE_IMAGE";
$.shortcut('bi.data_label_image_button', BI.DataLabelImageButton);