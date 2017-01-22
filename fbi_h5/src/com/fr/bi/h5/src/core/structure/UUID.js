var f = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'];
export default function () {
    var str = "";
    for (var i = 0; i < 16; i++) {
        var r = parseInt(f.length * Math.random(), 10);
        str += f[r];
    }
    return str;
}