var TableHelper = require('./TableHelper');
var React = require('react');
var ReactDOM = require('react-dom');
var TableCell = require('./TableCell');

var {translateDOMPositionXY} = require('core');
var {Animated, Easing, View} = require('lib');

var cn = require('classnames');

var {PropTypes} = React;

var DIR_SIGN = TableHelper.DIR_SIGN;

var TableCellGroupImpl = React.createClass({

    propTypes_DISABLED_FOR_PERFORMANCE: {

        columns: PropTypes.array.isRequired,

        isScrolling: PropTypes.bool,

        left: PropTypes.number,

        onColumnResize: PropTypes.func,

        rowHeight: PropTypes.number.isRequired,

        rowIndex: PropTypes.number.isRequired,

        width: PropTypes.number.isRequired,

        zIndex: PropTypes.number.isRequired
    },

    render() /*object*/ {
        var props = this.props;
        var columns = props.columns;
        var cells = new Array(columns.length);

        var currentPosition = 0;
        for (var i = 0, j = columns.length; i < j; i++) {
            var columnProps = columns[i].props;
            if (!columnProps.allowCellsRecycling || (
                currentPosition - props.left <= props.width &&
                currentPosition - props.left + columnProps.width >= 0)) {
                var key = 'cell_' + i;
                cells[i] = this._renderCell(
                    props.rowIndex,
                    props.rowHeight,
                    columnProps,
                    currentPosition,
                    key
                );
            }
            currentPosition += columnProps.width;
        }

        var contentWidth = this._getColumnsWidth(columns);

        var style = {
            height: props.height,
            position: 'absolute',
            width: contentWidth,
            zIndex: props.zIndex,
            transitionDuration: '0ms',
            transitionTimingFunction: 'ease-out'
        };
        if (this.props.trans) {
            return (
                <Animated.View
                    className={'fixedDataTableCellGroupLayout-cellGroup'}
                    style={{
                        transform: [{
                            translateX: this.props.trans.x
                        }, {
                            translateY: 0
                        }, {
                            translateZ: 0
                        }], ...style
                    }}>
                    {cells}
                </Animated.View>
            );
        } else {
            //translateDOMPositionXY(style, -1 * DIR_SIGN * props.left, 0, 999);
            return (
                <View
                    className={'fixedDataTableCellGroupLayout-cellGroup'}
                    //解决safari下3d转换s时zindex被忽略问题
                    style={{
                        transform: 'translate3d(' + -1 * DIR_SIGN * props.left + 'px,' + 0 + 'px,' + 999 + 'px)',
                        backfaceVisibility: 'hidden'
                        , ...style
                    }}>
                    {cells}
                </View>
            );
        }
    },

    _renderCell(/*number*/ rowIndex,
                /*number*/ height,
                /*object*/ columnProps,
                /*number*/ left,
                /*string*/ key) /*object*/ {

        var cellIsResizable = columnProps.isResizable &&
            this.props.onColumnResize;
        var onColumnResize = cellIsResizable ? this.props.onColumnResize : null;

        var className = columnProps.cellClassName;

        return (
            <TableCell
                isScrolling={this.props.isScrolling}
                align={columnProps.align}
                className={className}
                height={height}
                key={key}
                maxWidth={columnProps.maxWidth}
                minWidth={columnProps.minWidth}
                onColumnResize={onColumnResize}
                rowIndex={rowIndex}
                columnKey={columnProps.columnKey}
                width={columnProps.width}
                left={left}
                cell={columnProps.cell}
            />
        );
    },

    _getColumnsWidth(/*array*/ columns) /*number*/ {
        var width = 0;
        for (var i = 0; i < columns.length; ++i) {
            width += columns[i].props.width;
        }
        return width;
    },
});

var TableCellGroup = React.createClass({

    propTypes_DISABLED_FOR_PERFORMANCE: {
        isScrolling: PropTypes.bool,
        height: PropTypes.number.isRequired,

        offsetLeft: PropTypes.number,

        left: PropTypes.number,
        zIndex: PropTypes.number.isRequired
    },

    shouldComponentUpdate(/*object*/ nextProps) /*boolean*/ {
        return (
            !nextProps.isScrolling ||
            this.props.rowIndex !== nextProps.rowIndex ||
            this.props.left !== nextProps.left
        );
    },

    getDefaultProps() /*object*/ {
        return {
            offsetLeft: 0
        };
    },

    render() /*object*/ {
        var {offsetLeft, ...props} = this.props;

        var style = {
            height: props.height
        };

        if (DIR_SIGN === 1) {
            style.left = offsetLeft;
        } else {
            style.right = offsetLeft;
        }

        var onColumnResize = props.onColumnResize ? this._onColumnResize : null;

        return (
            <div
                style={style}
                className={'fixedDataTableCellGroupLayout-cellGroupWrapper'}>
                <TableCellGroupImpl
                    {...props}
                    onColumnResize={onColumnResize}
                />
            </div>
        );
    },

    _onColumnResize(/*number*/ left,
                    /*number*/ width,
                    /*?number*/ minWidth,
                    /*?number*/ maxWidth,
                    /*string|number*/ columnKey,
                    /*object*/ event) {
        this.props.onColumnResize && this.props.onColumnResize(
            this.props.offsetLeft,
            left - this.props.left + width,
            width,
            minWidth,
            maxWidth,
            columnKey,
            event
        );
    }
});


module.exports = TableCellGroup;
