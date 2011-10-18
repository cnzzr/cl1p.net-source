ALTER TABLE Clip CHANGE Value Value longtext;
ALTER TABLE Clip CHANGE (Value Value longtext) TYPE=MYISAM CHARACTER SET utf8;
alter table Clip MODIFY Value LONGTEXT CHARACTER SET utf8;
alter table Clip CHANGE Value BLOB CHARACTER SET utf8;
alter table ForumComment add Handle VARCHAR(100) default ''

