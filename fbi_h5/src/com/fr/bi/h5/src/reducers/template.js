import Immutable from 'immutable'
import {UPDATE_TEMPLATE} from '../constants/ActionTypes'

const initialState = Immutable.fromJS(BH.STORE.popConfig);

export default function todos(state = initialState, action) {
  switch (action.type) {
    case UPDATE_TEMPLATE:
      return action.$template;

    default:
      return state
  }
}
