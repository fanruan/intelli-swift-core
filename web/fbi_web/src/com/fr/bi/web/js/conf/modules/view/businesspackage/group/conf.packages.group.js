/**
 * Created by roy on 15/11/24.
 */
BIConf.BusinessPackageGroupView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BIConf.BusinessPackageGroupView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-business-package-group-view"
        })
    },

    _init: function () {
        BIConf.BusinessPackageGroupView.superclass._init.apply(this, arguments);
    },
    _render: function (vessel) {
        var self = this;
        this.groupPane = BI.createWidget({
            type: "bi.business_package_group"
        });

        this.groupPane.on(BI.BusinessPackageGroup.EVENT_CHANGE, function () {
            self.model.set("groups", self.groupPane.getValue());
            self.notifyParentEnd();
        });

        this.groupPane.on(BI.BusinessPackageGroup.EVENT_CANCEL, function () {
            self.notifyParentEnd();
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: self.groupPane,
                right: 0,
                top: -35,
                left: 0,
                bottom: 0
            }
            ]

        });

    },


    change: function (changed) {
        var self = this;
        if (BI.isNotNull(changed.groups)) {
            self.model.update({
                data: {
                    groups: self.model.get("groups")
                },
                complete: function () {
                    self.notifyParent();
                }
            })
        }
    },

    load: function () {
        this.groupPane.populate();
    },

    refresh: function () {
        this.readData(true);
    }


});