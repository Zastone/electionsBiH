#!/usr/bin/env ruby

base_path = File.realpath(File.dirname(__FILE__))

cleaned_data_path = File.realpath(File.join(base_path, '..', 'Cleaned Data'))

ddl = File.read(File.join(base_path, 'create_ddl.sql'))

etl = File.readlines(File.join(base_path, 'import_csv.sql'))

etl_placeholder_replacements = [
    ['/data_in/m/', 'Municipality IDs'],
    ['/data_in/r/', 'Results']
]

etl.map! do |line|
  etl_placeholder_replacements.each do |placeholder, replacement|
    line.gsub!(placeholder, File.join(cleaned_data_path, replacement) + '/')
  end
  line
end

File.write(File.join(base_path, 'create_db.sql'), ddl + etl.join(''))

