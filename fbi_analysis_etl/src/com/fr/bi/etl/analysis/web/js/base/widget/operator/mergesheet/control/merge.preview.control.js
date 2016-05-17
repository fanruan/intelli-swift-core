BI.AnalysisETLMergeSheetPreviewController    = BI.inherit(BI.MVCController, {


    populate : function (widget, model) {
        var left = model.get("left")
        if(BI.isNotNull(left)) {
            widget.lefttable.setText(left.table_name)
            BI.Utils.buildData(left, function (left_data) {
                left_data.push(BI.range(0, left[ETLCst.FIELDS].length))
                left_data.push([])
                widget.left.populate.apply(widget.left, left_data);
            });

        }
        var right = model.get("right")
        if(BI.isNotNull(right)) {
            widget.righttable.setText(right.table_name)
            BI.Utils.buildData(right, function (right_data) {
                right_data.push([])
                right_data.push([])
                widget.right.populate.apply(widget.right, right_data);
            });

        }
        var merge = model.get("merge")
        if(BI.isNotNull(merge)) {
            BI.Utils.buildData(merge, function (merge_data) {
                merge_data.push(merge["leftColumns"])
                merge_data.push(merge["mergeColumns"])
                widget.merge.populate.apply(widget.merge, merge_data);
            });
        }

    }

})