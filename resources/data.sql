
INSERT INTO mensalistas (CPF, nome, telefone, observacao, saldo) VALUES ('111.111.111-11', 'Mensalista 1', '(11) 11111-1111', 'Observação 1', 100);
INSERT INTO mensalistas (CPF, nome, telefone, observacao, saldo) VALUES ('222.222.222-22', 'Mensalista 2', '(22) 22222-2222', 'Observação 2', 200);
INSERT INTO mensalistas (CPF, nome, telefone, observacao, saldo) VALUES ('333.333.333-33', 'Mensalista 3', '(33) 33333-3333', 'Observação 3', 0);
INSERT INTO mensalistas (CPF, nome, telefone, observacao, saldo) VALUES ('444.444.444-44', 'Mensalista 4', '(44) 44444-4444', '-', 400);
INSERT INTO mensalistas (CPF, nome, telefone, observacao, saldo) VALUES ('555.555.555-55', 'Mensalista 5', '(55) 55555-5555', '-', 0);

INSERT INTO veiculos (mensalistaID, placa, descricao) VALUES (1, 'AAA-1111', 'Carro 1 - 2001');
INSERT INTO veiculos (mensalistaID, placa, descricao) VALUES (2, 'BBB-2222', 'Carro 2 - 2002');
INSERT INTO veiculos (mensalistaID, placa, descricao) VALUES (3, 'CCC-3333', 'Carro 3 - 2003');
INSERT INTO veiculos (mensalistaID, placa, descricao) VALUES (4, 'DDD-4444', '');
INSERT INTO veiculos (mensalistaID, placa, descricao) VALUES (5, 'EEE-5555', '-');

INSERT INTO pagamentos (mensalistaID, dataPagamento, valor, blocos) VALUES (1, now(), 100, 10);
INSERT INTO pagamentos (mensalistaID, dataPagamento, valor, blocos) VALUES (2, now(), 200, 20);
INSERT INTO pagamentos (mensalistaID, dataPagamento, valor, blocos) VALUES (4, now(), 400, 40);
