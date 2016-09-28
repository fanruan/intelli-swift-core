/**
 * Created by zcf on 2016/9/22.
 */
BI.Track = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.Track.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-slider-track"
        });
    },
    _init: function () {
        BI.extend(BI.Track.superclass._init.apply(this, arguments));
        this.backgroundTrack = BI.createWidget({
            type: "bi.layout",
            cls: "background-track",
            height: 23
        });
        this.blueTrack = BI.createWidget({
            type: "bi.layout",
            cls: "blue-track",
            height: 9
        });
        this.grayTrack = BI.createWidget({
            type: "bi.layout",
            cls: "gray-track",
            height: 9
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.backgroundTrack,
                width: "100%"
            }, {
                el: {
                    type: "bi.vertical",
                    items: [{
                        type: "bi.absolute",
                        items: [{
                            el: this.grayTrack,
                            top: 0,
                            left: 0,
                            width: "100%"
                        }, {
                            el: this.blueTrack,
                            top: 0,
                            left: 0,
                            width: "0%"
                        }]
                    }],
                    hgap: 7,
                    height: 9
                },
                top: 7,
                left: 0,
                width: "100%"
            }]
        })
    },
    getLength: function () {
        return this.backgroundTrack.element[0].scrollWidth
    },
    reset: function () {
        this.blueTrack.element.css({"width": "0%"})
    },
    setBlueTrackWidth: function (percent) {
        this.blueTrack.element.css({"width": percent + "%"});
    },
    setBlueTrackLeft: function (percent) {
        this.blueTrack.element.css({"left": percent + "%"});
    }
});
$.shortcut("bi.track", BI.Track);