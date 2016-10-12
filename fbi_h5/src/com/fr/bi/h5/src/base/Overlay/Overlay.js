import PureRenderMixin from 'react-addons-pure-render-mixin'
import TimerMixin from 'react-timer-mixin';
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import React, {
    Component,
    StyleSheet,
    Modal,
    View,
    Image,
    Text,
    TouchableHighlight,
    Animated,
    Easing,
    Dimensions,
} from 'lib';

class Overlay extends Component {
    constructor(props) {
        super(props);
        this.state = {
            opacity: new Animated.Value(0)
        };
    }

    componentDidMount() {
        this.open();
    }

    render() {
        return (
            <View style={styles.container}>
                <Modal
                    transparent={true}
                >
                    <Animated.View style={ [styles.mask, {
                        opacity: this.state.opacity
                    }] }>
                        {this.props.children}
                    </Animated.View>
                </Modal>
            </View>
        );
    }

    open() {
        Animated.parallel([
            Animated.timing(
                this.state.opacity,
                {
                    easing: Easing.linear,
                    duration: 500,
                    toValue: 1
                }
            )
        ]).start();
    }

    close(op) {
        Animated.parallel([
            Animated.timing(
                this.state.opacity,
                {
                    easing: Easing.linear,
                    duration: 500,
                    toValue: 0
                }
            )
        ]).start((endState)=> {
            this.setState({visible: false}, ()=> {
                this.props.onClose && this.props.onClose(op);
            })
        });
    }
}

const styles = StyleSheet.create({
    container: {},
    mask: {
        flex: 1,
        padding: 20,
        justifyContent: "flex-end",
        alignItems: 'center',
        backgroundColor: "rgba(56,56,56,0.6)"
    }
});

mixin.onClass(Overlay, TimerMixin);

export default Overlay