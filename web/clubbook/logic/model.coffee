mongoose = require('mongoose')
__ = require("underscore")
check = require('validator').check

#-------------------------------------------------------------------------------------
#  User
#-------------------------------------------------------------------------------------
UserSchema = new mongoose.Schema
  created_on: { type: Date, 'default': Date.now }
  updated_on: { type: Date, 'default': Date.now }

  email: {type: String, trim: true, lowercase: true}
  password: {type: String}
  name: {type: String, trim: true, required: true}
  
  bloked: [{ type: String }]
  gender: {type: String, trim: true, required: true, 'enum':["male", "female"]}
  photos: [{url:{ type: String }, public_id:{ type: String }, profile:{ type: Boolean, default:false }}]
  dob: { type: Date }
  city: {type: String, trim: true, lowercase: true}
  info: {type: String}
  
  ios_tokens: [{ type: String }]
  android_tokens: [{ type: String }]
          
  fb_id: {type:String, unique: true, sparse: true}
  fb_access_token: {type:String}
  
  checkin: [{club: { type: mongoose.Schema.ObjectId, ref: 'Venue' }, time: Date, active: Boolean}]

UserSchema.virtual('avatar').get ()->
  photo = null
  if this.photos and this.photos.length > 0
    for _photo in this.photos
      if _photo.profile
        photo = _photo

  return photo


UserSchema.pre 'save', (next, done) ->
  this.updated_on = new Date().toISOString()
  next()

UserSchema.set('toJSON', { getters: true, virtuals: true })
UserSchema.set('toObject', { getters: true, virtuals: true })
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

#-------------------------------------------------------------------------------------
#  Venue
#-------------------------------------------------------------------------------------
VenueSchema = new mongoose.Schema
  created_on: { type: Date, 'default': Date.now }
  updated_on: { type: Date, 'default': Date.now }
  club_admin: [{type: String, trim: true}]
  club_password: [{type: String, trim: true}]
  club_name: {type: String, trim: true}
  club_email: {type: String, trim: true}
  club_houres: {type: String}
  club_photos: [{type: String, trim: true}]
  club_phone: {type: String, trim: true}
  club_address: {type: String, trim: true, required: true}
  club_site: {type: String, trim: true}
  club_info: {type: String, trim: true, required: true}
  club_logo: {type: String, trim: true}
  club_loc:
    lon: Number
    lat: Number
  active_checkins: {type: Number, default: 0, min:0}
  
VenueSchema.pre 'save', (next, done) ->
  this.updated_on = new Date().toISOString()
  next()

VenueSchema.set('toJSON', { getters: true, virtuals: true })
VenueSchema.index( { club_loc: "2d" } )
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
  conversation: [{msg: { type: String, required: true }, time: { type: Date, 'default': Date.now },from_who: {type: mongoose.Schema.ObjectId, ref: 'User', required: true}}]

ChatSchema.pre 'save', (next, done) ->
  this.updated_on = new Date().toISOString()
  next()

ChatSchema.set('toJSON', { getters: true, virtuals: true })
exports.Chat = mongoose.model 'Chat', ChatSchema



