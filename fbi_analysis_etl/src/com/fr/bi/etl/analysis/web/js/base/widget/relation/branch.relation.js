BI.ETLBranchRelation = BI.inherit(BI.BranchRelation, {

    _defaultConfig: function () {
        return BI.extend(BI.ETLBranchRelation.superclass._defaultConfig.apply(this, arguments), {
            vgap:40,
            hgap:60
        })
    },

    populate : function (items) {
        var maxWidth = 0;
        var maxHeight = 0;
        var o = this.options;
        BI.each(items, function (idx, item) {
            item.el = BI.createWidget(item.el)
            var width = item.el.getWidth();
            if(BI.isNotNull(width)) {
                maxWidth = Math.max(maxWidth, width);
            }
            var height = item.el.getHeight();
            if(BI.isNotNull(height)) {
                maxHeight = Math.max(maxHeight, height);
            }
        })
        if(maxWidth === 0) {
            maxWidth = 90
        }
        if(maxHeight === 0) {
            maxHeight = 30
        }
        maxHeight += o.vgap;
        maxWidth += o.hgap;
        items = BI.map(items, function (idx, item) {
            return BI.extend(item, {
                el : {
                    type:"bi.center_adapt",
                    items:[item.el],
                    height:maxHeight,
                    width:maxWidth
                },
                height:maxHeight,
                width:maxWidth
            })
        })

        BI.ETLBranchRelation.superclass.populate.apply(this, [items]);
    }
});
$.shortcut("bi.etl_branch_relation", BI.ETLBranchRelation)