/**
 * @class BIConf.UpdateCubePaneView
 * @extend BI.View
 * Cube更新界面
 */
BIConf.UpdateCubePaneView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(BIConf.UpdateCubePaneView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-update-cube-pane"
        })
    },

    _init: function(){
        BIConf.UpdateCubePaneView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [{
                type: "bi.cube_path"
            },
                this._buildImmediateButton(),
                this._buildTimeSetting(),
                this._buildLog()
            ],
            hgap: 20,
            vgap: 20
        })
    },

    _buildImmediateButton: function(){
        var self = this;
        this.immediateButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Immediate_Update_DataBase"),
            height: 28,
            handler: function(){
                self.model.set("immediateUpdate", true);
                self.immediateButton.setEnable(false);
                self.immediateButton.setText(BI.i18nText("BI-Cube_is_genarating"));
                self.notifyLabel.setVisible(false);
                var cubeStatus = setInterval(function(){
                    self.update({
                        noset: true,
                        success: function(data){
                            self._updateComplete(cubeStatus, data);
                        }
                    });
                }, 5000);
            }
        });
        this.notifyLabel = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Table_Self_Update"),
            cls: "notify-immediate",
            height: 30,
            textAlign: "left"
        });

        return BI.createWidget({
            type: "bi.left",
            items: [ this.immediateButton, this.notifyLabel],
            height: 30
        })
    },

    _updateComplete: function(cubeStatus, data){
        var self = this;
        var needCheck = data['hasCheck'];
        var isAlling = data['isAlling'];
        var isChecking = data['isChecking'];
        var isSingleing = data['isSingleing'];
        if( BI.isNotNull(needCheck) ){
            self.notifyLabel.setText( BI.i18nText("BI-Table_Check_update") );
            if( !isAlling && !isChecking && !isSingleing ){
                clearInterval(cubeStatus);
                self.immediateButton.setEnable(true);
                self.immediateButton.setText(BI.i18nText("BI-Immediate_Update_DataBase"));
                self.notifyLabel.setVisible(true);
            }
        } else {
            if( BI.isNotNull(isAlling) || BI.isNotNull(isChecking) || BI.isNotNull(isSingleing) ){
                self.immediateButton.setEnable( false );
                self.notifyLabel.setVisible(false);
            } else {
                self.notifyLabel.setText(BI.i18nText("BI-Table_Self_Update"));
                self.immediateButton.setEnable( true );
                self.immediateButton.setText(BI.i18nText("BI-Immediate_Update_DataBase"));
                self.notifyLabel.setVisible( true );
                clearInterval(cubeStatus);
            }
        }
    },

    _buildTimeSetting: function(){
        return BI.createWidget({
           type: "bi.global_update_setting"
        })
    },

    _buildLog: function(){
        return BI.createWidget({
            type: "bi.cube_log"
        })
    },

    refresh: function(){
        var self = this;
        this.update({
            noset: true,
            success: function(data){
                self._updateComplete({}, data);
            }
        });
    },

    local: function(){
        if(this.model.has("immediateUpdate")){
            this.model.get("immediateUpdate");
            return true;
        }
        return false;
    }
});