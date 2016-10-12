import React, {
    Component,
    StyleSheet,
    View
} from 'lib'
import cn from 'classnames'


class AbsoluteLayout extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static defaultProps = {
        left: '',
        right: '',
        top: '',
        bottom: '',
        full: false
    };

    render() {
        const {style, children, left, right, top, bottom, full, ...props} = this.props;
        const st = {};
        if (full) {
            st.left = 0;
            st.right = 0;
            st.top = 0;
            st.bottom = 0;
        } else {
            if (left != null) {
                st.left = left;
            }
            if (right != null) {
                st.right = right;
            }
            if (top != null) {
                st.top = top;
            }
            if (bottom != null) {
                st.bottom = bottom;
            }
        }
        return <View {...props} style={[style, {position: 'absolute'}, st]}
                     className={cn('react-view', props.className)}>{children}</View>
    }
}
export default AbsoluteLayout
