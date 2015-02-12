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
cloudinary = require('cloudinary')
  

exports.user_list = (req, res)->
  console.log 'USER LIST'
  console.log req.query.iSortCol_0
  echo = req.query.sEcho + 1

  if req.query.sSortDir_0 == 'asc'
    sort_order = 1
  else
    sort_order = -1

  switch req.query.iSortCol_0
     when '0' then sort_users = { created_on: sort_order }
     when '1' then sort_users = { name: sort_order }
     when '2' then sort_users = { email: sort_order }
     when '3' then sort_users = { gender: sort_order}
     when '4' then sort_users = { created_on: sort_order}
     when '5' then sort_users = { name: sort_order}
     when '6' then sort_users = { name: sort_order}
     else sort_users = { created_on: -1 }


  query = { name: { $exists: true } }

  db_model.User.count(query).exec (err, user_count)->
    #.sort(sort_users).skip(parseInt( req.query.iDisplayStart, 0)).limit(parseInt(req.query.iDisplayLength, 0 ))
    db_model.User.find(query).sort(sort_users).skip(parseInt( req.query.iDisplayStart, 0)).limit(parseInt(req.query.iDisplayLength, 0 )).exec (err, users)->
          data = []
          #feedbacks = __.filter feedbacks, (client)-> client != null
          for user in users
            avatar = cloudinary.image(user.avatar.public_id, { height: 50 }) #"http"#"<img border='1', src='" + user.avatar + "', alt='Pulpit rock', width='50', height='50'/>"

            name = ''
            if user.fb_id
              name = "<span><a href='http://facebook.com/" + user.fb_id + "', target='_blank'>" + user.name + " </a></span>" +
                     "<a href='http://facebook.com/" + user.fb_id + "', target='_blank'><img border='1', src='/img/facbook_logo.gif', alt='Pulpit rock', width='15', height='15'/></a>"
            else
              name = "<span>" + user.name + "</span>"

            email = "<a href='mailto:" + user.email + "'>" + user.email + "</a>"
            
            gender = "<span>" + user.gender + "</span>"

            created_on = moment.utc(user.created_on).format("DD.MM.YYYY")

            #description = "<span style='color: #817E7E;font-style: italic;'>" + feedback.description + "</span>"

            # replays = ""
            # for messages in feedback.replays
            #   replays += messages
            #   replays += "<br>"

            replay = """
               <input class="user_replay btn default blue" type="button" value="Send Message from Clubbook" user_id="#{user._id}"  />
               <div name="user_replay_form" style="display: none">
                 <input type="hidden" name="user_id" value="#{user._id}">
                 <textarea rows="4" cols="40" name="user_replay_text"></textarea> <br><br>
                 <input class="user_send btn default green" type="button" value="Send" />
                 <input class="user_send_cancel btn default" type="button" value="Cancel" />
               </div>
            """
            data.push [avatar, name, email, gender, created_on, replay]
          
          console.log user_count
          console.log users.count
          res.json
            sEcho: echo
            iTotalRecords: user_count
            iTotalDisplayRecords: user_count
            aaData: data

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

      # change is visible nearby property
      if req.body.is_visible_nearby
        if req.body.is_visible_nearby == 'false'
          user.is_visible_nearby = false
        else
          user.is_visible_nearby = true

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

exports.update_user_location = (req, res)->
  current_user_id = req.params.me._id.toString()
  db_model.User.findById(current_user_id).exec (err, current_user)->
    current_user.last_loc = {lon: parseFloat(req.query.user_lon), lat: parseFloat(req.query.user_lat)}
    db_model.save_or_update_user current_user, (err)-> 
        res.json
          status: "ok"
          result:
            user: current_user

exports.users_checkedin = (req, res)->
  current_user_id = req.params.me._id.toString()
  skip = 0
  if req.query.skip
    skip = parseInt(req.query.skip)
  take = 100
  if req.query.take
    take = parseInt(req.query.take)

  db_model.User.findById(current_user_id).exec (err, current_user)->
    db_model.Venue.find({club_loc: { '$near' : [req.query.user_lon, req.query.user_lat], '$maxDistance': parseFloat(req.query.distance)/112 }}).exec (err, venues)->
      venue_objects = __.map venues, (venue)->
        venue._id.toString()
      
      query = {'checkin': { '$elemMatch': { 'club' : {'$in': venue_objects}, 'active': true}}, 'bloked_users': {'$ne': current_user._id}}
      if req.query.gender
        query.gender = req.query.gender
      db_model.User.find(query, { checkin: {$slice: -1}}).skip(skip).limit(take).populate('checkin.club').exec (err, users)->
        converted_users = []
        for user in users
          user_object = convert_user_to_friend(current_user, user)
          converted_users.push user_object   
        res.json
          status: "ok"
          users: converted_users

exports.users_around = (req, res)->
  current_user_id = req.params.me._id.toString()
  skip = 0
  if req.query.skip
    skip = parseInt(req.query.skip)
  take = 20
  if req.query.take
    take = parseInt(req.query.take)
  db_model.User.findById(current_user_id).exec (err, current_user)->
    geoNear = 
        near: [parseFloat(req.query.user_lon), parseFloat(req.query.user_lat) ] ,
        distanceField: "distance",
        maxDistance: parseFloat(req.query.distance)/6371,
        spherical: true,
        distanceMultiplier: 6371  
    match = { name: { '$exists': true } }
    if req.query.gender
      match.gender = req.query.gender

    query =  [{'$geoNear': geoNear}, {'$match': match}, {'$skip':skip}, {'$limit':take}]
    db_model.User.aggregate query,{}, (err, users)->
      converted_users = []
      for user in users
        user_object = user_to_friend(current_user, user)
        converted_users.push user_object   
      res.json
        status: "ok"
        users: converted_users

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

exports.update_pass = (req, res)->
  access_token = req.param("access_token")
  db_model.User.findOne({access_token: access_token}).exec (err, user)->
    if user.password == req.body.old_password
      user.password = req.body.new_password
      db_model.save_or_update_user user, (err)->
        if err then console.log err
        res.json
          status: "ok"
          result:
            user: user
    else
        res.json
          status: "error"
          result:
            msg: "wrong password"

exports.delete_user_me = (req, res)->
  access_token = req.param("access_token")
  db_model.User.findOne({access_token: access_token}).exec (err, user)->
    db_model.User.remove {"_id": user._id}, (err)->
      db_model.Chat.remove {"user1": user._id}, (err)->
        db_model.Chat.remove {"user2": user._id}, (err)->
          res.json
            status: 'ok'

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

  db_model.User.findById(req.params.objectId).exec (err, current_user)->
    db_model.User.find({"_id": {'$in': current_user.friends}, 'bloked_users': {'$ne': current_user._id}, 'friends': current_user._id}, { checkin: {$slice: -1} }).populate('checkin.club').sort("name").exec (err, users)->
      db_model.User.count({"_id": {'$nin': current_user.friends}, 'friends': current_user._id}).exec (err, pending_count)->
        converted_users = []
        for user in users
          user_object = convert_user_to_friend(current_user, user)

          converted_users.push user_object   

        if err
          console.log err
          res.json
            status: "error"
            message: "can not find user friends: #{req.params.objectId}"
        else
          res.json
            status: "ok"
            result:
              friends: converted_users
              friends_count: converted_users.length
              pending_count: pending_count
              _temp_user: current_user

exports.friends_pending = (req, res)->
  # my_id, my_friends = user._id, user.friends
  # db.users.find({'_id':{'$not_in': my_friends}, 'friends': my_id})

  db_model.User.findById(req.params.objectId).exec (err, current_user)->
    db_model.User.find({"_id": {'$nin': current_user.friends}, 'friends': current_user._id}, { checkin: {$slice: -1}}).populate('checkin.club').sort("name").exec (err, users)->
      db_model.User.count({"_id": {'$in': current_user.friends}, 'bloked_users': {'$ne': current_user._id}, 'friends': current_user._id}).exec (err, friends_count)->
        converted_users = []
        for user in users
          user_object = convert_user_to_friend(current_user, user)
          converted_users.push user_object   

        if err
          console.log err
          res.json
            status: "error"
            message: "can not find user friends: #{req.params.objectId}"
        else
          res.json
            status: "ok"
            result:
              friends: converted_users
              friends_count: friends_count
              pending_count: converted_users.length
              _temp_user: current_user

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
        db_model.User.findById(req.params.friendId).select(db_model.USER_PUBLIC_INFO).exec (err, friend)->
          if friend.push
            send_push 'user_' + req.params.friendId, 'sent you friend request', req.params.friendId, user.name, "friends", user.name + ' sent you friend request', req.params.objectId
          
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
        db_model.User.findById(req.params.friendId).select(db_model.USER_PUBLIC_INFO).exec (err, friend)->
          console.log friend
          if friend.push
            #send push
            send_push 'user_' + req.params.friendId, 'confirmed your friend request', req.params.friendId, user.name, "friends", user.name + ' confirmed your friend request', req.params.objectId

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

exports.friends_cencel_request = (req, res)->
  if req.params.objectId is req.params.friendId
    res.json
      status: "error"
      message: "users are the same"

  else
    db_model.User.findById(req.params.objectId).exec (err, user)->
      user.friends = __.filter user.friends, (user_friend)-> user_friend.toString() isnt req.params.friendId
      user.save ()->
        res.json
          status: "ok"
          result:
            message: "cencel friend request"
            _temp_user: user 

exports.block_user = (req, res)->
  if req.params.objectId is req.params.userId
    res.json
      status: "error"
      message: "users are the same"

  else
    db_model.User.findById(req.params.objectId).exec (err, user)->
      user.bloked_users.push req.params.userId
      db_model.save_or_update_user user, ()->
        res.json
          status: "ok"
          result:
            message: "block user"
            _temp_user: user

exports.unblock_user = (req, res)->
  if req.params.objectId is req.params.userId
    res.json
      status: "error"
      message: "users are the same"

  else
    db_model.User.findById(req.params.objectId).exec (err, user)->
      user.bloked_users = __.filter user.bloked_users, (user_bloked_user)-> user_bloked_user.toString() isnt req.params.userId
      db_model.save_or_update_user user, ()->
        res.json
          status: "ok"
          result:
            message: "unblock user"
            _temp_user: user

exports.invite_friend = (req, res)->
  db_model.User.findById(req.params.objectId).exec (err, current_user)->
    user = new db_model.User
              name: req.body.name
              email: req.body.email
              state:'invited'
              invited_by: current_user

    db_model.save_or_update_user user, (err)-> 
      if err
        res.json
          status: "error"
          err: err
      else
        res.json
          status: "ok"
          result:
            user: user

exports.invite_friend_fb = (req, res)->
  db_model.User.findById(req.params.objectId).exec (err, current_user)->
    fb_ids = req.body.fb_ids.split(",")
    async.each Object.keys(fb_ids), ((key, callback) ->
          fb_id = fb_ids[key].trim()

          db_model.User.findOne({"fb_id":fb_id}).exec (err, user)->
             if not user
               user = new db_model.User
                 fb_id: fb_id
       
               db_model.save_or_update_user user, (err)-> 
                 current_user.friends.push user
                 callback()
             else
               callback()

         ), (err) ->
            db_model.save_or_update_user current_user, (err)->
              res.json
                status: "ok"


exports.find_friends = (req, res)->
  emails = req.body.emails.split(",")
  db_model.User.find({'email': {'$in': emails}}).select(db_model.USER_PUBLIC_INFO).exec (err, users)->
    if err
      res.json
        status: "error"
        err: err
    else
      res.json
        status: "ok"
        result:
          users: users

exports.find_friends_fb = (req, res)->
  db_model.User.findById(req.params.me._id).exec (err, current_user)->
    fb_ids = req.body.fb_ids.split(",")
    db_model.User.find({'fb_id': {'$in': fb_ids}}, { checkin: {$slice: -1} }).populate('checkin.club').exec (err, users)-> 
      converted_users = []
      for user in users
        user_object = convert_user_to_friend(current_user, user)
        converted_users.push user_object   

      if err
        res.json
          status: "error"
          err: err
      else
        res.json
          status: "ok"
          result:
            users: converted_users

##################################################################################################

exports.get_config = (req, res)->
  res.json
    status: "ok"
    result:
      chekin_max_distance: config.chekin_max_distance
      max_failed_checkin_count: config.max_failed_checkin_count
      update_checkin_status_interval: config.update_checkin_status_interval

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
    lon: req.body.club_lon
    lat: req.body.club_lat

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

exports.club_users = (req, res)->
  current_user_id = req.params.me._id.toString()
  club_id = req.params.objectId
  db_model.Venue.findById(club_id).exec (err, club)->
    db_model.User.findById(current_user_id).exec (err, current_user)->
      db_model.User.find({'checkin': { '$elemMatch': { 'club' : club, 'active': true}}, 'bloked_users': {'$ne': current_user._id}}, { checkin: 0 }).exec (err, users)->
          user_objects = []
          for user in users
            user_object = convert_user_to_friend(current_user, user)
            user_objects.push user_object

          user_objects = __.sortBy user_objects , (o) ->
                  !o.is_friend
          res.json
            users: user_objects
            status: "ok"

exports.clubs_yesterday = (req, res)->
    current_user_id = req.params.me._id.toString()
    current_date_time = new Date()
    yesterday_data_time = new Date(new Date().getTime() - 48 * 60 * 60 * 1000);

    query =  [{'$match': {_id : mongoose.Types.ObjectId(current_user_id)}}, {'$unwind': '$checkin'}, {'$match': {'checkin.time': {'$gte': yesterday_data_time, '$lte': current_date_time}}}]

    db_model.User.aggregate query, {}, (err, result)->
      club_ids = []
      for r in result
        club_ids.push r.checkin.club

      db_model.Venue.find({'_id': { '$in': club_ids }}).exec (err, clubs)->
        # set today working hour for each club
        club_objects = []
        for club in clubs
          club_object = club.toObject()
          club_objects.push club_object
          if club_object.club_working_hours
            for wh in club.club_working_hours
              if wh.day == moment.utc().day()
                club_object.id = club._id
                club_object.club_today_working_hours = wh

        db_model.User.findById(req.params.me._id.toString()).exec (err, user)->
          match_pre =  {"_id": {'$in': user.friends}, 'bloked_users': {'$ne': user._id}, 'friends': user._id, 'checkin.active': true }
          match_post =  {'checkin.active': true}
          group = { _id: "$checkin.club", count: { $sum: 1 }}
          query = [ { '$match': match_pre}, { '$unwind': "$checkin" }, { '$match': match_post }, {'$group': group} ]

          db_model.User.aggregate query, {}, (err, checkins)->
            for c in checkins
              theclub = __.find(club_objects, (c_res)->
                      c_res._id.toString() == c._id.toString()
                )
              if theclub
                theclub.active_friends_checkins = c.count
            for club in club_objects
              is_favorite_club = __.find(user.favorite_clubs, (c_res)->
                      c_res.toString() == club._id.toString()
                )
              if is_favorite_club
                club.is_favorite = true
              else
                club.is_favorite = false
            res.json
              status: "ok"
              clubs: club_objects

exports.club_users_yesterday = (req, res)->
  current_user_id = req.params.me._id.toString()
  club_id = req.params.objectId
  db_model.Venue.findById(club_id).exec (err, club)->
    current_date_time = new Date()
    yesterday_data_time = new Date(new Date().getTime() - 48 * 60 * 60 * 1000);
    console.log yesterday_data_time
    db_model.User.findOne({'_id': mongoose.Types.ObjectId(current_user_id), 'checkin': { '$elemMatch': { 'club' : club, 'time': {'$gte': yesterday_data_time, '$lte': current_date_time}}}}).exec (err, current_user)->
      if current_user
        db_model.User.find({'checkin': { '$elemMatch': { 'club' : club, 'time': {'$gte': yesterday_data_time, '$lte': current_date_time}}}, 'bloked_users': {'$ne': current_user._id}}, { checkin: {$slice: -1} }).populate('checkin.club').exec (err, users)->
        
          user_objects = []
          for user in users
            user_object = convert_user_to_friend(current_user, user)
            user_objects.push user_object

          user_objects = __.sortBy user_objects , (o) ->
                  !o.is_friend
          res.json
            users: user_objects
            status: "ok"
      else
        res.json
          status: "ok"
          msg: 'yes_not_checked_in'

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

exports.club_types = (req, res)->
  console.log 'club types'
  query =  [{'$group':{_id: "$club_types", count: { '$sum': 1 } } }]
  console.log query
  #query = { 'club_loc':{ '$near' : [ params.lat,params.lon] }}
  db_model.Venue.aggregate query, {}, (err, result)->
    console.log err
    console.log  result
    res.json
      status: 'ok'
      result: result

exports.list_events = (req, res)->
  skip = 0
  if req.query.skip
    skip = parseInt(req.query.skip)
  take = 300
  if req.query.take
    take = parseInt(req.query.take)

  params =
    lat: req.query.user_lat
    lon: req.query.user_lon
    user_id: req.params.me._id.toString()
    search: req.query.search

  if req.query.distance
    params.distance = parseInt(req.query.distance)
  else  
    params.distance = 20

  params.skip = skip
  params.take = take

  manager.list_events params, (err, news)->
    if err
      res.json
        status: 'error'
        error: err
    else
      res.json
        status: 'ok'
        news: news

exports.list_dj_events = (req, res)->
  skip = 0
  if req.query.skip
    skip = parseInt(req.query.skip)
  take = 300
  if req.query.take
    take = parseInt(req.query.take)

  params =
    lat: req.query.user_lat
    lon: req.query.user_lon
    user_id: req.params.me._id.toString()
    search: req.query.search

  if req.query.distance
    params.distance = parseInt(req.query.distance)
  else  
    params.distance = 20

  params.skip = skip
  params.take = take

  manager.list_dj_events params, (err, news)->
    if err
      res.json
        status: 'error'
        error: err
    else
      res.json
        status: 'ok'
        news: news


exports.list_club = (req, res)->
  console.log req.params

  skip = 0
  if req.query.skip
    skip = parseInt(req.query.skip)
  
  take = 300
  if req.query.take
    take = parseInt(req.query.take)

  params =
    #distance: req.query.distance
    lat: req.query.user_lat
    lon: req.query.user_lon
    user_id: req.params.me._id.toString()
    type: req.query.type
    search: req.query.search

  
  if req.query.distance
    params.distance = parseInt(req.query.distance)

  params.skip = skip
  params.take = take
  
  console.log params
  manager.list_club params, (err, clubs, types)->
    if err
      res.json
        status: 'error'
        error: err
    else
      # get people who checkin in these clubs
      res.json
        status: 'ok'
        clubs: clubs
        types: types

exports.list_venue = (req, res)->
  skip = 0
  if req.query.skip
    skip = parseInt(req.query.skip)
  take = 10
  if req.query.take
    take = parseInt(req.query.take)
  params =
    type_venue: req.params.type.toString()
    lat: req.query.user_lat
    lon: req.query.user_lon
    user_id: req.params.me._id.toString()
    search: req.query.search
  if req.query.distance
    params.distance = parseInt(req.query.distance)
  else  
    params.distance = 20
  params.skip = skip
  params.take = take
  manager.list_venue params, (err, clubs)->
    if err
      res.json
        status: 'error'
        error: err
    else
      res.json
        status: 'ok'
        clubs: clubs

exports.get_all_lists = (req, res)->
  skip = 0
  if req.query.skip
    skip = parseInt(req.query.skip)
  take = 10
  if req.query.take
    take = parseInt(req.query.take)
  params =
    lat: req.query.user_lat
    lon: req.query.user_lon
    user_id: req.params.me._id.toString()
    search: req.query.search
  if req.query.distance
    params.distance = parseInt(req.query.distance)
  else  
    params.distance = 20
  params.skip = skip
  params.take = take
  params.type_venue = "club"
  manager.list_venue params, (err, clubs)->
    if err
      res.json
        status: 'error'
        error: err
    else
      params.type_venue = "bar"
      manager.list_venue params, (err, bars)->
        if err
          res.json
            status: 'error'
            error: err
        else
          params.type_venue = "festival"
          manager.list_venue params, (err, festivals)->
            if err
              res.json
                status: 'error'
                error: err
            else
              manager.list_events params, (err, dj_events)->
                if err
                  res.json
                    status: 'error'
                    error: err
                else
                  manager.list_dj_events params, (err, events)->
                    if err
                      res.json
                        status: 'error'
                        error: err
                    else
                      res.json
                        status: 'ok'
                        clubs: clubs
                        bars: bars
                        festivals: festivals
                        dj_events: dj_events
                        events: events

exports.add_favorite_club = (req, res)->
  params = 
    user_id: req.params.me._id.toString()
    club_id: req.params.objectId

  manager.add_favorite_club params, (err, result)->
    if err
      console.log err
      res.json
        status: 'error'
        err: err
    else
      res.json
        status: 'ok'
        result: result

exports.remove_favorite_club = (req, res)->
  params = 
    user_id: req.params.me._id.toString()
    club_id: req.params.objectId

  manager.remove_favorite_club params, (err, result)->
    if err
      console.log err
      res.json
        status: 'error'
        err: err
    else
      res.json
        status: 'ok'
        result: result


exports.news = (req, res)->
  params =
    club_id: req.params.objectId
    skip: req.query.skip
    limit: req.query.limit
    
  manager.news params, (err, news)->
    if err
      res.json
        status: 'error'
        error: err
    else
      res.json
        status: 'ok'
        news: news

exports.events = (req, res)->
  params =
    club_id: req.params.objectId
    skip: req.query.skip
    limit: req.query.limit
    
  manager.events params, (err, events)->
    if err
      res.json
        status: 'error'
        error: err
    else
      res.json
        status: 'ok'
        events: events

exports.news_favorite = (req, res)->
  params =
    user_id: req.params.me._id.toString()
    skip: req.query.skip
    limit: req.query.limit

  manager.news_favorite params, (err, news)->
    if err
      res.json
        status: 'error'
        error: err
    else
      res.json
        status: 'ok'
        news: news

exports.events_favorite = (req, res)->
  params =
    user_id: req.params.me._id.toString()
    skip: req.query.skip
    limit: req.query.limit

  manager.events_favorite params, (err, events)->
    if err
      res.json
        status: 'error'
        error: err
    else
      res.json
        status: 'ok'
        events: events

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

      # send message to pubnub
      pubnub = require("pubnub").init({ publish_key: config.pub_publish_key, subscribe_key: config.pub_subscribe_key})
      pubnab_data =
        data:
          club: req.params.objectId
        type: "checkin"
      
      console.log  "checkin"
      pubnub.publish
        channel: "checkin"#"checkin_" + req.params.objectId
        message: pubnab_data
        callback: (e) ->
          console.log "SUCCESS, PubNub message sent!", e

        error: (e) ->
          console.log "FAILED! PubNub can not send message", e

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

    # send message to pubnub
    pubnub = require("pubnub").init({ publish_key: config.pub_publish_key, subscribe_key: config.pub_subscribe_key})
    pubnab_data =
      data:
        club: req.params.objectId
      type: "checkout"
    
    console.log  "checkin_" + req.params.objectId
    pubnub.publish
      channel: "checkin"#"checkin_" + req.params.objectId
      message: pubnab_data
      callback: (e) ->
        console.log "SUCCESS, PubNub message sent!", e

      error: (e) ->
        console.log "FAILED! PubNub can not send message", e

    res.json
      status: 'ok'
      user: user

exports.user_push = (req, res)->
  console.log 'user_' + req.params.user_id
  params =
    user_from: '543dabbe29f5a9020000000d'
    user_to: req.params.user_id
    msg: req.body.message
    msg_type: 'message'

  manager.get_user_by_id params.user_from, (err, user_from)->
    manager.chat params, (err, chat)->
      message = params.msg

      # check if we have push
      if chat.user2.push
        send_push 'user_' + params.user_to, message, params.user_from + "_" + params.user_to, user_from.name, "chat", user_from.name + ': "' + message + '"', params.user_from

      # send message to pubnub
      pubnub = require("pubnub").init({ publish_key: config.pub_publish_key, subscribe_key: config.pub_subscribe_key})
      conversation = prepare_chat_messages(chat, params.user_to)[0]
      pubnab_data =
        data:
          user_from: params.user_from
          user_to: params.user_to
          last_message: conversation[conversation.length - 1]
        type: "chat"

      pubnub.publish
        channel: "message_" + params.user_to
        message: pubnab_data
        callback: (e) ->
          console.log "SUCCESS, PubNub message sent!", e

        error: (e) ->
          console.log "FAILED! PubNub can not send message", e

      res.json
        status: 'ok'
        chat: chat

  #send_push 'user_' + req.params.user_id, req.body.message, 'header', req.body.message   
  #res.json
  #  status: 'ok'

exports.chat = (req, res)->
  # fix empty message type
  if not req.body.msg_type
    req.body.msg_type = "message"
  loc =
    lon: req.body.lon
    lat: req.body.lat

  params =
    user_from: req.body.user_from
    user_to: req.body.user_to
    msg: req.body.msg
    url: req.body.url
    msg_type: req.body.msg_type
    location: loc

  manager.get_user_by_id req.body.user_from, (err, user_from)->
    manager.chat params, (err, chat)->
      message = req.body.msg

      # check if we have push
      if chat.user2.push
        send_push 'user_' + req.body.user_to, message, req.body.user_from + "_" + req.body.user_to, user_from.name, "chat", user_from.name + ': "' + message + '"', req.body.user_from

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
      url: conversation.url
      location: conversation.location
      from_who: conversation.from_who
      read: conversation.read
      from_who_name: if conversation.from_who.toString() is current_user._id.toString() then current_user.name else receiver.name
      from_who_avatar: if conversation.from_who.toString() is current_user._id.toString() then current_user.avatar else receiver.avatar
      is_my_message: current_user._id.toString() is conversation.from_who.toString()

  return [messages, current_user, receiver]

exports.get_conversations = (req, res)->
  #params =
  #  user_id: req.params.current_user

  db_model.User.findById(req.params.current_user).exec (err, current_user)->  
    db_model.User.find({'bloked_users': current_user}).exec (err, bloked_users)->
      db_model.Chat.find({'$or':[{'user1': mongoose.Types.ObjectId(req.params.current_user), 'user2': {'$nin': bloked_users}}, {'user2': mongoose.Types.ObjectId(req.params.current_user), 'user1': {'$nin': bloked_users}}]}, { 'conversation': { '$slice': -1 }}).populate("user1",'-checkin').populate("user2",'-checkin').exec (err, chats)->
        if not chats
          res.json
            status: 'ok'
            result:
              chats: []
        else
          sorted_chats = __.sortBy(chats, (chat) ->
            if chat.unread.user && chat.unread.user.toString() == req.params.current_user.toString()
               return chat.unread.count
            else
              return 0
          ).reverse()

          #callback err, sorted_chats

          result = []
          for chat in sorted_chats
            if chat.unread.user && chat.unread.user.toString() == req.params.current_user.toString()
              unread_messages = chat.unread.count
            else
              unread_messages = 0

            chat_dto = prepare_chat_messages(chat, req.params.current_user)
            result.push
              chat_id: chat._id
              updated_on: chat.updated_on
              unread_messages: unread_messages
              conversation: chat_dto[0]
              current_user: convert_user_to_friend(current_user, chat_dto[1])
              receiver: convert_user_to_friend(current_user, chat_dto[2])

          # sort by recived date
          result = __.sortBy result, (chat)-> chat.updated_on
          result = result.reverse()
          res.json
            status: 'ok'
            result:
              chats: result

exports.delete_conversation = (req, res)->
  params =
    user1: req.params.current_user
    user2: req.params.receiver

  query = { '$or': [{ 'user1': mongoose.Types.ObjectId(params.user1), 'user2': mongoose.Types.ObjectId(params.user2) }, { 'user1': mongoose.Types.ObjectId(params.user2), 'user2': mongoose.Types.ObjectId(params.user1) }] }

  db_model.Chat.remove query, (err)->
    res.json
      status: 'ok'

exports.get_conversation = (req, res)->
  params =
    user1: req.params.current_user
    user2: req.params.receiver

  query = { '$or': [{ 'user1': mongoose.Types.ObjectId(params.user1), 'user2': mongoose.Types.ObjectId(params.user2) }, { 'user1': mongoose.Types.ObjectId(params.user2), 'user2': mongoose.Types.ObjectId(params.user1) }] }
  db_model.User.findById(req.params.current_user).exec (err, current_user)->  
    db_model.Chat.findOne(query).populate("user1", '-checkin').populate("user2", '-checkin').exec (err, chat)->
      if not chat
        db_model.User.find({'$or':[{"_id": params.user1}, {"_id": params.user2}]}).select('-checkin').exec (err, users)->
          chat =
            user1: users[0]
            user2: users[1]
            unread: {}
            updated_on: new Date()
            conversation: []
          chat_dto = prepare_chat_messages chat, req.params.current_user
          res.json
            status: 'ok'
            result:
              chat_id: chat._id
              conversation: chat_dto[0]
              current_user: convert_user_to_friend(current_user, chat_dto[1])
              receiver: convert_user_to_friend(current_user, chat_dto[2])
      else
        chat_dto = prepare_chat_messages chat, req.params.current_user

        res.json
          status: 'ok'
          result:
            chat_id: chat._id
            conversation: chat_dto[0]
            current_user: convert_user_to_friend(current_user, chat_dto[1])
            receiver: convert_user_to_friend(current_user, chat_dto[2])

exports.readchat = (req, res)->
  params =
    current_user: req.params.current_user
    receiver: req.params.receiver

  manager.readchat params, (err, readchat)->
    res.json
      status: 'ok'

exports.unread_notifications_count = (req, res)->
  manager.unread_messages_count req.query, req.params.me._id.toString(), (err, unread_chat_count, pending_friends_count, venue_count)->
    res.json
      status: 'ok'
      unread_chat_count: unread_chat_count
      pending_friends_count: pending_friends_count
      venue_count: venue_count

exports.remove_user = (req, res)->
  console.log "remove user", req.params.user_id
  db_model.User.remove {"_id": req.params.user_id}, (err)->
    db_model.Chat.remove {"user1": mongoose.Types.ObjectId(req.params.user_id)}, (err)->
      db_model.Chat.remove {"user2": mongoose.Types.ObjectId(req.params.user_id)}, (err)->
        res.json
          status: 'ok'

exports.checkin_clean = (req, res)->
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

# helpers

convert_user_to_friend = (current_user, user)->
  user_object = user.toObject();

  return user_to_friend current_user, user_object

user_to_friend = (current_user, user)->
  is_current_user_friend_to_user = __.find(user.friends, (f) ->
          f.toString() is current_user._id.toString()
        )

  is_user_friend_to_current_user = __.find(current_user.friends, (f) ->
          f.toString() is user._id.toString()
        )

  user.is_friend = false
  
  friend_status = 'none'
  if is_current_user_friend_to_user and not is_user_friend_to_current_user
    friend_status = "receive_request"
  if is_user_friend_to_current_user and not is_current_user_friend_to_user
    friend_status = "sent_request"
  if is_user_friend_to_current_user and is_current_user_friend_to_user
    friend_status = "friend"
    user.is_friend = true

  is_blocked = __.find(current_user.bloked_users, (u) ->
          u.toString() is user._id.toString()
        )

  user.friend_status = friend_status;

  if user.photos and user.photos.length > 0
    for _photo in user.photos
      if _photo.profile
        user.avatar = _photo

  if user.dob
    user.age = Math.floor((new Date() - user.dob) / 31536000000)

  if is_blocked
    user.is_blocked = true
  else
    user.is_blocked = false 

  return user


send_push = (channel, msg, unique_id, header, type, alert, from_user_id)->
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
        action: "com.nl.clubbook.UPDATE_STATUS"
        msg: msg
        unique_id: unique_id
        header: header
        type: type
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
        type: type
        from_user_id: from_user_id
        sound: "nothing"