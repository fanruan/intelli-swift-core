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
import {sc} from 'core'
import {Layout} from 'layout'
import {Colors, Sizes} from 'data'

const [aHeight,aWidth] = [217, 217];

class Popup extends Component {
    constructor(props) {
        super(props);
        this.state = {
            offset: new Animated.Value(0),
            opacity: new Animated.Value(0)
        };
    }

    static defaultProps = {
        buttonText: ['取消', '确定'],
        direction: 'bottom',
        align: '',
        // contentHeight: aHeight,
        // contentWidth: aWidth
    };

    componentDidMount() {
        this.open();
    }

    render() {
        const {direction, align, contentWidth, contentHeight} = this.props;
        return <View style={styles.container}>
            <Modal
                transparent={true}
            >
                <TouchableWithoutFeedback onPress={()=> {
                    this.close()
                }}>
                    <Animated.View style={[styles.opacityContainer, {
                        opacity: this.state.opacity
                    }] }>
                        <TouchableWithoutFeedback>
                            <View style={[styles.sheet,
                                sc([styles.topSheet, direction === 'top' && align === ''],
                                    [styles.bottomSheet, direction === 'bottom' && align === ''],
                                    [styles.leftSheet, direction === 'left'],
                                    [styles.rightSheet, direction === 'right'],
                                    [styles.topLeftSheet, direction === 'top' && align === 'left'],
                                    [styles.topRightSheet, direction === 'top' && align === 'right'],
                                    [styles.bottomLeftSheet, direction === 'bottom' && align === 'left'],
                                    [styles.bottomRightSheet, direction === 'bottom' && align === 'right'])]}>
                                <Animated.View style={[styles.body, sc([{
                                    width: contentWidth,
                                    height: contentHeight || aHeight,
                                    transform: [{
                                        translateY: this.state.offset.interpolate({
                                            inputRange: [0, 1],
                                            outputRange: [-(contentHeight || aHeight), 0]
                                        }),
                                    }]
                                }, direction === 'top'], [{
                                    width: contentWidth,
                                    height: contentHeight || aHeight,
                                    transform: [{
                                        translateY: this.state.offset.interpolate({
                                            inputRange: [0, 1],
                                            outputRange: [contentHeight || aHeight, 0]
                                        }),
                                    }]
                                }, direction === 'bottom'], [{
                                    width: contentWidth || aWidth,
                                    height: contentHeight || '100%',
                                    transform: [{
                                        translateX: this.state.offset.interpolate({
                                            inputRange: [0, 1],
                                            outputRange: [-(contentWidth || aWidth), 0]
                                        }),
                                    }]
                                }, direction === 'left'], [{
                                    width: contentWidth || aWidth,
                                    height: contentHeight || '100%',
                                    transform: [{
                                        translateX: this.state.offset.interpolate({
                                            inputRange: [0, 1],
                                            outputRange: [contentWidth || aWidth, 0]
                                        }),
                                    }]
                                }, direction === 'right']), {}]}>
                                    <Layout dir='top' box='first'
                                            style={[styles.content, sc([styles.topWrapper, direction === 'top' && align === ''],
                                                [styles.bottomWrapper, direction === 'bottom' && align === ''],
                                                [styles.leftWrapper, direction === 'left'],
                                                [styles.rightWrapper, direction === 'right'],
                                                [styles.topLeftWrapper, direction === 'top' && align === 'left'],
                                                [styles.topRightWrapper, direction === 'top' && align === 'right'],
                                                [styles.bottomLeftWrapper, direction === 'bottom' && align === 'left'],
                                                [styles.bottomRightWrapper, direction === 'bottom' && align === 'right'])]}>
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
        </View>;
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
    opacityContainer: {
        position: 'absolute',
        left: 0,
        right: 0,
        top: 0,
        bottom: 0
    },
    sheet: {
        position: 'absolute'
    },
    topSheet: {
        left: 0,
        right: 0,
        top: 0
    },
    bottomSheet: {
        left: 0,
        right: 0,
        bottom: 0
    },
    leftSheet: {
        left: 0,
        top: 0,
        bottom: 0
    },
    rightSheet: {
        right: 0,
        top: 0,
        bottom: 0
    },
    topLeftSheet: {
        top: 0,
        left: 0
    },
    topRightSheet: {
        top: 0,
        right: 0
    },
    bottomLeftSheet: {
        bottom: 0,
        left: 0
    },
    bottomRightSheet: {
        bottom: 0,
        right: 0
    },
    body: {
        backgroundColor: '#ffffff'
    },
    content: {
        position: 'absolute',
        left: 0,
        right: 0,
        top: 0,
        bottom: 0
    },
    topWrapper: {
        borderBottomColor: Colors.BORDER,
        borderBottomWidth: 1
    },
    bottomWrapper: {
        borderTopColor: Colors.BORDER,
        borderTopWidth: 1
    },
    leftWrapper: {
        borderRightColor: Colors.BORDER,
        borderRightWidth: 1
    },
    rightWrapper: {
        borderLeftColor: Colors.BORDER,
        borderLeftWidth: 1
    },
    topLeftWrapper: {
        borderBottomColor: Colors.BORDER,
        borderBottomWidth: 1,
        borderRightColor: Colors.BORDER,
        borderRightWidth: 1
    },
    topRightWrapper: {
        borderBottomColor: Colors.BORDER,
        borderBottomWidth: 1,
        borderLeftColor: Colors.BORDER,
        borderLeftWidth: 1
    },
    bottomLeftWrapper: {
        borderTopColor: Colors.BORDER,
        borderTopWidth: 1,
        borderRightColor: Colors.BORDER,
        borderRightWidth: 1
    },
    bottomRightWrapper: {
        borderTopColor: Colors.BORDER,
        borderTopWidth: 1,
        borderLeftColor: Colors.BORDER,
        borderLeftWidth: 1
    },
    header: {
        paddingLeft: 20,
        paddingRight: 20,
        height: Sizes.HEADER_HEIGHT,
        borderBottomColor: Colors.BORDER,
        borderBottomWidth: 1 / PixelRatio.get()
    }
});

mixin.onClass(Popup, TimerMixin);

export default Popup