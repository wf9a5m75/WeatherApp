export class App {
  _markers = [];
  _reverseQ = [];

  constructor(
    authentication,
    map,
    db,
    geocoder,
    createMarker,
    infoWnd,
    getPrefecture,
    getDatetime,
    PREFECTURE,
  ) {
    this.auth = authentication;
    this.createMarker = createMarker;
    this.infoWnd = infoWnd;
    this.map = map;
    this.db = db;
    this.geocoder = geocoder;
    this.getPrefecture = getPrefecture;
    this.getDatetime = getDatetime;
    this.PREFECTURE = PREFECTURE;
    this.map.addListener('click', evt => this.onMapClicked(evt));
    this.map.set('editable', false);
  }

  async restoreMarkers() {
    // Restore markers from DB values.
    const self = this;
    const dataDocs = await this.db.readAll();
    this._markers.forEach((marker) => {
      marker.unbindAll();
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
        'doc': dataDoc,
        'map': self.map,
        'city': data.city,
        'pref': data.pref,
        'id': data.id,
      };
      const marker = self.createMarker(properties);
      self._markers.push(marker);

      marker.addListener('click', () => self.onMarkerClicked(marker));
      marker.addListener('dragend', () => self.onMarkerDragEed(marker));
    });

    self.map.addListener('editable_changed', () => {
      const isEditable = self.map.get('editable');
      self._markers.forEach((marker) => {
        marker.setDraggable(isEditable);
      });

    });

  }

  onMarkerClicked(marker) {
    console.log(marker);
    this.infoWnd.setContent(JSON.stringify({
      id: marker.get('id'),
      city: marker.get('city'),
      pref: marker.get('pref'),
    }, null, 2));
    this.infoWnd.open(marker);
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
    marker.set('pref', pref);
    dataDoc.set('city', city);
    dataDoc.set('pref', pref);
    await this.db.put(dataDoc);
  }

  async onMapClicked(event) {
    if (!this.map.get('editable')) {
      return;
    }
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

  async signIn() {
    await this.auth.signIn();
    this.map.set('editable', this.auth.user !== undefined);
  }

  getHash(str) {
    let hash = 0;
    if (str.length === 0) {
      return '0';
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

}
