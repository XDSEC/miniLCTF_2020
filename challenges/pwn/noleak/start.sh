#!/bin/sh
# Add your startup script

echo $FLAG > /home/ctf/flag
unset FLAG

# DO NOT DELETE
/etc/init.d/xinetd start;
sleep infinity;
