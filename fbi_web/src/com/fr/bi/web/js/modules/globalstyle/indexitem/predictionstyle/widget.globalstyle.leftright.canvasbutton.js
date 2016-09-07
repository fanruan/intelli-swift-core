/**
 * Created by zcf on 2016/9/2.
 */
BI.GlobalStyleCanvasButton = BI.inherit(BI.BasicButton, {
    _defaultConfig: function () {
        var conf = BI.IconButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            tagName: "a",
            baseCls: (conf.baseCls || "") + " bi-icon-button horizon-center display-block",
            direction: "left",
            initState: true
        })
    },

    _init: function () {
        BI.GlobalStyleCanvasButton.superclass._init.apply(this, arguments);
        var o = this.options;
        this.currentState = o.initState;
        this.element.css({
            textAlign: 'center'
        });
        this.buttonGroup = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            items: [],
            layouts: [{
                type: "bi.float_center_adapt"
            }]
        });
        if (o.direction === "left") {
            this.buttonGroup.populate([this._createLeftCanvasButton(this.currentState)])
        }
        if (o.direction === "right") {
            this.buttonGroup.populate([this._createRightCanvasButton(this.currentState)])
        }
    },

    _getColor: function (state) {
        if (state) {
            return "#4d4d4d"
        } else {
            return "#c4c6c6"
        }
    },

    _createLeftCanvasButton: function (state) {
        var canvas = BI.createWidget({
            type: "bi.canvas",
            height: 26,
            width: 14
        });
        canvas.line(14, 0, 0, 13, 14, 26, {
            strokeStyle: this._getColor(state),
            lineWidth: 1
        });
        canvas.stroke();
        return canvas
    },

    _createRightCanvasButton: function (state) {
        var canvas = BI.createWidget({
            type: "bi.canvas",
            height: 26,
            width: 14
        });
        canvas.line(0, 0, 14, 13, 0, 26, {
            strokeStyle: this._getColor(state),
            lineWidth: 1
        });
        canvas.stroke();
        return canvas
    },

    setState: function (state) {
        var o = this.options;
        if (o.direction === "left") {
            this.buttonGroup.populate([this._createLeftCanvasButton(state)])

        }
        if (o.direction === "right") {
            this.buttonGroup.populate([this._createRightCanvasButton(state)])
        }
        this.setEnable(state)
    },

    doClick: function () {
        BI.GlobalStyleCanvasButton.superclass.doClick.apply(this, arguments);
        if (this.isValid()) {
            this.fireEvent(BI.GlobalStyleCanvasButton.EVENT_CHANGE, this);
        }
    }
});
BI.GlobalStyleCanvasButton.EVENT_CHANGE = "BI.GlobalStyleCanvasButton.EVENT_CHANGE";
$.shortcut("bi.global_style_canvas_button", BI.GlobalStyleCanvasButton);