create table if not exists data_source
(
    id          bigint auto_increment
    primary key,
    source_name varchar(256) not null,
    source_type varchar(256) not null,
    source_host varchar(256) not null,
    source_port int          not null,
    source_user varchar(256) not null,
    source_pwd  varchar(256) not null,
    constraint source_name
    unique (source_name)
    );
create table if not exists backup_job (
                                          id              bigint auto_increment
                                          primary key,
                                          job_name        varchar(256)                                                        not null,
    job_describe    varchar(4000),
    job_type        int          default 0 comment '0: 一次性任务，1：周期性任务'         not null,
    job_state       varchar(256) default 'NORMAL' comment 'NORMAL RUNNING SUCCESS FAIL' not null,
    job_enable varchar(256) default 'ON' not null comment 'JOB是否开启  ON OFF',
    data_source_id  bigint                                                              not null,
    cron_expression varchar(20),
    execute_time    datetime,
    last_time       datetime,
    next_time       datetime,
    create_time     datetime     default current_timestamp,
    job_policy      longtext
    );
create table if not exists backup_job_record
(
    id          bigint auto_increment
    primary key,
    job_id      bigint                                               not null,
    job_result  varchar(256) comment 'SUCCESS FAIL' not null,
    job_message varchar(4000),
    start_time  datetime,
    end_time    datetime
    );
create table if not exists backup_job_record_atom
(
    id         bigint auto_increment
    primary key,
    record_id  bigint                                                       not null,
    data_base varchar(256) null comment '数据库',
    result     varchar(256)  comment 'SUCCESS FAIL'        not null,
    log        longtext,
    file_path  varchar(4000) default 0 comment '0: 一次性任务，1：周期性任务' not null,
    file_size  bigint,
    tables     longtext,
    start_time datetime,
    end_time   datetime
    );
create table if not exists restore_job
(
    id             bigint auto_increment
    primary key,
    data_source_id bigint                                                        not null,
    job_state      varchar(256) default 'RUNNING' comment 'RUNNING SUCCESS FAIL' not null,
    log                       longtext,
    start_time    datetime     default current_timestamp,
    end_time    datetime
    );
create table if not exists restore_job_record
(
    id                        bigint auto_increment
    primary key,
    restore_job_id            bigint not null,
    backup_job_record_atom_id bigint not null,
    data_base                 varchar(256),
    table_name                varchar(256),
    log                       longtext,
    job_state                 varchar(256) default 'RUNNING' comment 'RUNNING SUCCESS FAIL' not null,
    start_time                datetime,
    end_time                  datetime
    );