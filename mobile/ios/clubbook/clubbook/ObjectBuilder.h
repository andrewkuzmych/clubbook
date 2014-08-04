//
//  ObjectBuilder.h
//  BrowseMeetup
//
//  Created by Andrew on 10/1/13.
//  Copyright (c) 2013 TAMIM Ziad. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "User.h"
#import "Place.h"
#import "Chat.h"
#import "UnreadMessages.h"


@interface ObjectBuilder : NSObject

+ (User *)fbLoginFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (User *)signupFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (User *)signinFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (NSArray *)placesFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (Place *)placeFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (User *)checkinFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (User *)userFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (User *)tempUserFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (Chat *)chatFromJSON:(NSData *)objectNotation error:(NSError **)error;
+ (NSArray *)conversationsJSON:(NSData *)objectNotation error:(NSError **)error;
+ (NSArray *)friendsJSON:(NSData *)objectNotation error:(NSError **)error;
+ (UnreadMessages *)unreadMessagesFromJSON:(NSData *)objectNotation error:(NSError **)error;

@end
