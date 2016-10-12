import React, {
    Component,
    StyleSheet,
    View
} from 'lib'
import cn from 'classnames'


class HorizontalCenterLayout extends Component {
    constructor(props, context) {
        super(props, context);
    }

    render() {
        const {children, ...props} = this.props;
        return <View data-flex='main:center'  {...props}
                     className={cn('', props.className)}>{children}</View>
    }
}
export default HorizontalCenterLayout
