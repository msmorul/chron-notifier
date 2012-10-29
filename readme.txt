UTF8 Instructions

1. Prepare MySQL
In my.cnf

[client]
default-character-set=utf8

[mysqld]
character-set-server=utf8
collation-server=utf8_unicode_ci

2. In the persistance.xml file, your connection should specify utf-8n teh connection URL
?useUnicode=true&amp;connectionCollation=utf8_general_ci&amp;characterSetResults=utf8

3. Create database and table;

create database chron;
use chron;
grant all on chron.* to chron@localhost identified by 'password';
CREATE TABLE `Ticket` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accountId` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `identifier` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `itemId` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `requestType` int(11) NOT NULL,
  `spaceId` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `status` int(11) NOT NULL,
  `statusMessage` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `submittor` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
 

4. Optionally, use tomcat-users for authentication (BAD, VERY BAD!!!). 
The s/w uses two roles, Processor aka chronopolis and Submittor aka duracloud
to manage requests. Here's a brain dead, minimal example of what you can put
in the tomcat-users file. Ideally, you should tie this to LDAP, DB or 
anything w/ an encrypted password

<user username="producer" password="producer" roles="Submittor"/>
<user username="dura" password="producer" roles="Submittor"/>
<user username="chron" password="chron" roles="Processor"/>
<user username="admin" password="admin" roles="Submittor,Processor"/>

