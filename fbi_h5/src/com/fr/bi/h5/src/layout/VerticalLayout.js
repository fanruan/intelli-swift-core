import React, {
    Component,
    StyleSheet,
    View
} from 'lib'

import Layout from './Layout'

class VerticalLayout extends Component {
    constructor(props, context) {
        super(props, context);
    }

    render() {
        const {children, ...props} = this.props;
        return <Layout dir={'top'} {...props}>{children}</Layout>
    }
}
export default VerticalLayout
