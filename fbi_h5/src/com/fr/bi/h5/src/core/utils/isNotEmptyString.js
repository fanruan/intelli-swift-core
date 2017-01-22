import isString from 'lodash/isString'
export default function (str) {
    return isString(str) && str !== '';
}