/**
 * Created by fay on 2016/9/1.
 */
BI.DataImagePopup = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function () {
        return BI.extend(BI.DataImagePopup.superclass._defaultConfig.apply(this, arguments), {
            width: 600,
            height: 500
        })
    },

    _init: function () {
        BI.DataImagePopup.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Data_Image"),
            height: 50,
            textAlign: "left",
            lgap: 10
        });
        return true;
    },

    rebuildCenter: function (center) {
        var o = this.options;
        this.dataImagePane = BI.createWidget({
            type: "bi.data_image",
            element: center,
            dId: o.dId
        });
        return true;
    },

    populate: function () {
        this.dataImagePane.populate();
    },

    end: function () {
        this.fireEvent(BI.DataImagePopup.EVENT_CHANGE, this.dataImagePane.getValue());
    }
});

BI.DataImagePopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.data_image_popup", BI.DataImagePopup);