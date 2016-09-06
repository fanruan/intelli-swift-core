var React = require('react');
var ReactDOM = require('react-dom');
var TableCellGroup = require('./TableCellGroup');

var cn = require('classnames');
var {translateDOMPositionXY} = require('core');
var {Animated, View} = require('lib');

var {PropTypes} = React;

var TableRowImpl = React.createClass({

    propTypes: {

        isScrolling: PropTypes.bool,

        fixedColumns: PropTypes.array.isRequired,

        height: PropTypes.number.isRequired,

        index: PropTypes.number.isRequired,

        scrollableColumns: PropTypes.array.isRequired,

        scrollLeft: PropTypes.number.isRequired,

        width: PropTypes.number.isRequired,

        onClick: PropTypes.func,

        onDoubleClick: PropTypes.func,

        onColumnResize: PropTypes.func
    },

    render() /*object*/ {
        var style = {
            width: this.props.width,
            height: this.props.height
        };

        var className = cn({
            'fixedDataTableRowLayout-main': true,
            'public-fixedDataTableRow-main': true,
            'public-fixedDataTableRow-highlighted': (this.props.index % 2 === 1),
            'public-fixedDataTableRow-odd': (this.props.index % 2 === 1),
            'public-fixedDataTableRow-even': (this.props.index % 2 === 0)
        });

        var fixedColumnsWidth = this._getColumnsWidth(this.props.fixedColumns);
        var fixedColumns =
            <TableCellGroup
                ref='fixed_cells'
                key='fixed_cells'
                isScrolling={this.props.isScrolling}
                height={this.props.height}
                left={0}
                width={fixedColumnsWidth}
                zIndex={2}
                columns={this.props.fixedColumns}
                onColumnResize={this.props.onColumnResize}
                rowHeight={this.props.height}
                rowIndex={this.props.index}
            />;
        var columnsShadow = this._renderColumnsShadow(fixedColumnsWidth);
        var scrollableColumns =
            <TableCellGroup
                ref='scrollable_cells'
                key='scrollable_cells'
                isScrolling={this.props.isScrolling}
                height={this.props.height}
                left={this.props.scrollLeft}
                offsetLeft={fixedColumnsWidth}
                width={this.props.width - fixedColumnsWidth}
                zIndex={0}
                columns={this.props.scrollableColumns}
                onColumnResize={this.props.onColumnResize}
                rowHeight={this.props.height}
                rowIndex={this.props.index}
                trans={this.props.trans}
            />;

        return (
            <View
                className={cn(className, this.props.className)}
                onClick={this.props.onClick ? this._onClick : null}
                onDoubleClick={this.props.onDoubleClick ? this._onDoubleClick : null}
                onMouseDown={this.props.onMouseDown ? this._onMouseDown : null}
                onMouseEnter={this.props.onMouseEnter ? this._onMouseEnter : null}
                onMouseLeave={this.props.onMouseLeave ? this._onMouseLeave : null}
                style={style}>
                <View className={'fixedDataTableRowLayout-body'}>
                    {scrollableColumns}
                    {columnsShadow}
                    {fixedColumns}
                </View>
            </View>
        );
    },

    _getColumnsWidth(/*array*/ columns) /*number*/ {
        var width = 0;
        for (var i = 0; i < columns.length; ++i) {
            width += columns[i].props.width;
        }
        return width;
    },

    _renderColumnsShadow(/*number*/ left) /*?object*/ {
        if (left > 0) {
            var className = cn({
                'fixedDataTableRowLayout-fixedColumnsDivider': true,
                'fixedDataTableRowLayout-columnsShadow': this.props.scrollLeft > 0,
                'public-fixedDataTableRow-fixedColumnsDivider': true,
                'public-fixedDataTableRow-columnsShadow': this.props.scrollLeft > 0
            });
            var style = {
                transform: 'translate3d(' + left + 'px,0,999px)',
                //left: left,
                height: this.props.height
            };
            return <div className={className} style={style}/>;
        }
    },

    _onClick(/*object*/ event) {
        this.props.onClick(event, this.props.index);
    },

    _onDoubleClick(/*object*/ event) {
        this.props.onDoubleClick(event, this.props.index);
    },

    _onMouseDown(/*object*/ event) {
        this.props.onMouseDown(event, this.props.index);
    },

    _onMouseEnter(/*object*/ event) {
        this.props.onMouseEnter(event, this.props.index);
    },

    _onMouseLeave(/*object*/ event) {
        this.props.onMouseLeave(event, this.props.index);
    },

    setScrolling(){
        this.refs['fixed_cells'].setScrolling();
        this.refs['scrollable_cells'].setScrolling();
    },

    setScrollEnd(){
        this.refs['fixed_cells'].setScrollEnd();
        this.refs['scrollable_cells'].setScrollEnd();
    }
});

var TableRow = React.createClass({

    propTypes: {

        isScrolling: PropTypes.bool,

        height: PropTypes.number.isRequired,

        zIndex: PropTypes.number,

        offsetTop: PropTypes.number.isRequired,

        width: PropTypes.number.isRequired
    },

    render() /*object*/ {
        var style = {
            width: this.props.width,
            height: this.props.height,
            zIndex: (this.props.zIndex ? this.props.zIndex : 0)
        };
        translateDOMPositionXY(style, 0, this.props.offsetTop);

        return (
            <View
                style={style}
                className={'fixedDataTableRowLayout-rowWrapper'}>
                <TableRowImpl
                    ref='tableRowImpl'
                    {...this.props}
                    offsetTop={undefined}
                    zIndex={undefined}
                />
            </View>
        );
    },

    setScrolling(){
        this.refs['tableRowImpl'].setScrolling();
    },

    setScrollEnd(){
        this.refs['tableRowImpl'].setScrollEnd();
    }
});


module.exports = TableRow;
