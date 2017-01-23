/*!
 * jLayout - JavaScript Layout Algorithms v0.2
 *
 * Licensed under the revised BSD License.
 * Copyright 2008, Bram Stein
 * All rights reserved.
 */
/*global window.jLayout */
(function () {
    window.jLayout = {
        layout: function (spec, shared) {
            var that = {}, my = shared || {};

            my.hgap = spec.hgap || 0;
            my.vgap = spec.vgap || 0;

            /**
             * Lay out the container using a layout algorithm.
             */
            that.layout = function (container) {
                return container;
            };

            /**
             * Return the preferred size of the container.
             */
            that.preferred = function (container) {
                return {
                    width: container.width(),
                    height: container.height()
                };
            };

            /**
             * Return the minimum size the container is allowed to have.
             */
            that.minimum = function (container) {
                return {
                    width: 0,
                    height: 0
                };
            };

            /**
             * Return the maximum size the container is allowed to have.
             */
            that.maximum = function (container) {
                return {
                    width: Number.MAX_VALUE,
                    height: Number.MAX_VALUE
                };
            };

            return that;
        },

        parameter: function (spec) {
            var my = {},
                items = spec.items || [],
                that = this.layout(spec, my),
                scrollable = spec.scrollable,
                scrollContainer = spec.scrollContainer;
            that.layout = function (container) {
                var bounds = container.bounds(),
                    insets = container.insets(),
                    totalWidth = bounds.width - (insets.left + insets.right),
                    totalHeight = bounds.height - (insets.top + insets.bottom),
                    maxOffsetX = 0, maxOffsetY = 0;
                for (var i = 0; i < items.length; i ++) {
                    var item = items[i];
                    var width = (scrollable || totalWidth == 0) ? item.width : Math.min(item.width, totalWidth - item.x);
                    var height = (scrollable || totalHeight == 0) ? item.height : Math.min(item.height, totalHeight - item.y);
                    if (scrollable) {
                        maxOffsetX = Math.max(maxOffsetX, width + item.x);
                        maxOffsetY = Math.max(maxOffsetY, height + item.y);
                    }
                    item.el.bounds({
                        'x' : item.x,
                        'y' : item.y + (item.delta || 0),
                        'width' : width,
                        'height' : height
                    });
                    item.el.doLayout();
                }
                if (scrollContainer) {
                    scrollContainer.bounds({
                        'x' : 0,
                        'y' : 0,
                        width : maxOffsetX,
                        height : maxOffsetY
                    });
                }
            };

            function typeLayout(type) {
                return function () {
                    return {
                        "width": 0,
                        "height": 0
                    }
                };
            }

            that.preferred = typeLayout('preferred');
            that.minimum = typeLayout('minimum');
            that.maximum = typeLayout('maximum');

            return that;
        },

        flow: function (spec) {
            var my = {},
                that = this.layout(spec, my),
                items = spec.items || [],
                alignment = (spec.alignment && (spec.alignment === 'center' || spec.alignment === 'right' || spec.alignment === 'left') && spec.alignment) || 'left',
            hgap = typeof spec.hgap === 'number' && !isNaN(spec.hgap) ? spec.hgap : 5,
            vgap = typeof spec.vgap === 'number' && !isNaN(spec.vgap) ? spec.vgap : 5;
            that.layout = function (container) {
                var parentSize = container.bounds(),
                    insets = container.insets(),
                    i = 0,
                    len = items.length,
                    itemSize,
                    currentRow = [],
                    rowSize = {
                        width: 0,
                        height: 0
                    },
                    offset = {
                        x: insets.left,
                        y: insets.top
                    };

                parentSize.width -= insets.left + insets.right;
                parentSize.height -= insets.top + insets.bottom;

                for (; i < len; i += 1) {
                    if (items[i].el.isVisible()) {
                        itemSize = items[i].el.preferredSize();

                        if ((rowSize.width + itemSize.width) > parentSize.width) {
                            align(currentRow, offset, rowSize, parentSize);

                            currentRow = [];
                            offset.y += rowSize.height;
                            offset.x = insets.left;
                            rowSize.width = 0;
                            rowSize.height = 0;
                        }
                        rowSize.height = Math.max(rowSize.height, itemSize.height + vgap);
                        rowSize.width += itemSize.width + hgap;

                        currentRow.push(items[i]);
                    }
                }
                align(currentRow, offset, rowSize, parentSize);
                return container;
            };

            function align(row, offset, rowSize, parentSize) {
                var location = {
                        x: offset.x,
                        y: offset.y
                    },
                    i = 0,
                    len = row.length;

                switch (alignment) {
                    case 'center':
                    {
                        location.x += (hgap + parentSize.width - rowSize.width) / 2;
                        break;
                    }
                    case 'right':
                    {
                        location.x += parentSize.width - rowSize.width + hgap;
                        break;
                    }
                }

                for (; i < len; i += 1) {
                    location.y = offset.y;
                    row[i].el.bounds(location);
                    row[i].el.doLayout();
                    location.x += row[i].el.bounds().width + hgap;
                }
            }

            function typeLayout(type) {
                return function (container) {
                    var i = 0,
                        width = 0,
                        height = 0,
                        typeSize,
                        firstComponent = false,
                        insets = container.insets();

                    for (; i < items.length; i += 1) {
                        if (items[i].isVisible()) {
                            typeSize = items[i][type + 'Size']();
                            height = Math.max(height, typeSize.height);
                            width += typeSize.width;
                        }
                    }

                    return {
                        'width': width + insets.left + insets.right + (items.length - 1) * hgap,
                        'height': height + insets.top + insets.bottom
                    };
                };
            }

            that.preferred = typeLayout('preferred');
            that.minimum = typeLayout('minimum');
            that.maximum = typeLayout('maximum');

            return that;
        },
        /**
         * 水平布局，容器内的所有组件高度和容器一致，宽度根据自身的设置决定
         * 容器内的组件对齐方式可选为“靠左”、“靠右”以及“中间”的对齐位置
         * 默认：
         * 按中间对齐
         * 水平间隙为2px
         * 垂直间隙为0
         */
        horizontal: function (spec) {
            var my = {},
                that = this.layout(spec, my),
                items = spec.items || [],
                alignment = (spec.alignment && (spec.alignment === 'center' || spec.alignment === 'right' || spec.alignment === 'left') && spec.alignment) || 'center',
                hgap = typeof spec.hgap === 'number' && !isNaN(spec.hgap) ? spec.hgap : 2,
                vgap = typeof spec.vgap === 'number' && !isNaN(spec.vgap) ? spec.vgap : 0;
            that.layout = function (container) {
                var bounds = container.bounds(),
                    insets = container.insets(),
                    x = 0,
                    y = insets.top + vgap,
                    maxWidth = bounds.width - (insets.left + insets.right + hgap * 2),
                    totalWidth = 0,
                    totalHeight = bounds.height - (insets.top + insets.bottom),
                    eachHeight = totalHeight - 2 * vgap,
                    i,
                    len = items.length;
                for (i = 0; i < len; i++) {
                    var item = items[i];
                    // 只有可见的才参与布局
                    if (item.el.isVisible()) {
                        totalWidth += (item.width + hgap);
                    }
                }
                moveElements(x, y, maxWidth - totalWidth + 2 * hgap, eachHeight);
                return container;
            };
            function moveElements(x, y, deltaWidth, height) {
                var bounds = {
                        "x": x,
                        "y": y,
                        "width": deltaWidth,
                        "height": height
                    },
                    i = 0,
                    len = items.length;
                switch (alignment) {
                    case "left" :
                    {
                        bounds.x += 0;
                        break;
                    }
                    case 'center':
                    {
                        bounds.x += deltaWidth / 2;
                        break;
                    }
                    case 'right':
                    {
                        bounds.x += deltaWidth;
                        break;
                    }
                }
                for (; i < len; i++) {
                    var item = items[i];
                    if (item.el.isVisible()) {
                        bounds.width = item.width;
                        item.el.bounds(bounds);
                        item.el.doLayout();
                        bounds.x += (item.width + hgap);
                    }
                }
            }

            function typeLayout(type) {
                return function (container) {
                    var i = 0,
                        width = 0,
                        height = 0,
                        typeSize,
                        insets = container.insets();
                    for (; i < items.length; i += 1) {
                        if (items[i].el.isVisible()) {
                            typeSize = items[i].el[type + 'Size']();
                            height = Math.max(height, typeSize.height);
                            width += items[i].width + hgap;
                        }
                    }

                    return {
                        'width': width + insets.left + insets.right,
                        'height': height + insets.top + insets.bottom
                    }
                }
            }

            that.preferred = typeLayout('preferred');
            that.minimum = typeLayout('minimum');
            that.maximum = typeLayout('maximum');

            return that;
        },
        vertical: function (spec) {
            var my = {},
                that = this.layout(spec, my),
                items = spec.items || [],
                spec = spec,
                hgap = typeof spec.hgap === 'number' && !isNaN(spec.hgap) ? spec.hgap : 0,
                vgap = typeof spec.vgap === 'number' && !isNaN(spec.vgap) ? spec.vgap : 0;
            that.layout = function (container) {
                var bounds = container.bounds(),
                    insets = container.insets(),
                    i,
                    len = items.length,
                    x = insets.left + hgap,
                    y = insets.top + vgap,
                    maxHeight = bounds.height - (insets.top + insets.bottom + vgap * 2),
                    totalHeight = 0,
                    totalWidth = bounds.width - (insets.left + insets.right),
                    eachWidth = totalWidth - 2 * hgap;
                for (i = 0; i < len; i++) {
                    var item = items[i];
                    if (item.el.isVisible()) {
                        totalHeight += item.height;
                    }
                }
                moveElements(x, y, eachWidth, maxHeight - totalHeight);
                return container;
            };

            function moveElements(x, y, width, height) {
                var bounds = {
                        "x": x,
                        "y": y,
                        "width": width,
                        "height": height
                    },
                    i = 0,
                    len = items.length;
                for (; i < len; i++) {
                    var item = items[i];
                    if (item.el.isVisible()) {
                        bounds.height = item.height;
                        item.el.bounds(bounds);
                        item.el.doLayout();
                        bounds.y += item.height + vgap;
                    }
                }
            }

            function typeLayout(type) {
                return function (container) {
                    var i = 0,
                        width = 0,
                        height = 0,
                        typeSize,
                        insets = container.insets();
                    for (; i < items.length; i += 1) {
                        if (items[i].el.isVisible()) {
                            typeSize = items[i].el[type + 'Size']();
                            width = Math.max(width, typeSize.width);
                            height += items[i].height + vgap;
                        }
                    }

                    return {
                        'width': width + insets.left + insets.right,
                        'height': height + insets.top + insets.bottom
                    }
                };
            }

            that.preferred = typeLayout('preferred');
            that.minimum = typeLayout('minimum');
            that.maximum = typeLayout('maximum');

            return that;
        },
        /**
         * 格子布局
         */
        gridLayout: function (spec) {
            var my = {},
                that = this.layout(spec, my),
                leftGap = spec.leftGap || 0,
                rightGap = spec.rightGap || 0,
                topGap = spec.topGap || 0,
                bottomGap = spec.bottomGap || 0,
                items = spec.items || [],
                columns = spec.columns,
                rows = spec.rows,
                widths = spec.widths,
                heights = spec.heights;
            that.layout = function (container) {
                // richer:获取布局容器的边界
                var bounds = container.bounds();
                var insets = container.insets(),
                    totalWidth = bounds.width - (insets.left + insets.right) - leftGap - rightGap,
                    totalHeight = bounds.height - (insets.top + insets.bottom) - topGap - bottomGap;
                var realWidths = {}, setWidthTotal = 0, setWLen = 0;
                if (widths) {
                    for (var i = 0; i < widths.length; i ++) {
                        var wid = widths[i];
                        if (wid != 'auto') {
                            realWidths["" + i] = wid;
                            setWidthTotal += wid;
                            setWLen ++;
                        }
                    }
                }
                var realHeights = {}, setHeightTotal = 0, setHLen = 0;
                if (heights) {
                    for (var i = 0; i < heights.length; i ++) {
                        var he = heights["" + i];
                        if (he != 'auto') {
                            realHeights[i] = he;
                            setHeightTotal += he;
                            setHLen ++;
                        }
                    }
                }
                var eachWidth = (totalWidth - (columns - 1) * my.hgap - setWidthTotal) / (columns - setWLen),
                    eachHeight = (totalHeight - (rows - 1) * my.vgap - setHeightTotal) / (rows - setHLen);

                var sum = function(arr, average, index, gap) {
                    if (!arr) {
                        return index * (average + gap)
                    }
                    var result = 0;
                    for (var n = 0; n < index; n ++) {
                        var el = arr[n];
                        if (el == 'auto') {
                            result += average;
                        } else {
                            result += el;
                        }
                        if (n > 0) {
                            result += gap;
                        }
                    }
                    return result;
                };

                for (var k = 0; k < items.length; k++) {
                    var item = items[k];
                    var w = realWidths[item.column] || eachWidth, h = realHeights[item.row] || eachHeight;
                    var x = sum(widths, eachWidth, item.column, my.hgap);
                    var y = sum(heights, eachHeight, item.row, my.vgap);
                    if (item.el) {
                        item.el.bounds({
                            'x' : x + leftGap,
                            'y' : y + topGap,
                            'width' : w,
                            'height' : h
                        });
                        // richer:该$元素的大小和位置改变了,当然要doLayout一下,让它内部的子元素也重新布局
                        item.el.doLayout();
                    }
                }
                return container;
            };
            function typeLayout(type) {
                return function (container) {
                    if (type == 'minimum' || type == "maximum") {
                        return container[type.substring(0, 3) + "Size"]()
                    }
                    var bounds = container.bounds();
                    var insets = container.insets(),
                        totalWidth = bounds.width - (insets.left + insets.right),
                        totalHeight = bounds.height - (insets.top + insets.bottom),
                        eachWidth = totalWidth - (columns - 1) * my.hgap,
                        eachHeight = totalHeight - (rows - 1) * my.vgap;
                    return {
                        'width': eachWidth,
                        'height': eachHeight
                    };
                };
            }

            that.preferred = typeLayout('preferred');
            that.minimum = typeLayout('minimum');
            that.maximum = typeLayout('maximum');
            return that;
        },


        tableLayout : function(spec) {
            var my = {},
                that = this.layout(spec, my),
                vgap = spec.vgap,
                items = spec.items,
                columnSize = spec.columnSize,
                rowSize = spec.rowSize,
                scrollContainer = spec.scrollContainer;
            that.layout = function (container) {
                var bounds = container.bounds(),
                    insets = container.insets(),
                    totalWidth = bounds.width - (insets.left + insets.right),
                    totalHeight = bounds.height - (insets.top + insets.bottom),
                    fixHeight = 0,maxOffsetY = 0;
                for (var row = 0; row < items.length; row++) {
                    var rowItems = items[row];
                    if (isRowHide(rowItems)) {
                       fixHeight += (heightY(row, totalHeight) + vgap);
                    } else {
                        for (var column = 0; column < rowItems.length; column++) {
                            var item = rowItems[column];
                            var y = offsetY(row) - fixHeight;
                            var h = heightY(row, totalHeight);
                            maxOffsetY = Math.max(y + h, maxOffsetY);
                            if (item == null) {
                                continue;
                            }
                            var rect = {
                            	'x' : offsetX(column, totalWidth),
                                'y' : y
                           	};
                           	if(isNaN(item.el.width)) {
                           		//没有为el定义width，采用table的width
                           		rect.width = widthX(column, totalWidth);
                           	}
                           	if(isNaN(item.el.height)) {
                           		rect.height = h;
                           	}
                            item.element.bounds(rect);
                            item.element.doLayout();
                        }
                    }
                }
                if (scrollContainer) {
                    scrollContainer.bounds({
                        x : 0,
                        y : 0,
                        width : totalWidth,
                        height : maxOffsetY
                    });
                }
            };
            function isRowHide(rowItems) {
               for (var column = 0; column < rowItems.length; column++) {
                   if (rowItems[column] != null && rowItems[column].element.isVisible()) {
                       return false;
                   }
               }
                return true;
            }

            function offsetX(index, totalWidth) {
                var offsetX = 0;
                for (var i = 0; i < index; i ++) {
                    var cz = columnSize[i];
                    if (cz > 0 && cz < 1) {
                        cz = cz * totalWidth;
                    }
                    offsetX += cz;
                }
                return offsetX;
            }
            function offsetY(index) {
                var offsetY = 0;
                for (var i = 0; i < index; i ++) {
                    offsetY += rowSize[i] + vgap;
                }
                return offsetY;
            }

            function widthX(index, totalWidth) {
                var width = columnSize[index];
                if (!isNaN(width)) {
                    if (width > 0 && width < 1) {
                        return width * totalWidth;
                    }
                    return width;
                }
                // richie:如果不是数字的宽度不在最后一位，则忽略掉这个宽度
                if (index != columnSize.length - 1) {
                    return 0;
                }
                return totalWidth - offsetX(index, totalWidth);
            }

            function heightY(index, totalHeight) {
                var height = rowSize[index];
                if (!isNaN(height)) {
                    return height;
                }
                // richie:如果不是数字的高度不在最后一位，则忽略掉这个高度
                if (index != rowSize.length - 1) {
                    return 0;
                }
                return totalHeight - offsetY(index);
            }

            function typeLayout(type) {
                return function (container) {
                    return {
                        "width": 0,
                        "height": 0
                    }
                };
            }

            that.preferred = typeLayout('preferred');
            that.minimum = typeLayout('minimum');
            that.maximum = typeLayout('maximum');

            return that;
        },
        borderlayout : function(spec) {
            var my = {},
                that = this.layout(spec, my),
                east = spec.east,
                west = spec.west,
                north = spec.north,
                south = spec.south,
                center = spec.center;
            that.layout = function (container) {
                var bounds = container.bounds(),
                    insets = container.insets(),
                    top = insets.top,
                    left = insets.left,
                    totalWidth = bounds.width - (insets.left + insets.right),
                    totalHeight = bounds.height - (insets.top + insets.bottom);
                var offsetTop = 0;
                var offsetLeft = 0;
                var centerWidth = totalWidth;
                var centerHeight = totalHeight;
                if (north && north.el && north.el.isVisible()) {
                    var nh = north.height || north.el.height();
                    north.el.bounds({
                        'x' : left,
                        'y' : top,
                        'width' : totalWidth,
                        'height' : nh
                    });
                    offsetTop = top + nh;
                    centerHeight = totalHeight - nh;
                    north.el.doLayout();
                }

                if (south && south.el && south.el.isVisible()) {
                    var sh = south.height || south.el.height();
                    south.el.bounds({
                        'x' : left,
                        'y' : totalHeight - sh,
                        'width' : totalWidth,
                        'height' : sh
                    });
                    centerHeight -= sh;
                    south.el.doLayout();
                }
                if (west && west.el && west.el.isVisible()) {
                    var ww = west.width || west.el.width();
                    west.el.bounds({
                        'x' : left,
                        'y' : offsetTop,
                        'width' : ww,
                        'height' : centerHeight
                    });
                    offsetLeft = left + ww;
                    centerWidth = totalWidth - ww;
                    west.el.doLayout();
                }
                if (east && east.el && east.el.isVisible()) {
                    var ew = east.width || east.el.width();
                    east.el.bounds({
                        'x' : totalWidth - ew,
                        'y' : offsetTop,
                        'width' : ew,
                        'height' : centerHeight
                    });
                    centerWidth = centerWidth - ew;
                    east.el.doLayout();
                }
                center.el.bounds({
                    'x' : offsetLeft,
                    'y' : offsetTop,
                    'width' : centerWidth,
                    'height' : centerHeight
                });
                center.el.doLayout();
            };
            function typeLayout(type) {
                return function (container) {
                    return {
                        "width": 0,
                        "height": 0
                    }
                };
            }

            that.preferred = typeLayout('preferred');
            that.minimum = typeLayout('minimum');
            that.maximum = typeLayout('maximum');

            return that;
        } ,
        card : function(spec) {
            var my = {},
                that = this.layout(spec, my),
                items = spec.items;
            that.layout = function (container) {
                var bounds = container.bounds(),
                    insets = container.insets(),
                    totalWidth = bounds.width - (insets.left + insets.right),
                    totalHeight = bounds.height - (insets.top + insets.bottom);
               for (var i = 0; i < items.length; i ++) {
                   var item = items[i];
                   if (item.el instanceof $ && item.el.isVisible()) {
                       item.el.bounds({
                           'x' : insets.left,
                           'y' : insets.top,
                           'width' : totalWidth,
                           'height' : totalHeight
                       });
                       item.el.doLayout();
                   }
               }
            };
            function typeLayout(type) {
                return function (container) {
                    return {
                        "width": 0,
                        "height": 0
                    }
                };
            }

            that.preferred = typeLayout('preferred');
            that.minimum = typeLayout('minimum');
            that.maximum = typeLayout('maximum');

            return that;
        },

        /**
         * Grid layout
         */
        grid: function (spec) {
            var my = {},
                that = this.layout(spec, my),
                items = spec.items || [],
            // initialize the number of columns to the number of items
            // we're laying out.
                columns = spec.columns || items.length,
                rows = spec.rows || 0;

            if (rows > 0) {
                columns = Math.floor((items.length + rows - 1) / rows);
            }
            else {
                rows = Math.floor((items.length + columns - 1) / columns);
            }

            // alex_mod 加两个属性,用来fix grid's height & width(因为原来的就是简单平均分配height & width)
            var widths = spec.widths || [], heights = spec.heights || [];
            $.each([
                {
                    ar: widths,
                    len: columns
                },
                {
                    ar: heights,
                    len: rows
                }
            ], function (idx, it) {
                while (it.ar.length < it.len) {
                    it.ar[it.ar.length] = -1
                }
            });

            that.layout = function (container) {
                var bounds = container.bounds();

                var i, j,
                    insets = container.insets(),
                    x = insets.left,
                    y = insets.top,
                    available_width = bounds.width - (insets.left + insets.right) - (columns - 1) * my.hgap,
                    available_height = bounds.height - (insets.top + insets.bottom) - (rows - 1) * my.vgap;
                // alex_mod 根据columns & rows重新计算ac_widths & ac_heights
                var ac_widths = Array.prototype.concat.call([], widths),
                    ac_heights = Array.prototype.concat.call([], heights);
                $.each([
                    {
                        ar: ac_widths,
                        len: available_width
                    },
                    {
                        ar: ac_heights,
                        len: available_height
                    }
                ], function (_i, it) {
                    var free_c = 0;
                    $.each(it.ar, function (idx, ilen) {
                        if (ilen >= 0) {
                            it.len -= ilen;
                        } else {
                            free_c++;
                        }
                    });
                    if (free_c > 0) {
                        var av_len = it.len / free_c;
                        $.each(it.ar, function (idx, ilen) {
                            if (ilen < 0) {
                                it.ar[idx] = av_len;
                            }
                        })
                    }
                });

                var wi = 0, hi = 0;
                for (i = 0; i < items.length; i++) {
                    var width = ac_widths[wi], height = ac_heights[hi];
                    items[i].bounds({
                        'x': x,
                        'y': y,
                        'width': width,
                        'height': height
                    });

                    if (wi < columns - 1) {
                        x += width + my.hgap;
                        wi++;
                    } else {
                        y += height + my.vgap;
                        x = insets.left;
                        wi = 0;
                        hi++;
                    }
                    items[i].doLayout();
                }

                return container;
            };

            function typeLayout(type) {
                return function (container) {
                    var i,
                        width = 0,
                        height = 0,
                        type_size,
                        insets = container.insets();

                    var ca_widths = Array.prototype.concat.call([], widths),
                        ca_heights = Array.prototype.concat.call([], heights);

                    var wi = 0, hi = 0;
                    for (var i = 0; i < items.length; i++) {
                        type_size = items[i][type + 'Size']();
                        var width = ca_widths[wi], height = ca_heights[hi];
                        if (widths[wi] < 0) {
                            ca_widths[wi] = Math.max(ca_widths[wi], type_size.width)
                        }
                        if (heights[hi] < 0) {
                            ca_heights[hi] = Math.max(ca_heights[hi], type_size.height)
                        }

                        if (wi < columns - 1) {
                            wi++;
                        } else {
                            wi = 0;
                            hi++;
                        }
                        items[i].doLayout();
                    }

                    // item的总的height & width
                    var ww = 0;
                    $.each(ca_widths, function (idx, w) {
                        if (w > 0) ww += w
                    });
                    var hh = 0;
                    $.each(ca_heights, function (idx, h) {
                        if (h > 0) hh += h
                    });

                    return {
                        'width': insets.left + insets.right + ww + (columns - 1) * my.hgap,
                        'height': insets.top + insets.bottom + hh + (rows - 1) * my.vgap
                    };
                };
            }

            // this creates the min and preferred size methods, as they
            // only differ in the function they call.
            that.preferred = typeLayout('preferred');
            that.minimum = typeLayout('minimum');
            that.maximum = typeLayout('maximum');
            return that;
        },

        /**
         * 居中布局
         */
        center : function(spec) {
            var my = {},
                that = this.layout(spec, my),
                item = spec.item || [];
            that.layout = function(container) {
                var bounds = container.bounds(),
                    insets = container.insets(),
                    x = insets.left,
                    y = insets.top,
                    totalWidth = bounds.width - (insets.left + insets.right),
                    totalHeight = bounds.height - (insets.top + insets.bottom);
                var width = item.width || 'auto', height = item.height || 'auto';
                if (width === 'auto') {
                    width = totalWidth;
                }
                if (height === 'auto') {
                    height = totalHeight;
                }
                if (item.el) {
                    item.el.bounds({
                        'x' : x + (totalWidth - width) * 0.5,
                        'y' : y + (totalHeight - height) * 0.5,
                        'width' : width,
                        'height' : height
                    });
                    item.el.doLayout();
                }
            };

            function typeLayout(type) {
                return function () {
                    return {
                        "width": 0,
                        "height": 0
                    }
                };
            }

            that.preferred = typeLayout('preferred');
            that.minimum = typeLayout('minimum');
            that.maximum = typeLayout('maximum');
            return that;
        }
    }


})();