/**
 * Created by wuk on 16/4/25.
 */
BI.AuthorityButton = BI.inherit(BI.BasicButton, {
    _defaultConfig: function(){
        var conf = BI.AuthorityButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-database-table",
            connName: ""
        })
    },

    _init: function(){
        BI.AuthorityButton.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.tableNameText = BI.createWidget({
            type: "bi.text_button",
            text: o.text,
            title: o.text,
            value: o.value
        });
        BI.createWidget({
            type: "bi.center_adapt",
            element: this.element,
            items: [this.tableNameText],
            height: 30,
            hgap: 5
        });
    }

});
$.shortcut("bi.authority_button", BI.AuthorityButton);
