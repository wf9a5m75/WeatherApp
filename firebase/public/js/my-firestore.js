import {
  collection,
  doc,
  getDocs,
  getFirestore,
  setDoc,
  updateDoc,
} from 'https://www.gstatic.com/firebasejs/9.15.0/firebase-firestore.js';

export class DataDoc {
  _data = {}
  _updated = false;
  _new = false;
  doc = undefined;

  constructor(doc, data) {
    this.doc = doc;
    this._data = data;
    this._new = doc === null || doc === undefined;
  }

  get(key) {
    return this._data[key];
  }

  set(key, value) {
    this._updated = true;
    this._data[key] = value;
  }

  get data() {
    return this._data;
  }

  get updated() {
    return this._updated;
  }
  get isNew() {
    return this._new;
  }

  resetStates() {
    this._isNew = false;
    this._updated = false;
  }
}

export class MyFirestore {
  constructor(firebaseApp, collectionName) {
    this.db = getFirestore(firebaseApp);
    this.collectionName = collectionName;
  }

  createDataDoc(id, data) {
    const dataDoc = new DataDoc(null, data)
    dataDoc.doc = doc(this.db, this.collectionName, id);
    return dataDoc;
  }


  async readAll() {
    const collRef = collection(this.db, this.collectionName);
    const querySnapshot = await getDocs(collRef);
    const results = [];
    querySnapshot.forEach((doc) => {
      results.push(new DataDoc(doc.ref, doc.data()));
    });
    return results;
  }

  async put(dataDoc) {
    const collRef = collection(this.db, this.collectionName);
    if (!dataDoc.isNew && !dataDoc.updated) {
      return;
    }

    if (dataDoc.updated) {
      await updateDoc(dataDoc.doc, dataDoc.data);
    } else {
      await setDoc(dataDoc.doc, dataDoc.data)
    }
  }
  async putAll(dataDocs) {

    const collRef = collection(this.db, this.collectionName);
    const targets = dataDocs.filter((item) => item.updated || item.isNew);
    if (targets.lenght === 0) {
      return;
    }

    const promises = targets.forEach((item) => {
      const updated = item.updated;
      item.resetStates();
      if (item.updated) {
        return updateDoc(item.doc, item.data);
      } else {
        return setDoc(item.doc, item.data)
      }
    });
    await Promise.all(promises);
  }

  async update(data) {
    await updateDoc(doc(this.db, "cities", "something"), {
      'hello': 'world'
    });
  }
}
