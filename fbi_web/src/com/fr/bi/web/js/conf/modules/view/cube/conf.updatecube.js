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
        /*监测cube状态走和生成cube走得是两个Action，会出现这么一个bug：在task还没add进入container的这个阶段，checkStatus返回是false，此时按钮会结束灰化，为了避免该bug，加入taskAdding来标识该阶段*/
        this.taskAdding=false;
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
                // self.model.set("immediateUpdate", true);
                self.taskAdding = true;
                BI.Utils.generateCube(function (data) {
                    self.taskAdding = false;
                })
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
                if (isGenerating === false && self.taskAdding ==false) {
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
        }, 1000)
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
