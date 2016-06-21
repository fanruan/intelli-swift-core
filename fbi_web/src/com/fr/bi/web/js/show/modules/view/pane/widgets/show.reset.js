/**
 * Created by Young's on 2016/5/5.
 */
BIShow.ResetView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(BIShow.ResetView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-widget"
        })
    },

    _init: function(){
        BIShow.ResetView.superclass._init.apply(this, arguments);
    },

    _render: function(veseel){
        var self = this;
        var resetButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Reset"),
            width: "100%",
            height: 30
        });
        resetButton.on(BI.Button.EVENT_CHANGE, function(){
            self._resetAllControlValues();
        });
        var deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-font remove-button",
            width: 20,
            height: 20
        });
        deleteButton.on(BI.IconButton.EVENT_CHANGE, function(){
            self.model.destroy(); 
        });
        deleteButton.setVisible(false);
        BI.createWidget({
            type: "bi.absolute",
            element: veseel,
            items: [{
                el: resetButton,
                left: 0,
                right: 0,
                top: 8
            }, {
                el: deleteButton,
                right: 5,
                top: 13
            }]
        });
        veseel.hover(function(){
            deleteButton.setVisible(true);
        }, function(){
            deleteButton.setVisible(false);
        });
    },

    _resetAllControlValues: function(){
        BI.each(BI.Utils.getAllWidgetIDs(), function(i, wid){
             BI.Broadcasts.send(BICst.BROADCAST.RESET_PREFIX + wid);
        });
        BI.Utils.broadcastAllWidgets2Refresh(true);
    }
});