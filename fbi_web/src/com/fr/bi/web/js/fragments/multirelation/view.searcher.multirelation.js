/**
 * Created by roy on 16/2/23.
 */
BI.MultiRelationSearcherView = BI.inherit(BI.Pane, {
    _defaultConfig: function () {
        return BI.extend(BI.MultiRelationSearcherView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-multi-relation-searcher-view"
        })
    },

    _init: function () {
        var self = this, o = this.options;
        BI.MultiRelationSearcherView.superclass._init.apply(this, arguments);
        this.searcher = o.searcher;
        this.searcher.on(BI.Controller.EVENT_CHANGE, function (type, val, ob) {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
            if (type === BI.Events.CLICK) {
                self.fireEvent(BI.MultiRelationSearcherView.EVENT_CHANGE, val, ob);
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

    populate: function (searchResult, selectedResult, keyword) {
        searchResult || (searchResult = []);
        this.setTipVisible(searchResult.length === 0);
        this.searcher.populate(searchResult, selectedResult, keyword);

    },

    empty: function () {
        this.searcher.empty();
    }

});
BI.MultiRelationSearcherView.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.multi_relation_searcher_view", BI.MultiRelationSearcherView);