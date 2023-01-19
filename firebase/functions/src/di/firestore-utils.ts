
import {
  DocumentData,
  Query,
  QuerySnapshot,
} from 'firebase-admin/firestore';

export class FirestoreUtils {
  static async toData(query: Query<DocumentData>): Promise<any[]> {
    const snapshot: QuerySnapshot<DocumentData> = await query.get();
    const results: any[]  = [];
    snapshot.forEach(doc => {
      results.push(doc.data());
    });
    return results;
  }

}
