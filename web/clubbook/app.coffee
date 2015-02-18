express = require('express');
http = require('http');
path = require('path');
crypto = require('crypto');
cronJob = require("cron").CronJob
cloudinary = require('cloudinary')
fs = require('fs')

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

# config cloudinary
cloudinary.config({ cloud_name: 'ddsoyfjll', api_key: '635173958253643', api_secret: 'kc8bGmuk3HgDJCqQxMjuTbgTwJ0' });
app.locals.api_key = cloudinary.config().api_key;
app.locals.cloud_name = cloudinary.config().cloud_name;


# global.manager = require("./logic/manager")
db_model = require("./logic/model")

# controllers
controller = require('./routes/controller')
services = require('./routes/services')
cron = require('./routes/cron')
#---------------------------------------------------------------------

passport.serializeUser (user, done) ->
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

#---------------------------------------------------------------------

app.use (req, res, next)->
  next()

# all environments
app.set('port', process.env.PORT || 4000);
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

#--------------------------------------------------------------------------------
# utils
#--------------------------------------------------------------------------------
validate_venue = (req, res, next)->
  db_model.Venue.findById(req.params.venue_id).exec (err, venue)->
    if venue
      console.log "Venue page:", req.params.venue_id
      req.params.venue = venue
      next()
    else
      console.log "ERROR: wrong venue_id"
      res.redirect "/"

require_role = (role)->
  (req, res, next)->
    if req.isAuthenticated()
      switch role
        when "user" then next()
        when "admin"
          if req.user.is_admin
            next()
          else
            res.redirect("/login")
        else next()
    else
      res.redirect("/login")
      #res.send(403)

local_user = (req, res, next)->
  res.locals.current_user = req.user
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

app.get '/', controller.index
app.get '/login', controller.login
app.post '/login', (req, res, next)->
  passport.authenticate("local", (err, user, info) ->
    if err
      console.log err
      return next(err)
    if not user
      res.render "login", {title: "Clubbook", error: info}
    else
      req.logIn user, (err) ->
        if err then return next(err)
        res.redirect "/home"

  ) req, res, next

app.get '/logout', (req, res)->
  req.logout()
  res.redirect('/')

app.get '/terms', controller.terms
app.get '/faq', controller.terms

app.get '/qr', controller.download
app.get '/privacy', controller.privacy
app.get '/reset_pass', controller.reset_pass
app.post '/reset_pass', controller.reset_pass_action
app.post '/cloudinary_upload', controller.cloudinary_upload

app.get '/home', require_role("user"), local_user, controller.home
app.get '/venue/users', require_role("user"), local_user, controller.users

#clubs
app.get '/venue/clubs', require_role("user"), local_user, controller.clubs
app.get '/venue/club_create', require_role("user"), local_user, controller.club_create
app.post '/venue/club_create', require_role("user"), local_user, controller.club_create_action
app.get '/venue/club_edit/:id', require_role("user"), local_user, controller.club_edit
app.post '/venue/club_edit/:id', require_role("user"), local_user, controller.club_edit_action
app.get '/venue/club_delete/:id', require_role("user"), local_user, controller.club_delete_action

# festivals
app.get '/venue/festivals', require_role("user"), local_user, controller.festivals
app.get '/venue/festival_create', require_role("user"), local_user, controller.festival_create
app.post '/venue/festival_create', require_role("user"), local_user, controller.festival_create_action
app.get '/venue/festival_edit/:id', require_role("user"), local_user, controller.festival_edit
app.post '/venue/festival_edit/:id', require_role("user"), local_user, controller.festival_edit_action
app.get '/venue/festival_delete/:id', require_role("user"), local_user, controller.festival_delete_action

#dj
app.get '/venue/djs', require_role("user"), local_user, controller.djs
app.get '/venue/dj_create', require_role("user"), local_user, controller.dj_create
app.post '/venue/dj_create', require_role("user"), local_user, controller.dj_create_action
app.get '/venue/dj_edit/:id', require_role("user"), local_user, controller.dj_edit
app.post '/venue/dj_edit/:id', require_role("user"), local_user, controller.dj_edit_action
app.get '/venue/dj_delete/:id', require_role("user"), local_user, controller.dj_delete_action

#news
app.get '/venue/:type/news/:id', require_role("user"), local_user, controller.news
app.get '/venue/:type/news_create/:id', require_role("user"), local_user, controller.news_create
app.post '/venue/:type/news_create/:id', require_role("user"), local_user, controller.news_create_action
app.get '/venue/:type/news_edit/:id/:news_id', require_role("user"), local_user, controller.news_edit
app.post '/venue/:type/news_edit/:id/:news_id', require_role("user"), local_user, controller.news_edit_action
app.get '/venue/:type/news_delete/:id/:news_id', require_role("user"), local_user, controller.news_delete_action

#events
app.get '/venue/:type/events/:id', require_role("user"), local_user, controller.events
app.get '/venue/:type/events_create/:id', require_role("user"), local_user, controller.events_create
app.post '/venue/:type/events_create/:id', require_role("user"), local_user, controller.events_create_action
app.get '/venue/:type/events_edit/:id/:events_id', require_role("user"), local_user, controller.events_edit
app.post '/venue/:type/events_edit/:id/:events_id', require_role("user"), local_user, controller.events_edit_action
app.get '/venue/:type/events_delete/:id/:events_id', require_role("user"), local_user, controller.events_delete_action

#--------------------------------------------------------------------------------
# Mobile API
#--------------------------------------------------------------------------------

app.post '/home/user/:user_id/replay', services.user_push

# if not registered crete new user. Return user info.
app.post '/_s/signin/fb', services.fb_signin
app.post '/_s/signup', services.signup
app.post '/_s/signinmail', services.signinmail

# retrieve clubs
app.get '/_s/obj/club', handle_access_token, services.list_club #
app.get '/_s/obj/club_types', handle_access_token, services.club_types

#infinite scroll for clubs, festivals, bars, dj events, events
app.get '/_s/obj/events/list', handle_access_token, services.list_events
app.get '/_s/obj/dj_events/list', handle_access_token, services.list_dj_events
app.get '/_s/obj/dj/list', handle_access_token, services.list_dj
app.get '/_s/obj/:type/list', handle_access_token, services.list_venue
app.get '/_s/obj/list', handle_access_token, services.get_all_lists

#events
app.get '/_s/obj/:type/:objectId/events/list', handle_access_token, services.venue_events

#news
app.get '/_s/obj/:type/:objectId/news/list', handle_access_token, services.venue_news

app.get '/_s/obj/club/:objectId', handle_access_token, services.find_club
app.get '/_s/obj/club/:objectId/users', handle_access_token, services.club_users
app.get '/_s/obj/club/:objectId/users/yesterday', handle_access_token, services.club_users_yesterday
app.get '/_s/obj/clubs/yesterday', handle_access_token, services.clubs_yesterday

# favorite_clubs
app.get '/_s/obj/club/:objectId/favorite/remove', handle_access_token, services.remove_favorite_club
app.get '/_s/obj/club/:objectId/favorite/add', handle_access_token, services.add_favorite_club

#news
app.get '/_s/obj/club/:objectId/news', handle_access_token, services.news #
app.get '/_s/obj/club/:objectId/events', handle_access_token, services.events #

# users news
app.get '/_s/obj/user/favorite/news', handle_access_token, services.news_favorite
app.get '/_s/obj/user/favorite/events', handle_access_token, services.events_favorite

# checkin / chekout
app.get '/_s/obj/club/:objectId/checkin', handle_access_token, services.checkin
app.get '/_s/obj/club/:objectId/update', handle_access_token, services.update_checkin
app.get '/_s/obj/club/:objectId/checkout', handle_access_token, services.checkout

# chat
app.post '/_s/obj/chat', handle_access_token, services.chat
app.get '/_s/obj/chat/:current_user/:receiver', handle_access_token, services.get_conversation
app.get '/_s/obj/chat/:current_user/:receiver/delete', handle_access_token, services.delete_conversation
app.get '/_s/obj/chat/:current_user/:receiver/read', handle_access_token, services.readchat
app.get '/_s/obj/chat/:current_user', handle_access_token, services.get_conversations


app.get '/_s/obj/users/checkedin', handle_access_token, services.users_checkedin
app.get '/_s/obj/users/around', handle_access_token, services.users_around

# crud users
app.get '/_s/obj/user/location/update', handle_access_token, services.update_user_location
app.get '/_s/obj/user/me', handle_access_token, services.get_user_me
app.put '/_s/obj/user/me', handle_access_token, services.update_user
app.delete '/_s/obj/user/me', handle_access_token, services.delete_user_me
app.get '/_s/obj/user/:objectId', handle_access_token, services.get_user_by_id
app.get '/_s/obj/user/me/notifications', handle_access_token, services.unread_notifications_count
app.put '/_s/obj/user/update_pass', handle_access_token, services.update_pass

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
# cencel friend request
app.get '/_s/obj/user/:objectId/friends/:friendId/cancel', handle_access_token, services.friends_cencel_request
# block user 
app.get '/_s/obj/user/:objectId/block/:userId', handle_access_token, services.block_user
# unblock
app.get '/_s/obj/user/:objectId/unblock/:userId', handle_access_token, services.unblock_user
#invite friend
app.post '/_s/obj/user/:objectId/invite', handle_access_token, services.invite_friend
#invite fb friend
app.post '/_s/obj/user/:objectId/fb/invite', handle_access_token, services.invite_friend_fb
#find friends
app.post '/_s/obj/user/find', handle_access_token, services.find_friends
#find fb friends
app.post '/_s/obj/user/fb/find', handle_access_token, services.find_friends_fb

#
# configuration
app.get '/_s/obj/config', services.get_config

# helper functions
app.post '/_s/create_club', services.create_club
app.get '/_f/user/remove/:user_id', services.remove_user
app.get '/_f/checkin/clean', services.checkin_clean

app.get '/_a/user/list', services.user_list
app.get '/_a/dj/list', services.dj_list
app.get '/_a/:type/list', services.venue_list

if config.is_test_server == "false"
  checkout_job = new cronJob(
    cronTime: "0 0 */1 * * *"
               
    onTick: ->
      cron.cron_checkout()
    start: false
  )
  checkout_job.start()

http.createServer(app).listen app.get('port'), ()->
  console.log 'Express server listening on port ' + app.get('port')
