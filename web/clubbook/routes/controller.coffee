mongoose = require('mongoose')
manager = require("../logic/manager")
db_model = require("../logic/model")
__ = require("underscore")
moment = require('moment-timezone')
moment.lang("ru")
email_sender = require("../email/email_sender")
async = require("async")
imager = require("imager")
config_image = require('./image_config')


exports.index = (req, res)->
  create_base_model req, res, (model)->
    #model.message_sent = req.flash("message_sent").length > 0
    #db_model.Venue.find({visible:true}).exec (err, venues)->
    #model.active_venues = venues
    res.render "landing", model

exports.image = (reg,res)->
  im = new imager(config_image, "S3") # 'Rackspace' or 'S3'
  im.upload ['https://s3-us-west-2.amazonaws.com/my-unique-nameakiai2uobdaoczuao6kqpicture-bucket/46366eb8-e421-4f7f-ac72-16c15ec08070'], ((err, cdnUri, uploaded) ->
     res.json
          status: "ok"
  ), "items"


# -----------------------------------------------------------------------------------------
# utils
#------------------------------------------------------------------------------------------

create_base_model = (req, res, callback)->
  # default values
  model =
    title: "Clubbook"
    req_path: req.path
    config: config

  # Open Graph tags
  model["meta_data"] =
    title: config.meta.title
    type: config.meta.type
    url: req.originalUrl
    image: config.meta.image
    description: config.meta.description
    fb_admins: config.meta.admins
    fb_app_id: config.meta.app_id

  # disaply error logic
  if req.query.error
    model.has_error = true
  else
    model.has_error = false

  callback model
