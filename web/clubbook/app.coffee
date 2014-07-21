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

#--------------------------------------------------------------------------------
# Web pages
#--------------------------------------------------------------------------------

# landing page
app.get '/', controller.index

#--------------------------------------------------------------------------------
# Mobile API
#--------------------------------------------------------------------------------

# if not registered crete new user. Return user info.
app.post '/_s/signin/fb', services.fb_signin
app.post '/_s/signup', services.signup
app.post '/_s/signinmail', services.signinmail
app.post '/_s/create_club', services.create_club
app.get '/_s/user/by_id/:user_id', services.get_user_by_id
app.get '/_s/list_club/:distance/:user_lat/:user_lon', services.list_club
app.get '/_s/find_club/:club_id/:user_id', services.find_club
app.get '/_s/cu_count/:distance/:user_lat/:user_lon', services.cu_count
app.get '/_s/checkin/:club_id/:user_id', services.checkin
app.get '/_s/checkin/update/:club_id/:user_id', services.update_checkin
app.get '/_s/checkout/:club_id/:user_id', services.checkout
app.get '/_s/club_clubbers/:club_id', services.club_clubbers
app.post '/_s/chat', services.chat
app.get '/_s/readchat/:current_user/:receiver', services.readchat
app.get '/_s/conversation/:current_user/:receiver', services.get_conversation
app.get '/_s/conversations/:user_id', services.get_conversations
app.get '/_s/cron_checkout', services.cron_checkout
app.get '/_s/unread/messages/count/:user_id', services.unread_messages_count
app.post '/_s/updateusername/:user_id', services.update_name
app.post '/_s/updatedob/:user_id', services.update_dob
app.post '/_s/updategender/:user_id', services.update_gender
app.post '/_s/updateinfo/:user_id', services.update_info
app.get '/_s/user/:user_id/friends', services.get_user_friends
app.put '/_s/obj/user/:objectId', services.update_user

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
