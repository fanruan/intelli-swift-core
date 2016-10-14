import React, {
    Component,
    StyleSheet,
    View
} from 'lib'
import cn from 'classnames'


class Layout extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static defaultProps = {
        full: false,
        dir: 'left', //left,right,top,bottom
        main: 'left', //left,right,center,justify
        cross: 'stretch', //top,bottom,center,baseline,stretch
        box: '' //mean,first,last,justify
    };

    render() {
        const {children, dir, main, cross, box, full, style, ...props} = this.props;
        const currentStyle = {};
        if (full) {
            currentStyle.flex = 1;
        }
        return <View data-flex={`dir:${dir} main:${main} cross:${cross} box:${box}`} {...props}
                     className={cn('', props.className)} style={[currentStyle, style]}>{children}</View>
    }
}
export default Layout
