#!/bin/bash

mysql -e "INSERT INTO \`1145141919810\` (id, content) VALUES ('ffffllllaaaagggg', '$FLAG');" -uroot -proot miniL

unset FLAG

rm -f /flag.sh
