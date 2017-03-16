BI.AnalysisETLMergeSheetPreviewController    = BI.inherit(BI.MVCController, {


    populate : function (widget, model) {
        var left = model.get("left")
        if(widget.first !== true) {
            if(BI.isNotNull(left)) {
                widget.lefttable.setText(left.table_name)
                BI.Utils.buildData(left, widget.left, function (left_data) {
                    left_data.push(BI.range(0, left[ETLCst.FIELDS].length))
                    left_data.push([])
                    widget.left.populate.apply(widget.left, left_data);
                });

            }
            var right = model.get("right")
            if(BI.isNotNull(right)) {
                widget.righttable.setText(right.table_name)
                BI.Utils.buildData(right, widget.right, function (right_data) {
                    right_data.push([])
                    right_data.push([])
                    widget.right.populate.apply(widget.right, right_data);
                });

            }
        }
        widget.first = true;
        var merge = model.get("merge")
        if(BI.isNotNull(merge)) {
            widget.mergeCard.showCardByName(widget._constants.NORMAL)
            BI.Utils.buildData(merge, widget.merge, function (merge_data) {
                merge_data.push(merge["leftColumns"])
                merge_data.push(merge["mergeColumns"])
                widget.merge.populate.apply(widget.merge, merge_data);
            });
        } else {
            widget.mergeCard.showCardByName(widget._constants.ERROR)
        }

    }

})