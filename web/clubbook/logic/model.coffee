mongoose = require('mongoose')
__ = require("underscore")
check = require('validator').check
moment = require('moment-timezone')

#-------------------------------------------------------------------------------------
#  Admin
#-------------------------------------------------------------------------------------

AdminSchema = new mongoose.Schema
  created_on: { type: Date, 'default': Date.now }
  updated_on: { type: Date, 'default': Date.now }

  login: {type: String, trim: true, required: true, unique: true}
  password: {type: String, trim: true, required: true}
  email: {type: String}
  type: {type: String, trim: true, required: true, default: 'merchant', 'enum': ["merchant", "admin"]}

AdminSchema.virtual('is_admin').get ()->
  this.type in ["admin"]

AdminSchema.pre 'save', (next, done) ->
  this.updated_on = new Date().toISOString()
  next()

exports.Admin = mongoose.model 'Admin', AdminSchema

#-------------------------------------------------------------------------------------
#  User
#-------------------------------------------------------------------------------------
UserSchema = new mongoose.Schema
  created_on: { type: Date, 'default': Date.now }
  updated_on: { type: Date, 'default': Date.now }

  email: {type: String, trim: true, lowercase: true}
  password: {type: String}
  name: {type: String, trim: true}

  bloked: [
    { type: String }
  ]
  gender: {type: String, trim: true, 'enum': ["male", "female"]}
  photos: [
    {url: { type: String }, public_id: { type: String }, profile: { type: Boolean, default: false }}
  ]
  dob: { type: Date }
  city: {type: String, trim: true, lowercase: true}
  push: {type: Boolean, default: true}
  is_visible_nearby: {type: Boolean, default: true}

  country: {type: String, trim: true, lowercase: true}
  bio: {type: String}

  friends: [type: mongoose.Schema.ObjectId, ref: 'User']

  favorite_clubs: [type: mongoose.Schema.ObjectId, ref: 'Venue']

  bloked_users: [type: mongoose.Schema.ObjectId, ref: 'User']

  state: {type: String, default: 'active', 'enum': ["active", "inactive", "invited"]}

  invited_by: {type: mongoose.Schema.ObjectId, ref: 'User'}
  
  ios_tokens: [
    { type: String }
  ]
  android_tokens: [
    { type: String }
  ]

  fb_id: {type: String, unique: true, sparse: true}
  fb_access_token: {type: String}
  access_token: {type: String, required: true}

  checkin: [
    {club: { type: mongoose.Schema.ObjectId, ref: 'Venue' }, time: Date, active: Boolean}
  ]
  loc:
    lon: Number
    lat: Number

# http://stackoverflow.com/questions/6183147/storing-friend-relationships-in-mongodb
# friends:[_id]

UserSchema.virtual('avatar').get ()->
  photo = null
  if this.photos and this.photos.length > 0
    for _photo in this.photos
      if _photo.profile
        photo = _photo

  return photo

UserSchema.virtual('age').get ()->
  if this.dob
    return Math.floor((new Date() - this.dob) / 31536000000)
  else
    null

UserSchema.virtual('dob_format').get ()->
  if this.dob
    return moment.utc(this.dob).format("YYYY-MM-DD");
  else
    null

UserSchema.pre 'save', (next, done) ->
  this.updated_on = new Date().toISOString()
  if this.friends and this.friends.length > 0
    this.friends = __.uniq this.friends, false, (friend)-> friend.toString()
  next()

UserSchema.set('toJSON', { getters: true, virtuals: true })
UserSchema.set('toObject', { getters: true, virtuals: true })
UserSchema.index({ last_loc: "2d" })
exports.User = mongoose.model 'User', UserSchema

exports.User.schema.path('email').validate (value, respond)->
  if value
    try
      check(value).len(6, 64).isEmail()
      respond true
    catch e
      respond false
  else
    respond true
, 'Wrong email format'


exports.User.schema.path('name').validate (value, respond)->
  try
    check(value).len(1, 50)
    respond true
  catch e
    respond false
, 'Wrong first_name format'


exports.USER_PUBLIC_INFO = '_id photos name gender dob country push'


#-------------------------------------------------------------------------------------
#  News
#-------------------------------------------------------------------------------------
NewsSchema = new mongoose.Schema
  created_on: { type: Date, 'default': Date.now }
  updated_on: { type: Date, 'default': Date.now }
  venue: {type: mongoose.Schema.ObjectId, ref: 'Venue', required: true}
  image: {type: String, trim: true}
  title: {type: String, trim: true}
  share: {type: String, trim: true}
  type: {type: String, trim: true}
  buy_tickets: {type: String, trim: true}
  description: {type: String, trim: true}
  is_favorite: {type: Boolean, default: false}
  start_time: {type: Date}
  end_time: {type: Date}
  photos: [
    {type: String, trim: true}
  ]

###VenueSchema.virtual('updated_on_formated').get ()->
  this.updated_on
  return null
###
NewsSchema.pre 'save', (next, done) ->
  this.updated_on = new Date().toISOString()
  next()

NewsSchema.set('toJSON', { getters: true, virtuals: true })
exports.News = mongoose.model 'News', NewsSchema

#-------------------------------------------------------------------------------------
#  Venue
#-------------------------------------------------------------------------------------
VenueSchema = new mongoose.Schema
  created_on: { type: Date, 'default': Date.now }
  updated_on: { type: Date, 'default': Date.now }
  club_admin: [
    {type: String, trim: true}
  ]
  club_password: [
    {type: String, trim: true}
  ]
  club_name: {type: String, trim: true}
  club_email: {type: String, trim: true}
  club_houres: {type: String}
  club_photos: [
    {type: String, trim: true}
  ]
  club_phone: {type: String, trim: true}
  club_address: {type: String, trim: true, required: true}
  club_site: {type: String, trim: true}
  club_info: {type: String, trim: true, required: true}
  club_logo: {type: String, trim: true}
  club_dress_code: {type: String, trim: true}
  club_age_restriction: {type: String, trim: true}
  club_capacity: {type: String}
  club_types: [
    {type: String, trim: true}
  ]
  club_loc:
    lon: Number
    lat: Number
  active_checkins: {type: Number, default: 0, min: 0}
  active_friends_checkins: {type: Number, default: 0, min: 0}
  club_working_hours: [
    {
      status: {type: String},
      day: { type: Number },   # 0 - 6
      start_time: {type: String},
      end_time: {type: String}
    }
  ]

VenueSchema.virtual('club_today_working_hours').get ()->
  if this.club_working_hours
    for wh in this.club_working_hours
      if wh.day == moment.utc().day()
        return wh
  return null


VenueSchema.pre 'save', (next, done) ->
  this.updated_on = new Date().toISOString()
  next()

VenueSchema.set('toJSON', { getters: true, virtuals: true })
VenueSchema.index({ club_loc: "2d" })
exports.Venue = mongoose.model 'Venue', VenueSchema

#-------------------------------------------------------------------------------------
#  Chat
#-------------------------------------------------------------------------------------
ChatSchema = new mongoose.Schema
  created_on: { type: Date, 'default': Date.now }
  updated_on: { type: Date, 'default': Date.now }

  user1: {type: mongoose.Schema.ObjectId, ref: 'User', required: true}
  user2: {type: mongoose.Schema.ObjectId, ref: 'User', required: true}
  unread: {user: {type: mongoose.Schema.ObjectId, ref: 'User'}, count: {type: Number, 'default': 0 }}
  conversation: [
    {
      location:
        lon: Number
        lat: Number
      msg: { type: String },
      url: { type: String },
      time: { type: Date, 'default': Date.now, required: true},
      type: {type: String, trim: true, required: true, "default": "message", "enum": ["message", "drink", "smile", "photo", "location"]},
      from_who: {type: mongoose.Schema.ObjectId, ref: 'User', required: true}
      read: {type: Boolean, default: false}
    }
  ]

ChatSchema.pre 'save', (next, done) ->
  this.updated_on = new Date().toISOString()
  next()

ChatSchema.set('toJSON', { getters: true, virtuals: true })
exports.Chat = mongoose.model 'Chat', ChatSchema


#-------------------------------------------------------------------------------------
#  helper methonds
#-------------------------------------------------------------------------------------

exports.save_or_update_user = (user, callback)->
  if not user._id or not user.access_token
    console.log "generate access_token"
    user.access_token = exports.get_access_token(user)
  user.save callback

exports.get_access_token = (user)->
  access_token = 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'.replace /[xy]/g, (c)->
    r = Math.random()*16|0
    v = if c == 'x' then r else (r&0x3|0x8)
    return v.toString(16)

  return access_token

exports.verify_access_token = (user, access_token)->
  if user.access_token and access_token and user.access_token is access_token
    return true
  else
    return false



