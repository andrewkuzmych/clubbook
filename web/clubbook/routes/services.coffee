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
  console.log "FB login", req.body
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
      dob = moment(req.body.dob, "DD.MM.YYYY").toDate()
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
    dob = moment(req.body.dob, "DD.MM.YYYY").toDate()
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
    if err
      console.log err
      res.json
        status: 'error'
        err: err
    else
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
  # fix empty message type
  if not req.body.msg_type
    req.body.msg_type = "message"

  params =
    user_from: req.body.user_from
    user_to: req.body.user_to
    msg: req.body.msg
    msg_type: req.body.msg_type

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

      conversation = prepare_chat_messages(chat, req.body.user_to)[0]
      pubnab_data =
        data:
          user_from: req.body.user_from
          user_to: req.body.user_to
          last_message: conversation[conversation.length - 1]
        type: "chat"

      pubnub.publish
        channel: "message_" + req.body.user_to
        message: pubnab_data
        callback: (e) ->
          console.log "SUCCESS, PubNub message sent!", e

        error: (e) ->
          console.log "FAILED! PubNub can not send message", e

      res.json
        status: 'ok'
        chat: chat


prepare_chat_messages = (chat, current_user)->
  if chat is null then return;

  if current_user is chat.user1._id.toString()
    current_user = chat.user1
    receiver = chat.user2
  else
    current_user = chat.user2
    receiver = chat.user1

  messages = []
  for conversation in chat.conversation
    messages.push
      msg: conversation.msg
      time: conversation.time
      type: conversation.type
      from_who: conversation.from_who
      from_who_name: if conversation.from_who.toString() is current_user._id.toString() then current_user.name else receiver.name
      from_who_avatar: if conversation.from_who.toString() is current_user._id.toString() then current_user.avatar else receiver.avatar
      is_my_message: current_user._id.toString() is conversation.from_who.toString()

  return [messages, current_user, receiver]


exports.get_conversations = (req, res)->
  params =
    user_id: req.params.user_id

  manager.get_conversations params, (err, chats)->
    result = []
    for chat in chats
      if chat.unread.user && chat.unread.user.toString() == params.user_id.toString()
        unread_messages = chat.unread.count
      else
        unread_messages = 0

      chat_dto = prepare_chat_messages(chat, req.params.user_id)
      result.push
        chat_id: chat._id
        updated_on: chat.updated_on
        unread_messages: unread_messages
        conversation: chat_dto[0]
        current_user: chat_dto[1]
        receiver: chat_dto[2]

    # sort by recived date
    result = __.sortBy result, (chat)-> chat.updated_on
    result = result.reverse()

    res.json
      status: 'ok'
      result:
        chats: result

exports.get_conversation = (req, res)->
  params =
    user1: req.params.current_user
    user2: req.params.receiver

  manager.get_conversation params, (err, chat)->
    chat_dto = prepare_chat_messages chat, req.params.current_user
    console.log chat

    res.json
      status: 'ok'
      result:
        chat_id: chat._id
        conversation: chat_dto[0]
        current_user: chat_dto[1]
        receiver: chat_dto[2]


exports.cron_checkout = (req, res)->
  manager.cron_checkout()

  res.json
    status: "ok"

exports.readchat = (req, res)->
  params =
    current_user: req.params.current_user
    receiver: req.params.receiver

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

exports.checkin_clean =(req, res)->
  console.log "checkin_clean", req.params.user_id
  db_model.User.find({}).exec (err, users)->
    for user in users
      user.checkin = []
      user.save()

  db_model.Venue.find({}).exec (err, venues)->
    for venue in venues
      venue.active_checkins = 0
      venue.save()

  res.json
    status: 'ok'