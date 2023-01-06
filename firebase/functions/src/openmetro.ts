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
