import React, { PropTypes } from 'react';
import cn from 'classnames';
import style from './flexboxgrid.css';
import {View} from 'lib'

const ModificatorType = PropTypes.oneOf(['xs', 'sm', 'md', 'lg']);
const modificatorKeys = ['start', 'center', 'end', 'top', 'middle', 'bottom', 'around', 'between', 'first', 'last'];

const propTypes = {
    reverse: PropTypes.bool,
    start: ModificatorType,
    center: ModificatorType,
    end: ModificatorType,
    top: ModificatorType,
    middle: ModificatorType,
    bottom: ModificatorType,
    around: ModificatorType,
    between: ModificatorType,
    first: ModificatorType,
    last: ModificatorType,
    className: PropTypes.string,
    children: PropTypes.node
};

function getClassNames(props) {
    const modificators = ['row'];

    for (let i = 0; i < modificatorKeys.length; ++i) {
        const key = modificatorKeys[i];
        const value = props[key];
        if (value) {
            modificators.push(`${key}-${value}`);
        }
    }

    if (props.reverse) {
        modificators.push('reverse');
    }

    return cn(props.className, modificators);
}

export default function Row(props) {
    return <View
        {...props}
        className = {getClassNames(props)}
        >
        {props.children}
    </View>;
}

Row.propTypes = propTypes;
