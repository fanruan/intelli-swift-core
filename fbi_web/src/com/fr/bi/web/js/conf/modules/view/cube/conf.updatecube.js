/**
 * @class BIConf.UpdateCubePaneView
 * @extend BI.View
 * Cube更新界面
 */
BIConf.UpdateCubePaneView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BIConf.UpdateCubePaneView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-update-cube-pane"
        })
    },

    _init: function () {
        BIConf.UpdateCubePaneView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
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

    _buildImmediateButton: function () {
        var self = this;
        this.immediateButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Immediate_Update_DataBase"),
            height: 28,
            handler: function () {
                self.immediateButton.setEnable(false);
                self.immediateButton.setText(BI.i18nText("BI-Cube_is_Generating"));
                self.cubeLog.refreshLog(true);
                self.model.set("immediateUpdate", true);
            }
        });

        return BI.createWidget({
            type: "bi.left",
            items: [this.immediateButton],
            height: 30
        })
    },

    _checkCubeStatus: function () {
        var self = this;
        this.update({
            noset: true,
            success: function (data) {
                var isGenerating = data.isGenerating;
                if (isGenerating === false) {
                    self.immediateButton.setEnable(true);
                    self.immediateButton.setText(BI.i18nText("BI-Immediate_Update_DataBase"));
                } else {
                    self.immediateButton.setEnable(false);
                    self.immediateButton.setText(BI.i18nText("BI-Cube_is_Generating"));
                }
            }
        });
    },

    _createCheckInterval: function () {
        var self = this;
        this.interval = setInterval(function () {
            self._checkCubeStatus();
        }, 2000)
    },

    _buildTimeSetting: function () {
        return BI.createWidget({
            type: "bi.global_update_setting"
        })
    },

    _buildLog: function () {
        return this.cubeLog = BI.createWidget({
            type: "bi.cube_log"
        })

    },

    refresh: function () {
        this._checkCubeStatus();
        this._createCheckInterval();
    },

    local: function () {
        if (this.model.has("immediateUpdate")) {
            this.model.get("immediateUpdate");
            return true;
        }
        return false;
    }
});
