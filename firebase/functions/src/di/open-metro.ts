import {
  IDailyForecast,
  IForecast,
  IWeeklyForecastsResult,
} from '../api_interface';
export * from '../api_interface';
import axios from 'axios';

export interface IHourlyUnits {
  time: string;
  temperature_2m: string;
  cloudcover: string;
  rain: string;
  snowfall: string;
};

export interface IHourlyResult {
  time: string[];
  temperature_2m?: number[];
  relativehumidity_2m?: number[];
  windspeed_10m?: number[];
  cloudcover?: number[];
  rain?: number[];
  snowfall?: number[];

  [key: string]: any;
};
export interface IApiResult {
  latitude: number;
  longitude: number;
  generationtime_ms: number;
  utc_offset_seconds: number;
  timezone: string;
  timezone_abbreviation: string;
  elevation: number;
  hourly_units?: IHourlyUnits;

  hourly?: IHourlyResult;

  [key: string]: any;
};


export const reduceToDailyForecasts = (
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

export const getRawForecastData = async (
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

export const convertToIForecasts = (
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
