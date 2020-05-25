FROM ctftraining/base_pwn_xinetd_kafel_1804

COPY src /tmp/

RUN sed -i 's/archive.ubuntu.com/mirrors.tuna.tsinghua.edu.cn/' /etc/apt/sources.list && \
    sed -i 's/# deb-src/deb-src/' /etc/apt/sources.list && \
    sed -i '/security/d' /etc/apt/sources.list && \
    apt-get update -y && \
    apt-get upgrade -y; \
    # netbase tcpdump xinetd
    apt-get install -y --no-install-recommends netbase tcpdump xinetd; \
    # lib
    apt-get install -y lib32ncurses5 lib32z1 lib32stdc++6; \
    cp /tmp/pwn /home/ctf/pwn && \
    cp /tmp/pwn.xinetd.conf /etc/xinetd.d/pwn && \
    chown root:ctf /home/ctf/pwn && \
    chmod 750 /home/ctf/pwn && \
    apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
