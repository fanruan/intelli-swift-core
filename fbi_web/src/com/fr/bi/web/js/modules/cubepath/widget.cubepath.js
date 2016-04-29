/**
 * Created by Young's on 2016/3/19.
 */
BI.CubePath = BI.inherit(BI.Widget, {

    constants: {
        PATH_WIDTH: 320,
        BUTTON_HEIGHT: 28,
        BUTTON_WIDTH: 90
    },

    _defaultConfig: function(){
        return BI.extend(BI.CubePath.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-path"
        })
    },

    _init: function(){
        BI.CubePath.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.path = "";
        var pathLabel = BI.createWidget({
            type: "bi.label",
            cls: "cube-label",
            text: o.path,
            height: this.constants.BUTTON_HEIGHT,
            width: this.constants.PATH_WIDTH
        });
        var pathInput = BI.createWidget({
            type: "bi.sign_editor",
            text: o.path,
            height: this.constants.BUTTON_HEIGHT,
            width: this.constants.PATH_WIDTH
        });
        pathInput.setVisible(false);
        var modifyButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Modify"),
            height: this.constants.BUTTON_HEIGHT,
            width: this.constants.BUTTON_WIDTH
        });
        modifyButton.on(BI.Button.EVENT_CHANGE, function(){
            if(cancelButton.isVisible()){
                BI.Utils.checkCubePath(pathInput.getValue(), function(res){
                    if(BI.isEmptyString(res)){
                        //错误的路径
                        BI.Msg.alert();
                    } else {
                        self.path = res;
                        var id = BI.UUID();
                        var confirm = BI.createWidget({
                            type: "bi.cube_path_confirm"
                        });
                        BI.Popovers.create(id, confirm, {width: 500, height: 300}).open(id);
                        confirm.on(BI.CubePathConfirm.EVENT_SAVE, function(){

                        });
                    }
                });
            } else {
                this.setText(BI.i18nText("BI-OK"));
                cancelButton.setVisible(true);
                tipLabel.setVisible(true);
                pathInput.setVisible(true);
                pathLabel.setVisible(false);
            }
        });
        var cancelButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Cancel"),
            height: this.constants.BUTTON_HEIGHT,
            width: this.constants.BUTTON_WIDTH
        });
        cancelButton.on(BI.Button.EVENT_CHANGE, function(){
            pathLabel.setVisible(true);
            pathInput.setVisible(false);
            modifyButton.setText(BI.i18nText("BI-Modify"));
            this.setVisible(false);
            tipLabel.setVisible(false);
        });
        cancelButton.setVisible(false);
        var tipLabel = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Cube_Path_Format_Hit"),
            cls: "cube-label",
            height: this.constants.BUTTON_HEIGHT
        });
        tipLabel.setVisible(false);
        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Cube_Path"),
                cls: "cube-label",
                height: this.constants.BUTTON_HEIGHT
            }, pathLabel, pathInput, modifyButton, cancelButton, tipLabel],
            hgap: 5
        });
        BI.Utils.getCubePath(function(path){
            self.path = path;
            pathLabel.setText(path);
            pathInput.setValue(path);
        })
    }
});
$.shortcut("bi.cube_path", BI.CubePath);