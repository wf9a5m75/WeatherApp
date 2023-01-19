import { onRequest } from 'firebase-functions/v2/https';
import {
  getCacheManager,
  getFirestoreManager,
} from '../di/factory';
import { PREFECTURE } from '../const/';
import { dateISO8601 } from '../di/utils';
import { StatusCodes } from 'http-status-codes';
import { FirestoreUtils } from '../di/firestore-utils';

type ICity = {
  id: string;
  name: string;
}
type IPrefecture = {
  id: string;
  name: string;
  cities: ICity[];
}
exports.locations = onRequest(async (req, res) => {
  const db = getFirestoreManager();
  const cacheMgr = getCacheManager("cities");

  // Compare the `If-None-Match` value and the cache ETag values.
  // If they are the same, the cache of the client side is valid.
  const isValidETag = await cacheMgr.validateETag(req.get('If-None-Match'));
  if (isValidETag) {
    // HTTP 304: Not Modified
    res.status(StatusCodes.NOT_MODIFIED).end();
    return;
  }

  // Compare the last update of the `last_updates/cities` and
  // the value of `cache/cities.meta`.
  // If they are the same, the contents are not changed.
  // So, returns the cached data
  const lastUpdate  = await db.getDocData('last_updates/cities');
  const hasDataNotChanged = await cacheMgr.validateLastUpdate(lastUpdate.timestamp);

  if (hasDataNotChanged) {
    const contents = await cacheMgr.getContents();
    const etag = await cacheMgr.getETag();
    if (contents && etag) {
      res.setHeader('ETag', etag);
      res.setHeader('Content-Type', 'Application/json');
      res.status(StatusCodes.OK);
      res.send(contents).end();
      return;
    }
  }

  // If there is no cache data, we need to create it.
  const citiesRef = db.getCollection('cities');
  const cities = await FirestoreUtils.toData(citiesRef);

  const pref_cities: {
    [key: string]: IPrefecture;
  } = {};

  const prefIDs = Object.keys(PREFECTURE);
  prefIDs.forEach((prefId: string) => {
    pref_cities[prefId] = {
      id: prefId,
      name: PREFECTURE[prefId],
      cities: [],
    }
  });

  cities.forEach((cityData: any) => {
    if (!(cityData.pref in pref_cities)) {
      console.error(cityData);
      throw "unknow pref";
    }
    pref_cities[cityData.pref].cities.push({
      name: cityData.city,
      id: cityData.id,
    });
  });

  const resultPrefectures = [];
  prefIDs.forEach((prefId: string) => {
    resultPrefectures.push(pref_cities[prefId]);
  });

  const lastUpdateISO8601 = dateISO8601(new Date(lastUpdate.timestamp));
  const result = {
    last_update: lastUpdateISO8601,
    prefectures: resultPrefectures,
  };

  // Save cache data, then obtain the ETag
  const txt = JSON.stringify(result);
  const etag = await cacheMgr.saveCache(
    lastUpdate.timestamp,
    txt,
  );

  // Return the API response
  res.setHeader('ETag', etag);
  res.status(StatusCodes.OK);
  res.json(result).end();
});
