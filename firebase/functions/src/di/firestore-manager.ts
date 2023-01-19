import {
  CollectionReference,
  DocumentData,
  DocumentSnapshot,
  DocumentReference,
  Firestore,
  Query,
  QuerySnapshot,
  QueryDocumentSnapshot,
} from 'firebase-admin/firestore';

export class FirestoreManager {
  constructor(
    private db: Firestore
  ) { }

  async getDocData(resourcePath: string): Promise<any> {
    const docRef: DocumentReference = this.getDocRef(resourcePath);
    const snap: DocumentSnapshot = await docRef.get();
    if (!snap.exists) {
      return Promise.resolve(null);
    }
    return await snap.data();
  }

  getDocRef(resourcePath: string): DocumentReference {
    return this.db.doc(resourcePath);
  }

  getCollectionRef(
    collectionName: string
  ): CollectionReference {
    return this.db.collection(collectionName);
  }
}
