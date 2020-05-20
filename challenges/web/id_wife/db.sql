drop database if exists miniL;

create database miniL;

use miniL;

create table user
(
id text not null ,
herf varchar(200) not null
);
CREATE USER 'web'@'%' IDENTIFIED BY 'web';
GRANT ALL PRIVILEGES ON miniL.* TO 'web'@'%';
GRANT SELECT ON mysql.* TO 'web'@'%';

create table `1145141919810`
(
id text not null ,
content text not null
);

insert into user(id,herf) value
('w1nd',"http://www.f1ag.com/wp-content/uploads/2020/04/F@G2S0VOQ1@9K37CK7EC.jpg"),
('frank',"http://www.f1ag.com/wp-content/uploads/2020/04/FFRLH1PGRF76LN_U.jpg"),
('sad',"http://www.f1ag.com/wp-content/uploads/2020/04/KBZXG0J1@B8GVU@IC0SF.jpg"),
('huai',"http://www.f1ag.com/wp-content/uploads/2020/04/YZUBG1RSBLREX9JXEH.jpg"),
('nen9ma0',"http://www.f1ag.com/wp-content/uploads/2020/04/E6TOS3T7XE9RBQRHFB.jpg"),
('endcat',"http://www.f1ag.com/wp-content/uploads/2020/04/YRGCQYYL6P5WXZP7SR-e1586589241259.jpg"),
('v0id',"http://www.f1ag.com/wp-content/uploads/2020/04/EVURBPTQ6MDKFP_3U1.png"),
('reclu3e',"http://www.f1ag.com/wp-content/uploads/2020/04/5HPZVROL8DIPKD1CIUO.jpg"),
('luoqian',"http://www.f1ag.com/wp-content/uploads/2020/04/8JJVVZI@D4HNMG02HIDX7.jpg"),
('K0rz3n',"http://www.f1ag.com/wp-content/uploads/2020/04/P@F28ZZ9QH5J3V61D.jpg"),
('happy',"http://www.f1ag.com/wp-content/uploads/2020/04/40ACEJU8KTTOVEEUG.jpg"),
('ruby',"http://www.f1ag.com/wp-content/uploads/2020/04/7RCQ@9A3J8U2HI0HFCG.jpg"),
('konge',"http://www.f1ag.com/wp-content/uploads/2020/04/71H60CYPUGA7PG2G0Y.png"),
('wallet',"http://www.f1ag.com/wp-content/uploads/2020/04/QW6E5@@ICVU095ZM57M15.png"),
('qie',"http://www.f1ag.com/wp-content/uploads/2020/04/O0G2BS8NKO4SX7FZR3E.png"),
('rx',"http://www.f1ag.com/wp-content/uploads/2020/04/XD_4IJBGNA1KAQ9RDOO.png"),
('whye',"http://www.f1ag.com/wp-content/uploads/2020/04/@9WUJ06@2D7_G27EP.png"),
('gloucester',"http://www.f1ag.com/wp-content/uploads/2020/04/AFYILFTSZ6V37EG@7.png");
