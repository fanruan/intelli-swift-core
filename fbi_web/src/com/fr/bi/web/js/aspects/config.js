BI.Plugin.registerObject("bi.button", function (button) {
    BI.aspect.before(button, "doClick", function () {
        BI.Msg.toast("AOP Test");
    })
});