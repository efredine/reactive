import createHistory from 'history/createBrowserHistory'
import { autorun } from 'mobx'
import {ViewStore} from './ViewStore'

export function startRouter(viewStore: ViewStore) {

  const history = createHistory()

  autorun(() => {
    const path = viewStore.currentPath
    if (path !== history.location.pathname) {
      history.push(path, {})
    }
  })
}
