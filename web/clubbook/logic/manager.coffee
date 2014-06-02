mongoose = require('mongoose')
mongoose.connect(config.db.connection)
db_model = require('./model')
async = require("async")
__ = require("underscore")
async = require("async")
moment = require('moment-timezone')

exports.get_user_by_id = (user_id, callback)->
  db_model.User.findById(user_id).exec (err, user)->
    if err
      callback err, null
    else
      callback null, user

exports.signinmail = (params, callback)->
  
  if __.isEmpty params.email?.trim() 
      callback 'email is empty', null
  else if __.isEmpty params.password?.trim()  
      callback 'password is empty', null
  else
    db_model.User.findOne({"email":params.email,"password":params.password }).exec (err, user)->
      
      if user
        callback null, user
      else
        callback "Wrong User or password " ,user

exports.list_club = (params, callback)->
  db_model.Venue.find({ 'club_loc':{ '$near' : [ params.lat,params.lon], '$maxDistance' :  params.distance/111.12 }}).exec (err, clubs)->
    callback err, clubs

exports.find_club = (club_id, callback)->

  
  db_model.Venue.findById(club_id).exec (err, club)->
  
    if err
      callback err, null
    else
      callback null, club  


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


exports.save_user = (params, callback)->
 
  db_model.User.findOne({"email":params.email}).exec (err, user)->
    
    if user 
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
            dob: params.dob
             
    #user.photos.push { url: params.photos, profile:true}    

      user.save (err)->
        console.log err
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
    console.log params
    if params.dob
        dobArray = params.dob.split(".")
        dob = new Date(dobArray[2], parseInt(dobArray[1]) - 1, dobArray[0], 15, 0, 0, 0);

    db_model.User.findOne({"fb_id":params.fb_id}).exec (err, user)->
      if user
        # update user
        if params.email then user.email = params.email
        user.gender = params.gender
        user.name = params.name
        if dob then user.dob = dob
        user.fb_id = params.fb_id
        user.fb_access_token = params.fb_access_token
        if params.fb_token_expires then user.fb_token_expires = params.fb_token_expires
        if params.fb_city then user.fb_city = params.fb_city

        if user.photos.length == 0
          user.photos.push { url: params.avatar, profile:true}

        callback err, user

      else
        # new user
        if __.isEmpty params.email?.trim()
          # user didn't provide email
          user = new db_model.User
            gender: params.gender
            name: params.name
            fb_id: params.fb_id
            fb_access_token: params.fb_access_token
            fb_token_expires: params.fb_token_expires

          user.photos.push { url: params.avatar, profile:true}


          if dob then user.dob = dob
          
          # SEND WELCOME MAIL
          #email_sender.welcome user, (err, info)->
          #  console.log 'welcome mail sent', user._id
          
          callback err, user

        else
          db_model.User.findOne({"email":params.email?.toLowerCase()}).exec (err, user)->
            if user
              # merge data
              user.gender = params.gender
              user.name = params.name
              if dob then user.dob = dob
              user.fb_id = params.fb_id
              user.fb_access_token = params.fb_access_token
              if params.fb_token_expires then user.fb_token_expires = params.fb_token_expires
              if params.fb_city then user.fb_city = params.fb_city

              if user.photos.length == 0
                user.photos.push { url: params.avatar, profile:true}
                    
              callback err, user

            else
              user = new db_model.User
                email: params.email
                gender: params.gender
                name: params.name
                fb_id: params.fb_id
                fb_access_token: params.fb_access_token
                fb_token_expires: params.fb_token_expires
              user.photos.push { url: params.avatar, profile:true}
              
              if dob then user.dob = dob
              
              # SEND WELCOME MAIL
              #email_sender.welcome user, (err, info)->
              #  console.log 'welcome mail sent', user._id


              callback err, user

