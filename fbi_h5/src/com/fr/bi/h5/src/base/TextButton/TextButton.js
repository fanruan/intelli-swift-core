import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import {cn, sc, requestAnimationFrame, emptyFunction} from 'core'
import React, {
    Component,
    StyleSheet
} from 'lib'

import Button from '../Button/Button'

class TextButton extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        selected: null,
        disabled: false,
        invalid: false,
        stopPropagation: false,
        effect: true,
        onSelected: emptyFunction,
        onPress: emptyFunction
    };

    state = {};

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillUpdate() {

    }

    render() {
        const {text, ...props}=this.props;
        return <Button {...props}>
            <Text>{text}</Text>
        </Button>
    }

}
mixin.onClass(TextButton, PureRenderMixin);
export default TextButton
