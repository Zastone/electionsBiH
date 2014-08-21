#!/usr/bin/env ruby

base_path = File.realpath(File.dirname(__FILE__))

cleaned_data_path = File.realpath(File.join(base_path, '..', 'Cleaned Data'))

ddl = File.read('create_ddl.sql')

etl = File.readlines('import_csv.sql')

etl.map!{ |line| line.gsub('/data_in/m/', File.join(cleaned_data_path, 'Municipality IDs') + '/') }

File.write(File.join(base_path, 'create_db.sql'), ddl + etl.join(''))

