# Be sure to restart your server when you modify this file.

# Your secret key for verifying cookie session data integrity.
# If you change this key, all old sessions will become invalid!
# Make sure the secret is at least 30 characters and all random, 
# no regular words or you'll be exposed to dictionary attacks.
ActionController::Base.session = {
  :key         => '_learning_app_session',
  :secret      => 'cb8f0d2a10101e725e1a77607e4296f6b134933297e1384a32cd1cd579edd410390f522de38bcb3d4c971775e176f800b432acd60f26424d509b4c300f8289a1'
}

# Use the database for sessions instead of the cookie-based default,
# which shouldn't be used to store highly confidential information
# (create the session table with "rake db:sessions:create")
# ActionController::Base.session_store = :active_record_store
