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

    manager.save_or_update_fb_user params, (err, user)->
      prepare_result req, res, use

exports.signup = (req, res)->
  if req.body.dob
    dobArray = req.body.dob.split(".")
    dob = new Date(dobArray[0], parseInt(dobArray[1]) - 1, dobArray[2], 15, 0, 0, 0);

  params = 
      gender: req.body.gender
      name: req.body.name
      email: req.body.email
      password: req.body.password
      photos: req.body.photos
      dob: dob
  
  manager.save_user params, (err, user)-> 
    if err 
      res.json
        status: "error"
        err:err
    else
      res.json
        name: " Name : " + req.body.name
        status: "test"
