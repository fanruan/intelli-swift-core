/**
 * Created by zcf on 2017/1/16.
 */
BI.StateIconButton = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.StateIconButton.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-state-icon-button"
        })
    },

    _init: function () {
        BI.StateIconButton.superclass._init.apply(this, arguments);

        this.buttonWrapper = BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this._createIconButton(""),
                top: 2,
                bottom: 0,
                left: 0,
                right: 0
            }]
        })
    },

    _createIconButton: function (cls) {
        var self = this, o = this.options;
        var button = BI.createWidget({
            type: "bi.icon_button",
            stopPropagation: true,
            cls: cls,
            height: o.height,
            width: o.width,
            iconWidth: o.iconWidth,
            iconHeight: o.iconHeight
        });
        button.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.StateIconButton.EVENT_CHANGE);
        });
        return button;
    },

    _createUpdatingButtonItem: function () {
        var cls = "excel-success-font";
        return {
            el: this._createIconButton(cls),
            top: 2,
            bottom: 0,
            left: 0,
            right: 0
        }
    },

    _createFailButtonItem: function () {
        var cls = "excel-fail-font";
        return {
            el: this._createIconButton(cls),
            top: 2,
            bottom: 0,
            left: 0,
            right: 0
        }
    },

    setState: function (state) {
        this.buttonWrapper.empty();
        if (state) {
            this.buttonWrapper.addItem(this._createUpdatingButtonItem());
        } else {
            this.buttonWrapper.addItem(this._createFailButtonItem());
        }
    }
});
BI.StateIconButton.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.state_icon_button", BI.StateIconButton);