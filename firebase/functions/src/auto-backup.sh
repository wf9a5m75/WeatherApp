#!/bin/bash

date
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd $DIR

#-----------------------
# mysql backup
#-----------------------
DATE=$(date +%F)
#docker exec -it blog-amefun-mysql sh -c 'mysqldump --all-databases --lock-all-tables -uroot -p""' > "backup/${DATE}.sql"
echo "use wp_blog_americanfunding_jp;" > "backup/${DATE}.sql"
docker exec -it blog-amefun-mysql sh -c 'mysqldump wp_blog_americanfunding_jp -u"blog-amefun" -p"d32eo32nrn4998"' >> "backup/${DATE}.sql"
sed -i -e '2d' "backup/${DATE}.sql"

#-----------------------------------------------------------------
# Delete old logs that the modified time was over 5 days ago
#-----------------------------------------------------------------
find ./backup/ -mtime +5 > oldfiles.list
while read file; do
  echo "[delete] ${file}"
  rm -rf $file
done < oldfiles.list

rm -rf oldfiles.list 2>/dev/null >&1

#-----------------------------------------------------------------
# save to git repository
#-----------------------------------------------------------------
git add .
git commit -m "auto backup at ${DATE}"
git push
