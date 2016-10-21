/**
 * Created by windy on 2016/10/20.
 */
export default function isNumeric (obj) {
    var type = jQuery.type(obj);
    return ( type === "number" || type === "string" ) && !isNaN(obj - parseFloat(obj));
}