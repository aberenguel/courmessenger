
create sequence if not exists  hibernate_sequence start with 1 increment by 1;

create table if not exists user_account (
    id                bigint       not null,
    creation_date     timestamp    not null,
    email             varchar(255) not null,
    encoded_password varchar(255)  not null,
    name             varchar(255)  not null,
    primary key (id),
    constraint email_uk unique (email)
);


create table if not exists message (
    id                bigint       not null,
    creation_date     timestamp    not null,
    iv                binary(50)   not null,
    encrypted_subject binary(250)  not null,
    encrypted_body    binary(5050) not null,
    receiver_id       bigint       not null,
    sender_id         bigint       not null,
    primary key (id),
    constraint receiver_fk foreign key (receiver_id) references user_account,
    constraint sender_fk foreign key (sender_id) references user_account
);
