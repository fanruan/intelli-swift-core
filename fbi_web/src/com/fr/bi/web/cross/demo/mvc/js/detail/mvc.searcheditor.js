SearchEditorView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(SearchEditorView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-search-editor bi-mvc-layout"
        })
    },

    _init: function(){
        SearchEditorView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 30,
            vgap: 20,
            items: [{
                el: {
                    type: "bi.search_editor",
                    width: 200
                }
            }, {
                el: {
                    type: "bi.small_search_editor",
                    width: 200
                }
            }]
        })
    }
});

SearchEditorModel = BI.inherit(BI.Model, {

});