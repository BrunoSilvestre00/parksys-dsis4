/*
Armazene neste arquivo o _schema_ do seu banco de dados (CREATE TABLE, etc.).
*/

create schema if not exists parksys_db;

use parksys_db;

create table if not exists mensalistas (
	ID int not null auto_increment,
	CPF varchar(14) not null, -- XXX.XXX.XXX-XX
    nome varchar(50) not null,
    telefone varchar(16), -- (XX) 9XXXX-XXXX
    observacao varchar(100),
    saldo double,
    constraint cpf_mensalista unique (CPF),
    constraint pk_mensalista primary key (id)
);

create table if not exists entrada_saida (
	ID int not null auto_increment,
	placa VARCHAR(8) not null,
	descricao varchar(100) not null,
    horario_entrada datetime not null,
    horario_saida datetime,
    duracao_bloco double not null,
    constraint pk_entrada_saida primary key (id)
);

create table if not exists configuracao (
	duracao_bloco double not null,
	tarifa double not null,
    desconto_mensalista double not null,
    qtd_minima_horas int not null,
    vagas_max int not null
);

create table if not exists veiculos (
	id int not null auto_increment,
    mensalistaID int not null,
    placa varchar(8), -- XXX-XXXX
    descricao varchar(100),
    constraint pk_placa unique (placa),
    constraint pk_veiculo primary key (id),
    constraint fk_mensalista foreign key (mensalistaID) references mensalistas(id)
);

create table if not exists pagamentos (
	id int not null auto_increment,
    mensalistaID int not null,
    dataPagamento datetime,
    valor double,
    blocos int,    
    constraint pk_pagamento primary key (id),
    constraint fk_mensalista_pagamento foreign key (mensalistaID) references mensalistas(id)
);
