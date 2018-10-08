/**
 * Basisklasse für alle Stores.
 */
export abstract class Store {

  /** Alle Store-Instanzen, die von dieser Klasse abgeleitet wurden. */
  static stores: Store[] = []

  /** Setzt alle Stores auf ihre Anfangszustände zurück. */
  static resetAllStores(): void {
    for (const store of Store.stores) {
      store.init()
    }
  }

  /** Erzeugt einen neuen Store. */
  constructor() {
    this.init()
    Store.stores.push(this)
  }

  /** Setzt alle Werte im Store auf den Anfangszustand zurück. */
  abstract init(): void

}
