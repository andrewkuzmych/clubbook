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

exports.cron_checkout = ()->
  
  manager.cron_checkout()

