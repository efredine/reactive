import * as React from 'react'
import * as ReactDOM from 'react-dom'
// import App from './App'
import './index.css'
import registerServiceWorker from './registerServiceWorker'

import { fetchJSONwithCredentials } from './api'
import { App2 } from './App2'
import { startRouter } from './stores/router'
import { ViewStore } from './stores/ViewStore'

const viewStore = new ViewStore(fetchJSONwithCredentials)
startRouter(viewStore)

ReactDOM.render(
  <App2 store = { viewStore } />,
  document.getElementById('root') as HTMLElement
);
registerServiceWorker()
