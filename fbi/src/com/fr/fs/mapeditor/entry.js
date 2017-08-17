$.extend(FS.Design.op, {
    20: function(container) {
        FS.FontEndMapEditorOperator.init(container)
    }
});
FS.FontEndMapEditorOperator = {
    init: function(rendererEle) {
        var f = $("<iframe>").css({
            border: 0,
            frameBorder: 0,
            width: "100%",
            height: "100%"
        }).appendTo(rendererEle);
        f.attr("src", FR.servletURL + "?op=map")
    }
};