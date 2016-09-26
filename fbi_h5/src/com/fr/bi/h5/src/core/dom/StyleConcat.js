function StylesConcat() {
    var styles = {};

    for (var i = 0; i < arguments.length; i++) {
        var arg = arguments[i];
        if (!arg) continue;
        if (arg[1]) {
            styles = {...styles, ...arg[0]};
        }
    }

    return styles;
}

export default StylesConcat