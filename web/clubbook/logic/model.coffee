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
  name: {type: String, trim: true, required: true}
  
  gender: {type: String, trim: true, required: true, 'enum':["male", "female"]}
  photos: [{url:{ type: String }, profile:{ type: Boolean, default:false }}]
  dob: { type: Date }

  password: {type: String}
  
  ios_tokens: [{ type: String }]
  android_tokens: [{ type: String }]
          
  fb_id: {type:String, unique: true, sparse: true}
  fb_access_token: {type:String}
  fb_token_expires: Number
  
  checkin: [{club: { type: mongoose.Schema.ObjectId, ref: 'Venue' }, lon: Number, lat: Number, time: Date, active: Boolean}]


UserSchema.pre 'save', (next, done) ->
  this.updated_on = new Date().toISOString()
  next()

UserSchema.virtual('avatar').get ()->
  if this.fb_id
    "https://graph.facebook.com/#{this.fb_id}/picture?width=200&height=200"
  else
    if this.gender is "male"
      "http://socialmediababe.com/wp-content/uploads/2011/07/FB-profile-avatar.jpg"
    else if this.gender is "female"
      "http://smartcitiesindex.gsma.com/community/styles/default/xenforo/avatars/avatar_female_m.png"

UserSchema.set('toJSON', { getters: true, virtuals: true })
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
  
VenueSchema.pre 'save', (next, done) ->
  this.updated_on = new Date().toISOString()
  next()

VenueSchema.set('toJSON', { getters: true, virtuals: true })
VenueSchema.index( { club_loc: "2d" } )
exports.Venue = mongoose.model 'Venue', VenueSchema

#-------------------------------------------------------------------------------------
#  Checkin
#-------------------------------------------------------------------------------------
CheckinSchema = new mongoose.Schema
  created_on: { type: Date, 'default': Date.now }
  updated_on: { type: Date, 'default': Date.now }

  venue: {type: mongoose.Schema.ObjectId, ref: 'Venue', required: true}
  user: {type: mongoose.Schema.ObjectId, ref: 'User', required: true}

CheckinSchema.pre 'save', (next, done) ->
  this.updated_on = new Date().toISOString()
  next()

CheckinSchema.set('toJSON', { getters: true, virtuals: true })
exports.Checkin = mongoose.model 'Checkin', CheckinSchema
