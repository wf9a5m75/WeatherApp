import { FirestoreUtils } from './firestore-utils';

const createDummyDoc = (data: string) => {
  const docSpy = jasmine.createSpyObj("DocumentData", ["data"]);
  docSpy.data.and.returnValue(data);
  return docSpy;
}

describe("FirestoreUtils", () => {
  it ("toData", async () => {
    // Mock: Query<DocumentData>
    const querySpy = jasmine.createSpyObj("Query", ["get"]);

    // Mock: QuerySnapshot<DocumentData>
    const querySnapshotSpy = jasmine.createSpyObj("QuerySnapshot", ["forEach"]);

    // create dummy data
    const docs = [];
    for (let i = 0; i < 3; i++) {
      docs.push(createDummyDoc(`data${i}`));
    }

    // Mock: implement forEach()
    querySnapshotSpy.forEach.and.callFake((iterator) => {
      docs.forEach(iterator);
    });

    // Mock: implement query.get()
    querySpy.get.and.returnValue(Promise.resolve(querySnapshotSpy));

    // Test: toData() should return the all contents from each docs.
    const results = await FirestoreUtils.toData(querySpy);
    expect(results.length).toBe(docs.length);
    expect(results).toEqual(["data0", "data1", "data2"]);
  });
});
