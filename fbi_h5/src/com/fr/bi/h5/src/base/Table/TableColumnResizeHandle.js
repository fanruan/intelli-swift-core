var {MouseMoveTracker} = require('core');
var React = require('react');
var ReactComponentWithPureRenderMixin = require('react-addons-pure-render-mixin');

var clamp = require('lodash/clamp');
var cn = require('classnames');

var {PropTypes} = React;

var TableColumnResizeHandle = React.createClass({
    mixins: [ReactComponentWithPureRenderMixin],

    propTypes: {
        visible: PropTypes.bool.isRequired,

        height: PropTypes.number.isRequired,

        leftOffset: PropTypes.number.isRequired,

        knobHeight: PropTypes.number.isRequired,

        initialWidth: PropTypes.number,

        minWidth: PropTypes.number,

        maxWidth: PropTypes.number,

        initialEvent: PropTypes.object,

        onColumnResizeEnd: PropTypes.func,

        columnKey: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.number
        ])
    },

    getInitialState() /*object*/ {
        return {
            width: 0,
            cursorDelta: 0
        };
    },

    componentWillReceiveProps(/*object*/ newProps) {
        if (newProps.initialEvent && !this._mouseMoveTracker.isDragging()) {
            this._mouseMoveTracker.captureMouseMoves(newProps.initialEvent);
            this.setState({
                width: newProps.initialWidth,
                cursorDelta: newProps.initialWidth
            });
        }
    },

    componentDidMount() {
        this._mouseMoveTracker = new MouseMoveTracker(
            this._onMove,
            this._onColumnResizeEnd,
            document.body
        );
    },

    componentWillUnmount() {
        this._mouseMoveTracker.releaseMouseMoves();
        this._mouseMoveTracker = null;
    },

    render() /*object*/ {
        var style = {
            width: this.state.width,
            height: this.props.height,
        };
        style.left = this.props.leftOffset;
        return (
            <div
                className={cn({
          'fixedDataTableColumnResizerLineLayout-main': true,
          'fixedDataTableColumnResizerLineLayout-hiddenElem': !this.props.visible,
          'public-fixedDataTableColumnResizerLine-main': true
        })}
                style={style}>
                <div
                    className={'fixedDataTableColumnResizerLineLayout-mouseArea'}
                    style={{height: this.props.height}}
                />
            </div>
        );
    },

    _onMove(/*number*/ deltaX) {
        var newWidth = this.state.cursorDelta + deltaX;
        var newColumnWidth =
            clamp(newWidth, this.props.minWidth, this.props.maxWidth);

        // Please note cursor delta is the different between the currently width
        // and the new width.
        this.setState({
            width: newColumnWidth,
            cursorDelta: newWidth
        });
    },

    _onColumnResizeEnd() {
        this._mouseMoveTracker.releaseMouseMoves();
        this.props.onColumnResizeEnd(
            this.state.width,
            this.props.columnKey
        );
    }
});

module.exports = TableColumnResizeHandle;
