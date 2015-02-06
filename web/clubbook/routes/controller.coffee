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
    db_model.Venue.find(model.my_venues).exec (err, venues)->
      model.clubs = venues
      res.render "pages/clubs", model

exports.festivals = (req, res)->
  create_base_model req, res, (model)->
    #req.params.venue_id
    db_model.Festival.find(model.my_festivals).exec (err, venues)->
      model.festivals = venues
      console.log venues
      res.render "pages/festivals", model

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
      if req.body.club_check
        venue.club_types.push "club"
      if req.body.bar_check
        venue.club_types.push "bar"
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
        res.redirect "/venue/club_news/#{req.params.id}"        
      else
        model._id = req.params.id
        model.news = news
        model.type = "club"
        res.render "pages/news", model

exports.festival_create = (req, res)->
  create_base_model req, res, (model)->
    console.log 'Festival'
    model.cloudinary = cloudinary
    model.festival = {}
    res.render "pages/festival_update", model

exports.festival_create_action = (req, res)->
  validate_club_model req, null, (validation)->
    if validation.has_error
      res.redirect "/venue/festival_create?error=1"
    else
      festival = new db_model.Festival
        fest_email : req.body.fest_email
        fest_logo : req.body.fest_logo
        fest_name : req.body.fest_name
        fest_phone : req.body.fest_phone
        fest_site : req.body.fest_site
        fest_info : req.body.fest_info
        fest_address : req.body.fest_address
      festival.fest_loc = {lon:req.body.lng, lat: req.body.lat}    
      festival.fest_photos = [];
      if req.body.fest_photos
        for fest_photo in req.body.fest_photos.split(',')
          festival.fest_photos.push fest_photo
      festival.save (err)->
        console.log err
        res.redirect "/venue/festivals" 

exports.festival_edit = (req, res)->
  create_base_model req, res, (model)->
    db_model.Festival.findById(req.params.id).exec (err, venue)->
      model.cloudinary = cloudinary
      model.festival = venue
      res.render "pages/festival_update", model

exports.festival_edit_action = (req, res)->
  validate_club_model req, null, (validation)->
    if validation.has_error
      res.redirect "/venue/festival_create?error=1"
    else
      db_model.Festival.findById(req.params.id).exec (err, venue)->
        venue.fest_email = req.body.fest_email
        venue.fest_name = req.body.fest_name
        venue.fest_phone = req.body.fest_phone
        venue.fest_site = req.body.fest_site
        venue.fest_info = req.body.fest_info
        venue.fest_address = req.body.fest_address
        venue.fest_loc = {lon:req.body.lng, lat: req.body.lat}
        venue.fest_logo = req.body.fest_logo
        venue.fest_photos = [];
        if req.body.fest_photos
          for fest_photo in req.body.fest_photos.split(',')
            venue.fest_photos.push fest_photo
        venue.save (err)->
          console.log err
          res.redirect "/venue/festivals"

exports.festival_delete_action = (req, res)->
  db_model.Festival.findByIdAndRemove(req.params.id).exec (err)->
    db_model.News.remove {"festival":req.params.id}, (err)->
      res.redirect "/venue/festivals"

exports.festival_news = (req, res)->
  create_base_model req, res, (model)->
    db_model.News.find({'festival': req.params.id}).exec (err, news)-> 
      if not news
        console.log  'missing news for this festival'
        res.redirect "/venue/festivals"        
      else
        model._id = req.params.id
        model.news = news
        model.type = "festival"
        res.render "pages/news", model

exports.festival_news_create = (req, res)->
  create_base_model req, res, (model)->
    model.cloudinary = cloudinary
    model.news = {}
    model.type_news = "news"
    model.data_time = moment().format("DD-MM-YYYY")
    model.fest_id = req.params.fest_id
    res.render "pages/news_update", model

exports.festival_news_create_action = (req, res)->
  create_news_model req, null, (news)->
    news.festival = mongoose.Types.ObjectId(req.params.fest_id)
    news.save (err)->
      console.log 'SAVE'
  res.redirect "/venue/festival_news/#{req.params.fest_id}"

exports.festival_news_edit = (req, res)->
  edit_news_model req, null, (model)->
    model.fest_id = req.params.fest_id
    res.render "pages/news_update", model

exports.festival_news_edit_action = (req, res)->
  edit_news_action_model req, null, (news)->
    res.redirect "/venue/festival_news/#{req.params.fest_id}"

exports.festival_news_delete_action = (req, res)->
  db_model.News.findByIdAndRemove(req.params.id).exec (err)->
    res.redirect "/venue/festival_news/#{req.params.fest_id}" 

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

exports.club_news_create = (req, res)->
  create_base_model req, res, (model)->
    model.cloudinary = cloudinary
    model.news = {}
    model.type_news = "news"
    model.data_time = moment().format("DD-MM-YYYY")
    model.club_id = req.params.club_id
    res.render "pages/news_update", model

exports.club_news_create_action = (req, res)->
  create_news_model req, null, (news)->
    news.venue = mongoose.Types.ObjectId(req.params.club_id)
    news.save (err)->
      console.log 'SAVE'
  res.redirect "/venue/club_news/#{req.params.club_id}"

exports.club_news_edit = (req, res)->
  edit_news_model req, null, (model)->
    model.club_id = req.params.club_id
    res.render "pages/news_update", model

exports.club_news_edit_action = (req, res)->
  edit_news_action_model req, null, (news)->
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
  if req.user
    model.my_venues = if req.user.is_admin then {} else {"club_admin": req.user._id}
    model.my_festivals = if req.user.is_admin then {} else {"fest_admin": req.user._id}
  callback model

create_news_model = (req, res, callback)->
  news = new db_model.News
    title: req.body.title
    description: req.body.description
    share: req.body.share 
    buy_tickets: req.body.buy_tickets
  if req.body.event_check
    news.type = "event"
    start_date_time = req.body.start_date + " " + req.body.start_time
    news.start_time = new Date(moment.utc(start_date_time, "DD-MM-YYYY HH:mm"))
    if req.body.end_date&&req.body.end_time
      end_date_time = req.body.end_date + " " + req.body.end_time
      news.end_time = moment.utc(end_date_time, "DD-MM-YYYY HH:mm")
  else
    news.type = "news"
  news.photos = []
  for photo in req.body.news_images.split(',')
    if photo
      news.photos.push photo
  callback news

edit_news_model = (req, res, callback)->
  create_base_model req, res, (model)->
    db_model.News.findById(req.params.id).exec (err, news)->
      if news.type == "event"
        model.type_news = news.type
        model.start_time_ = moment.utc(news.start_time).format("HH:mm")
        model.start_date_ = moment.utc(news.start_time).format("DD-MM-YYYY")
        if news.end_time
          model.end_time_ = moment.utc(news.end_time).format("HH:mm")
          model.end_date_ = moment.utc(news.end_time).format("DD-MM-YYYY")
      else
        model.type_news = "news"  
      model.data_time = moment().format("DD-MM-YYYY")
      model.cloudinary = cloudinary
      model.news = news

      callback model

edit_news_action_model = (req, res, callback)->
  db_model.News.findById(req.params.id).exec (err, news)->
    news.title = req.body.title
    news.description = req.body.description
    news.share = req.body.share
    news.buy_tickets = req.body.buy_tickets
    news.photos = []
    if req.body.event_check
      news.type = "event"
      start_date_time = req.body.start_date + " " + req.body.start_time
      news.start_time = new Date(moment.utc(start_date_time, "DD-MM-YYYY HH:mm"))
      if req.body.end_date&&req.body.end_time
        end_date_time = req.body.end_date + " " + req.body.end_time
        news.end_time = moment.utc(end_date_time, "DD-MM-YYYY HH:mm")
      else
        news.end_time = null
    else
      news.type = "news"
    for photo in req.body.news_images.split(',')
      if photo
        news.photos.push photo
    news.save (err)->
    callback news