BI.AnalysisETLMergeSheetPreviewController    = BI.inherit(BI.MVCController, {


    populate : function (widget, model) {
        var left = model.get("left")
        if(BI.isNotNull(left)) {
            widget.lefttable.setText(left.table_name)
            var left_data = BI.Utils.buildData(left);
            left_data.push(BI.range(0, left[ETLCst.FIELDS].length))
            left_data.push([])
            widget.left.populate.apply(widget.left, left_data);
        }
        var right = model.get("right")
        if(BI.isNotNull(right)) {
            widget.righttable.setText(right.table_name)
            var right_data =  BI.Utils.buildData(right);
            right_data.push([])
            right_data.push([])
            widget.right.populate.apply(widget.right, right_data);
        }
        var merge = model.get("merge")
        if(BI.isNotNull(merge)) {
            var merge_data = BI.Utils.buildData(merge);
            merge_data.push(merge["leftColumns"])
            merge_data.push(merge["mergeColumns"])
            widget.merge.populate.apply(widget.merge, merge_data);
        }

    }

})