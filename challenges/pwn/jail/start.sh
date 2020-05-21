#!/bin/sh
# Add your startup script
echo $FLAG > /flag
chown root:root /flag
chmod 444 /flag
export FLAG=
# DO NOT DELETE
/etc/init.d/xinetd start;
sleep infinity;
