mongoose = require('mongoose')
manager = require("../logic/manager")
db_model = require("../logic/model")
__ = require("underscore")
async = require("async")
moment = require('moment-timezone')
email_sender = require("../email/email_sender")
check = require('validator').check
apn = require('apn')
gcm = require('node-gcm')


exports.fb_signin = (req, res)->
  errors = {}
  # validate empty fields
  if __.isEmpty req.body.fb_id?.trim()
    errors["fb_id"] =
      message: "fb_id is empty"
      path: "fb_id"
  if __.isEmpty req.body.fb_access_token?.trim()
    errors["fb_access_token"] =
      message: "fb_access_token is empty"
      path: "fb_access_token"
  if __.isEmpty req.body.gender?.trim()
    errors["gender"] =
      message: "gender is empty"
      path: "gender"
  if __.isEmpty req.body.name?.trim()
    errors["name"] =
      message: "name is empty"
      path: "name"

  if not __.isEmpty errors
    res.json
      status: "error"
      errors: errors

  else
    if req.body.dob
      dobArray = req.body.dob.split(".")
      dob = new Date(dobArray[2], parseInt(dobArray[1]) - 1, dobArray[0], 15, 0, 0, 0)
      req.body.dob = dob

    if req.body.avatar
      console.log "avatar", req.body.avatar
      req.body.avatar = JSON.parse req.body.avatar

    manager.save_or_update_fb_user req.body, (err, user)->
      if err
        console.log "fb error:", err
        res.json
          status: "error"
          errors: err.errors
      else
        console.log "return user", user
        res.json
          status: "ok"
          result:
            user: user

exports.signinmail = (req, res)->
  params =
    email: req.body.email
    password: req.body.password

  manager.signinmail params, (err, user)->
    if err
      res.json
        status: "error"
        err: err
    else
      res.json
        status: "ok"
        result:
          user: user


exports.signup = (req, res)->
  console.log "----- sign up by email"
  console.log req.body

  if req.body.dob
    dobArray = req.body.dob.split(".")
    dob = new Date(dobArray[0], parseInt(dobArray[1]) - 1, dobArray[2], 15, 0, 0, 0);
    req.body.dob = dob

  if req.body.avatar
    console.log "avatar", req.body.avatar
    req.body.avatar = JSON.parse req.body.avatar

  manager.save_user req.body, (err, user)->
    if err
      res.json
        status: "error"
        err: err
    else
      res.json
        status: "ok"
        result:
          user: user

exports.get_user_by_id = (req, res)->
  manager.get_user_by_id req.params.user_id, (err, user)->
    if err
      res.json
        status: "error"
        message: "can not find user: #{req.params.user_id}"
    else
      res.json
        status: "ok"
        result:
          user: user

exports.uploadphoto = (req, res)->
  params =
    userid: req.body._id
    photos: req.body.photos

  manager.uploadphoto params, (err, user)->
    if err
      res.json
        status: "error"
        err: err
    else
      res.json
        user: user
        status: "OK"

exports.create_club = (req, res)->
  adminsarray = req.body.club_admin.split(";")
  photosarray = req.body.club_photos.split(";")

  loc =
    lat: req.body.club_lat
    lon: req.body.club_lon

  console.log loc


  params =
    club_admin: adminsarray
    club_name: req.body.club_name
    club_email: req.body.club_email
    club_houres: req.body.club_houres
    club_photos: photosarray
    club_phone: req.body.club_phone
    club_address: req.body.club_address
    club_site: req.body.club_site
    club_info: req.body.club_info
    club_loc: loc
    club_logo: req.body.club_logo

  manager.create_club params, (err, club)->
    if err
      res.json
        status: "error"
        err: err
    else
      res.json
        club: club
        status: "Added OK"


exports.find_club = (req, res)->
  manager.find_club req.params.club_id, req.params.user_id, (err, club, users)->
    if err
      res.json
        status: "error"
        err: err
    else
      res.json
        club: club
        users: users
        status: "Found Club OK!"


exports.list_club = (req, res)->
  params =
    distance: req.params.distance
    lat: req.params.user_lat
    lon: req.params.user_lon

  manager.list_club params, (err, clubs)->
    if err
      res.json
        status: 'error'
        error: err
    else
      # get people who checkin in these clubs
      res.json
        status: 'ok'
        clubs: clubs

exports.cu_count = (req, res)->
  params =
    distance: req.params.distance
    lat: req.params.user_lat
    lon: req.params.user_lon

  manager.cu_count params, (err, club_count)->
    if err
      res.json
        status: 'error'
        error: err
    else
      res.json
        status: 'ok'
        club_count: club_count

exports.checkin = (req, res)->
  params =
    user_id: req.params.user_id
    club_id: req.params.club_id

  manager.checkin params, (err, user)->
    res.json
      status: 'ok'
      user: user

exports.update_checkin = (req, res)->
  params =
    user_id: req.params.user_id
    club_id: req.params.club_id

  manager.update_checkin params, (err, user)->
    res.json
      status: 'ok'
      user: user

exports.checkout = (req, res)->
  params =
    user_id: req.params.user_id
    club_id: req.params.club_id

  manager.checkout params, (err, user)->
    res.json
      status: 'ok'
      user: user

exports.club_clubbers = (req, res)->
  params =
    club_id: req.params.club_id

  manager.club_clubbers params, (err, users)->
    res.json
      status: 'ok'
      users: users

exports.chat = (req, res)->
  params =
    user_from: req.body.user_from
    user_to: req.body.user_to
    msg: req.body.msg

  manager.get_user_by_id req.body.user_from, (err, user_from)->
    manager.chat params, (err, chat)->
      pubnub = require("pubnub").init({ publish_key: config.pub_publish_key, subscribe_key: config.pub_subscribe_key})
      message = req.body.msg

      Parse = require("parse").Parse
      Parse.initialize config.parse_app_id, config.parse_js_key
      Parse.Push.send
        channels: [ 'user_' + req.body.user_to]
        data:
          action: "com.nl.clubbook.UPDATE_STATUS"
          msg: message
          unique_id: req.body.user_from + "_" + req.body.user_to
          header: user_from.name
          type: "chat"
      ,
        success: ->
          console.log "push sent"

      # Push was successful
        error: (error) ->
          console.log "push error: "
          console.log error

      pubnab_data =
        msg: message
        user_from: req.body.user_from
        user_to: req.body.user_to
        type: "chat"


      pubnub.publish
        channel: "message_" + req.body.user_to
        message: pubnab_data
        callback: (e) ->
          console.log "SUCCESS yes!", e

        error: (e) ->
          console.log "FAILED! RETRY PUBLISH!", e

      res.json
        status: 'ok'
        chat: chat


exports.get_conversations = (req, res)->
  params =
    user_id: req.params.user_id

  manager.get_conversations params, (err, conversations)->
    res.json
      status: 'ok'
      conversations: conversations

exports.get_conversation = (req, res)->
  params =
    user1: req.params.user1
    user2: req.params.user2

  manager.get_conversation params, (err, chat)->
    res.json
      status: 'ok'
      chat_id: chat._id
      conversation: chat.conversation


exports.cron_checkout = (req, res)->
  manager.cron_checkout()

  res.json
    status: "ok"

exports.readchat = (req, res)->
  params =
    chat_id: req.params.chat_id
    user_id: req.params.user_id

  manager.readchat params, (err, readchat)->
    res.json
      status: 'ok'

exports.unread_messages_count = (req, res)->
  manager.unread_messages_count req.params.user_id, (err, count)->
    res.json
      status: 'ok'
      count: count
      
exports.update_name =(req, res)->
  params = 
    user_id: req.params.user_id
    name: req.body.name

  manager.update_name params, (err, user)->
     res.json
        status: 'ok'
        user: user

exports.update_dob =(req, res)->
  params = 
    user_id: req.params.user_id
    dob: req.body.dob

  manager.update_dob params, (err, user)->
     res.json
        status: 'ok'
        user: user

exports.update_gender =(req, res)->
  params = 
    user_id: req.params.user_id
    gender: req.body.gender

  manager.update_gender params, (err, user)->
     res.json
        status: 'ok'
        user: user

exports.update_info =(req, res)->
  params = 
    user_id: req.params.user_id
    info: req.body.info

  manager.update_info params, (err, user)->
     res.json
        status: 'ok'
        user: user

exports.remove_user =(req, res)->
  console.log "remove user", req.params.user_id
  db_model.User.remove {"_id": req.params.user_id}, (err)->
    db_model.Chat.remove {"user1": req.params.user_id}, (err)->
      db_model.Chat.remove {"user2": req.params.user_id}, (err)->
        res.json
          status: 'ok'