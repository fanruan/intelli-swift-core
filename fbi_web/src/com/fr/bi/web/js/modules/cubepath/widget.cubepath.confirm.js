/**
 * Created by Young's on 2016/3/19.
 */
BI.CubePathConfirm = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function () {
        return BI.extend(BI.CubePathConfirm.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-path-confirm"
        })
    },

    _init: function () {
        BI.CubePathConfirm.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Cube_Path_Modi_Hint"),
            height: 50,
            textAlign: "left"
        })
    },

    rebuildCenter: function (center) {
        var o = this.options;
        var isWarning = o.is_warning;
        var notEmptyFile = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Cube_Path_File_Not_Empty_Warning"),
            cls: "cube-path-warning-tip",
            height: 30,
            textAlign: "left"
        });
        BI.createWidget({
            type: "bi.htape",
            element: center,
            cls: "bi-cube-path-confirm",
            items: [{
                el: {
                    type: "bi.center_adapt",
                    cls: "cube-path-confirm-icon cube-path-confirm-font",
                    items: [{
                        type: "bi.icon"
                    }],
                    width: 80,
                    height: 80
                },
                width: 80
            }, {
                el: {
                    type: "bi.vertical",
                    items: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Cube_Path_Completed"),
                        cls:  isWarning ? "cube-path-normal-tip" : "cube-path-warning-tip",
                        height: 30,
                        textAlign: "left"
                    }, {
                        el: isWarning ? notEmptyFile : BI.createWidget()
                    }, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Cube_Path_Cancel_Tip"),
                        height: 30,
                        cls: "cube-path-normal-tip",
                        textAlign: "left"
                    }, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Cube_Path_OK_Tip"),
                        height: 30,
                        cls: "cube-path-normal-tip",
                        textAlign: "left"
                    }]
                },
                width: "fill"
            }],
            vgap: 20
        })
    },

    end: function () {
        BI.CubePathConfirm.superclass.end.apply(this, arguments);
        this.fireEvent(BI.CubePathConfirm.EVENT_SAVE);
    }
});
BI.CubePathConfirm.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.cube_path_confirm", BI.CubePathConfirm);