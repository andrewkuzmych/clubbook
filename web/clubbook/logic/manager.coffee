mongoose = require('mongoose')
mongoose.connect(config.db.connection)
db_model = require('./model')
async = require("async")
__ = require("underscore")
async = require("async")
moment = require('moment-timezone')


exports.save_or_update_fb_user = (params, callback)->
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

              callback err, user

            else
              user = new db_model.User
                email: params.email
                gender: params.gender
                name: params.name
                fb_id: params.fb_id
                fb_access_token: params.fb_access_token
                fb_token_expires: params.fb_token_expires
              
              if dob then user.dob = dob
              
              # SEND WELCOME MAIL
              #email_sender.welcome user, (err, info)->
              #  console.log 'welcome mail sent', user._id

              callback err, user

