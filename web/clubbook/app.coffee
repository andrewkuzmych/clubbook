express = require('express');
http = require('http');
path = require('path');
crypto = require('crypto');
cronJob = require("cron").CronJob

passport = require('passport');
LocalStrategy = require('passport-local').Strategy

# redis store
RedisStore = require('connect-redis')(express)
if process.env.REDISTOGO_URL
  rtg = require('url').parse process.env.REDISTOGO_URL
  redis = require('redis').createClient rtg.port, rtg.hostname
  redis.auth rtg.auth.split(':')[1]
  # auth 1st part is username and 2nd is password separated by ":"
  try
    console.log "clean sessions"
  #redis.flushdb()
  catch err
    console.log "error:", err

app = express();

# global modules: config, db manager
global.config = require('yaml-config').readConfig('./config/config.yaml', app.settings.env)
# global.manager = require("./logic/manager")
db_model = require("./logic/model")

# controllers
controller = require('./routes/controller')
services = require('./routes/services')
cron = require('./routes/cron')
#---------------------------------------------------------------------

###passport.serializeUser (user, done) ->
  done null, user._id

passport.deserializeUser (id, done) ->
  db_model.Admin.findById(id).exec (err, user)->
    done err, user

passport.use new LocalStrategy
  usernameField: 'username',
  passwordField: 'password'
  , (username="", password, done) ->
    db_model.Admin.findOne({"login": username.toLowerCase().trim()}).exec (err, user) ->
      if err
        return done(err)
      if not user
        return done(null, false, message: "Incorrect username.")
      if user.password isnt password
        return done(null, false, message: "Incorrect password.")

      done null, user
###

#---------------------------------------------------------------------

app.use (req, res, next)->
  next()

# all environments
app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'jade');
app.use require('connect-assets')()
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.cookieParser('8af2d646a892492ee45617e499403a94'));
app.use(express.methodOverride());

# init session store
if process.env.REDISTOGO_URL
  app.use express.session
    secret: process.env.CLIENT_SECRET or "8af2d646a892492ee45617e499403a94"
    cookie: { maxAge: 604800000 }
    store: new RedisStore {client: redis}
else
  app.use express.session(secret: '8af2d646a892492ee45617e499403a94')

app.use(require('connect-flash')())
app.use(passport.initialize())
app.use(passport.session())

app.use (req, res, next)->
  next()

app.use (req, res, next)->
  #res.locals.req = req
  next()

app.use(app.router)

# development only
if ('development' == app.get('env'))
  app.locals.pretty = true;
  app.use(express.errorHandler())

app.locals.moment = require('moment-timezone')
app.locals.moment_tz = "Europe/Kiev"

local_user = (req, res, next)->
  res.locals.current_user = req.user;
  next()

handle_access_token = (req, res, next)->
  access_token = req.param("access_token")
  if not access_token
    res.json
      status: 'error'
      message: 'access_token is missing'
  else
    db_model.User.findOne({access_token: access_token}).exec (err, user)->
      console.log err, user
      if user
        console.log "user", user
        req.params.me = user
        next()
      else
        console.log "can not find user by access_token", access_token
        res.json
          status: 'error'
          message: "can not find user by access_token = " + access_token


#--------------------------------------------------------------------------------
# Web pages
#--------------------------------------------------------------------------------

# landing page
app.get '/', controller.index
app.get '/terms', controller.terms
app.get '/privacy', controller.privacy

#--------------------------------------------------------------------------------
# Mobile API
#--------------------------------------------------------------------------------

# if not registered crete new user. Return user info.
app.post '/_s/signin/fb', services.fb_signin
app.post '/_s/signup', services.signup
app.post '/_s/signinmail', services.signinmail

# retrieve clubs
app.get '/_s/obj/club', handle_access_token, services.list_club
app.get '/_s/obj/club/:objectId', handle_access_token, services.find_club

# checkin / chekout
app.get '/_s/obj/club/:objectId/checkin', handle_access_token, services.checkin
app.get '/_s/obj/club/:objectId/update', handle_access_token, services.update_checkin
app.get '/_s/obj/club/:objectId/checkout', handle_access_token, services.checkout

# chat
app.post '/_s/obj/chat', handle_access_token, services.chat
app.get '/_s/obj/chat/:current_user/:receiver', handle_access_token, services.get_conversation
app.get '/_s/obj/chat/:current_user/:receiver/read', handle_access_token, services.readchat
app.get '/_s/obj/chat/:current_user', handle_access_token, services.get_conversations

# crud users
app.get '/_s/obj/user/me', handle_access_token, services.get_user_me
app.put '/_s/obj/user/me', handle_access_token, services.update_user
app.delete '/_s/obj/user/me', handle_access_token, services.delete_user_me
app.get '/_s/obj/user/:objectId', handle_access_token, services.get_user_by_id
app.get '/_s/obj/user/me/notifications', handle_access_token, services.unread_notifications_count

# crud user images
app.post '/_s/obj/user/:userId/image', handle_access_token, services.user_image_add
app.put '/_s/obj/user/:userId/image/:objectId', handle_access_token, services.user_image_update
app.delete '/_s/obj/user/:userId/image/:objectId', handle_access_token, services.user_image_delete

# friendship
# all friends
app.get '/_s/obj/user/:objectId/friends', handle_access_token, services.friends_my
# pending frineds
app.get '/_s/obj/user/:objectId/friends/pending', handle_access_token, services.friends_pending
# send friend request
app.get '/_s/obj/user/:objectId/friends/:friendId/friend', handle_access_token, services.friends_request
# confirm friend request
app.get '/_s/obj/user/:objectId/friends/:friendId/confirm', handle_access_token, services.friends_confirm
# remove from friends
app.get '/_s/obj/user/:objectId/friends/:friendId/unfriend', handle_access_token, services.friends_unfriend
# remove friend request
app.get '/_s/obj/user/:objectId/friends/:friendId/remove', handle_access_token, services.friends_remove_request

# configuration
app.get '/_s/obj/config', services.get_config

# helper functions
app.post '/_s/create_club', services.create_club
app.get '/_f/user/remove/:user_id', services.remove_user
app.get '/_f/checkin/clean', services.checkin_clean

if config.is_test_server == "false"
  checkout_job = new cronJob(
    cronTime: "0 */5 * * * *"
    onTick: ->
      cron.cron_checkout()
    start: false
  )
  checkout_job.start()

http.createServer(app).listen app.get('port'), ()->
  console.log 'Express server listening on port ' + app.get('port')
