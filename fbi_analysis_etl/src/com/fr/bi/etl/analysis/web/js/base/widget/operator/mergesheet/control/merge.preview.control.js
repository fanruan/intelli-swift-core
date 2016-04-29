BI.AnalysisETLMergeSheetPreviewController    = BI.inherit(BI.MVCController, {


    populate : function (widget, model) {
        var left = model.get("left")
        if(BI.isNotNull(left)) {
            widget.lefttable.setText(left.table_name)
            var leftFields = BI.Utils.getFieldArrayFromTable(left)
            var left_data = this._buildData(leftFields);
            left_data.push(BI.range(0, leftFields.length))
            left_data.push([])
            widget.left.populate.apply(widget.left, left_data);
        }
        var right = model.get("right")
        if(BI.isNotNull(right)) {
            widget.righttable.setText(right.table_name)
            var rightFields = BI.Utils.getFieldArrayFromTable(right)
            var right_data = this._buildData(rightFields);
            right_data.push([])
            right_data.push([])
            widget.right.populate.apply(widget.right, right_data);
        }
        var merge = model.get("merge")
        if(BI.isNotNull(merge)) {
            var mergeFields = BI.Utils.getFieldArrayFromTable(merge);
            var merge_data = this._buildData(mergeFields);
            merge_data.push(merge["leftColumns"])
            merge_data.push(merge["mergeColumns"])
            widget.merge.populate.apply(widget.merge, merge_data);
        }

    },


    _buildData : function(fields) {
        //测试数据
        var header = [];
        var items = [];
        BI.each(fields, function(idx, item){
            header.push({text:item.field_name});
            BI.each(BI.range(0 ,10), function(i){
                if(BI.isNull(items[i])){
                    items[i] = [];
                }
                items[i].push({text:"row:"+i +" column:" +idx})
            })

        })
        return [items, header];
    }

})