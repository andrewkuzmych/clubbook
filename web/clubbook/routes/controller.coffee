mongoose = require('mongoose')
manager = require("../logic/manager")
db_model = require("../logic/model")
__ = require("underscore")
moment = require('moment-timezone')
moment.lang("ru")
email_sender = require("../email/email_sender")
async = require("async")
http = require('http');
path = require('path');
crypto = require('crypto');
cloudinary = require('cloudinary')
fs = require('fs')

exports.user_push = (req, res)->
  console.log 'user_' + req.params.user_id
  send_push 'user_' + req.params.user_id, req.body.message, 'header', req.body.message
         
  res.json
    status: 'ok'


send_push = (channel, msg, header, alert)->
    #send push
    Parse = require("parse").Parse
    Parse.initialize config.parse_app_id, config.parse_js_key
    
    # send message to Parse (android)
    queryAndroid = new Parse.Query(Parse.Installation)
    queryAndroid.equalTo "channels", channel
    queryAndroid.equalTo "deviceType", "android"
    Parse.Push.send
      where: queryAndroid 
      data:
        alert: alert
        #action: "com.nl.clubbook.UPDATE_STATUS"
        msg: msg
        header: header
    ,
      success: ->
        console.log "push sent"

    # Push was successful
      error: (error) ->
        console.log "push error: "
        console.log error

    queryIOS = new Parse.Query(Parse.Installation)
    queryIOS.equalTo "channels", channel
    queryIOS.equalTo "deviceType", "ios"      
    Parse.Push.send
      where: queryIOS 
      data:
        badge: "Increment"
        alert: alert
        sound: "nothing"

exports.download = (req, res)->
  res.render "download", {}

exports.index = (req, res)->
  create_base_model req, res, (model)->
    #model.message_sent = req.flash("message_sent").length > 0
    #db_model.Venue.find({visible:true}).exec (err, venues)->
    #model.active_venues = venues
    res.render "landing", model

exports.login = (req, res)->
  create_base_model req, res, (model)->
    #model.message_sent = req.flash("message_sent").length > 0
    #db_model.Venue.find({visible:true}).exec (err, venues)->
    #model.active_venues = venues
    res.render "login", model

exports.terms = (req, res)->
  create_base_model req, res, (model)->
    #model.message_sent = req.flash("message_sent").length > 0
    #db_model.Venue.find({visible:true}).exec (err, venues)->
    #model.active_venues = venues
    res.render "terms", model

exports.faq = (req, res)->
  create_base_model req, res, (model)->
    #model.message_sent = req.flash("message_sent").length > 0
    #db_model.Venue.find({visible:true}).exec (err, venues)->
    #model.active_venues = venues
    res.render "faq", model

exports.privacy = (req, res)->
  create_base_model req, res, (model)->
    #model.message_sent = req.flash("message_sent").length > 0
    #db_model.Venue.find({visible:true}).exec (err, venues)->
    #model.active_venues = venues
    res.render "privacy", model

exports.home = (req, res)->
  create_base_model req, res, (model)->
    db_model.Venue.findOne().exec (err, venue)->
      res.redirect "/venue/clubs"

exports.clubs = (req, res)->
  create_base_model req, res, (model)->
    #req.params.venue_id
    db_model.Venue.find().exec (err, venues)->
        model.clubs = venues
        res.render "pages/clubs", model

exports.users = (req, res)->
  console.log 'Users'
  create_base_model req, res, (model)->
      res.render "pages/users", model

  #create_base_model req, res, (model)->
  #  res.render "pages/users", model

exports.club_create = (req, res)->
  create_base_model req, res, (model)->
    console.log 'CLUB'
    model.cloudinary = cloudinary
    model.club = {}

    club_working_hours =  [ {"day" : 1, "status" : "closed"},
                            {"day" : 2, "status" : "closed"},
                            {"day" : 3, "status" : "closed"},
                            {"day" : 4, "status" : "closed"},
                            {"day" : 5, "status" : "closed"},
                            {"day" : 6, "status" : "closed"},
                            {"day" : 0, "status" : "closed"}  ]

    model.club.club_working_hours = club_working_hours
    model.age_restrictions = ["n/a", "18+", "21+", "23+", "25+"]
    res.render "pages/club_update", model


exports.club_create_action = (req, res)->
  validate_club_model req, null, (validation)->
    if validation.has_error
      res.redirect "/venue/club_create?error=1"
    else
      venue = new db_model.Venue
        club_email : req.body.club_email
        club_logo : req.body.club_logo
        club_name : req.body.club_name
        club_phone : req.body.club_phone
        club_site : req.body.club_site
        club_info : req.body.club_info
        club_address : req.body.club_address
        club_age_restriction : req.body.club_age_restriction
        club_capacity : req.body.club_capacity
        
      venue.club_loc = {lon:req.body.lng, lat: req.body.lat}
      venue.club_working_hours = []
      for day in [0..6]
        wh =
          day: day
        if req.body["start_date_" + day] && req.body["end_date_" + day]
          wh.start_time = req.body["start_date_" + day]
          wh.end_time = req.body["end_date_" + day]
          wh.status = 'opened'
        else
          wh.status = 'N/A'

        venue.club_working_hours.push wh


      venue.club_photos = [];

      for club_photo in req.body.club_images.split(',')
        venue.club_photos.push club_photo
        
      if req.body.club_logo
        venue.club_logo = req.body.club_logo
      console.log venue
      venue.save (err)->
        console.log 1111111111
        console.log err
        res.redirect "/venue/clubs" 
 
exports.club_news = (req, res)->
  create_base_model req, res, (model)->
    db_model.News.find({'venue': req.params.id}).exec (err, news)-> 
      if not news
        console.log  'missing news for this club'
      else
        model._id = req.params.id
        model.news = news
        res.render "pages/club_news", model

exports.club_edit = (req, res)->
  create_base_model req, res, (model)->
    db_model.Venue.findById(req.params.id).exec (err, venue)->
      model.cloudinary = cloudinary
      model.club = venue
      model.age_restrictions = ["n/a", "18+", "21+", "23+", "25+"]
      console.log model.club
      res.render "pages/club_update", model

exports.club_edit_action = (req, res)->
  validate_club_model req, null, (validation)->
    if validation.has_error
      res.redirect "/venue/club_edit?error=1"
    else
      db_model.Venue.findById(req.params.id).exec (err, venue)->
        venue.club_email = req.body.club_email
        venue.club_name = req.body.club_name
        venue.club_phone = req.body.club_phone
        venue.club_site = req.body.club_site
        venue.club_info = req.body.club_info
        venue.club_address = req.body.club_address
        venue.club_loc = {lon:req.body.lng, lat: req.body.lat}
        venue.club_age_restriction = req.body.club_age_restriction
        venue.club_capacity = req.body.club_capacity

        for wh in venue.club_working_hours
            console.log "start_date_" + wh.day
            console.log  req.body["start_date_" + wh.day]
            if req.body["start_date_" + wh.day] && req.body["end_date_" + wh.day]
              wh.status = 'opened'
              wh.start_time = req.body["start_date_" + wh.day]
              wh.end_time = req.body["end_date_" + wh.day]
            else
              wh.status = 'N/A'


        console.log venue.club_working_hours

        venue.club_photos = [];

        for club_photo in req.body.club_images.split(',')
          venue.club_photos.push club_photo
        
        if req.body.club_logo
          venue.club_logo = req.body.club_logo

        venue.save (err)->
          console.log err
          res.redirect "/venue/clubs"
    
exports.club_delete_action = (req, res)->
  db_model.Venue.findByIdAndRemove(req.params.id).exec (err)->
    db_model.News.remove {"venue":req.params.id}, (err)->
      res.redirect "/venue/clubs"

###exports.news = (req, res)->
  create_base_model req, res, (model)->
    db_model.Venue.findById(req.params.venue_id).exec (err, venue)->
      db_model.News.find({'venue' : venue}).sort({created_on: 'desc'}).exec (err, news)->
        #model.news = news
        news_new = []
        if news
          __.each news, ((the_new) ->
            the_news_new = 
              _id: the_new._id
              title: the_new.title
              description: the_new.description
              image: the_new.image
              updated_on: moment.utc(the_new.updated_on).format("DD.MM.YYYY")

            news_new.push the_news_new
          ), this

        model.news = news_new

        res.render "pages/news", model###

exports.club_news_create = (req, res)->
  create_base_model req, res, (model)->
    model.cloudinary = cloudinary
    model.news = {}
    res.render "pages/news_update", model

exports.club_news_create_action = (req, res)->
  validate_news_model req, null, (validation)->
    if validation.has_error
      res.redirect "/venue/news_create?error=1"
    else
      news = new db_model.News
        venue: mongoose.Types.ObjectId(req.params.id)
        title: req.body.title
        description: req.body.description
      if req.body.news_image
        news.image = req.body.news_image
      news.save (err)->
        console.log 'SAVE'
        res.redirect "/venue/club_news/#{req.params.id}"

###exports.news_create = (req, res)->
  create_base_model req, res, (model)->
    model.cloudinary = cloudinary
    model.news = {}
    res.render "pages/news_update", model

exports.news_create_action = (req, res)->
  validate_news_model req, null, (validation)->
    if validation.has_error
      res.redirect "/venue/#{req.params.venue_id}/news_create?error=1"
    else
      news = new db_model.News
        venue: mongoose.Types.ObjectId(req.params.venue_id)
        title: req.body.title
        description: req.body.description

      if req.body.news_image
        news.image = req.body.news_image
      news.save (err)->
        console.log 'SAVE'
        res.redirect "/venue/#{req.params.venue_id}/news" ###

exports.news_edit = (req, res)->
  create_base_model req, res, (model)->
    db_model.News.findById(req.params.id).exec (err, news)->
      model.cloudinary = cloudinary
      model.news = news
      console.log news
      console.log model.news
      res.render "pages/news_update", model

exports.news_edit_action = (req, res)->
  validate_news_model req, null, (validation)->
    if validation.has_error
      res.redirect "/venue/news_edit/#{req.params.id}?error=1"
    else
      db_model.News.findById(req.params.id).exec (err, news)->
        news.title = req.body.title
        news.description = req.body.description
        if req.body.news_image
          news.image = req.body.news_image

        news.save (err)->
          res.redirect "/venue/club_news/#{req.params.club_id}" 

exports.news_delete_action = (req, res)->
  db_model.News.findByIdAndRemove(req.params.id).exec (err)->
    res.redirect "/venue/club_news/#{req.params.club_id}"


exports.reset_pass = (req, res)->
  create_base_model req, res, (model)->
    #model.message_sent = req.flash("message_sent").length > 0
    #db_model.Venue.find({visible:true}).exec (err, venues)->
    #model.active_venues = venues
    res.render "reset_pass", model

exports.reset_pass_action = (req, res)->
  email = req.body.email.replace /^\s+|\s+$/g, ""
  error = ""
  success = ""
  manager.findUserByEmail email, (err, user)->
    if user
        #email_sender.send_pass user, (err, info)->
        success = "password sent on email"
        res.render "reset_pass", {title: "Clubbook", error: error, success: success}
        #res.redirect "/login?message="+req.i18n.t("pass_send_on_email")
        #success = "пароль выслан на email"
      # send pass
    else
      error = 'email not registered'#req.i18n.t("email_not_registered")
      res.render "reset_pass", {title: "Clubbook", error: error, success: success}

exports.cloudinary_upload = (req, res)->
  cloudinary.api.resources (items) ->
    res.render "index",
      images: items.resources
      cloudinary: cloudinary

    return

# -----------------------------------------------------------------------------------------
# utils
#------------------------------------------------------------------------------------------

validate_news_model = (req, prize, callback)->
  validation =
    has_error:false
  if not req.body.title or not req.body.title.trim()
    validation.has_error = true
    console.log "title"
  if not req.body.description or not req.body.description.trim()
    validation.has_error = true
    console.log "description"
  
  callback validation
  

validate_club_model = (req, prize, callback)->
  validation =
    has_error:false
  #if not req.body.title or not req.body.title.trim()
  #  validation.has_error = true
  #  console.log "title"
  #if not req.body.description or not req.body.description.trim()
  #  validation.has_error = true
  #  console.log "description"
  
  callback validation

create_base_model = (req, res, callback)->
  # default values
  model =
    title: "Clubbook"
    req_path: req.path
    config: config

  # Open Graph tags
  model["meta_data"] =
    title: config.meta.title
    type: config.meta.type
    url: req.originalUrl
    image: config.meta.image
    description: config.meta.description
    fb_admins: config.meta.admins
    fb_app_id: config.meta.app_id

  # disaply error logic
  if req.query.error
    model.has_error = true
  else
    model.has_error = false

  ###  # manage venue page
    if req.params.venue
      model.current_venue = req.params.venue
      # dropdown with available venues
    if req.user
      query_venue = if req.user.is_admin then {} else {"admins": req.user._id}
      db_model.Venue.find(query_venue).sort("title").exec (err, venues)->
        model.my_venues = venues
        if venues.length > 0
          if req.params.venue_id
            model.active_venue = __.find venues, (venue)-> venue._id.toString() is req.params.venue_id
          if not model.active_venue then model.active_venue = venues[0]
        callback model
    else###
  callback model
