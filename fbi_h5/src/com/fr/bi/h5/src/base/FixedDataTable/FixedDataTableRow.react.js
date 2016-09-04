var React = require('react');
var FixedDataTableCellGroup = require('./FixedDataTableCellGroup.react');

var cn = require('classnames');
var {translateDOMPositionXY} = require('core');

var {PropTypes} = React;

var FixedDataTableRowImpl = React.createClass({

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
            <FixedDataTableCellGroup
                key="fixed_cells"
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
            <FixedDataTableCellGroup
                key="scrollable_cells"
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
            <div
                className={cn(className, this.props.className)}
                onClick={this.props.onClick ? this._onClick : null}
                onDoubleClick={this.props.onDoubleClick ? this._onDoubleClick : null}
                onMouseDown={this.props.onMouseDown ? this._onMouseDown : null}
                onMouseEnter={this.props.onMouseEnter ? this._onMouseEnter : null}
                onMouseLeave={this.props.onMouseLeave ? this._onMouseLeave : null}
                style={style}>
                <div className={'fixedDataTableRowLayout-body'}>
                    {scrollableColumns}
                    {columnsShadow}
                    {fixedColumns}
                </div>
            </div>
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
    }
});

var FixedDataTableRow = React.createClass({

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
            <div
                style={style}
                className={'fixedDataTableRowLayout-rowWrapper'}>
                <FixedDataTableRowImpl
                    {...this.props}
                    offsetTop={undefined}
                    zIndex={undefined}
                    />
            </div>
        );
    }
});


module.exports = FixedDataTableRow;
