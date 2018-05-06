create sequence if not exists hibernate_sequence start 1 increment 1;

create table if not exists user_account (
    id                int8         not null,
    creation_date     timestamp    not null,
    email             varchar(255) not null,
    encoded_password  varchar(255) not null,
    name              varchar(255) not null,
    primary key (id),
    constraint email_uk unique (email)
);

create table if not exists message (
    id                int8        not null,
    creation_date     timestamp   not null,
    encrypted_body    bytea       not null,
    encrypted_subject bytea       not null,
    iv                bytea       not null,
    receiver_id       int8        not null,
    sender_id         int8        not null,
    primary key (id),
    constraint receiver_fk foreign key (receiver_id) references user_account,
    constraint sender_fk   foreign key (sender_id)   references user_account
);
