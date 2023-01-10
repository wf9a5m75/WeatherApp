export class App {
  _markers = [];
  _reverseQ = [];

  constructor(
    map,
    db,
    geocoder,
    createMarkerFactory,
    getPrefecture,
    getDatetime,
    PREFECTURE,
  ) {
    this.createMarkerFactory = createMarkerFactory;
    this.map = map;
    this.db = db;
    this.geocoder = geocoder;
    this.getPrefecture = getPrefecture;
    this.getDatetime = getDatetime;
    this.PREFECTURE = PREFECTURE;
    this.map.addListener('click', evt => this.onMapClicked(evt));
  }

  async loadAllData() {
    return this.db.readAll();
  }

  async restoreMarkers() {
    // Restore markers from DB values.
    const self = this;
    const dataSet = await this.loadAllData();
    this._markers.forEach((marker) => {
      marker.clearInstanceListeners();
      marker.put('city', undefined);
      marker.put('pref', undefined);
      marker.put('id', undefined);
      marker.put('position', undefined);
      marker.put('map', undefined);
    });
    this._markers.length = 0;
    dataSet.forEach((data) => {
      data.draggable = true;
      const marker = self.createMarkerFactory(data);
      marker.setMap(self.map);
      self._markers.push(marker);

      marker.addListener('dragend', () => self.onMarkerDragEed(marker));
    });
  }

  async onMarkerDragEed(marker) {
    // Clear city and pref properties if dragged.
    marker.set('city', undefined);
    marker.set('pref', undefined);
    const city = await this.reverseGeocoding(marker.getPosition());
    marker.set('city', city);
    marker.set('pref', this.getPrefecture(city));
  }

  async onMapClicked(event) {
    // Create a makrer at the clicked position
    const marker = this.createMarkerFactory({
      position: event.latLng,
      draggable: true
    });
    marker.setMap(this.map);
    marker.addListener('dragend', () => this.onMarkerDragEed(marker));
    marker.set('city', await this.reverseGeocoding(event.latLng));

    this._markers.push(marker);
  }

  async saveMarkers() {
    const self = this;

    // Convert markers to DB values
    const dataPromises = await this._markers.map(async (marker) => {

      // Obtain administrative address from latLng
      if (!marker.get('city')) {
        const city = await self.reverseGeocoding(marker.position);
        marker.set('city', city);
      }

      // Get prefecture
      if (!marker.get('pref')) {
        const city = marker.get('city');
        const pref = this.getPrefecture(city);
        marker.set('pref', pref);
      }

      // Generate hash ID
      const id = marker.get('id') || this.getHash(marker.position.toUrlValue());

      // Create DB value
      return {
        position: marker.getPosition().toJSON(),
        id,
        city: marker.get('city'),
        pref: marker.get('pref'),
      };
    });

    // Wait all promises
    const data = await Promise.all(dataPromises);

    // Store to the DB
    console.log(data);
    await this.db.putAll(data);
    return data;
  }

  generateLocationsJSON(dataSet) {
    const self = this;
    const prefEng = Object.values(this.PREFECTURE);
    const prefTable = {};
    prefEng.forEach((pref) => {
      prefTable[pref] = [];
    });
    dataSet.forEach((data) => {
      prefTable[data.pref].push({
        'name': data.city,
        'id': data.id,
      });
    });

    const prefectures = Object.keys(prefTable).map((prefId) => {
      return {
        'id': prefId,
        'name': self.PREFECTURE[prefId],
        'cities': prefTable[prefId],
      };
    });

    const last_update = this.getDatetime(new Date());

    return {
      last_update,
      prefectures,
    };
  }

  getHash(str) {
    let hash = 0;
    if (str.length === 0) {
      return 0;
    }

    for (let i = 0; i < str.length; i++) {
      const c = str.charCodeAt(i);
      hash = ((hash << 5) - hash) + c;
      hash |= 0; // Convert to 32bit integer
    }
    return hash.toString(16);
  }


  async reverseGeocoding(location) {
    if (this._reverseQ.length > 0) {
      this._reverseQ.push(location);
    }
    // Reverse geocoding
    const response = await this.geocoder.geocode({
      location,
      language: 'ja'
    });

    const results = response.results.filter(
      (result) => {
        return (
          result.types.includes('postal_code') ||
          result.types.includes('plus_code')
        );
      }
    );
    if (results.length === 0) {
      return '';
    }

    const mem = {
      prefecture: '(prefecture)',
      city: '(city)',
    };
    results[0].address_components.forEach((component) => {
      if (component.types.includes('administrative_area_level_1')) {
        mem['prefecture'] = component.long_name;
      }
      if (component.types.includes('locality')) {
        mem['city'] = component.long_name;
      }
    });

    return `${mem['prefecture']}${mem['city']}`;
  }

  async firestoreTest() {
    await this.db.putAll([
      {"id": "something", "data": "hello"}
    ]);
  }
}
