/**
 * Created by roy on 16/1/28.
 */
BI.CustomGroupSearcherView = BI.inherit(BI.Pane, {
    _defaultConfig: function () {
        var conf = BI.CustomGroupSearcherView.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-custom-group-searcher-view",
        })
    },

    _init: function () {
        var self = this, o = this.options;
        BI.CustomGroupSearcherView.superclass._init.apply(this, arguments);
        this.searcher = o.searcher;
        this.searcher.on(BI.Controller.EVENT_CHANGE, function (type, val, ob) {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
            if (type === BI.Events.CLICK) {
                self.fireEvent(BI.CustomGroupSearcherView.EVENT_CHANGE, val, ob);
            }
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.searcher,
                top: 0,
                bottom: 0,
                right: 0,
                left: 0
            }]
        });
    },

    _assertTip: function () {
        var o = this.options;
        if (!this._tipText) {
            this._tipText = BI.createWidget({
                type: "bi.label",
                cls: "bi-tips",
                text: o.tipText,
                height: 25
            });
            BI.createWidget({
                type: "bi.absolute",
                element: this.element,
                items: [{
                    el: this._tipText,
                    top: 0,
                    bottom: 0,
                    left: 0,
                    right: 0
                }]
            });
        }
    },

    setTipVisible: function (b) {
        if (b === true) {
            this._assertTip();
            this._tipText.setVisible(true);
        } else {
            this._tipText && this._tipText.setVisible(false);
        }
    },

    populate: function (searchResult, keyword, selectedIDs) {
        var self = this;
        searchResult || (searchResult = []);
        this.setTipVisible(searchResult.length === 0);
        this.searcher.populate(searchResult, selectedIDs, keyword);
    },

    empty: function () {
        this.searcher.empty();
    }
});
BI.CustomGroupSearcherView.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.etl_add_group_field_custom_group_searcher_view", BI.CustomGroupSearcherView);