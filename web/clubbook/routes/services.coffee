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
  prepare_result = (req, res, user)->
    user.save (err)->
      if err
        console.log "fb error:", err
        res.json
          status: "error"
          errors: err.errors
      else
        res.json
          status: "ok"
          result:
            user: user

  errors = {}
  if req.body.dob
    dobArray = req.body.dob.split(".")
    dob = new Date(dobArray[2], parseInt(dobArray[1]) - 1, dobArray[0], 15, 0, 0, 0);
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
    params = 
      gender: req.body.gender
      name: req.body.name
      email: req.body.email
      fb_id: req.body.fb_id
      dob: req.body.dob
      fb_access_token: req.body.fb_access_token
      fb_token_expires: req.body.fb_token_expires

    if req.body.avatar
      params.avatar = req.body.avatar

    manager.save_or_update_fb_user params, (err, user)->
      prepare_result req, res, user


exports.signinmail = (req, res)->
    params = 
        email: req.body.email
        password: req.body.password

    manager.signinmail params, (err, user)-> 
      if err 
        res.json
          status: "error"
          err:err
      else
        res.json
          status: "ok"
          result:
            user: user


exports.signup = (req, res)->
  if req.body.dob
    dobArray = req.body.dob.split(".")
    dob = new Date(dobArray[0], parseInt(dobArray[1]) - 1, dobArray[2], 15, 0, 0, 0);

  params = 
      gender: req.body.gender
      name: req.body.name
      email: req.body.email
      password: req.body.password
      dob: dob
    
  manager.save_user params, (err, user)-> 
    if err 
      res.json
        status: "error"
        err:err
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
          err:err
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
          err:err
      else
        res.json
          club: club
          status: "Added OK"


exports.find_club = (req, res)->
 
  loc = 
    lat: req.params.club_lat
    lon: req.params.club_lon
  
  manager.find_club req.params.club_id, (err, club)->

      if err 
        res.json
          status: "error"
          err:err
      else
        res.json
          club: club
          status: "Found Club OK!"


exports.list_club = (req, res)->

  params =
    distance: req.params.distance
    lat: req.params.user_lat
    lon: req.params.user_lon

  manager.list_club params, (err, clubs)->
    if err
      res.json
        status:'error'
        error:err
    else
      res.json
        status:'ok'
        clubs: clubs

exports.cu_count = (req, res)->
  console.log 0
  
  params =
    distance: req.params.distance
    lat: req.params.user_lat
    lon: req.params.user_lon
  
  console.log params


 
  manager.cu_count params, (err, club_count)->
     if err
       res.json
         status:'error'
         error:err
     else
       res.json
         status:'ok'
         club_count: club_count

exports.checkin = (req, res)->
  params = 
    user_id: req.params.user_id
    club_id: req.params.club_id

  manager.checkin params, (err, user)->
    res.json
      status: 'ok'
      user: user


