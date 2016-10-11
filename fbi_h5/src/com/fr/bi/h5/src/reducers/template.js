import Immutable from 'immutable'
import * as ActionTypes from '../constants/ActionTypes'

const initialState = Immutable.fromJS(BH.STORE.popConfig);

export default function template(state = initialState, action) {
    switch (action.type) {
        case ActionTypes.UPDATE_TEMPLATE:
            return action.$template;

        case ActionTypes.UPDATE_WIDGET:
            return state.setIn(['widgets', action.wId], action.$widget);
            break;
        default:
            return state
    }
}
