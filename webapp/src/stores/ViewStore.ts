import {action, computed, observable} from 'mobx'
import {fromPromise, IPromiseBasedObservable} from 'mobx-utils'

export enum ViewName {
  Connect,
  Connected,
}

export interface View<T> {
  document:  IPromiseBasedObservable<T>
  name: ViewName
}

export interface User {
  name: string
}

export class ViewStore {

  @observable currentUser?: User
  @observable currentView: View<JSON>
  private fetch: <T>(url: string, body?:any) => Promise<T>

  constructor(fetch: <T>(url: string, body?:any) => Promise<T>) {
    this.fetch = fetch
    this.currentView = {
      document: fromPromise( Promise.resolve({} as JSON) ),
      name: ViewName.Connect
    };
  }

  @computed get currentPath() {
    switch(this.currentView.name) {
      case ViewName.Connected: return '/connected'
      case ViewName.Connect: return '/connect'
    }
  }

  @computed get isAuthenticated() {
    return this.currentUser !== null
  }

  @action connect = () => {
    this.currentView = {
      document: fromPromise(this.fetch('/getCompanyInfo')),
      name: ViewName.Connected
    }
  }
}
