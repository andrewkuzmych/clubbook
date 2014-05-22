module.exports =
  variants:
    items:
      
      # keepNames: true,
      resize:
        mini: "300x200"
        preview: "800x600"

      crop:
        thumb: "200x200"

      resizeAndCrop:
        large:
          resize: "1000x1000"
          crop: "900x900"

    gallery:
      crop:
        thumb: "100x100"

  storage:
    S3:
      key: "AKIAI2UOBDAOCZUAO6KQ"
      secret: "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"
      bucket: "clubbookimages1"


  # set `secure: false` if you want to use buckets with characters like '.' (dot)
  debug: true