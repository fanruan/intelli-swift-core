/**
 * Created by zcf on 2016/11/24.
 */
BI.DimensionTagButton = BI.inherit(BI.BasicButton, {

    _defaultConfig: function () {
        return BI.extend(BI.DimensionTagButton.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension-tag-button",
            drop: {}
        })
    },

    _init: function () {
        BI.DimensionTagButton.superclass._init.apply(this, arguments);

        var o = this.options;

        BI.createWidget({
            type: "bi.text_button",
            element: this.element,
            cls: o.cls,
            text: o.text,
            value: o.value,
            height: 40,
            width: 60
        });

        this.element.droppable(o.drop)
    },

    doClick: function () {
        BI.DimensionTagButton.superclass.doClick.apply(this, arguments);
        if (this.isValid()) {
            this.fireEvent(BI.DimensionTagButton.EVENT_CHANGE, this.getValue(), this);
        }
    },

    setHighLightBg: function (enable) {
        if (enable) {
            this.element.addClass("high-light-background");
        } else {
            this.element.removeClass("high-light-background");
        }
    }
});
BI.DimensionTagButton.EVENT_CHANGE = "BI.DimensionTagButton.EVENT_CHANGE";
$.shortcut("bi.dimension_tag_button", BI.DimensionTagButton);