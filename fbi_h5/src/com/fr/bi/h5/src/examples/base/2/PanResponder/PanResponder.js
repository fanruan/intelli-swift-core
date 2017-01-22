var React = require('lib');
var {
    Component,
    PanResponder,
    StyleSheet,
    View,
    processColor
} = React;

var CIRCLE_SIZE = 80;
var CIRCLE_COLOR = 'blue';
var CIRCLE_HIGHLIGHT_COLOR = 'green';

class PanResponderDemo extends Component {
    constructor(props, context) {
        super(props, context);

    }

    _panResponder = {}
    _previousLeft = 0
    _previousTop = 0
    _circleStyles = {}
    circle = {
        setNativeProps(props:Object){
        }
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
        this._previousLeft = 20;
        this._previousTop = 84;
        this._circleStyles = {
            style: {
                left: this._previousLeft,
                top: this._previousTop
            }
        };
    }

    componentDidMount() {
        this._updatePosition();
    }

    render() {
        return (
            <View
                style={styles.container}>
                <View
                    ref={(circle) => {
            this.circle = circle;
          }}
                    style={styles.circle}
                    {...this._panResponder.panHandlers}
                />
            </View>
        );
    }

    _highlight() {

        this.circle && this.circle.setNativeProps({
            style: {
                backgroundColor: processColor(CIRCLE_HIGHLIGHT_COLOR)
            }
        });
    }

    _unHighlight() {
        this.circle && this.circle.setNativeProps({
            style: {
                backgroundColor: processColor(CIRCLE_COLOR)
            }
        });
    }

    _updatePosition() {
        this.circle && this.circle.setNativeProps(this._circleStyles);

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
        this._highlight();
    }

    _handlePanResponderMove(e, gestureState) {
        // console.log(gestureState)
        this._circleStyles.style.left = this._previousLeft + gestureState.dx;
        this._circleStyles.style.top = this._previousTop + gestureState.dy;
        this._updatePosition();
    }

    _handlePanResponderEnd(e, gestureState) {
        this._unHighlight();
        this._previousLeft += gestureState.dx;
        this._previousTop += gestureState.dy;
        console.log(gestureState.dx);
        console.log(gestureState.dy);
    }
}

var styles = StyleSheet.create({
    circle: {
        width: CIRCLE_SIZE,
        height: CIRCLE_SIZE,
        borderRadius: CIRCLE_SIZE / 2,
        backgroundColor: CIRCLE_COLOR,
        position: 'absolute',
        left: 0,
        top: 0
    },
    container: {
        flex: 1,
        paddingTop: 64
    }
});

module.exports = PanResponderDemo;
