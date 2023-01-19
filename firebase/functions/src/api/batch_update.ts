import { onRequest } from 'firebase-functions/v2/https';
import axios from 'axios';
import {
  IApiResult,
} from '../openmetro';
import {
  IDailyForecast,
  IForecast,
  IWeeklyForecastsResult,
} from '../api_interface';
import {
  awaitTimeout,
  dateISO8601,
  getHash
} from '../di/utils';

import {
  getCacheManager,
  getFirestoreManager,
  FirestoreManager,
  CacheManager
} from '../di/factory';
import { FirestoreUtils } from '../di/firestore-utils';

export const batchUpdate = onRequest(async (req, res) => {
  const db = getFirestoreManager();
  const startTime = new Date();
  const timestamp = startTime.getTime();
  const result = await getLastUpdateTimestamp(db);

  const collRef = db.getCollectionRef("cities");
  const cities = await FirestoreUtils.toData(collRef);
  const tasks = [];
  const results = [];
  let i = 0;
  cities.forEach(async (item) => {
    if (item.weather || i > 50) {
      return;
    }
    i++;
    results.push(item.id);
    tasks.push(getForecastDataAt(item.id, item.position.lat, item.position.lng));
  });

  if (i > 0) {
    const taskResults = await Promise.all(tasks);

    const tasks2 = [];
    taskResults.forEach(async (taskResult, idx: number) => {
      const docRef = db.getCollectionRef('cities').doc(taskResult.id);
      tasks2.push(db.setContents(docRef, {
        weather: {
          last_update: startTime,
          timestamp: startTime.getTime(),
          etag: getHash(`cities/${taskResult.id}/weather-${timestamp}`),
          forecast: taskResult.data
        }
      },
      {
        merge: true
      }));
    });

    await Promise.all(tasks2);
    await saveLastUpdateTimestamp(db, startTime);
  }

  res.json(results);

});

const saveLastUpdateTimestamp = async (db: FirestoreManager, time: Date): Promise<void> => {
  const docRef = await db.getDocRef('last_updates/weather');
  await db.setContents(docRef, {
    last_update: time,
    timestamp: time.getTime()
  });
}

const getLastUpdateTimestamp = async (db: FirestoreManager): Promise<number> => {
  const result = await db.getDocData('last_updates/weather');

  if (!result) {
    return -1;
  }
  return result.timestamp as number;
}

const getForecastDataAt = async (tag: string, latitude: number, longitude: number): Promise<any> => {
  const rawData = await getRawForecastData(latitude, longitude);
  if (!rawData) {
    return Promise.reject('Can not obtain the forecast data');
  }
  const forecasts = convertToIForecasts(rawData);
  return {
    id: tag,
    data: reduceToDailyForecasts(forecasts)
  };
};

const reduceToDailyForecasts = (
  forecasts: IForecast[]
): IDailyForecast[] => {
  const results: IDailyForecast[] = [];
  const N = forecasts.length;
  if (N === 0) {
    return results;
  }

  let i = 0;
  while (i < N) {
    const datePrefix = forecasts[i].time.replace(/T.*$/, '');
    const oneDay: IDailyForecast = {
      date: datePrefix,
      forecasts: [],
    };
    while ((i < N) && (forecasts[i].time.startsWith(datePrefix))) {
      oneDay.forecasts.push(forecasts[i]);
      i += 1;
    }
    results.push(oneDay);
  }
  return results;
};

const getRawForecastData = async (
  latitude: number,
  longitude: number,
): Promise<IApiResult | null> => {
  const url = `
  https://api.open-meteo.com/v1/forecast?
  latitude=${latitude}&
  longitude=${longitude}&
  hourly=temperature_2m,cloudcover,rain,snowfall&
  timezone=Asia/Tokyo
  `.replace(/[\s\n\r]/g, '');

  try {
    const response = await axios.get(url);
    return response.data as Promise<IApiResult>;
  } catch (exception) {
    return Promise.resolve(null);
  }
}

const convertToIForecasts = (
  data: IApiResult,
): IForecast[] => {
  const results: IForecast[] = [];

  data.hourly!!.time.forEach((
    time: string,
    idx: number,
  ): void => {
    let status = 'sunny';
    const cloudcover = data.hourly!!.cloudcover!![idx];
    const rain = data.hourly!!.rain!![idx];
    const snowfall = data.hourly!!.snowfall!![idx];

    if (cloudcover >= 30) {
      if ((rain < 5) && (snowfall === 0)) {
        status = 'cloudy';
      } else if (rain > 4) {
        status = 'rain';
      } else {
        status = 'snow';
      }
    }

    results.push({
      time,
      'temperature': data.hourly!!.temperature_2m!![idx],
      status,
    });
  });

  return results;
};
