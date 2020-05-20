#!/bin/bash
sed -i "s/flag_here/$FLAG/" flag.php
unset FLAG
rm -f /flag.sh