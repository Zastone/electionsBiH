# Load DSL and Setup Up Stages
require 'capistrano/setup'

# Includes default deployment tasks
require 'capistrano/deploy'

# Includes tasks from other gems included in your Gemfile
#
# For documentation on these, see for example:
#
#   https://github.com/capistrano/rvm
#   https://github.com/capistrano/rbenv
#   https://github.com/capistrano/chruby
#   https://github.com/capistrano/bundler
#   https://github.com/capistrano/rails
#
# require 'capistrano/rvm'
# require 'capistrano/rbenv'
# require 'capistrano/chruby'
# require 'capistrano/bundler'
# require 'capistrano/rails/assets'
# require 'capistrano/rails/migrations'

# Loads custom tasks from `lib/capistrano/tasks' if you have any defined.
Dir.glob('lib/capistrano/tasks/*.rake').each { |r| import r }

# Capistrano-SBT module is another option to support SBT:
# https://github.com/yyuu/capistrano-sbt
# https://github.com/yyuu/capistrano-sbt/blob/develop/lib/capistrano-sbt.rb

# https://gist.github.com/Jesus/448d618c83fb0445ebbf

# http://stackoverflow.com/questions/21006875/set-default-stage-with-capistrano-3
invoke :production
