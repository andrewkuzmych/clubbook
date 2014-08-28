mongoose = require('mongoose')
manager = require("../logic/manager")
db_model = require("../logic/model")
__ = require("underscore")
moment = require('moment-timezone')
moment.lang("ru")
email_sender = require("../email/email_sender")
async = require("async")



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

exports.privacy = (req, res)->
  create_base_model req, res, (model)->
    #model.message_sent = req.flash("message_sent").length > 0
    #db_model.Venue.find({visible:true}).exec (err, venues)->
    #model.active_venues = venues
    res.render "privacy", model

exports.home = (req, res)->
  create_base_model req, res, (model)->
    #model.message_sent = req.flash("message_sent").length > 0
    #db_model.Venue.find({visible:true}).exec (err, venues)->
    #model.active_venues = venues
    res.render "layout", model

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

# -----------------------------------------------------------------------------------------
# utils
#------------------------------------------------------------------------------------------

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

  callback model
