//
//  Constants.m
//  Clubbook
//
//  Created by Andrew on 6/18/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "Constants.h"

@implementation Constants

//CLOUDINARY_URL=cloudinary://635173958253643:kc8bGmuk3HgDJCqQxMjuTbgTwJ0@ddsoyfjll
+ (NSString*) Cloudinary
{ @synchronized(self) { return @"cloudinary://635173958253643:kc8bGmuk3HgDJCqQxMjuTbgTwJ0@ddsoyfjll"; } }

+ (NSString*) PubnabPubKay
{ @synchronized(self) { return @"pub-c-b0a0ffb6-6a0f-4907-8d4f-642e500c707a";}}

+ (NSString*) PubnabSubKay
{ @synchronized(self) { return @"sub-c-f56b81f4-ed0a-11e3-8a10-02ee2ddab7fe";}}

+ (int) MaxCheckinRadius
{ @synchronized(self) { return 10000; } }
@end
