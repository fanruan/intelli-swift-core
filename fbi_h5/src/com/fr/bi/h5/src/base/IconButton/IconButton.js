import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import {cn, sc, requestAnimationFrame, emptyFunction} from 'core'
import React, {
    Component,
    StyleSheet
} from 'lib'

import Button from '../Button/Button'
import Icon from '../Icon/Icon'

class IconButton extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        className: '',
        iconWidth: 16,
        iconHeight: 16,
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
        const {iconWidth, iconHeight, ...props}=this.props;
        return <Button {...props}>
            <Icon iconWidth={iconWidth} iconHeight={iconHeight}/>
        </Button>
    }

}
mixin.onClass(IconButton, PureRenderMixin);
export default IconButton
