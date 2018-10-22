import { observer } from 'mobx-react'
import * as React from 'react';
import './App.css';

import {Connect} from './components/Connect'
import {Connected} from './components/Connected'
import {ViewName, ViewStore} from './stores/ViewStore'

export const App2 = observer(({ store }) => (
  <div className="App">
    <header className="App-header">
      <h1 className="App-title">Welcome to Reactive</h1>
    </header>
    <p className="App-intro">
      We're getting started.
    </p>
    { renderCurrentView(store) }
  </div>
))


function renderCurrentView(store: ViewStore) {
  switch (store.currentView.name) {
    case ViewName.Connect:
      return <Connect store={ store }/>
    case ViewName.Connected:
      return <Connected view = { store.currentView } store={ store }/>
  }

  return null;
}
