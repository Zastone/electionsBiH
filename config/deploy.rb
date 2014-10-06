# config valid only for Capistrano 3.1
lock '3.2.1'

set :application, 'electionsBiH'
set :repo_url, 'git@github.com:Zastone/electionsBiH.git'

# Default branch is :master
ask :branch, proc { `git rev-parse --abbrev-ref HEAD`.chomp }.call

# Default deploy_to directory is /var/www/my_app
set :deploy_to, "/home/mbilas/apps/#{fetch(:application)}"

# Default value for :scm is :git
# set :scm, :git

# Default value for :format is :pretty
# set :format, :pretty

# Default value for :log_level is :debug
# set :log_level, :debug

# Default value for :pty is false
# set :pty, true

set :_production_conf, 'prod.conf'
# Default value for :linked_files is []
set :linked_files, [fetch(:_production_conf), 'db-pass']


# Default value for linked_dirs is []
# set :linked_dirs, %w{bin log tmp/pids tmp/cache tmp/sockets vendor/bundle public/system}
set :linked_dirs, ['pids']

# Default value for default_env is {}
# set :default_env, { path: "/opt/ruby/bin:$PATH" }

# Default value for keep_releases is 5
# set :keep_releases, 5

namespace :sbt do

  desc 'Run SBT Assembly'
  task :assembly do
    on roles(:app) do
      within "#{release_path}/election-api" do
        # see https://github.com/capistrano/capistrano/issues/719
        execute 'sbt', "-Dconfig.file=../#{fetch(:_production_conf)}", 'assembly'
      end
    end
  end

end

namespace :bundler do

  desc 'Bundler: install required gems'
  task :install do
    on roles(:all) do
      within release_path do
        execute 'bundle', 'install'
      end
    end
  end

end

namespace :db do

  desc 'Initialize the DB with fresh schema and data. Overwrites old data.'
  task :init do
    on roles(:db) do
      within release_path do
        execute 'ruby', 'Database/build_init_script.rb'
        execute 'mysql', '-u', 'root', '-p`cat db-pass`', '<', 'Database/create_db.sql'
      end
    end
  end

  before :init, 'deploy:updating', 'deploy:updated', 'bundler:install'

end

namespace :deploy do

  desc 'Restart application'
  task :restart do
    on roles(:app), in: :sequence, wait: 5 do
      # Your restart mechanism here, for example:
      # execute :touch, release_path.join('tmp/restart.txt')
    end
  end

  after :publishing, :restart

  before :publishing, 'sbt:assembly'

  after :restart, :clear_cache do
    on roles(:web), in: :groups, limit: 3, wait: 10 do
      within release_path do
        execute 'bin/stop-app.sh'
        sleep 2
        execute 'bin/start-app.sh'
      end
    end
  end

end
