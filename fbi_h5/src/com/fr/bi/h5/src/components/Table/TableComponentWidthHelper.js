import {each, sum} from 'core';

const REMAIN_WIDTH = 8;

function sumBy(array, it) {
    let res = 0;
    each(array, (width, i)=> {
        res += it(width, i);
    });
    return res;
}

class TableComponentWidthHelper {
    constructor(helper, width) {
        this.width = width;
        this.helper = helper;
        this.header = helper.getHeader();
        this.items = [];
        each(this.header, (item, i)=> {
            this.items[i] = [item];
        });
    }

    setItems(items) {
        if (items.length > 0) {
            this.items = [];
            each(this.header, (item, i)=> {
                this.items[i] = [this.header[i]].concat(items[i]);
            });
        }
    }

    fit(widths) {
        if (widths.length < 2) {
            return {a: widths[0], b: 0};
        }
        const $11 = widths.length;
        const $12 = (1 + widths.length) * widths.length / 2;
        const $21 = $12;
        const $22 = sumBy(widths, (width, i)=> {
            return (i + 1) * (i + 1);
        });
        const f1 = sum(widths);
        const f2 = sumBy(widths, (width, i)=> {
            return (i + 1) * width;
        });
        return {
            a: (f2 * $12 - f1 * $22) / ($12 * $21 - $11 * $22),
            b: (f2 * $11 - f1 * $21) / ($11 * $22 - $21 * $12)
        }
    }

    getGBWidth(str) {
        str = str + '';
        str = str.replace(/[^\x00-\xff]/g, 'xx');
        return Math.ceil(str.length/2);
    }

    getWidthsByOneCol(col) {
        const widths = [];
        each(col, (item)=> {
            widths.push(this.getGBWidth(item.text));
        });
        return widths;
    }

    adjustWidth(widths) {
        if (widths.length < 1) {
            return [];
        }
        if (widths.length === 1) {
            return [this.width];
        }
        const allWidth = sum(widths);
        if (this.helper.isFreeze()) {
            const halfWidth = Math.floor(this.width / 2);
            if (widths[0] > halfWidth) {
                if (allWidth + halfWidth - widths[0] < this.width) {
                    const shared = Math.floor((halfWidth - (allWidth - widths[0] / (widths.length - 1))));
                    for (let i = 1; i < widths.length; i++) {
                        widths[i] += shared;
                    }
                    //把偏差加到最后一列
                    widths[widths.length - 1] += (halfWidth - (allWidth - widths[0])) - (shared * widths.length - 1);
                }
                widths[0] = halfWidth;
            } else {
                if (allWidth < this.width) {
                    const shared = Math.floor((this.width - allWidth) / widths.length);
                    for (let i = 0; i < widths.length; i++) {
                        widths[i] += shared;
                    }
                    if (widths[0] > halfWidth) {
                        widths[widths.length - 1] += widths[0] - halfWidth;
                        widths[0] = halfWidth;
                    }
                    widths[widths.length - 1] += this.width - allWidth - shared * widths.length;
                }
            }
        } else {
            if (allWidth < this.width) {
                const shared = Math.floor((this.width - allWidth) / widths.length);
                for (let i = 0; i < widths.length; i++) {
                    widths[i] += shared;
                }
                widths[widths.length - 1] += this.width - allWidth - shared * widths.length;
            }
        }
        return widths;
    }

    getWidth() {
        const result = [];
        each(this.items, (col)=> {
            result.push(Math.ceil(this.fit(this.getWidthsByOneCol(col)).a * 14 * 1.2) + REMAIN_WIDTH);
        });
        return this.adjustWidth(result);
    }
}


export default TableComponentWidthHelper
