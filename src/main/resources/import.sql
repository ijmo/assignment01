CREATE TABLE organization_sequence (next_val BIGINT(19));
INSERT INTO organization_sequence (next_val) VALUES (1);

INSERT INTO user (id, username, password, authorities, created_on) VALUES (1, 'admin', '{noop}admin', 'ROLE_USER,ROLE_ADMIN', CURRENT_TIMESTAMP());
INSERT INTO user (id, username, password, authorities, created_on) VALUES (2, 'user', '{noop}1234', 'ROLE_USER', CURRENT_TIMESTAMP());
