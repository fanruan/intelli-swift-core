import React, {
    Component,
    StyleSheet,
    View
} from 'lib'
import cn from 'classnames'

class VerticalCenterLayout extends Component {
    constructor(props, context) {
        super(props, context);
    }

    render() {
        const {children, ...props} = this.props;
        return <View data-flex='cross:center'  {...props}
                     className={cn('', props.className)}>{children}</View>
    }
}
export default VerticalCenterLayout
