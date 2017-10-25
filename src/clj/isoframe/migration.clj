(ns isoframe.migration
  (:require [clojure.java.jdbc :as jdbc]))

(defn migrate [db]
  (jdbc/execute! db
                 ["
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS
user_resource (
  id text PRIMARY KEY,
  resource_type text default 'User',
  ts timestamptz DEFAULT current_timestamp,
  resource jsonb
);

CREATE TABLE IF NOT EXISTS
session_resource (
  id text PRIMARY KEY,
  resource_type text default 'Session',
  ts timestamptz DEFAULT current_timestamp,
  resource jsonb
);

CREATE TABLE IF NOT EXISTS
todo_resource (
  id text PRIMARY KEY,
  resource_type text default 'Session',
  ts timestamptz DEFAULT current_timestamp,
  resource jsonb
);

"]))
