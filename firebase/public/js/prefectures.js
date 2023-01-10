export const PREFECTURE = {
  '北海道': 'hokkaido',
  '青森県': 'aomori',
  '岩手県': 'iwate',
  '秋田県': 'akita',
  '宮城県': 'miyagi',
  '山形県': 'yamagata',
  '福島県': 'fukushima',
  '茨城県': 'ibaragi',
  '栃木県': 'tochigi',
  '群馬県': 'gunma',
  '埼玉県': 'saitama',
  '千葉県': 'chiba',
  '東京都': 'tokyo',
  '神奈川県': 'kanagawa',
  '山梨県': 'yamanashi',
  '長野県': 'nagano',
  '新潟県': 'niigata',
  '富山県': 'toyama',
  '石川県': 'ishikawa',
  '福井県': 'fukui',
  '静岡県': 'shizuoka',
  '愛知県': 'aichi',
  '岐阜県': 'gifu',
  '三重県': 'mie',
  '滋賀県': 'shiga',
  '京都府': 'kyoto',
  '大阪府': 'osaka',
  '兵庫県': 'hyogo',
  '奈良県': 'nara',
  '和歌山県': 'wakayama',
  '岡山県': 'okayama',
  '広島県': 'hiroshima',
  '鳥取県': 'tottori',
  '島根県': 'shimane',
  '山口県': 'yamaguchi',
  '徳島県': 'tokushima',
  '香川県': 'kagawa',
  '愛媛県': 'echime',
  '高知県': 'kouchi',
  '福岡県': 'fukuoka',
  '佐賀県': 'saga',
  '長崎県': 'nagasaki',
  '大分県': 'ooita',
  '熊本県': 'kumamoto',
  '宮崎県': 'miyazaki',
  '鹿児島県': 'kagoshima',
  '沖縄県': 'okinawa',
};

export const getPrefecture = (address) => {
  for (let pref in PREFECTURE) {
    if (address.includes(pref)) {
      return PREFECTURE[pref];
    }
  }
  throw `Can not detect prefecture from: ${address}`;
};
