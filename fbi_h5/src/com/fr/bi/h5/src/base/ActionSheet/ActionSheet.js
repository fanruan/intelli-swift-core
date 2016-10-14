import PureRenderMixin from 'react-addons-pure-render-mixin'
import TimerMixin from 'react-timer-mixin';
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import React, {
    Component,
    StyleSheet,
    PixelRatio,
    Modal,
    View,
    Text,
    Animated,
    Easing,
    Dimensions,
    TouchableWithoutFeedback
} from 'lib';
import {Layout} from 'layout'
import {Colors, Sizes} from 'data'

const [aHeight] = [217];

class ActionSheet extends Component {
    constructor(props) {
        super(props);
        this.state = {
            offset: new Animated.Value(0),
            opacity: new Animated.Value(0)
        };
    }

    static defaultProps = {
        buttonText: ['取消', '确定']
    };

    componentDidMount() {
        this.open();
    }

    render() {
        return (
            <View style={styles.container}>
                <Modal
                    transparent={true}
                >
                    <TouchableWithoutFeedback onPress={()=> {
                        this.close()
                    }}>
                        <Animated.View style={[styles.mask, {
                            opacity: this.state.opacity
                        }] }>
                            <TouchableWithoutFeedback>
                                <View style={styles.sheet}>
                                    <Animated.View style={[styles.body, {
                                        height: aHeight,
                                        transform: [{
                                            translateY: this.state.offset.interpolate({
                                                inputRange: [0, 1],
                                                outputRange: [aHeight, 0]
                                            }),
                                        }]
                                    }]}>
                                        <Layout dir='top' box='first' style={styles.wrapper}>
                                            <Layout main='justify' cross='center' style={styles.header}>
                                                <Text onPress={()=> {
                                                    this.close(this.props.buttonText[0]);
                                                }}>{this.props.buttonText[0]}</Text>
                                                <Text>{this.props.title}</Text>
                                                <Text onPress={()=> {
                                                    this.close(this.props.buttonText[1]);
                                                }}>{this.props.buttonText[1]}</Text>
                                            </Layout>
                                            <Layout box='mean'>
                                                {this.props.children}
                                            </Layout>
                                        </Layout>
                                    </Animated.View>
                                </View>
                            </TouchableWithoutFeedback>
                        </Animated.View>
                    </TouchableWithoutFeedback>
                </Modal>
            </View>
        );
    }

    //显示动画
    open() {
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
    close(op) {
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
            this.setState({visible: false}, ()=> {
                this.props.onClose && this.props.onClose(op);
            })
        });
    }
}

const styles = StyleSheet.create({
    mask: {
        position: 'absolute',
        left: 0,
        right: 0,
        top: 0,
        bottom: 0,
        backgroundColor: 'rgba(56,56,56,0.6)'
    },
    sheet: {
        position: 'absolute',
        left: 0,
        right: 0,
        bottom: 0
    },
    body: {
        backgroundColor: '#ffffff'
    },
    wrapper: {
        height: aHeight,
        borderTopColor: Colors.BORDER,
        borderTopWidth: 1
    },
    header: {
        paddingLeft: 20,
        paddingRight: 20,
        height: Sizes.HEADER_HEIGHT,
        borderBottomColor: Colors.BORDER,
        borderBottomWidth: 1 / PixelRatio.get()
    }
});

mixin.onClass(ActionSheet, TimerMixin);

export default ActionSheet