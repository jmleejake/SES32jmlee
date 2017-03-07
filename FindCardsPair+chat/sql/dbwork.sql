/*대기실 화면에서 표기해줄 대기자 및 대기여부 리스트*/
drop table game_server;

create table game_server (
	user_name varchar2(20) primary key,	
	type varchar2(5),
	wait char(1) default 'T'
);

/*대기실 화면 상세표시 내역*/
drop table game_server_detail;

create table game_server_detail (
	user_name varchar2(20),
	ip_add varchar2(20),
	port_no number(4),
	row_no number(2),
	col_no number(2),
	constraint fk_svrdt_user_name foreign key (user_name)
	references game_server(user_name) on delete cascade
);

/*점수판*/
drop table leaderboard;

create table leaderboard (
	name varchar(20) not null, 
	type varchar(20) not null, 
	score number(20) not null,
	today date default sysdate
);


select rownum, gs.user_name,  type, ip_add, port_no, row_no, col_no,
case wait when 'T' then '대기중'
              when 'F' then '게임중'
              end wait_status
from GAME_SERVER gs
inner join game_server_detail gsd on gs.user_name = gsd.user_name

/*COMMIT ROLLBACK*/
rollback
commit

delete from game_server;
delete from game_server_detail;

insert into game_server (user_name, type)
values ('a', '3X4');
insert into game_server_detail (user_name, ip_add, port_no, row_no, col_no)
values ('a', '0.0.0.0', 1234, 3, 4);

insert into game_server (user_name, type, wait)
values ('b', '4X5', 'F');
insert into game_server_detail (user_name, ip_add, port_no, row_no, col_no)
values ('b', '0.0.0.0', 1234, 4, 5);


insert into game_server (user_name, type)
values ('c', '5X6');
insert into game_server_detail (user_name, ip_add, port_no, row_no, col_no)
values ('c', '0.0.0.0', 1234, 5, 6);

insert into game_server (user_name, type)
values ('d', '6X5');
insert into game_server_detail (user_name, ip_add, port_no, row_no, col_no)
values ('d', '0.0.0.0', 1234, 6, 5);


insert into game_server (user_name, type, wait)
values ('e', '4X3', 'F');
insert into game_server_detail (user_name, ip_add, port_no, row_no, col_no)
values ('e', '0.0.0.0', 1234, 4, 3);
