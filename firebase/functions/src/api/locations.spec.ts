import { ViewModel } from './locations';
import { StatusCodes } from 'http-status-codes';

describe("api/locations", () => {
  let dbSpy;
  let cacheSpy;
  let instance: ViewModel;
  let req;
  let res;

  beforeEach(() => {
    dbSpy = jasmine.createSpyObj("Database", [
      "getDocData",
      "getCollectionRef",
    ]);

    cacheSpy = jasmine.createSpyObj("Cache", [
      "validateETag",
      "validateLastUpdate",
      "getContents",
      "getETag",
      "saveCache"
    ])

    const getUTCDateTimeSpy = jasmine.createSpy("getLastUpdate")
                                .and.returnValue("2023-01-21T12:31")

    const collectionToDataSpy = jasmine.createSpy("collectionToDataSpy")
                                .and.callFake(async (collRef) => {
                                  if (collRef.path === "cities") {
                                    return Promise.resolve([
                                      {
                                        "city": "mihara city",
                                        "id": "-1092bc45",
                                        "position": {
                                          "lat": 34.39878630924938,
                                          "lng": 133.07776107663705
                                        },
                                        "pref": "hiroshima"
                                      },
                                      {
                                        "city": "kakegawa city",
                                        "id": "-1166fd05",
                                        "position": {
                                          "lat": 34.76436007982476,
                                          "lng": 137.99855900429887
                                        },
                                        "pref": "shizuoka"
                                      }
                                    ]);
                                  } else {
                                    return Promise.resolve([
                                      {
                                        "city": "something wrong",
                                        "id": "ssawwwad",
                                        "pref": "something wrong"
                                      },
                                      {
                                        "city": "something wrong",
                                        "id": "222rfafasf",
                                        "pref": "something wrong"
                                      }
                                    ]);
                                  }
                                });

    req = jasmine.createSpyObj("Request", ["get"]);
    req.get.withArgs("If-None-Match").and.returnValue(undefined);

    res = jasmine.createSpyObj("Response", ["setHeader", "status", "json", "send"])
    res.json.and.returnValue({
      end: jasmine.createSpy('Response.end').and.stub()
    });
    res.status.and.returnValue({
      end: jasmine.createSpy('Response.end').and.stub()
    });
    res.send.and.returnValue({
      end: jasmine.createSpy('Response.end').and.stub()
    });

    dbSpy.getCollectionRef.and.returnValue({
      "path": "cities"
    });
    dbSpy.getDocData.withArgs("last_updates/cities")
      .and.returnValue(Promise.resolve({
        'timestamp': 1673658582553,
        'last_update': new Date("2023-01-13T05:09:42Z-8")
      }));

    instance = new ViewModel(
      dbSpy,
      cacheSpy,
      getUTCDateTimeSpy,
      collectionToDataSpy,
      {
        "hiroshima": "Hiroshima, Japan",
        "shizuoka": "Shizuoka, Japan"
      }
    );

  });

  it ("should reply StatusCodes.OK if no server side cache is available", async ()=> {

    cacheSpy.validateLastUpdate.withArgs(1673658582553)
      .and.returnValue(false);

    cacheSpy.saveCache.and.returnValue('ETag hash');

    await instance.locations(req, res);

    expect(res.status).toHaveBeenCalledWith(StatusCodes.OK);
    expect(res.setHeader).toHaveBeenCalledWith('ETag', 'ETag hash');
    expect(res.json).toHaveBeenCalledWith({
      'last_update': '2023-01-21T12:31',
      'prefectures': [
        {
          'id': 'hiroshima',
          'name': 'Hiroshima, Japan',
          'cities': [
            {
              'name': 'mihara city',
              'id': '-1092bc45'
            }
          ]
        },
        {
          'id': 'shizuoka',
          'name': 'Shizuoka, Japan',
          'cities': [
            {
              'name': 'kakegawa city',
              'id': '-1166fd05'
            }
          ]
        }
      ]
    });
    expect(res.json().end).toHaveBeenCalled();
  });


  it ("should reply StatusCodes.INTERNAL_SERVER_ERROR if data is invalid", async ()=> {

    dbSpy.getCollectionRef.and.returnValue({
      "path": "invalid path"
    });

    await instance.locations(req, res);

    expect(res.status).toHaveBeenCalledWith(StatusCodes.INTERNAL_SERVER_ERROR);
    expect(res.status().end).toHaveBeenCalled();
  });

  it ("should reply StatusCodes.OK if server side cache is available", async ()=> {

    cacheSpy.validateLastUpdate.withArgs(1673658582553)
      .and.returnValue(true);

    cacheSpy.getETag.and.returnValue('saved ETag');
    cacheSpy.getContents.and.returnValue({
      'something': 'cached value'
    });

    await instance.locations(req, res);

    expect(res.status).toHaveBeenCalledWith(StatusCodes.OK);
    expect(res.setHeader).toHaveBeenCalledWith('ETag', 'saved ETag');
    expect(res.send).toHaveBeenCalledWith({
      'something': 'cached value'
    });
    expect(res.send().end).toHaveBeenCalled();
  });


  it ("should reply StatusCodes.NOT_MODIFIED if etag is matched", async ()=> {
    req.get.withArgs("If-None-Match").and.returnValue("requested ETag");

    cacheSpy.validateETag.withArgs("requested ETag")
      .and.returnValue(true);

    await instance.locations(req, res);

    expect(res.status).toHaveBeenCalledWith(StatusCodes.NOT_MODIFIED);
    expect(res.status().end).toHaveBeenCalled();
  });
})
