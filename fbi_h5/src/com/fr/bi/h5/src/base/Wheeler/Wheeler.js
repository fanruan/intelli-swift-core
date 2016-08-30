import React, {Component, PropTypes, ReactChildren} from 'react';
import ReactComponentWithPureRenderMixin from 'react-addons-pure-render-mixin'
import {WheelHandler, emptyFunction, debounce, invariant} from 'core'
import {View} from 'lib'

export const DEFAULT_MAX_SCROLL_SIZE = 10000000;

var Wheeler = React.createClass({
    mixins: [ReactComponentWithPureRenderMixin],
    propTypes: {

        overflowX: PropTypes.oneOf(['hidden', 'auto']),
        overflowY: PropTypes.oneOf(['hidden', 'auto']),

        scrollLeft: PropTypes.number,

        scrollTop: PropTypes.number,

        maxScrollX: PropTypes.number,
        maxScrollY: PropTypes.number,

        onScrollStart: PropTypes.func,

        onScrollEnd: PropTypes.func
    },

    getDefaultProps() /*object*/ {
        return {
            scrollLeft: 0,
            scrollTop: 0,
            maxScrollX: DEFAULT_MAX_SCROLL_SIZE,
            maxScrollY: DEFAULT_MAX_SCROLL_SIZE
        }
    },

    getInitialState() /*object*/ {
        var props = this.props;
        this._didScrollStop = debounce(this._didScrollStop, 200).bind(this);

        return this._calculateState(props);
    },

    componentWillMount() {
        this._wheelHandler = new WheelHandler(
            this._onWheel,
            this._shouldHandleWheelX,
            this._shouldHandleWheelY
        );
    },

    componentDidMount() {

    },

    componentWillReceiveProps(/*object*/ nextProps) {
        var newOverflowX = nextProps.overflowX;
        var newOverflowY = nextProps.overflowY;
        if (newOverflowX !== this.props.overflowX ||
            newOverflowY !== this.props.overflowY) {
            this._wheelHandler = new WheelHandler(
                this._onWheel,
                newOverflowX !== 'hidden',
                newOverflowY !== 'hidden'
            );
        }

        if (this.props.scrollTop !== nextProps.scrollTop) {
            this._didScrollStart();
        }
        this._didScrollStop();

        this.setState(this._calculateState(nextProps, this.state));
    },

    componentDidUpdate() {

    },

    render() {
        var props = this.props;

        return (
            <View
                {...props}
                onWheel={this._wheelHandler.onWheel}
                style={{overflow: 'hidden', overflowX: 'hidden', overflowY: 'hidden'}}
            >
                {this.props.children(this.state)}
            </View>
        );
    },

    _calculateState(/*object*/ props, /*?object*/ oldState) /*object*/ {

        const {maxScrollX, maxScrollY} = props;
        var scrollX, scrollY;
        if (oldState && props.overflowX !== 'hidden') {
            scrollX = oldState.scrollX;
        } else {
            scrollX = props.scrollLeft;
        }
        if (oldState && props.overflowY !== 'hidden') {
            scrollY = oldState.scrollY;
        } else {
            scrollY = props.scrollTop;
        }
        scrollX = Math.min(scrollX, maxScrollX);
        scrollY = Math.min(scrollY, maxScrollY);

        return {
            scrollX,
            scrollY,
            maxScrollX,
            maxScrollY
        };
    },

    _shouldHandleWheelX(/*number*/ delta) /*boolean*/ {
        if (this.props.overflowX === 'hidden') {
            return false;
        }

        delta = Math.round(delta);
        if (delta === 0) {
            return false;
        }

        return (
            (delta < 0 && this.state.scrollX > 0) ||
            (delta >= 0 && this.state.scrollX < this.state.maxScrollX)
        );
    },

    _shouldHandleWheelY(/*number*/ delta) /*boolean*/ {
        if (this.props.overflowY === 'hidden' || delta === 0) {
            return false;
        }

        delta = Math.round(delta);
        if (delta === 0) {
            return false;
        }

        return (
            (delta < 0 && this.state.scrollY > 0) ||
            (delta >= 0 && this.state.scrollY < this.state.maxScrollY)
        );
    },

    _onWheel(/*number*/ deltaX, /*number*/ deltaY) {
        if (this.isMounted()) {
            if (!this._isScrolling) {
                this._didScrollStart();
            }
            var x = this.state.scrollX;
            var y = this.state.scrollY;
            if (Math.abs(deltaY) > Math.abs(deltaX) &&
                this.props.overflowY !== 'hidden') {
                y += deltaY;
                y = y < 0 ? 0 : y;
                y = y > this.state.maxScrollY ? this.state.maxScrollY : y;
                this.setState({
                    scrollY: y
                });
            } else if (deltaX && this.props.overflowX !== 'hidden') {
                x += deltaX;
                x = x < 0 ? 0 : x;
                x = x > this.state.maxScrollX ? this.state.maxScrollX : x;
                this.setState({
                    scrollX: x
                });
            }

            this._didScrollStop();
        }
    },


    _onHorizontalScroll(/*number*/ scrollPos) {
        if (this.isMounted() && scrollPos !== this.state.scrollX) {
            if (!this._isScrolling) {
                this._didScrollStart();
            }
            this.setState({
                scrollX: scrollPos
            });
            this._didScrollStop();
        }
    },

    _onVerticalScroll(/*number*/ scrollPos) {
        if (this.isMounted() && scrollPos !== this.state.scrollY) {
            if (!this._isScrolling) {
                this._didScrollStart();
            }
            this.setState({
                scrollY: scrollPos
            });
            this._didScrollStop();
        }
    },

    _didScrollStart() {
        if (this.isMounted() && !this._isScrolling) {
            this._isScrolling = true;
            if (this.props.onScrollStart) {
                this.props.onScrollStart(this.state.scrollX, this.state.scrollY);
            }
        }
    },

    _didScrollStop() {
        if (this.isMounted() && this._isScrolling) {
            this._isScrolling = false;
            this.setState({redraw: true});
            if (this.props.onScrollEnd) {
                this.props.onScrollEnd(this.state.scrollX, this.state.scrollY);
            }
        }
    }
});

export default Wheeler
