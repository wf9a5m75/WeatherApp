import { MyFirestore } from './my-firestore.js';
import { GeocoderProcessor } from './geocoder.js';

export class Factory {
  static _redIcon;
  static _blueIcon;
  static {
    this._redIcon = this.generateIcon([255, 0, 0, 255]);
    this._blueIcon = this.generateIcon([0, 0, 255, 255]);
  }

  static createMap(mapDiv, options) {
    options = options || {}
    return new google.maps.Map(mapDiv, options);
  }

  // static _onCityPropertyOfMarkerIsChanged = ()
  static createMarker(options) {
    const marker = new google.maps.Marker(options);
    if (options.city) {
      marker.setIcon(Factory._blueIcon);
    } else {
      marker.setIcon(Factory._redIcon);
    }

    marker.addListener('city_changed', (...params) => {
      const city = marker.get('city');
      if (city) {
        marker.setIcon(Factory._blueIcon);
      } else {
        marker.setIcon(Factory._redIcon);
      }
    });
    return marker;
  }

  // static async createDB(dbName, objectName) {
  //   return new MyIndexedDB(dbName, objectName);
  // }
  static createDB(firebaseApp, collectionName) {
    return new MyFirestore(firebaseApp, collectionName)
  }

  static createGeocoder() {
    return new GeocoderProcessor();
  }

  static generateIcon(rgba) {
    return {
      'path': 'm12 0c-4.4183 2.3685e-15 -8 3.5817-8 8 0 1.421 0.3816 2.75 1.0312 3.906 0.1079 0.192 0.221 0.381 0.3438 0.563l6.625 11.531 6.625-11.531c0.102-0.151 0.19-0.311 0.281-0.469l0.063-0.094c0.649-1.156 1.031-2.485 1.031-3.906 0-4.4183-3.582-8-8-8zm0 4c2.209 0 4 1.7909 4 4 0 2.209-1.791 4-4 4-2.2091 0-4-1.791-4-4 0-2.2091 1.7909-4 4-4z',
      'fillColor': `rgb(${rgba[0]},${rgba[1]},${rgba[2]})`,
      'fillOpacity': rgba[3] / 255,
      'scale': 1.3,
      'strokeWeight': 1,
      'strokeColor': 'rgb(255, 255, 255)',
      'strokeOpacity': 0.65,
      'anchor': {
        x: 12,
        y: 27
      }
    };
  }
}
