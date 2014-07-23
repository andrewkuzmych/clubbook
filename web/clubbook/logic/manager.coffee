mongoose = require('mongoose')
mongoose.connect(config.db.connection)
db_model = require('./model')
async = require("async")
__ = require("underscore")
async = require("async")

CHECKIN_LIVE_TIME =  1000 * 60 * 30;

exports.get_user_by_id = (user_id, callback)->
  db_model.User.findById(user_id).exec (err, user)->
    if err
      callback err, null
    else
      callback null, user

exports.get_user_friends = (user_id, callback)->
  db_model.User.find({"_id": {"$ne": mongoose.Types.ObjectId(user_id)}}).select(db_model.USER_PUBLIC_INFO).sort("name").exec callback

exports.signinmail = (params, callback)->

  if __.isEmpty params.email?.trim()
      callback 'email is empty', null
  else if __.isEmpty params.password?.trim()
      callback 'password is empty', null
  else
    db_model.User.findOne({"email":params.email,"password":params.password },{ checkin: 0 }).exec (err, user)->

      if user
        callback null, user
      else
        callback "Wrong User or password " ,user

exports.list_club = (params, callback)->
  db_model.Venue.find({ 'club_loc':{ '$near' : [ params.lat,params.lon], '$maxDistance' :  exports.radius_to_km(params.distance) }}).exec (err, clubs)->
    callback err, clubs

exports.get_people_count_in_club = (club, callback)->
  db_model.User.count({'checkin': { '$elemMatch': { 'club' : club, 'active': true}}}).exec callback

exports.find_club = (club_id, user_id, callback)->
  db_model.Venue.findById(club_id).exec (err, club)->
    if err
      callback err, null
    else
      db_model.User.find({'checkin': { '$elemMatch': { 'club' : club, 'active': true}}, '_id': {'$ne':  mongoose.Types.ObjectId(user_id)}}, { checkin: 0 }).exec (err, users)->
        callback null, club, users


exports.create_club = (params, callback)->
  if __.isEmpty params.club_name?.trim()
      callback 'club name is empty', null
  else if __.isEmpty params.club_houres?.trim()
      callback 'club opening houres is empty', null
  else if __.isEmpty params.club_phone?.trim()
      callback 'club phone is empty', null
  else if __.isEmpty params.club_address?.trim()
      callback 'club address is empty', null
  else if __.isEmpty params.club_site?.trim()
      callback 'club site is empty', null

  else
    club = new db_model.Venue
      club_admin: params.club_admin
      club_name: params.club_name
      club_email: params.club_email
      club_houres: params.club_houres
      club_photos: params.club_photos
      club_phone: params.club_phone
      club_address: params.club_address
      club_site: params.club_site
      club_info: params.club_info
      club_loc: params.club_loc
      club_logo: params.club_logo

    club.save (err)->
        console.log err
        callback err, club

exports.cu_count = (params, callback)->
  query = { 'club_loc':{ '$near' : [ params.lat,params.lon], '$maxDistance' :  exports.radius_to_km(params.distance) }}
  db_model.Venue.count(query).exec (err, club_count)->
    callback err, club_count

exports.club_clubbers = (params, callback)->
  # do not return "checkin " field of a user
  db_model.User.find({'checkin': { '$elemMatch': { 'club' : mongoose.Types.ObjectId(params.club_id), 'active': true}} }, { checkin: 0 }).exec (err, users)->
    callback err, users

exports.update_name = (params, callback)->
  db_model.User.findById(params.user_id).exec (err, user)->

    user.name = params.name

    user.save (err)->
        console.log err
        callback err, user

exports.update_dob = (params, callback)->
  db_model.User.findById(params.user_id).exec (err, user)->

    user.dob = params.dob

    user.save (err)->
        console.log err
        callback err, user

exports.update_gender = (params, callback)->
  db_model.User.findById(params.user_id).exec (err, user)->

    user.gender = params.gender

    user.save (err)->
        console.log err
        callback err, user

exports.update_info = (params, callback)->
  db_model.User.findById(params.user_id).exec (err, user)->

    user.info = params.info

    user.save (err)->
        console.log err
        callback err, user

##################################################################################################################
# Checkin, Checkout logic
##################################################################################################################

exports.checkin = (params, callback)->
  console.log "Checkin user", params
  db_model.User.count({'_id': params.user_id, checkin: { '$elemMatch': { 'active': true, 'club': params.club_id }} }).exec (err, count_of_active_checkins)->
    if count_of_active_checkins > 0
      console.log "user is already checkedin in this club", params
      # return user
      db_model.User.findById(params.user_id).exec callback

    else
      db_model.Venue.findById(params.club_id).exec (err, club)->
        if not club
          callback 'club does not exist', null
        else
          db_model.User.findById(params.user_id).exec (err, user)->
            if not user
              callback 'user does not exist', null
            else
              # checkout user from all clubs
              exports.checkout_from_all_clubs user, ()->
                club.active_checkins += 1
                club.save ()->
                  user.checkin.push { club: club, lat: club.club_loc.lat, lon: club.club_loc.lon, time: Date.now(), active: true }
                  user.save (err)->
                    callback err, user

exports.update_checkin = (params, callback)->
  console.log "Upadete checkin state", params
  db_model.Venue.findById(params.club_id).exec (err, club)->
    if not club
      callback 'club does not exist', null
    else
      db_model.User.findById(params.user_id).exec (err, user)->
        if not user
          callback 'user does not exist', null
        else
          for oldcheckin in user.checkin
            if oldcheckin.club.toString() == club._id.toString() && oldcheckin.active == true
              oldcheckin.time = Date.now()
              break;

          user.save (err)->
            callback err, user

exports.checkout_from_all_clubs = (user, callback)->
  async.each user.checkin, (checkin, next_checkin) ->
    if checkin.active
      exports.checkout {user_id: user._id, club_id: checkin.club}, ()->
        next_checkin()
    else
      next_checkin()
  , (err) ->
    console.log "checkout for User is done"
    callback()

# analyze user checkins and if is older them 30 min make checkout
exports.update_checkout_states = (user, callback)->
  async.each user.checkin, (checkin, next_checkin) ->
    if checkin.active and checkin.time < new Date().getTime() - CHECKIN_LIVE_TIME
      exports.checkout {user_id: user._id, club_id: checkin.club}, ()->
        next_checkin()
    else
      next_checkin()
  , (err) ->
    console.log "checkout for User is done"
    callback()


exports.checkout = (params, callback)->
  console.log "checkout user", params
  db_model.Venue.findById(params.club_id).exec (err, club)->
    if not club
      console.log "club does not exist"
      callback 'club does not exist', null
    else
      # decrement count of active chekins
      if club.active_checkins > 0 then club.active_checkins -= 1
      club.save ()->
        db_model.User.findById(params.user_id).exec (err, user)->
          if not user
            console.log "user does not exist"
            callback 'user does not exist', null
          else
            for oldcheckin in user.checkin
              if oldcheckin.club.toString() == club._id.toString()
                oldcheckin.active = false
            user.save (err)->
              console.log "done checkout"
              callback err, user

exports.cron_checkout = ()->
  db_model.User.find({'checkin': { '$elemMatch': { 'active': true, 'time': {'$lte': new Date().getTime() - CHECKIN_LIVE_TIME } }} }).exec (err, users)->
    console.log "checkout ", users.length
    async.eachSeries users, (user, callback) ->
      exports.update_checkout_states user, ()->
        callback()
    , (err) ->
      console.log "checkout done"

#-----------------------------------------------------------------------------------------------------------------------

exports.save_user = (params, callback)->
  db_model.User.findOne({"email":params.email}).exec (err, user)->
    if user
      console.log 'user exists', params.email
      callback 'user exists', null
    else if __.isEmpty params.name?.trim()
      callback 'name is empty', null
    else if __.isEmpty params.email?.trim()
      callback 'email is empty', null
    else if __.isEmpty params.password?.trim()
      callback 'password is empty', null
    else
      user = new db_model.User
            gender: params.gender
            name: params.name
            email: params.email
            password: params.password
            city: params.city
            country: params.country

      if params.dob
        user.dob = params.dob
      if params.avatar
        user.photos.push {public_id: params.avatar.public_id, url: params.avatar.url, profile: true}

      user.save (err)->
        console.log "save user", err, user
        callback err, user

exports.uploadphoto = (params, callback)->
  if __.isEmpty params.userid?.trim()
      callback 'no user id', null
  else if __.isEmpty params.photos?.trim()
      callback 'no photo url provided', null

  db_model.User.findOne({_id: params.userid}).exec (err, user)->
    if not user
      callback 'user does not exists', null
    else
      photosArray = params.photos.split(";")
      for photo in photosArray
        user.photos.push { url: photo, profile:false}

      user.save (err)->
        console.log err
        callback err, user

exports.save_or_update_fb_user = (params, callback)->
    console.log "save or update user", params

    db_model.User.findOne({"fb_id":params.fb_id}).exec (err, user)->
      if user
        console.log "fb_login:", "update user"
        # update user
        if params.email then user.email = params.email
        user.gender = params.gender
        user.name = params.name
        user.fb_id = params.fb_id
        user.fb_access_token = params.fb_access_token
        if params.dob then user.dob = params.dob
        if params.city then user.city = params.city

        if user.photos.length == 0
          user.photos.push {public_id: params.avatar.public_id, url: params.avatar.url, profile: true}

        user.save (err)->
          callback err, user

      else
        # new user
        if __.isEmpty params.email?.trim()
          console.log "fb_login:", "create user"
          # user didn't provide email
          user = new db_model.User
            gender: params.gender
            name: params.name
            fb_id: params.fb_id
            fb_access_token: params.fb_access_token
          if params.dob then user.dob = params.dob
          if params.city then user.city = params.city

          user.photos.push {public_id: params.avatar.public_id, url: params.avatar.url, profile: true}

          # SEND WELCOME MAIL
          #email_sender.welcome user, (err, info)->
          #  console.log 'welcome mail sent', user._id

          user.save (err)->
            callback err, user

        else
          db_model.User.findOne({"email":params.email?.toLowerCase()}).exec (err, user)->
            if user
              console.log "fb_login:", "update user"
              # merge data
              user.gender = params.gender
              user.name = params.name
              user.fb_id = params.fb_id
              user.fb_access_token = params.fb_access_token

              if params.dob then user.dob = params.dob
              if params.city then user.city = params.city

              if user.photos.length == 0
                user.photos.push {public_id: params.avatar.public_id, url: params.avatar.url, profile: true}

              user.save (err)->
                callback err, user

            else
              console.log "fb_login:", "create user"
              user = new db_model.User
                email: params.email
                gender: params.gender
                name: params.name
                fb_id: params.fb_id
                fb_access_token: params.fb_access_token
              if params.dob then user.dob = params.dob
              if params.city then user.city = params.city

              user.photos.push {public_id: params.avatar.public_id, url: params.avatar.url, profile: true}

              # SEND WELCOME MAIL
              #email_sender.welcome user, (err, info)->
              #  console.log 'welcome mail sent', user._id

              user.save (err)->
                callback err, user

exports.chat = (params, callback)->
  query = { '$or': [{ 'user1': mongoose.Types.ObjectId(params.user_from), 'user2': mongoose.Types.ObjectId(params.user_to) },
    { 'user1': mongoose.Types.ObjectId(params.user_to), 'user2': mongoose.Types.ObjectId(params.user_from) }] }

  db_model.Chat.findOne(query).populate("user1", db_model.USER_PUBLIC_INFO).populate("user2", db_model.USER_PUBLIC_INFO).exec (err, chat)->
    if not chat
      chat = new db_model.Chat
        user1: mongoose.Types.ObjectId(params.user_from)
        user2: mongoose.Types.ObjectId(params.user_to)

    chat.conversation.push {msg: params.msg, from_who: mongoose.Types.ObjectId(params.user_from), type: params.msg_type}

    if chat.unread.user && chat.unread.user.toString() == params.user_to.toString()
      chat.unread.count += 1
    else
      chat.unread.user = mongoose.Types.ObjectId(params.user_to)
      chat.unread.count = 1

    chat.save (err)->
      # retreive chat with user data
      db_model.Chat.findById(chat._id).populate("user1", db_model.USER_PUBLIC_INFO).populate("user2", db_model.USER_PUBLIC_INFO).exec callback


exports.get_conversation = (params, callback)->
  query = { '$or': [{ 'user1': mongoose.Types.ObjectId(params.user1), 'user2': mongoose.Types.ObjectId(params.user2) }, { 'user1': mongoose.Types.ObjectId(params.user2), 'user2': mongoose.Types.ObjectId(params.user1) }] }

  db_model.Chat.findOne(query).populate("user1", db_model.USER_PUBLIC_INFO).populate("user2", db_model.USER_PUBLIC_INFO).exec (err, chat)->
    if not chat
      db_model.User.find({'$or':[{"_id": params.user1}, {"_id": params.user2}]}).select(db_model.USER_PUBLIC_INFO).exec (err, users)->
        chat_result =
          user1: users[0]
          user2: users[1]
          unread: {}
          updated_on: new Date()
          conversation: []
        callback err, chat_result
    else
      callback err, chat

exports.get_conversations = (params, callback)->
  db_model.Chat.find({'$or':[{'user1': mongoose.Types.ObjectId(params.user_id)}, {'user2': mongoose.Types.ObjectId(params.user_id)}]}, { 'conversation': { '$slice': -1 } }).populate("user1", db_model.USER_PUBLIC_INFO).populate("user2", db_model.USER_PUBLIC_INFO).exec (err, chats)->
    if not chats
      callback err, []
    else
      sorted_chats = __.sortBy(chats, (chat) ->
        if chat.unread.user && chat.unread.user.toString() == params.user_id.toString()
           return chat.unread.count
          else
            return 0
      ).reverse()

      callback err, sorted_chats

exports.readchat = (params, callback)->
  query = { '$or': [{ 'user1': mongoose.Types.ObjectId(params.current_user), 'user2': mongoose.Types.ObjectId(params.receiver) }, { 'user1': mongoose.Types.ObjectId(params.receiver), 'user2': mongoose.Types.ObjectId(params.current_user) }] }
  
  db_model.Chat.findOne(query).exec (err, chat)->
    if chat.unread.user && chat.unread.user.toString() == params.current_user.toString()
      chat.unread.count = 0
 
    chat.save (err)->
      callback err, chat


exports.unread_messages_count = (user_id, callback)->
  db_model.Chat.find({'unread.user':  mongoose.Types.ObjectId(user_id)}, {'conversation':0}).exec (err, chats)->
    count = 0
    for chat in chats
      count = count + chat.unread.count
    callback null, count

# convert the radius value to km
exports.radius_to_km = (distance)->
  # http://stackoverflow.com/questions/7837731/units-to-use-for-maxdistance-and-mongodb
  #one degree is approximately 111.12 kilometers
  return distance/111.12









