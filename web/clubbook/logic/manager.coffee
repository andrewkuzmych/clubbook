mongoose = require('mongoose')
mongoose.connect(config.db.connection)
db_model = require('./model')
async = require("async")
__ = require("underscore")
async = require("async")
moment = require('moment-timezone')

CHECKIN_LIVE_TIME =  1000 * 60 * 360;

exports.get_user_by_id = (user_id, callback)->
  console.log "METHOD - Manager get_user_by_id"
  db_model.User.findById(user_id).exec (err, user)->
    if err
      callback err, null
    else
      callback null, user

exports.findUserByEmail = (email, callback)->
  console.log "METHOD - Manager findUserByEmail"
  query = {"$and": [{"email": email},{"email": {"$exists": true}},{"password": {"$exists": true}}]}

  db_model.User.findOne(query).exec (err, user)->
    console.log user
    callback err, user

exports.get_friend = (friend_id, current_user_id, callback)->
  console.log "METHOD - Manager get_friend"
  db_model.User.findById(friend_id).select('-checkin').exec (err, user)->
    db_model.User.findById(current_user_id).select('-checkin').exec (err, current_user)->
      is_current_user_friend_to_user = __.find(user.friends, (f) ->
              f.toString() is current_user._id.toString()
            )

      is_user_friend_to_current_user = __.find(current_user.friends, (f) ->
              f.toString() is user._id.toString()
            )

      friend_status = 'none'
      if is_current_user_friend_to_user and not is_user_friend_to_current_user
        friend_status = "receive_request"
      if is_user_friend_to_current_user and not is_current_user_friend_to_user
        friend_status = "sent_request"
      if is_user_friend_to_current_user and is_current_user_friend_to_user
        friend_status = "friend"

      is_blocked = __.find(current_user.bloked_users, (u) ->
              u.toString() is user._id.toString()
            )

      user_object = user.toObject();
      user_object.friend_status = friend_status;

      if is_blocked
        user_object.is_blocked = true
      else
        user_object.is_blocked = false


      if err
        callback err, null
      else
        callback null, user_object

exports.get_user_friends = (user_id, callback)->
  console.log "METHOD - Manager get_user_friends"
  db_model.User.find({"_id": {"$ne": mongoose.Types.ObjectId(user_id)}}).select(db_model.USER_PUBLIC_INFO).sort("name").exec callback

exports.signinmail = (params, callback)->
  console.log "METHOD - Manager signinmail"
  if __.isEmpty params.email?.trim()
      callback 'email is empty', null
  else if __.isEmpty params.password?.trim()
      callback 'password is empty', null
  else
    db_model.User.findOne({"email":params.email,"password":params.password },{ checkin: 0 }).exec (err, user)->
      if user
        db_model.save_or_update_user user, (err)-> callback err, user
      else
        callback "Wrong User or password " ,user

exports.list_club = (params, callback)->
  console.log "METHOD - Manager list_club"
  geoNear = 
      near: [parseFloat(params.lon), parseFloat(params.lat)],
      distanceField: "distance",
      spherical: true,
      distanceMultiplier: 6371  

  if params.distance
    geoNear.maxDistance = params.distance/6371 
  query =  [{'$geoNear': geoNear}, {'$skip':params.skip}, {'$limit':params.take}]
  match = {}
  if params.search
    search = params.search.replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1")
    match.club_name =  { '$regex': search, '$options': 'i' }
  if params.type
    match.club_type = params.type

  if match
    query = [{'$geoNear': geoNear}, {'$match': match}, {'$skip':params.skip}, {'$limit':params.take}]
  db_model.Venue.aggregate query,{}, (err, clubs)->
    for club in clubs
      if club.club_working_hours
        for wh in club.club_working_hours
          if wh.day == moment.utc().day()
            club.id = club._id
            club.club_today_working_hours = wh
    db_model.User.findById(params.user_id).exec (err, user)->
      match_pre =  {"_id": {'$in': user.friends}, 'bloked_users': {'$ne': user._id}, 'friends': user._id, 'checkin.active': true }
      match_post =  {'checkin.active': true}
      group = { _id: "$checkin.club", count: { $sum: 1 }}
      query = [ { '$match': match_pre}, { '$unwind': "$checkin" }, { '$match': match_post }, {'$group': group} ]

      db_model.User.aggregate query, {}, (err, result)->
        for c in result
          theclub = __.find(clubs, (c_res)->
                  c_res._id.toString() == c._id.toString()
            )
          if theclub
            theclub.active_friends_checkins = c.count
        for club in clubs
          is_favorite_club = __.find(user.favorite_clubs, (c_res)->
                  c_res.toString() == club._id.toString()
            )
          if is_favorite_club
            club.is_favorite = true
          else
            club.is_favorite = false
        query =  [{'$group':{_id: "$club_types", count: { '$sum': 1 } } }]
        db_model.Venue.aggregate query, {}, (err, types)->
          callback err, clubs, types 

exports.list_events = (params, callback)->
  console.log "METHOD - Manager list_events"
  db_model.Events.count().exec (err, count)->
    geoNear = 
        near: [parseFloat(params.lon), parseFloat(params.lat)],
        distanceField: "distance",
        spherical: true,
        limit: count,
        distanceMultiplier: 6371  
    if params.distance
      geoNear.maxDistance = params.distance/6371 
    query =  [{'$geoNear': geoNear}, {'$skip':params.skip}, {'$limit':params.take}]
    db_model.Events.aggregate query,{}, (err, events)->
      format_date_events events, (events_updated)->
        dj_ids = []
        venue_ids = []
        for the_event in events_updated
          if the_event.dj
            dj_ids.push the_event.dj
          else
            if the_event.club
              venue_ids.push the_event.club
            else
              venue_ids.push the_event.festival
        db_model.Dj.find({"_id": {'$in': dj_ids}}).exec (err, djs)->
          db_model.Venue.find({"_id": {'$in': venue_ids}}).exec (err, venues)->
            for the_event in events_updated
              if the_event.festival
                venue = __.find(venues, (c_res)->
                  c_res._id.toString() == the_event.festival.toString())
                the_event.festival = venue
              if the_event.club
                venue = __.find(venues, (c_res)->
                  c_res._id.toString() == the_event.club.toString())
                the_event.club = venue
              if the_event.dj
                dj = __.find(djs, (c_res)->
                  c_res._id.toString() == the_event.dj.toString())
                the_event.dj = dj
            callback err, events_updated

exports.list_dj_events = (params, callback)->
  console.log "METHOD - Manager list_events"
  db_model.Events.count({"dj":{'$exists': true}}).exec (err, count)->
    geoNear = 
        near: [parseFloat(params.lon), parseFloat(params.lat)],
        distanceField: "distance",
        spherical: true,
        limit: count,
        distanceMultiplier: 6371  
        query: {"dj":{'$exists': true}}
    if params.distance
      geoNear.maxDistance = params.distance/6371 
    query =  [{'$geoNear': geoNear}, {'$skip':params.skip}, {'$limit':params.take}]
    db_model.Events.aggregate query,{}, (err, events)->
      format_date_events events, (events_updated)->
        events_ids = []
        for e in events_updated
          events_ids.push e.dj
        db_model.Dj.find({"_id": {'$in': events_ids}}).exec (err, djs)->
          for the_event in events_updated
            the_dj = __.find(djs, (c_res)->
                    c_res._id.toString() == the_event.dj.toString()
              )
            the_event.dj = the_dj
          callback err, events_updated

exports.list_venue = (params, callback)->
  console.log "METHOD - Manager list_club"
  db_model.Venue.count().exec (err, count)->
    geoNear = 
      near: [parseFloat(params.lon), parseFloat(params.lat)],
      distanceField: "distance",
      spherical: true,
      limit: count,
      distanceMultiplier: 6371
    if params.distance
      geoNear.maxDistance = params.distance/6371 
    if params.type_venue is "festival"
      geoNear.query = {"category": "festival"}
    else
      geoNear.query = {"category": "club", "club_types": params.type_venue}
    query =  [{'$geoNear': geoNear}, {'$skip':params.skip}, {'$limit':params.take}]
    match = {}
    if params.search
      search = params.search.replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1")
      match.club_name =  { '$regex': search, '$options': 'i' }
    if match
      query = [{'$geoNear': geoNear}, {'$match': match}, {'$skip':params.skip}, {'$limit':params.take}]
    db_model.Venue.aggregate query,{}, (err, clubs)->
      if params.type_venue != "festival"
        for club in clubs
          if club.club_working_hours
            for wh in club.club_working_hours
              if wh.day == moment.utc().day()
                club.id = club._id
                club.club_today_working_hours = wh
      db_model.User.findById(params.user_id).exec (err, user)->
        match_pre =  {"_id": {'$in': user.friends}, 'bloked_users': {'$ne': user._id}, 'friends': user._id, 'checkin.active': true }
        match_post =  {'checkin.active': true}
        group = { _id: "$checkin.club", count: { $sum: 1 }}
        query = [ { '$match': match_pre}, { '$unwind': "$checkin" }, { '$match': match_post }, {'$group': group} ]
        db_model.User.aggregate query, {}, (err, result)->
          for c in result
            theclub = __.find(clubs, (c_res)->
                    c_res._id.toString() == c._id.toString()
              )
            if theclub
              theclub.active_friends_checkins = c.count
          for club in clubs
            is_favorite_club = __.find(user.favorite_clubs, (c_res)->
                    c_res.toString() == club._id.toString()
              )
            if is_favorite_club
              club.is_favorite = true
            else
              club.is_favorite = false
          callback err, clubs

exports.get_people_count_in_club = (club, callback)->
  console.log "METHOD - Manager get_people_count_in_club"
  db_model.User.count({'checkin': { '$elemMatch': { 'club' : club, 'active': true}}}).exec callback

exports.find_club = (club_id, user_id, callback)->
  console.log "METHOD - Manager find_club"
  db_model.Venue.findById(club_id).exec (err, club)->
    if err
      callback err, null
    else if club
      db_model.User.findById(user_id).exec (err, user)->
        club_object = club.toObject()
        is_favorite_club = __.find(user.favorite_clubs, (c_res)->
                c_res.toString() == club_object._id.toString()
          )
        if is_favorite_club
          club_object.is_favorite = true
        else
          club_object.is_favorite = false
        db_model.User.find({'checkin': { '$elemMatch': { 'club' : club, 'active': true}}, 'bloked_users': {'$ne': user._id}}, { checkin: 0 }).exec (err, users)->
        #get friends count
          db_model.User.find({'checkin': { '$elemMatch': { 'club' : club, 'active': true}}, "_id": {'$in': user.friends}, 'friends': user._id}).exec (err, friends)->
            user_objects = []
            for user in users
              user_object = user.toObject();
              is_friend = __.find(friends, (f) ->
                      f._id.toString() is user._id.toString()
                    )
              if is_friend
                user_object.is_friend = true
              else
                user_object.is_friend = false
              user_objects.push user_object

            club_object.active_friends_checkins = friends.length

            callback null, club_object, user_objects, friends.length
    else 
      callback 'missing club with this id', null


exports.create_club = (params, callback)->
  console.log "METHOD - Manager create_club"
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

##################################################################################################################
# Checkin, Checkout logic
##################################################################################################################

exports.add_favorite_club = (params, callback)->
  console.log "METHOD - Add favorite clubs"
  isClub = false
  query =  [{'$match': {_id : mongoose.Types.ObjectId(params.user_id)}}, {'$unwind': '$favorite_clubs'}]
  db_model.User.aggregate query, {}, (err, result)-> 
    for r in result
      if r.favorite_clubs.toString() == params.club_id.toString()
        isClub = true
    if !isClub
      db_model.User.findById(params.user_id).exec (err, user)->
        user.favorite_clubs.push params.club_id
        db_model.save_or_update_user user, (err)-> callback err, user
    else
      callback 'club already a favorite', null

exports.remove_favorite_club = (params, callback)->
  console.log "METHOD - Remove favorite clubs"
  console.log "Params: "
  console.log params
  isClub = false
  query =  [{'$match': {_id : mongoose.Types.ObjectId(params.user_id)}}, {'$unwind': '$favorite_clubs'}]
  db_model.User.aggregate query, {}, (err, result)-> 
    for r in result
      if r.favorite_clubs.toString() == params.club_id.toString()
        isClub = true
    if isClub
      db_model.User.findById(params.user_id).exec (err, user)->
        user.favorite_clubs.pull params.club_id
        db_model.save_or_update_user user, (err)-> callback err, user
    else
      callback 'club not favorite', null

exports.news = (params, callback)->
  console.log "METHOD - News"
  console.log "Params: "
  console.log params
  if !params.skip
    params.skip=0
  if !params.limit
    params.limit=10
  db_model.News.find({'venue': params.club_id}).populate('venue').sort( { updated_on: -1 } ).skip(params.skip).limit(params.limit).exec (err, news)-> 
    if not news
      callback 'missing news for this club', null
    else
      news_objects = []
      for n in news
        news_object = n.toObject()
        news_object.created_on_formatted = moment.utc(news_object.created_on).format("YYYY-MM-DD, HH:mm:ss")
        news_object.updated_on_formatted = moment.utc(news_object.updated_on).format("YYYY-MM-DD, HH:mm:ss")
        news_objects.push news_object
      callback err, news_objects

exports.events = (params, callback)->
  console.log "METHOD - Events"
  console.log "Params: "
  console.log params
  if !params.skip
    params.skip=0
  if !params.limit
    params.limit=10
  db_model.News.find({'$and':[{'venue': params.club_id},{'type': 'event'}]}).populate('venue').sort( { updated_on: -1 } ).skip(params.skip).limit(params.limit).exec (err, news)-> 
    if not news
      callback 'missing news for this club', null
    else
      news_objects = []
      for n in news
        news_object = n.toObject()
        news_object.created_on_formatted = moment.utc(news_object.created_on).format("YYYY-MM-DD, HH:mm:ss")
        news_object.updated_on_formatted = moment.utc(news_object.updated_on).format("YYYY-MM-DD, HH:mm:ss")
        news_object.start_time_formatted = moment.utc(news_object.start_time).format("YYYY-MM-DD, HH:mm:ss")
        news_object.end_time_formatted = moment.utc(news_object.end_time).format("YYYY-MM-DD, HH:mm:ss")
        news_objects.push news_object
      callback err, news_objects

exports.venue_events = (params, callback)->
  console.log "METHOD - Events"
  query = JSON.parse('{ "'+ params.type_venue + '":"' + params.objectId+'" }')
  db_model.Events.find(query).populate(params.type_venue).exec (err, events)-> 
    if not events
      console.log  'missing events'       
    else
      events_objects = []
      for events_object in events
        events_objects.push events_object.toObject()
      format_date_events events_objects, (events_updated)->
        callback err, events_updated

exports.venue_news = (params, callback)->
  console.log "METHOD - News"
  query = JSON.parse('{ "'+ params.type_venue + '":"' + params.objectId+'" }')
  db_model.News.find(query).populate(params.type_venue).exec (err, news)-> 
    if not news
      console.log  'missing news'       
    else
      format_date_news news, (news_updated)->
        callback err, news_updated

exports.news_favorite = (params, callback)->
  console.log "METHOD - News favorite club"
  console.log "Params: "
  console.log params
  if !params.skip
    params.skip=0
  if !params.limit
    params.limit=10
  db_model.User.findById(params.user_id).exec (err, user)->
    if not user
      callback 'user does not exist', null
    else
      db_model.News.find({'venue': {'$in': user.favorite_clubs}}).populate('venue').sort( { updated_on: -1 } ).skip(params.skip).limit(params.limit).exec (err, news)-> 
        if not news
          callback 'news does not exist', null
        else
          news_objects = []
          for n in news
            news_object = n.toObject()
            news_object.created_on_formatted = moment.utc(news_object.created_on).format("YYYY-MM-DD, HH:mm:ss")
            news_object.updated_on_formatted = moment.utc(news_object.updated_on).format("YYYY-MM-DD, HH:mm:ss")
            news_objects.push news_object
          callback err, news_objects

exports.events_favorite = (params, callback)->
  console.log "METHOD - Events favorite club"
  console.log "Params: "
  console.log params
  if !params.skip
    params.skip=0
  if !params.limit
    params.limit=10
  db_model.User.findById(params.user_id).exec (err, user)->
    if not user
      callback 'user does not exist', null
    else
      db_model.News.find({'$and':[{'venue': {'$in': user.favorite_clubs}},{'type': 'event'}]}).populate('venue').sort( { updated_on: -1 } ).skip(params.skip).limit(params.limit).exec (err, news)-> 
        if not news
          callback 'news does not exist', null
        else
          news_objects = []
          for n in news
            news_object = n.toObject()
            news_object.created_on_formatted = moment.utc(news_object.created_on).format("YYYY-MM-DD, HH:mm:ss")
            news_object.updated_on_formatted = moment.utc(news_object.updated_on).format("YYYY-MM-DD, HH:mm:ss")
            news_object.start_time_formatted = moment.utc(news_object.start_time).format("YYYY-MM-DD, HH:mm:ss")
            news_object.end_time_formatted = moment.utc(news_object.end_time).format("YYYY-MM-DD, HH:mm:ss")
            news_objects.push news_object
          callback err, news_objects
      

exports.checkin = (params, callback)->
  console.log "METHOD - Manager checkin"
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
              # add to favorite list
              exports.add_favorite_club {user_id: params.user_id, club_id: params.club_id}, ()->
              # checkout user from all clubs
              exports.checkout_from_all_clubs user, ()->
                club.active_checkins += 1
                club.save ()->
                  user.checkin.push { club: club, lat: club.club_loc.lat, lon: club.club_loc.lon, time: Date.now(), active: true }

                  db_model.save_or_update_user user, (err)-> callback err, user

exports.update_checkin = (params, callback)->
  console.log "METHOD - Manager update_checkin"
  console.log "params: "
  console.log params
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

          db_model.save_or_update_user user, (err)-> callback err, user

exports.checkout_from_all_clubs = (user, callback)->
  console.log "METHOD - Manager checkout_from_all_clubs"
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
  console.log "METHOD - Manager update_checkout_states"
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
  console.log "METHOD - Manager checkout"
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
            db_model.save_or_update_user user, (err)-> callback err, user

exports.cron_checkout = ()->
  console.log "METHOD - Manager cron_checkout"
  db_model.User.find({'checkin': { '$elemMatch': { 'active': true, 'time': {'$lte': new Date().getTime() - CHECKIN_LIVE_TIME } }} }).exec (err, users)->
    console.log "checkout ", users.length
    async.eachSeries users, (user, callback) ->
      exports.update_checkout_states user, ()->
        callback()
    , (err) ->
      console.log "checkout done"

#-----------------------------------------------------------------------------------------------------------------------

exports.save_user = (params, callback)->
  console.log "METHOD - Manager save_user"
  db_model.User.findOne({"email":params.email}).exec (err, user)->
    if user
      if user.state is 'invited'
        user.gender = params.gender
        user.name = params.name
        user.email = params.email
        user.password = params.password
        user.city = params.city
        user.country = params.country
        user.state = 'active'

        if params.bio
          user.bio = params.bio
        if params.dob
          user.dob = params.dob
        if params.avatar
          user.photos.push {public_id: params.avatar.public_id, url: params.avatar.url, profile: true}

        db_model.save_or_update_user user, (err)-> callback err, user
      else
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

      if params.bio
        user.bio = params.bio
      if params.dob
        user.dob = params.dob
      if params.avatar
        user.photos.push {public_id: params.avatar.public_id, url: params.avatar.url, profile: true}

      db_model.save_or_update_user user, (err)-> callback err, user

exports.uploadphoto = (params, callback)->
  console.log "METHOD - Manager uploadphoto"
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

      db_model.save_or_update_user user, (err)-> callback err, user

exports.save_or_update_fb_user = (params, callback)->
    console.log "METHOD - Manager save_or_update_fb_user"
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
          if params.avatar
            user.photos.push {public_id: params.avatar.public_id, url: params.avatar.url, profile: true}

        db_model.save_or_update_user user, (err)-> callback err, user

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
          if params.bio then user.bio = params.bio
          if params.city then user.city = params.city
          if params.country then user.country = params.country

          user.photos.push {public_id: params.avatar.public_id, url: params.avatar.url, profile: true}

          # SEND WELCOME MAIL
          #email_sender.welcome user, (err, info)->
          #  console.log 'welcome mail sent', user._id

          db_model.save_or_update_user user, (err)-> callback err, user

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

              db_model.save_or_update_user user, (err)-> callback err, user

            else
              console.log "fb_login:", "create user"
              user = new db_model.User
                email: params.email
                gender: params.gender
                name: params.name
                fb_id: params.fb_id
                fb_access_token: params.fb_access_token
              if params.dob then user.dob = params.dob
              if params.bio then user.bio = params.bio
              if params.city then user.city = params.city
              if params.country then user.country = params.country

              user.photos.push {public_id: params.avatar.public_id, url: params.avatar.url, profile: true}

              # SEND WELCOME MAIL
              #email_sender.welcome user, (err, info)->
              #  console.log 'welcome mail sent', user._id

              db_model.save_or_update_user user, (err)-> callback err, user

exports.chat = (params, callback)->
  console.log "METHOD - Manager chat"
  query = { '$or': [{ 'user1': mongoose.Types.ObjectId(params.user_from), 'user2': mongoose.Types.ObjectId(params.user_to) },
    { 'user1': mongoose.Types.ObjectId(params.user_to), 'user2': mongoose.Types.ObjectId(params.user_from) }] }

  db_model.Chat.findOne(query).populate("user1", db_model.USER_PUBLIC_INFO).populate("user2", db_model.USER_PUBLIC_INFO).exec (err, chat)->
    if not chat
      chat = new db_model.Chat
        user1: mongoose.Types.ObjectId(params.user_from)
        user2: mongoose.Types.ObjectId(params.user_to)

    chat.conversation.push {msg: params.msg, url: params.url, location: params.location, from_who: mongoose.Types.ObjectId(params.user_from), type: params.msg_type}

    if chat.unread.user && chat.unread.user.toString() == params.user_to.toString()
      chat.unread.count += 1
    else
      chat.unread.user = mongoose.Types.ObjectId(params.user_to)
      chat.unread.count = 1
    
    chat.save (err)->
      # retreive chat with user data
      db_model.Chat.findById(chat._id).populate("user1", db_model.USER_PUBLIC_INFO).populate("user2", db_model.USER_PUBLIC_INFO).exec callback


exports.get_conversation = (params, callback)->
  console.log "METHOD - Manager get_conversation"
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
  console.log "METHOD - Manager get_conversations"
  db_model.User.findById(params.user_id).exec (err, user)->  
    db_model.User.find({'bloked_users': user}).exec (err, bloked_users)->
      db_model.Chat.find({'$or':[{'user1': mongoose.Types.ObjectId(params.user_id), 'user2': {'$nin': bloked_users}}, {'user2': mongoose.Types.ObjectId(params.user_id), 'user1': {'$nin': bloked_users}}]}, { 'conversation': { '$slice': -1 }}).populate("user1", db_model.USER_PUBLIC_INFO).populate("user2", db_model.USER_PUBLIC_INFO).exec (err, chats)->
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
  console.log "METHOD - Manager readchat"
  query = { '$or': [{ 'user1': mongoose.Types.ObjectId(params.current_user), 'user2': mongoose.Types.ObjectId(params.receiver) }, { 'user1': mongoose.Types.ObjectId(params.receiver), 'user2': mongoose.Types.ObjectId(params.current_user) }] }
  
  db_model.Chat.findOne(query).exec (err, chat)->
    if not chat
      # new conversion
      callback err, null

    else
      for conv in chat.conversation
        if conv.from_who.toString() != params.current_user.toString()
          conv.read = true


      if chat.unread.user && chat.unread.user.toString() == params.current_user.toString()
        chat.unread.count = 0

      chat.save (err)->
        callback err, chat


exports.unread_messages_count = (param, user_id, callback)->
  console.log "METHOD - Manager unread_messages_count"
  db_model.Chat.find({'unread.user':  mongoose.Types.ObjectId(user_id)}, {'conversation':0}).exec (err, chats)->
    unread_chat_count = 0
    for chat in chats
      unread_chat_count = unread_chat_count + chat.unread.count

    db_model.User.findById(user_id).exec (err, user)->
      db_model.User.count({"_id": {'$nin': user.friends}, 'friends': user._id}).exec (err, pending_friends_count)->      
        db_model.Venue.count({club_loc: { '$near' : [param.user_lon, param.user_lat], '$maxDistance': 1/112 }}).exec (err, venue_count)->        
          callback null, unread_chat_count, pending_friends_count, venue_count


# convert the radius value to km
exports.radius_to_km = (distance)->
  console.log "METHOD - Manager radius_to_km"
  # http://stackoverflow.com/questions/7837731/units-to-use-for-maxdistance-and-mongodb
  #one degree is approximately 111.12 kilometers
  #degree = Math.PI * 6378.137/180 

  return distance/75

format_date_events = (events, callback)->
  events_upcoming = []
  for even in events
    if even.end_time
      if moment.utc(even.end_time).format('YYYY-MM-DD HH:mm:ss') > moment().format('YYYY-MM-DD HH:mm:ss')
        even.created_on_formatted = moment.utc(even.created_on).format("YYYY-MM-DD, HH:mm:ss")
        even.updated_on_formatted = moment.utc(even.updated_on).format("YYYY-MM-DD, HH:mm:ss")
        even.start_time_formatted = moment.utc(even.start_time).format("YYYY-MM-DD, HH:mm:ss")
        even.end_time_formatted = moment.utc(even.end_time).format("YYYY-MM-DD, HH:mm:ss")
        events_upcoming.push even
    else if moment.utc(even.start_time).format('YYYY-MM-DD HH:mm:ss') > moment().format('YYYY-MM-DD HH:mm:ss')
      even.created_on_formatted = moment.utc(even.created_on).format("YYYY-MM-DD, HH:mm:ss")
      even.updated_on_formatted = moment.utc(even.updated_on).format("YYYY-MM-DD, HH:mm:ss")
      even.start_time_formatted = moment.utc(even.start_time).format("YYYY-MM-DD, HH:mm:ss")
      events_upcoming.push even
  callback events_upcoming

format_date_news = (news, callback)->
  news_objects = []
  for news_object in news
    news_objects.push news_object.toObject()
  for the_news in news_objects
    the_news.created_on_formatted = moment.utc(the_news.created_on).format("YYYY-MM-DD, HH:mm:ss")
    the_news.updated_on_formatted = moment.utc(the_news.updated_on).format("YYYY-MM-DD, HH:mm:ss")
  callback news_objects




