#
#
INSERT INTO `facility_reservation`.`user` (`id`,`password`,`permission_level`,`note`) VALUES ('ginga','soft00',2,'施設情報、ユーザー情報機能は使えません。施設予約のみ可能');
INSERT INTO `facility_reservation`.`user` (`id`,`password`,`permission_level`,`note`) VALUES ('root','admin00',1,'何でもできる');
#
#
INSERT INTO `facility_reservation`.`facility_type` (`id`, `name`,`user_id`,`insert_date`,`update_date`) VALUES(1,'会議室','root',now(),now());
INSERT INTO `facility_reservation`.`facility_type` (`id`, `name`,`user_id`,`insert_date`,`update_date`) VALUES(2,'応接室','root',now(),now());
INSERT INTO `facility_reservation`.`facility_type` (`id`, `name`,`user_id`,`insert_date`,`update_date`) VALUES(3,'講堂','root',now(),now());
#
#
INSERT INTO `facility_reservation`.`facility` (`id`,`name`,`capacity`,`type_id`,`user_id`,`insert_date`,`update_date`) VALUES (1,'会議室001',20,1,'root',now(),now());
INSERT INTO `facility_reservation`.`facility` (`id`,`name`,`capacity`,`type_id`,`user_id`,`insert_date`,`update_date`) VALUES (2,'会議室002',40,1,'root',now(),now());
INSERT INTO `facility_reservation`.`facility` (`id`,`name`,`capacity`,`type_id`,`user_id`,`insert_date`,`update_date`) VALUES (3,'会議室003',20,1,'root',now(),now());
INSERT INTO `facility_reservation`.`facility` (`id`,`name`,`capacity`,`type_id`,`user_id`,`insert_date`,`update_date`) VALUES (4,'応接室001',10,2,'root',now(),now());