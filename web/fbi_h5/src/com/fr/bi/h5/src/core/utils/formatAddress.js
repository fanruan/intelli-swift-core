/**
 * 格式化web url
 * Created by Young's on 2016/10/24.
 */
export default function formatAddress(address) {
    var temp = '';
    var url1 = /[a-zA-z]+:\/\/[^\s]*/;
    var url2 = /\/[^\s]*/;
    if (address.match(url1) || address.match(url2)) {
        temp = address;
    } else if (address !== "") {
        temp = "http://" + address;
    }
    return temp;
}
