import { FirestoreManager } from './firestore-manager';

describe("di/firestore-manager", () => {
  let instance: FirestoreManager;

  beforeEach(() => {

    // Mock: Create a firestore mock
    const firestoreSpy = jasmine.createSpyObj("Firestore", [
      "collection",
      "doc"
    ]);

    // Mock: docRef.data()
    const dummySnap = jasmine.createSpyObj('DocumentSnapshot', ['data'], {
      'exists': true,
    });
    dummySnap.data.and.returnValue("Hello World");

    // Mock: firestore.doc()
    firestoreSpy.doc.and.callFake((path) => {
      const docSpy = jasmine.createSpyObj('DocumentReference',
        ['get'],
        {
          path,
        }
      );

      // Mock: docRef.get()
      docSpy.get.and.callFake(() => {
        return dummySnap;
      })
      return docSpy;
    });

    // Mock: firestore.collection()
    firestoreSpy.collection.and.callFake((path) => {
      const collectionSpy = jasmine.createSpyObj('CollectionReference',
        [],
        {
          path,
        }
      );
      return collectionSpy;
    });

    // Create an instance
    instance = new FirestoreManager(firestoreSpy);
  });

  it ("getDocData", async () => {
    const result = await instance.getDocData("cities/something");
    expect(result).toBe("Hello World");
  });
  it ("getDocRef", () => {
    const docRef = instance.getDocRef("cities/something");
    expect(docRef.path).toBe("cities/something");
  });
  it ("getCollection", () => {
    const collRef = instance.getCollectionRef("cities");
    expect(collRef.path).toBe("cities");
  });
});
