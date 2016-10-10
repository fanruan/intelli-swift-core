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

const {width, height} = Dimensions.get('window');
const navigatorH = 64; // navigator height
const [aWidth, aHeight] = [300, 214];
const [left, top] = [0, 0];
const [middleLeft, middleTop] = [(width - aWidth) / 2, (height - aHeight) / 2 - navigatorH];

class Dialog extends Component {
    constructor(props) {
        super(props);
        this.state = {
            offset: new Animated.Value(0),
            opacity: new Animated.Value(0),
            title: "",
            choose1: "",
            choose2: ""
        };
    }

    componentDidMount() {
        this.in();
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
                        <Animated.View style={[styles.tip, {
                            width: aWidth,
                            height: aHeight,
                            transform: [{
                                translateY: this.state.offset.interpolate({
                                    inputRange: [0, 1],
                                    outputRange: [aHeight, 0]
                                }),
                            }]
                        }]}>
                            <View style={styles.tipBody}>
                                <View style={styles.tipTitleView}>
                                    <Text style={styles.tipTitleText}>{this.props.title}</Text>
                                </View>
                                <TouchableHighlight style={styles.tipContentView} underlayColor='#f0f0f0'
                                >
                                    <Text style={styles.tipText}>{this.props.choose1}</Text>
                                </TouchableHighlight>

                                <TouchableHighlight style={styles.tipContentView} underlayColor='#f0f0f0'
                                >
                                    <Text style={styles.tipText}>{this.props.choose2}</Text>
                                </TouchableHighlight>
                            </View>
                            <View style={styles.tipBottom}>
                                <TouchableHighlight style={styles.button} underlayColor='#f0f0f0'
                                >
                                    <Text style={styles.buttonText}>取消</Text>
                                </TouchableHighlight>
                            </View>
                        </Animated.View>
                    </Animated.View>
                </Modal>
            </View>
        );
    }

    //显示动画
    in() {
        Animated.parallel([
            Animated.timing(
                this.state.opacity,
                {
                    easing: Easing.linear,
                    duration: 500,
                    toValue: 1
                }
            ),
            Animated.timing(
                this.state.offset,
                {
                    easing: Easing.linear,
                    duration: 500,
                    toValue: 1
                }
            )
        ]).start();
    }

    //隐藏动画
    out() {
        Animated.parallel([
            Animated.timing(
                this.state.opacity,
                {
                    easing: Easing.linear,
                    duration: 500,
                    toValue: 0
                }
            ),
            Animated.timing(
                this.state.offset,
                {
                    easing: Easing.linear,
                    duration: 500,
                    toValue: 0
                }
            )
        ]).start((endState)=> {
            this.setState({visible: false})
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
    },
    tip: {
        alignItems: "center",
        justifyContent: "space-between"
    },
    tipBody: {
        justifyContent: "space-between",
        alignSelf: 'stretch',
        backgroundColor: '#fff'
    },
    tipBottom: {
        marginTop: 10,
        alignSelf: 'stretch',
        backgroundColor: '#fff'
    },
    tipTitleView: {
        height: 55,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
    },
    tipTitleText: {
        color: "#999999",
        fontSize: 14,
    },
    tipContentView: {
        borderTopWidth: 0.5,
        borderColor: "#f0f0f0",
        height: 45,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
    },
    tipText: {
        color: "#e6454a",
        fontSize: 17,
        textAlign: "center",
    },
    button: {
        height: 45,
        justifyContent: 'center'
    },
    buttonText: {
        fontSize: 17,
        color: "#e6454a",
        textAlign: "center",
    }
});

mixin.onClass(Dialog, TimerMixin);

export default Dialog