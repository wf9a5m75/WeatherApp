export class GeocoderProcessor {
  _q = [];
  constructor() {
    this._geocoder = new google.maps.Geocoder();
  }

  _doProcess() {
    if (this._q.length === 0) {
      return;
    }
    const self = this;

    const params = this._q.shift();
    console.log(params);
    this._geocoder.geocode(params.request)
    .then((result) => {
      params.resolve(result);
    })
    .catch(params.reject)
    .finally(() => {
      setTimeout(() => self._doProcess(), 500 + ~~(Math.random() * 250));
    });
  }

  async geocode(request) {
    const self = this;
    return new Promise((resolve, reject) => {
      self._q.push({
        request,
        resolve,
        reject,
      });

      self._doProcess();
    });
  }
}
