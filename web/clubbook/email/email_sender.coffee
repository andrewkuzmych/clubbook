jade = require('jade')
fs = require('fs')
moment = require('moment')
async = require("async")
request = require("request")
__ = require("underscore")

# oleg@eventinarea.com / event88888888
mandrill = require('node-mandrill')('Q0Soq0NlJAPoiRAR_ZwbKw')
from_email = "notify@happyscan.me" # password: event8888

model = require('../logic/model')

exports.send_pass = (user, callback)->
  if not user
    callback "error", "Can not find user"
  else
    model_dto =
      user_password: user.password

    compile_template './email/send_pass.jade', model_dto, (err, html_body)->
      if err
        callback err, "can not compile"
      else
        mandrill '/messages/send',
          message:
            to: [{email: user.email, name: if user.name then user.name.split(" ")[0] else ""}]
            from_email: from_email
            from_name: "HappyScan.me"
            subject: "Відновлення пароля"
            tags: ["reset_password_hs"]
            track_opens: true
            track_clicks: true
            google_analytics_domains: ["eventinarea.com"]
            html: html_body
          , (error, response)->        
            if error then console.log( JSON.stringify(error) )
            #console.log "email sent, event_created, ", event.saved_by.email 
            callback null, "OK"

exports.welcome = (user, callback)->
  if not user
    callback "error", "Can not find user"
  else if not user.email
    callback "error", "User wothout email"

  else
    model_dto =
      user_name: user.first_name

    compile_template './email/welcome_mail.jade', model_dto, (err, html_body)->
      if err
        callback err, "can not compile"
      else
        mandrill '/messages/send',
          message:
            to: [{email: user.email, name: if user.name then user.name.split(" ")[0] else ""}]
            from_email: from_email
            from_name: "HappyScan.me"
            subject: "Ласкаво просимо в HappyScan"
            tags: ["welcome_mail_hs"]
            track_opens: true
            track_clicks: true
            google_analytics_domains: ["eventinarea.com"]
            html: html_body
          , (error, response)->        
            if error then console.log( JSON.stringify(error) )
            #console.log "email sent, event_created, ", event.saved_by.email 
            callback null, "OK"

exports.servey = (user, prize, condition, days_to_use, venue, callback)->
  if not user
    callback "error", "Can not find user"
  else
    model_dto =
      user_name: user.first_name
      prize: prize
      condition: condition
      days_to_use: days_to_use
      venue: venue

    compile_template './email/servey_mail.jade', model_dto, (err, html_body)->
      if err
        callback err, "can not compile"
      else
        mandrill '/messages/send',
          message:
            to: [{email: user.email, name: if user.name then user.name.split(" ")[0] else ""}]
            from_email: from_email
            from_name: "HappyScan.me"
            subject: "Вітаємо з першим виграшем в HappyScan"
            tags: ["servey_mail_hs"]
            track_opens: true
            track_clicks: true
            google_analytics_domains: ["eventinarea.com"]
            html: html_body
          , (error, response)->        
            if error then console.log( JSON.stringify(error) )
            #console.log "email sent, event_created, ", event.saved_by.email 
            callback null, "OK"

exports.new_gift = (usergift_id, gift, user, venue, days, callback)->
  if not user
    callback "error", "Can not find user"
  else
    model_dto =
      gift: gift
      usergift_id: usergift_id
      user_name: user.first_name
      days: days
      venue: venue

    compile_template './email/new_gift.jade', model_dto, (err, html_body)->
      if err
        callback err, "can not compile"
      else
        mandrill '/messages/send',
          message:
            to: [{email: user.email, name: if user.name then user.name.split(" ")[0] else ""}]
            from_email: from_email
            from_name: "HappyScan.me"
            subject: "Новий подарунок в HappyScan"
            tags: ["new_gift_mail_hs"]
            track_opens: true
            track_clicks: true
            google_analytics_domains: ["eventinarea.com"]
            html: html_body
          , (error, response)->        
            if error then console.log( JSON.stringify(error) )
            #console.log "email sent, event_created, ", event.saved_by.email 
            callback null, "OK"

exports.new_dob_gift = (usergift_id, gift, user, venue, days, callback)->
  if not user
    callback "error", "Can not find user"
  else
    model_dto =
      usergift_id: usergift_id
      gift: gift
      user_name: user.first_name
      days: days
      venue: venue

    compile_template './email/new_dob_gift.jade', model_dto, (err, html_body)->
      if err
        callback err, "can not compile"
      else
        mandrill '/messages/send',
          message:
            to: [{email: user.email, name: if user.name then user.name.split(" ")[0] else ""}]
            from_email: from_email
            from_name: "HappyScan.me"
            subject: "Новий подарунок в HappyScan"
            tags: ["new_gift_mail_hs"]
            track_opens: true
            track_clicks: true
            google_analytics_domains: ["eventinarea.com"]
            html: html_body
          , (error, response)->        
            if error then console.log( JSON.stringify(error) )
            #console.log "email sent, event_created, ", event.saved_by.email 
            callback null, "OK"

exports.venue_servey = (admin, venue, total_scans, total_used, total_users, total_scans_last_week, total_used_last_week, callback)->
    model_dto =
      total_scans: total_scans
      total_used: total_used
      total_users: total_users
      total_scans_last_week: total_scans_last_week
      total_used_last_week: total_used_last_week
      venue: venue._id
      venue_title: venue.title
    
    compile_template './email/venue_digest.jade', model_dto, (err, html_body)->
      if err
        callback err, "can not compile"
      else
        mandrill '/messages/send',
          message:
            to: [{email: admin.email, name: ""}]
            from_email: from_email
            from_name: "HappyScan.me"
            subject: "Статистика HappyScan за минулий тиждень. " + venue.title
            tags: ["venue_servey"]
            track_opens: true
            track_clicks: true
            google_analytics_domains: ["eventinarea.com"]
            html: html_body
          , (error, response)->        
            if error then console.log( JSON.stringify(error) )
            #console.log "email sent, event_created, ", event.saved_by.email 
            callback null, "OK"

exports.new_gift_admin = (venue, type, callback)->
  text = "Адмін !!! Новий подарунок в HappyScan!!!"
  if type == "dob"
    text = "Адмін !!! Нове подарунок (День Народження) в HappyScan!!!"

  html_body = """
      <div>
        <div><b>#{venue}</b> створив новий подарунок</div>
      </div>
      """

  mandrill '/messages/send',
      message:
        to: [{email: "andrew@happyscan.me"},{email: "oleg@happyscan.me"},{email: "lidiya@happyscan.me"}]
        from_email: from_email
        from_name: "HappyScan.me"
        subject: text
        tags: ["new_gift_admin"]
        track_opens: true
        track_clicks: true
        google_analytics_domains: ["eventinarea.com"]
        html: html_body
      , (error, response)->
        if error then console.log( JSON.stringify(error) )
        callback null, "OK"



exports.send_message_from_landing = (message, callback)->
  if not message
    callback "error", "Message is empty"
  else
    html_body = """
      <div>
        <div><b>Name: </b>#{message.name}</div>
        <div><b>Email: </b>#{message.email}</div>
        <div><b>Message: </b>#{message.message}</div>
      </div>
      """

    mandrill '/messages/send',
      message:
        to: [{email: "oleg@happyscan.me"},{email: "lidiya@happyscan.me"},{email: "andrew@happyscan.me"}]
        from_email: from_email
        from_name: "HappyScan.me"
        subject: "New message!"
        tags: ["new_message_from_landing"]
        track_opens: true
        track_clicks: true
        google_analytics_domains: ["eventinarea.com"]
        html: html_body
      , (error, response)->
        if error then console.log( JSON.stringify(error) )
        callback null, "OK"


###################################################################################################
# UTILS
###################################################################################################

compile_template = (template, model_dto, callback)->
  fs.readFile template, 'utf8', (err, data)->
    if err
      return callback err
    else
      try
        fn = jade.compile(data)
        html_body = fn model_dto  
        callback null, html_body

      catch error
        console.log error
        callback error

###################################################################################################
# Format result
###################################################################################################

# prepare event image
prepare_image = (images, width_to_get=174)-> 
  if images and images.length > 0 and images[0]
    image = images[0]    
  else
    image = 
      url: "http://lh4.ggpht.com/iJ5iXID7oHaS5WTDvKIVrRgCsEYiD-_Ad6sGaNzg5Ks-_Nlr2ZeMIjzJ8k6zlt8YDR-OGhutJBYpc4zcVqnXPw"
      image_height: 218
      image_width: 174
  
  # get proportions
  image_height = Math.round(image.image_height / image.image_width * width_to_get)
  image_width = width_to_get  
  
  result =
    url: image.url + '=s' + Math.max(image_width, image_height)
    width: image_width
    height: image_height  

  return result.url


