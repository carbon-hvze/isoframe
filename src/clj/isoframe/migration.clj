(ns isoframe.migration
  (:require [clojure.java.jdbc :as jdbc]))

(defn migrate [db]
  (jdbc/execute! db
                 ["
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS
user_resource (
  id text PRIMARY KEY,
  txid bigint not null,
  resource_type text default 'User',
  ts timestamptz DEFAULT current_timestamp,
  resource jsonb
);

CREATE TABLE IF NOT EXISTS
session_resource (
  id text PRIMARY KEY,
  txid bigint not null,
  resource_type text default 'Session',
  ts timestamptz DEFAULT current_timestamp,
  resource jsonb
);

CREATE TABLE IF NOT EXISTS
todo_resource (
  id text PRIMARY KEY,
  txid bigint not null,
  resource_type text default 'Todo',
  ts timestamptz DEFAULT current_timestamp,
  resource jsonb
);

CREATE TABLE IF NOT EXISTS
task_resource (
  id text PRIMARY KEY,
  txid bigint not null,
  resource_type text default 'Task',
  ts timestamptz DEFAULT current_timestamp,
  resource jsonb
);

CREATE TABLE IF NOT EXISTS
transaction_resource (
  id serial primary key,
  resource_type text default 'Transaction',
  ts timestamptz DEFAULT current_timestamp,
  resource jsonb
)
"]))
