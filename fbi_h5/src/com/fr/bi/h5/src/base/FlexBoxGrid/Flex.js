import React, { PropTypes } from 'react';
import cn from 'classnames';
import style from './flexboxgrid.css';
import {View} from 'lib'
const propTypes = {
    fluid: PropTypes.bool,
    className: PropTypes.string,
    children: PropTypes.node
};

export default function Flex(props) {
    const {fluid, className, children, ...others} = props;
    const containerClass = fluid ? 'container-fluid' : 'container';

    return <View className={cn(className, containerClass)} {...others}>{children}</View>;
}

Flex.propTypes = propTypes;
