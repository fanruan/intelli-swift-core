import React, {Component, PropTypes, ReactChildren} from 'react';
import mixin from 'react-mixin';
import ReactComponentWithPureRenderMixin from 'react-addons-pure-render-mixin'
import {WheelHandler, emptyFunction, debounce, invariant} from 'core'
import {PanResponder, View} from 'lib'


export const DEFAULT_MAX_SCROLL_SIZE = 10000000;

class Mover extends Component {
    constructor(props, context) {
        super(props, context);
        this.state = {scrollTop: props.scrollTop, scrollLeft: props.scrollLeft};
        this._scrollLeft = props.scrollLeft;
        this._scrollTop = props.scrollTop;
    }


    static propTypes = {

        overflowX: PropTypes.oneOf(['hidden', 'auto']),
        overflowY: PropTypes.oneOf(['hidden', 'auto']),

        scrollLeft: PropTypes.number,

        scrollTop: PropTypes.number,

        maxScrollX: PropTypes.number,
        maxScrollY: PropTypes.number,

        onSwipeStart: PropTypes.func,

        onSwipe: PropTypes.func,

        onSwipeEnd: PropTypes.func
    }

    static defaultProps = {
        scrollLeft: 0,
        scrollTop: 0,
        maxScrollX: DEFAULT_MAX_SCROLL_SIZE,
        maxScrollY: DEFAULT_MAX_SCROLL_SIZE
    }


    componentWillMount() {
        this._panResponder = PanResponder.create({
            onStartShouldSetPanResponder: this._handleStartShouldSetPanResponder.bind(this),
            onMoveShouldSetPanResponder: this._handleMoveShouldSetPanResponder.bind(this),
            onPanResponderGrant: this._handlePanResponderGrant.bind(this),
            onPanResponderMove: this._handlePanResponderMove.bind(this),
            onPanResponderRelease: this._handlePanResponderEnd.bind(this),
            onPanResponderTerminate: this._handlePanResponderEnd.bind(this)
        });
    }

    componentWillReceiveProps(nextProps) {
        this.setState(nextProps);
        this._scrollLeft = nextProps.scrollLeft || this._scrollLeft;
        this._scrollTop = nextProps.scrollTop || this._scrollTop;
    }

    componentDidMount() {

    }

    render() {
        return (
            <View
                {...this.props}
                style={{overflow: 'hidden',...this.props.style}} {...this._panResponder.panHandlers}>
                {this.props.children(this.state)}
            </View>
        );
    }

    _setNextState(gestureState) {
        let {scrollLeft, scrollTop} = {
            scrollLeft: this._scrollLeft,
            scrollTop: this._scrollTop
        };
        const {maxScrollX, maxScrollY} = this.props;
        scrollLeft -= gestureState.dx;
        scrollTop -= gestureState.dy;
        if (scrollLeft < 0) {
            scrollLeft = 0;
        }
        if (scrollTop < 0) {
            scrollTop = 0;
        }
        if (scrollLeft > maxScrollX) {
            scrollLeft = maxScrollX;
        }
        if (scrollTop > maxScrollY) {
            scrollTop = maxScrollY;
        }
        this.setState({scrollX: scrollLeft, scrollY: scrollTop})
    }

    _handleStartShouldSetPanResponder(e, gestureState) {
        // Should we become active when the user presses down on the circle?
        return true;
    }

    _handleMoveShouldSetPanResponder(e, gestureState) {
        // Should we become active when the user moves a touch over the circle?
        return true;
    }

    _handlePanResponderGrant(e, gestureState) {
        this.props.onSwipeStart && this.props.onSwipeStart(this.state.scrollX, this.state.scrollY);
    }

    _handlePanResponderMove(e, gestureState) {
        this._setNextState(gestureState);
        this.props.onSwipe && this.props.onSwipe(this.state.scrollX, this.state.scrollY);
    }

    _handlePanResponderEnd(e, gestureState) {
        this._setNextState(gestureState);
        this.props.onSwipeEnd && this.props.onSwipeEnd(this.state.scrollX, this.state.scrollY);
        this._scrollLeft = this.state.scrollX;
        this._scrollTop = this.state.scrollY;
    }
}

mixin.onClass(Mover, ReactComponentWithPureRenderMixin);

export default Mover
