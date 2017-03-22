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
        this._buildDriver();

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

    _buildDriver: function () {
        var self = this;
        this.driver = BI.createWidget({
            type: "bi.deal_with_cube_log_data_driver"
        });

        this.driver.on(BI.DealWithCubeLogDataDriver.EVENT_CUBE_UPDATING, function () {
            self._setImmediateButtonStatus(false);
        });

        this.driver.on(BI.DealWithCubeLogDataDriver.EVENT_CUBE_UPDATE_COMPLETE, function () {
            self._setImmediateButtonStatus(true);
            self.cubeLog.setEnd();
        })
    },

    _buildImmediateButton: function () {
        var self = this;
        this.immediateButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Immediate_Update_DataBase"),
            height: 28,
            handler: function () {
                self._setImmediateButtonStatus(false);
                self.cubeLog.setStart();
                BI.Utils.generateCube(function (data) {
                    if (data.result) {
                        self.driver.populate();
                    } else {
                        self.cubeLog.setEnd();
                    }
                });
            }
        });

        return BI.createWidget({
            type: "bi.left",
            items: [this.immediateButton, {
                type: "bi.label",
                height: 28,
                lgap: 5,
                cls: "table-self-update-tip",
                text: BI.i18nText("BI-Table_Self_Update")
            }],
            height: 30
        })
    },

    _buildTimeSetting: function () {
        return BI.createWidget({
            type: "bi.global_update_setting"
        })
    },

    _buildLog: function () {
        return this.cubeLog = BI.createWidget({
            type: "bi.cube_log",
            driver: this.driver
        })

    },

    _setImmediateButtonStatus: function (isAvailable) {
        var self = this;
        if (isAvailable) {
            self.immediateButton.setEnable(true);
            self.immediateButton.setText(BI.i18nText("BI-Immediate_Update_DataBase"));
        } else {
            self.immediateButton.setEnable(false);
            self.immediateButton.setText(BI.i18nText("BI-Cube_is_Generating"));
        }
    },

    refresh: function () {
        this.driver.populate();
    },

    local: function () {
        if (this.model.has("immediateUpdate")) {
            this.model.get("immediateUpdate");
            return true;
        }
        return false;
    }
});
