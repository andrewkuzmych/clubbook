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
      dob = moment.utc(req.body.dob, "DD.MM.YYYY").toDate()
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

exports.update_user = (req, res)->
  db_model.User.findById(req.params.me._id.toString()).exec (err, user)->
    if not user
      res.json
        status: "error"
        result:
          message: "can not find user by id"
    else
      if req.body.dob
        if moment(req.body.dob, "DD.MM.YYYY", true).isValid()
          req.body.dob = moment.utc(req.body.dob, "DD.MM.YYYY").toDate()
        else
          delete req.body.dob

      if req.body.password
        user.password = req.body.password
      if req.body.name
        user.name = req.body.name
      if req.body.gender
        user.gender = req.body.gender
      if req.body.dob
        user.dob = req.body.dob
      if req.body.bio
        user.bio = req.body.bio
      if req.body.country
        user.country = req.body.country
      
      # change is push property
      if req.body.push_not
        if req.body.push_not == 'false'
          user.push = false
        else
          user.push = true

      db_model.save_or_update_user user, (err)->
        if err then console.log err
        res.json
          status: "ok"
          result:
            user: user
            params: req.body


exports.user_image_add = (req, res)->
  db_model.User.findById(req.params.userId).exec (err, user)->
    if not user
      res.json
        status: "error"
        result:
          message: "can not find user by id: " + req.query.userId
    else
      req.body.avatar = JSON.parse req.body.avatar
      user.photos.push {public_id: req.body.avatar.public_id, url: req.body.avatar.url, profile: false}
      db_model.save_or_update_user user, ()->
        res.json
          status: "ok"
          result:
            user: user
            image: user.photos[user.photos.length - 1]

exports.user_image_update = (req, res)->
  db_model.User.findById(req.params.userId).exec (err, user)->
    if not user
      res.json
        status: "error"
        result:
          message: "can not find user by id: " + req.query.userId
    else
      if req.body.is_avatar
        for photo in user.photos
          photo.profile = photo._id.toString() == req.params.objectId
      db_model.save_or_update_user user, ()->
        res.json
          status: "ok"
          result:
            user: user

exports.user_image_delete = (req, res)->
  db_model.User.findById(req.params.userId).exec (err, user)->
    if not user
      res.json
        status: "error"
        result:
          message: "can not find user by id: " + req.query.userId
    else
      # do not remove the last photo or avatar
      if user.photos and user.photos.length > 1
        user.photos = __.filter user.photos, (photo)-> photo._id.toString() != req.params.objectId or photo.profile

      db_model.save_or_update_user user, ()->
        res.json
          status: "ok"
          result:
            user: user


exports.signup = (req, res)->
  console.log "----- sign up by email"
  console.log req.body

  if req.body.dob
    dob = moment.utc(req.body.dob, "DD.MM.YYYY").toDate()
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

exports.get_user_me = (req, res)->
  manager.get_user_by_id req.params.me._id.toString(), (err, user)->
    if err
      res.json
        status: "error"
        message: "can not find user: #{req.params.user_id}"
    else
      res.json
        status: "ok"
        result:
          user: user

exports.delete_user_me = (req, res)->
  res.json
    status: "ok"

exports.get_user_by_id = (req, res)->
  manager.get_friend req.params.objectId, req.params.me._id.toString(), (err, user)->
    if err
      res.json
        status: "error"
        message: "can not find user: #{req.params.user_id}"
    else
      res.json
        status: "ok"
        result:
          user: user

##################################################################################################
# friendship
exports.friends_my_test = (req, res)->
  db_model.User.findById(req.params.objectId).exec (err, user)->

    db_model.User.find({"_id": {"$ne": mongoose.Types.ObjectId(req.params.objectId)}}).select(db_model.USER_PUBLIC_INFO).sort("name").exec (err, users)->
      if err
        console.log err
        res.json
          status: "error"
          message: "can not find user friends: #{req.params.objectId}"
      else
        res.json
          status: "ok"
          result:
            friends: users
            user: user

exports.friends_my = (req, res)->
  # my_id, my_friends = user._id, user.friends
  # db.users.find({'_id':{'$in': my_friends}, 'friends': my_id})

  db_model.User.findById(req.params.objectId).exec (err, user)->

    db_model.User.find({"_id": {'$in': user.friends}, 'friends': user._id}, { checkin: {$slice: -1} }).select(db_model.USER_PUBLIC_INFO).populate('checkin.club').sort("name").exec (err, users)->
      if err
        console.log err
        res.json
          status: "error"
          message: "can not find user friends: #{req.params.objectId}"
      else
        res.json
          status: "ok"
          result:
            friends: users
            _temp_user: user


exports.friends_pending = (req, res)->
  # my_id, my_friends = user._id, user.friends
  # db.users.find({'_id':{'$not_in': my_friends}, 'friends': my_id})

  db_model.User.findById(req.params.objectId).exec (err, user)->

    db_model.User.find({"_id": {'$nin': user.friends}, 'friends': user._id}, { checkin: {$slice: -1}}).select(db_model.USER_PUBLIC_INFO).populate('checkin.club').sort("name").exec (err, users)->
      if err
        console.log err
        res.json
          status: "error"
          message: "can not find user friends: #{req.params.objectId}"
      else
        res.json
          status: "ok"
          result:
            friends: users
            _temp_user: user


exports.friends_request = (req, res)->
  # user.friends.push friend_id

  if req.params.objectId is req.params.friendId
    res.json
      status: "error"
      message: "users are the same"

  else
    db_model.User.findById(req.params.objectId).exec (err, user)->
      user.friends.push req.params.friendId
      db_model.save_or_update_user user, ()->
        res.json
          status: "ok"
          result:
            message: "friend request"
            _temp_user: user


exports.friends_confirm = (req, res)->
  # user.friends.push friend_id

  if req.params.objectId is req.params.friendId
    res.json
      status: "error"
      message: "users are the same"

  else
    # user.friends.push friend_id
    db_model.User.findById(req.params.objectId).exec (err, user)->
      user.friends.push req.params.friendId
      db_model.save_or_update_user user, ()->
        res.json
          status: "ok"
          result:
            message: "friend request"
            _temp_user: user


exports.friends_unfriend = (req, res)->
  # user.friends.remove friend_id
  if req.params.objectId is req.params.friendId
    res.json
      status: "error"
      message: "users are the same"

  else
    # user.friends.push friend_id
    db_model.User.findById(req.params.objectId).exec (err, user)->
      user.friends = __.filter user.friends, (friend)-> friend.toString() isnt req.params.friendId
      db_model.save_or_update_user user, ()->

        db_model.User.findById(req.params.friendId).exec (err, user_friend)->
          user_friend.friends = __.filter user_friend.friends, (friend)-> friend.toString() isnt user._id.toString()
          user_friend.save ()->

            res.json
              status: "ok"
              result:
                message: "friend request"
                _temp_user: user

exports.friends_remove_request = (req, res)->
  if req.params.objectId is req.params.friendId
    res.json
      status: "error"
      message: "users are the same"

  else
    db_model.User.findById(req.params.friendId).exec (err, friend)->
      friend.friends = __.filter friend.friends, (user_friend)-> user_friend.toString() isnt req.params.objectId
      friend.save ()->
        res.json
          status: "ok"
          result:
            message: "remove friend request"
            _temp_user: friend

##################################################################################################

exports.get_config = (req, res)->
  res.json
    status: "ok"
    result:
      chekin_max_distance: 1000
      max_failed_checkin_count: 3
      update_checkin_status_interval: 10*60

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
  manager.find_club req.params.objectId, req.params.me._id.toString(), (err, club, users, friends_count)->
    if err
      res.json
        status: "error"
        err: err
    else
      res.json
        club: club
        users: users
        friends_count: friends_count
        status: "Found Club OK!"


exports.list_club = (req, res)->
  console.log req.params

  params =
    distance: req.query.distance
    lat: req.query.user_lat
    lon: req.query.user_lon

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


exports.checkin = (req, res)->
  params =
    user_id: req.params.me._id.toString()
    club_id: req.params.objectId

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
    user_id: req.params.me._id.toString()
    club_id: req.params.objectId

  manager.update_checkin params, (err, user)->
    res.json
      status: 'ok'
      user: user

exports.checkout = (req, res)->
  params =
    user_id: req.params.me._id.toString()
    club_id: req.params.objectId

  manager.checkout params, (err, user)->
    res.json
      status: 'ok'
      user: user

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
      message = req.body.msg
      
      Parse = require("parse").Parse
      Parse.initialize config.parse_app_id, config.parse_js_key
      
      # send message to Parse (android)
      queryAndroid = new Parse.Query(Parse.Installation)
      queryAndroid.equalTo "channels", 'user_' + req.body.user_to
      queryAndroid.equalTo "deviceType", "android"
      Parse.Push.send
        where: queryAndroid 
        data:
          action: "com.nl.clubbook.UPDATE_STATUS"
          alert: "hello Amsterdam"
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

      # send message to Parse (ios)
      queryIOS = new Parse.Query(Parse.Installation)
      queryIOS.equalTo "channels", 'user_' + req.body.user_to
      queryIOS.equalTo "deviceType", "ios"      
      Parse.Push.send
        where: queryIOS 
        data:
          badge: "Increment"
          alert: user_from.name + ': "' + message + '"'
      ,
        success: ->
          console.log "push sent"

      # Push was successful
        error: (error) ->
          console.log "push error: "
          console.log error

      # send message to pubnub
      pubnub = require("pubnub").init({ publish_key: config.pub_publish_key, subscribe_key: config.pub_subscribe_key})
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
    conversationTime = moment.utc(conversation.time).format("YYYY-MM-DD, HH:mm:ss")
    messages.push
      msg: conversation.msg
      time: conversationTime
      type: conversation.type
      from_who: conversation.from_who
      from_who_name: if conversation.from_who.toString() is current_user._id.toString() then current_user.name else receiver.name
      from_who_avatar: if conversation.from_who.toString() is current_user._id.toString() then current_user.avatar else receiver.avatar
      is_my_message: current_user._id.toString() is conversation.from_who.toString()

  return [messages, current_user, receiver]


exports.get_conversations = (req, res)->
  params =
    user_id: req.params.current_user

  manager.get_conversations params, (err, chats)->
    result = []
    for chat in chats
      if chat.unread.user && chat.unread.user.toString() == params.user_id.toString()
        unread_messages = chat.unread.count
      else
        unread_messages = 0

      chat_dto = prepare_chat_messages(chat, req.params.current_user)
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

    res.json
      status: 'ok'
      result:
        chat_id: chat._id
        conversation: chat_dto[0]
        current_user: chat_dto[1]
        receiver: chat_dto[2]

exports.readchat = (req, res)->
  params =
    current_user: req.params.current_user
    receiver: req.params.receiver

  manager.readchat params, (err, readchat)->
    res.json
      status: 'ok'

exports.unread_messages_count = (req, res)->
  manager.unread_messages_count req.params.current_user, (err, unread_chat_count, pending_friends_count)->
    res.json
      status: 'ok'
      unread_chat_count: unread_chat_count
      pending_friends_count: pending_friends_count

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
      db_model.save_or_update_user user, (err)-> console.log "done cleeacn checkins"

  db_model.Venue.find({}).exec (err, venues)->
    for venue in venues
      venue.active_checkins = 0
      venue.save()

  res.json
    status: 'ok'