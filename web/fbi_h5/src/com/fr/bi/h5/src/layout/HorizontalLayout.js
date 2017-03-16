import React, {
    Component,
    StyleSheet,
    View
} from 'lib'

import Layout from './Layout'

class HorizontalLayout extends Component {
    constructor(props, context) {
        super(props, context);
    }

    render() {
        const {children, ...props} = this.props;
        return <Layout dir={'left'} {...props}>{children}</Layout>
    }
}
export default HorizontalLayout
