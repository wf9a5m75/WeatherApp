export class App {
  _markers = [];
  _reverseQ = [];

  constructor(
    map,
    db,
    geocoder,
    createMarker,
    getPrefecture,
    getDatetime,
    PREFECTURE,
  ) {
    this.createMarker = createMarker;
    this.map = map;
    this.db = db;
    this.geocoder = geocoder;
    this.getPrefecture = getPrefecture;
    this.getDatetime = getDatetime;
    this.PREFECTURE = PREFECTURE;
    this.map.addListener('click', evt => this.onMapClicked(evt));
  }

  async restoreMarkers() {
    // Restore markers from DB values.
    const self = this;
    const dataDocs = await this.db.readAll();
    this._markers.forEach((marker) => {
      marker.clearInstanceListeners();
      marker.put('city', undefined);
      marker.put('pref', undefined);
      marker.put('id', undefined);
      marker.put('position', undefined);
      marker.put('map', undefined);
      marker.put('doc', undefined);
    });
    this._markers.length = 0;

    dataDocs.forEach((dataDoc) => {
      const data = dataDoc.data;

      const properties = {
        'position': data.position,
        'draggable': true,
        'doc': dataDoc,
        'map': self.map,
        'city': data.city,
        'pref': data.pref,
        'id': data.id,
      };
      const marker = self.createMarker(properties);
      self._markers.push(marker);

      marker.addListener('dragend', () => self.onMarkerDragEed(marker));
    });
  }

  async onMarkerDragEed(marker) {
    const position = marker.getPosition().toJSON();
    const dataDoc = marker.get('doc');
    dataDoc.set('position', position);

    // Clear city and pref properties if dragged.
    marker.set('city', undefined);
    marker.set('pref', undefined);

    const city = await this.reverseGeocoding(position);
    const pref = this.getPrefecture(city);
    marker.set('city', city);
    marker.set('pref', );
    dataDoc.set('city', city);
    dataDoc.set('pref', '');

  }

  async onMapClicked(event) {
    const position = event.latLng.toJSON();
    const city = await this.reverseGeocoding(position);
    const pref = this.getPrefecture(city);
    const id = this.getHash(event.latLng.toUrlValue());
    const dataDoc = this.db.createDataDoc(id, {
      id,
      position,
      city,
      pref,
    });

    // Create a makrer at the clicked position
    const marker = this.createMarker({
      position,
      draggable: true,
      city,
      pref,
      doc: dataDoc,
      map: this.map,
    });
    marker.addListener('dragend', () => this.onMarkerDragEed(marker));

    this._markers.push(marker);

    this.db.put(dataDoc);
  }

  // async saveMarkers() {
  //   const self = this;
  //
  //   // Convert markers to DB values
  //   const dataPromises = await this._markers.map(async (marker) => {
  //
  //     // Obtain administrative address from latLng
  //     if (!marker.get('city')) {
  //       const city = await self.reverseGeocoding(marker.position);
  //       marker.set('city', city);
  //     }
  //
  //     // Get prefecture
  //     if (!marker.get('pref')) {
  //       const city = marker.get('city');
  //       const pref = this.getPrefecture(city);
  //       marker.set('pref', pref);
  //     }
  //
  //     // Generate hash ID
  //     const id = marker.get('id') || this.getHash(marker.position.toUrlValue());
  //
  //     // Create DB value
  //     return {
  //       position: marker.getPosition().toJSON(),
  //       id,
  //       city: marker.get('city'),
  //       pref: marker.get('pref'),
  //     };
  //   });
  //
  //   // Wait all promises
  //   const data = await Promise.all(dataPromises);
  //
  //   // Store to the DB
  //   console.log(data);
  //   await this.db.putAll(data);
  //   return data;
  // }

  // generateLocationsJSON(dataSet) {
  //   const self = this;
  //   const prefEng = Object.values(this.PREFECTURE);
  //   const prefTable = {};
  //   prefEng.forEach((pref) => {
  //     prefTable[pref] = [];
  //   });
  //   dataSet.forEach((data) => {
  //     prefTable[data.pref].push({
  //       'name': data.city,
  //       'id': data.id,
  //     });
  //   });
  //
  //   const prefectures = Object.keys(prefTable).map((prefId) => {
  //     return {
  //       'id': prefId,
  //       'name': self.PREFECTURE[prefId],
  //       'cities': prefTable[prefId],
  //     };
  //   });
  //
  //   const last_update = this.getDatetime(new Date());
  //
  //   return {
  //     last_update,
  //     prefectures,
  //   };
  // }

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
    await this.db.update([
      {"id": "something", "data": "hello"}
    ]);
  }
}
