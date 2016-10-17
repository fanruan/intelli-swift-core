import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import React, {
    Component,
    StyleSheet,
    Modal,
    View,
    Animated,
    Easing,
    Dimensions,
} from 'lib';
import {Layout} from 'layout'

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
        position: 'absolute',
        left: 0,
        right: 0,
        top: 0,
        bottom: 0,
        backgroundColor: 'rgba(56,56,56,0.6)'
    }
});

export default Overlay