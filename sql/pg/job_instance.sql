CREATE TABLE jobs (
                                id serial PRIMARY KEY,
                                uuid varchar(36) UNIQUE NOT NULL,
                                command text NOT NULL,
                                spark_job_id text DEFAULT NULL,
                                spark_job_app_name text DEFAULT NULL,
                                start_job_time timestamp DEFAULT NULL,
                                end_job_time timestamp DEFAULT NULL,
                                error text DEFAULT NULL,
                                status   text DEFAULT NULL,
--                                status   ExecutionJobStatus,
--                                status   integer references job_status(id),
                                created_at timestamp NOT NULL DEFAULT current_timestamp,
                                updated_at timestamp NOT NULL DEFAULT current_timestamp,
                                executed_by_user integer  DEFAULT NULL,
                                ctx_id integer DEFAULT NULL
);

create table job_status(id serial primary key, name text);

--insert into job_status(name) values ('Queued'),('NotStarted'),('Starting'),('Recovering'),('Idle'),('Running'),('Busy'),('ShuttingDown'),('Error'),('Dead'),('Killed'),('Success'),('SavedInDbQueue');

create type status_t as enum ('Queued','NotStarted','Starting','Recovering','Idle','Running','Busy','ShuttingDown','Error','Dead','Killed','Success','SavedInDbQueue');
create type ExecutionJobStatus as enum ('Queued','NotStarted','Starting','Recovering','Idle','Running','Busy','ShuttingDown','Error','Dead','Killed','Success','SavedInDbQueue');

-- SOME BOOTSTRAP INSERTS

--INSERT INTO jobs (uuid,command,spark_job_id,spark_job_app_name,start_job_time,end_job_time,status) VALUES ('asdfsdfs','commmandd','somesparkjobid','sparkjobappname','2020-11-26 11:12:27.062914','2020-11-26 11:12:27.062914','Queued');
--INSERT INTO jobs (uuid,command,spark_job_id,spark_job_app_name,start_job_time,end_job_time,status) VALUES ('asdfsdfs222','commmandd222','somesparkjobid222','sparkjobappname222','2020-11-26 11:12:27.062914','2020-11-26 11:12:27.062914','Queued');

-- SOME USEFUL QUERIES

--select * from pg_stat_activity;
--
--https://stackoverflow.com/questions/5267715/right-query-to-get-the-current-number-of-connections-in-a-postgresql-db
--
--SELECT sum(numbackends) FROM pg_stat_database;
--
--
--https://dba.stackexchange.com/questions/161760/number-of-active-connections-and-remaining-connections
--
--
--select max_conn,used,res_for_super,max_conn-used-res_for_super res_for_normal
--from
--  (select count(*) used from pg_stat_activity) t1,
--  (select setting::int res_for_super from pg_settings where name=$$superuser_reserved_connections$$) t2,
--  (select setting::int max_conn from pg_settings where name=$$max_connections$$) t3;
--
