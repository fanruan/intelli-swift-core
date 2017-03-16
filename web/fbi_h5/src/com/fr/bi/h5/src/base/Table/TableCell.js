var TableCellDefault = require('./TableCellDefault');
var TableHelper = require('./TableHelper');
var React = require('react');
var {View} = require('lib');
var cn = require('classnames');

var DIR_SIGN = TableHelper.DIR_SIGN;

var {PropTypes} = React;

var DEFAULT_PROPS = {
    align: 'left',
    highlighted: false
};

var TableCell = React.createClass({

    propTypes_DISABLED_FOR_PERFORMANCE: {
        isScrolling: PropTypes.bool,
        align: PropTypes.oneOf(['left', 'center', 'right']),
        className: PropTypes.string,
        highlighted: PropTypes.bool,
        width: PropTypes.number.isRequired,
        minWidth: PropTypes.number,
        maxWidth: PropTypes.number,
        height: PropTypes.number.isRequired,

        cell: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.element,
            PropTypes.func
        ]),

        columnKey: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ]),

        rowIndex: PropTypes.number.isRequired,

        onColumnResize: PropTypes.func,

        left: PropTypes.number
    },

    shouldComponentUpdate(nextProps) {
        return (
            !nextProps.isScrolling ||
            this.props.rowIndex !== nextProps.rowIndex
        );
    },

    getDefaultProps() /*object*/ {
        return DEFAULT_PROPS;
    },

    render() /*object*/ {

        var {height, width, columnKey, ...props} = this.props;

        var style = {
            height,
            width
        };

        if (DIR_SIGN === 1) {
            style.left = props.left;
        } else {
            style.right = props.left;
        }

        var className = cn(
            cn({
                'fixedDataTableCellLayout-main': true,
                'fixedDataTableCellLayout-lastChild': props.lastChild,
                'fixedDataTableCellLayout-alignRight': props.align === 'right',
                'fixedDataTableCellLayout-alignCenter': props.align === 'center',
                'public-fixedDataTableCell-alignRight': props.align === 'right',
                'public-fixedDataTableCell-highlighted': props.highlighted,
                'public-fixedDataTableCell-main': true
            }),
            props.className
        );

        var columnResizerComponent;
        if (props.onColumnResize) {
            var columnResizerStyle = {
                height
            };
            columnResizerComponent = (
                <View
                    className={'fixedDataTableCellLayou-columnResizerContainer'}
                    style={columnResizerStyle}
                    onMouseDown={this._onColumnResizerMouseDown}>
                    <View
                        className={cn(
              'fixedDataTableCellLayout-columnResizerKnob',
              'public-fixedDataTableCell-columnResizerKnob'
            )}
                        style={columnResizerStyle}
                        />
                </View>
            );
        }

        var cellProps = {
            columnKey,
            height,
            width
        };

        if (props.rowIndex >= 0) {
            cellProps.rowIndex = props.rowIndex;
        }

        var content;
        if (React.isValidElement(props.cell)) {
            content = React.cloneElement(props.cell, cellProps);
        } else if (typeof props.cell === 'function') {
            content = props.cell(cellProps);
        } else {
            content = (
                <TableCellDefault
                    {...cellProps}>
                    {props.cell}
                </TableCellDefault>
            );
        }

        return (
            <View className={className} style={style}>
                {columnResizerComponent}
                {content}
            </View>
        );
    },

    _onColumnResizerMouseDown(/*object*/ event) {
        this.props.onColumnResize(
            this.props.left,
            this.props.width,
            this.props.minWidth,
            this.props.maxWidth,
            this.props.columnKey,
            event
        );
    }
});

module.exports = TableCell;
