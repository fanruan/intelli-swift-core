/**
 * Created by Young's on 2016/3/19.
 */
BI.CubePathConfirm = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend(BI.CubePathConfirm.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-path-confirm"
        })
    },

    _init: function(){
        BI.CubePathConfirm.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function(north){
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Cube_Path_Modi_Hint"),
            height: 50
        })
    },

    rebuildCenter: function(center){
        BI.createWidget({
            type: "bi.htape",
            element: center,
            items: [{
                el: {
                    type: "bi.icon",
                    cls: "cube-path-confirm-icon cube-path-confirm-font",
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
                        cls: "cube-path-warning-tip",
                        height: 30
                    }, {
                        type: "bi.left",
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Cube_Path_Cancel_Tip"),
                            height: 30,
                            cls: "cube-path-normal-tip"
                        }, {
                            type: "bi.label",
                            text: BI.i18nText("BI-Cube_Path_OK_Tip"),
                            height: 30,
                            cls: "cube-path-normal-tip"
                        }]
                    }]
                },
                width: "fill"
            }]
        })
    },

    end: function(){
        BI.CubePathConfirm.superclass.end.apply(this, arguments);
        this.fireEvent(BI.CubePathConfirm.EVENT_SAVE);
    }
});
BI.CubePathConfirm.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.cube_path_confirm", BI.CubePathConfirm);