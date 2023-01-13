import {
  CollectionReference,
  DocumentData,
  DocumentSnapshot,
  DocumentReference,
  Firestore,
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

  async getDocsSet(
    collectionName: string,
    whereCause?: string
  ): Promise<DocsSet> {
    const collRef: CollectionReference = this.db.collection(collectionName);
    const snapshot: QuerySnapshot<DocumentData> = await collRef.get();
    const results: QueryDocumentSnapshot<DocumentData>[]  = [];
    snapshot.forEach(doc => {
      results.push(doc);
    });
    return new DocsSet(results);
  }
}

export class DocsSet {
  constructor(private docs: QueryDocumentSnapshot<DocumentData>[]) { }

  forEach(callback:
    (doc: QueryDocumentSnapshot<DocumentData>, index: number) => void
  ): void {
    this.docs.forEach(callback);
  }

  toData(): any[] {
    const results: any[] = [];
    this.docs.forEach((doc) => results.push(doc.data()));
    return results;
  }
}
