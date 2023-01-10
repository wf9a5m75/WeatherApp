export class MyIndexedDB {
  constructor(dbName, objectName) {
    this.dbName = dbName;
    this.objectName = objectName;
  }

  async open() {
    const self = this;
    return new Promise((resolve, reject) => {
      const request = window.indexedDB.open(this.dbName);
      request.onerror = (err) => reject(err);
      request.onsuccess = (ev) => {
        self.db = ev.target.result;
        console.log(self.db);
        resolve();
      }
      request.onupgradeneeded = (ev) => {
        const db = ev.target.result;
        db.createObjectStore(self.objectName, {
          keyPath: 'id',
        });
      };
    });
  }

  async readAll() {
    const self = this;
    return new Promise((resolve) => {
      const docs = self.db.transaction(this.objectName, 'readwrite');
      const store = docs.objectStore(this.objectName);

      const results = [];
      store.openCursor().onsuccess = (event) => {
        const cursor = event.target.result;
        if (cursor) {
          results.push(cursor.value);
          cursor.continue();
        } else {
          resolve(results);
        }
      };
    });
  }

  async putAll(dataSet) {
    const self = this;
    return new Promise((resolve, reject) => {
      const docs = self.db.transaction(this.objectName, 'readwrite');
      const store = docs.objectStore(this.objectName);
      const promises = dataSet.map((data) => {
        return new Promise((onSuccess, onError) => {
          const deffer = store.put(data);
          deffer.onsuccess = onSuccess;
          deffer.onerror = onError;
        });
      });
      resolve(promises);
    })
    .then(async (promises) => {
      await Promise.all(promises);
    });
  }
}
