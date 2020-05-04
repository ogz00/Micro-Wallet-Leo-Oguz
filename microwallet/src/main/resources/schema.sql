create table if not exists db_wallet.app_role
(
    id bigserial
        constraint app_role_pkey
            primary key,
    created_at timestamp,
    updated_at timestamp,
    updated_by varchar(255),
    version bigint,
    description varchar(255) not null,
    role_name varchar(255) not null
);




create table if not exists db_wallet.player
(
    id bigserial not null
        constraint player_pkey
            primary key,
    created_at timestamp,
    updated_at timestamp,
    updated_by varchar(255),
    version bigint,
    country varchar(255) not null,
    full_name varchar(255) not null,
    password varchar(255) not null,
    username varchar(255) not null
        constraint uk_o39xn8lmj05iew7d2tgw836jy
            unique
);



create table if not exists db_wallet.player_role
(
    player_id bigserial not null
        constraint player_role_player_id_fk
            references db_wallet.player
            on update cascade,
    role_id bigserial not null
        constraint player_role_app_role_id_fk
            references db_wallet.app_role
            on update cascade
);

create table if not exists db_wallet.currency
(
    id bigserial not null
        constraint currency_pkey
            primary key,
    created_at timestamp,
    updated_at timestamp,
    updated_by varchar(255),
    version bigint,
    name varchar(255) not null
        unique,
    code varchar(255) not null
            unique
);

create table if not exists db_wallet.wallet
(
    id bigserial not null
        constraint wallet_pkey
            primary key,
    created_at timestamp,
    updated_at timestamp,
    updated_by varchar(255),
    version bigint,
    player_id bigserial not null
        constraint wallet_player_id_fk
        references db_wallet.player
            on update cascade,
    currency_id bigserial not null
        constraint wallet_currency_id_fk
            references db_wallet.currency
            on update cascade
);


create table if not exists db_wallet.transaction
(
    id bigserial not null
        constraint transaction_pkey
            primary key,
    created_at timestamp,
    updated_at timestamp,
    updated_by varchar(255),
    version bigint,
    supplied_id bigint,
    amount decimal,
    opening_balance decimal,
    wallet_id bigserial not null
        constraint transaction_wallet_id_fk
            references db_wallet.wallet
            on update cascade
);
