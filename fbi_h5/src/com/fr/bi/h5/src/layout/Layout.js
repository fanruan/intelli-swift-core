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
        flex: '',
        dir: 'left', //left,right,top,bottom
        main: 'left', //left,right,center,justify
        cross: 'stretch', //top,bottom,center,baseline,stretch
        box: '' //mean,first,last,justify
    };

    render() {
        const {children, dir, main, cross, box, flex, ...props} = this.props;
        return <View data-flex-box={flex === true ? 1 : flex}
                     data-flex={`dir:${dir} main:${main} cross:${cross} box:${box}`} {...props}
                     className={cn('', props.className)}>{children}</View>
    }
}
export default Layout
