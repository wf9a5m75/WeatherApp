import { getFirestoreManager } from '../di/factory';
import { asyncWait } from '../di/utils';
import {
  reduceToDailyForecasts,
  getRawForecastData,
  convertToIForecasts,
  IDailyForecast,
} from '../di/open-metro';
import * as functions from 'firebase-functions';

const targetCities = [];

const doProcess = async () => {
  if (targetCities.length === 0) {
    return;
  }
  const task = targetCities.shift();
  const forecasts: IDailyForecast[] = await getForecastFromOpenMetro(
    task.position.lat,
    task.position.lng,
  );

  console.log(forecasts);

  await asyncWait(100);
  // return await doProcess();
};

const getForecastFromOpenMetro = async (
  latitude: number,
  longitude: number
): Promise<IDailyForecast[]> => {
  const rawData = await getRawForecastData(latitude, longitude);
  if (!rawData) {
    await asyncWait(1000);
    return await getForecastFromOpenMetro(latitude, longitude);
  }
  const forecasts = convertToIForecasts(rawData);
  await asyncWait(100);
  return reduceToDailyForecasts(forecasts);
};

export const updateweather = functions.pubsub.schedule('*/15 * * * *').onRun(async (context) => {
  const db = getFirestoreManager();

  const citySet = await db.getDocsSet('cities');

  citySet.forEach(docRef => {
    const data = docRef.data();
    targetCities.push({
      id: data.id,
      position: data.position,
    });
  });
  // console.log(targetCities);

  await doProcess();
});
