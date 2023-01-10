import {
  addDoc,
  collection,
  getDocs,
  getFirestore,
} from 'https://www.gstatic.com/firebasejs/9.15.0/firebase-firestore.js';

export class MyFirestore {
  constructor(firebaseApp, collectionName) {
    this.db = getFirestore(firebaseApp);
    console.log(this.db);
    this.collectionName = collectionName;
    if (window.location.host === 'localhost') {
      this.db.connectFirestoreEmulator(this.db, 'localhost', 8080);
    }

  }


  async readAll() {
    const self = this;
    const collRef = collection(this.db, this.collectionName);
    const querySnapshot = await getDocs(collRef);
    const results = [];
    querySnapshot.forEach((doc) => results.push(doc.data()));
    console.log(results);
    return results;
  }

  async putAll(dataSet) {
    const collRef = collection(this.db, this.collectionName);
    const promises = dataSet.map((data) => {
      return addDoc(collRef, data);
    });
    await Promise.all(promises);
  }
}
