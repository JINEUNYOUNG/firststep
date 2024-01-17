use first;

create table user(
	user_idx int not null auto_increment primary key,
	id varchar(50) not null,
	password varchar(32) not null,
	user_name varchar(32) not null,
	resident_registration_number char(13) not null,
	phonenumber varchar(32) not null,
	address varchar(32) not null,
	admin_level int default '0',
	user_reg_date varchar(14) default date_format(now(), '%Y-%m-%d %H:%i:%s') not null
)
	
create table board (
	board_idx int not null auto_increment primary key,
	title varchar(100),
	content varchar(1000),
	user_idx int not null,
	board_type int not null,
	board_reg_date varchar(14) default date_format(now(), '%Y-%m-%d %H:%i:%s') not null,
	board_view int default 0,
	notice boolean default false,
	foreign key (user_idx) references user(user_idx) on delete cascade,
	foreign key (board_type_idx) references board_type(board_type_idx)
)

create table file(
	file_idx int not null auto_increment primary key,
	board_idx int not null,
	file_name varchar(100),
	original_name varchar(200),
	file_size int default '0',
	file_extension varchar(10) not null,
	file_reg_date varchar(14) default date_format(now(), '%Y-%m-%d %H:%i:%s') not null,
	foreign key (board_idx) references board(board_idx) on delete cascade
)

create table comment (
	comment_idx int not null auto_increment primary key,
	user_idx int not null,
	board_idx int not null,
	comment_content varchar(100),
	comment_reg_date varchar(14) default date_format(now(), '%Y-%m-%d %H:%i:%s'),
	foreign key (user_idx) references user(user_idx) on delete cascade,
	foreign key (board_idx) references board(board_idx) on delete cascade
)

create table likes (
	user_idx int not null,
	board_idx int not null,
	primary key (user_idx, board_idx),
	foreign key (user_idx) references user(user_idx) on delete cascade,
	foreign key (board_idx) references board(board_idx) on delete cascade
)




-- 복호화 키
    SET @encryption_key = 'giens';

-- 관리자 계정 설정
INSERT INTO user (id, password, user_name, resident_registration_number, phonenumber, address, admin_level)
VALUES ('admin', AES_ENCRYPT('1234', @encryption_key),AES_ENCRYPT('진은영', @encryption_key),AES_ENCRYPT('930208-2682712', @encryption_key),
        AES_ENCRYPT('01042216201',@encryption_key),
        AES_ENCRYPT('양천구 오목로 27길 12', @encryption_key),
        1
       );

-- 복호화 테스트
SELECT AES_DECRYPT(password, @encryption_key) AS decrypted_password
FROM user
WHERE user_idx = 1;
