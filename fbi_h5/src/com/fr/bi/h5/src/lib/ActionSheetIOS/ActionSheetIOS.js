import TimerMixin from 'react-timer-mixin';
import mixin from 'react-mixin'
import React, {Component} from 'react';
import Easing from 'animated/lib/Easing';
import Animated from '../Animated/Animated.web';
import View from '../View/View.web';
import TouchableHighlight from '../Touchable/TouchableHighlight.web';
import TouchableWithoutFeedback from '../Touchable/TouchableWithoutFeedback.web';
import Text from '../Text/Text.web';
import Modal from '../Modal/Modal.web';
import Portal from '../Portal/Portal'
import StyleSheet from '../StyleSheet/StyleSheet.web';

const TITLE_HEIGHT = 55;
const BUTTON_HEIGHT = 45;
const GAP = 10;


/**
 * ActionSheetIOS.showActionSheetWithOptions({
                            title: 'title',
                            options: ['1','2', '3','取消', '删除'],
                            cancelButtonIndex: 3,
                            destructiveButtonIndex: 4
                        });
 */
class ActionSheetIOS extends Component {
    constructor(props) {
        super(props);
        this.state = {
            offset: new Animated.Value(0),
            opacity: new Animated.Value(0)
        };
    }

    static defaultProps = {
        options: []
    };

    static showActionSheetWithOptions(options, callback) {
        Portal.showModal('ActionSheetIOS', <ActionSheetIOS
            {...options}
            onClose={(buttonIndex)=> {
                Portal.closeModal('ActionSheetIOS');
                callback && callback(buttonIndex);
            }}
        />)
    };

    componentDidMount() {
        this._open();
    }

    _onPress(buttonIndex) {
        this._close(buttonIndex);
    }

    _onClose() {
        this._close(this.props.cancelButtonIndex);
    }

    _onDestruct() {
        this._close(this.props.destructiveButtonIndex)
    }

    render() {
        const {options, cancelButtonIndex, destructiveButtonIndex, title, message} = this.props;
        let cancelBtn, destructiveBtn, titleView, height = options.length * BUTTON_HEIGHT;
        const buttons = [];
        if (title != null && title != '') {
            titleView = <View style={styles.titleView}>
                <Text style={styles.titleText}>{this.props.title}</Text>
            </View>;
            height += TITLE_HEIGHT;
        }
        if (cancelButtonIndex != null) {
            cancelBtn = <View style={styles.tipBottom}>
                <TouchableHighlight style={styles.button} underlayColor='#f0f0f0' onPress={this._onClose.bind(this)}
                >
                    <Text style={styles.cancelButtonText}>{options[cancelButtonIndex]}</Text>
                </TouchableHighlight>
            </View>;
            height += 2 * GAP;
        }
        if (destructiveButtonIndex != null) {
            destructiveBtn =
                <TouchableHighlight style={styles.buttonContentView} underlayColor='#f0f0f0'
                                    onPress={this._onDestruct.bind(this)}
                >
                    <Text style={styles.destructButtonText}>{options[destructiveButtonIndex]}</Text>
                </TouchableHighlight>;
        }
        options.forEach((text, buttonIndex)=> {
            if (buttonIndex === cancelButtonIndex || buttonIndex === destructiveButtonIndex) {
                return;
            }
            buttons.push(<TouchableHighlight style={styles.buttonContentView} underlayColor='#f0f0f0'
                                             onPress={this._onPress.bind(this)}
            >
                <Text style={styles.buttonText}>{text}</Text>
            </TouchableHighlight>);
        });
        return (
            <View style={styles.container}>
                <Modal
                    transparent={true}
                >
                    <TouchableWithoutFeedback onPress={()=> {
                        this._close()
                    }}>
                        <Animated.View style={ [styles.mask, {
                            opacity: this.state.opacity
                        }] }>
                            <TouchableWithoutFeedback>
                                <Animated.View style={[styles.tip, {
                                    height: height,
                                    transform: [{
                                        translateY: this.state.offset.interpolate({
                                            inputRange: [0, 1],
                                            outputRange: [height, 0]
                                        }),
                                    }]
                                }]}>
                                    <View style={styles.tipBody}>
                                        {titleView}
                                        {buttons}
                                        {destructiveBtn}
                                    </View>
                                    {cancelBtn}
                                </Animated.View>
                            </TouchableWithoutFeedback>
                        </Animated.View>
                    </TouchableWithoutFeedback>
                </Modal>
            </View>
        );
    }

    _open() {
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

    _close(buttonIndex) {
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
            this.props.onClose && this.props.onClose(buttonIndex);
        });
    }
}

const styles = StyleSheet.create({
    container: {},
    mask: {
        position: 'absolute',
        left: 0,
        right: 0,
        top: 0,
        bottom: 0,
        backgroundColor: "rgba(56,56,56,0.6)"
    },
    tip: {
        position: 'absolute',
        left: 20,
        right: 20,
        bottom: 0,
        alignItems: "center",
        justifyContent: "space-between"
    },
    tipBody: {
        justifyContent: "space-between",
        alignSelf: 'stretch',
        backgroundColor: '#fff'
    },
    tipBottom: {
        marginBottom: GAP,
        alignSelf: 'stretch',
        backgroundColor: '#fff'
    },
    titleView: {
        height: TITLE_HEIGHT,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
    },
    titleText: {
        color: "#999999",
        fontSize: 14,
    },
    buttonContentView: {
        borderTopWidth: 0.5,
        borderColor: "#f0f0f0",
        height: BUTTON_HEIGHT,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
    },
    buttonText: {
        fontSize: 17,
        textAlign: "center",
    },

    button: {
        height: BUTTON_HEIGHT,
        justifyContent: 'center'
    },
    cancelButtonText: {
        fontSize: 17,
        color: "#007aff",
        textAlign: "center",
    },
    destructButtonText: {
        fontSize: 17,
        color: "#e6454a",
        textAlign: "center"
    }
});

mixin.onClass(ActionSheetIOS, TimerMixin);

export default ActionSheetIOS